package cz.cvut.felk.kbss.freeplane.server.rest.dto;

public class CollaborationDto {

    private Long collaborationId;
    private Long collaboratorId;
    private String collaboratorEmail;
    private Long mindmapId;
    private String role;

    public Long getCollaborationId() {
        return collaborationId;
    }

    public void setCollaborationId(Long collaborationId) {
        this.collaborationId = collaborationId;
    }

    public Long getCollaboratorId() {
        return collaboratorId;
    }

    public void setCollaboratorId(Long collaboratorId) {
        this.collaboratorId = collaboratorId;
    }

    public String getCollaboratorEmail() {
        return collaboratorEmail;
    }

    public void setCollaboratorEmail(String collaboratorEmail) {
        this.collaboratorEmail = collaboratorEmail;
    }

    public Long getMindmapId() {
        return mindmapId;
    }

    public void setMindmapId(Long mindmapId) {
        this.mindmapId = mindmapId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
