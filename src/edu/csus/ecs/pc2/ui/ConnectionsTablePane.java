// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ConnectionEvent;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IConnectionListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * View Connections.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ConnectionsTablePane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 7773025675128875572L;

    private JPanel loginButtonPane = null;
    
    private static final int VERT_PAD = 2;
    private static final int HORZ_PAD = 20;
    
    private JTableCustomized connectionsTable = null;
    private DefaultTableModel connectionsTableModel = null;

    private JButton disconnectButton = null;

    private JPanel messagePane = null;

    private JLabel messageLabel = null;
    
    private JScrollPane scrollPane = null;
    
    private Log log = null;

    /**
     * This method initializes
     * 
     */
    public ConnectionsTablePane() {
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
        this.add(getConnectionButtonPanel(), java.awt.BorderLayout.SOUTH);
        this.add(getScrollPane(), java.awt.BorderLayout.CENTER);

        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
    }

    @Override
    public String getPluginTitle() {
        return "Connections Table Panel";
    }

    /**
     * This method initializes loginButtonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getConnectionButtonPanel() {
        if (loginButtonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(15);
            flowLayout.setVgap(5);
            loginButtonPane = new JPanel();
            loginButtonPane.setLayout(flowLayout);
            loginButtonPane.setPreferredSize(new java.awt.Dimension(35, 35));
            loginButtonPane.add(getDisconnectButton(), null);
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
            scrollPane = new JScrollPane(getConnectionsTable());
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
     * This method initializes loginListBox
     * 
     * @return JTableCustomized
     */
    private JTableCustomized getConnectionsTable() {
        if (connectionsTable == null) {
            Object[] cols = { "Connection", "ConnectionHandlerID" };
            connectionsTableModel = new DefaultTableModel(cols, 0) {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };
            TableRowSorter<DefaultTableModel> trs = new TableRowSorter<DefaultTableModel>(connectionsTableModel);
            
            connectionsTable = new JTableCustomized(connectionsTableModel);
            /*
             * Remove ElementId from display - this does not remove the column, it merely makes it invisible
             * This is the "key" / unique ID column
             */
            TableColumnModel tcm = connectionsTable.getColumnModel();
            tcm.removeColumn(tcm.getColumn(cols.length - 1));
            
            connectionsTable.setRowSorter(trs);
            connectionsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            
            ArrayList<SortKey> sortList = new ArrayList<SortKey>();
            sortList.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
            trs.setSortKeys(sortList);


            /*
             * Column headers left justified with a tad of pad
             */
            ((DefaultTableCellRenderer)connectionsTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
            connectionsTable.setRowHeight(connectionsTable.getRowHeight() + VERT_PAD);
            
            // Set special compare routines here, if any.  I left an example...
            // trs.setComparator(0, new StringToNumberComparator());
           
            resizeColumnWidth(connectionsTable);
        }
        return connectionsTable;
    }

    private Object[] buildConnectionRow(ConnectionHandlerID connectionHandlerID) {

        // Object[] cols = { "Connection", ["ConnectionHandlerId"] };
        try {
            int cols = connectionsTableModel.getColumnCount();
            Object[] s = new Object[cols];

            s[0] = connectionHandlerID.toString();
            // This column is invisible for the "unique" ID
            s[1] = connectionHandlerID;
            return s;
        } catch (Exception exception) {
            StaticLog.getLog().log(Log.INFO, "Exception in buildConnectionRow()", exception);
            
        }
        return null;
    }

    private void reloadConnectionTable() {
        ConnectionHandlerID[] connectionHandlerIDs = getContest().getConnectionHandleIDs();

        for (ConnectionHandlerID connectionHandlerID : connectionHandlerIDs) {
            updateConnectionRow(connectionHandlerID);
        }
    }
    
    /**
     * Find row that contains the supplied key (in last column)
     * @param value - unique key - really, the ElementId of run
     * @return index of row, or -1 if not found
     */
    private int getRowByKey(Object value) {
        Object o;
        
        if(connectionsTableModel != null) {
            int col = connectionsTableModel.getColumnCount() - 1;
            for (int i = connectionsTableModel.getRowCount() - 1; i >= 0; --i) {
                o = connectionsTableModel.getValueAt(i, col);
                if (o != null && o.equals(value)) {
                    return i;
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
    private void updateConnectionRow(final ConnectionHandlerID connectionHandlerID) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Object[] objects = buildConnectionRow(connectionHandlerID);
                int rowNumber = getRowByKey(connectionHandlerID);
                if (rowNumber == -1) {
                    connectionsTableModel.addRow(objects);
                } else {
                    for(int j = connectionsTableModel.getColumnCount()-1; j >= 0; j--) {
                        connectionsTableModel.setValueAt(objects[j], rowNumber, j);
                    }
                }
                resizeColumnWidth(connectionsTable);            }
        });
    }

    private void removeConnectionRow(final ConnectionHandlerID connectionHandlerID) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int rowNumber = getRowByKey(connectionHandlerID);
                if (rowNumber != -1) {
                    connectionsTableModel.removeRow(rowNumber);
                    resizeColumnWidth(connectionsTable);
                }
            }
        });
    }
    
    private void updateGUIperPermissions() {
        if(connectionsTable != null) {
            connectionsTable.setVisible(isAllowed(Permission.Type.FORCE_LOGOFF_CLIENT));
        }
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        getContest().addConnectionListener(new ConnectionListenerImplementation());
        getContest().addAccountListener(new AccountListenerImplementation());
           
        log = getController().getLog();
        
        initializePermissions();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateGUIperPermissions();
                reloadConnectionTable();
            }
        });
    }

    /**
     * Connection Listener for use by ServerView.
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    public class ConnectionListenerImplementation implements IConnectionListener {

        public void connectionEstablished(ConnectionEvent event) {
            updateConnectionRow(event.getConnectionHandlerID());
        }

        public void connectionDropped(final ConnectionEvent event) {
            removeConnectionRow(event.getConnectionHandlerID());
        }

        public void connectionRefreshAll(ConnectionEvent connectionEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadConnectionTable();
                }
            });
        }
        
        
    }

    /**
     * Reset to model existing.
     * 
     */
    protected void undoEdit() {
        reloadConnectionTable();
    }

    /**
     * This method initializes logoffButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getDisconnectButton() {
        if (disconnectButton == null) {
            disconnectButton = new JButton();
            disconnectButton.setText("Disconnect");
            disconnectButton.setToolTipText("Disconnect the selected connection");
            disconnectButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    logoffConnection();
                }
            });
        }
        return disconnectButton;
    }

    /**
     * Looks up the unique connection ID for the item at the supplied table row.
     * Have to map the row to the underlying tablemodel data first.
     * The ElementID is stored in the last (invisible) column, in most cases.
     * 
     * @param nRow - selected row
     */
    public ConnectionHandlerID getConnectionIdFromTableRow(int nRow) {
        int modelIndex = connectionsTable.convertRowIndexToModel(nRow);
        TableModel tm = connectionsTable.getModel();
        ConnectionHandlerID connId = (ConnectionHandlerID) tm.getValueAt(modelIndex,  tm.getColumnCount()-1);
        return(connId);
    }

    protected void logoffConnection() {

        int[] selectedIndexes = connectionsTable.getSelectedRows();
        int nConn = selectedIndexes.length;
        
        if (nConn == 0) {
            showMessage("Please select a connection");
            return;
        }

        int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), "Disconnect " + nConn +
                " connection" + (nConn == 1 ? "?" : "s?"), "Confirm Disconnect");

        if (result == JOptionPane.YES_OPTION) {
            for (int i : selectedIndexes) {
                ConnectionHandlerID connectionHandlerID = (ConnectionHandlerID) getConnectionIdFromTableRow(i);
                if (connectionHandlerID != null) {
                    try {
                        getController().forceConnectionDrop(connectionHandlerID);    
                    } catch (Exception e) {
                        log.log(Log.WARNING, "Unable to disconnect connection " + connectionHandlerID, e);
                        showMessage("Connection may not be dropped: " + connectionHandlerID + " check log");
                    }
                    
                }
            }
        }

    }

    private void showMessage(final String string) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(string);
            }
        });
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePane() {
        if (messagePane == null) {
            messageLabel = new JLabel();
            messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            messageLabel.setText("");
            messagePane = new JPanel();
            messagePane.setLayout(new BorderLayout());
            messagePane.setPreferredSize(new Dimension(25, 25));
            messagePane.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return messagePane;
    }
    
    /**
     * Account Listener
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
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

} // @jve:decl-index=0:visual-constraint="10,10"
