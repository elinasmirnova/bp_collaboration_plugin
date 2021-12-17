package cz.cvut.felk.kbss.freeplane.server.dao;

import cz.cvut.felk.kbss.freeplane.server.model.Collaborator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CollaboratorRepository extends JpaRepository<Collaborator, Long> {

    @Query("SELECT с FROM Collaborator с WHERE с.email = :email")
    Collaborator getCollaboratorByEmail(@Param("email") String email);
}
