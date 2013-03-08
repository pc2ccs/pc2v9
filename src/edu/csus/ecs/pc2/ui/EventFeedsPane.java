package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.HeapSorter;
import com.ibm.webrunner.j2mclb.util.NumericStringComparator;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.EventFeedDefinition;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.security.Permission;

/**
 * Event Feed Monitor and Maintenance/Edit Pane - list of event feed servers.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// SOMEDAY implement this.

// $HeadURL$
public class EventFeedsPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -4930863862533276867L;

    private JPanel buttonPane = null;

    private JPanel centerPane = null;

    private JButton addButton = null;

    private JButton editButton = null;

    private MCLB eventFeedListBox = null;

    private JButton viewerButton = null;

    private EditEventFeedDefinitionFrame eventFeedDefinitionFrame = null;


    /**
     * This method initializes
     * 
     */
    public EventFeedsPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(449, 176));
        this.add(getButtonPane(), BorderLayout.SOUTH);
        this.add(getCenterPane(), BorderLayout.CENTER);
    }

    @Override
    public String getPluginTitle() {
        return "Event Feed Pane";
    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        getContest().addAccountListener(new AccountListenerImplementation());

        initializePermissions();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateGUIperPermissions();
                reloadListBox();
            }
        });
    }

    protected void reloadListBox() {

        EventFeedDefinition [] defs = getContest().getEventFeedDefinitions();
        
        getEventFeedListBox().removeAllRows();

        for (EventFeedDefinition definition : defs) {
            addEventFeedDefinitonRow(definition);
        }

    }

    private void addEventFeedDefinitonRow(EventFeedDefinition definition) {
        Object[] objects = buildEventFeedDefRow(definition);
        getEventFeedListBox().addRow(objects, definition.getElementId());
        getEventFeedListBox().autoSizeAllColumns();
    }

    private void updateGUIperPermissions() {

        addButton.setVisible(isAllowed(Permission.Type.EDIT_EVENT_FEED));
        editButton.setVisible(isAllowed(Permission.Type.EDIT_EVENT_FEED));
        viewerButton.setVisible(isAllowed(Permission.Type.VIEW_EVENT_FEED));
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(25);
            buttonPane = new JPanel();
            buttonPane.setLayout(flowLayout);
            buttonPane.setPreferredSize(new Dimension(35, 35));
            buttonPane.add(getAddButton(), null);
            buttonPane.add(getEditButton(), null);
            buttonPane.add(getViewerButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            centerPane = new JPanel();
            centerPane.setLayout(new BorderLayout());
            centerPane.add(getEventFeedListBox(), BorderLayout.CENTER);
        }
        return centerPane;
    }

    /**
     * This method initializes addButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddButton() {
        if (addButton == null) {
            addButton = new JButton();
            addButton.setText("Add");
            addButton.setMnemonic(KeyEvent.VK_A);
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addEventFeedDef();
                }
            });
        }
        return addButton;
    }
    
    public EditEventFeedDefinitionFrame getEventFeedDefinitionFrame() {
        if (eventFeedDefinitionFrame == null){
            eventFeedDefinitionFrame = new EditEventFeedDefinitionFrame();
        }
        return eventFeedDefinitionFrame;
    }

    protected void addEventFeedDef() {
        getEventFeedDefinitionFrame().setEventDefinition(null);
        getEventFeedDefinitionFrame().setVisible(true);
    }

    /**
     * This method initializes editButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getEditButton() {
        if (editButton == null) {
            editButton = new JButton();
            editButton.setText("Edit");
            editButton.setMnemonic(KeyEvent.VK_E);
            editButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    editEventFeedDef();
                }
            });
        }
        return editButton;
    }

    protected void editEventFeedDef() {

        if (getEventFeedListBox().getRowCount() < 1) {
            showMessage("No Event Feed exist");
            return;
        }

        EventFeedDefinition definition = getSelectedDefinition();

        if (definition == null) {
            showMessage("No Event Feed selected");
            return;
        }
        
        getEventFeedDefinitionFrame().setEventDefinition(definition);
        getEventFeedDefinitionFrame().setVisible(true);
    }

    /**
     * This method initializes eventFeedListBox
     * 
     * @return edu.csus.ecs.pc2.ui.MCLB
     */
    private MCLB getEventFeedListBox() {
        if (eventFeedListBox == null) {
            eventFeedListBox = new MCLB();

            eventFeedListBox.removeAllRows();
            eventFeedListBox.removeAllColumns();
            Object[] columns = { "Status", "Site", "Port", "Name", "Connected" };

            eventFeedListBox.addColumns(columns);

            // Sorters
            HeapSorter sorter = new HeapSorter();

            HeapSorter numericStringSorter = new HeapSorter();
            numericStringSorter.setComparator(new NumericStringComparator());

            HeapSorter accountNameSorter = new HeapSorter();
            accountNameSorter.setComparator(new AccountColumnComparator());
            // TODO consider using accountNameSorter on Site columns

            int idx = 0;

            // Status
            eventFeedListBox.setColumnSorter(idx++, sorter, 1);

            // Site
            eventFeedListBox.setColumnSorter(idx++, numericStringSorter, 2);

            // Port
            eventFeedListBox.setColumnSorter(idx++, numericStringSorter, 2);

            // Connected
            eventFeedListBox.setColumnSorter(idx++, accountNameSorter, 2);

            eventFeedListBox.autoSizeAllColumns();

        }
        return eventFeedListBox;
    }

    /**
     * This method initializes viewerButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getViewerButton() {
        if (viewerButton == null) {
            viewerButton = new JButton();
            viewerButton.setText("Viewer");
            viewerButton.setToolTipText("Start a Event Feed Viewer");
            viewerButton.setMnemonic(KeyEvent.VK_V);
            viewerButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    startViewer();
                }
            });
        }
        return viewerButton;
    }

    protected void startViewer() {

        try {

            EventFeedDefinition definition = getSelectedDefinition();
            if (definition == null) {
                showMessage("Select an event feed to view");
                return;

            }

            EventFeedViewerFrame eventFeedViewerFrame = new EventFeedViewerFrame();
            eventFeedViewerFrame.setEventFeedDefinition(definition);
            eventFeedViewerFrame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMessage(final String string) {
        JOptionPane.showMessageDialog(this, string, "Event Feed Definitioin message", JOptionPane.INFORMATION_MESSAGE);
    }

    private EventFeedDefinition getSelectedDefinition() {

        int selectedIndex = eventFeedListBox.getSelectedIndex();
        if (selectedIndex == -1) {
            return null;
        }

        ElementId elementId = (ElementId) eventFeedListBox.getKeys()[selectedIndex];
        EventFeedDefinition definition = getContest().getEventFeedDefinition(elementId);
        return definition;
    }

    /**
     * Build a row for the
     * 
     * @param definition
     * @return
     */
    protected Object[] buildEventFeedDefRow(EventFeedDefinition definition) {

        // Object[] columns = { "Status", "Site", "Port", "Name", "Connected" };
        int cols = eventFeedListBox.getColumnCount();
        Object[] s = new String[cols];

        s[0] = "STOPPED";
        if (definition.isActive()) {
            s[0] = "Running";
        }
        s[1] = Integer.toString(definition.getSiteNumber());
        s[2] = Integer.toString(definition.getPort());
        s[3] = definition.getDisplayName();
        s[4] = "None";
        // TODO CCS add getNumberFeedConnections to get contest
        // s[4] = Integer.toString(getContest().getNumberFeedConnections(definition));

        return s;
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

} // @jve:decl-index=0:visual-constraint="10,10"
