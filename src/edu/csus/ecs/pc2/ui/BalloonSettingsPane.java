package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.HeapSorter;
import com.ibm.webrunner.j2mclb.util.NumericStringComparator;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.SiteComparatorBySiteNumber;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.BalloonSettingsEvent;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IBalloonSettingsListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.Permission.Type;

/**
 * View List (Grid) of Balloon Settings.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class BalloonSettingsPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -7483784815760107250L;

    private JPanel balloonSettingsButtonPane = null;

    private MCLB balloonSettingsListBox = null;

    private JButton addButton = null;

    private JButton editButton = null;

    private JPanel messagePanel = null;

    private JLabel messageLabel = null;

    private Log log = null;

    private EditBalloonSettingsFrame editBalloonSettingsFrame = null;

    private JButton copyButton = null;
    
    /**
     * This method initializes
     * 
     */
    public BalloonSettingsPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(564, 229));
        this.add(getMessagePanel(), java.awt.BorderLayout.NORTH);
        this.add(getBalloonSettingsListBox(), java.awt.BorderLayout.CENTER);
        this.add(getBalloonSettingsButtonPane(), java.awt.BorderLayout.SOUTH);

        editBalloonSettingsFrame = new EditBalloonSettingsFrame();

    }

    @Override
    public String getPluginTitle() {
        return "BalloonSettingss Pane";
    }

    /**
     * This method initializes balloonSettingsButtonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getBalloonSettingsButtonPane() {
        if (balloonSettingsButtonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(25);
            balloonSettingsButtonPane = new JPanel();
            balloonSettingsButtonPane.setLayout(flowLayout);
            balloonSettingsButtonPane.setPreferredSize(new java.awt.Dimension(35, 35));
            balloonSettingsButtonPane.add(getAddButton(), null);
            balloonSettingsButtonPane.add(getCopyButton(), null);
            balloonSettingsButtonPane.add(getEditButton(), null);
        }
        return balloonSettingsButtonPane;
    }

    /**
     * This method initializes balloonSettingsListBox
     * 
     * @return edu.csus.ecs.pc2.core.log.MCLB
     */
    private MCLB getBalloonSettingsListBox() {
        if (balloonSettingsListBox == null) {
            balloonSettingsListBox = new MCLB();

            Object[] cols = { "Site", "Print", "E-mail", "Printer", "Send To", "Mail Server" , "Balloon Client"};

            balloonSettingsListBox.addColumns(cols);

            /**
             * No sorting at this time, the only way to know what order the balloonSettingss are is to NOT sort them. Later we can add a sorter per BalloonSettingsDisplayList somehow.
             */

            // Sorters
            HeapSorter sorter = new HeapSorter();
            HeapSorter numericStringSorter = new HeapSorter();
            numericStringSorter.setComparator(new NumericStringComparator());

            // Site # (String)
            balloonSettingsListBox.setColumnSorter(0, sorter, 1);
            // Print
            balloonSettingsListBox.setColumnSorter(1, sorter, 2);
            // E-mail
            balloonSettingsListBox.setColumnSorter(2, sorter, 3);
            // Printer
            balloonSettingsListBox.setColumnSorter(3, sorter, 4);
            // Send To
            balloonSettingsListBox.setColumnSorter(4, sorter, 4);
            // Mail Server
            balloonSettingsListBox.setColumnSorter(5, sorter, 6);
            // Balloon Client
            balloonSettingsListBox.setColumnSorter(6, sorter, 7);

            balloonSettingsListBox.autoSizeAllColumns();

        }
        return balloonSettingsListBox;
    }

    public void updateBalloonSettingRow(final BalloonSettings balloonSettings) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Object[] objects = buildBalloonSettingsRow(balloonSettings);
                int rowNumber = balloonSettingsListBox.getIndexByKey(balloonSettings.getElementId());
                if (rowNumber == -1) {
                    balloonSettingsListBox.addRow(objects, balloonSettings.getElementId());
                } else {
                    balloonSettingsListBox.replaceRow(objects, rowNumber);
                }
                balloonSettingsListBox.autoSizeAllColumns();
                balloonSettingsListBox.sort();
            }
        });
    }
    

    private String yesNoString(boolean b) {
        if (b) {
            return "Yes";
        } else {
            return "No";
        }
    }

    protected Object[] buildBalloonSettingsRow(BalloonSettings balloonSettings) {
//        Object[] cols = { "Site", "Print", "E-mail", "Printer", "Sent To", "Mail Server" , "Balloon Client"};
        int numberColumns = balloonSettingsListBox.getColumnCount();
        Object[] c = new String[numberColumns];

        c[0] = "Site " + balloonSettings.getSiteNumber();
        c[1] = yesNoString(balloonSettings.isPrintBalloons());
        c[2] = yesNoString(balloonSettings.isEmailBalloons());
        c[3] = balloonSettings.getPrintDevice();
        c[4] = balloonSettings.getEmailContact();
        c[5] = balloonSettings.getMailServer();
        // it is an error if we get here and balloonClient is not set
        c[6] = balloonSettings.getBalloonClient().toString();

        return c;
    }

    private void reloadListBox() {
        balloonSettingsListBox.removeAllRows();
        BalloonSettings[] balloonSettingsArray = getContest().getBalloonSettings();

        for (BalloonSettings balloonSettings : balloonSettingsArray) {
            addBalloonSettingsRow(balloonSettings);
        }
    }

    private void addBalloonSettingsRow(BalloonSettings balloonSettings) {
        Object[] objects = buildBalloonSettingsRow(balloonSettings);
        balloonSettingsListBox.addRow(objects, balloonSettings.getElementId());
        balloonSettingsListBox.autoSizeAllColumns();
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        log = getController().getLog();

        editBalloonSettingsFrame.setContestAndController(inContest, inController);

        getContest().addBalloonSettingsListener(new BalloonSettingsListenerImplementation());
        getContest().addAccountListener(new AccountListenerImplementation());
        
        initializePermissions();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateGUIperPermissions();
                reloadListBox();
            }
        });
    }

    private void updateGUIperPermissions() {
        addButton.setVisible(isAllowed(Permission.Type.ADD_NOTIFICATIONS));
        editButton.setVisible(isAllowed(Permission.Type.EDIT_NOTIFICATIONS));
        copyButton.setVisible(isAllowed(Permission.Type.EDIT_NOTIFICATIONS));
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
            addButton.setToolTipText("Add new notification settings for a site");
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addBalloonSettings();
                }
            });
        }
        return addButton;
    }

    public boolean isAllowed(Account account, Type perm) {
        if (account == null) {
            return false;
        }
        return account.getPermissionList().isAllowed(perm);
    }

    protected void addBalloonSettings() {
        Vector<Account> accounts = getContest().getAccounts(ClientType.Type.SCOREBOARD);
        accounts.addAll(getContest().getAccounts(ClientType.Type.ADMINISTRATOR));
        accounts.addAll(getContest().getAccounts(ClientType.Type.JUDGE));
        accounts.addAll(getContest().getAccounts(ClientType.Type.SPECTATOR));
        boolean clientFound = false;
        Account[] accountArray = accounts.toArray(new Account[accounts.size()]);
        for (Account account : accountArray) {
            if (account.isAllowed(Type.BALLOON_EMAIL) || account.isAllowed(Type.BALLOON_PRINT)) {
                clientFound = true;
                break;
            }
        }
        if (clientFound) {
            editBalloonSettingsFrame.setBalloonSettings(null);
            editBalloonSettingsFrame.setVisible(true);
        } else {
            FrameUtilities.showMessage(getParentFrame(), "ERROR", "No accounts have permission to send a notification, have you created a scoreboard account?");
        }
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
            editButton.setToolTipText("Edit existing site settings");
            editButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    editSelectedBalloonSettings();
                }
            });
        }
        return editButton;
    }

    protected void editSelectedBalloonSettings() {

        int selectedIndex = balloonSettingsListBox.getSelectedIndex();
        if (selectedIndex == -1) {
            showMessage("Select a notification setting to edit");
            return;
        }

        try {
            ElementId elementId = (ElementId) balloonSettingsListBox.getKeys()[selectedIndex];
            BalloonSettings balloonSettingsToEdit = getContest().getBalloonSettings(elementId);

            editBalloonSettingsFrame.setBalloonSettings(balloonSettingsToEdit);
            editBalloonSettingsFrame.setVisible(true);
        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to edit notification settings, check log");
        }
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

    private void showMessage(final String string) {
        if (string.trim().length() == 0) {
            return;
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(getParentFrame(), string, "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    /**
     * Balloon Settings Listener.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    private class BalloonSettingsListenerImplementation implements IBalloonSettingsListener {

        public void balloonSettingsAdded(final BalloonSettingsEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateBalloonSettingRow(event.getBalloonSettings());
                }
            });
        }

        public void balloonSettingsChanged(final BalloonSettingsEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateBalloonSettingRow(event.getBalloonSettings());
                }
            });
        }

        public void balloonSettingsRemoved(BalloonSettingsEvent event) {
            // TODO remove balloon setting
            log.info("debug BalloonSettings REMOVED  " + event.getBalloonSettings());
        }

        public void balloonSettingsRefreshAll(BalloonSettingsEvent balloonSettingsEvent) {

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            }); 
        }
    }

    /**
     * This method initializes copyButton
     *
     * @return javax.swing.JButton
     */
    private JButton getCopyButton() {
        if (copyButton == null) {
            copyButton = new JButton();
            copyButton.setText("Copy");
            copyButton.setToolTipText("Copy settings from an existing site to a new site");
            copyButton.setActionCommand("Copy");
            copyButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    copySelectedBalloonSettings();
                }
            });
        }
        return copyButton;
    }

    protected void copySelectedBalloonSettings() {
        int rowCount = balloonSettingsListBox.getRowCount();
        int siteCount = getContest().getSites().length;
        if (rowCount == siteCount) {
            showMessage("No available destination sites, please use edit instead.");
            return;
        }
        int selectedIndex = balloonSettingsListBox.getSelectedIndex();
        if (selectedIndex == -1) {
            showMessage("Select a balloonSettings to copy");
            return;
        }

        try {
            // would be nice to select the new row, but this will do
            balloonSettingsListBox.deselectAllRows();
            ElementId elementId = (ElementId) balloonSettingsListBox.getKeys()[selectedIndex];
            BalloonSettings sourceBalloonSettings = getContest().getBalloonSettings(elementId);
            Site newSite = promptForSite(sourceBalloonSettings.getSiteNumber());
            if (newSite == null) {
                showMessage("Copy Aborted.");
            } else {
                BalloonSettings balloonSettingsToEdit = sourceBalloonSettings.copy(newSite);
                getController().updateBalloonSettings(balloonSettingsToEdit);
                editBalloonSettingsFrame.setBalloonSettings(balloonSettingsToEdit);
                editBalloonSettingsFrame.setVisible(true);
            }
        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to clone notification settings, check log");
        }
    }

    private Site promptForSite(int siteNumber) {
        Site[] sites = getContest().getSites();
        Arrays.sort(sites, new SiteComparatorBySiteNumber());
        Vector<Site> dest = new Vector<Site>();
        for (Site site : sites) {
            // do not allow them to copy balloonSettings to a site with settings already
            if (getContest().getBalloonSettings(site.getSiteNumber()) != null) {
                continue;
            } else {
                dest.add(site);
            }
        }
        Site s = (Site)JOptionPane.showInputDialog(
                this,
                "Copying from site "+siteNumber+" to:\n",
                "Copy Destination Dialog",
                JOptionPane.QUESTION_MESSAGE,
                null,
                dest.toArray(new Site[dest.size()]),
                null);

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
