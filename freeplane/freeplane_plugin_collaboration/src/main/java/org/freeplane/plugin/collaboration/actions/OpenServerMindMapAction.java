package org.freeplane.plugin.collaboration.actions;

import org.freeplane.core.ui.AFreeplaneAction;
import org.freeplane.core.ui.EnabledAction;
import org.freeplane.core.ui.components.UITools;
import org.freeplane.plugin.collaboration.ConnectionService;
import org.freeplane.plugin.collaboration.dialogs.OpenMindMapDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;

/**
 * The action to open a mind map from the server.
 */
@EnabledAction(checkOnPopup = true)
public class OpenServerMindMapAction extends AFreeplaneAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenServerMindMapAction.class);

    private static final String ACTION_IDENTIFIER = "OpenServerMindMapAction";
    private final ConnectionService connectionService;

    public OpenServerMindMapAction() {
        super(ACTION_IDENTIFIER);
        connectionService = ConnectionService.getInstance();
    }

    /**
     * Handles action
     *
     * @param e action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        List<String> mindmaps = null;
        try {
            mindmaps = this.connectionService.getMindmaps();
        } catch (IOException ex) {
            LOGGER.error("Error while mind maps fetching");
            JOptionPane.showMessageDialog(UITools.getCurrentFrame(), "Error while mind maps fetching, please try again", "Error", JOptionPane.ERROR_MESSAGE);
        }

        final DefaultListModel<String> data = new DefaultListModel<>();
        if (mindmaps != null) {
            mindmaps.forEach(data::addElement);
        }
        OpenMindMapDialog openMindmapDialog = new OpenMindMapDialog(UITools.getCurrentFrame(), data);
        openMindmapDialog.setVisible(true);
    }

    @Override
    protected void setEnabled() {
        super.setEnabled(ConnectionService.isLogged());
    }
}
