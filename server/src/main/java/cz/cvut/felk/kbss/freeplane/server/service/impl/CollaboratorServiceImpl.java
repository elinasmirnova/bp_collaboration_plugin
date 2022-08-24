package cz.cvut.felk.kbss.freeplane.server.service.impl;

import cz.cvut.felk.kbss.freeplane.server.dao.CollaboratorRepository;
import cz.cvut.felk.kbss.freeplane.server.model.Collaborator;
import cz.cvut.felk.kbss.freeplane.server.service.CollaboratorService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

/**
 * Collaborator service implementation
 */
@Service
public class CollaboratorServiceImpl implements CollaboratorService {

    private final CollaboratorRepository collaboratorRepository;

    public CollaboratorServiceImpl(CollaboratorRepository collaboratorRepository) {
        this.collaboratorRepository = collaboratorRepository;
    }

    @Override
    @Transactional
    public Collaborator getCollaboratorByEmail(String email) {
        return collaboratorRepository.getCollaboratorByEmail(email);
    }

    @Override
    @Transactional
    public Collaborator createCollaborator(String email) {
        Collaborator collaborator = new Collaborator();
        collaborator.setEmail(email);
        collaborator.setCreationDate(LocalDateTime.now());
        return collaboratorRepository.saveAndFlush(collaborator);
    }
}
