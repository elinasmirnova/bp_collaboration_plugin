package cz.cvut.felk.kbss.freeplane.server.model;

/**
 * Mind map locking info for WebSocket communication.
 */
public class MindmapLockInfo {

    private Long mindmapId;
    private String lockedBy;
    private boolean isLocked;

    public MindmapLockInfo(Long mindmapId, String lockedBy, boolean isLocked) {
        this.mindmapId = mindmapId;
        this.lockedBy = lockedBy;
        this.isLocked = isLocked;
    }

    public Long getMindmapId() {
        return mindmapId;
    }

    public void setMindmapId(Long mindmapId) {
        this.mindmapId = mindmapId;
    }

    public String getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }
}
