package org.freeplane.plugin.collaboration.model;

/**
 * Server mind map DTO
 */
public class ServerMindMap {

    private String mindMapId;
    private String title;
    private byte[] bytes;
    private boolean isReadOnly;

    public ServerMindMap(String mindMapId, byte[] bytes, boolean isReadOnly) {
        this.mindMapId = mindMapId;
        this.bytes = bytes;
        this.isReadOnly = isReadOnly;
    }

    public String getMindMapId() {
        return mindMapId;
    }

    public void setMindMapId(String mindMapId) {
        this.mindMapId = mindMapId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public void setReadOnly(boolean readOnly) {
        isReadOnly = readOnly;
    }
}
