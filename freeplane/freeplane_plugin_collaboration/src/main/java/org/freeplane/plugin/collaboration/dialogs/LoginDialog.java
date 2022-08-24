package org.freeplane.plugin.collaboration.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginDialog extends JDialog {

    private javax.swing.JPanel jContentPane = null;
    private JPasswordField jPasswordField = null;
    private JTextField emailTextField = null;
    private StringBuilder password = null;
    private StringBuilder email = null;
    private JButton jCancelButton = null;
    private JButton loginJButton = null;

    public LoginDialog(Frame owner) {
        super(owner, "Login", true);
        this.setContentPane(getJContentPane());
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent we) {
                cancelPressed();
            }
        });
        pack();
        setLocationRelativeTo(owner);
    }

    private javax.swing.JPanel getJContentPane() {
        if (jContentPane == null) {
            JLabel mailJLabel = new JLabel();
            JLabel emailJLabel = new JLabel();
            JLabel passwordJLabel = new JLabel();
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
            emailJLabel.setText("E-mail");
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = 2;
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            passwordJLabel.setText("Password");
            gridBagConstraints3.gridx = 1;
            gridBagConstraints3.gridy = 1;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.weighty = 5.0;
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
            mailJLabel.setText("Please log in to connect to the Freeplane server");
            gridBagConstraints6.gridx = 0;
            gridBagConstraints6.gridy = 3;
            gridBagConstraints6.insets = new java.awt.Insets(20, 0, 0, 0);
            gridBagConstraints7.gridx = 1;
            gridBagConstraints7.gridy = 3;
            gridBagConstraints7.insets = new java.awt.Insets(20, 0, 0, 0);
            jContentPane.add(getEmailTextField(), gridBagConstraints3);
            jContentPane.add(emailJLabel, gridBagConstraints1);
            jContentPane.add(passwordJLabel, gridBagConstraints2);
            jContentPane.add(getJPasswordField(), gridBagConstraints4);
            jContentPane.add(mailJLabel, gridBagConstraints5);
            jContentPane.add(getJOKButton(), gridBagConstraints6);
            jContentPane.add(getJCancelButton(), gridBagConstraints7);
            getRootPane().setDefaultButton(getJOKButton());
        }
        return jContentPane;
    }

    private JTextField getEmailTextField() {
        if (emailTextField == null) {
            emailTextField = new JTextField();
        }
        return emailTextField;
    }

    private JButton getJOKButton() {
        if (loginJButton == null) {
            loginJButton = new JButton();
            loginJButton.setAction(new AbstractAction() {
                /**
                 *
                 */
                private static final long serialVersionUID = 1L;

                public void actionPerformed(final ActionEvent e) {
                    okPressed();
                }
            });
            loginJButton.setText("Login");
        }
        return loginJButton;
    }

    private JButton getJCancelButton() {
        if (jCancelButton == null) {
            jCancelButton = new JButton();
            jCancelButton.setAction(new AbstractAction() {
                /**
                 *
                 */
                private static final long serialVersionUID = 1L;

                public void actionPerformed(final ActionEvent e) {
                    cancelPressed();
                }
            });
            jCancelButton.setText("Cancel");
        }
        return jCancelButton;
    }

    private void cancelPressed() {
        password = null;
        close();
    }

    private void close() {
        this.dispose();
    }

    private void okPressed() {
        if (!checkEmailAndPasswordFilled()) {
            JOptionPane.showMessageDialog(this, "Please fill in e-mail and password");
            return;
        }
        password = new StringBuilder();
        password.append(jPasswordField.getPassword());

        email = new StringBuilder();
        email.append(emailTextField.getText());

        close();
    }

    private boolean checkEmailAndPasswordFilled() {
        return emailTextField.getText() != null && jPasswordField.getPassword() != null;
    }

    private JPasswordField getJPasswordField() {
        if (jPasswordField == null) {
            jPasswordField = new JPasswordField(20);
        }
        return jPasswordField;
    }

    public StringBuilder getPassword() {
        return password;
    }

    public StringBuilder getEmail() {
        return email;
    }
}
