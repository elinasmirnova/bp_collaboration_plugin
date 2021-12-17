package cz.cvut.felk.kbss.freeplane.server.config;

import cz.cvut.felk.kbss.freeplane.server.model.SessionInfo;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class SocketSessionRegistry {

    private final ConcurrentMap<String, Set<SessionInfo>> userSessionInfoMap = new ConcurrentHashMap();

    public SocketSessionRegistry() {
    }

    public Set<SessionInfo> getSessionInfo(String user) {
        Set<SessionInfo> sessionInfo = this.userSessionInfoMap.get(user);
        return sessionInfo != null ? sessionInfo : Collections.emptySet();
    }

    public ConcurrentMap<String, Set<SessionInfo>> getAllSessionIds() {
        return this.userSessionInfoMap;
    }

    public void registerSessionId(String user, String sessionId) {
        Assert.notNull(user, "User must not be null");
        Assert.notNull(sessionId, "Session ID must not be null");
        Set<SessionInfo> sessionInfoSet = this.userSessionInfoMap.get(user);
        SessionInfo sessionInfo = new SessionInfo(user, sessionId);
        if (sessionInfoSet != null) {
            sessionInfoSet.add(sessionInfo);
            this.userSessionInfoMap.put(user, sessionInfoSet);
        } else {
            Set<SessionInfo> newSessionInfoSet = Collections.singleton(sessionInfo);
            this.userSessionInfoMap.put(user, newSessionInfoSet);
        }
    }

    public void unregisterSessionId(String user, String sessionId) {
        Assert.notNull(user, "User Name must not be null");
        Assert.notNull(sessionId, "Session ID must not be null");

        Set<SessionInfo> sessionInfoSet = this.userSessionInfoMap.get(user);
        if (sessionInfoSet != null && sessionInfoSet.size() > 1) {
            Set<SessionInfo> updatedSessionInfoSet = sessionInfoSet.stream().filter(s -> sessionId.equals(s.getSessionId())).collect(Collectors.toSet());
            this.userSessionInfoMap.put(user, updatedSessionInfoSet);
        } else {
            this.userSessionInfoMap.remove(user);
        }
    }
}
