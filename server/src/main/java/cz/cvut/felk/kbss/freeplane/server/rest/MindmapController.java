package cz.cvut.felk.kbss.freeplane.server.rest;

import cz.cvut.felk.kbss.freeplane.server.config.SocketSessionRegistry;
import cz.cvut.felk.kbss.freeplane.server.exception.ApiResponseStatusException;
import cz.cvut.felk.kbss.freeplane.server.model.Collaboration;
import cz.cvut.felk.kbss.freeplane.server.model.Mindmap;
import cz.cvut.felk.kbss.freeplane.server.model.SessionInfo;
import cz.cvut.felk.kbss.freeplane.server.model.User;
import cz.cvut.felk.kbss.freeplane.server.rest.dto.*;
import cz.cvut.felk.kbss.freeplane.server.security.utils.SecurityUtils;
import cz.cvut.felk.kbss.freeplane.server.service.MindmapService;
import cz.cvut.felk.kbss.freeplane.server.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/mindmaps")
public class MindmapController {

    @Autowired
    private SocketSessionRegistry socketSessionRegistry;

    private final MindmapService mindmapService;
    private final SessionService sessionService;

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public MindmapController(MindmapService mindmapService, SessionService sessionService, SimpMessagingTemplate messagingTemplate) {
        this.mindmapService = mindmapService;
        this.sessionService = sessionService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping
    public ResponseEntity<Void> createMindmap(@RequestBody MindmapToCreateDto mindmapDto) {
        Mindmap mindmap = new Mindmap();
        mindmap.setCreator(Objects.requireNonNull(SecurityUtils.getCurrentUserDetails()).getUser());
        mindmap.setCreationDate(LocalDateTime.now());
        mindmap.setXml(mindmapDto.getXml());
        mindmap.setPublic(mindmapDto.isPublic());
        mindmap.setTitle(mindmapDto.getTitle());

        try {
            this.mindmapService.save(mindmap);
        } catch (Exception e) {
            throw new ApiResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "");
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/all")
    public ResponseEntity<List<MindmapInfoDto>> getAllMindmaps() {
        final List<MindmapInfoDto> mindmaps;
        try {
            mindmaps = this.mindmapService.getAllMindmaps().stream().map(mindmapService::convertToDto).collect(Collectors.toList());
        } catch (Exception e) {
            throw new ApiResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "");
        }
        return new ResponseEntity<>(mindmaps, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MindmapContentDto> getMindmapContentById(@PathVariable long id) throws Exception {
        final Mindmap map = getMindmapWithPreconditions(id);
        final MindmapContentDto mapDto = this.mindmapService.convertToContentDto(map);

        return new ResponseEntity<>(mapDto, HttpStatus.OK);
    }

    @GetMapping("/{id}/info")
    public ResponseEntity<MindmapInfoDto> getMindmapInfoById(@PathVariable long id) throws Exception {
        final Mindmap map = getMindmapWithPreconditions(id);
        final MindmapInfoDto mapDto = this.mindmapService.convertToDto(map);

        return new ResponseEntity<>(mapDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMindmap(@PathVariable Long id) throws Exception {
        try {
            if (!this.mindmapService.isCurrentUserMindmapCreator(id)) {
                throw new ApiResponseStatusException(HttpStatus.FORBIDDEN, "");
            }
            final Mindmap map = mindmapService.findMindmapById(id);
            if (map != null) {
                mindmapService.delete(map);
            }
        } catch (Exception e) {
            throw new ApiResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "");
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateMindmapAccess(@PathVariable("id") Long id, @RequestParam("isPublic") boolean isPublic) {
        final Mindmap map = this.getMindmapWithPreconditions(id);

        try {
            map.setPublic(isPublic);
            this.mindmapService.update(map);
        } catch (Exception e) {
            throw new ApiResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "");
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<MindmapsCollectionDto> getUsersMindmaps() {

        final MindmapsCollectionDto mindmapsCollectionDto;
        try {
            User user = SecurityUtils.getCurrentUserDetails().getUser();

            final List<MindmapInfoDto> yourMindmaps = this.mindmapService.getOwnMindmapsByCollaborator(user.getCollaboratorId())
                    .stream()
                    .map(this.mindmapService::convertToDto)
                    .collect(Collectors.toList());

            final List<MindmapInfoDto> sharedMindmaps = this.mindmapService.getSharedMindmapsByCollaborator(user.getCollaboratorId())
                    .stream()
                    .map(this.mindmapService::convertToDto)
                    .collect(Collectors.toList());

            mindmapsCollectionDto = new MindmapsCollectionDto();
            mindmapsCollectionDto.setYourMindmaps(yourMindmaps);
            mindmapsCollectionDto.setSharedMindmaps(sharedMindmaps);

        } catch (Exception e) {
            throw new ApiResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "");
        }

        return new ResponseEntity<>(mindmapsCollectionDto, HttpStatus.OK);
    }

    @GetMapping("/{id}/share")
    public ResponseEntity<String> shareMindmap(@PathVariable Long id, @RequestParam("email") String email, @RequestParam("role") String role) {

        if (!SecurityUtils.getCurrentUserDetails().getUser().isAdmin() && !this.mindmapService.isCurrentUserMindmapCreator(id)) {
            throw new ApiResponseStatusException(HttpStatus.FORBIDDEN, "");
        }

        String uuid = UUID.nameUUIDFromBytes(email.getBytes()).toString();

        final String uri = UriComponentsBuilder.newInstance()
                .scheme("http").host("localhost:8080")
                .path("/mindmap")
                .path("/" + id.toString())
                .path("/share")
                .queryParam("to", email)
                .queryParam("role", role)
                .toUriString();

        // save to DB ?
        return new ResponseEntity<>(uri, HttpStatus.CREATED);
    }


    @MessageMapping("/mindmap/{id}")
    public String joinCollaboration(@DestinationVariable Long mindmapId, @RequestParam("collaborator") String email, @RequestParam("id") String role) {
        User currentUser = SecurityUtils.getCurrentUserDetails().getUser();

        Mindmap map = this.mindmapService.findMindmapById(mindmapId);

        for (Collaboration collaboration : map.getCollaborations()) {
            Set<SessionInfo> activeSessions = this.socketSessionRegistry.getSessionInfo(collaboration.getCollaborator().getEmail());
            //activeSessions.stream().filter(session -> session.getMap().getMindmapId().equals(mindmapId)).map(SessionInfo::get)
        }
        Set<SessionInfo> sessionInfoSet = this.socketSessionRegistry.getSessionInfo(currentUser.getEmail());
        return null;
    }


    //    @GetMapping("/{id}")
    // @MessageMapping("/{id}")
    public ResponseEntity openMindmap(@DestinationVariable Long id) throws Exception {
        final Mindmap map = mindmapService.findMindmapById(id);
        if (map == null) {
            throw new Exception("CHYBA");
        }

        if (map.getCollaborations() != null) {
//            List<Long> collaborationsId = map.getCollaborations().stream().map(Collaboration::getId).collect(Collectors.toList());
//            for (Long collaborationId : collaborationsId) {
//                sessionService.
//            }
            for (Collaboration collaboration : map.getCollaborations()) {
//                Set<String> sessionIds = socketSessionRegistry.getSessionIds(collaboration.getCollaborator().toString());
//                if (sessionIds.stream().anyMatch(sessionId -> sessionService.getSessionById(sessionId).isEditingFlag())) {
//
//                }
//                if (!socketSessionRegistry.getSessionIds(collaboration.getCollaborator().getCollaboratorId().toString()).isEmpty()) {
                messagingTemplate.convertAndSendToUser(collaboration.getCollaborator().getCollaboratorId().toString(), "topic/mindmaps/" + id, map.getXml());
//                }
            }
        }
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    private Mindmap getMindmapWithPreconditions(final long id) {
        if (!SecurityUtils.getCurrentUserDetails().getUser().isAdmin() && !mindmapService.isCurrentUserMindmapCreator(id)) {
            throw new ApiResponseStatusException(HttpStatus.FORBIDDEN, "");
        }

        final Mindmap map = mindmapService.findMindmapById(id);
        if (map == null) {
            throw new ApiResponseStatusException(HttpStatus.NOT_FOUND, "");
        }

        return map;
    }
}
