package cz.cvut.felk.kbss.freeplane.server.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Mind map entity
 */
@Entity
public class Mindmap implements Serializable {

    private Long mindmapId;
    private String title;
    private byte[] xml;
    private boolean isPublic;
    private LocalDateTime creationDate;
    private LocalDateTime editionDate;
    private Long lastEditorId;

    private User creator;

    private List<Collaboration> collaborations;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mindmap_id")
    public Long getMindmapId() {
        return mindmapId;
    }

    public void setMindmapId(Long mindmapId) {
        this.mindmapId = mindmapId;
    }

    @Basic
    @Column(name = "title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "xml")
    public byte[] getXml() {
        return xml;
    }

    public void setXml(byte[] xml) {
        this.xml = xml;
    }

    @Basic
    @Column(name = "is_public")
    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    @Basic
    @Column(name = "creation_date")
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @Basic
    @Column(name = "edition_date")
    public LocalDateTime getEditionDate() {
        return editionDate;
    }

    public void setEditionDate(LocalDateTime editionDate) {
        this.editionDate = editionDate;
    }

    @Basic
    @Column(name = "last_editor_id")
    public Long getLastEditorId() {
        return lastEditorId;
    }

    public void setLastEditorId(Long lastEditorId) {
        this.lastEditorId = lastEditorId;
    }


    @OneToMany(mappedBy = "mindmap", fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "creator_id", referencedColumnName = "collaborator_id", nullable = false)
    public User getCreator() {
        return creator;
    }

    public void setCreator(User user) {
        this.creator = user;
    }

    @PreRemove
    public void deleteUsersMindmap() {
        creator.getMindmaps().remove(this);
    }

    @Override
    public String toString() {
        return "Mindmap{" +
                "mindmapId=" + mindmapId +
                ", title='" + title + '\'' +
                ", xml='" + xml + '\'' +
                ", isPublic=" + isPublic +
                ", creationDate=" + creationDate +
                ", editionDate=" + editionDate +
                ", lastEditorId=" + lastEditorId +
                ", creator=" + creator +
                ", collaborations=" + collaborations +
                '}';
    }
}
