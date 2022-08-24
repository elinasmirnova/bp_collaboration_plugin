package org.freeplane.plugin.collaboration.actions;

import org.freeplane.core.ui.AFreeplaneAction;
import org.freeplane.core.ui.EnabledAction;
import org.freeplane.core.ui.components.UITools;
import org.freeplane.core.util.TextUtils;
import org.freeplane.features.mode.Controller;
import org.freeplane.features.mode.mindmapmode.MModeController;
import org.freeplane.plugin.collaboration.ConnectionService;
import org.freeplane.plugin.collaboration.dialogs.SavingMindMapDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;

/**
 * The action to save a local mind map or save already opened mind map from the server.
 */
@EnabledAction(checkOnPopup = true)
public class SaveServerMindMapAction extends AFreeplaneAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenServerMindMapAction.class);

    private static final String ACTION_IDENTIFIER = "SaveServerMindMapAction";
    private final ConnectionService connectionService;

    public SaveServerMindMapAction() {
        super(ACTION_IDENTIFIER);
        connectionService = ConnectionService.getInstance();
    }

    /**
     * On action shows a window for saving mind map on the server.
     *
     * @param e action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (Controller.getCurrentController().getMap().isReadOnly()) {
            JOptionPane.showMessageDialog(Controller.getCurrentController()
                            .getMapViewManager().getMapViewComponent(),
                    TextUtils.getText("SaveAction_readonlyMsg"),
                    TextUtils.getText("SaveAction_readonlyTitle"),
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        final boolean success = ((MModeController) Controller.getCurrentModeController()).save();
        final Controller controller = Controller.getCurrentController();
        if (success) {
            controller.getViewController().out(TextUtils.getText("saved"));
        } else {
            controller.getViewController().out(TextUtils.getText("saving_canceled"));
        }

        byte[] mapToSave = null;
        try {
            mapToSave = Files.readAllBytes(Controller.getCurrentController().getMap().getFile().toPath());
        } catch (IOException ex) {
            LOGGER.error("Error while reading current mind map", ex);
            JOptionPane.showMessageDialog(UITools.getCurrentFrame(), "Saving mind map to the server is failed, please try again", "Error", JOptionPane.ERROR_MESSAGE);
        }

        if (ConnectionService.getCurrentStompSession() == null) {
            SavingMindMapDialog savingMindMapDialog = new SavingMindMapDialog(UITools.getCurrentFrame(), mapToSave);
            savingMindMapDialog.setVisible(true);
        } else {

            try {
                connectionService.updateMindmap(mapToSave, ConnectionService.getMindMap().getMindMapId());
            } catch (IOException ex) {
                LOGGER.error("Error while saving mind map", ex);
                JOptionPane.showMessageDialog(UITools.getCurrentFrame(), "Saving mind map to the server is failed, please try again", "Error", JOptionPane.ERROR_MESSAGE);
            }

            String message = ConnectionService.getMindMap().getMindMapId() + "," + ConnectionService.getEmail();
            ConnectionService.getCurrentStompSession().send("/app/disconnect", message);

            ConnectionService.getCurrentStompSession().disconnect();
            ConnectionService.setCurrentStompSession(null);
            ConnectionService.setMindMap(null);
            Controller.getCurrentController().getMapViewManager().close();
        }
    }

    @Override
    protected void setEnabled() {
        super.setEnabled(ConnectionService.isLogged());
    }
}
