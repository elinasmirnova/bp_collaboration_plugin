package org.freeplane.plugin.collaboration.dialogs;

import org.freeplane.core.ui.components.UITools;
import org.freeplane.features.map.IMapLifeCycleListener;
import org.freeplane.features.map.MapModel;
import org.freeplane.features.mode.Controller;
import org.freeplane.features.ui.IMapViewChangeListener;
import org.freeplane.n3.nanoxml.XMLException;
import org.freeplane.plugin.collaboration.ConnectionService;
import org.freeplane.plugin.collaboration.model.ServerMindMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class OpenMindMapDialog extends JDialog {

    private final JList<String> list;
    private final ConnectionService connectionService = ConnectionService.getInstance();

    private class OpenAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            String mindmapId = getSelectedMindmap().substring(getSelectedMindmap().indexOf("(") + 1, getSelectedMindmap().indexOf(")"));
            ServerMindMap serverMindmap = null;
            try {
                serverMindmap = connectionService.getMindmap(mindmapId);
                ConnectionService.getInstance().openMapFromServer(new ByteArrayInputStream(serverMindmap.getBytes()), serverMindmap.isReadOnly());
            } catch (XMLException | IOException ex) {
                JOptionPane.showMessageDialog(UITools.getCurrentFrame(), "Error while fetching mind map, please try again", "Error", JOptionPane.ERROR_MESSAGE);
            }

            setVisible(false);

            if (serverMindmap != null && !serverMindmap.isReadOnly()) {
                try {
                    connectionService.openMindMapWebsockets(serverMindmap);
                } catch (ExecutionException | InterruptedException ex) {
                    JOptionPane.showMessageDialog(UITools.getCurrentFrame(), "Error while establishing connection, please try again", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public OpenMindMapDialog(Frame owner, ListModel<String> mindmaps) {
        super(owner, "Mindmaps", true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.list = new JList<>(mindmaps);
        this.setContentPane(getJContentPane());
        setPreferredSize(new Dimension(300, 200));
        pack();
        setLocationRelativeTo(owner);
    }

    private Container getJContentPane() {
        final Container content = getContentPane();
        content.setLayout(new GridBagLayout());
        content.add(new JScrollPane(list), new GridBagConstraints(1, 1, 3, 1, 1.0, 1.0, GridBagConstraints.NORTH,
                GridBagConstraints.BOTH, new Insets(8, 8, 8, 8), 0, 0));
        JButton jButton = new JButton("Open");
        content.add(jButton, new GridBagConstraints(1, 2, 3, 1, 1.0, 0.0, GridBagConstraints.NORTH,
                GridBagConstraints.BOTH, new Insets(0, 8, 8, 8), 0, 0));
        final OpenMindMapDialog.OpenAction openAction = new OpenMindMapDialog.OpenAction();
        jButton.addActionListener(openAction);
        return content;
    }

    public String getSelectedMindmap() {
        return list.getSelectedValue();
    }
}
