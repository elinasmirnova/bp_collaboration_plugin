package cz.cvut.felk.kbss.freeplane.server.service.impl;

import cz.cvut.felk.kbss.freeplane.server.dao.SessionRepository;
import cz.cvut.felk.kbss.freeplane.server.model.Session;
import cz.cvut.felk.kbss.freeplane.server.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionServiceImpl implements SessionService {

    private final SessionRepository repository;

    @Autowired
    public SessionServiceImpl(SessionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Session getSessionById(String id) {
        return repository.getSessionById(id);
    }
}
