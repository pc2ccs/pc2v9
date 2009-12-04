package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.Profile;

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

    public static final String EXPORT_BUTTON_NAME = "Export";  //  @jve:decl-index=0:
    
    public static final String NEW_BUTTON_NAME = "New";


    private JPanel buttonPanel = null;

    private JButton saveButton = null;

    private JButton cancelButton = null;

    private JPanel centerPane = null;

    private JCheckBox resetContestTimeCheckBox = null;

    private JLabel profileNameLabel = null;

    private JLabel contestTitleLabel = null;

    private JTextField profileNameTextField = null;

    private JTextField contestTitleTextField = null;

    private JCheckBox removeAllSubmissionsCheckBox = null;

    private JCheckBox removeAllLanguageCheckBox = null;

    private JCheckBox removeAllProblemsCheckBox = null;

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

        this.add(getCenterPane(), BorderLayout.CENTER);
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

    public void setSaveButtonName(String name) {
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

        // closeWindow();
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

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            contestTitleLabel = new JLabel();
            contestTitleLabel.setBounds(new Rectangle(21, 54, 108, 16));
            contestTitleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            contestTitleLabel.setText("Contest Title");
            profileNameLabel = new JLabel();
            profileNameLabel.setBounds(new Rectangle(21, 20, 112, 16));
            profileNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            profileNameLabel.setText("Profile Name");
            centerPane = new JPanel();
            centerPane.setLayout(null);
            centerPane.add(getResetContestTimeCheckBox(), null);
            centerPane.add(profileNameLabel, null);
            centerPane.add(contestTitleLabel, null);
            centerPane.add(getProfileNameTextField(), null);
            centerPane.add(getContestTitleTextField(), null);
            centerPane.add(getRemoveAllSubmissionsCheckBox(), null);
            centerPane.add(getRemoveAllLanguageCheckBox(), null);
            centerPane.add(getRemoveAllProblemsCheckBox(), null);
        }
        return centerPane;
    }

    /**
     * This method initializes resetContestTimeCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getResetContestTimeCheckBox() {
        if (resetContestTimeCheckBox == null) {
            resetContestTimeCheckBox = new JCheckBox();
            resetContestTimeCheckBox.setBounds(new Rectangle(126, 216, 331, 21));
            resetContestTimeCheckBox.setToolTipText("Set elapsed time to zero");
            resetContestTimeCheckBox.setSelected(true);
            resetContestTimeCheckBox.setText("Reset Contest Times");
        }
        return resetContestTimeCheckBox;
    }

    /**
     * This method initializes profileNameTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getProfileNameTextField() {
        if (profileNameTextField == null) {
            profileNameTextField = new JTextField();
            profileNameTextField.setBounds(new Rectangle(148, 16, 273, 25));
        }
        return profileNameTextField;
    }

    /**
     * This method initializes contestTitleTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getContestTitleTextField() {
        if (contestTitleTextField == null) {
            contestTitleTextField = new JTextField();
            contestTitleTextField.setBounds(new Rectangle(149, 50, 273, 25));
        }
        return contestTitleTextField;
    }

    /**
     * This method initializes removeAllSubmissionsCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getRemoveAllSubmissionsCheckBox() {
        if (removeAllSubmissionsCheckBox == null) {
            removeAllSubmissionsCheckBox = new JCheckBox();
            removeAllSubmissionsCheckBox.setBounds(new Rectangle(126, 178, 331, 21));
            removeAllSubmissionsCheckBox.setSelected(true);
            removeAllSubmissionsCheckBox.setText("Remove ALL submissions (runs and clarifications)");
        }
        return removeAllSubmissionsCheckBox;
    }

    /**
     * This method initializes removeAllLanguageCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getRemoveAllLanguageCheckBox() {
        if (removeAllLanguageCheckBox == null) {
            removeAllLanguageCheckBox = new JCheckBox();
            removeAllLanguageCheckBox.setBounds(new Rectangle(126, 101, 331, 21));
            removeAllLanguageCheckBox.setText("Remove ALL languages");
        }
        return removeAllLanguageCheckBox;
    }

    /**
     * This method initializes removeAllProblemsCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getRemoveAllProblemsCheckBox() {
        if (removeAllProblemsCheckBox == null) {
            removeAllProblemsCheckBox = new JCheckBox();
            removeAllProblemsCheckBox.setBounds(new Rectangle(126, 139, 331, 21));
            removeAllProblemsCheckBox.setSelected(true);
            removeAllProblemsCheckBox.setText("Remove ALL problems");
        }
        return removeAllProblemsCheckBox;
    }

    public void populateGUI(Profile profile) {
        getProfileNameTextField().setText(profile.getName());
        ContestInformation contestInformation = getContest().getContestInformation();
        String title = contestInformation.getContestTitle();
        if (title == null){
            title = "";
        }
        getContestTitleTextField().setText(title);
    }

    public void populateGUI() {
        getProfileNameTextField().setText("");
    }
    
} // @jve:decl-index=0:visual-constraint="10,10"
