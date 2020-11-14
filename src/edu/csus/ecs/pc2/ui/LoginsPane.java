// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.HeapSorter;
import com.ibm.webrunner.j2mclb.util.NumericStringComparator;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.ILoginListener;
import edu.csus.ecs.pc2.core.model.LoginEvent;
import edu.csus.ecs.pc2.core.report.LoginReport;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * This pane displays a table of currently logged-in clients.
 * If multiple simultaneous logins are currently allowed then if there are currently
 * multiple logins for a given client (e.g. multiple simultaneous TEAMx logins) then the table
 * will contain a separate entry (row) for each such login.
 * 
 * @author pc2@ecs.csus.edu
 */

public class LoginsPane extends JPanePlugin {

    private static final long serialVersionUID = -3609625972816987570L;

    private JPanel loginButtonPane = null;

    private MCLB loginListBox = null;

    private JButton logoffButton = null;

    private JPanel messagePanel = null;

    private JLabel messageLabel = null;

    private JButton reportButton = null;
    
    private JLabel rowCountLabel = null;
    

     //list of columns in the LoginPane.  The order in this enum defines the column ordinal number.
    private enum COLUMN {
        SITE, TYPE, CLIENT_NUMBER, CONNECTION_ID, SINCE
    };

    // define the column headers.  The order of these names should correspond to the COLUMN enum, above.
    private String[] columnNames = { "Site", "Type", "Number", "Connection Id", "Since" };


    /**
     * Constructs a LoginsPane and initializes it with an empty logins table
     * and a button panel containing "Logoff" and "Report" buttons.
     */
    public LoginsPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this LoginsPane with an empty logins table
     * containing headers with Sorters attached, along with a button panel.
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
     * This method initializes loginButtonPane.
     * 
     * @return javax.swing.JPanel containing "Logoff" and "Report" buttons.
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
     * This method returns a loginListBox (an instance of {@link MCLB}, a MultiColumnListBox),
     * with headers with Sorters attached.  The listbox is initially empty.
     * 
     * @return an (initially empty) edu.csus.ecs.pc2.ui.MCLB.
     * 
     * @see #updateLoginRow(ClientId, ConnectionHandlerID)
     */
    private MCLB getLoginListBox() {
        if (loginListBox == null) {
            loginListBox = new MCLB();

            loginListBox.add(getMessagePanel(), java.awt.BorderLayout.NORTH);
//            Object[] cols = { "Site", "Type", "Number", "Connection Id", "Since" };
//            loginListBox.addColumns(cols);

            loginListBox.addColumns(columnNames);
            
            // Sorters
            HeapSorter sorter = new HeapSorter();
            HeapSorter numericStringSorter = new HeapSorter();
            numericStringSorter.setComparator(new NumericStringComparator());

            // Site
            loginListBox.setColumnSorter(COLUMN.SITE.ordinal(), sorter, 1);

            // Type
            loginListBox.setColumnSorter(COLUMN.TYPE.ordinal(), sorter, 2);

            // Id
            loginListBox.setColumnSorter(COLUMN.CLIENT_NUMBER.ordinal(), numericStringSorter, 3);

            // Connection Handler Id
            loginListBox.setColumnSorter(COLUMN.CONNECTION_ID.ordinal(), sorter, 4);

            // Since
            loginListBox.setColumnSorter(COLUMN.SINCE.ordinal(), sorter, 5);

            loginListBox.autoSizeAllColumns();

        }
        return loginListBox;
    }

    /**
     * Returns an array of Objects intended to comprise a row in the logins table.
     * 
     * @param clientId a ClientId containing the Site, Client Type, and Client Number for the row.
     * @param connectionHandlerID the ConnectionHandlerID for the row.
     * 
     * @return an array of Objects representing a table row.
     */
    private Object[] buildLoginRow(ClientId clientId, ConnectionHandlerID connectionHandlerID) {

        // Object[] cols = { "Site", "Type", "Number", "Connection Id", "Since" };

        Object[] obj = new Object[loginListBox.getColumnCount()];

        obj[COLUMN.SITE.ordinal()] = "Site " + clientId.getSiteNumber();
        obj[COLUMN.TYPE.ordinal()] = clientId.getClientType().toString().toLowerCase();
        obj[COLUMN.CLIENT_NUMBER.ordinal()] = "" + clientId.getClientNumber();
        if (connectionHandlerID != null) {
            obj[COLUMN.CONNECTION_ID.ordinal()] = connectionHandlerID.toString();
//            obj[COLUMN.CONNECTION_ID.ordinal()] = connectionHandlerID;
        } else {
            obj[COLUMN.CONNECTION_ID.ordinal()] = "Undefined";
        }
        obj[COLUMN.SINCE.ordinal()] = new Date().toString();

        return obj;
    }

    /**
     * Return array of all logged in users.
     * The returned array may be empty (that is, have size=0, i.e., no elements)
     * but will never be null.
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

    /**
     * This method removes all entries from the logins listbox (GUI table), then reloads the table
     * with all the currently logged-in clients.  
     * It is invoked when this LoginsPane is first created (more specifically, when the creator
     * calls {@link #setContestAndController(IInternalContest, IInternalController)}), and
     * again whenever the registered {@link LoginListenerImplementation} receives a
     * {@link LoginEvent.Action#REFRESH_ALL} login event.
     */
    private void reloadListBox() {
        loginListBox.removeAllRows();

        //get the clientIds for every currently logged-in client
        ClientId[] clientList = getAllLoggedInUsers();

        //process each client
        for (ClientId clientId : clientList) {
            
            //get a list of all the ConnectionHandlerIDs associated with the current clientId
            List<ConnectionHandlerID> connList = Collections.list(getContest().getConnectionHandlerIDs(clientId));
            
            //add a row for each clientId/ConnectionHandlerID pair (note that a given client might have multiple connections)
            for (ConnectionHandlerID connHID : connList) {
                updateLoginList(clientId, connHID);
            }
        }
    }

    /**
     * Add or update a row in the GUI logins table.
     * If the current logins table already contains a row for the specified clientId/connectionHandlerID pair,
     * this method replaces that row with a new row built from the specified information; 
     * if not, the method adds a new row to the table for the specified clientId.
     * 
     * Note: in an earlier implementation, "clientId" was used as the key for rows in the table.  With the
     * addition of the possibility of multiple logins for a single client (e.g. more than one simultaneous login for Team1), 
     * "clientId" is no longer a sufficiently unique key; it was replaced with a key constructed from the combination
     * of clientid and connectionHandlerID.
     * 
     * @param clientId the Id of the client for which a row is to be added.
     * @param connectionHandlerID the {@link ConnectionHandlerID} associated with the specified clientId.
     * 
     */
    private void updateLoginRow(ClientId clientId, ConnectionHandlerID connectionHandlerID) {
        
        String key = clientId.toString() + connectionHandlerID.toString();
        
        int row = loginListBox.getIndexByKey(key);
        if (row == -1) {
            Object[] objects = buildLoginRow(clientId, connectionHandlerID);
            loginListBox.addRow(objects, key);
        } else {
            Object[] objects = buildLoginRow(clientId, connectionHandlerID);
            loginListBox.replaceRow(objects, row);
        }
        loginListBox.autoSizeAllColumns();
        updateRowCount();
    }

    /**
     * Removes a row from the GUI logins table.
     * 
     * See "Note" at {@link #updateLoginRow(ClientId, ConnectionHandlerID)}.
     * 
     * @param clientId the ClientId to be used in determining the row to be removed.
     * @param connectionHandlerID the ConnectionHandlerID to be used in determining the row to be removed.
     */
    private void removeLoginRow(ClientId clientId, ConnectionHandlerID connectionHandlerID) {

        String key = clientId.toString() + connectionHandlerID.toString();
        
        int row = loginListBox.getIndexByKey(key);
        if (row != -1) {
            loginListBox.removeRow(row);
        }
        loginListBox.autoSizeAllColumns();
        updateRowCount();
    }

    /**
     * This method saves the specified contest and controller for later reference by the
     * methods in this class.  The method also registers a {@link LoginListenerImplementation} with the 
     * contest so that this class will receive callbacks when any login-related events occur, and also
     * initializes the login listbox (table) with all currently logged-in clients.
     */
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        getContest().addLoginListener(new LoginListenerImplementation());

        initializePermissions();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reloadListBox();
                updateGUIperPermissions();
            }
        });
    }
    
    private void updateGUIperPermissions() {
        logoffButton.setVisible(isAllowed(Permission.Type.FORCE_LOGOFF_CLIENT));
        reportButton.setVisible(isAllowed(Permission.Type.FORCE_LOGOFF_CLIENT));
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

    /**
     * This method initializes logoffButton.
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

    /**
     * This method is invoked when the LoginsPane "Logoff" button is clicked;
     * it finds the currently selected row in the Logins table and then 
     * invokes the Controller to force-logoff the client represented in that row.
     */
    protected void logoffSelectedClient() {
        
        int selected = getLoginListBox().getSelectedIndex();
        
        if (selected == -1){
            showMessage("Please Select Client to logoff");
            return;
        }
        
        //login table keys are no longer simply ClientIds; they are now Strings which are the concatenation of
        // clientId.toString() + connectionHandlerID.toString() -- meaning the following statement throws a ClassCastException...
//        ClientId clientId = (ClientId) getLoginListBox().getKeys()[selected];
        
        String key = (String) getLoginListBox().getKeys()[selected];
        Object [] rowData = getLoginListBox().getRowByKey(key);
        ClientId clientId = getClientIdFromRow(rowData);
       
        ConnectionHandlerID connectionHandlerID;
        
        //make sure we got a valid clientId from the table
        if (clientId != null) {
            
            //if the ClientId doesn't already contain a ConnectionHandlerID, find the ConnectionHandlerID object 
            // that matches the string in the table (note that this depends on the 
            // string having been put into the table using ConnectionHandlerID.toString() -- see {@link #buildLoginRow(ClientId, ConnectionHandlerID)}
            connectionHandlerID = null;
            if (clientId.getConnectionHandlerID() == null) {

                //get the ConnectionHandlerID string out of the table row
                String connHIDString = (String) rowData[COLUMN.CONNECTION_ID.ordinal()];

                //search for a ConnectionHandlerID in the list of ConnectionHIDs for the current client
                List<ConnectionHandlerID> connList = Collections.list(getContest().getConnectionHandlerIDs(clientId));
                for (ConnectionHandlerID connHID : connList) {
                    if (connHID.toString().equals(connHIDString)) {
                        clientId.setConnectionHandlerID(connHID);
                        connectionHandlerID = connHID;
                        break;
                    }
                }
            }
            //make sure we found a ConnectionHandlerID for the selected client
            if (connectionHandlerID == null) {
                showMessage("Error: unable to find a ConnectionHandlerID for the selected client.");
                getController().getLog().severe("Unable to find ConnectionHandlerID for client " + clientId);
                return;
            } 
        } else {
            //we didn't get a valid clientId
            showMessage("Error: unable to obtain a valid ClientId from the LoginsPane table.");
            getController().getLog().severe("Unable to obtain ClientId from LoginsPane table.");
            return;
        }
        
        getController().getLog().info("Sending Force logoff to " + clientId  +" @ " + connectionHandlerID);
        getController().logoffUser(clientId);
    }
    
    /**
     * Extracts a ClientId from the specified rowData, which is expected to be an array of Objects matching
     * the content of the LoginsPane display table.
     * 
     * @param rowdata an array of Objects containing the elements of a login row.
     * 
     * @return a ClientId corresponding to the given rowdata, or null if the method is unable to extract a valid ClientId.
     */
    private ClientId getClientIdFromRow(Object [] rowdata) {
        
        ClientId clientId = null ;
        
//        private enum COLUMN {SITE, TYPE, CLIENT_NUMBER, CONNECTION_ID, SINCE};

        try {
            //get the site number out of the table
            int site = Integer.parseInt(((String) rowdata[COLUMN.SITE.ordinal()]).substring(4).trim()); //first four chars of the string are "Site"
            
            //get the client "type" out of the table
            String clientTypeStr = ((String) rowdata[COLUMN.TYPE.ordinal()]).trim().toUpperCase();
            ClientType.Type type = ClientType.Type.valueOf(clientTypeStr);
            
            //get the client "number" out of the table (e.g. for "Team 3" the client number is "3")
            int clientNumber = Integer.parseInt((String) rowdata[COLUMN.CLIENT_NUMBER.ordinal()]);
            
            //construct a ClientId object from the values extracted from the table
            clientId = new ClientId(site, type, clientNumber);
            
        } catch (NumberFormatException e) {
            getController().getLog().severe("NumberFormatException while attempting to retrieve ClientId from LoginsPane table: " + e.getMessage());
        } catch (Exception e) {
            getController().getLog().severe("Exception while attempting to retrieve ClientId from LoginsPane table: " + e.getMessage());            
        }
        
        return clientId;
    }

    /**
     * This method initializes messagePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePanel() {
        if (messagePanel == null) {
            rowCountLabel = new JLabel();
            rowCountLabel.setText("0   ");
            rowCountLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            rowCountLabel.setPreferredSize(new java.awt.Dimension(100,16));
            messageLabel = new JLabel();
            messageLabel.setText("");
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messagePanel = new JPanel();
            messagePanel.setLayout(new BorderLayout());
            messagePanel.setPreferredSize(new java.awt.Dimension(25, 25));
            messagePanel.add(messageLabel, java.awt.BorderLayout.CENTER);
            messagePanel.add(rowCountLabel, java.awt.BorderLayout.EAST);
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
   
   /**
    * Account Listener Implementation.
    * 
    * @author pc2@ecs.csus.edu
    * @version $Id$
    */
   public class AccountListenerImplementation implements IAccountListener {

       public void accountAdded(AccountEvent accountEvent) {
           // ignored
       }

       public void accountModified(AccountEvent accountEvent) {
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
           // ignore
       }

       public void accountsModified(AccountEvent accountEvent) {
           Account[] accounts = accountEvent.getAccounts();
           for (Account account : accounts) {

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
       }

       public void accountsRefreshAll(AccountEvent accountEvent) {

           initializePermissions();

           SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                   updateGUIperPermissions();
               }
           });
       }
   }
   
   /**
    * This updates the rowCountlabel & toolTipText. It should be called only while on the swing thread.
    */
   private void updateRowCount() {
           rowCountLabel.setText(Integer.toString(loginListBox.getRowCount())+"  ");
           rowCountLabel.setToolTipText(loginListBox.getRowCount() + " logins ");
   }

} // @jve:decl-index=0:visual-constraint="10,10"
