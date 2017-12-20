package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Arrays;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.SiteComparatorBySiteNumber;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.security.Permission.Type;

import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.Dimension;
import java.io.Serializable;

/**
 * Add/Edit BalloonSettings Pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EditBalloonSettingPane extends JPanePlugin {

    private static final long serialVersionUID = -1060536964672397704L;

    private Site noneSelected = new Site("Select Site", 0);

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

    private Log log = null;

    private boolean populatingGUI = true;

    private JPanel centerPane = null;

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

    private JComboBox<Site> siteComboBox = null;

    private IInternalContest contest;

    private JLabel balloonClientLabel = null;

    private JComboBox<Serializable> balloonClientComboBox = null;

    private JButton advancedMailButton = null;

    private Properties savedMailProperties = null;
    private Properties changedMailProperties = null;

    private PropertiesEditFrame propertiesEditFrame;

    private JPanel westPanel = null;

    /**
     * This method initializes
     * 
     */
    public EditBalloonSettingPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(610, 400));

        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
        this.add(getCenterPane(), java.awt.BorderLayout.CENTER);
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        contest = inContest;
        setMailProperties(null);
        log = getController().getLog();
        addWindowCloserListener();
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

        dismissPropertiesEditFrame();
        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }
    }

    private void addWindowCloserListener() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (getParentFrame() != null) {
                    getParentFrame().addWindowListener(new java.awt.event.WindowAdapter() {
                        public void windowClosing(java.awt.event.WindowEvent e) {
                            handleCancelButton();
                        }
                    });
                }
            }
        });
    }

    protected void enableUpdateButtons(boolean fieldsChanged) {
        if (fieldsChanged) {
            cancelButton.setText("Cancel");
        } else {
            cancelButton.setText("Close");
        }
        // only enable the visible one, we are either editting or adding not both
        if (getUpdateButton().isVisible()) {
            getUpdateButton().setEnabled(fieldsChanged);
        } else {
            getAddButton().setEnabled(fieldsChanged);
        }
    }

    /**
     * Create a BalloonSettings from the fields.
     * 
     * @param checkBalloonSettings
     * @return BalloonSettings as retrieved from UI
     * @throws InvalidFieldValue
     */
    public BalloonSettings getBalloonSettingsFromFields(BalloonSettings checkBalloonSettings) {

        if (checkBalloonSettings == null){
            if (getSiteComboBox().getSelectedIndex() == 0) {
                // select site
                new InvalidFieldValue("Invalid site selected.");
            }
            Site site = (Site)getSiteComboBox().getSelectedItem();
            checkBalloonSettings =  new BalloonSettings(site.getDisplayName(), site.getSiteNumber());
        }

        // XXX should this Exception  be in the above null check?
        if (getBalloonClientComboBox().getSelectedIndex() == 0) {
            // select user
            new InvalidFieldValue("Invalid notification user selected.");
        } else {
            checkBalloonSettings.setBalloonClient((ClientId)getBalloonClientComboBox().getSelectedItem());
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
        
        for (int row = 0; row < getColorListBox().getRowCount(); row ++)        {
            
            Object [] colValues = getColorListBox().getRow(row);
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

        checkBalloonSettings.setMailProperties(changedMailProperties);
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

        dismissPropertiesEditFrame();
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
        
        if (getSiteComboBox().getSelectedIndex() < 1) {
            showMessage("You must specify a site number");
            return false;
        }

        if (getBalloonClientComboBox().getSelectedIndex() < 1) {
            showMessage("You must specify a notification Client");
            return false;
        }

        if (getPrintNotificationsCheckBox().isSelected()) {
            if (getPrintDeviceTextBox().getText().length() < 1) {
                showMessage("You must specify a print device");
                return false;
            }
        }

        if (getSendEmailNotificationsCheckBox().isSelected()) {
            if (getEmailContactTextBox().getText().length() < 1) {
                showMessage("You must specify an email address/contact");
                return false;
            }
            if (getEmailServerTextBox().getText().length() < 1) {
                showMessage("You must specify a SMTP (e-mail) server");
                return false;
            }
        }

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

            int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), "Notification Settings modified, save changes?", "Confirm Choice");

            if (result == JOptionPane.YES_OPTION) {
                if (getAddButton().isVisible()) {
                    addBalloonSettings();
                } else {
                    updateBalloonSettings();
                }
                // these should be done in add/Update, but just in case
                dismissPropertiesEditFrame();
                if (getParentFrame() != null) {
                    getParentFrame().setVisible(false);
                }
            } else if (result == JOptionPane.NO_OPTION) {
                dismissPropertiesEditFrame();
                if (getParentFrame() != null) {
                    getParentFrame().setVisible(false);
                }
            }

        } else {
            dismissPropertiesEditFrame();
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
        savedMailProperties = null;
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI(balloonSettings);
                setMailProperties(balloonSettings);
                enableUpdateButtons(false);
                showMessage("");
            }

        });
    }

    private void setMailProperties(BalloonSettings settings) {
        if (settings != null) {
            savedMailProperties = settings.getMailProperties();
        }
        if (savedMailProperties == null) {
            savedMailProperties = BalloonSettings.getDefaultMailProperties();
        }
        changedMailProperties = (Properties) savedMailProperties.clone();
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
            populateBalloonClientCombo(null);
            getSiteComboBox().setEnabled(true);

        } else {
            populateSiteJCombo(balloonSettings.getSiteNumber());
            populateBalloonClientCombo(balloonSettings.getBalloonClient());
            getSiteComboBox().setEnabled(false);
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

    private void populateBalloonClientCombo(ClientId balloonClient) {
        
        // combo should be populate with all admin and board clients for all sites
        Vector<Account> accounts = getContest().getAccounts(ClientType.Type.SCOREBOARD);
        accounts.addAll(getContest().getAccounts(ClientType.Type.ADMINISTRATOR));
        accounts.addAll(getContest().getAccounts(ClientType.Type.JUDGE));
        accounts.addAll(getContest().getAccounts(ClientType.Type.SPECTATOR));
        Account[] accountArray;
        getBalloonClientComboBox().removeAllItems();
        getBalloonClientComboBox().addItem("Select User");
        if (accounts.size() > 0) {
            accountArray = accounts.toArray(new Account[accounts.size()]);
            // TODO ideally this would be boards 1st, then admins
            Arrays.sort(accountArray, new AccountComparator());
            int found = 0;
            int count = 0;
            for (Account account : accountArray) {
                if (account.isAllowed(Type.BALLOON_EMAIL) || account.isAllowed(Type.BALLOON_PRINT)) {
                    getBalloonClientComboBox().addItem(account.getClientId());
                    count++;
                    if (balloonClient != null && account.getClientId().equals(balloonClient)) {
                        found = count;
                    }
                }
            }
            if (balloonClient == null ) {
                getBalloonClientComboBox().setSelectedIndex(0);
            } else {
                getBalloonClientComboBox().setSelectedIndex(found);
            }
        }
    }

    /**
     * Populate site combo box.
     * 
     * @param siteNumber
     */
    private void populateSiteJCombo(int siteNumber) {

        int siteIndex = 0;

        getSiteComboBox().removeAllItems();
        getSiteComboBox().addItem(noneSelected);
        
        int i = 1; // 1 since we started with noneSelected
        Site[] sites = getContest().getSites();
        Arrays.sort(sites, new SiteComparatorBySiteNumber());
        for (Site site : sites) {
            if (siteNumber == 0) { // this is an add
                // do not allow them to add balloonSettings for a site with settings already
                if (contest.getBalloonSettings(site.getSiteNumber()) != null) {
                    continue;
                }
            }
            getSiteComboBox().addItem(site);

            if (siteNumber == site.getSiteNumber()) {
                siteIndex = i;
            }
            i++;
        }
        
        getSiteComboBox().setSelectedIndex(siteIndex);
    }

    private void setBalloonColors(BalloonSettings inBalloonSettings) {

        getColorListBox().removeAllRows();

        for (Problem problem : getContest().getProblems()) {

            Object[] row = new Object[2];
            row[0] = problem;
            String color = problem.getColorName();
            if (color == null) {
                color = "";
            }
            if (inBalloonSettings != null){
                color = inBalloonSettings.getColor(problem);
            }
            JTextField editField = new JTextField(color);
            editField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableButtons();
                    getColorListBox().autoSizeAllColumns();
                }
            });
            // try to keep the 1st letter of the border (hard to read)
            editField.setMargin(new Insets(0,1,0,0));
            row[1] = editField;
            getColorListBox().addRow(row);

        }
        getColorListBox().autoSizeAllColumns();
    }

    public void showMessage(final String message) {
        if (message.trim().length() == 0) {
            return;
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(getParentFrame(), message, "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    /**
     * This method initializes centerPane
     * 
     * @return java.awt.Panel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            balloonClientLabel = new JLabel();
            balloonClientLabel.setText("Balloon Client");
            balloonClientLabel.setBounds(new Rectangle(11, 280, 100, 16));
            siteLabel = new JLabel();
            siteLabel.setText("Site");
            siteLabel.setBounds(new Rectangle(14, 21, 48, 16));
            printDeviceLabel = new JLabel();
            printDeviceLabel.setText("Print Device");
            printDeviceLabel.setBounds(new Rectangle(41, 217, 112, 21));
            jLabel = new JLabel();
            jLabel.setText("EMail Server");
            jLabel.setBounds(new Rectangle(42, 114, 112, 21));
            mailContactLabel = new JLabel();
            mailContactLabel.setText("EMail Contact");
            mailContactLabel.setBounds(new Rectangle(41, 79, 112, 21));
            centerPane = new JPanel();
            centerPane.setLayout(new BorderLayout());
            centerPane.add(getWestPanel(), BorderLayout.WEST);
            centerPane.add(getColorListBox(), BorderLayout.CENTER);
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
            sendEmailNotificationsCheckBox.setText("Send Email Notifications");
            sendEmailNotificationsCheckBox.setBounds(new Rectangle(14, 47, 185, 21));
            sendEmailNotificationsCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableButtons();
                }
            });
        }
        return sendEmailNotificationsCheckBox;
    }

    /**
     * hides the properties edit frame, and sets it to null.
     */
    private void dismissPropertiesEditFrame() {
        if (propertiesEditFrame != null) {
            propertiesEditFrame.setVisible(false);
            propertiesEditFrame = null;
        }
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
                if (getSiteComboBox().getSelectedIndex() > 0 || getBalloonClientComboBox().getSelectedIndex() > 0) {
                    enableButton = true;
                }
                if (getPrintNotificationsCheckBox().isSelected() || getSendEmailNotificationsCheckBox().isSelected()) {
                    enableButton = true;
                }
                for (int i = 0; i < getColorListBox().getRowCount(); i++) {
                    Object[] o = getColorListBox().getRow(i);
                    JTextField colorTextField = (JTextField)o[1];
                    String color = colorTextField.getText().trim();
                    if (color.length() > 0) {
                        enableButton = true;
                        break;
                    }
                }
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
            printNotificationsCheckBox.setText("Print Notifications");
            printNotificationsCheckBox.setBounds(new Rectangle(13, 187, 162, 24));
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
            emailContactTextBox.setBounds(new Rectangle(172, 79, 179, 21));
            emailContactTextBox.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
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
            emailServerTextBox.setBounds(new Rectangle(172, 114, 179, 21));
            emailServerTextBox.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    changedMailProperties.put(BalloonSettings.MAIL_HOST, getEmailServerTextBox().getText());
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
            postScriptEnabledCheckBox.setText("PostScript enabled printer");
            postScriptEnabledCheckBox.setBounds(new Rectangle(39, 243, 212, 24));
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
            printDeviceTextBox.setBounds(new Rectangle(171, 217, 179, 21));
            printDeviceTextBox.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
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
    private JComboBox<Site> getSiteComboBox() {
        if (siteComboBox == null) {
            siteComboBox = new JComboBox<Site>();
            siteComboBox.setBounds(new Rectangle(85, 17, 254, 25));
            siteComboBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableButtons();
                }
            });
        }
        return siteComboBox;
    }

    /**
     * This method initializes balloonClientComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox<Serializable> getBalloonClientComboBox() {
        if (balloonClientComboBox == null) {
            balloonClientComboBox = new JComboBox<Serializable>();
            balloonClientComboBox.setBounds(new Rectangle(131, 280, 204, 25));
            balloonClientComboBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // message is usually only updated on Update/Add, clear it till then
                    showMessage("");
                    enableButtons();
                }
            });
        }
        return balloonClientComboBox;
    }

    /**
     * This method initializes AdvancedMailButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAdvancedMailButton() {
        if (advancedMailButton == null) {
            advancedMailButton = new JButton();
            advancedMailButton.setToolTipText("Advanced Mail Server Settings");
            advancedMailButton.setFont(new Font("Dialog", Font.BOLD, 12));
            advancedMailButton.setBounds(new Rectangle(52, 151, 263, 25));
            advancedMailButton.setText("EMail Server Advanced Settings");
            advancedMailButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showMailPropertiesEditor();
                }
            });
        }
        return advancedMailButton;
    }

    protected void showMailPropertiesEditor() {
        if (propertiesEditFrame == null) { 
            propertiesEditFrame = new PropertiesEditFrame();
        }
        propertiesEditFrame.setTitle("Edit Advanced Mail Properties");
        propertiesEditFrame.setProperties(changedMailProperties, new UpdateMailProperties());
        propertiesEditFrame.setSize(350, 315);
        propertiesEditFrame.setVisible(true);
        
    }
    /**
     * Update the edited properties.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    protected class UpdateMailProperties implements IPropertyUpdater {

        public void updateProperties(Properties properties) {
            changedMailProperties = properties;
            getEmailServerTextBox().setText(properties.getProperty(BalloonSettings.MAIL_HOST));
            enableButtons();
        }
    }
    /**
     * This method initializes westPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getWestPanel() {
        if (westPanel == null) {
            westPanel = new JPanel();
            westPanel.setLayout(null);
            westPanel.setPreferredSize(new Dimension(370, 341));
            westPanel.add(siteLabel, null);
            westPanel.add(getSiteComboBox(), null);
            westPanel.add(getSendEmailNotificationsCheckBox(), null);
            westPanel.add(mailContactLabel, null);
            westPanel.add(getEmailContactTextBox(), null);
            westPanel.add(jLabel, null);
            westPanel.add(getEmailServerTextBox(), null);
            westPanel.add(getAdvancedMailButton(), null);
            westPanel.add(getPrintNotificationsCheckBox(), null);
            westPanel.add(printDeviceLabel, null);
            westPanel.add(getPrintDeviceTextBox(), null);
            westPanel.add(getPostScriptEnabledCheckBox(), null);
            westPanel.add(balloonClientLabel, null);
            westPanel.add(getBalloonClientComboBox(), null);
        }
        return westPanel;
    }

} // @jve:decl-index=0:visual-constraint="28,22"
