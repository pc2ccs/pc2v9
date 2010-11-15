package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Pluralize;
import edu.csus.ecs.pc2.core.imports.LoadAccounts;
import edu.csus.ecs.pc2.core.security.Permission;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import java.awt.Dimension;

/**
 * Review Account Load Frame
 * 
 * 1st line of load file are the column headers.
 * Required columns are:  account, site, & password
 * Optional columns are: displayname, group, permdisplay, & permlogin
 * perm* are booleans, but ignored if empty
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ReviewAccountLoadFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -172535000039944166L;

    private JPanel buttonPane = null;

    private MCLB accountListBox = null;

    private JPanel messagePane = null;

    private JLabel messageLabel = null;

    private Log log;

    private IInternalController controller;

    private IInternalContest contest;

    private Account[] accounts;
    private static final String CHANGE_BEGIN = "";

    private static final String CHANGE_END = "*";

    private JButton acceptButton = null;

    private JPanel jPanel = null;

    private JButton cancelButton = null;

    private String loadedFileName;

    private JCheckBox showAllAccountsCheckBox = null;
    
    /**
     * This method initializes
     * 
     */
    public ReviewAccountLoadFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(800,1000));
        this.setPreferredSize(new java.awt.Dimension(800,1000));
        this.setTitle("Review Account Loading");
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setContentPane(getJPanel());
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                handleCancel();
            }
        });
        
        FrameUtilities.centerFrameTop(this);
    }

    /**
     * This method initializes languageButtonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(25);
            buttonPane = new JPanel();
            buttonPane.setLayout(flowLayout);
            buttonPane.setPreferredSize(new java.awt.Dimension(35, 35));
            buttonPane.add(getAcceptButton(), null);
            buttonPane.add(getCancelButton(), null);
            buttonPane.add(getShowAllAccountsCheckBox(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes languageListBox
     * 
     * @return edu.csus.ecs.pc2.core.log.MCLB
     */
    private MCLB getAccountListBox() {
        if (accountListBox == null) {
            accountListBox = new MCLB();

            Object[] cols = { "Site", "Type", "Account Id", "Display Name", "Password", "Permissions", "Group", "Alias"};
            accountListBox.addColumns(cols);

            /**
             * No sorting at this time, the only way to know what order the languages are is to NOT sort them. Later we can add a sorter per LanguageDisplayList somehow.
             */

            // // Sorters
            // HeapSorter sorter = new HeapSorter();
            // // HeapSorter numericStringSorter = new HeapSorter();
            // // numericStringSorter.setComparator(new NumericStringComparator());
            //
            // // Display Name
            // languageListBox.setColumnSorter(0, sorter, 1);
            // // Compiler Command Line
            // languageListBox.setColumnSorter(1, sorter, 2);
            // // Exe Name
            // languageListBox.setColumnSorter(2, sorter, 3);
            // // Execute Command Line
            // languageListBox.setColumnSorter(3, sorter, 4);
            accountListBox.autoSizeAllColumns();

        }
        return accountListBox;
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        contest = inContest;
        controller = inController;
        log = controller.getLog();

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
            messageLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messagePane = new JPanel();
            messagePane.setLayout(new BorderLayout());
            messagePane.setPreferredSize(new java.awt.Dimension(25, 25));
            messagePane.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return messagePane;
    }

    /**
     * show message to user
     * 
     * @param string
     */
    private void showMessage(final String string) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(string);
                messageLabel.setToolTipText(string);
                messageLabel.setForeground(Color.BLACK);
            }
        });

    }

    private void showMessage(final String string, final Color color) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(string);
                messageLabel.setToolTipText(string);
                messageLabel.setForeground(color);
            }
        });
    }
    
    
    
    public String getPluginTitle() {
        return "Review Account Load Frame";
    }

    /**
     * Return all accounts for all sites.
     * TODO: consider making getAllAccounts public in controller
     * 
     * @return Array of all accounts in contest.
     */
    private Account[] getAllAccounts() {

        ArrayList<Account> allAccounts = new ArrayList<Account>();

        for (ClientType.Type ctype : ClientType.Type.values()) {
            if (contest.getAccounts(ctype).size() > 0) {
                Vector<Account> accountsOfType = contest.getAccounts(ctype);
                allAccounts.addAll(accountsOfType);
            }
        }

        Account[] accountList = (Account[]) allAccounts.toArray(new Account[allAccounts.size()]);
        return accountList;
    }

    public void setFile(String filename) {
        
        loadedFileName = filename;
        showMessage("Loaded " + filename);
        getAccountListBox().removeAllRows();
        log.info("Attempting to load accounts from file: "+filename);
        LoadAccounts loadAccounts = new LoadAccounts();
        getAcceptButton().setEnabled(false);
        getShowAllAccountsCheckBox().setSelected(false);
        try {
            accounts = loadAccounts.fromTSVFile(filename, getAllAccounts(), contest.getGroups());
            refreshList();
        } catch (Exception e) {
            log.warning(e.getMessage());
            showErrorMessage(e.getMessage());
        }
        FrameUtilities.centerFrameTop(this);
        setVisible(true);
    }

    private void refreshList() {
        if (accounts != null) {
            int count=0;
            getAccountListBox().removeAllRows();
            Arrays.sort(accounts, new AccountComparator());
            for (Account account : accounts) {
                Account accountOrig = contest.getAccount(account.getClientId());
                if (getShowAllAccountsCheckBox().isSelected() || !accountOrig.isSameAs(account)) {
                    updateAccountRow(account);
                    count++;
                }
            }
            log.info("found " + count + " "+Pluralize.simplePluralize("account", count));
            if (count > 0) {
                getAcceptButton().setEnabled(true);
            }
        }
    }
    
    private void showErrorMessage(String msg){
        showMessage(msg, Color.RED);
    }


    public void updateAccountRow(final Account account) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Object[] objects = buildAccountRow(account);
                int rowNumber = accountListBox.getIndexByKey(account.getClientId());
                if (rowNumber == -1) {
                    accountListBox.addRow(objects, account.getClientId());
                } else {
                    accountListBox.replaceRow(objects, rowNumber);
                }
                accountListBox.autoSizeAllColumns();
            }
        });
    }

    protected Object[] buildAccountRow(Account account) {
        // Object[] cols = { "Site", "Type", "Account Id", "Display Name", "Password", "Permissions", "Group", "Alias"};
        try {
            int cols = accountListBox.getColumnCount();
            Object[] s = new String[cols];

            ClientId clientId = account.getClientId();
            Account accountOrig = contest.getAccount(clientId);
            s[0] = getSiteTitle("" + account.getSiteNumber());
            s[1] = clientId.getClientType().toString().toLowerCase();
            s[2] = "" + clientId.getClientNumber();
            if (getTeamDisplayName(accountOrig).equals(getTeamDisplayName(account))) {
                s[3] = getTeamDisplayName(account);
            } else {
                s[3] = CHANGE_BEGIN + getTeamDisplayName(account) + CHANGE_END;
            }
            if (accountOrig.getPassword().equals(account.getPassword())) {
                s[4] = account.getPassword();
            } else {
                s[4] = CHANGE_BEGIN + account.getPassword() + CHANGE_END;
            }
            String perms = "";
            if (account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD)) {
                perms = perms + "DISPLAY_ON_SCOREBOARD ";
            }
            if (account.isAllowed(Permission.Type.LOGIN)) {
                perms = perms + "LOGIN ";
            }
            if (account.isAllowed(Permission.Type.CHANGE_PASSWORD)) {
                perms = perms + "CHANGE_PASSWORD ";
            }
            s[5] = perms.trim();
            if (accountOrig.getGroupId() == null && account.getGroupId() == null) {
                s[6] = "";
            } else {
                if (account.getGroupId() == null) {
                    s[6] = CHANGE_BEGIN + "<removed>" + CHANGE_END;
                } else {
                    if (accountOrig.getGroupId() == null) {
                        s[6] = CHANGE_BEGIN + contest.getGroup(account.getGroupId()).toString() + CHANGE_END;
                    } else {
                        // neither are null
                        if (account.getGroupId().equals(accountOrig.getGroupId())) {
                            s[6] = contest.getGroup(account.getGroupId()).toString();
                        } else {
                            s[6] = CHANGE_BEGIN + contest.getGroup(account.getGroupId()).toString() + CHANGE_END;
                        }
                    }
                }
            }
            // TODO extra foo to handle null,  maybe it should initialize to "" ?
            if (accountOrig.getAliasName() == null && account.getAliasName() == null) {
                s[7] = "";
            } else {
                if (account.getAliasName() == null) {
                    s[7] = CHANGE_BEGIN + "<removed>" + CHANGE_END;
                } else {
                    if (accountOrig.getAliasName() == null) {
                        s[7] = CHANGE_BEGIN + account.getAliasName() + CHANGE_END;
                    } else {
                        // neither are null
                        if (account.getAliasName().equals(accountOrig.getAliasName())) {
                            s[7] = account.getAliasName();
                        } else {
                            s[7] = CHANGE_BEGIN + account.getAliasName() + CHANGE_END;
                        }
                    }
                }
            }
            return s;
        } catch (Exception exception) {
            StaticLog.getLog().log(Log.INFO, "Exception in buildAccountRow()", exception);
        }
        return null;
    }

    private String getSiteTitle(String string) {
        return "Site " + string;
    }

    private String getTeamDisplayName(Account account) {
        if (account != null) {
            return account.getDisplayName();
        }

        return "Unknown";
    }

    /**
     * This method initializes acceptButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAcceptButton() {
        if (acceptButton == null) {
            acceptButton = new JButton();
            acceptButton.setText("Accept");
            acceptButton.setMnemonic(java.awt.event.KeyEvent.VK_A);
            acceptButton.setEnabled(false);
            acceptButton.setPreferredSize(new java.awt.Dimension(100, 26));
            acceptButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleAccept();
                }
            });
        }
        return acceptButton;
    }

    protected void handleAccept() {
        controller.updateAccounts(accounts);
        log.info("Loaded "+accounts.length+" from file "+loadedFileName);
        this.dispose();
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel() {
        if (jPanel == null) {
            jPanel = new JPanel();
            jPanel.setLayout(new BorderLayout());
            jPanel.add(getAccountListBox(), java.awt.BorderLayout.CENTER);
            jPanel.add(getMessagePane(), java.awt.BorderLayout.NORTH);
            jPanel.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
        }
        return jPanel;
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
            cancelButton.setPreferredSize(new java.awt.Dimension(100, 26));
            cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleCancel();
                }
            });
        }
        return cancelButton;
    }

    protected void handleCancel() {
        this.dispose();
    }

    /**
     * This method initializes showAllAccountsCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getShowAllAccountsCheckBox() {
        if (showAllAccountsCheckBox == null) {
            showAllAccountsCheckBox = new JCheckBox();
            showAllAccountsCheckBox.setText("Included unchanged accounts");
            showAllAccountsCheckBox.setPreferredSize(new Dimension(250, 24));
            showAllAccountsCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    refreshList();
                }
            });
        }
        return showAllAccountsCheckBox;
    }
} // @jve:decl-index=0:visual-constraint="46,36"
