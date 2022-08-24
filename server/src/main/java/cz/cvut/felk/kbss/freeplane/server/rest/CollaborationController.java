package cz.cvut.felk.kbss.freeplane.server.rest;

import cz.cvut.felk.kbss.freeplane.server.exception.ApiResponseStatusException;
import cz.cvut.felk.kbss.freeplane.server.model.Collaboration;
import cz.cvut.felk.kbss.freeplane.server.model.Collaborator;
import cz.cvut.felk.kbss.freeplane.server.model.Mindmap;
import cz.cvut.felk.kbss.freeplane.server.rest.dto.CollaborationDto;
import cz.cvut.felk.kbss.freeplane.server.security.utils.SecurityUtils;
import cz.cvut.felk.kbss.freeplane.server.service.CollaborationService;
import cz.cvut.felk.kbss.freeplane.server.service.CollaboratorService;
import cz.cvut.felk.kbss.freeplane.server.service.MindmapService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Collaboration REST controller.
 */
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/collaborations")
public class CollaborationController {

    private final CollaborationService collaborationService;
    private final CollaboratorService collaboratorService;
    private final MindmapService mindmapService;

    public CollaborationController(CollaborationService collaborationService, CollaboratorService collaboratorService, MindmapService mindmapService) {
        this.collaborationService = collaborationService;
        this.collaboratorService = collaboratorService;
        this.mindmapService = mindmapService;
    }

    /**
     * Adding a collaborator to the mind map
     *
     * @param collaborationDto collaboration DTO request body
     * @return HTTPStatus code
     */
    @PostMapping
    public ResponseEntity<Void> addCollaboration(@RequestBody CollaborationDto collaborationDto) {
        Objects.requireNonNull(collaborationDto.getCollaboratorEmail(), "Input collaborator email must not be null");
        Objects.requireNonNull(collaborationDto.getMindmapId(), "Input mindmap id must not be null");
        Objects.requireNonNull(collaborationDto.getRole(), "Input role must not be null");

        if (!SecurityUtils.getCurrentUserDetails().getUser().isAdmin() && !this.mindmapService.isCurrentUserMindmapCreator(collaborationDto.getMindmapId())) {
            throw new ApiResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        if (this.collaborationService.getCollaborationWithCollaboratorEmailForMindmap(collaborationDto.getCollaboratorEmail(), collaborationDto.getMindmapId()) != null) {
            throw new ApiResponseStatusException(HttpStatus.CONFLICT, "Collaborator with this e-mail already has an access to the mind map with id " + collaborationDto.getMindmapId());
        }

        try {
            Collaborator collaborator = collaboratorService.getCollaboratorByEmail(collaborationDto.getCollaboratorEmail());
            if (collaborator == null) {
                collaborator = collaboratorService.createCollaborator(collaborationDto.getCollaboratorEmail());
            }

            Mindmap mindmap = mindmapService.findMindmapById(collaborationDto.getMindmapId());

            collaborationService.addCollaboration(collaborator, mindmap, collaborationDto.getRole());
        } catch (Exception e) {
            throw new ApiResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Some error occurred while adding collaborator for the mindmap with id " + collaborationDto.getMindmapId());
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Deleting collaboration endpoint.
     *
     * @param id collaboration ID
     * @return HTTPStatus code
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCollaboration(@PathVariable("id") long id) {
        final Collaboration collaboration = getCollaborationWithPreconditions(id);

        try {
            this.collaborationService.deleteCollaboration(collaboration);
        } catch (Exception e) {
            throw new ApiResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Some error occurred while deleting collaboration with id " + id);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Fetching collaborations for the mind map endpoint.
     *
     * @param mindmapId mind map ID
     * @return collection of the collaborations and HTTPStatus code
     */
    @GetMapping("/mindmap/{id}")
    public ResponseEntity<List<CollaborationDto>> getCollaborationsByMindmap(@PathVariable("id") long mindmapId) {
        final List<CollaborationDto> collaborationDtoList;

        if (!SecurityUtils.getCurrentUserDetails().getUser().isAdmin() && !this.mindmapService.isCurrentUserMindmapCreator(mindmapId)) {
            throw new ApiResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        try {
            collaborationDtoList = this.collaborationService.getCollaborationsByMindmap(mindmapId)
                    .stream()
                    .map(this.collaborationService::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ApiResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Some error occurred while fetching collaborations for the mindmap with id " + mindmapId);
        }

        return new ResponseEntity<>(collaborationDtoList, HttpStatus.OK);
    }

    /**
     * Get collaboration by its ID.
     *
     * @param id collaboration ID
     * @return collaboration DTO and HTTPStatus code
     */
    @GetMapping("/{id}")
    public ResponseEntity<CollaborationDto> getCollaborationById(@PathVariable("id") long id) {
        final CollaborationDto collaborationDto;
        final Collaboration collaboration = getCollaborationWithPreconditions(id);

        try {
            collaborationDto = this.collaborationService.convertToDto(collaboration);
        } catch (Exception e) {
            throw new ApiResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Some error occurred while fetching collaboration with id " + id);
        }
        return new ResponseEntity<>(collaborationDto, HttpStatus.OK);
    }

    /**
     * Updating role for the collaboration.
     *
     * @param id   mind map ID
     * @param role a new user's role
     * @return HTTPStatus code
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCollaborationRole(@PathVariable("id") long id, @RequestParam String role) {
        final Collaboration collaboration = getCollaborationWithPreconditions(id);

        try {
            this.collaborationService.updateCollaborationRole(collaboration, role);
        } catch (Exception e) {
            throw new ApiResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Some error occurred while updating collaboration role for collaboration id " + id);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Collaboration getCollaborationWithPreconditions(final long id) {
        final Collaboration collaboration = this.collaborationService.getCollaborationById(id);

        if (collaboration == null) {
            throw new ApiResponseStatusException(HttpStatus.NOT_FOUND, "Collaboration with id " + id + " does not exist");
        }

        if (!SecurityUtils.getCurrentUserDetails().getUser().isAdmin() && !this.mindmapService.isCurrentUserMindmapCreator(collaboration.getMindmap().getMindmapId())) {
            throw new ApiResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return collaboration;
    }
}
