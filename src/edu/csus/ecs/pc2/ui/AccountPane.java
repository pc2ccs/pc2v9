package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.Permission.Type;

/**
 * Add/Edit Account Pane
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class AccountPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -1572390105202179281L;

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

    private JList permissionsJList = null;

    private JLabel permissionCountLabel = null;

    private JLabel displayNameLabel = null;

    private JTextField displayNameTextField = null;

    private JLabel passwordLabel = null;

    private JTextField passwordTextField = null;

    private JTextField passwordConfirmField = null;

    private JLabel jLabel = null;

    private JLabel groupTitleLabel = null;

    private JTextField groupTextField = null;

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
        this.setSize(new java.awt.Dimension(536,294));

        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
        this.add(getMainSplitPane(), java.awt.BorderLayout.CENTER);
    }

    public void setContestAndController(IContest inContest, IController inController) {
        super.setContestAndController(inContest, inController);
        log = getController().getLog();
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

        // Account newAccount = getAccountFromFields();

        // TODO update account
        // getController().addNewAccount(newAccount);

        cancelButton.setText("Close");
        addButton.setEnabled(false);
        updateButton.setEnabled(false);

        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }
    }

    // TODO uncomment getAccountFromFields
    // private Account getAccountFromFields() {
    // return account;
    // }

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

        // Account newAccount = getAccountFromFields();

        cancelButton.setText("Close");
        addButton.setEnabled(false);
        updateButton.setEnabled(false);

        // TODO update account
        // getController().updateAccount(newAccount);

        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }
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

            int result = FrameUtilities.yesNoCancelDialog("Account modified, save changes?", "Confirm Choice");

            if (result == JOptionPane.YES_OPTION) {
                if (getAddButton().isEnabled()) {
                    addAccount();
                } else {
                    updateAccount();
                }
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

        if (account2 != null) {

            getAddButton().setVisible(false);
            getUpdateButton().setVisible(true);

        } else {

            getAddButton().setVisible(true);
            getUpdateButton().setVisible(false);

        }
        
        populatePermissions (account2);
    }
    
    private String [] getPermissionDescriptions() {
        Permission permission = new Permission();
        
        String [] permissionListNames =  new String[Permission.Type.values().length];
        
        int i = 0;
        for (Type type : Permission.Type.values()){
            permissionListNames[i] = permission.getDescription(type);
            i++;
        }
        
        Arrays.sort(permissionListNames);

        return permissionListNames;
    }

    private void populatePermissions(Account inAccount) {
        
//        Vector <Integer> permissionIndexes = new Vector<Integer>();
        
        defaultListModel.removeAllElements();
        for (String name : getPermissionDescriptions()) {
            defaultListModel.addElement(name);
        }
        
        int count = 0;
        for (Type type : Permission.Type.values()){
            if (account.isAllowed(type)){
                count ++;
            }
        }
        
        permissionCountLabel.setText(count+" permissions selected");
        
        
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
            groupTitleLabel = new JLabel();
            groupTitleLabel.setBounds(new java.awt.Rectangle(13,171,137,16));
            groupTitleLabel.setText("Group");
            jLabel = new JLabel();
            jLabel.setBounds(new java.awt.Rectangle(13,116,191,21));
            jLabel.setText("Password Confirmation ");
            passwordLabel = new JLabel();
            passwordLabel.setBounds(new java.awt.Rectangle(13,61,191,21));
            passwordLabel.setText("Password");
            displayNameLabel = new JLabel();
            displayNameLabel.setBounds(new java.awt.Rectangle(13,6,191,21));
            displayNameLabel.setText("Display Name");
            accountDetailPane = new JPanel();
            accountDetailPane.setLayout(null);
            accountDetailPane.setPreferredSize(new java.awt.Dimension(120, 120));
            accountDetailPane.add(displayNameLabel, null);
            accountDetailPane.add(getDisplayNameTextField(), null);
            accountDetailPane.add(passwordLabel, null);
            accountDetailPane.add(getPasswordTextField(), null);
            accountDetailPane.add(getJTextField(), null);
            accountDetailPane.add(jLabel, null);
            accountDetailPane.add(groupTitleLabel, null);
            accountDetailPane.add(getJTextField2(), null);
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
            permissionPane.add(permissionCountLabel, java.awt.BorderLayout.SOUTH);
            permissionPane.add(permissionMainTitle, java.awt.BorderLayout.NORTH);
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
            permissionsJList = new JList();
            permissionsJList.setModel(defaultListModel);
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
            displayNameTextField.setBounds(new java.awt.Rectangle(13,33,272,22));
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
            passwordTextField.setBounds(new java.awt.Rectangle(13,88,272,22));
        }
        return passwordTextField;
    }

    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getJTextField() {
        if (passwordConfirmField == null) {
            passwordConfirmField = new JTextField();
            passwordConfirmField.setBounds(new java.awt.Rectangle(13,143,272,22));
        }
        return passwordConfirmField;
    }

    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getJTextField2() {
        if (groupTextField == null) {
            groupTextField = new JTextField();
            groupTextField.setBounds(new java.awt.Rectangle(13,193,272,22));
        }
        return groupTextField;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
