package cz.cvut.felk.kbss.freeplane.server.service;

import cz.cvut.felk.kbss.freeplane.server.model.Collaboration;
import cz.cvut.felk.kbss.freeplane.server.model.Collaborator;
import cz.cvut.felk.kbss.freeplane.server.model.Mindmap;
import cz.cvut.felk.kbss.freeplane.server.rest.dto.CollaborationDto;

import java.util.List;

/**
 * Collaboration service interface
 */
public interface CollaborationService {

    void addCollaboration(Collaborator collaborator, Mindmap mindmap, String role);

    void deleteCollaboration(Collaboration collaboration);

    Collaboration getCollaborationWithCollaboratorEmailForMindmap(String email, Long mindMapId);

    List<Collaboration> getCollaborationsByMindmap(long mindmapId);

    Collaboration getCollaborationById(long id);

    CollaborationDto convertToDto(Collaboration collaboration);

    void updateCollaborationRole(Collaboration collaboration, String role);
}
