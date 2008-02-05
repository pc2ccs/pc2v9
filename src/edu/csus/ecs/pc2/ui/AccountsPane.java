package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.HeapSorter;
import com.ibm.webrunner.j2mclb.util.NumericStringComparator;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.imports.ICPCImportData;
import edu.csus.ecs.pc2.core.imports.LoadICPCData;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.PermissionList;

/**
 * Account Pane list.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$$
public class AccountsPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 2297963114219167947L;

    private MCLB accountListBox = null;

    private JPanel buttonPane = null;

    private JButton addButton = null;

    private JButton editButton = null;

    private JButton filterButton = null;

    private JButton loadButton = null;

    private JPanel messagePanel = null;

    private JLabel messageLabel = null;

    private PermissionList permissionList = new PermissionList();

    private EditAccountFrame editAccountFrame = new EditAccountFrame();

    private GenerateAccountsFrame generateAccountsFrame = new GenerateAccountsFrame();

    private Log log;

    private String lastDir = ".";

    private ReviewAccountLoadFrame reviewAccountLoadFrame;

    private JButton loadICPCButton = null;

    private ICPCAccountFrame icpcAccountFrame = null; // @jve:decl-index=0:visual-constraint="701,102"

    private JButton generateAccountsButton = null;

    /**
     * This method initializes
     * 
     */
    public AccountsPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(625, 216));
        this.add(getMessagePanel(), java.awt.BorderLayout.NORTH);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
        this.add(getRunsListBox(), java.awt.BorderLayout.CENTER);
    }

    @Override
    public String getPluginTitle() {
        return "Accounts Pane";
    }

    protected Object[] buildAccountRow(Account account) {
        // Object[] cols = { "Site", "Type", "Account Id", "Display Name" };
        try {
            int cols = accountListBox.getColumnCount();
            Object[] s = new String[cols];

            ClientId clientId = account.getClientId();
            s[0] = getSiteTitle("" + account.getSiteNumber());
            s[1] = clientId.getClientType().toString().toLowerCase();
            s[2] = "" + clientId.getClientNumber();

            s[3] = getTeamDisplayName(account);
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

        return account.getDisplayName();
    }

    /**
     * This method initializes accountListBox
     * 
     * @return edu.csus.ecs.pc2.core.log.MCLB
     */
    private MCLB getRunsListBox() {
        if (accountListBox == null) {
            accountListBox = new MCLB();

            Object[] cols = { "Site", "Type", "Account Id", "Display Name" };
            accountListBox.addColumns(cols);

            // Sorters
            HeapSorter sorter = new HeapSorter();
            HeapSorter numericStringSorter = new HeapSorter();
            numericStringSorter.setComparator(new NumericStringComparator());

            // Site
            accountListBox.setColumnSorter(0, sorter, 3);

            // Type
            accountListBox.setColumnSorter(1, sorter, 2);

            // Account Id
            accountListBox.setColumnSorter(2, numericStringSorter, 1);

            // Display Name
            accountListBox.setColumnSorter(3, sorter, 4);

            cols = null;

            accountListBox.autoSizeAllColumns();

        }
        return accountListBox;
    }

    public void updateAccountRow(final Account account) {
        // default to autosizing and sorting
        updateAccountRow(account, true);
    }

    public void updateAccountRow(final Account account, final boolean autoSizeAndSort) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Object[] objects = buildAccountRow(account);
                int rowNumber = accountListBox.getIndexByKey(account.getClientId());
                if (rowNumber == -1) {
                    accountListBox.addRow(objects, account.getClientId());
                } else {
                    accountListBox.replaceRow(objects, rowNumber);
                }
                if (autoSizeAndSort) {
                    accountListBox.autoSizeAllColumns();
                    accountListBox.sort();
                }
            }
        });
    }

    /**
     * Return all accounts for all sites.
     * 
     * @return Array of all accounts in model.
     */
    private Account[] getAllAccounts() {

        Vector<Account> allAccounts = new Vector<Account>();

        for (ClientType.Type ctype : ClientType.Type.values()) {
            if (getContest().getAccounts(ctype).size() > 0) {
                Vector<Account> accounts = getContest().getAccounts(ctype);
                allAccounts.addAll(accounts);
            }
        }

        Account[] accountList = (Account[]) allAccounts.toArray(new Account[allAccounts.size()]);
        return accountList;
    }

    public void reloadAccountList() {

        Account[] accounts = getAllAccounts();

        // TODO bulk load these record

        for (Account account : accounts) {
            updateAccountRow(account, false);
        }
        getRunsListBox().autoSizeAllColumns();
        getRunsListBox().sort();
    }

    private boolean isAllowed(Permission.Type type) {
        return permissionList.isAllowed(type);
    }

    private void initializePermissions() {
        Account account = getContest().getAccount(getContest().getClientId());
        permissionList.clearAndLoadPermissions(account.getPermissionList());
    }

    private void updateGUIperPermissions() {

        if (isAllowed(Permission.Type.ADD_ACCOUNT)) {
            addButton.setVisible(isAllowed(Permission.Type.ADD_ACCOUNT));
            
            // TODO eventually set to true when add account works.
            addButton.setEnabled(false);
        }

        editButton.setVisible(isAllowed(Permission.Type.EDIT_ACCOUNT));

        // TODO once filter is working make this visible.
        getFilterButton().setVisible(false);
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        log = getController().getLog();
        getContest().addAccountListener(new AccountListenerImplementation());

        initializePermissions();

        editAccountFrame.setContestAndController(inContest, inController);
        generateAccountsFrame.setContestAndController(inContest, inController);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reloadAccountList();
                updateGUIperPermissions();
            }
        });
    }

    /**
     * Account Listener Implementation
     * 
     * @author pc2@ecs.csus.edu
     * 
     */

    public class AccountListenerImplementation implements IAccountListener {

        public void accountAdded(AccountEvent accountEvent) {
            updateAccountRow(accountEvent.getAccount());
        }

        public void accountModified(AccountEvent accountEvent) {
            updateAccountRow(accountEvent.getAccount());

            // check if is this account
            Account account = accountEvent.getAccount();
            /**
             * If this is the account then update the GUI display per the potential change in Permissions.
             */
            if (getContest().getClientId().equals(account.getClientId())) {
                // They modified us!!
                initializePermissions();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        updateGUIperPermissions();
                    }
                });

            }
        }

        public void accountsAdded(AccountEvent accountEvent) {
            Account[] accounts = accountEvent.getAccounts();
            for (Account account : accounts) {
                updateAccountRow(account, false);
            }
            getRunsListBox().autoSizeAllColumns();
            getRunsListBox().sort();
        }

        public void accountsModified(AccountEvent accountEvent) {
            Account[] accounts = accountEvent.getAccounts();
            for (Account account : accounts) {
                updateAccountRow(account, false);

                /**
                 * If this is the account then update the GUI display per the potential change in Permissions.
                 */
                if (getContest().getClientId().equals(account.getClientId())) {
                    // They modified us!!
                    initializePermissions();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            updateGUIperPermissions();
                        }
                    });

                }

            }
            getRunsListBox().autoSizeAllColumns();
            getRunsListBox().sort();
        }
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(25);
            buttonPane = new JPanel();
            buttonPane.setPreferredSize(new Dimension(35, 35));
            buttonPane.setLayout(flowLayout);
            buttonPane.add(getGenerateAccountsButton(), null);
            buttonPane.add(getAddButton(), null);
            buttonPane.add(getEditButton(), null);
            buttonPane.add(getFilterButton(), null);
            buttonPane.add(getLoadButton(), null);
            buttonPane.add(getLoadICPCButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    // TODO enable the Add button when add account works.
    private JButton getAddButton() {
        if (addButton == null) {
            addButton = new JButton();
            addButton.setText("Add");
            addButton.setEnabled(false);
            addButton.setToolTipText("Add new account");
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                     addAccount();
                }
            });
        }
        return addButton;
    }

    protected void addAccount() {
        editAccountFrame.setAccount(null);
        editAccountFrame.setVisible(true);
    }

    /**
     * This method initializes jButton1
     * 
     * @return javax.swing.JButton
     */
    private JButton getEditButton() {
        if (editButton == null) {
            editButton = new JButton();
            editButton.setText("Edit");
            editButton.setToolTipText("Edit account");
            editButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    editSelectedAccount();
                }
            });
        }
        return editButton;
    }

    protected void editSelectedAccount() {

        int selectedIndex = accountListBox.getSelectedIndex();
        if (selectedIndex == -1) {
            showMessage("Select an account to edit");
            return;
        }

        try {
            ClientId clientId = (ClientId) accountListBox.getKeys()[selectedIndex];
            Account accountToEdit = getContest().getAccount(clientId);
            editAccountFrame.setAccount(accountToEdit);
            editAccountFrame.setVisible(true);
        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to edit account, check log");
        }
    }

    /**
     * This method initializes filterButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getFilterButton() {
        if (filterButton == null) {
            filterButton = new JButton();
            filterButton.setText("Filter");
            filterButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    filterAccounts();
                }
            });
        }
        return filterButton;
    }

    protected void filterAccounts() {
        // TODO Auto-generated method stub

        showMessage("Would have filtered ");
    }

    /**
     * This method initializes loadButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getLoadButton() {
        if (loadButton == null) {
            loadButton = new JButton();
            loadButton.setText("Load");
            loadButton.setToolTipText("Load Account Information from file");
            loadButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    loadAccountsFromDisk();
                }
            });
        }
        return loadButton;
    }

    protected void loadAccountsFromDisk() {
        JFileChooser chooser = new JFileChooser(lastDir);
        // ExtensionFileFilter filter = new ExtensionFileFilter();
        // filter.addExtension("txt");
        // filter.addDescription("Text Files");
        // chooser.addChoosableFileFilter(filter);
        // or??
        // chooser.setFileFilter(filter);
        showMessage("");
        int returnVal = chooser.showOpenDialog(this);
        String msg = "";
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            lastDir = chooser.getCurrentDirectory().toString();
            try {
                File selectedFile = chooser.getSelectedFile().getCanonicalFile();
                if (selectedFile.exists()) {
                    if (selectedFile.isFile()) {
                        if (selectedFile.canRead()) {
                            getReviewAccountLoadFrame().setFile(selectedFile.toString());
                            getReviewAccountLoadFrame().setVisible(true);
                        } else {
                            msg = "File is not readable (" + selectedFile.toString() + ")";
                            log.log(Log.WARNING, msg);
                            showMessage(msg, Color.RED);
                        }
                    } else {
                        msg = "Selected file is not a file (" + selectedFile.toString() + ")";
                        log.log(Log.WARNING, msg);
                        showMessage(msg, Color.RED);
                    }
                } else {
                    msg = "File does not exist (" + selectedFile.toString() + ")";
                    log.log(Log.WARNING, msg);
                    showMessage(msg, Color.RED);
                }
            } catch (IOException e) {
                msg = "Trouble retrieving selected file (" + chooser.getSelectedFile().toString() + ") " + e.toString();
                log.log(Log.WARNING, msg, e);
                showMessage(msg, Color.RED);
            }
        }
    }

    private ReviewAccountLoadFrame getReviewAccountLoadFrame() {
        if (reviewAccountLoadFrame == null) {
            reviewAccountLoadFrame = new ReviewAccountLoadFrame();
            reviewAccountLoadFrame.setContestAndController(getContest(), getController());
        }
        return reviewAccountLoadFrame;
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePanel() {
        if (messagePanel == null) {
            messageLabel = new JLabel();
            messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            messageLabel.setText("");
            messagePanel = new JPanel();
            messagePanel.setLayout(new BorderLayout());
            messagePanel.setPreferredSize(new Dimension(25, 25));
            messagePanel.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return messagePanel;
    }

    private void showMessage(final String string) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(string);
            }
        });
    }

    private void showMessage(final String string, final Color color) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(string);
                messageLabel.setForeground(color);

            }
        });
    }

    /**
     * This method initializes loadICPCButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getLoadICPCButton() {
        if (loadICPCButton == null) {
            loadICPCButton = new JButton();
            loadICPCButton.setText("Load ICPC");
            loadICPCButton.setToolTipText("Load ICPC PC2_Team.tab");
            loadICPCButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    loadICPCAccounts();
                }
            });
        }
        return loadICPCButton;
    }

    protected void loadICPCAccounts() {
        try {
            JFileChooser chooser = new JFileChooser(lastDir);
            chooser.setDialogTitle("Select PC2_Team.tab");
            chooser.setFileFilter(new TabFileFilter());
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File newFile = chooser.getSelectedFile().getCanonicalFile();
                boolean newFileProblem = true;
                if (newFile.exists()) {
                    if (newFile.isFile()) {
                        if (newFile.canRead()) {
                            lastDir = chooser.getCurrentDirectory().toString();
                            // TODO move this off the swing thread, maybe into its own class
                            Account[] accounts;
                            Vector<Account> accountVector = getContest().getAccounts(ClientType.Type.TEAM);
                            accounts = accountVector.toArray(new Account[accountVector.size()]);
                            ICPCImportData importData = LoadICPCData.loadAccounts(lastDir, getContest().getGroups(), accounts);
                            if (importData.getAccounts() != null) {
                                newFileProblem = false;
                                getICPCAccountFrame().setICPCAccounts(importData.getAccounts());
                                getICPCAccountFrame().setContestAndController(getContest(), getController());
                                getICPCAccountFrame().setVisible(true);
                            }
                        }
                    }
                }
                if (newFileProblem) {
                    log.warning("Problem reading PC2_Contest.tab " + newFile.getCanonicalPath() + "");
                    JOptionPane.showMessageDialog(null, "Could not open file " + newFile, "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }

        } catch (Exception e) {
            log.log(Log.WARNING, "Exception ", e);
        }
    }

    /**
     * This method initializes ICPCAccountFrame
     * 
     * @return edu.csus.ecs.pc2.ui.ICPCAccountFrame
     */
    private ICPCAccountFrame getICPCAccountFrame() {
        if (icpcAccountFrame == null) {
            icpcAccountFrame = new ICPCAccountFrame();
        }
        return icpcAccountFrame;
    }

    /**
     * This method initializes generateAccountsButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getGenerateAccountsButton() {
        if (generateAccountsButton == null) {
            generateAccountsButton = new JButton();
            generateAccountsButton.setText("Generate");
            generateAccountsButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    generateAccountsFrame.setVisible(true);
                }
            });
        }
        return generateAccountsButton;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
