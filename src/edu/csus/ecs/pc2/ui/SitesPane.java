package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.SiteComparatorBySiteNumber;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.ISiteListener;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.model.SiteEvent;
import edu.csus.ecs.pc2.core.model.SiteList;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.Permission.Type;

/**
 * View/Edit sites' settings.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL${date}
public class SitesPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 8567214090568964720L;

    private JPanel siteButtonPanel = null;

    private MCLB siteListBox = null;

    private JButton addSiteButton = null;

    private JButton updateSiteButton = null;

    private JButton cancelSiteEditButton = null;

    private SiteList siteList = new SiteList();

    private JPanel messagePane = null;

    private JLabel messageLabel = null;

    private JButton reconnectButton = null;
    
    public static final int DEFAULT_LISTENING_PORT = 50002;
    
    private JButton shutdownButton = null;

    /**
     * This method initializes
     * 
     */
    public SitesPane() {
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
        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getSiteButtonPanel(), java.awt.BorderLayout.SOUTH);
        this.add(getSiteListBox(), java.awt.BorderLayout.CENTER);

    }

    @Override
    public String getPluginTitle() {
        return "Sites Panel";
    }

    /**
     * This method initializes siteButtonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getSiteButtonPanel() {
        if (siteButtonPanel == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(15);
            flowLayout.setVgap(5);
            siteButtonPanel = new JPanel();
            siteButtonPanel.setLayout(flowLayout);
            siteButtonPanel.setPreferredSize(new java.awt.Dimension(35, 35));
            siteButtonPanel.add(getAddSiteButton(), null);
            siteButtonPanel.add(getUpdateSiteButton(), null);
            siteButtonPanel.add(getCancelSiteEditButton(), null);
            siteButtonPanel.add(getReconnectButton(), null);
            siteButtonPanel.add(getShutdownButton(), null);
            siteButtonPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.isShiftDown()) {
                        addNewTestSite();
                    }
                }
            });

        }
        return siteButtonPanel;
    }

    /**
     * This method initializes siteListBox
     * 
     * @return edu.csus.ecs.pc2.core.log.MCLB
     */
    private MCLB getSiteListBox() {
        if (siteListBox == null) {
            siteListBox = new MCLB();

            Object[] cols = { "Site Number", "Site Title", "Password", "IP", "Port" };

            siteListBox.addColumns(cols);

            /**
             * No sorters for this frame, keep it in order. If you want sorters, be sure to change update methods.
             */
            // // Sorters
            // HeapSorter sorter = new HeapSorter();
            // HeapSorter numericStringSorter = new HeapSorter();
            // numericStringSorter.setComparator(new NumericStringComparator());
            //
            // // Site Number
            // siteListBox.setColumnSorter(0, numericStringSorter, 1);
            //
            // // Site Title
            // siteListBox.setColumnSorter(1, sorter, 2);
            //
            // // Password
            // siteListBox.setColumnSorter(2, sorter, 3);
            //
            // // IP
            // siteListBox.setColumnSorter(3, sorter, 4);
            //
            // // Port
            // siteListBox.setColumnSorter(4, sorter, 5);
            siteListBox.autoSizeAllColumns();

        }
        return siteListBox;
    }

    /**
     * This method initializes addSiteButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddSiteButton() {
        if (addSiteButton == null) {
            addSiteButton = new JButton();
            addSiteButton.setText("Add Site");
            addSiteButton.setMnemonic(KeyEvent.VK_A);
            addSiteButton.setToolTipText("Add a new Site");
            addSiteButton.setText("Add Site");
            addSiteButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addNewSite();
                }
            });
        }
        return addSiteButton;
    }

    /**
     * Add new site with localhost and incremented listening port.
     * 
     * Listening port = <code> {@link #DEFAULT_LISTENING_PORT} + (siteNum - 1) * 1000</code>
     * 
     */
    protected void addNewTestSite() {
        int nextSiteNumber = siteListBox.getRowCount() + 1;
        int port = DEFAULT_LISTENING_PORT + (nextSiteNumber - 1) * 1000;
        Site newSite = createSite(nextSiteNumber, "localhost", port);
        addSiteRow(newSite);
        enableUpdateButtons(true);
    }
    
    /**
     * Add new site with no host and default listening port.
     */
    protected void addNewSite() {
        int nextSiteNumber = siteListBox.getRowCount() + 1;
        int port = DEFAULT_LISTENING_PORT;
        Site newSite = createSite(nextSiteNumber, "", port);
        addSiteRow(newSite);
        enableUpdateButtons(true);
    }


    /**
     * This method initializes updateSiteButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getUpdateSiteButton() {
        if (updateSiteButton == null) {
            updateSiteButton = new JButton();
            updateSiteButton.setText("Apply");
            updateSiteButton.setMnemonic(KeyEvent.VK_P);
            updateSiteButton.setToolTipText("Apply all site changes");
            updateSiteButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    updateSitesData();
                }
            });
            updateSiteButton.setText("Update Site");
        }
        return updateSiteButton;
    }

    protected void updateSitesData() {

        showMessage("");
        String message = validateSiteListBox();

        if (message == null) {

            Site[] sites = getContest().getSites();

            for (int i = 0; i < siteListBox.getRowCount(); i++) {

                Site newSite = createSiteFromRow(i);
                if (i >= sites.length) {
                    getController().addNewSite(newSite);
                } else {
                    if (!newSite.isSameAs(getContest().getSite(newSite.getSiteNumber()))) {
                        getController().updateSite(newSite);
                    }
                }
            }
            enableUpdateButtons(false);
        } else {
            showMessage(message);
        }
    }

    /**
     * Return true if input string contains a valid port number.
     * 
     * @param portString
     * @return true if valid port (1..65535) else false
     */
    private boolean validPort(String portString) {
        int portNum = 0;
        try {
            portNum = Integer.parseInt(portString);
        } catch (Exception e) {
            StaticLog.getLog().log(Log.DEBUG, "validPort: ", e);
            return false;
        }

        return portNum > 0 && portNum < 65536;
    }

    /**
     * Checks for invalid site settings.
     * 
     * Checks for: <br>
     * Duplicate passwords <br>
     * Duplicate site names <br>
     * Duplicate IP and port combinations.<br>
     * 
     * @param inSiteList
     * @return null if passes all validations, otherwire returns a nice error message.
     */
    private String validateSites(SiteList inSiteList) {
        if (inSiteList.size() > 1) {
            Site[] sites = inSiteList.getList();
            for (int s1 = 0; s1 < sites.length - 1; s1++) {
                Site site1 = sites[s1];
                for (int s2 = s1 + 1; s2 < sites.length; s2++) {

                    Site site2 = sites[s2];
                    if (site1.getPassword().equals(site2.getPassword())) {
                        return ("Duplicate passwords not allowed, " + site1.getPassword() + ", for " + site1 + " and " + site2);
                    }
                    if (site1.toString().equals(site2.toString())) {
                        return ("Duplicate site names not allowed, name is: " + site1);
                    }

                    String ip1 = site1.getConnectionInfo().getProperty(Site.IP_KEY);
                    String port1 = site1.getConnectionInfo().getProperty(Site.PORT_KEY);

                    String ip2 = site2.getConnectionInfo().getProperty(Site.IP_KEY);
                    String port2 = site2.getConnectionInfo().getProperty(Site.PORT_KEY);

                    if (ip1 == null || ip1.trim().length() == 0) {
                        return ("Please enter an IP for Site " + site1);
                    }
                    if (port1 == null || port1.trim().length() == 0) {
                        return ("Please enter a port number for: " + site1);
                    }

                    if (!validPort(port1)) {
                        return ("Invalid port, must be numeric, " + port1 + ") for: " + site1);
                    }

                    if (ip2 == null || ip2.trim().length() == 0) {
                        return ("Please enter an IP for Site " + site2);
                    }
                    if (port2 == null || port2.trim().length() == 0) {
                        return ("Please enter a port number for: " + site2);
                    }
                    if (!validPort(port2)) {
                        return ("Invalid port, must be numeric, " + port2 + ") for: " + site2);
                    }

                    String conInfo1 = ip1 + ":" + port1;
                    String conInfo2 = ip2 + ":" + port2;

                    if (conInfo1.trim().equals(conInfo2.trim())) {
                        return "Duplicate IP and port values are not allowed, for " + site1 + " and " + site2;
                    }
                }
            }
        }
        return null;
    }

    /**
     * validates site list box, return message.
     * 
     * @return null if valid, error message string if a problem.
     */
    private String validateSiteListBox() {

        SiteList siteListToValidate = new SiteList();

        for (int i = 0; i < siteListBox.getRowCount(); i++) {
            Site newSite = createSiteFromRow(i);
            siteListToValidate.add(newSite);
        }

        return validateSites(siteListToValidate);
    }

    /**
     * Create a Site from hst site list box.
     * 
     * @param i -
     *            index in the list box.
     * @return
     */
    private Site createSiteFromRow(int i) {

        Object[] objects = siteListBox.getRow(i);

        // Object[] cols = { "Site Number", "Site Title", "Password", "IP", "Port" };
        // Integer siteNumberInteger = ((Integer) objects[0]);
        String siteTitle = ((JTextField) objects[1]).getText();
        String password = ((JTextField) objects[2]).getText();
        String hostName = ((JTextField) objects[3]).getText();
        String portString = ((JTextField) objects[4]).getText();
        int port = Integer.parseInt(portString);

        ElementId siteId = (ElementId) siteListBox.getKeys()[i];
        Site site = (Site) siteList.get(siteId);
        site.setDisplayName(siteTitle);
        site.setPassword(password);

        Properties props = new Properties();
        props.put(Site.IP_KEY, hostName);
        props.put(Site.PORT_KEY, "" + port);
        site.setConnectionInfo(props);

        return site;
    }

    /**
     * This method initializes cancelSiteEditButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCancelSiteEditButton() {
        if (cancelSiteEditButton == null) {
            cancelSiteEditButton = new JButton();
            cancelSiteEditButton.setText("Cancel");
            cancelSiteEditButton.setMnemonic(KeyEvent.VK_C);
            cancelSiteEditButton.setToolTipText("Undo changes");
            cancelSiteEditButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    undoEdit();
                }
            });
        }
        return cancelSiteEditButton;
    }

    private Object[] buildSiteRow(Site site) {

        // Object[] cols = { "Site Number", "Site Title", "Password", "IP", "Port" };

        Object[] obj = new Object[siteListBox.getColumnCount()];

        obj[0] = site.getSiteNumber();
        obj[1] = createJTextField(site.toString(), false);
        obj[2] = createJTextField(site.getPassword(), false);
        String hostName = site.getConnectionInfo().getProperty(Site.IP_KEY);
        obj[3] = createJTextField(hostName, false);
        String port = site.getConnectionInfo().getProperty(Site.PORT_KEY);
        JTextField textField = createJTextField(port, false);
        textField.setDocument(new IntegerDocument());
        textField.setText(port); // had to re-add port because IntegerDocument cleared it out.
        obj[4] = textField;

        return obj;
    }

    private void reloadListBox() {
        siteListBox.removeAllRows();
        siteList = new SiteList();

        Site[] sites = getContest().getSites();
        Arrays.sort(sites, new SiteComparatorBySiteNumber());

        for (Site site : sites) {
            Site siteClone = site.clone();
            addSiteRow(siteClone);
        }

        enableUpdateButtons(false);
    }

    protected void enableButtons() {
        boolean enableButtons = false;
        Site[] sites = getContest().getSites();
        if (getSiteListBox().getRowCount() > sites.length) {
            enableButtons = true;
        } else {
            // compare the sites
            for (int i = 0; i < siteListBox.getRowCount(); i++) {
                Site newSite = createSiteFromRow(i);
                if (!newSite.isSameAs(getContest().getSite(newSite.getSiteNumber()))) {
                    enableButtons = true;
                    break;
                }
            }
        }
        enableUpdateButtons(enableButtons);
    }
    
    private void enableUpdateButtons(boolean enableButtons) {

        updateSiteButton.setEnabled(enableButtons);
        cancelSiteEditButton.setEnabled(enableButtons);
    }

    private JTextField createJTextField(String text, boolean passwordField) {
        JTextField textField = new JTextField();
        textField.setText(text);

        // textField.setEditable(editFieldsEnabled);

        textField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                siteListBox.autoSizeAllColumns();
                enableButtons();
            }
        });
        return textField;
    }

    private Site createSite(int nextSiteNumber, String hostName, int port) {
        Site site = new Site("Site " + nextSiteNumber, nextSiteNumber);
        Properties props = new Properties();
        props.put(Site.IP_KEY, hostName);
        props.put(Site.PORT_KEY, "" + port);
        site.setConnectionInfo(props);
        site.setPassword("site" + nextSiteNumber);
        return site;
    }

    /**
     * Add or update a site row
     * 
     * @param site
     */
    private void updateSiteRow(Site site) {
        int row = siteListBox.getIndexByKey(site.getElementId());
        if (row == -1) {
            Object[] objects = buildSiteRow(site);
            siteListBox.addRow(objects, site.getElementId());
            siteList.add(site);
        } else {
            Object[] objects = buildSiteRow(site);
            siteListBox.replaceRow(objects, row);
            siteList.update(site);
        }
        siteListBox.autoSizeAllColumns();
    }

    /**
     * Add a site row.
     * 
     * @param site
     */
    private void addSiteRow(Site site) {
        updateSiteRow(site);
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        getContest().addSiteListener(new SiteListenerImplementation());
        getContest().addAccountListener(new AccountListenerImplementation());

        initializePermissions();
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reloadListBox();
                updateGUIperPermissions();
            }
        });
    }
    

    private void updateGUIperPermissions() {
        addSiteButton.setVisible(isAllowed(Permission.Type.ADD_SITE));
        updateSiteButton.setVisible(isAllowed(Permission.Type.EDIT_SITE));
        cancelSiteEditButton.setVisible(isAllowed(Permission.Type.EDIT_SITE));
        reconnectButton.setVisible(isAllowed(Permission.Type.EDIT_SITE));
        reconnectButton.setVisible(isAllowed(Permission.Type.EDIT_SITE));
        shutdownButton.setVisible(isAllowed(Permission.Type.SHUTDOWN_SERVER) || isAllowed(Permission.Type.SHUTDOWN_ALL_SERVERS));
    }

    /**
     * Site Listener for use by ServerView.
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    public class SiteListenerImplementation implements ISiteListener {

        public void siteProfileStatusChanged(SiteEvent event) {
            // TODO this UI does not use a change in profile status 
        }

        public void siteAdded(SiteEvent event) {
            StaticLog.unclassified("Site " + event.getAction() + " " + event.getSite());
            updateSiteList(event.getSite());
        }

        public void siteRemoved(SiteEvent event) {
            StaticLog.unclassified("Site " + event.getAction() + " " + event.getSite());
        }

        public void siteLoggedOn(SiteEvent event) {
            StaticLog.unclassified("Site " + event.getAction() + " " + event.getSite());
        }

        public void siteLoggedOff(SiteEvent event) {
            StaticLog.unclassified("Site " + event.getAction() + " " + event.getSite());
        }

        public void siteChanged(SiteEvent event) {
            StaticLog.unclassified("Site " + event.getAction() + " " + event.getSite());
            updateSiteList(event.getSite());
        }

        public void sitesRefreshAll(SiteEvent siteEvent) {
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
        showMessage("");
        reloadListBox();
    }

    /**
     * Update site list with input site.
     * 
     * @param site
     */
    public void updateSiteList(Site site) {

        final Site siteClone = site.clone();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateSiteRow(siteClone);
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

    private void showMessage(final String message) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(message);
                messageLabel.setToolTipText(message);
            }
        });
    }
    
    private void showInfoMessage(final String message) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(getParentFrame(), message, "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
        

    /**
     * This method initializes reconnectButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getReconnectButton() {
        if (reconnectButton == null) {
            reconnectButton = new JButton();
            reconnectButton.setText("Reconnect");
            reconnectButton.setMnemonic(KeyEvent.VK_R);
            reconnectButton.setToolTipText("Reconnect the selected site");
            reconnectButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    reconnectToSelectedSite();
                }
            });
        }
        return reconnectButton;
    }

    protected void reconnectToSelectedSite() {
        
        int selectedSite = getSiteListBox().getSelectedIndex();
        
        if (selectedSite == -1){
            showInfoMessage("Select a site to reconnect to");
            return;
        }
        
        try {
            
            Site site = createSiteFromRow(selectedSite);
            
            if (getContest().getSite(site.getSiteNumber()) == null){
                showInfoMessage("Can not connect to site "+site.getSiteNumber()+", Update Site first");
                return;
            }

            getController().sendServerLoginRequest(site.getSiteNumber());
            
        } catch (Exception e) {
            showInfoMessage("Unable to reconnect site, check log");
            getController().getLog().log(Log.WARNING, "Exception attempting to reconnect to site ", e);
        }
        
        
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
     * This method initializes shutdownButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getShutdownButton() {
        if (shutdownButton == null) {
            shutdownButton = new JButton();
            shutdownButton.setText("Shutdown");
            shutdownButton.setMnemonic(KeyEvent.VK_S);
            shutdownButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleShutdownAction();
                }
            });
        }
        return shutdownButton;
    }
    
    /**
     * Is the input ClientId a server.
     * 
     * @param id
     * @return
     */
    private boolean isServer(ClientId id) {
        return id != null && id.getClientType().equals(ClientType.Type.SERVER);
    }

    protected void handleShutdownAction() {

        boolean shutdownAll = false;

        if (isAllowed(Type.SHUTDOWN_ALL_SERVERS)) {

            int result = FrameUtilities.yesNoCancelDialog(this, "Do you want to shutdown all servers?", "Shutdown All?");

            if (result == JOptionPane.YES_OPTION) {
                shutdownAll = true;
            } else if (result == JOptionPane.CANCEL_OPTION) {
                return;
            }

        }

        if (shutdownAll) {

            if (isServer()) {
                getController().shutdownRemoteServers(getContest().getClientId());
                getController().shutdownServer(getContest().getClientId());

            } else {
                getController().sendShutdownAllSites();
            }

        } else {

            int siteNumber = getSiteListBox().getSelectedIndex();

            if (siteNumber == -1) {
                showInfoMessage("No site selected - select a site to shutdown");
                return;
            }

            siteNumber++; // selected index starts at zero

            Site site = getContest().getSite(siteNumber);
            String siteName = site.getDisplayName();

            int result = FrameUtilities.yesNoCancelDialog(this, "Shutdown Site " + siteNumber + " (" + siteName + ") ?", "Shutdown Site?");

            if (result == JOptionPane.YES_OPTION) {

                if (isServer()) {
                    getController().shutdownServer(getContest().getClientId(), siteNumber);
                } else {
                    getController().sendShutdownSite(siteNumber);
                }
            } // else do nothing
        }
    }

    private boolean isServer() {
        return isServer(getContest().getClientId());
    } 
    
} // @jve:decl-index=0:visual-constraint="10,10"
