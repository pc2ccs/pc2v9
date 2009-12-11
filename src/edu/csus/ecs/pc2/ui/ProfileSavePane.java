package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
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

    public static final String CLONE_BUTTON_NAME = "Clone";  //  @jve:decl-index=0:

    public static final String EXPORT_BUTTON_NAME = "Export";  //  @jve:decl-index=0:
    
    public static final String NEW_BUTTON_NAME = "New";  //  @jve:decl-index=0:


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

    private JPasswordField contestPasswordTextField = null;

    private JButton enterNewContestPasswordButton = null;

    private JLabel contestPasswordLabel = null;

    private JCheckBox showPasswordCheckBox = null;

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
        } else if (actionText.equalsIgnoreCase(EXPORT_BUTTON_NAME)) {
            exportProfile();
        } else if (actionText.equalsIgnoreCase(NEW_BUTTON_NAME)) {
            createNewProfile();
        } else {
            showMessage("Unable to take action: " + actionText);
        }
    }

    private void createNewProfile() {
        showMessage("Would have created new  profile");
        // TODO Create New Profile 
//      closeWindow();
    }

    private void exportProfile() {
        showMessage("Would have exported profile");
        // TODO Export Profile 
//      closeWindow();
    }

    private void cloneProfile() {
        showMessage("Would have cloned profile");
        // TODO Clone Profile 
//        closeWindow();
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
            contestPasswordLabel = new JLabel();
            contestPasswordLabel.setBounds(new Rectangle(5, 92, 124, 23));
            contestPasswordLabel.setText("Contest Password");
            contestPasswordLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            contestTitleLabel = new JLabel();
            contestTitleLabel.setBounds(new Rectangle(21, 51, 108, 23));
            contestTitleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            contestTitleLabel.setText("Contest Title");
            profileNameLabel = new JLabel();
            profileNameLabel.setBounds(new Rectangle(21, 17, 112, 23));
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
            centerPane.add(getContestPasswordTextField(), null);
            centerPane.add(getEnterNewContestPasswordButton(), null);
            centerPane.add(contestPasswordLabel, null);
            centerPane.add(getShowPasswordCheckBox(), null);
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
            resetContestTimeCheckBox.setBounds(new Rectangle(126, 291, 331, 21));
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
            profileNameTextField.setBounds(new Rectangle(150, 16, 273, 25));
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
            contestTitleTextField.setBounds(new Rectangle(150, 50, 273, 25));
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
            removeAllSubmissionsCheckBox.setBounds(new Rectangle(126, 253, 331, 21));
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
            removeAllLanguageCheckBox.setBounds(new Rectangle(126, 176, 331, 21));
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
            removeAllProblemsCheckBox.setBounds(new Rectangle(126, 214, 331, 21));
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

    /**
     * This method initializes contestPasswordTextField
     * 
     * @return javax.swing.JTextField
     */
    private JPasswordField getContestPasswordTextField() {
        if (contestPasswordTextField == null) {
            contestPasswordTextField = new JPasswordField();
            contestPasswordTextField.setBounds(new Rectangle(150, 91, 167, 24));
        }
        return contestPasswordTextField;
    }

    /**
     * This method initializes enterNewContestPasswordButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getEnterNewContestPasswordButton() {
        if (enterNewContestPasswordButton == null) {
            enterNewContestPasswordButton = new JButton();
            enterNewContestPasswordButton.setBounds(new Rectangle(150, 130, 247, 27));
            enterNewContestPasswordButton.setText("Enter New Contest Password");
        }
        return enterNewContestPasswordButton;
    }

    /**
     * This method initializes showPasswordCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getShowPasswordCheckBox() {
        if (showPasswordCheckBox == null) {
            showPasswordCheckBox = new JCheckBox();
            showPasswordCheckBox.setBounds(new Rectangle(333, 92, 139, 23));
            showPasswordCheckBox.setToolTipText("Show password ");
            showPasswordCheckBox.setSelected(true);
            showPasswordCheckBox.setText("Hide text");
            showPasswordCheckBox.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    changeEchoChar (showPasswordCheckBox.isSelected());
                }
            });
        }
        return showPasswordCheckBox;
    }

    protected void changeEchoChar(boolean hideText) {
        if (hideText){
            getContestPasswordTextField().setEchoChar('*');
        } else {
            getContestPasswordTextField().setEchoChar((char) 0);
        }
    }
    
} // @jve:decl-index=0:visual-constraint="10,10"
