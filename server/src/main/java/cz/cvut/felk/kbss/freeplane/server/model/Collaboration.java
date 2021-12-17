package cz.cvut.felk.kbss.freeplane.server.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Collaboration implements Serializable {

    private Long id;
    private String role;
    private Collaborator collaborator;
    private Mindmap mindmap;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "role")
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collaborator_id", referencedColumnName = "collaborator_id", nullable = false)
    public Collaborator getCollaborator() {
        return collaborator;
    }

    public void setCollaborator(Collaborator collaboratorByCollaboratorId) {
        this.collaborator = collaboratorByCollaboratorId;
    }

    @ManyToOne
    @JoinColumn(name = "mindmap_id", referencedColumnName = "mindmap_id", nullable = false)
    public Mindmap getMindmap() {
        return mindmap;
    }

    public void setMindmap(Mindmap mindmaps) {
        this.mindmap = mindmaps;
    }
}
