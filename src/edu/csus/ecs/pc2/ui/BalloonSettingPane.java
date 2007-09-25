package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Panel;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Site;

/**
 * Add/Edit BalloonSettings Pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class BalloonSettingPane extends JPanePlugin {

    public final String DEFAULT_INTERNATIONAL_VALIDATOR_COMMAND = "{:validator} {:infile} {:outfile} {:ansfile} {:resfile} ";

    private static final long serialVersionUID = -1060536964672397704L;

    private Site noneSelected = new Site("None Selected", 0);

    private JPanel messagePane = null;

    private JPanel buttonPane = null;

    private JButton addButton = null;

    private JButton updateButton = null;

    private JButton cancelButton = null;

    private JLabel messageLabel = null;

    /**
     * The input balloonSettings.
     */
    private BalloonSettings balloonSettings = null;

    @SuppressWarnings("unused")
    private Log log = null;

    @SuppressWarnings("unused")
    private boolean populatingGUI = true;

    private Panel centerPane = null;

    private JCheckBox sendEmailNotificationsCheckBox = null;

    private JCheckBox printNotificationsCheckBox = null;

    private JLabel mailContactLabel = null;

    private JLabel jLabel = null;

    private JTextField emailContactTextBox = null;

    private JTextField emailServerTextBox = null;

    private JCheckBox postScriptEnabledCheckBox = null;

    private JTextField printDeviceTextBox = null;

    private JLabel printDeviceLabel = null;

    private JComboBox siteComboBox = null;

    private JLabel siteLabel = null;

    private MCLB colorListBox = null;

    /**
     * This method initializes
     * 
     */
    public BalloonSettingPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(610, 306));

        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
        this.add(getCenterPane(), java.awt.BorderLayout.CENTER);
    }

    public void setContestAndController(IContest inContest, IController inController) {
        super.setContestAndController(inContest, inController);

        // getContest().addBalloonSettingsListener(new Proble)

        log = getController().getLog();
    }

    public String getPluginTitle() {
        return "Edit BalloonSettings Pane";
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
            messagePane.setPreferredSize(new java.awt.Dimension(25, 25));
            messagePane.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return messagePane;
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(15);
            buttonPane = new JPanel();
            buttonPane.setLayout(flowLayout);
            buttonPane.add(getAddButton(), null);
            buttonPane.add(getUpdateButton(), null);
            buttonPane.add(getCancelButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes addButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddButton() {
        if (addButton == null) {
            addButton = new JButton();
            addButton.setText("Add");
            addButton.setMnemonic(java.awt.event.KeyEvent.VK_A);
            addButton.setEnabled(false);
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addBalloonSettings();
                }
            });
        }
        return addButton;
    }

    /**
     * Add BalloonSettings to the fields.
     * 
     */
    protected void addBalloonSettings() {

        BalloonSettings newBalloonSettings = null;
        try {
            newBalloonSettings = getBalloonSettingsFromFields(null);
        } catch (InvalidFieldValue e) {
            showMessage(e.getMessage());
            return;
        }

        getController().addNewBalloonSettings(newBalloonSettings);

        cancelButton.setText("Close");
        addButton.setEnabled(false);
        updateButton.setEnabled(false);

        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }
    }

    /**
     * Enable or disable Update button based on comparison of run to fields.
     * 
     */
    public void enableUpdateButton() {

        if (populatingGUI) {
            return;
        }

        boolean enableButton = false;

        if (balloonSettings != null) {

            try {
                BalloonSettings changedBalloonSettings = getBalloonSettingsFromFields(null);

                // TODO
                 if (!balloonSettings.isSameAs(changedBalloonSettings)) {
                     enableButton = true;
                 }

            } catch (InvalidFieldValue e) {
                // invalid field, but that is ok as they are entering data
                // will be caught and reported when they hit update or add.
                StaticLog.getLog().log(Log.DEBUG, "Input BalloonSettings (but not saving) ", e);
                enableButton = true;
            }

        } else {
            if (getAddButton().isVisible()) {
                enableButton = true;
            }
        }

//         TODO enable
         enableUpdateButtons(enableButton);

    }

    protected void enableUpdateButtons(boolean fieldsChanged) {
        if (fieldsChanged) {
            cancelButton.setText("Cancel");
        } else {
            cancelButton.setText("Close");
        }
        updateButton.setEnabled(fieldsChanged);
        addButton.setEnabled(fieldsChanged);
    }

    /**
     * Create a BalloonSettings from the fields.
     * 
     * @param checkBalloonSettings
     * @return
     * @throws InvalidFieldValue
     */
    public BalloonSettings getBalloonSettingsFromFields(BalloonSettings checkBalloonSettings) throws InvalidFieldValue {
        
        BalloonSettings newBalloonSettings = new BalloonSettings("New", 0);
        
        newBalloonSettings.setPrintBalloons(getPrintNotificationsCheckBox().isSelected());
        newBalloonSettings.setEmailBalloons(getSendEmailNotificationsCheckBox().isSelected());
        
        // TODO get and add colors
        // TODO get and add text fields

        return newBalloonSettings;
    }

    /**
     * This method initializes updateButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getUpdateButton() {
        if (updateButton == null) {
            updateButton = new JButton();
            updateButton.setText("Update");
            updateButton.setEnabled(false);
            updateButton.setMnemonic(java.awt.event.KeyEvent.VK_U);
            updateButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    updateBalloonSettings();
                }
            });
        }
        return updateButton;
    }

    protected void updateBalloonSettings() {

        if (!validateBalloonSettingsFields()) {
            // new balloonSettings is invalid, just return, message issued by validateBalloonSettingsFields
            return;
        }

        BalloonSettings newBalloonSettings = null;

        try {
            newBalloonSettings = getBalloonSettingsFromFields(balloonSettings);
        } catch (InvalidFieldValue e) {
            showMessage(e.getMessage());
            return;
        }

        getController().updateBalloonSettings(newBalloonSettings);

        cancelButton.setText("Close");
        addButton.setEnabled(false);
        updateButton.setEnabled(false);

        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }
    }

    /**
     * Validate that all balloonSettings fields are ok.
     * 
     * @return
     */
    private boolean validateBalloonSettingsFields() {

        return false; // TODO

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
            cancelButton.setMnemonic(java.awt.event.KeyEvent.VK_C);
            cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleCancelButton();
                }
            });
        }
        return cancelButton;
    }

    protected void handleCancelButton() {

        if (getAddButton().isEnabled() || getUpdateButton().isEnabled()) {

            // Something changed, are they sure ?

            int result = FrameUtilities.yesNoCancelDialog("BalloonSettings modified, save changes?", "Confirm Choice");

            if (result == JOptionPane.YES_OPTION) {
                if (getAddButton().isEnabled()) {
                    addBalloonSettings();
                } else {
                    updateBalloonSettings();
                }
                if (getParentFrame() != null) {
                    getParentFrame().setVisible(false);
                }
            } else if (result == JOptionPane.NO_OPTION) {
                if (getParentFrame() != null) {
                    getParentFrame().setVisible(false);
                }
            }

        } else {
            if (getParentFrame() != null) {
                getParentFrame().setVisible(false);
            }
        }
    }

    public BalloonSettings getBalloonSettings() {
        return balloonSettings;
    }

    public void setBalloonSettings(final BalloonSettings balloonSettings) {

        this.balloonSettings = balloonSettings;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI(balloonSettings);
                // enableUpdateButtons(false);
                showMessage("");
            }
        });
    }

    private void populateGUI(BalloonSettings inBalloonSettings) {

        // TODO set site number

        populateSiteJCombo();

        if (inBalloonSettings == null) {
            getSendEmailNotificationsCheckBox().setSelected(false);
            getPrintNotificationsCheckBox().setSelected(false);

            getEmailContactTextBox().setText("");
            getPrintDeviceTextBox().setText("");
            getPostScriptEnabledCheckBox().setSelected(false);

            clearBallonColors();

        } else {

            getSendEmailNotificationsCheckBox().setSelected(inBalloonSettings.isEmailBalloons());
            getPrintNotificationsCheckBox().setSelected(inBalloonSettings.isPrintBalloons());

            getEmailContactTextBox().setText(inBalloonSettings.getEmailContact());
            getPrintDeviceTextBox().setText(inBalloonSettings.getPrintDevice());
            getPostScriptEnabledCheckBox().setSelected(inBalloonSettings.isPostscriptCapable());
        }

    }

    private void populateSiteJCombo() {

        siteComboBox.removeAllItems();

        siteComboBox.addItem(noneSelected);

        for (Site site : getContest().getSites()) {
            siteComboBox.addItem(site);
        }

    }

    private void clearBallonColors() {

        colorListBox.removeAllRows();

        for (Problem problem : getContest().getProblems()) {

            Object[] row = new Object[2];
            row[0] = problem.getDisplayName();
            row[1] = new JTextField("");
            colorListBox.addRow(row);

        }
        colorListBox.autoSizeAllColumns();
    }

    public void showMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(message);
            }
        });
    }

    /**
     * This method initializes centerPane
     * 
     * @return java.awt.Panel
     */
    private Panel getCenterPane() {
        if (centerPane == null) {
            siteLabel = new JLabel();
            siteLabel.setBounds(new java.awt.Rectangle(25, 22, 48, 16));
            siteLabel.setText("Site");
            printDeviceLabel = new JLabel();
            printDeviceLabel.setBounds(new java.awt.Rectangle(53, 177, 112, 21));
            printDeviceLabel.setText("Print Device");
            jLabel = new JLabel();
            jLabel.setBounds(new java.awt.Rectangle(53, 115, 112, 21));
            jLabel.setText("EMail Server");
            mailContactLabel = new JLabel();
            mailContactLabel.setBounds(new java.awt.Rectangle(52, 80, 112, 21));
            mailContactLabel.setText("EMail Contact");
            centerPane = new Panel();
            centerPane.setLayout(null);
            centerPane.add(getSendEmailNotificationsCheckBox(), null);
            centerPane.add(getPrintNotificationsCheckBox(), null);
            centerPane.add(mailContactLabel, null);
            centerPane.add(jLabel, null);
            centerPane.add(getEmailContactTextBox(), null);
            centerPane.add(getEmailServerTextBox(), null);
            centerPane.add(getPostScriptEnabledCheckBox(), null);
            centerPane.add(getPrintDeviceTextBox(), null);
            centerPane.add(printDeviceLabel, null);
            centerPane.add(getSiteComboBox(), null);
            centerPane.add(siteLabel, null);
            centerPane.add(getColorListBox(), null);
        }
        return centerPane;
    }

    /**
     * This method initializes jCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getSendEmailNotificationsCheckBox() {
        if (sendEmailNotificationsCheckBox == null) {
            sendEmailNotificationsCheckBox = new JCheckBox();
            sendEmailNotificationsCheckBox.setBounds(new java.awt.Rectangle(25, 48, 185, 21));
            sendEmailNotificationsCheckBox.setText("Send Email Notifications");
            sendEmailNotificationsCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableButtons();
                }
            });
        }
        return sendEmailNotificationsCheckBox;
    }

    protected void enableButtons() {
        
        if (populatingGUI) {
            return;
        }

        boolean enableButton = false;

        if (balloonSettings != null) {

            try {
                BalloonSettings changedBalloonSettings = getBalloonSettingsFromFields(null);
                if (!balloonSettings.isSameAs(changedBalloonSettings)) {
                    enableButton = true;
                }
                
            } catch (InvalidFieldValue e) {
                // invalid field, but that is ok as they are entering data
                // will be caught and reported when they hit update or add.
                StaticLog.getLog().log(Log.DEBUG, "Input Balloon Setting (but not saving) ",e);
                enableButton = true;
            }

        } else {
            if (getAddButton().isVisible()) {
                enableButton = true;
            }
        }

        enableUpdateButtons(enableButton);
    }

    /**
     * This method initializes jCheckBox1
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getPrintNotificationsCheckBox() {
        if (printNotificationsCheckBox == null) {
            printNotificationsCheckBox = new JCheckBox();
            printNotificationsCheckBox.setBounds(new java.awt.Rectangle(25, 147, 162, 24));
            printNotificationsCheckBox.setText("Print Notifications");
            printNotificationsCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableButtons();
                }
            });
        }
        return printNotificationsCheckBox;
    }

    /**
     * This method initializes emailContactTextBox
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getEmailContactTextBox() {
        if (emailContactTextBox == null) {
            emailContactTextBox = new JTextField();
            emailContactTextBox.setBounds(new java.awt.Rectangle(183, 80, 179, 21));
            emailContactTextBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableButtons();
                }
            });
        }
        return emailContactTextBox;
    }

    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getEmailServerTextBox() {
        if (emailServerTextBox == null) {
            emailServerTextBox = new JTextField();
            emailServerTextBox.setBounds(new java.awt.Rectangle(183, 115, 179, 21));
            emailServerTextBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableButtons();
                }
            });
        }
        return emailServerTextBox;
    }

    /**
     * This method initializes postScriptEnabledCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getPostScriptEnabledCheckBox() {
        if (postScriptEnabledCheckBox == null) {
            postScriptEnabledCheckBox = new JCheckBox();
            postScriptEnabledCheckBox.setBounds(new java.awt.Rectangle(52, 205, 212, 24));
            postScriptEnabledCheckBox.setText("PostScript enabled printer");
            postScriptEnabledCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableButtons();
                }
            });
        }
        return postScriptEnabledCheckBox;
    }

    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getPrintDeviceTextBox() {
        if (printDeviceTextBox == null) {
            printDeviceTextBox = new JTextField();
            printDeviceTextBox.setBounds(new java.awt.Rectangle(183, 177, 179, 21));
            printDeviceTextBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableButtons();
                }
            });
        }
        return printDeviceTextBox;
    }

    /**
     * This method initializes siteComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getSiteComboBox() {
        if (siteComboBox == null) {
            siteComboBox = new JComboBox();
            siteComboBox.setBounds(new java.awt.Rectangle(91, 18, 265, 24));
            siteComboBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableButtons();
                }
            });
        }
        return siteComboBox;
    }

    /**
     * This method initializes colorListBox
     * 
     * @return com.ibm.webrunner.j2mclb.MCLB
     */
    private MCLB getColorListBox() {
        if (colorListBox == null) {
            colorListBox = new MCLB();
            colorListBox.setBounds(new java.awt.Rectangle(379, 15, 208, 210));
            String cols[] = { "Problem", "Color" };
            colorListBox.addColumns(cols);
            colorListBox.autoSizeAllColumns();
        }

        return colorListBox;
    }

} // @jve:decl-index=0:visual-constraint="28,22"
