package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.HeapSorter;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ConnectionEvent;
import edu.csus.ecs.pc2.core.model.IConnectionListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * View Connections.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ConnectionsPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -5590899142044283529L;

    private JPanel loginButtonPane = null;

    private MCLB connectionsListBox = null;

    private JButton disconnectButton = null;

    private JPanel messagePane = null;

    private JLabel messageLabel = null;
    
    private Log log = null;

    /**
     * This method initializes
     * 
     */
    public ConnectionsPane() {
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
        this.add(getConnectionsListBox(), java.awt.BorderLayout.CENTER);

        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
    }

    @Override
    public String getPluginTitle() {
        return "Connections Panel";
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
     * This method initializes loginListBox
     * 
     * @return edu.csus.ecs.pc2.core.log.MCLB
     */
    private MCLB getConnectionsListBox() {
        if (connectionsListBox == null) {
            connectionsListBox = new MCLB();
            connectionsListBox.setMultipleSelections(true);

            Object[] cols = { "Connection" };

            connectionsListBox.addColumns(cols);

            // Sorters
            HeapSorter sorter = new HeapSorter();
            // HeapSorter numericStringSorter = new HeapSorter();
            // numericStringSorter.setComparator(new NumericStringComparator());

            // Connection
            connectionsListBox.setColumnSorter(0, sorter, 1);

            connectionsListBox.autoSizeAllColumns();

        }
        return connectionsListBox;
    }

    private Object[] buildConnectionRow(ConnectionHandlerID connectionHandlerID) {

        // Object[] cols = { "Site", "Type", "Number", "Connection Id", "Since" };

        Object[] obj = new Object[connectionsListBox.getColumnCount()];

        obj[0] = connectionHandlerID.toString();

        return obj;
    }

    private void reloadListBox() {
        connectionsListBox.removeAllRows();

        ConnectionHandlerID[] connectionHandlerIDs = getContest().getConnectionHandleIDs();

        for (ConnectionHandlerID connectionHandlerID : connectionHandlerIDs) {
            updateConnectionRow(connectionHandlerID);
        }
    }

    /**
     * Add or update a login row
     * 
     * @param login
     */
    private void updateConnectionRow(final ConnectionHandlerID connectionHandlerID) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int row = connectionsListBox.getIndexByKey(connectionHandlerID);
                if (row == -1) {
                    Object[] objects = buildConnectionRow(connectionHandlerID);
                    connectionsListBox.addRow(objects, connectionHandlerID);
                } else {
                    Object[] objects = buildConnectionRow(connectionHandlerID);
                    connectionsListBox.replaceRow(objects, row);
                }
                connectionsListBox.autoSizeAllColumns();
            }
        });
    }

    private void removeConnectionRow(final ConnectionHandlerID connectionHandlerID) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int row = connectionsListBox.getIndexByKey(connectionHandlerID);
                if (row != -1) {
                    connectionsListBox.removeRow(row);
                }
                connectionsListBox.autoSizeAllColumns();
            }
        });
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        getContest().addConnectionListener(new ConnectionListenerImplementation());
        
        log = getController().getLog();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reloadListBox();
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

    protected void logoffConnection() {

        int[] selectedIndex = connectionsListBox.getSelectedIndexes();

        if (selectedIndex.length == 0) {
            showMessage("Please select a connection");
            return;
        }

        int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), "Disconnect " + selectedIndex.length + " connections?", "Confirm Disconnect");

        if (result == JOptionPane.YES_OPTION) {
            for (int i : selectedIndex) {
                ConnectionHandlerID connectionHandlerID = (ConnectionHandlerID) connectionsListBox.getKeys()[i];
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

} // @jve:decl-index=0:visual-constraint="10,10"
