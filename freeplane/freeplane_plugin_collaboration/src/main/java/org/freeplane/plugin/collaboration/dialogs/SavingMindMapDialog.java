package org.freeplane.plugin.collaboration.dialogs;

import org.freeplane.core.ui.components.UITools;
import org.freeplane.plugin.collaboration.ConnectionService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class SavingMindMapDialog extends JDialog {

    private class SaveAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                connectionService.saveMindmap(mindMapToSave, getMindMapTitle(), getIsPublic());
                dispose();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(UITools.getCurrentFrame(), "Error while saving mind map on server, please try again", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private javax.swing.JPanel jContentPane = null;
    private final ConnectionService connectionService = ConnectionService.getInstance();
    private final byte[] mindMapToSave;

    private JTextField titleTextField = null;
    private JCheckBox isPublicCheckBox = null;
    private JButton saveButton = null;

    public SavingMindMapDialog(Frame owner, byte[] mindmapToSave) {
        super(owner, "Save mind map on server", true);
        this.mindMapToSave = mindmapToSave;
        this.setContentPane(getJContentPane());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(300, 150));
        pack();
        setLocationRelativeTo(owner);
    }

    private javax.swing.JPanel getJContentPane() {
        if (jContentPane == null) {
            JLabel mailJLabel = new JLabel();
            JLabel titleLabel = new JLabel();
            JLabel isPublicLabel = new JLabel();
            final GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            final GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            final GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            final GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            final GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            final GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            final GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            jContentPane = new javax.swing.JPanel();
            jContentPane.setLayout(new GridBagLayout());
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = 1;
            gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
            titleLabel.setText("Title");
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = 2;
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            isPublicLabel.setText("Is public");
            gridBagConstraints3.gridx = 1;
            gridBagConstraints3.gridy = 1;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridx = 1;
            gridBagConstraints4.gridy = 2;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.gridy = 0;
            gridBagConstraints5.gridwidth = 2;
            gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.anchor = java.awt.GridBagConstraints.CENTER;
            gridBagConstraints5.insets = new java.awt.Insets(0, 0, 20, 0);
            mailJLabel.setText("");
            gridBagConstraints6.gridx = 0;
            gridBagConstraints6.gridy = 3;
            gridBagConstraints6.insets = new java.awt.Insets(20, 0, 0, 0);
            gridBagConstraints7.gridx = 1;
            gridBagConstraints7.gridy = 3;
            gridBagConstraints7.insets = new java.awt.Insets(20, 0, 0, 0);
            jContentPane.add(getTitleTextField(), gridBagConstraints3);
            jContentPane.add(titleLabel, gridBagConstraints1);
            jContentPane.add(isPublicLabel, gridBagConstraints2);
            jContentPane.add(getIsPublicCheckBox(), gridBagConstraints4);
            jContentPane.add(mailJLabel, gridBagConstraints5);
            jContentPane.add(getSaveButton(), gridBagConstraints6);
            getRootPane().setDefaultButton(getSaveButton());
            final SavingMindMapDialog.SaveAction saveAction = new SavingMindMapDialog.SaveAction();
            getSaveButton().addActionListener(saveAction);
        }
        return jContentPane;
    }

    public JTextField getTitleTextField() {
        if (titleTextField == null) {
            titleTextField = new JTextField();
        }
        return titleTextField;
    }

    public JCheckBox getIsPublicCheckBox() {
        if (isPublicCheckBox == null) {
            isPublicCheckBox = new JCheckBox();
        }
        return isPublicCheckBox;
    }

    public JButton getSaveButton() {
        if (saveButton == null) {
            saveButton = new JButton();
            saveButton.setText("Save");
        }
        return saveButton;
    }

    public String getMindMapTitle() {
        return titleTextField.getText();
    }

    public boolean getIsPublic() {
        return isPublicCheckBox.isSelected();
    }
}
