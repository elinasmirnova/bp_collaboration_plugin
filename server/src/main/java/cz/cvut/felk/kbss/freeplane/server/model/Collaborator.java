package cz.cvut.felk.kbss.freeplane.server.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@Table(name="collaborator")
public class Collaborator implements Serializable {

    private Long collaboratorId;
    private String email;
    private LocalDateTime creationDate;

    private List<Collaboration> collaborations;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collaborator_id")
    public Long getCollaboratorId() {
        return collaboratorId;
    }

    public void setCollaboratorId(Long id) {
        this.collaboratorId = id;
    }

    @Basic
    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "creation_date")
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @OneToMany(mappedBy = "collaborator", cascade = CascadeType.ALL)
    public List<Collaboration> getCollaborations() {
        return collaborations;
    }

    public void setCollaborations(List<Collaboration> collaborations) {
        this.collaborations = collaborations;
    }

    public void addCollaboration(Collaboration collaboration) {
        if (this.collaborations == null) {
            this.collaborations = new ArrayList<>();
        }
        this.collaborations.add(collaboration);
    }

//    @Override
//    public String toString() {
//        return "Collaborator{" +
//                "collaboratorId=" + collaboratorId +
//                ", email='" + email + '\'' +
//                ", creationDate=" + creationDate +
//                ", collaborations=" + collaborations +
//                '}';
//    }
}
