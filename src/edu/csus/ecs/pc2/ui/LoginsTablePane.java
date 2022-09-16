// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.StringToNumberComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
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

public class LoginsTablePane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 3066343394034827198L;

    private JPanel loginButtonPane = null;

    private static final int VERT_PAD = 2;
    private static final int HORZ_PAD = 20;
    
    private JTableCustomized loginsTable = null;
    private DefaultTableModel loginsTableModel = null;

    private JButton logoffButton = null;

    private JPanel messagePanel = null;

    private JLabel messageLabel = null;

    private JButton reportButton = null;
    
    private JLabel rowCountLabel = null;
    
    private JScrollPane scrollPane = null;

     //list of columns in the LoginPane.  The order in this enum defines the column ordinal number.
    private enum COLUMN {
        SITE, TYPE, CLIENT_NUMBER, CONNECTION_ID, SINCE, CLIENT_ID, CONNECTIONHANDLER_ID
    };

    // define the column headers.  The order of these names should correspond to the COLUMN enum, above.
    private Object [] columnNames = { "Site", "Type", "Number", "Connection Id", "Since", "ClientID", "ConnectionHandlerID" };


    /**
     * Constructs a LoginsPane and initializes it with an empty logins table
     * and a button panel containing "Logoff" and "Report" buttons.
     */
    public LoginsTablePane() {
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
        this.add(getMessagePanel(), java.awt.BorderLayout.NORTH);
        this.add(getLoginButtonPanel(), java.awt.BorderLayout.SOUTH);
        this.add(getScrollPane(), java.awt.BorderLayout.CENTER);

    }

    @Override
    public String getPluginTitle() {
        return "Logins Table Panel";
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
     * This method initializes the scrollPane for the connections table
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane(getLoginsTable());
        }
        return scrollPane;
    }
    
    private void resizeColumnWidth(JTableCustomized table) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                TableColumnAdjuster tca = new TableColumnAdjuster(table, HORZ_PAD);
                tca.adjustColumns();
            }
        });
    }
    /**
     * This method initializes the connectionsTable
     * 
     * @return JTableCustomized
     */
    private JTableCustomized getLoginsTable() {
        if (loginsTable == null) {
            loginsTableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };
            TableRowSorter<DefaultTableModel> trs = new TableRowSorter<DefaultTableModel>(loginsTableModel);
            
            loginsTable = new JTableCustomized(loginsTableModel);
            /*
             * Remove ClientId and ConnectionID from display - this does not remove the columns, it merely makes it invisible
             * These are the "keys" / unique ID column
             */
            TableColumnModel tcm = loginsTable.getColumnModel();
            tcm.removeColumn(tcm.getColumn(columnNames.length - 1));
            tcm.removeColumn(tcm.getColumn(columnNames.length - 2));
            
            loginsTable.setRowSorter(trs);
            loginsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            
            ArrayList<SortKey> sortList = new ArrayList<SortKey>();
            sortList.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
            trs.setSortKeys(sortList);


            /*
             * Column headers left justified with a tad of pad
             */
            ((DefaultTableCellRenderer)loginsTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
            loginsTable.setRowHeight(loginsTable.getRowHeight() + VERT_PAD);
            
            // Set special compare routines here... This is the Client Number 
            trs.setComparator(2, new StringToNumberComparator());
           
            resizeColumnWidth(loginsTable);
        }
        return loginsTable;
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

        // Object[] cols = { "Site", "Type", "Number", "Connection Id", "Since", [ConnectionHandlerID] };
        try {
            int nCols = loginsTableModel.getColumnCount();
            Object[] obj = new Object[nCols];
    
            obj[COLUMN.SITE.ordinal()] = "Site " + clientId.getSiteNumber();
            obj[COLUMN.TYPE.ordinal()] = clientId.getClientType().toString().toLowerCase();
            obj[COLUMN.CLIENT_NUMBER.ordinal()] = "" + clientId.getClientNumber();
            obj[COLUMN.CONNECTION_ID.ordinal()] = connectionHandlerID.toString();
            obj[COLUMN.SINCE.ordinal()] = new Date().toString();
            obj[COLUMN.CLIENT_ID.ordinal()] = clientId;
            obj[COLUMN.CONNECTIONHANDLER_ID.ordinal()] = connectionHandlerID;
            return obj;
        } catch (Exception exception) {
            StaticLog.getLog().log(Log.INFO, "Exception in buildLoginRow()", exception);
        }
        return null;
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
     * It is invoked when this LoginsTablePane is first created (more specifically, when the creator
     * calls {@link #setContestAndController(IInternalContest, IInternalController)}), and
     * again whenever the registered {@link LoginListenerImplementation} receives a
     * {@link LoginEvent.Action#REFRESH_ALL} login event.
     */
    private void reloadLoginTable() {
 
        //get the clientIds for every currently logged-in client
        ClientId[] clientList = getAllLoggedInUsers();

        //process each client
        for (ClientId clientId : clientList) {
            
            //get a list of all the ConnectionHandlerIDs associated with the current clientId
            List<ConnectionHandlerID> connList = Collections.list(getContest().getConnectionHandlerIDs(clientId));
            
            //add a row for each clientId/ConnectionHandlerID pair (note that a given client might have multiple connections)
            for (ConnectionHandlerID connHID : connList) {
                updateLoginRow(clientId, connHID);
            }
        }
    }
   
    /**
     * Find row that contains the supplied key (in last column)
     * @param value - unique key - really, the ElementId of run
     * @return index of row, or -1 if not found
     */
    private int getRowByKey(Object clientid, Object connectionid) {
        Object o;
        
        if(loginsTableModel != null) {
            int col_conn = loginsTableModel.getColumnCount() - 1;
            int col_clt = col_conn-1;
            for (int i = loginsTableModel.getRowCount() - 1; i >= 0; --i) {
                o = loginsTableModel.getValueAt(i, col_conn);
                if (o != null && o.equals(connectionid)) {
                    o = loginsTableModel.getValueAt(i,  col_clt);
                    if(o != null && o.equals(clientid)) {
                        return i;
                    }
                }
            }
        }
        return(-1);
    }

    /**
     * Add or update a login row
     * 
     * @param login
     */
    private void updateLoginRow(ClientId clientId, ConnectionHandlerID connectionHandlerID) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Object[] objects = buildLoginRow(clientId, connectionHandlerID);
                int rowNumber = getRowByKey(clientId, connectionHandlerID);
                if (rowNumber == -1) {
                    loginsTableModel.addRow(objects);
                } else {
                    for(int j = loginsTableModel.getColumnCount()-1; j >= 0; j--) {
                        loginsTableModel.setValueAt(objects[j], rowNumber, j);
                    }
                }
                resizeColumnWidth(loginsTable);
                updateRowCount();
            }
        });
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
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int rowNumber = getRowByKey(clientId, connectionHandlerID);
                if (rowNumber != -1) {
                    loginsTableModel.removeRow(rowNumber);
                    resizeColumnWidth(loginsTable);
                }
                updateRowCount();
            }
        });
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
                reloadLoginTable();
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
            updateLoginRow(event.getClientId(), event.getConnectionHandlerID());
        }

        public void loginRemoved(final LoginEvent event) {
            removeLoginRow(event.getClientId(), event.getConnectionHandlerID());
        }

        public void loginDenied(LoginEvent event) {
            // updateLoginRow(event.getClientId(), event.getConnectionHandlerID());
        }
        
        public void loginRefreshAll(LoginEvent event) {
            
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadLoginTable();
                }
            });
            
        }
    }

    /**
     * Reset to model existing.
     * 
     */
    protected void undoEdit() {
        reloadLoginTable();
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
     * Looks up the unique connection ID for the item at the supplied table row.
     * Have to map the row to the underlying tablemodel data first.
     * The ElementID is stored in the last (invisible) column, in most cases.
     * 
     * @param nRow - selected row
     */
    public ConnectionHandlerID getConnectionHandlerIdFromTableRow(int nRow) {
        int modelIndex = loginsTable.convertRowIndexToModel(nRow);
        TableModel tm = loginsTable.getModel();
        ConnectionHandlerID connId = (ConnectionHandlerID) tm.getValueAt(modelIndex,  tm.getColumnCount()-1);
        return(connId);
    }

    /**
     * Looks up the unique client ID for the item at the supplied table row.
     * Have to map the row to the underlying tablemodel data first.
     * The ElementID is stored in the 2nd to last (invisible) column, in most cases.
     * 
     * @param nRow - selected row
     */
    public ClientId getClientIdFromTableRow(int nRow) {
        int modelIndex = loginsTable.convertRowIndexToModel(nRow);
        TableModel tm = loginsTable.getModel();
        ClientId clientId = (ClientId) tm.getValueAt(modelIndex,  tm.getColumnCount()-2);
        return(clientId);
    }

    /**
     * This method is invoked when the LoginsPane "Logoff" button is clicked;
     * it finds the currently selected row in the Logins table and then 
     * invokes the Controller to force-logoff the client represented in that row.
     */
    protected void logoffSelectedClient() {
        
        int[] selectedIndexes = loginsTable.getSelectedRows();
        int nSel = selectedIndexes.length;
        
        if (nSel == 0 || nSel > 1){
            showMessage("Please Select a single Client to logoff");
            return;
        }
        int selected = selectedIndexes[0];
       
        //login table keys are no longer simply ClientIds; they are both the client id and connection id
        ClientId clientId = getClientIdFromTableRow(selected);
        ConnectionHandlerID connectionHandlerID = getConnectionHandlerIdFromTableRow(selected);
        
        //make sure we got a valid clientId from the table 
        if (clientId != null) {
            //make sure we found a ConnectionHandlerID for the selected client
            // JB - It's unclear to me how this can happen...
            if (connectionHandlerID == null) {
                showMessage("Error: unable to find a ConnectionHandlerID for the selected client.");
                getController().getLog().severe("Unable to find ConnectionHandlerID for client " + clientId);
                return;
            } 
            // JB - Or how this can happen...  Original code had it in there, so we'll leave it for now.
            if (clientId.getConnectionHandlerID() == null) {
                clientId.setConnectionHandlerID(connectionHandlerID);
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
       int nLogins = loginsTableModel.getRowCount();
       rowCountLabel.setText(Integer.toString(nLogins) + "  ");
       rowCountLabel.setToolTipText(nLogins + " logins ");
   }

} // @jve:decl-index=0:visual-constraint="10,10"
