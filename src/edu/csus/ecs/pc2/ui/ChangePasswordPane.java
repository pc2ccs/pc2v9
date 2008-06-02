/**
 * 
 */
package edu.csus.ecs.pc2.ui;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPasswordField;

/**
 * @author pc2@ecs.csus.edu
 * @version $Id$
 * 
 */

public class ChangePasswordPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 8687088072866264762L;

    private JPanel buttonPane = null;

    private JButton cancelButton = null;

    private JPanel mainPane = null;

    private JPanel messagePane = null;

    private JLabel messageLabel = null;

    private JLabel currentPasswordLabel = null;

    private JPasswordField currentPasswordField = null;

    private JLabel newPasswordLabel = null;

    private JPasswordField newPasswordField = null;

    private JLabel confirmPasswordLabel = null;

    private JPasswordField confirmPasswordField = null;

    private JPanel eastPanel = null;

    private JButton okButton = null;

    /**
     * 
     */
    public ChangePasswordPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(595, 240));
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
        this.add(getMainPane(), java.awt.BorderLayout.CENTER);
        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getEastPanel(), java.awt.BorderLayout.EAST);
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.csus.ecs.pc2.ui.JPanePlugin#getPluginTitle()
     */
    @Override
    public String getPluginTitle() {
        return "Change Password UI";
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.csus.ecs.pc2.ui.JPanePlugin#setContestAndController(edu.csus.ecs.pc2.core.model.IInternalContest, edu.csus.ecs.pc2.core.IInternalController)
     */
    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
    }

    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(50);
            buttonPane = new JPanel();
            buttonPane.setPreferredSize(new java.awt.Dimension(35, 35));
            buttonPane.setLayout(flowLayout);
            buttonPane.add(getOkButton(), null);
            buttonPane.add(getCancelButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes cancelButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton();
            cancelButton.setText("Cancel");
            cancelButton.setPreferredSize(new java.awt.Dimension(100, 26));
            cancelButton.setMnemonic(java.awt.event.KeyEvent.VK_C);
            cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleCancelButton();
                }
            });
        }
        return cancelButton;
    }

    private void handleCancelButton() {
        clear();
        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }
    }

    /**
     * This method initializes mainPane
     * 
     * @return javax.swing.JSplitPane
     */
    private JPanel getMainPane() {
        if (mainPane == null) {
            GridLayout gridLayout2 = new GridLayout();
            gridLayout2.setRows(3);
            gridLayout2.setHgap(10);
            gridLayout2.setVgap(15);
            gridLayout2.setColumns(2);
            GridLayout gridLayout1 = new GridLayout();
            gridLayout1.setRows(3);
            gridLayout1.setVgap(15);
            gridLayout1.setColumns(2);
            gridLayout1.setHgap(5);
            confirmPasswordLabel = new JLabel();
            confirmPasswordLabel.setText("Confirm New Password:");
            confirmPasswordLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
            newPasswordLabel = new JLabel();
            newPasswordLabel.setText("New Password:");
            newPasswordLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
            currentPasswordLabel = new JLabel();
            currentPasswordLabel.setText("Current Password:");
            currentPasswordLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(3);
            gridLayout.setHgap(5);
            gridLayout.setVgap(20);
            gridLayout.setColumns(2);
            mainPane = new JPanel();
            mainPane.setLayout(gridLayout2);
            mainPane.add(currentPasswordLabel, null);
            mainPane.add(getCurrentPasswordField(), null);
            mainPane.add(newPasswordLabel, null);
            mainPane.add(getNewPasswordField(), null);
            mainPane.add(confirmPasswordLabel, null);
            mainPane.add(getConfirmPasswordField(), null);
        }
        return mainPane;
    }

    /**
     * This method initializes messagePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePane() {
        if (messagePane == null) {
            messageLabel = new JLabel();
            messageLabel.setText("");
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messagePane = new JPanel();
            messagePane.setLayout(new BorderLayout());
            messagePane.setPreferredSize(new java.awt.Dimension(30, 30));
            messagePane.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return messagePane;
    }

    /**
     * This method initializes currentPasswordField
     * 
     * @return javax.swing.JPasswordField
     */
    private JPasswordField getCurrentPasswordField() {
        if (currentPasswordField == null) {
            currentPasswordField = new JPasswordField();
            currentPasswordField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableOk();
                }
                public void keyPressed(java.awt.event.KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        newPasswordField.requestFocus();
                    }
                }
            });
        }
        return currentPasswordField;
    }

    protected void enableOk() {
        getOkButton().setEnabled(!(isEmpty(getCurrentPasswordField()) && isEmpty(getNewPasswordField()) && isEmpty(getConfirmPasswordField())));
    }

    private boolean isEmpty(JPasswordField field) {
        return (field.getPassword().length == 0);
    }
    
    /**
     * This method initializes newPasswordField
     * 
     * @return javax.swing.JPasswordField
     */
    private JPasswordField getNewPasswordField() {
        if (newPasswordField == null) {
            newPasswordField = new JPasswordField();
            newPasswordField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableOk();
                }
                public void keyPressed(java.awt.event.KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        confirmPasswordField.requestFocus();
                    }
                }
            });
        }
        return newPasswordField;
    }

    /**
     * This method initializes confirmPasswordField
     * 
     * @return javax.swing.JPasswordField
     */
    private JPasswordField getConfirmPasswordField() {
        if (confirmPasswordField == null) {
            confirmPasswordField = new JPasswordField();
            confirmPasswordField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableOk();
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (getOkButton().isEnabled()){
                            handleOkButton();
                        }
                    }
                }
            });
        }
        return confirmPasswordField;
    }

    /**
     * This method initializes eastPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getEastPanel() {
        if (eastPanel == null) {
            eastPanel = new JPanel();
        }
        return eastPanel;
    }

    public void clear() {
        getCurrentPasswordField().setText("");
        getNewPasswordField().setText("");
        getConfirmPasswordField().setText("");
    }

    /**
     * This method initializes okButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getOkButton() {
        if (okButton == null) {
            okButton = new JButton();
            okButton.setText("Ok");
            okButton.setEnabled(false);
            okButton.setPreferredSize(new java.awt.Dimension(100, 26));
            okButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleOkButton();
                }
            });
        }
        return okButton;
    }

    protected void handleOkButton() {
        // current password will be verified on the server
        if (isEmpty(getCurrentPasswordField())) {
            FrameUtilities.showMessage(getParentFrame(), "Password cannot be set", "You must supply your current password.");
            return;
        }
        if (!Utilities.isEquals(getNewPasswordField().getPassword(), getConfirmPasswordField().getPassword())) {
            FrameUtilities.showMessage(getParentFrame(),"Password cannot be set", "Passwords do not match.");
            return;
        } else {
            if (isEmpty(getNewPasswordField()) || isEmpty(getConfirmPasswordField())) {
                FrameUtilities.showMessage(getParentFrame(),"Password cannot be set", "Passwords cannot be blank.");
                return;
            }
        }
        getController().requestChangePassword(String.valueOf(getCurrentPasswordField().getPassword()), String.valueOf(getNewPasswordField().getPassword()));
        handleCancelButton();
    }

} // @jve:decl-index=0:visual-constraint="10,10"
