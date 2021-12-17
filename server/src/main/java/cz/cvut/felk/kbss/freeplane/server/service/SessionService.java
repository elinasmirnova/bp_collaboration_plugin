package cz.cvut.felk.kbss.freeplane.server.service;

import cz.cvut.felk.kbss.freeplane.server.model.Session;

public interface SessionService {

    Session getSessionById(final String id);
}
