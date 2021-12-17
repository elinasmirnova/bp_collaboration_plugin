package cz.cvut.felk.kbss.freeplane.server.service;

import cz.cvut.felk.kbss.freeplane.server.model.Collaborator;

public interface CollaboratorService {

    Collaborator getCollaboratorByEmail(String email);

    Collaborator createCollaborator(String email);
}
