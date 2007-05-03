package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.util.TabSeparatedValueParser;
import javax.swing.JButton;

/**
 * Review Account Load Frame
 * 
 * 1st line of load file are the column headers.
 * Required columns are:  account, site, & password
 * Optional columns are: displayname, group, permdisplay, & permlogin
 * perm* are booleans, but ignored if empty
 * @author pc2@ecs.csus.edu
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

    private IController controller;

    private IContest contest;

    private int siteColumn = -1;

    private int accountColumn = -1;

    private int displayNameColumn = -1;

    private int passwordColumn = -1;

    private int groupColumn = -1;
    
    private int permDisplayColumn = -1;

    private int permLoginColumn = -1;
    private static final String CHANGE_BEGIN = "";

    private static final String CHANGE_END = "*";

    private JButton acceptButton = null;

    private JPanel jPanel = null;

    private JButton cancelButton = null;

    private HashMap<ClientId, Account> accountMap = new HashMap<ClientId, Account>();
    
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
        this.setSize(new java.awt.Dimension(564, 229));
        this.setTitle("Review Account Loading");
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setContentPane(getJPanel());
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

            Object[] cols = { "Site", "Type", "Account Id", "Display Name", "Password", "Permissions" };
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

    public void setContestAndController(IContest inContest, IController inController) {
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
            }
        });

    }

    public String getPluginTitle() {
        return "Review Account Load Frame";
    }

    public void setFile(String filename) {
        // TODO Auto-generated method stub
        int lineCount = 0;
        String[] columns;
        showMessage("");
        try {
            FileReader fileReader = new FileReader(filename);
            BufferedReader in = new BufferedReader(fileReader);
            String line = in.readLine();
            lineCount++;
            if (line != null) {
                columns = TabSeparatedValueParser.parseLine(line);
                siteColumn = -1;
                accountColumn = -1;
                displayNameColumn = -1;
                passwordColumn = -1;
                groupColumn = -1;
                for (int i = 0; i < columns.length; i++) {
                    if (columns[i].equalsIgnoreCase("site")) {
                        siteColumn = i;
                    }
                    if (columns[i].equalsIgnoreCase("account")) {
                        accountColumn = i;
                    }
                    if (columns[i].equalsIgnoreCase("displayname")) {
                        displayNameColumn = i;
                    }
                    if (columns[i].equalsIgnoreCase("password")) {
                        passwordColumn = i;
                    }
                    if (columns[i].equalsIgnoreCase("group")) {
                        groupColumn = i;
                    }
                    if (columns[i].equalsIgnoreCase("permdisplay")) {
                        permDisplayColumn = i;
                    }
                    if (columns[i].equalsIgnoreCase("permlogin")) {
                        permLoginColumn = i;
                    }
                }
                if (accountColumn == -1 || siteColumn == -1 || passwordColumn == -1) {
                    // TODO change this to a popup
                    String msg = "1st line should be the row headers (account, password, and site are required)";
                    showMessage(msg);
                    log.info(msg);
                    return;
                }
            }
            line = in.readLine();
            lineCount++;
            while (line != null) {
                try {
                    String[] values = TabSeparatedValueParser.parseLine(line);
                    Account account = getAccount(values);
                    if (account == null) {
                        // TODO change this to a popup
                        String msg = filename + ":" + lineCount + ": " + " please create the account first (" + values[accountColumn] + ")";
                        showMessage("<HTML><FONT COLOR='red'>" + msg + "</FONT></HTML>");
                        log.info(msg);
                        break;
                    }
                    accountMap.put(account.getClientId(), account);
                    updateAccountRow(account);
                } catch (Exception e) {
                    // TODO change this to a popup
                    String msg = filename + ":" + lineCount + ": " + e.getMessage();
                    e.printStackTrace();
                    showMessage("<HTML><FONT COLOR='red'>" + msg + "</FONT></HTML>");
                    log.info(msg);
                    break;
                }
                line = in.readLine();
                lineCount++;
            }
            in.close();
            fileReader.close();
            in = null;
            fileReader = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (accountMap.size() > 0) {
            getAcceptButton().setEnabled(true);
        } else {
            getAcceptButton().setEnabled(false);
        }
        setVisible(true);
    }

    Account getAccount(String[] values) {
        String accountString = values[accountColumn];
        String[] accountSplit = accountString.split("[0-9]+$");
        String accountName = accountString.substring(0, accountSplit[0].length());
        Type type = Type.valueOf(accountName.toUpperCase());
        int clientNumber = Integer.parseInt(accountString.substring(accountSplit[0].length()));
        String siteString = values[siteColumn];
        String password = values[passwordColumn];
        ClientId clientId = new ClientId(Integer.parseInt(siteString), type, clientNumber);
        Account accountClean = contest.getAccount(clientId);
        if (accountClean == null) {
            // would be nice if we could create the account... not now though...
//            accountClean = new Account(clientId, password, clientId.getSiteNumber());
            return null;
        }
        Account account = new Account(clientId, password, clientId.getSiteNumber());
        account.clearListAndLoadPermissions(accountClean.getPermissionList());
        // TODO uncomment once account has group
        // account.setGroup(accountClean.getGroup());
        account.setPassword(password);
        if (displayNameColumn != -1 && values.length > displayNameColumn) {
            account.setDisplayName(values[displayNameColumn]);
        }
        // if (groupColumn != -1) {
        // account.setGroup(values[groupColumn] && values.length >= groupColumn);
        // }
        if (permDisplayColumn != -1 && values.length > permDisplayColumn && values[permDisplayColumn].length() > 0) {
            if (Boolean.parseBoolean(values[permDisplayColumn])) {
                account.addPermission(Permission.Type.DISPLAY_ON_SCOREBOARD);
            } else {
                account.removePermission(Permission.Type.DISPLAY_ON_SCOREBOARD);
            }
        }
        if (permLoginColumn != -1 && values.length > permLoginColumn && values[permLoginColumn].length() > 0) {
            if (Boolean.parseBoolean(values[permLoginColumn])) {
                account.addPermission(Permission.Type.LOGIN);
            } else {
                account.removePermission(Permission.Type.LOGIN);
            }
        }
        return account;
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
                accountListBox.sort();
            }
        });
    }

    protected Object[] buildAccountRow(Account account) {
        // Object[] cols = { "Site", "Type", "Account Id", "Display Name", "Password" };
        try {
            int cols = accountListBox.getColumnCount();
            Object[] s = new String[cols];

            ClientId clientId = account.getClientId();
            Account accountOrig = contest.getAccount(clientId);
            s[0] = getSiteTitle("" + account.getSiteNumber());
            s[1] = clientId.getClientType().toString().toLowerCase();
            s[2] = "" + clientId.getClientNumber();
            if (getTeamDisplayName(accountOrig).equalsIgnoreCase(getTeamDisplayName(account))) {
                s[3] = getTeamDisplayName(account);
            } else {
                s[3] = CHANGE_BEGIN + getTeamDisplayName(account) + CHANGE_END;
            }
            if (accountOrig.getPassword().equalsIgnoreCase(account.getPassword())) {
                s[4] = account.getPassword();
            } else {
                s[4] = CHANGE_BEGIN + account.getPassword() + CHANGE_END;
            }
            // if (accountOrig.getGroup().equalsIgnoreCase(account.getGroup())) {
            // s[5] = account.getGroup();
            // } else {
            // s[5] = CHANGE_BEGIN + account.getGroup() + CHANGE_END;
            // }
            s[5] = "";
            String perms = "";
            if (account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD)) {
                perms = perms + "DISPLAY_ON_SCOREBOARD ";
            }
            if (account.isAllowed(Permission.Type.LOGIN)) {
                perms = perms + "LOGIN ";
            }
            s[5] = perms.trim();
            return s;
        } catch (Exception exception) {
            StaticLog.getLog().log(Log.INFO, "Exception in buildAccountRow()", exception);
        }
        return null;
    }

    private String getSiteTitle(String string) {
        // TODO Auto-generated method stub
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
        Account[] accounts = new Account[accountMap.size()];
        int i = 0;
        for (Iterator iter = accountMap.keySet().iterator(); iter.hasNext();) {
            ClientId element = (ClientId) iter.next();
            accounts[i] = accountMap.get(element);
            i++;
        }
        controller.updateAccounts(accounts);
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
} // @jve:decl-index=0:visual-constraint="46,36"
