package cz.cvut.felk.kbss.freeplane.server.rest;

import cz.cvut.felk.kbss.freeplane.server.exception.ApiResponseStatusException;
import cz.cvut.felk.kbss.freeplane.server.model.Mindmap;
import cz.cvut.felk.kbss.freeplane.server.model.User;
import cz.cvut.felk.kbss.freeplane.server.rest.dto.*;
import cz.cvut.felk.kbss.freeplane.server.security.utils.SecurityUtils;
import cz.cvut.felk.kbss.freeplane.server.service.CollaborationService;
import cz.cvut.felk.kbss.freeplane.server.service.MindmapService;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Mind map REST controller
 */
@Controller
@CrossOrigin
@RequestMapping("/mindmaps")
public class MindmapController {

    private final MindmapService mindmapService;

    @Autowired
    public MindmapController(MindmapService mindmapService) {
        this.mindmapService = mindmapService;
    }

    /**
     * Creating mind map endpoint
     *
     * @param mindmapDto mind map DTO request body
     * @return HttpStatus code
     */
    @PostMapping
    public ResponseEntity<String> createMindmap(@RequestBody MindmapToCreateDto mindmapDto) {
        Objects.requireNonNull(mindmapDto.getTitle(), "Mindmap title must not be null");
        Objects.requireNonNull(mindmapDto.getMap(), "Mindmap content must not be null");

        if (mindmapService.mindMapTitleExists(mindmapDto.getTitle())) {
            throw new ApiResponseStatusException(HttpStatus.CONFLICT, "Mind map with this title already exists");
        }

        Mindmap mindmap = new Mindmap();
        mindmap.setCreator(Objects.requireNonNull(SecurityUtils.getCurrentUserDetails()).getUser());
        mindmap.setCreationDate(LocalDateTime.now());
        mindmap.setEditionDate(LocalDateTime.now());
        mindmap.setPublic(mindmapDto.isPublic());
        mindmap.setTitle(mindmapDto.getTitle());
        mindmap.setXml(Base64.decodeBase64(mindmapDto.getMap()));

        Long mindmapId;
        try {
            mindmapId = this.mindmapService.save(mindmap);
        } catch (Exception e) {
            throw new ApiResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Some error occurred while creating mind map");
        }
        return new ResponseEntity<>(String.valueOf(mindmapId), HttpStatus.CREATED);
    }

    /**
     * Fetching all the mind maps
     *
     * @return collection of the mind maps
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<MindmapInfoDto>> getAllMindmaps() {
        final List<MindmapInfoDto> mindmaps;
        try {
            mindmaps = this.mindmapService.getAllMindmaps()
                    .stream()
                    .map(mindmapService::convertToDto)
                    .sorted(Comparator.comparing(MindmapInfoDto::getMindmapId))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ApiResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Some error occurred while fetching all mind maps");
        }
        return new ResponseEntity<>(mindmaps, HttpStatus.OK);
    }

    /**
     * Fetching content of the mind map by its ID
     *
     * @param id mind map ID
     * @return mind map content DTO
     * @throws Exception
     */
    @GetMapping("/{id}")
    public ResponseEntity<MindmapContentDto> getMindmapContentById(@PathVariable long id) throws Exception {
        final Mindmap map = getMindmapWithCheckPreconditions(id);
        final MindmapContentDto mapDto = this.mindmapService.convertToContentDto(map);
        mapDto.setReadOnly(!mindmapService.isCurrentUserMindmapCreator(id) && this.mindmapService.getCurrentUserRoleForMindmap(id).equals("READER"));

        return new ResponseEntity<>(mapDto, HttpStatus.OK);
    }

    /**
     * Fetching information about the mind map by its ID
     *
     * @param id mind map ID
     * @return mind map information DTO
     * @throws Exception
     */
    @GetMapping("/{id}/info")
    public ResponseEntity<MindmapInfoDto> getMindmapInfoById(@PathVariable long id) throws Exception {
        final Mindmap map = getMindmapWithCheckPreconditions(id);
        final MindmapInfoDto mapDto = this.mindmapService.convertToDto(map);

        return new ResponseEntity<>(mapDto, HttpStatus.OK);
    }

    /**
     * Deleting mind map endpoint.
     *
     * @param id mind map ID
     * @return HTTPStatus code
     * @throws Exception
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMindmap(@PathVariable Long id) throws Exception {
        try {
            if (!this.mindmapService.isCurrentUserMindmapCreator(id)) {
                throw new ApiResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
            }
            final Mindmap map = mindmapService.findMindmapById(id);
            if (map != null) {
                mindmapService.delete(map);
            }
        } catch (Exception e) {
            throw new ApiResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Some error occurred while deleting mind map with id " + id);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Updating mind map access (isPublic or not)
     *
     * @param id       mind map id
     * @param isPublic is Public request parameter
     * @return HttpStatus code
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateMindmapAccess(@PathVariable("id") Long id, @RequestParam("isPublic") boolean isPublic) {
        if (!this.mindmapService.isCurrentUserMindmapCreator(id)) {
            throw new ApiResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        final Mindmap map = mindmapService.findMindmapById(id);
        try {
            map.setPublic(isPublic);
            this.mindmapService.update(map);
        } catch (Exception e) {
            throw new ApiResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Some error occurred while updating isPublic flag for mind map with id " + id);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Loading user's mind maps and shared mind maps
     *
     * @return collection of mind maps
     */
    @GetMapping
    public ResponseEntity<MindmapsCollectionDto> getUsersMindmaps() {

        final MindmapsCollectionDto mindmapsCollectionDto;
        try {
            User user = SecurityUtils.getCurrentUserDetails().getUser();

            final List<MindmapInfoDto> yourMindmaps = this.mindmapService.getOwnMindmapsByCollaborator(user.getCollaboratorId())
                    .stream()
                    .map(this.mindmapService::convertToDto)
                    .sorted(Comparator.comparing(MindmapInfoDto::getMindmapId))
                    .collect(Collectors.toList());

            final List<MindmapInfoDto> sharedMindmaps = this.mindmapService.getSharedMindmapsByCollaborator(user.getCollaboratorId())
                    .stream()
                    .map(this.mindmapService::convertToDto)
                    .sorted(Comparator.comparing(MindmapInfoDto::getMindmapId))
                    .collect(Collectors.toList());

            mindmapsCollectionDto = new MindmapsCollectionDto();
            mindmapsCollectionDto.setYourMindmaps(yourMindmaps);
            mindmapsCollectionDto.setSharedMindmaps(sharedMindmaps);

        } catch (Exception e) {
            throw new ApiResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Some error occurred while fetching user's mind maps");
        }

        return new ResponseEntity<>(mindmapsCollectionDto, HttpStatus.OK);
    }

    /**
     * Sharing mind map endpoint
     *
     * @param id    path variable for mind map ID
     * @param email request parameter for e-mail
     * @param role  request parameter for role
     * @return String link to share
     */
    @GetMapping("/{id}/share")
    public ResponseEntity<String> shareMindmap(@PathVariable Long id, @RequestParam("email") String email, @RequestParam("role") String role) {

        if (!SecurityUtils.getCurrentUserDetails().getUser().isAdmin() && !this.mindmapService.isCurrentUserMindmapCreator(id)) {
            throw new ApiResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        final String uri = UriComponentsBuilder.newInstance()
                .scheme("http").host("localhost:8080")
                .path("/mindmap")
                .path("/" + id.toString())
                .path("/share")
                .queryParam("to", email)
                .queryParam("role", role)
                .toUriString();

        return new ResponseEntity<>(uri, HttpStatus.CREATED);
    }

    /**
     * Updating content of the mind map
     *
     * @param id             mind map ID
     * @param updatedMindmap mind map content in bytes
     * @return HttpStatus code
     */
    @PutMapping("/{id}/content")
    public ResponseEntity<Void> updateMindmap(@PathVariable("id") Long id, @RequestBody byte[] updatedMindmap) {
        final Mindmap map = this.getMindmapWithCheckPreconditions(id);

        try {
            map.setXml(updatedMindmap);
            map.setEditionDate(LocalDateTime.now());
            this.mindmapService.update(map);
        } catch (Exception e) {
            throw new ApiResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "");
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Mindmap getMindmapWithCheckPreconditions(final long id) {
        User currentUser = SecurityUtils.getCurrentUserDetails().getUser();
        if (!currentUser.isAdmin() && !mindmapService.isCurrentUserMindmapCreator(id)
                && !mindmapService.isCurrentUserCollaborator(id)
        ) {
            throw new ApiResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        final Mindmap map = mindmapService.findMindmapById(id);
        if (map == null) {
            throw new ApiResponseStatusException(HttpStatus.NOT_FOUND, "Mind map with id " + id + " does not exits");
        }
        return map;
    }
}
