package cz.cvut.felk.kbss.freeplane.server.listener;

import cz.cvut.felk.kbss.freeplane.server.config.SocketSessionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    @Autowired
    SocketSessionRegistry webAgentSessionRegistry;

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        // login get from header
        String agentId = sha.getNativeHeader("login").get(0);
        String sessionId = sha.getSessionId();
        webAgentSessionRegistry.registerSessionId(agentId, sessionId);
    }

    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        // login get from header
        String agentId = sha.getNativeHeader("login").get(0);
        String sessionId = sha.getSessionId();
        webAgentSessionRegistry.unregisterSessionId(agentId, sessionId);
    }
}
