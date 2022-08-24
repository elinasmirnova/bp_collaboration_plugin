package org.freeplane.plugin.collaboration;

import org.freeplane.core.ui.components.UITools;
import org.freeplane.features.mode.Controller;
import org.freeplane.n3.nanoxml.XMLException;
import org.freeplane.plugin.collaboration.model.ServerMindMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.*;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;

/**
 * Handles a client STOMP session from the server.
 */
public class ClientStompSessionHandler extends StompSessionHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ClientStompSessionHandler.class);

    /**
     * After connection plugin subscribes to the topics and sends a request message to get information whether a mind map is locked or not.
     * <p>
     * If the mind map is not locked, then the mind map will be opened and user can edit it. After saving the mind map, user will be disconnected from the STOMP server.
     * <p>
     * If the mind map is locked, then the warning will be shown. If user clicks the button OK and by this time the mind map will be released by another user,
     * the mind map will be opened and user will be able to edit the mind map immediately.
     *
     * @param session STOMP session
     * @param headers headers
     */
    @Override
    public void afterConnected(StompSession session, StompHeaders headers) {
        logger.info("Client connected: headers {}", headers);

        session.subscribe("/app/subscribe", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders stompHeaders) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders stompHeaders, Object o) {
                System.out.println("Message is " +
                        o.toString());
            }
        });

        session.subscribe("/queue/user/" + ConnectionService.getEmail() + "/responses", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders stompHeaders) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders stompHeaders, Object o) {
                ServerMindMap mindMap = ConnectionService.getMindMap();
                if (o.toString().equals("locked")) {
                    Controller.getCurrentController().getMapViewManager().closeWithoutSaving();
                    JOptionPane optionPane = new JOptionPane("Someone is editing mind map, try again later", JOptionPane.WARNING_MESSAGE);
                    JDialog dialog = optionPane.createDialog(UITools.getCurrentFrame(), "Warning!");

                    dialog.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            session.disconnect();
                            ConnectionService.setCurrentStompSession(null);
                            ConnectionService.setMindMap(null);
                        }
                    });

                    session.subscribe("/topic/mindmap/" + mindMap.getMindMapId() + "/disconnect", new StompFrameHandler() {
                        @Override
                        public Type getPayloadType(StompHeaders stompHeaders) {
                            return String.class;
                        }

                        @Override
                        public void handleFrame(StompHeaders stompHeaders, Object o) {
                            if (o.toString().equals("disconnect")) {
                                dialog.dispose();
                                ServerMindMap serverMindmap = null;
                                try {
                                    serverMindmap = ConnectionService.getInstance().getMindmap(mindMap.getMindMapId());
                                    ConnectionService.getInstance().openMapFromServer(new ByteArrayInputStream(serverMindmap.getBytes()), serverMindmap.isReadOnly());
                                } catch (IOException | XMLException e) {
                                    JOptionPane.showMessageDialog(UITools.getCurrentFrame(), "Error while fetching mind map, please try again", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                                try {
                                    ConnectionService.getInstance().openMindMapWebsockets(serverMindmap);
                                } catch (ExecutionException | InterruptedException ex) {
                                    JOptionPane.showMessageDialog(UITools.getCurrentFrame(), "Error while establishing connection, please try again", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    });

                    dialog.setVisible(true);
                    dialog.setModal(true);
                }
            }
        });

        session.subscribe("/queue/errors", this);

        String message = ConnectionService.getMindMap().getMindMapId() + "," + ConnectionService.getEmail();
        logger.info("Client sends: {}", message);
        session.send("/app/request", message);
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        logger.info("Client received: payload {}, headers {}", payload, headers);
    }

    @Override
    public void handleException(StompSession session, StompCommand command,
                                StompHeaders headers, byte[] payload, Throwable exception) {
        logger.error("Client error: exception {}, command {}, payload {}, headers {}",
                exception.getMessage(), command, payload, headers);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        logger.error("Client transport error: error {}", exception.getMessage());
    }
}