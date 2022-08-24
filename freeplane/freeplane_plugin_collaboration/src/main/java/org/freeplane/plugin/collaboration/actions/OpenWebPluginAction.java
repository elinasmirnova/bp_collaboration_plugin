package org.freeplane.plugin.collaboration.actions;

import org.freeplane.features.help.OpenURLAction;

import java.awt.event.ActionEvent;

/**
 * The action to open a mind maps management website.
 */
public class OpenWebPluginAction extends OpenURLAction {

    private static final String ACTION_IDENTIFIER = "OpenWebPluginAction";

    public OpenWebPluginAction() {
        super(ACTION_IDENTIFIER, "http://localhost:3000/home");
    }

    /**
     * Handles action
     *
     * @param e action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
    }
}
