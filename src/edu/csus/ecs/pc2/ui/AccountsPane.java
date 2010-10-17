package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
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
import javax.swing.filechooser.FileFilter;

import com.ibm.webrunner.j2mclb.util.HeapSorter;
import com.ibm.webrunner.j2mclb.util.NumericStringComparator;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.imports.ExportAccounts;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.Group;
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
     * These descriptions are used by the export accounts
     */
    private static final String XML_DESCRIPTION = "XML document (*.xml)";

    private static final String CSV_DESCRIPTION = "CSV (comma delimited) (*.csv)";

    private static final String TEXT_DESCRIPTION = "Text (tab delimited) (*.txt,*.tab)";

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

    private JButton generateAccountsButton = null;

    private JButton saveButton = null;

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
//        Object[] cols = { "Site", "Type", "Account Id", "Display Name" , "Group", "Alias"};

        try {
            int cols = accountListBox.getColumnCount();
            Object[] s = new String[cols];

            ClientId clientId = account.getClientId();
            s[0] = getSiteTitle("" + account.getSiteNumber());
            s[1] = clientId.getClientType().toString().toLowerCase();
            s[2] = "" + clientId.getClientNumber();

            s[3] = getTeamDisplayName(account);
            s[4] = getGroupName(account);
            s[5] = getTeamAlias(account);
            return s;
        } catch (Exception exception) {
            StaticLog.getLog().log(Log.INFO, "Exception in buildAccountRow()", exception);
        }
        return null;
    }

    private String getGroupName(Account account) {
        String groupName = "";
        if (account.getGroupId() != null) {
            Group group = getContest().getGroup(account.getGroupId());
            if (group != null) {
                groupName = group.getDisplayName();
            }
        }
        return groupName;
    }

    private String getSiteTitle(String string) {
        return "Site " + string;
    }

    private String getTeamDisplayName(Account account) {
        if (account != null) {
            return account.getDisplayName();
        }

        return "Invalid Account";
    }
    

    private String getTeamAlias(Account account) {
        if (account != null) {
            String alias = account.getAliasName();
            if (alias != null) {
                return alias;
            }
        }

        return "";
    }

    /**
     * This method initializes accountListBox
     * 
     * @return edu.csus.ecs.pc2.core.log.MCLB
     */
    private MCLB getRunsListBox() {
        if (accountListBox == null) {
            accountListBox = new MCLB();

            Object[] cols = { "Site", "Type", "Account Id", "Display Name" , "Group", "Alias"};
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

            // Group
            accountListBox.setColumnSorter(4, sorter, 5);

            // Alias
            accountListBox.setColumnSorter(5, sorter, 6);

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

        getRunsListBox().removeAllRows();
        
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
            sortRunsListBox();
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
            sortRunsListBox();
        }

        public void accountsRefreshAll(AccountEvent accountEvent) {

            initializePermissions();

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadAccountList();
                    updateGUIperPermissions();
                }
            });
            
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
            buttonPane.add(getSaveButton(), null);
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
            addButton.setMnemonic(KeyEvent.VK_A);
            addButton.setEnabled(true);
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
            editButton.setMnemonic(KeyEvent.VK_E);
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
            filterButton.setMnemonic(KeyEvent.VK_F);
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
            loadButton.setMnemonic(KeyEvent.VK_L);
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
     * This method invokes the autoSizeAllColumns() and sort()
     * for the runListBox on the awt thread.
     *
     */
    private void sortRunsListBox() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getRunsListBox().autoSizeAllColumns();
                getRunsListBox().sort();
            }
        });
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
            generateAccountsButton.setMnemonic(KeyEvent.VK_G);
            generateAccountsButton.setToolTipText("Generate multiple new accounts");
            generateAccountsButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    generateAccountsFrame.setVisible(true);
                }
            });
        }
        return generateAccountsButton;
    }

    /**
     * This method initializes saveButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getSaveButton() {
        if (saveButton == null) {
            saveButton = new JButton();
            saveButton.setToolTipText("Save Account Information to file");
            saveButton.setText("Save");
            saveButton.setMnemonic(KeyEvent.VK_S);
            saveButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    saveAccountsToDisk();
                }
            });
        }
        return saveButton;
    }

    protected void saveAccountsToDisk() {
        JFileChooser chooser = new JFileChooser(lastDir);
        FileFilter filterTAB = new FileNameExtensionFilter(TEXT_DESCRIPTION, "txt", "tab");
        chooser.addChoosableFileFilter(filterTAB);
        FileFilter filterCSV = new FileNameExtensionFilter(CSV_DESCRIPTION, "csv");
        chooser.addChoosableFileFilter(filterCSV);
        FileFilter filterXML = new FileNameExtensionFilter(XML_DESCRIPTION, "xml");
        chooser.addChoosableFileFilter(filterXML);
        chooser.setAcceptAllFileFilterUsed(false);
        // always default to Text (tab delimited), otherwise default to last filter (XML)
        chooser.setFileFilter(filterTAB);
        /*
         *  TODO consider making this a class field.
         *  When we are able to read other formats, we might want to save
         *  the selected file filter.
         */
        FileFilter selectedFilter = null;
        
        while (true) {
            showMessage("");
            int returnVal = chooser.showSaveDialog(this);
            String msg = "";
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                lastDir = chooser.getCurrentDirectory().toString();
                try {
                    File selectedFile = chooser.getSelectedFile().getCanonicalFile();
                    chooser.setCurrentDirectory(new File(lastDir));
                    selectedFilter = chooser.getFileFilter();
                    chooser.setFileFilter(selectedFilter);
                    Vector<Account> v = getContest().getAccounts(ClientType.Type.ALL);
                    Account[] a = new Account[v.size()];
                    v.copyInto(a);
                    int indexOfDot = selectedFile.getName().lastIndexOf('.');
                    String ext = "";
                    if (indexOfDot > 0) {
                        ext = selectedFile.getName().substring(indexOfDot + 1);
                        // tab delimited can end with 2 choices
                        if (ext.equalsIgnoreCase(".tab")) { //$NON-NLS-1$
                            // the format is called "TXT"
                            ext = "txt"; //$NON-NLS-1$
                        }
                    }
                    String ext2 = ".na";
                    String description = selectedFilter.getDescription();
                    if (description.equals(TEXT_DESCRIPTION)) {
                        ext2 = "txt"; //$NON-NLS-1$
                    } else {
                        if (description.equals(CSV_DESCRIPTION)) {
                            ext2 = "csv"; //$NON-NLS-1$
                        } else {
                            if (description.equals(XML_DESCRIPTION)) {
                                ext2 = "xml"; //$NON-NLS-1$
                            }
                        }
                    }
                    if (!ext.equals(ext2)) {
                        int indexOfParen = description.lastIndexOf('('); // the (.foo) stuff
                        String descShort = description.substring(0, indexOfParen - 1);
                        msg = "You selected the file type '" + descShort + "' but the file you selected ends with " + ext + "\n\nContinue to save as a " + descShort + "?";
                        int confirmValue = JOptionPane.showConfirmDialog(this, msg);
                        if (confirmValue == JOptionPane.NO_OPTION) {
                            // no this is not correct, pop up the save dialog again
                            continue;
                        }
                        if (confirmValue == JOptionPane.CANCEL_OPTION) {
                            // cancel, get me out of here
                            break;
                        }
                        // yes fails out
                    }
                    if (selectedFile.exists()) {
                        msg = "The file "+selectedFile.getName()+" already exists. Do You want to replace "
                                + "the existing file?";
                        int confirmValue = JOptionPane.showConfirmDialog(this, msg, "File already exists", JOptionPane.YES_NO_OPTION);
                        if (confirmValue == JOptionPane.NO_OPTION) {
                            // TODO or should we break?
                            continue;
                        }
                    }
                    ExportAccounts.Formats format = ExportAccounts.Formats.valueOf(ext2.toUpperCase());
                    if (!ExportAccounts.saveAccounts(format, a, getContest().getGroups(), selectedFile)) {
                        Exception saveException = ExportAccounts.getException();
                        // record this as an erro in ExportAccounts, not AccountsPane
                        log.throwing("ExportAccounts", "saveAccounts()", saveException);
                        showMessage("Error saving file "+saveException.getMessage()+", check log for details");
                    }
                    break;
                } catch (Exception e) {
                    log.throwing("AcountsPane", "saveAccountsToDisk()", e);
                    showMessage("Error: "+e.getMessage()+", check log for details");
                }
            } else {
                // select dialog canceled
                break;
            }
        }
    }

} // @jve:decl-index=0:visual-constraint="10,10"
