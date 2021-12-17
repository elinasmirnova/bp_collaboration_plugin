package cz.cvut.felk.kbss.freeplane.server.model;

public class SessionInfo {

    private Long userId;
    private String login;
    private String sessionId;
    private Long timestamp;
    private Mindmap map;
    private boolean isLocked;

    public SessionInfo(String login, String sessionId) {
        this.login = login;
        this.sessionId = sessionId;
    }

    public SessionInfo(Long userId, String sessionId, Mindmap map) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.map = map;
        timestamp = System.currentTimeMillis();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Mindmap getMap() {
        return map;
    }

    public void setMap(Mindmap map) {
        this.map = map;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }
}
