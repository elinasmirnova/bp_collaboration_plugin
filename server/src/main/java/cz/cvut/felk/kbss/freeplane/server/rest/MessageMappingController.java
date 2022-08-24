package cz.cvut.felk.kbss.freeplane.server.rest;

import cz.cvut.felk.kbss.freeplane.server.config.SocketSessionRegistry;
import cz.cvut.felk.kbss.freeplane.server.model.MindmapLockInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * STOMP messaging controller
 */
@Controller
public class MessageMappingController {

    @Autowired
    SocketSessionRegistry webAgentSessionRegistry;

    private static final Logger logger = LoggerFactory.getLogger(MessageMappingController.class);

    @Autowired
    private MessageSendingOperations<String> messageSendingOperations;

    @MessageMapping("/request")
    public void handleRequestMessage(String message) {
        if (message == null) {
            throw new RuntimeException(
                    String.format("'%s' is rejected", message)
            );
        }

        String response = "not locked";
        String mindmapId = message.substring(0, message.lastIndexOf(","));
        String email = message.substring(message.lastIndexOf(",") + 1);

        MindmapLockInfo lockInfo = webAgentSessionRegistry.getMindMapLockInfo(mindmapId);
        if (lockInfo != null) {
            if (!lockInfo.getLockedBy().equals(email)) {
                response = "locked";
            }
        }
        logger.info("Message with response: {}", response);
        this.messageSendingOperations.convertAndSend("/queue/user/" + email + "/responses", response);
    }

    @MessageMapping("/disconnect")
    public void handleDisconnectMessage(String message) {
        if (message == null) {
            throw new RuntimeException(
                    String.format("'%s' is rejected", message)
            );
        }

        String mindmapId = message.substring(0, message.lastIndexOf(","));
        String email = message.substring(message.lastIndexOf(",") + 1);

        webAgentSessionRegistry.unregisterSession(mindmapId, email);
        String response = "disconnect";
        logger.info("Message with response: {}", response);
        this.messageSendingOperations.convertAndSend("/topic/mindmap/" + mindmapId + "/disconnect", response);
    }

    @MessageMapping("/mindmap/{id}")
    @SendTo("/queue/mindmaps-responses")
    public byte[] handleMindmapMessage(@DestinationVariable Long id, byte[] message) {
        logger.info("Message with response: {}", message);
        if (message.equals("zero")) {
            throw new RuntimeException(String.format("'%s' is rejected", message));
        }
        return message;
    }

    @MessageExceptionHandler
    @SendTo("/queue/errors")
    public String handleException(Throwable exception) {
        logger.error("Server exception", exception);
        return "server exception: " + exception.getMessage();
    }
}
