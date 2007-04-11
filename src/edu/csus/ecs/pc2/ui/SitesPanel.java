package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.HeapSorter;
import com.ibm.webrunner.j2mclb.util.NumericStringComparator;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.MCLB;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.ISiteListener;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.model.SiteEvent;

/**
 * A site list.
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

            // Sorters
            HeapSorter sorter = new HeapSorter();
            HeapSorter numericStringSorter = new HeapSorter();
            numericStringSorter.setComparator(new NumericStringComparator());

            // Site Number
            siteListBox.setColumnSorter(0, numericStringSorter, 1);

            // Site Title
            siteListBox.setColumnSorter(1, sorter, 2);

            // Password
            siteListBox.setColumnSorter(2, sorter, 3);

            // IP
            siteListBox.setColumnSorter(3, sorter, 4);

            // Port
            siteListBox.setColumnSorter(4, sorter, 5);

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
        
        if (message == null){
            
            Site[] sites = getModel().getSites();

            for (int i = 0; i < siteListBox.getRowCount(); i++) {

                Site newSite = createSiteFromRow(i);

                if (i >= sites.length) {
                    getController().addNewSite(newSite);
                } else {
                    System.out.println("Would have updated "+newSite);
//                    getController().updateSite(newSite);
                }
            }
        }
    }

    /**
     * validates site list box, return message.
     * @return null if valid, error message string if a problem.
     */
    // TODO check unique IP/port pair
    // TODO insure unique passwords
    // TODO insure port is numeric
    private String validateSiteListBox() {
        return null;
    }

    private Site createSiteFromRow(int i) {

        Object [] objects = siteListBox.getRow(i);
        
//        Object[] cols = { "Site Number", "Site Title", "Password", "IP", "Port" };
        int siteNumber = i + 1;
        String siteTitle = ((JTextField) objects[1]).getText();
        String password = ((JTextField) objects[2]).getText();
        String hostName = ((JTextField) objects[3]).getText();
        String portString = ((JTextField) objects[4]).getText();
        int port = Integer.parseInt(portString);
        
        Site site = new Site (siteTitle, siteNumber);
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

    private Object[] getSiteRow(Site site) {

        // Object[] cols = { "Site Number", "Site Title", "Password", "IP", "Port" };

        Object[] obj = new Object[siteListBox.getColumnCount()];

        obj[0] = site.getSiteNumber();
        obj[1] = createJTextField(site.toString(), false);
        obj[2] = createJTextField(site.getPassword(), false);
        String hostName = site.getConnectionInfo().getProperty(Site.IP_KEY);
        obj[3] = createJTextField(hostName, false);
        String port = site.getConnectionInfo().getProperty(Site.PORT_KEY);
        obj[4] = createJTextField(port, false);

        return obj;
    }

    private void reloadListBox() {
        siteListBox.removeAllRows();
        Site[] sites = getModel().getSites();

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

    private void addSiteRow(Site site) {
        Object[] objects = getSiteRow(site);
        siteListBox.addRow(objects, site.getElementId());
        siteListBox.autoSizeAllColumns();
    }

    public void setModelAndController(IModel inModel, IController inController) {
        super.setModelAndController(inModel, inController);

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
    }
    
    protected void undoEdit() {
        reloadListBox();
    }

} // @jve:decl-index=0:visual-constraint="10,10"
