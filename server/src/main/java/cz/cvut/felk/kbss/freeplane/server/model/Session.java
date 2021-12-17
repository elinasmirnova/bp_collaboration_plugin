package cz.cvut.felk.kbss.freeplane.server.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class Session implements Serializable {

    private String sessionId; // composite primary key
    private LocalDateTime creationDate;
    private LocalDateTime editionDate;
    private Collaboration collaboration;
    private boolean editingFlag;

    @Id
    @Column(name = "session_id")
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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

    @ManyToOne
    @JoinColumn(name = "collaboration_id", referencedColumnName = "id", nullable = false)
    public Collaboration getCollaboration() {
        return collaboration;
    }

    public void setCollaboration(Collaboration collaboration) {
        this.collaboration = collaboration;
    }

    @Basic
    @Column(name = "editing_flag")
    public boolean isEditingFlag() {
        return editingFlag;
    }

    public void setEditingFlag(boolean editingFlag) {
        this.editingFlag = editingFlag;
    }
}
