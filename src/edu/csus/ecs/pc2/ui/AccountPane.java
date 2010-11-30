package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.PermissionGroup;
import edu.csus.ecs.pc2.core.list.PermissionByDescriptionComparator;
import edu.csus.ecs.pc2.core.list.SiteComparatorBySiteNumber;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.Permission.Type;
import edu.csus.ecs.pc2.core.security.PermissionList;

/**
 * Add/Edit Account Pane
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class AccountPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -1572390105202179281L;

    private static final String NONE_SELECTED = "NONE SELECTED";

    private JPanel messagePane = null;

    private JPanel buttonPane = null;

    private JButton addButton = null;

    private JButton updateButton = null;

    private JButton cancelButton = null;

    private JLabel messageLabel = null;

    private Account account = null;

    private DefaultListModel defaultListModel = new DefaultListModel();

    // May be used sometime later.
    @SuppressWarnings("unused")
    private Log log = null;

    private JSplitPane mainSplitPane = null;

    private JPanel accountDetailPane = null;

    private JPanel permissionPane = null;

    private JLabel permissionMainTitle = null;

    private JScrollPane permissionScrollPane = null;

    private JCheckBoxJList permissionsJList = null;

    private JLabel permissionCountLabel = null;

    private JLabel displayNameLabel = null;

    private JTextField displayNameTextField = null;

    private JLabel passwordLabel = null;

    private JTextField passwordTextField = null;

    private JTextField passwordConfirmField = null;

    private JLabel jLabel = null;

    private JLabel groupTitleLabel = null;

    private JComboBox groupComboBox = null;

    private boolean populatingGUI = false;

    private Permission permission = new Permission();

    private JLabel jLabel1 = null;

    private JComboBox accountTypeComboBox = null;

    private JLabel accountLabel = null;

    private JTextField accountTextField = null;

    private JComboBox siteSelectionComboBox = null;

    private JLabel siteLabel = null;

    private JLabel aliasLabel = null;

    private JTextField aliasTextField = null;

    private JPanel permButtonPane = null;

    private JButton resetPermissionsButton = null;

    /**
     * This method initializes
     * 
     */
    public AccountPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(536, 450));

        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
        this.add(getMainSplitPane(), java.awt.BorderLayout.CENTER);
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
        log = getController().getLog();
        addWindowCloserListener();
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

    public String getPluginTitle() {
        return "Edit Account Pane";
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
            addButton.setEnabled(false);
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addAccount();
                }
            });
        }
        return addButton;
    }

    protected void addAccount() {

        if (!validatedFields()) {
            return;
        }
        Account newAccount;

        try {
            newAccount = getAccountFromFields(null);
        } catch (InvalidFieldValue e) {
            showMessage(e.getMessage());
            return;
        }

        getController().addNewAccount(newAccount);

        cancelButton.setText("Close");
        addButton.setEnabled(false);
        updateButton.setEnabled(false);

        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }
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
                    updateAccount();
                }
            });
        }
        return updateButton;
    }

    protected void updateAccount() {

        if (!validatedFields()) {
            return;
        }
        Account newAccount;
        try {
            newAccount = getAccountFromFields(account);
        } catch (InvalidFieldValue e) {
            showMessage(e.getMessage());
            return;
        }

        cancelButton.setText("Close");
        addButton.setEnabled(false);
        updateButton.setEnabled(false);

        getController().updateAccount(newAccount);

        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }
    }

    private boolean validatedFields() {

        String oldPassword = getPasswordTextField().getText();
        String newPassword = getPasswordConfirmField().getText();

        if (!oldPassword.equals(newPassword)) {
            showMessage("Password and Confirm password do not match");
            return false;
        }

        if (getSiteSelectionComboBox().getSelectedIndex() == -1){
            showMessage("Select a site");
            return false;
        }
        
        if (getDisplayNameTextField().getText().length() == 0){
            showMessage("Enter a display name");
            return false;
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

            int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), "Account modified, save changes?", "Confirm Choice");

            if (result == JOptionPane.YES_OPTION) {
                if (getAddButton().isEnabled()) {
                    addAccount();
                } else {
                    updateAccount();
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

    public Account getAccount() {
        return account;
    }

    public void setAccount(final Account account) {

        this.account = account;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI(account);
                enableUpdateButtons(false);
            }
        });
    }

    private void populateGUI(Account account2) {

        populatingGUI = true;

        account = account2;

        if (account2 == null) {

            getAccountTextField().setText(""); // XXX this is not right
            getDisplayNameTextField().setText("");
            getPasswordTextField().setText("");
            getPasswordConfirmField().setText("");
            populateGroupComboBox(null);

            getAddButton().setVisible(true);
            getUpdateButton().setVisible(false);

            getAccountTypeComboBox().setSelectedIndex(0);
            getAccountTypeComboBox().setEnabled(true);
            
            loadSiteComboBox (getContest().getSiteNumber());
            getSiteSelectionComboBox().setEnabled(true);

            getAliasTextField().setText("");
        } else {

            getAccountTextField().setText(account2.getClientId().getName());
            getDisplayNameTextField().setText(account2.getDisplayName());
            getPasswordTextField().setText(account2.getPassword());
            getPasswordConfirmField().setText(account2.getPassword());

            populateGroupComboBox(account2.getGroupId());
            getAliasTextField().setText(account2.getAliasName());

            populatePermissions(account2);

            getAddButton().setVisible(false);
            getUpdateButton().setVisible(true);

            getAccountTypeComboBox().setEnabled(false);

            edu.csus.ecs.pc2.core.model.ClientType.Type accountType = account2.getClientId().getClientType();

            for (int i = 0; i < getAccountTypeComboBox().getItemCount(); i++) {
                ClientType.Type type = (ClientType.Type) getAccountTypeComboBox().getItemAt(i);
                if (accountType.equals(type)) {
                    getAccountTypeComboBox().setSelectedIndex(i);
                }
            }

            getAccountTypeComboBox().setEnabled(false);

            loadSiteComboBox (account2.getSiteNumber());
            getSiteSelectionComboBox().setEnabled(false);

        }

        populatePermissions(account2);

        populatingGUI = false;

        enableUpdateButton();
    }

    private void loadSiteComboBox(int siteNumber) {
        
        int selectedIndex = getSiteSelectionComboBox().getSelectedIndex();
        
        getSiteSelectionComboBox().removeAllItems();
        
        Site[] sites = getContest().getSites();
        Arrays.sort(sites, new SiteComparatorBySiteNumber());
        for (int i = 0; i < sites.length; i++) {
            if (sites[i].getSiteNumber() == getContest().getSiteNumber()) {
                Site newSite = new Site(sites[i].getDisplayName() + " (This Site)", getContest().getSiteNumber());
                sites[i] = newSite;
            }
            if (sites[i].getSiteNumber() == siteNumber) {
                selectedIndex = i;
            }
            getSiteSelectionComboBox().addItem(sites[i]);
        }

        if (selectedIndex != -1) {
            getSiteSelectionComboBox().setSelectedIndex(selectedIndex);
        } 
    }

    private void populateGroupComboBox(ElementId elementId) {
        int groupIndex = 0;
        int selectedIndex = 0;
        Group[] groups = getContest().getGroups();

        getGroupComboBox().removeAllItems();
        getGroupComboBox().addItem(NONE_SELECTED);
        for (Group group : groups) {
            groupIndex++;
            getGroupComboBox().addItem(group);
            if (elementId != null) {
                if (group.getElementId().equals(elementId)) {
                    selectedIndex = groupIndex;
                }
            }
        }
        getGroupComboBox().setSelectedIndex(selectedIndex);
    }

    private void populatePermissions(Account inAccount) {

        defaultListModel.removeAllElements();
        
        Permission.Type[] types = Permission.Type.values();
        Arrays.sort(types, new PermissionByDescriptionComparator());

        if (inAccount == null) {

            for (Type type : types) {
                JCheckBox checkBox = new JCheckBox(permission.getDescription(type));
                defaultListModel.addElement(checkBox);
            }
            getPermissionsJList().setSelectedIndex(-1);

        } else {

            int count = 0;
            for (Type type : types) {
                if (account.isAllowed(type)) {
                    count++;
                }
            }

            if (count > 0) {
                int[] indexes = new int[count];
                count = 0;
                int idx = 0;
                for (Type type : types) {
                    JCheckBox checkBox = new JCheckBox(permission.getDescription(type));
                    defaultListModel.addElement(checkBox);
                    if (account.isAllowed(type)) {
                        indexes[count] = idx;
                        count++;
                    }
                    idx++;
                }
                getPermissionsJList().setSelectedIndices(indexes);
                getPermissionsJList().ensureIndexIsVisible(0);
            } else {
                for (Type type : types) {
                    JCheckBox checkBox = new JCheckBox(permission.getDescription(type));
                    defaultListModel.addElement(checkBox);
                }
            }
        }

        showPermissionCount(getPermissionsJList().getSelectedIndices().length + " permissions selected");
    }

    protected void enableUpdateButtons(boolean editedText) {
        if (editedText) {
            cancelButton.setText("Cancel");
        } else {
            cancelButton.setText("Close");
        }
        addButton.setEnabled(editedText);
        updateButton.setEnabled(editedText);
    }

    public void showMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(message);
            }
        });
    }

    public void showPermissionCount(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                permissionCountLabel.setText(message);
            }
        });
    }

    /**
     * This method initializes mainSplitPane
     * 
     * @return javax.swing.JSplitPane
     */
    private JSplitPane getMainSplitPane() {
        if (mainSplitPane == null) {
            mainSplitPane = new JSplitPane();
            mainSplitPane.setOneTouchExpandable(true);
            mainSplitPane.setDividerLocation(300);
            mainSplitPane.setLeftComponent(getPermissionPane());
            mainSplitPane.setRightComponent(getAccountPane());
        }
        return mainSplitPane;
    }

    /**
     * This method initializes permissionPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPermissionPane() {
        if (accountDetailPane == null) {
            aliasLabel = new JLabel();
            aliasLabel.setBounds(new Rectangle(15, 326, 133, 16));
            aliasLabel.setText("Alias");
            siteLabel = new JLabel();
            siteLabel.setBounds(new java.awt.Rectangle(155, 221, 128, 16));
            siteLabel.setText("Site");
            accountLabel = new JLabel();
            accountLabel.setText("Account");
            accountLabel.setLocation(new java.awt.Point(15, 15));
            accountLabel.setSize(new java.awt.Dimension(86, 16));
            jLabel1 = new JLabel();
            jLabel1.setText("Account Type");
            jLabel1.setLocation(new java.awt.Point(15, 220));
            jLabel1.setSize(new java.awt.Dimension(134, 16));
            groupTitleLabel = new JLabel();
            groupTitleLabel.setText("Group");
            groupTitleLabel.setLocation(new java.awt.Point(15, 270));
            groupTitleLabel.setSize(new java.awt.Dimension(191, 16));
            jLabel = new JLabel();
            jLabel.setText("Password Confirmation ");
            jLabel.setSize(new java.awt.Dimension(191, 16));
            jLabel.setLocation(new java.awt.Point(15, 168));
            passwordLabel = new JLabel();
            passwordLabel.setText("Password");
            passwordLabel.setSize(new java.awt.Dimension(191, 16));
            passwordLabel.setLocation(new java.awt.Point(15, 116));
            displayNameLabel = new JLabel();
            displayNameLabel.setText("Display Name");
            displayNameLabel.setSize(new java.awt.Dimension(191, 16));
            displayNameLabel.setLocation(new java.awt.Point(15, 65));
            accountDetailPane = new JPanel();
            accountDetailPane.setLayout(null);
            accountDetailPane.setPreferredSize(new java.awt.Dimension(120, 120));
            accountDetailPane.add(displayNameLabel, null);
            accountDetailPane.add(getDisplayNameTextField(), null);
            accountDetailPane.add(passwordLabel, null);
            accountDetailPane.add(getPasswordTextField(), null);
            accountDetailPane.add(getPasswordConfirmField(), null);
            accountDetailPane.add(jLabel, null);
            accountDetailPane.add(groupTitleLabel, null);
            accountDetailPane.add(getGroupComboBox(), null);
            accountDetailPane.add(jLabel1, null);
            accountDetailPane.add(getAccountTypeComboBox(), null);
            accountDetailPane.add(accountLabel, null);
            accountDetailPane.add(getAccountTextField(), null);
            accountDetailPane.add(getSiteSelectionComboBox(), null);
            accountDetailPane.add(siteLabel, null);
            accountDetailPane.add(aliasLabel, null);
            accountDetailPane.add(getAliasTextField(), null);
        }
        return accountDetailPane;
    }

    /**
     * This method initializes accountPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAccountPane() {
        if (permissionPane == null) {
            permissionCountLabel = new JLabel();
            permissionCountLabel.setText("XX Permissions Selected");
            permissionCountLabel.setPreferredSize(new java.awt.Dimension(25, 25));
            permissionCountLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            permissionMainTitle = new JLabel();
            permissionMainTitle.setText("Permissions / Abilities");
            permissionMainTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            permissionMainTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            permissionMainTitle.setPreferredSize(new java.awt.Dimension(25, 25));
            permissionPane = new JPanel();
            permissionPane.setLayout(new BorderLayout());
            permissionPane.add(getPermissionScrollPane(), java.awt.BorderLayout.CENTER);
            permissionPane.add(permissionMainTitle, java.awt.BorderLayout.NORTH);
            permissionPane.add(getPermButtonPane(), BorderLayout.SOUTH);
        }
        return permissionPane;
    }

    /**
     * This method initializes permissionScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getPermissionScrollPane() {
        if (permissionScrollPane == null) {
            permissionScrollPane = new JScrollPane();
            permissionScrollPane.setViewportView(getPermissionsJList());
        }
        return permissionScrollPane;
    }

    /**
     * This method initializes permissionsJList
     * 
     * @return javax.swing.JList
     */
    private JList getPermissionsJList() {
        if (permissionsJList == null) {
            permissionsJList = new JCheckBoxJList();
            permissionsJList.setModel(defaultListModel);
            // ListSelectionListeners are called before JCheckBoxes get updated
            permissionsJList.addPropertyChangeListener("change", new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    showPermissionCount(permissionsJList.getSelectedIndices().length + " permissions selected");
                    enableUpdateButton();
                }
            });
        }
        return permissionsJList;
    }

    /**
     * This method initializes displayNameTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getDisplayNameTextField() {
        if (displayNameTextField == null) {
            displayNameTextField = new JTextField();
            displayNameTextField.setBounds(new java.awt.Rectangle(14, 88, 272, 22));
            displayNameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return displayNameTextField;
    }

    /**
     * This method initializes passwordTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getPasswordTextField() {
        if (passwordTextField == null) {
            passwordTextField = new JTextField();
            passwordTextField.setBounds(new Rectangle(14, 140, 272, 22));
            passwordTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return passwordTextField;
    }

    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getPasswordConfirmField() {
        if (passwordConfirmField == null) {
            passwordConfirmField = new JTextField();
            passwordConfirmField.setBounds(new Rectangle(14, 191, 272, 22));
            passwordConfirmField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return passwordConfirmField;
    }

    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JComboBox getGroupComboBox() {
        if (groupComboBox == null) {
            groupComboBox = new JComboBox();
            groupComboBox.setBounds(new java.awt.Rectangle(14, 291, 272, 22));
            groupComboBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return groupComboBox;
    }

    public void enableUpdateButton() {

        if (populatingGUI) {
            return;
        }

        boolean enableButton = false;

        if (account != null) {

            try {
                Account changedAccount = getAccountFromFields(null);

                // printAccount(account);
                // printAccount(changedAccount);

                if (!account.isSameAs(changedAccount)) {
                    enableButton = true;
                }

                if (!account.getPermissionList().isSameAs(changedAccount.getPermissionList())) {
                    enableButton = true;
                }

                if (!getPasswordTextField().getText().equals(getPasswordConfirmField().getText())) {
                    enableButton = true;
                }

            } catch (InvalidFieldValue e) {
                // invalid field, but that is ok as they are entering data
                // will be caught and reported when they hit update or add.
                getController().getLog().log(Log.DEBUG, "Input Problem (but not saving) ", e);
                enableButton = true;
            }
        } else {
            if (getAddButton().isVisible()) {
                enableButton = true;
            }
        }

        enableUpdateButtons(enableButton);

    }

    private Account getAccountFromFields(Account checkAccount) throws InvalidFieldValue {

        Site site = (Site) getSiteSelectionComboBox().getSelectedItem();
        
        if (checkAccount == null) {
            if (account == null) {
                ClientType.Type clientType = (ClientType.Type) getAccountTypeComboBox().getSelectedItem();
                ClientId clientId = new ClientId(getContest().getSiteNumber(), clientType, 0);
                checkAccount = new Account(clientId, null, site.getSiteNumber());
            } else {
                checkAccount = new Account(account.getClientId(), null, account.getSiteNumber());
            }
        }

        // clear out the permissions
        checkAccount.clearListAndLoadPermissions(new PermissionList());
        // get permissions
        Object[] objects = getPermissionsJList().getSelectedValues();
        for (Object object : objects) {
            JCheckBox checkBox = (JCheckBox) object;
            String name = checkBox.getText();
            Type type = getTypeFromDescrption(name);
            checkAccount.addPermission(type);
        }

        if (objects.length == 0 && account == null) {
            // Add default permissions if none selected and new account
            ClientType.Type clientType = checkAccount.getClientId().getClientType();
            checkAccount.clearListAndLoadPermissions(new PermissionGroup().getPermissionList(clientType));
        }

        // get display name and group
        checkAccount.setDisplayName(getDisplayNameTextField().getText());
        if (getGroupComboBox().getSelectedIndex() > 0) {
            Group group = (Group) getGroupComboBox().getSelectedItem();
            checkAccount.setGroupId(group.getElementId());
        } else {
            checkAccount.setGroupId(null);
        }

        checkAccount.setSiteNumber(site.getSiteNumber());

        checkAccount.setPassword(getPasswordConfirmField().getText());

        checkAccount.setAliasName(getAliasTextField().getText());
        return checkAccount;
    }

    /**
     * Return Permission Type from description string.
     * 
     * @param string
     * @return
     */
    private Type getTypeFromDescrption(String string) {
        for (Type type : Permission.Type.values()) {
            if (string.equals(permission.getDescription(type))) {
                return type;
            }
        }
        return null;
    }

    /**
     * Intialize Account Type combo box
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getAccountTypeComboBox() {
        if (accountTypeComboBox == null) {
            accountTypeComboBox = new JComboBox();
            accountTypeComboBox.setBounds(new java.awt.Rectangle(14, 242, 137, 25));

            accountTypeComboBox.addItem(ClientType.Type.TEAM);
            accountTypeComboBox.addItem(ClientType.Type.JUDGE);
            accountTypeComboBox.addItem(ClientType.Type.SCOREBOARD);
            accountTypeComboBox.addItem(ClientType.Type.ADMINISTRATOR);
            accountTypeComboBox.addItem(ClientType.Type.SPECTATOR);

        }
        return accountTypeComboBox;
    }

    /**
     * This method initializes accountTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getAccountTextField() {
        if (accountTextField == null) {
            accountTextField = new JTextField();
            accountTextField.setEditable(false);
            accountTextField.setSize(new java.awt.Dimension(272, 22));
            accountTextField.setLocation(new java.awt.Point(14, 38));
        }
        return accountTextField;
    }

    /**
     * This method initializes siteSelectionComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getSiteSelectionComboBox() {
        if (siteSelectionComboBox == null) {
            siteSelectionComboBox = new JComboBox();
            siteSelectionComboBox.setBounds(new java.awt.Rectangle(157, 243, 124, 25));
        }
        return siteSelectionComboBox;
    }

    /**
     * This method initializes aliasTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getAliasTextField() {
        if (aliasTextField == null) {
            aliasTextField = new JTextField();
            aliasTextField.setLocation(new Point(14, 351));
            aliasTextField.setSize(new Dimension(272, 22));
            aliasTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return aliasTextField;
    }

    /**
     * This method initializes permButtonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPermButtonPane() {
        if (permButtonPane == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(2);
            permButtonPane = new JPanel();
            permButtonPane.setLayout(gridLayout);
            permButtonPane.setPreferredSize(new Dimension(40, 40));
            permButtonPane.add(permissionCountLabel, null);
            permButtonPane.add(getResetPermissionsButton(), null);
        }
        return permButtonPane;
    }

    /**
     * This method initializes resetPermissionsButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getResetPermissionsButton() {
        if (resetPermissionsButton == null) {
            resetPermissionsButton = new JButton();
            resetPermissionsButton.setText("Reset");
            resetPermissionsButton.setToolTipText("Reset Default Permission");
            resetPermissionsButton.setMnemonic(KeyEvent.VK_R);
            resetPermissionsButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    resetPermissions();
                }
            });
        }
        return resetPermissionsButton;
    }

    /**
     * Get default permissions and set them into listbox.
     * 
     */
    protected void resetPermissions() {
        Account fakeAccount = new Account(account.getClientId(), account.getPassword(), account.getSiteNumber());
        PermissionList permissionList = new PermissionGroup().getPermissionList (account.getClientId().getClientType());
        if (permissionList != null){
            account.clearListAndLoadPermissions(permissionList);
        }
        populatePermissions(fakeAccount);
        permissionList = null;
        fakeAccount = null;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
