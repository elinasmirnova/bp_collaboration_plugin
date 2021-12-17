package cz.cvut.felk.kbss.freeplane.server.dao;

import cz.cvut.felk.kbss.freeplane.server.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, String> {

    @Query("SELECT s FROM Session s WHERE s.sessionId = :id")
    Session getSessionById(@Param("id") String id);

    @Query("SELECT s FROM Session s WHERE s.collaboration.id = :collaborationId")
    List<Session> getSessionByCollaborationId(@Param("collaborationId") Long collaborationId);
}
