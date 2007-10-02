package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.model.ISiteListener;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.model.SiteEvent;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.PermissionList;

/**
 * Contest Times Pane.
 * 
 * Shows contest times at all sites.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ContestTimesPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -8946167067842024295L;

    private JPanel contestTimeButtonPane = null;

    private MCLB contestTimeListBox = null;

    private JButton contestTimeRefreshButton = null;

    private JButton startClockButton = null;

    private JButton stopClockButton = null;

    private JPanel messagePane = null;

    private JLabel messageLabel = null;

    private PermissionList permissionList = new PermissionList();

    private JButton startAllButton = null;

    private JButton stopAllButton = null;

    /**
     * This method initializes
     * 
     */
    public ContestTimesPane() {
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
        this.add(getContestTimeListBox(), java.awt.BorderLayout.CENTER);
        this.add(getContestTimeButtonPane(), java.awt.BorderLayout.SOUTH);

        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
    }

    @Override
    public String getPluginTitle() {
        return "Contest Times";
    }

    /**
     * This method initializes contestTimeButtonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getContestTimeButtonPane() {
        if (contestTimeButtonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(35);
            contestTimeButtonPane = new JPanel();
            contestTimeButtonPane.setLayout(flowLayout);
            contestTimeButtonPane.setPreferredSize(new java.awt.Dimension(35, 35));
            contestTimeButtonPane.add(getStartClockButton(), null);
            contestTimeButtonPane.add(getContestTimeRefreshButton(), null);
            contestTimeButtonPane.add(getStopClockButton(), null);
            contestTimeButtonPane.add(getStartAllButton(), null);
            contestTimeButtonPane.add(getStopAllButton(), null);
        }
        return contestTimeButtonPane;
    }

    /**
     * This method initializes contestTimeListBox
     * 
     * @return edu.csus.ecs.pc2.core.log.MCLB
     */
    private MCLB getContestTimeListBox() {
        if (contestTimeListBox == null) {
            contestTimeListBox = new MCLB();

            Object[] cols = { "Site", "State", "Remaining", "Elapsed", "Length" };

            contestTimeListBox.addColumns(cols);
            contestTimeListBox.autoSizeAllColumns();

        }
        return contestTimeListBox;
    }

    public void updateContestTimeRow(final ContestTime contestTime) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Object[] objects = buildContestTimeRow(contestTime);
                int rowNumber = contestTimeListBox.getIndexByKey(contestTime.getElementId());
                if (rowNumber == -1) {
                    contestTimeListBox.addRow(objects, contestTime.getElementId());
                } else {
                    contestTimeListBox.replaceRow(objects, rowNumber);
                }
                contestTimeListBox.autoSizeAllColumns();
                contestTimeListBox.sort();
            }
        });
    }

    protected Object[] buildContestTimeRow(ContestTime contestTime) {

        // Object[] cols = { "Site", "State", "Remaining", "Elapsed", "Length" };

        int numberColumns = contestTimeListBox.getColumnCount();
        Object[] c = new String[numberColumns];

        c[0] = "Site " + contestTime.getSiteNumber();
        c[1] = "NO CONTACT";

        if (contestTime != null) {
            if (contestTime.isContestRunning()) {
                c[1] = "STARTED";
            } else {
                c[1] = "STOPPED";
            }

            c[2] = contestTime.getRemainingTimeStr();
            c[3] = contestTime.getElapsedTimeStr();
            c[4] = contestTime.getContestLengthStr();
        }

        return c;
    }

    private void reloadListBox() {

        showMessage("");
        contestTimeListBox.removeAllRows();
        Site[] sites = getContest().getSites();

        for (Site site : sites) {
            ContestTime contestTime = getContest().getContestTime(site.getSiteNumber());
            if (contestTime != null) {
                addContestTimeRow(contestTime);
            }
        }
    }

    private void addContestTimeRow(ContestTime contestTime) {
        Object[] objects = buildContestTimeRow(contestTime);
        if (contestTime != null) {
            contestTimeListBox.addRow(objects, contestTime.getElementId());
            contestTimeListBox.autoSizeAllColumns();
        }
    }

    private boolean isAllowed(Permission.Type type) {
        return permissionList.isAllowed(type);
    }

    private void initializePermissions() {
        Account account = getContest().getAccount(getContest().getClientId());
        if (account != null) {
            permissionList.clearAndLoadPermissions(account.getPermissionList());
        }
    }

    private void updateGUIperPermissions() {

        // getContestTimeRefreshButton();
        getStartClockButton().setVisible(isAllowed(Permission.Type.START_CONTEST_CLOCK));
        getStopClockButton().setVisible(isAllowed(Permission.Type.STOP_CONTEST_CLOCK));
        getStartAllButton().setVisible(isAllowed(Permission.Type.START_CONTEST_CLOCK));
        getStopAllButton().setVisible(isAllowed(Permission.Type.STOP_CONTEST_CLOCK));
    }

    public void setContestAndController(IContest inContest, IController inController) {
        super.setContestAndController(inContest, inController);
        initializePermissions();

        getContest().addContestTimeListener(new ContestTimeListenerImplementation());

        getContest().addSiteListener(new SiteListenerImplementation());

        getContest().addAccountListener(new AccountListenerImplementation());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reloadListBox();
                updateGUIperPermissions();
            }
        });
    }

    /**
     * Site Listener Implementation. 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    public class SiteListenerImplementation implements ISiteListener {

        protected void updateSiteInfo(int siteNumber) {
            ContestTime contestTime = getContest().getContestTime(siteNumber);
            if (contestTime != null) {
                updateContestTimeRow(contestTime);
            }

        }

        public void siteAdded(SiteEvent event) {
            int siteNumber = event.getSite().getSiteNumber();
            updateSiteInfo(siteNumber);
        }

        public void siteRemoved(SiteEvent event) {
            // TODO Auto-generated method stub

        }

        public void siteChanged(SiteEvent event) {
            int siteNumber = event.getSite().getSiteNumber();
            updateSiteInfo(siteNumber);
        }

        public void siteLoggedOn(SiteEvent event) {
            int siteNumber = event.getSite().getSiteNumber();
            updateSiteInfo(siteNumber);
        }

        public void siteLoggedOff(SiteEvent event) {
            int siteNumber = event.getSite().getSiteNumber();
            updateSiteInfo(siteNumber);
        }

    }

    /**
     * ContestTime Listener
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    public class ContestTimeListenerImplementation implements IContestTimeListener {

        public void contestTimeAdded(ContestTimeEvent event) {
            updateContestTimeRow(event.getContestTime());
        }

        public void contestTimeRemoved(ContestTimeEvent event) {
            // TODO Auto-generated method stub
        }

        public void contestTimeChanged(ContestTimeEvent event) {
            updateContestTimeRow(event.getContestTime());
        }

        public void contestStarted(ContestTimeEvent event) {
            updateContestTimeRow(event.getContestTime());
        }

        public void contestStopped(ContestTimeEvent event) {
            updateContestTimeRow(event.getContestTime());
        }

    }

    /**
     * This method initializes contestTimeRefreshButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getContestTimeRefreshButton() {
        if (contestTimeRefreshButton == null) {
            contestTimeRefreshButton = new JButton();
            contestTimeRefreshButton.setText("Refresh");
            contestTimeRefreshButton.setToolTipText("Refresh All Clocks");
            contestTimeRefreshButton.setMnemonic(java.awt.event.KeyEvent.VK_R);
            contestTimeRefreshButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    reloadListBox();
                }
            });
        }
        return contestTimeRefreshButton;
    }

    /**
     * This method initializes startClockButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getStartClockButton() {
        showMessage("");

        if (startClockButton == null) {
            startClockButton = new JButton();
            startClockButton.setText("Start");
            startClockButton.setMnemonic(java.awt.event.KeyEvent.VK_S);
            startClockButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    startClockTimes();
                }
            });
        }
        return startClockButton;
    }

    protected void startClockTimes() {
        int[] selectedSites = contestTimeListBox.getSelectedIndexes();
        showMessage("");
        if (selectedSites.length == 0) {
            showMessage("Please select site");
            return;
        }

        for (int i = 0; i < selectedSites.length; i++) {
            ElementId contestTimeElementId = (ElementId) contestTimeListBox.getKeys()[i];
            ContestTime contestTime = getContest().getContestTime(contestTimeElementId);
            if (contestTime != null) {
                showMessage("START site "+contestTime.getSiteNumber());
                getController().startContest(contestTime.getSiteNumber());
            }
        }

    }

    /**
     * This method initializes stopClockButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getStopClockButton() {
        showMessage("");

        if (stopClockButton == null) {
            stopClockButton = new JButton();
            stopClockButton.setText("Stop");
            stopClockButton.setMnemonic(java.awt.event.KeyEvent.VK_T);
            stopClockButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    stopClockTimes();
                }
            });
        }
        return stopClockButton;
    }

    protected void stopClockTimes() {

        int[] selectedSites = contestTimeListBox.getSelectedIndexes();
        if (selectedSites.length == 0) {
            showMessage("Please select site");
            return;
        }

        for (int i = 0; i < selectedSites.length; i++) {
            ElementId contestTimeElementId = (ElementId) contestTimeListBox.getKeys()[i];
            ContestTime contestTime = getContest().getContestTime(contestTimeElementId);
            if (contestTime != null) {
                getController().stopContest(contestTime.getSiteNumber());
            }
        }
    }

    /**
     * This method initializes messagePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePane() {
        if (messagePane == null) {
            messageLabel = new JLabel();
            messageLabel.setText("");
            messageLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messagePane = new JPanel();
            messagePane.setLayout(new BorderLayout());
            messagePane.setPreferredSize(new java.awt.Dimension(30, 30));
            messagePane.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return messagePane;
    }

    private void showMessage(final String string) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (messageLabel != null) {
                    messageLabel.setText(string);
                    messageLabel.setToolTipText(string);
                }
            }
        });
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     */
    public class AccountListenerImplementation implements IAccountListener {

        public void accountAdded(AccountEvent accountEvent) {
            // ignore doesn't affect this pane
        }

        public void accountModified(AccountEvent event) {
            // check if is this account
            Account account = event.getAccount();
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

    /**
     * This method initializes startAllButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getStartAllButton() {
        if (startAllButton == null) {
            startAllButton = new JButton();
            startAllButton.setText("Start ALL");
            startAllButton.setToolTipText("Start All sites' clocks");
            startAllButton.setMnemonic(java.awt.event.KeyEvent.VK_A);
            startAllButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    startAllClocks();
                }
            });
        }
        return startAllButton;
    }

    protected void startAllClocks() {
        getController().startAllContestTimes();
    }

    /**
     * This method initializes stopAllButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getStopAllButton() {
        if (stopAllButton == null) {
            stopAllButton = new JButton();
            stopAllButton.setText("Stop ALL");
            stopAllButton.setToolTipText("Stop all sites' clocks");
            stopAllButton.setMnemonic(java.awt.event.KeyEvent.VK_P);
            stopAllButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    stopAllContestTimes();
                }
            });
        }
        return stopAllButton;
    }

    protected void stopAllContestTimes() {
        int result = FrameUtilities.yesNoCancelDialog("Are you sure you want to stop all contest clocks?", "Confirm stop all clocks");

        if (result == JOptionPane.YES_OPTION) {
            getController().stopAllContestTimes();
        }
    }

} // @jve:decl-index=0:visual-constraint="10,10"
