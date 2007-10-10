package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.list.SiteComparatorBySiteNumber;
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

    private JLabel siteLabel = null;

    private MCLB colorListBox = null;

    private JComboBox siteComboBox = null;

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

        if (!validateBalloonSettingsFields()) {
            // new balloonSettings is invalid, just return, message issued by validateBalloonSettingsFields
            return;
        }

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

        if (checkBalloonSettings == null){
            Site site = (Site)getSiteComboBox().getSelectedItem();
            checkBalloonSettings =  new BalloonSettings(site.getDisplayName(), site.getSiteNumber());
        }

        checkBalloonSettings.setPrintBalloons(getPrintNotificationsCheckBox().isSelected());
        checkBalloonSettings.setEmailBalloons(getSendEmailNotificationsCheckBox().isSelected());

        if (checkBalloonSettings.isPrintBalloons()) {
            checkBalloonSettings.setPostscriptCapable(getPostScriptEnabledCheckBox().isSelected());
            checkBalloonSettings.setPrintDevice(getPrintDeviceTextBox().getText());
        }
        if (checkBalloonSettings.isEmailBalloons()) {
            checkBalloonSettings.setEmailContact(getEmailContactTextBox().getText());
            checkBalloonSettings.setMailServer(getEmailServerTextBox().getText());
        }

        for (int row = 0; row < colorListBox.getRowCount(); row ++)        {
            
            Object [] colValues = colorListBox.getRow(row);
            try {
                Problem problem = (Problem) colValues[0];
                JTextField textField = (JTextField) colValues[1];
                String color = textField.getText();
                
                checkBalloonSettings.addColor(problem, color);
                
            } catch (Exception e) {
                // TODO: log handle exception
                log.log(Log.WARNING, "Exception logged ", e);
            }
            
        }

        return checkBalloonSettings;
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
     * @return true if all fields are valid
     */
    private boolean validateBalloonSettingsFields() {
        
        // TODO code validateBalloonSettingsFields

        if (getPrintNotificationsCheckBox().isSelected()) {

            if (getPrintDeviceTextBox().getText().length() < 1) {
                showMessage("You must specify a print device");
                return false;
            }
        }
        if (getSendEmailNotificationsCheckBox().isSelected()) {
            if (getEmailContactTextBox().getText().length() < 1) {
                showMessage("You must specify a email address/contact");
                return false;
            }
            if (getEmailServerTextBox().getText().length() < 1) {
                showMessage("You must specify a SMTP (e-mail) server");
                return false;
            }
        }

        // TODO validate site combo working
//        if (getSiteComboBox().getSelectedIndex() < 1) {
//            showMessage("You must specify a site number");
//            return false;
//        }

       
        return true;

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

    public void setBalloonSettings(final BalloonSettings inBalloonSettings) {

        this.balloonSettings = inBalloonSettings;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI(balloonSettings);
                enableUpdateButtons(false);
                showMessage("");
            }
        });
    }

    /**
     * Populate GUI
     * 
     * @param inBalloonSettings
     */
    private void populateGUI(BalloonSettings inBalloonSettings) {

        populatingGUI = true;

        if (inBalloonSettings == null) {
            getSendEmailNotificationsCheckBox().setSelected(false);
            getPrintNotificationsCheckBox().setSelected(false);

            getEmailContactTextBox().setText("");
            getEmailServerTextBox().setText("");
            getPrintDeviceTextBox().setText("");
            getPostScriptEnabledCheckBox().setSelected(false);

            getAddButton().setVisible(true);
            getAddButton().setEnabled(true);
            getUpdateButton().setVisible(false);
            populateSiteJCombo(0);

        } else {
            populateSiteJCombo(balloonSettings.getSiteNumber());

            getSendEmailNotificationsCheckBox().setSelected(inBalloonSettings.isEmailBalloons());
            getPrintNotificationsCheckBox().setSelected(inBalloonSettings.isPrintBalloons());

            getEmailContactTextBox().setText(inBalloonSettings.getEmailContact());
            getEmailServerTextBox().setText(inBalloonSettings.getMailServer());
            getPrintDeviceTextBox().setText(inBalloonSettings.getPrintDevice());
            getPostScriptEnabledCheckBox().setSelected(inBalloonSettings.isPostscriptCapable());
            getAddButton().setVisible(false);
            getUpdateButton().setVisible(true);
        }
        
        setBalloonColors(inBalloonSettings);

        populatingGUI = false;

        enableButtons();

    }

    /**
     * Populate site combo box.
     * 
     * @param siteNumber
     */
    private void populateSiteJCombo(int siteNumber) {

        int siteIndex = 0;

        getSiteComboBox().addItem(noneSelected);
        
        // TODO Get this to work.

        int i = 1; // 1 since we started with noneSelected
        Site[] sites = getContest().getSites();
        Arrays.sort(sites, new SiteComparatorBySiteNumber());
        for (Site site : sites) {
            getSiteComboBox().addItem(site);

            if (siteNumber == site.getSiteNumber()) {
                siteIndex = i;
            }
            i++;
        }
        
        getSiteComboBox().setSelectedIndex(siteIndex);
    }

    private void setBalloonColors(BalloonSettings inBalloonSettings) {

        colorListBox.removeAllRows();

        for (Problem problem : getContest().getProblems()) {

            Object[] row = new Object[2];
            row[0] = problem;
            String color = "";
            if (inBalloonSettings != null){
                color = inBalloonSettings.getColor(problem);
            }
            JTextField editField = new JTextField(color);
            editField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyTyped(java.awt.event.KeyEvent e) {
                    enableButtons();
                }
            });
            row[1] = editField;
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
            centerPane.add(siteLabel, null);
            centerPane.add(getColorListBox(), null);
            centerPane.add(getSiteComboBox(), null);
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
        // Handle enabling email fields

        getEmailContactTextBox().setEnabled(getSendEmailNotificationsCheckBox().isSelected());
        getEmailServerTextBox().setEnabled(getSendEmailNotificationsCheckBox().isSelected());

        // Handle Print notification fields
        getPrintDeviceTextBox().setEnabled(getPrintNotificationsCheckBox().isSelected());
        getPostScriptEnabledCheckBox().setEnabled(getPrintNotificationsCheckBox().isSelected());

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
                StaticLog.getLog().log(Log.DEBUG, "Input Balloon Setting (but not saving) ", e);
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
     * This method initializes colorListBox
     * 
     * @return com.ibm.webrunner.j2mclb.MCLB
     */
    private MCLB getColorListBox() {
        if (colorListBox == null) {
            colorListBox = new MCLB();
            colorListBox.setBounds(new java.awt.Rectangle(379, 15, 208, 210));
            String[] cols = { "Problem", "Color" };
            colorListBox.addColumns(cols);
            colorListBox.autoSizeAllColumns();
        }

        return colorListBox;
    }

    /**
     * This method initializes siteCombobox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getSiteComboBox() {
        if (siteComboBox == null) {
            siteComboBox = new JComboBox();
            siteComboBox.setBounds(new java.awt.Rectangle(96, 18, 254, 25));
            siteComboBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableButtons();
                }
            });
        }
        return siteComboBox;
    }

} // @jve:decl-index=0:visual-constraint="28,22"
