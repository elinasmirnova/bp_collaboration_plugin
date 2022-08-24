package cz.cvut.felk.kbss.freeplane.server.listener;

import cz.cvut.felk.kbss.freeplane.server.config.SocketSessionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

/**
 * WebSocket events listener.
 */
@Component
public class WebSocketEventListener {

    @Autowired
    private SocketSessionRegistry webAgentSessionRegistry;

    /**
     * Listens to and handles WebSocket sessions.
     *
     * @param event
     */
    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String userEmail = sha.getNativeHeader("email").get(0);
        String mindmapId = sha.getNativeHeader("mindmap").get(0);
        webAgentSessionRegistry.registerSession(mindmapId, userEmail);
    }
}
