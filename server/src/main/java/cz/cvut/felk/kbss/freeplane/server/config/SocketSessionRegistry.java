package cz.cvut.felk.kbss.freeplane.server.config;

import cz.cvut.felk.kbss.freeplane.server.model.MindmapLockInfo;
import org.springframework.util.Assert;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * WebSocket sessions registry.
 */
public class SocketSessionRegistry {

    private final ConcurrentMap<Long, MindmapLockInfo> mindmapLockInfoCache = new ConcurrentHashMap();

    public SocketSessionRegistry() {
    }

    public MindmapLockInfo getMindMapLockInfo(String mindmap) {
        return this.mindmapLockInfoCache.get(Long.parseLong(mindmap));
    }

    /**
     * Registers WebSocket session after connection
     *
     * @param mindMapId mind map ID
     * @param user      user's e-mail
     */
    public void registerSession(String mindMapId, String user) {
        Assert.notNull(user, "User must not be null");
        Assert.notNull(mindMapId, "MindMap ID must not be null");
        if (!mindmapLockInfoCache.containsKey(Long.parseLong(mindMapId))) {
            MindmapLockInfo lockInfo = new MindmapLockInfo(Long.parseLong(mindMapId), user, true);
            mindmapLockInfoCache.put(Long.parseLong(mindMapId), lockInfo);
        }
    }

    /**
     * Registers WebSocket session after disconnection
     *
     * @param mindMapId mind map ID
     * @param user      user's e-mail
     */
    public void unregisterSession(String mindMapId, String user) {
        Assert.notNull(user, "User must not be null");
        Assert.notNull(mindMapId, "MindMap ID must not be null");
        mindmapLockInfoCache.remove(Long.parseLong(mindMapId));
    }
}
