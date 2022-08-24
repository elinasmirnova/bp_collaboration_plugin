package cz.cvut.felk.kbss.freeplane.server.rest;

import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

/**
 * WebSocket notification controller
 */
@Component
public class NotificationController {

    private final MessageSendingOperations<String> messageSendingOperations;

    public NotificationController(MessageSendingOperations<String> messageSendingOperations) {
        this.messageSendingOperations = messageSendingOperations;
    }

    public void disconnectNotify(long mindmapId) {
        String message = "disconnect";
        this.messageSendingOperations.convertAndSend("/topic/mindmap/" + mindmapId + "/disconnect", message);
    }
}
