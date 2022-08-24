package cz.cvut.felk.kbss.freeplane.server.rest.dto;

import java.time.LocalDateTime;
import java.util.List;

public class MindmapInfoDto {

    private Long mindmapId;
    private String title;
    private String isPublic;
    private LocalDateTime creationDate;
    private LocalDateTime editionDate;
    private Long lastEditorId;
    private Long creatorId;

    private List<CollaborationDto> collaborations;

    public Long getMindmapId() {
        return mindmapId;
    }

    public void setMindmapId(Long mindmapId) {
        this.mindmapId = mindmapId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String isPublic() {
        return isPublic;
    }

    public void setPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getEditionDate() {
        return editionDate;
    }

    public void setEditionDate(LocalDateTime editionDate) {
        this.editionDate = editionDate;
    }

    public Long getLastEditorId() {
        return lastEditorId;
    }

    public void setLastEditorId(Long lastEditorId) {
        this.lastEditorId = lastEditorId;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public List<CollaborationDto> getCollaborations() {
        return collaborations;
    }

    public void setCollaborations(List<CollaborationDto> collaborations) {
        this.collaborations = collaborations;
    }
}
