package cz.cvut.felk.kbss.freeplane.server.dao;

import cz.cvut.felk.kbss.freeplane.server.model.Collaboration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollaborationRepository extends JpaRepository<Collaboration, Long> {

    @Query("SELECT c FROM Collaboration c WHERE c.collaborator.email = :email")
    Collaboration getCollaborationByCollaboratorEmail(@Param("email") String email);

    @Query("SELECT c FROM Collaboration c WHERE c.mindmap.mindmapId = :mindmapId")
    List<Collaboration> getCollaborationsByMindmap(@Param("mindmapId") long mindmapId);

    @Query("SELECT c FROM Collaboration c WHERE c.id = :id")
    Collaboration getCollaborationById(@Param("id") long id);
}
