package cz.cvut.felk.kbss.freeplane.server.dao;

import cz.cvut.felk.kbss.freeplane.server.model.Mindmap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MindmapRepository extends JpaRepository<Mindmap, Long> {

    @Query("SELECT m FROM Mindmap m WHERE m.mindmapId = :id")
    Mindmap getMindmapById(@Param("id") long id);

    @Query("SELECT m FROM Mindmap m WHERE m.creator.collaboratorId = :collaboratorId")
    List<Mindmap> getOwnMindmapsByCollaboratorId(@Param("collaboratorId") long collaboratorId);

    @Query("SELECT m FROM Mindmap m JOIN Collaboration c ON m.mindmapId = c.mindmap.mindmapId WHERE c.collaborator.collaboratorId = :collaboratorId")
    List<Mindmap> getSharedMindmapsByCollaboratorId(@Param("collaboratorId") long collaboratorId);
}
