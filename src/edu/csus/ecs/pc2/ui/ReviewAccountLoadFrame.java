// Copyright (C) 1989-2026 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.imports.LoadAccounts;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Pluralize;
import edu.csus.ecs.pc2.core.security.Permission;

/**
 * Review Account Load Frame
 *
 * 1st line of load file are the column headers.
 * Required columns are:  account, site, and password
 * Optional columns are: Display Name, Permissions, Group, Alias, ICPC Id, Short School Name,Long School Name, Inst ID,Team Name, Country
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
    private Account[] newAccounts;

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
        getContentPane().setLayout(new BorderLayout());
        this.setSize(new Dimension(801, 600));
        this.setPreferredSize(new java.awt.Dimension(800,1000));
        this.setTitle("Review Account Loading");
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setContentPane(getJPanel());
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
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
     * This method initializes accountListBox
     *
     * @return edu.csus.ecs.pc2.core.log.MCLB
     */
    private MCLB getAccountListBox() {
        if (accountListBox == null) {
            accountListBox = new MCLB();
            accountListBox.setMaximumSize(new Dimension(4096, 1024));

            Object[] cols = { "New", "Site", "Type", "Account Id", "Display Name", "Password", "Permissions", "Group", "Alias", "ICPC Id", "Short School Name","Long School Name", "Inst Id", "Team Name", "Country"};
            accountListBox.addColumns(cols);

            /**
             * No sorting at this time, the only way to know what order the accounts are is to NOT sort them. Later we can add a sorter per accountDisplayList somehow.
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

    @Override
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
            @Override
            public void run() {
                messageLabel.setText(string);
                messageLabel.setToolTipText(string);
                messageLabel.setForeground(Color.BLACK);
            }
        });

    }

    private void showMessage(final String string, final Color color) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                messageLabel.setText(string);
                messageLabel.setToolTipText(string);
                messageLabel.setForeground(color);
            }
        });
    }



    @Override
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

        Account[] accountList = allAccounts.toArray(new Account[allAccounts.size()]);
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
            Account [] accountsLoaded = loadAccounts.fromTSVFile(contest, filename, getAllAccounts(), contest.getGroups());
            // Accounts loaded from 'filename' may not exist yet, so we create 2 lists:
            //    1 of existing accounts to update (this is the way it previously worked since accounts could only be updated)
            //    1 of new accounts.  An account is deemed to be "new" if it doesn't exist in the contest.
            ArrayList<Account> accountsToUpdate = new ArrayList<Account>();
            ArrayList<Account> accountsToAdd = new ArrayList<Account>();
            for(Account account : accountsLoaded) {
                if(contest.getAccount(account.getClientId()) != null) {
                    // Account exists, so add it to the accounts to update list
                    accountsToUpdate.add(account);
                } else {
                    // Account doesn't exist (yet) so add it to the accounts to be added list
                    accountsToAdd.add(account);
                }
            }
            accounts = accountsToUpdate.toArray(new Account[0]);
            newAccounts = accountsToAdd.toArray(new Account[0]);
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
            Arrays.sort(newAccounts, new AccountComparator());
            // New accounts are always shown first.  Why you ask?  Well..
            // I wish there was a way to "highlight" the row (in a different color), but MCLB can't do that.
            // Until the MCLB is changed to a JTable (someday), we just added a column for "New".
            // To hopefully make the new accounts more obvious, we put them first. *sigh* -- JohnB
            for (Account account : newAccounts) {
                Account accountOrig = contest.getAccount(account.getClientId());
                updateAccountRow(account, true);
                count++;
            }
            for (Account account : accounts) {
                Account accountOrig = contest.getAccount(account.getClientId());
                if (getShowAllAccountsCheckBox().isSelected() || !accountOrig.isSameAs(account)) {
                    updateAccountRow(account, false);
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


    private void updateAccountRow(final Account account, boolean newAccount) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Object[] objects;
                if(newAccount) {
                    objects = buildNewAccountRow(account);
                } else {
                    objects = buildAccountRow(account);
                }
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
        // Object[] cols = { "New", "Site", "Type", "Account Id", "Display Name", "Password", "Permissions", "Group", "Alias", "ICPC Id", "Short School Name","Long School Name", "Inst Id", "Team Name", "Country"}
        try {
            int cols = accountListBox.getColumnCount();
            Object[] s = new String[cols];

            ClientId clientId = account.getClientId();
            Account accountOrig = contest.getAccount(clientId);
            s[0] = "No";
            s[1] = getSiteTitle("" + account.getSiteNumber());
            s[2] = clientId.getClientType().toString().toLowerCase();
            s[3] = "" + clientId.getClientNumber();
            if (getTeamDisplayName(accountOrig).equals(getTeamDisplayName(account))) {
                s[4] = getTeamDisplayName(account);
            } else {
                s[4] = CHANGE_BEGIN + getTeamDisplayName(account) + CHANGE_END;
            }
            if (accountOrig.getPassword().equals(account.getPassword())) {
                s[5] = account.getPassword();
            } else {
                s[5] = CHANGE_BEGIN + account.getPassword() + CHANGE_END;
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
            s[6] = perms.trim();

            HashSet<ElementId> groupsOrig = accountOrig.getGroupIds();
            HashSet<ElementId> groupsNew = account.getGroupIds();

            if (groupsOrig == null && groupsNew == null) {
                s[7] = "";
            } else {
                if (groupsNew == null) {
                    s[7] = CHANGE_BEGIN + "<removed>" + CHANGE_END;
                } else {
                    // we're going to always need the new accounts list of groups, so compute it once first
                    String allGroups = "";
                    boolean firstString = true;

                    for(ElementId groupElementId : groupsNew) {
                        if(!firstString) {
                            allGroups = allGroups + ',';
                        } else {
                            firstString = false;
                        }
                        allGroups = allGroups + contest.getGroup(groupElementId).getDisplayName();
                    }

                    // groupsOrig may be null here, but groupsNew will always be non-null
                    if (groupsNew.equals(groupsOrig)) {
                        s[7] = allGroups;
                    } else {
                        s[7] = CHANGE_BEGIN + allGroups + CHANGE_END;
                    }
                }
            }

            if (accountOrig.getAliasName() == null && account.getAliasName() == null) {
                s[8] = "";
            } else {
                if (account.getAliasName() == null) {
                    s[8] = CHANGE_BEGIN + "<removed>" + CHANGE_END;
                } else {
                    if (accountOrig.getAliasName() == null) {
                        s[8] = CHANGE_BEGIN + account.getAliasName() + CHANGE_END;
                    } else {
                        // neither are null
                        if (account.getAliasName().equals(accountOrig.getAliasName())) {
                            s[8] = account.getAliasName();
                        } else {
                            s[8] = CHANGE_BEGIN + account.getAliasName() + CHANGE_END;
                        }
                    }
                }
            }
            // afk icpc teamId
            if (accountOrig.getExternalId().equals(account.getExternalId())) {
                s[9] = account.getExternalId();
            } else {
                s[9] = CHANGE_BEGIN + account.getExternalId() + CHANGE_END;
            }
            if (accountOrig.getShortSchoolName().equals(account.getShortSchoolName())) {
                s[10] = account.getShortSchoolName();
            } else {
                s[10] = CHANGE_BEGIN + account.getShortSchoolName() + CHANGE_END;
            }
            if (accountOrig.getLongSchoolName().equals(account.getLongSchoolName())) {
                s[11] = account.getLongSchoolName();
            } else {
                s[11] = CHANGE_BEGIN + account.getLongSchoolName() + CHANGE_END;
            }
            if (accountOrig.getInstitutionCode().equals(account.getInstitutionCode())) {
                s[12] = account.getInstitutionCode();
            } else {
                s[12] = CHANGE_BEGIN + account.getInstitutionCode() + CHANGE_END;
            }
            // afk icpc teamName
            if (accountOrig.getExternalName().equals(account.getExternalName())) {
                s[13] = account.getExternalName();
            } else {
                s[13] = CHANGE_BEGIN + account.getExternalName() + CHANGE_END;
            }
            if (accountOrig.getCountryCode().equals(account.getCountryCode())) {
                s[14] = account.getCountryCode();
            } else {
                s[14] = CHANGE_BEGIN + account.getCountryCode() + CHANGE_END;
            }
            return s;
        } catch (Exception exception) {
            StaticLog.getLog().log(Log.INFO, "Exception in buildAccountRow()", exception);
        }
        return null;
    }

    /**
     * Builds array of objects for a new account's row (basically, all fields are "new").
     *
     * @param account
     * @return array of objects for each column in the row.
     */
    protected Object[] buildNewAccountRow(Account account) {
        // Object[] cols = { "New", "Site", "Type", "Account Id", "Display Name", "Password", "Permissions", "Group", "Alias", "ICPC Id", "Short School Name","Long School Name", "Inst Id", "Team Name", "Country"}
        try {
            int cols = accountListBox.getColumnCount();
            Object[] s = new String[cols];

            ClientId clientId = account.getClientId();
            s[0] =  "Yes";
            s[1] = getSiteTitle("" + account.getSiteNumber());
            s[2] = clientId.getClientType().toString().toLowerCase();
            s[3] = "" + clientId.getClientNumber();
            s[4] = CHANGE_BEGIN + getTeamDisplayName(account) + CHANGE_END;
            s[5] = CHANGE_BEGIN + account.getPassword() + CHANGE_END;
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
            s[6] = perms.trim();

            HashSet<ElementId> groupsNew = account.getGroupIds();

            if (groupsNew == null || groupsNew.isEmpty()) {
                s[7] = "";
            } else {
                // we're going to always need the new accounts list of groups, so compute it once first
                String allGroups = "";
                boolean firstString = true;

                for(ElementId groupElementId : groupsNew) {
                    if(!firstString) {
                        allGroups = allGroups + ',';
                    } else {
                        firstString = false;
                    }
                    allGroups = allGroups + contest.getGroup(groupElementId).getDisplayName();
                }

                s[7] = CHANGE_BEGIN + allGroups + CHANGE_END;
            }
            String aliasName = account.getAliasName();
            if(aliasName != null && aliasName.length() > 0) {
                s[8] = CHANGE_BEGIN + aliasName + CHANGE_END;
            } else {
                s[8] = "";
            }
            s[9] = CHANGE_BEGIN + account.getExternalId() + CHANGE_END;
            s[10] = CHANGE_BEGIN + account.getShortSchoolName() + CHANGE_END;
            s[11] = CHANGE_BEGIN + account.getLongSchoolName() + CHANGE_END;
            s[12] = CHANGE_BEGIN + account.getInstitutionCode() + CHANGE_END;
            s[13] = CHANGE_BEGIN + account.getExternalName() + CHANGE_END;
            s[14] = CHANGE_BEGIN + account.getCountryCode() + CHANGE_END;
            return s;
        } catch (Exception exception) {
            StaticLog.getLog().log(Log.INFO, "Exception in buildNewAccountRow()", exception);
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
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleAccept();
                }
            });
        }
        return acceptButton;
    }

    protected void handleAccept() {
        int updateLength = accounts.length;
        int newLength = newAccounts.length;
        // First, update existing accounts
        if(updateLength > 0) {
            controller.updateAccounts(accounts);
        }
        // If we have accounts to add, add them.  It should be noted that:
        // controller.addNewAccount(Account) differs drastically from controller.addNewAccounts(Account [])
        // This is not because one takes an array and one doesn't it's because the non-array (addNewAccount(Account)) will re-assign
        // the ClientId for the new account (it find the first empty slot).
        // The array version does not reassign the ClientId and believes what is passed in each
        // Account in the array.  The latter does exactly what we want.
        if(newLength > 0) {
            controller.addNewAccounts(newAccounts);
        }

        log.info("Updated " + updateLength + " and added " + newAccounts.length + " accounts from file " + loadedFileName);
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
            jPanel.setPreferredSize(new Dimension(800, 600));
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
                @Override
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
            showAllAccountsCheckBox.setText("Include unchanged accounts");
            showAllAccountsCheckBox.setPreferredSize(new Dimension(250, 24));
            showAllAccountsCheckBox.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    refreshList();
                }
            });
        }
        return showAllAccountsCheckBox;
    }
} // @jve:decl-index=0:visual-constraint="46,36"
