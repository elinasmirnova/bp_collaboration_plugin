package cz.cvut.felk.kbss.freeplane.server.service.impl;

import cz.cvut.felk.kbss.freeplane.server.dao.CollaborationRepository;
import cz.cvut.felk.kbss.freeplane.server.model.Collaboration;
import cz.cvut.felk.kbss.freeplane.server.model.Collaborator;
import cz.cvut.felk.kbss.freeplane.server.model.Mindmap;
import cz.cvut.felk.kbss.freeplane.server.rest.dto.CollaborationDto;
import cz.cvut.felk.kbss.freeplane.server.service.CollaborationService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class CollaborationServiceImpl implements CollaborationService {

    private final CollaborationRepository collaborationRepository;

    public CollaborationServiceImpl(CollaborationRepository collaborationRepository) {
        this.collaborationRepository = collaborationRepository;
    }

    @Override
    @Transactional
    public void addCollaboration(Collaborator collaborator, Mindmap mindmap, String role) {
        Collaboration collaboration = new Collaboration();
        collaboration.setCollaborator(collaborator);
        collaboration.setMindmap(mindmap);
        collaboration.setRole(role);

        mindmap.addCollaboration(collaboration);
        collaborator.addCollaboration(collaboration);

        collaborationRepository.saveAndFlush(collaboration);
    }

    @Override
    @Transactional
    public void deleteCollaboration(Collaboration collaboration) {
        collaborationRepository.delete(collaboration);
    }

    @Override
    @Transactional
    public boolean existsCollaborationWithCollaboratorEmail(String email) {
        return collaborationRepository.getCollaborationByCollaboratorEmail(email) != null;
    }

    @Override
    @Transactional
    public List<Collaboration> getCollaborationsByMindmap(long mindmapId) {
        return collaborationRepository.findAllById(Collections.singleton(mindmapId));
    }

    @Override
    public Collaboration getCollaborationById(long id) {
        return collaborationRepository.findById(id).orElse(null);
    }

    @Override
    public void updateCollaborationRole(Collaboration collaboration, String newRole) {
        collaboration.setRole(newRole);
        collaborationRepository.saveAndFlush(collaboration);
    }

    @Override
    public CollaborationDto convertToDto(Collaboration collaboration) {
        CollaborationDto collaborationDto = new CollaborationDto();
        collaborationDto.setCollaborationId(collaboration.getId() != null ? collaboration.getId() : null);
        collaborationDto.setCollaboratorId(collaboration.getCollaborator() != null ? collaboration.getCollaborator().getCollaboratorId() : null);
        collaborationDto.setCollaboratorEmail(collaboration.getCollaborator() != null ? collaboration.getCollaborator().getEmail() : null);
        collaborationDto.setMindmapId(collaboration.getMindmap() != null ? collaboration.getMindmap().getMindmapId() : null);
        collaborationDto.setRole(collaboration.getRole());
        return collaborationDto;
    }
}
