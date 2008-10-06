package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.FlowLayout;

/**
 * Save/export profile settings.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProfileSavePane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -6767667140640583963L;

    public static final String CLONE_BUTTON_NAME = "Clone";

    public static final String EXPORT_BUTTON_NAME = "Export";

    private JPanel buttonPanel = null;

    private JButton saveButton = null;

    private JButton cancelButton = null;

    /**
     * This method initializes
     * 
     */
    public ProfileSavePane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(488, 391));
        this.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);

    }

    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(45);
            flowLayout.setVgap(5);
            buttonPanel = new JPanel();
            buttonPanel.setLayout(flowLayout);
            buttonPanel.add(getSaveButton(), null);
            buttonPanel.add(getCancelButton(), null);
        }
        return buttonPanel;
    }

    /**
     * This method initializes saveButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getSaveButton() {
        if (saveButton == null) {
            saveButton = new JButton();
            saveButton.setText("Clone");
            saveButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    takeProfileAction(saveButton.getText());
                }
            });
        }
        return saveButton;
    }
    
    public void setSaveButtonName (String name){
        getSaveButton().setText(name);
    }

    protected void takeProfileAction(String actionText) {

        if (actionText.equalsIgnoreCase(CLONE_BUTTON_NAME)) {
            cloneProfile();
            showMessage("Would have " + actionText);
        } else if (actionText.equalsIgnoreCase(EXPORT_BUTTON_NAME)) {
            exportProfile();
        } else {
            showMessage("Unable to take action: " + actionText);
        }

        // TODO 
        
//        closeWindow();
    }

    private void exportProfile() {
        showMessage("Would have exported profile");
    }

    private void cloneProfile() {
        showMessage("Would have cloned profile");
        
    }

    private void showMessage(String string) {
        JOptionPane.showMessageDialog(this, string);
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
            cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    closeWindow();
                }
            });
        }
        return cancelButton;
    }

    protected void closeWindow() {
        getParentFrame().setVisible(false);
    }

    @Override
    public String getPluginTitle() {
        return "ProfileSavePane";
    }

} // @jve:decl-index=0:visual-constraint="10,10"
