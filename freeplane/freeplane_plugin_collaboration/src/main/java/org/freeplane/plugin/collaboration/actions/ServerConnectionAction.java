package org.freeplane.plugin.collaboration.actions;

import org.freeplane.core.ui.AFreeplaneAction;
import org.freeplane.core.ui.components.UITools;
import org.freeplane.plugin.collaboration.ConnectionService;
import org.freeplane.plugin.collaboration.dialogs.LoginDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

/**
 * The action to connect to the server.
 */
public class ServerConnectionAction extends AFreeplaneAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenServerMindMapAction.class);
    private static final String ACTION_IDENTIFIER = "ServerConnectionAction";

    private final ConnectionService connectionService;

    public ServerConnectionAction() {
        super(ACTION_IDENTIFIER);
        connectionService = ConnectionService.getInstance();
    }

    /**
     * On action performs user authentication.
     *
     * @param e action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        final LoginDialog loginDialog = new LoginDialog(UITools.getCurrentFrame());
        loginDialog.setVisible(true);
        loginDialog.setModal(true);

        try {
            if (this.connectionService.userAuthentication(loginDialog.getEmail().toString(), loginDialog.getPassword().toString())) {
                setEnabled(false);
            }
        } catch (IOException ex) {
            LOGGER.error("Server connection is failed", ex);
            JOptionPane.showMessageDialog(UITools.getCurrentFrame(), "Server connection is failed, please try again", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    protected void setEnabled() {
        super.setEnabled(!ConnectionService.isLogged());
    }
}
