package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Date;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.HeapSorter;
import com.ibm.webrunner.j2mclb.util.NumericStringComparator;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.ILoginListener;
import edu.csus.ecs.pc2.core.model.LoginEvent;
import edu.csus.ecs.pc2.core.report.LoginReport;
import edu.csus.ecs.pc2.core.security.PermissionList;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.KeyEvent;

/**
 * View Logins.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$ 
 */

// $HeadURL${date}
public class LoginsPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -3609625972816987570L;

    private JPanel loginButtonPane = null;

    private MCLB loginListBox = null;

    private JButton logoffButton = null;

    private PermissionList permissionList = new PermissionList();

    private JPanel messagePanel = null;

    private JLabel messageLabel = null;

    private JButton reportButton = null;

    /**
     * This method initializes
     * 
     */
    public LoginsPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(536, 217));
        this.add(getLoginButtonPanel(), java.awt.BorderLayout.SOUTH);
        this.add(getLoginListBox(), java.awt.BorderLayout.CENTER);

    }

    @Override
    public String getPluginTitle() {
        return "Logins Panel";
    }

    /**
     * This method initializes loginButtonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getLoginButtonPanel() {
        if (loginButtonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(35);
            flowLayout.setVgap(5);
            loginButtonPane = new JPanel();
            loginButtonPane.setLayout(flowLayout);
            loginButtonPane.setPreferredSize(new java.awt.Dimension(35, 35));
            loginButtonPane.add(getLogoffButton(), null);
            loginButtonPane.add(getReportButton(), null);
        }
        return loginButtonPane;
    }

    /**
     * This method initializes loginListBox
     * 
     * @return edu.csus.ecs.pc2.core.log.MCLB
     */
    private MCLB getLoginListBox() {
        if (loginListBox == null) {
            loginListBox = new MCLB();

            loginListBox.add(getMessagePanel(), java.awt.BorderLayout.NORTH);
            Object[] cols = { "Site", "Type", "Number", "Connection Id", "Since" };

            loginListBox.addColumns(cols);

            // Sorters
            HeapSorter sorter = new HeapSorter();
            HeapSorter numericStringSorter = new HeapSorter();
            numericStringSorter.setComparator(new NumericStringComparator());

            // Site
            loginListBox.setColumnSorter(0, sorter, 1);

            // Type
            loginListBox.setColumnSorter(1, sorter, 2);

            // Id
            loginListBox.setColumnSorter(2, numericStringSorter, 3);

            // Connection Handler Id
            loginListBox.setColumnSorter(3, sorter, 4);

            // Since
            loginListBox.setColumnSorter(4, sorter, 5);

            loginListBox.autoSizeAllColumns();

        }
        return loginListBox;
    }

    private Object[] buildLoginRow(ClientId clientId, ConnectionHandlerID connectionHandlerID) {

        // Object[] cols = { "Site", "Type", "Number", "Connection Id", "Since" };

        Object[] obj = new Object[loginListBox.getColumnCount()];

        obj[0] = "Site " + clientId.getSiteNumber();
        obj[1] = clientId.getClientType().toString().toLowerCase();
        obj[2] = "" + clientId.getClientNumber();
        if (connectionHandlerID != null) {
            obj[3] = connectionHandlerID.toString();
        } else {
            obj[3] = "Undefined";
        }
        obj[4] = new Date().toString();

        return obj;
    }

    /**
     * Return array of all logged in users.
     */
    private ClientId[] getAllLoggedInUsers() {

        Vector<ClientId> clientList = new Vector<ClientId>();

        for (ClientType.Type ctype : ClientType.Type.values()) {

            ClientId[] users = getContest().getAllLoggedInClients(ctype);
            for (ClientId clientId : users) {
                clientList.addElement(clientId);
            }
        }
        if (clientList.size() == 0) {
            return new ClientId[0];
        } else {
            ClientId[] clients = (ClientId[]) clientList.toArray(new ClientId[clientList.size()]);
            return clients;
        }
    }

    private void reloadListBox() {
        loginListBox.removeAllRows();

        ClientId[] clientList = getAllLoggedInUsers();

        for (ClientId clientId : clientList) {
            ConnectionHandlerID connectionHandlerID = getContest().getConnectionHandleID(clientId);
            updateLoginList(clientId, connectionHandlerID);
        }
    }

    /**
     * Add or update a login row
     * 
     * @param login
     */
    private void updateLoginRow(ClientId clientId, ConnectionHandlerID connectionHandlerID) {
        int row = loginListBox.getIndexByKey(clientId);
        if (row == -1) {
            Object[] objects = buildLoginRow(clientId, connectionHandlerID);
            loginListBox.addRow(objects, clientId);
        } else {
            Object[] objects = buildLoginRow(clientId, connectionHandlerID);
            loginListBox.replaceRow(objects, row);
        }
        loginListBox.autoSizeAllColumns();
    }

    private void removeLoginRow(ClientId clientId, ConnectionHandlerID connectionHandlerID) {
        int row = loginListBox.getIndexByKey(clientId);
        if (row != -1) {
            loginListBox.removeRow(row);
        }
        loginListBox.autoSizeAllColumns();
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        getContest().addLoginListener(new LoginListenerImplementation());

        initializePermissions();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reloadListBox();
            }
        });
    }

    /**
     * Login Listener for use by ServerView.
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    public class LoginListenerImplementation implements ILoginListener {

        public void loginAdded(LoginEvent event) {
            updateLoginList(event.getClientId(), event.getConnectionHandlerID());
        }

        public void loginRemoved(final LoginEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    removeLoginRow(event.getClientId(), event.getConnectionHandlerID());
                }
            });
        }

        public void loginDenied(LoginEvent event) {
            // updateLoginList(event.getClientId(), event.getConnectionHandlerID());
        }
        
        public void loginRefreshAll(LoginEvent event) {
            
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
            
        }
    }

    /**
     * Reset to model existing.
     * 
     */
    protected void undoEdit() {
        reloadListBox();
    }

    public void updateLoginList(final ClientId clientId, final ConnectionHandlerID connectionHandlerID) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateLoginRow(clientId, connectionHandlerID);
            }
        });
    }

    private void initializePermissions() {
        Account account = getContest().getAccount(getContest().getClientId());
        if (account != null) {
            permissionList.clearAndLoadPermissions(account.getPermissionList());
        }
    }

    /**
     * This method initializes logoffButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getLogoffButton() {
        if (logoffButton == null) {
            logoffButton = new JButton();
            logoffButton.setText("Logoff");
            logoffButton.setMnemonic(KeyEvent.VK_L);
            logoffButton.setToolTipText("Logoff the selected user");
            logoffButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    logoffSelectedClient();
                }
            });
        }
        return logoffButton;
    }

    protected void logoffSelectedClient() {
        
        int selected = getLoginListBox().getSelectedIndex();
        
        if (selected == -1){
            showMessage("Please Select Client to logoff");
        }
        
        ClientId clientId = (ClientId) getLoginListBox().getKeys()[selected];
        ConnectionHandlerID connectionHandlerID = getContest().getConnectionHandleID(clientId);
        getController().getLog().info("Send Force logoff "+clientId+" "+connectionHandlerID);
        getController().logoffUser(clientId);
    }

    /**
     * This method initializes messagePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePanel() {
        if (messagePanel == null) {
            messageLabel = new JLabel();
            messageLabel.setText("");
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messagePanel = new JPanel();
            messagePanel.setLayout(new BorderLayout());
            messagePanel.setPreferredSize(new java.awt.Dimension(25, 25));
            messagePanel.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return messagePanel;
    }

    public void showMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(message);
            }
        });
    }

    /**
     * This method initializes reportButton
     * 
     * @return javax.swing.JButton
     */
   private JButton getReportButton() {
        if (reportButton == null) {
            reportButton = new JButton();
            reportButton.setText("Report");
            reportButton.setMnemonic(KeyEvent.VK_R);
            reportButton.setToolTipText("Show Logins Report");
            reportButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Utilities.viewReport(new LoginReport(), "Logins Report", getContest(), getController());
                }
            });
        }
        return reportButton;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
