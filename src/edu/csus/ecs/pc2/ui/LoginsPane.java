package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.HeapSorter;
import com.ibm.webrunner.j2mclb.util.NumericStringComparator;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ILoginListener;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.LoginEvent;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * View Logins.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL${date}
public class LoginsPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -5590899142044283529L;

    private JPanel loginButtonPane = null;

    private MCLB loginListBox = null;

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
            flowLayout.setHgap(15);
            flowLayout.setVgap(5);
            loginButtonPane = new JPanel();
            loginButtonPane.setLayout(flowLayout);
            loginButtonPane.setPreferredSize(new java.awt.Dimension(35, 35));
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

//        Object[] cols = { "Site", "Type", "Number", "Connection Id", "Since" };

        Object[] obj = new Object[loginListBox.getColumnCount()];

        obj[0] = "Site " + clientId.getSiteNumber();
        obj[1] = clientId.getClientType().toString().toLowerCase();
        obj[2] = "" + clientId.getClientNumber();
        if (connectionHandlerID != null){
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
    private ClientId [] getAllLoggedInUsers() {

        Vector<ClientId> allAccounts = new Vector<ClientId>();

        for (ClientType.Type ctype : ClientType.Type.values()) {
            Enumeration<ClientId> enumeration = getModel().getLoggedInClients(ctype);
            while (enumeration.hasMoreElements()) {
                ClientId clientId = (ClientId) enumeration.nextElement();
                allAccounts.addElement(clientId);
            }
        }

        ClientId[] list = (ClientId[]) allAccounts.toArray(new ClientId[allAccounts.size()]);
        return list;
    }

    private void reloadListBox() {
        loginListBox.removeAllRows();
        
        ClientId [] clientList = getAllLoggedInUsers();

        for (ClientId clientId : clientList){
            ConnectionHandlerID connectionHandlerID = getModel().getConnectionHandleID(clientId);
            updateLoginList(clientId, connectionHandlerID);
        }
    }

    /**
     * Add or update a login row
     * @param login
     */
    private void updateLoginRow (ClientId clientId, ConnectionHandlerID connectionHandlerID) {
        int row = loginListBox.getIndexByKey(clientId);
        if (row == -1){
            Object [] objects = buildLoginRow(clientId, connectionHandlerID);
            loginListBox.addRow(objects, clientId);
        }else {
            Object [] objects = buildLoginRow(clientId, connectionHandlerID);
            loginListBox.replaceRow(objects, row);
        }
        loginListBox.autoSizeAllColumns();
    }
    
    private void removeLoginRow (ClientId clientId, ConnectionHandlerID connectionHandlerID) {
        int row = loginListBox.getIndexByKey(clientId);
        if (row != -1){
            loginListBox.remove(row);
        }
        loginListBox.autoSizeAllColumns();
    }

    public void setModelAndController(IModel inModel, IController inController) {
        super.setModelAndController(inModel, inController);
        
        getModel().addLoginListener(new LoginListenerImplementation());
        
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
//            updateLoginList(event.getClientId(), event.getConnectionHandlerID());
        }}
    
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

} // @jve:decl-index=0:visual-constraint="10,10"
