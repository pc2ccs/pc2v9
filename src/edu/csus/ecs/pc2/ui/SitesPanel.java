package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Arrays;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.list.SiteComparatorBySiteNumber;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.ISiteListener;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.model.SiteEvent;
import edu.csus.ecs.pc2.core.model.SiteList;

/**
 * View, Add and change site's settings.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL${date}
public class SitesPanel extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -5590899142044283529L;

    private JPanel siteButtonPanel = null;

    private MCLB siteListBox = null;

    private JButton addSiteButton = null;

    private JButton updateSiteButton = null;

    private JButton cancelSiteEditButton = null;
    
    private SiteList siteList = new SiteList();

    /**
     * This method initializes
     * 
     */
    public SitesPanel() {
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
             * No sorters for this frame, keep it in order.  If you
             * want sorters, be sure to change update methods.
             */
//            // Sorters
//            HeapSorter sorter = new HeapSorter();
//            HeapSorter numericStringSorter = new HeapSorter();
//            numericStringSorter.setComparator(new NumericStringComparator());
//
//            // Site Number
//            siteListBox.setColumnSorter(0, numericStringSorter, 1);
//
//            // Site Title
//            siteListBox.setColumnSorter(1, sorter, 2);
//
//            // Password
//            siteListBox.setColumnSorter(2, sorter, 3);
//
//            // IP
//            siteListBox.setColumnSorter(3, sorter, 4);
//
//            // Port
//            siteListBox.setColumnSorter(4, sorter, 5);

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

    protected void addNewSite() {
        Site newSite = createSite(siteListBox.getRowCount() + 1);
        addSiteRow(newSite);
        enableUpdateButtons (true);
    }

    /**
     * This method initializes updateSiteButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getUpdateSiteButton() {
        if (updateSiteButton == null) {
            updateSiteButton = new JButton();
            updateSiteButton.setText("Update");
            updateSiteButton.setToolTipText("Update site changes");
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

        String message = validateSiteListBox();

        if (message == null) {

            Site[] sites = getModel().getSites();

            for (int i = 0; i < siteListBox.getRowCount(); i++) {

                Site newSite = createSiteFromRow(i);

                if (i >= sites.length) {
                    getController().addNewSite(newSite);
                } else {
                    getController().updateSite(newSite);
                }
            }
            enableUpdateButtons(false);
        } else {
            JOptionPane.showMessageDialog(this, message);
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
     * @param i - index in the list box.
     * @return
     */
    private Site createSiteFromRow(int i) {

        Object [] objects = siteListBox.getRow(i);
        
//        Object[] cols = { "Site Number", "Site Title", "Password", "IP", "Port" };
//        Integer siteNumberInteger = ((Integer) objects[0]);
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
        textField.setText(port); // had to re-add port becuase IntegerDocument cleared it out.
        obj[4] = textField;

        return obj;
    }

    private void reloadListBox() {
        siteListBox.removeAllRows();
        siteList = new SiteList();

        Site[] sites = getModel().getSites();
        Arrays.sort(sites, new SiteComparatorBySiteNumber());

        for (Site site : sites) {
            addSiteRow(site);
        }

        enableUpdateButtons (false);
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
            public void keyPressed(java.awt.event.KeyEvent e) {
                siteListBox.autoSizeAllColumns();
                enableUpdateButtons(true);
            }
        });
        return textField;
    }

    private Site createSite(int nextSiteNumber) {
        Site site = new Site("Site " + nextSiteNumber, nextSiteNumber);
        Properties props = new Properties();
        props.put(Site.IP_KEY, "localhost");
        int port = 50002 + (nextSiteNumber - 1) * 1000;
        props.put(Site.PORT_KEY, "" + port);
        site.setConnectionInfo(props);
        site.setPassword("site" + nextSiteNumber);
        return site;
    }
   
    /**
     * Add or update a site row
     * @param site
     */
    private void updateSiteRow (Site site) {
        int row = siteListBox.getIndexByKey(site.getElementId());
        if (row == -1){
            Object [] objects = buildSiteRow(site);
            siteListBox.addRow(objects, site.getElementId());
            siteList.add(site);
        }else {
            Object [] objects = buildSiteRow(site);
            siteListBox.replaceRow(objects, row);
            siteList.update(site);
        }
        siteListBox.autoSizeAllColumns();
    }

    /**
     * Add a site row.
     * @param site
     */
    private void addSiteRow(Site site) {
        updateSiteRow(site);
    }

    public void setModelAndController(IModel inModel, IController inController) {
        super.setModelAndController(inModel, inController);
        
        getModel().addSiteListener(new SiteListenerImplementation());
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reloadListBox();
            }
        });
    }
    

    /**
     * Site Listener for use by ServerView.
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    public class SiteListenerImplementation implements ISiteListener {

        public void siteAdded(SiteEvent event) {
            StaticLog.unclassified("Site " + event.getAction() + " " + event.getSite());
            updateSiteList (event.getSite());
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
            updateSiteList (event.getSite());
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
     * Update site list with input site.
     * @param site
     */
    public void updateSiteList(final Site site) {
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateSiteRow(site);
            }
        });
    }

} // @jve:decl-index=0:visual-constraint="10,10"
