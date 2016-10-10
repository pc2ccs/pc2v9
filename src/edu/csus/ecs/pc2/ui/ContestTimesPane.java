package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.Comparator;
import com.ibm.webrunner.j2mclb.util.HeapSorter;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.AccountNameComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.ILoginListener;
import edu.csus.ecs.pc2.core.model.ISiteListener;
import edu.csus.ecs.pc2.core.model.LoginEvent;
import edu.csus.ecs.pc2.core.model.SiteEvent;
import edu.csus.ecs.pc2.core.security.Permission;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * InternalContest Times Pane/Grid.
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
    private static final long serialVersionUID = 2L;

    private EditContestTimeFrame editContestTimeFrame = new EditContestTimeFrame();

    private EditScheduledStartTimeFrame editScheduledStartTimeFrame = new EditScheduledStartTimeFrame();

    private JPanel contestTimeButtonPane = null;

    private MCLB contestTimeListBox = null;

    private JButton refreshButton = null;

    private JButton setScheduledStartTimeButton = null;

    private JButton startClockButton = null;

    private JButton stopClockButton = null;

    private JPanel messagePane = null;

    private JLabel messageLabel = null;

    private JButton startAllButton = null;

    private JButton stopAllButton = null;

    private JButton editButton = null;

    private SimpleDateFormat formatter = new SimpleDateFormat(" HH:mm:ss MM-dd");

    private Logger log;

    
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
        this.setSize(new Dimension(621, 229));
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
            flowLayout.setHgap(15);
            contestTimeButtonPane = new JPanel();
            contestTimeButtonPane.setLayout(flowLayout);
            contestTimeButtonPane.setPreferredSize(new java.awt.Dimension(35, 35));
            contestTimeButtonPane.add(getSetScheduledStartTimeButton());
            contestTimeButtonPane.add(getStartClockButton(), null);
            contestTimeButtonPane.add(getRefreshButton(), null);
            contestTimeButtonPane.add(getEditButton(), null);
            contestTimeButtonPane.add(getStopClockButton(), null);
            contestTimeButtonPane.add(getStartAllButton(), null);
            contestTimeButtonPane.add(getStopAllButton(), null);
        }
        return contestTimeButtonPane;
    }

    /**
     * MCLB account name comparator.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    protected class AccountNameComparatorMCLB implements Comparator {

        /**
         * 
         */
        private static final long serialVersionUID = 6940019340965217198L;

        private AccountNameComparator accountNameComparator = new AccountNameComparator();

        public int compare(Object arg0, Object arg1) {
            return accountNameComparator.compare((String) arg0, (String) arg1);
        }
    }

    /**
     * This method initializes contestTimeListBox
     * 
     * @return edu.csus.ecs.pc2.core.log.MCLB
     */
    private MCLB getContestTimeListBox() {
        if (contestTimeListBox == null) {
            contestTimeListBox = new MCLB();

            contestTimeListBox.setMultipleSelections(true);
            Object[] cols = { "Site", "Scheduled Start", "Started?", "Current State", "Remaining", "Elapsed", "Length", "Logged In", "Since"  };


            contestTimeListBox.addColumns(cols);

            HeapSorter sorter = new HeapSorter();
            // HeapSorter numericStringSorter = new HeapSorter();
            // numericStringSorter.setComparator(new NumericStringComparator());
            HeapSorter accountNameSorter = new HeapSorter();
            accountNameSorter.setComparator(new AccountNameComparatorMCLB());

            // Site
            contestTimeListBox.setColumnSorter(0, accountNameSorter, 1);

            // State
            contestTimeListBox.setColumnSorter(3, sorter, 2);

            // TODO sort by times

            contestTimeListBox.autoSizeAllColumns();

        }
        return contestTimeListBox;
    }

    public void updateContestTimeRow(final ContestTime contestTime) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String[] objects = buildContestTimeRow(contestTime);
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

    protected String[] buildContestTimeRow(ContestTime contestTime) {

//        Object[] cols = { "Site", "Scheduled Start", "Started?", "Current State", "Remaining", "Elapsed", "Length", "Logged In", "Since"  };

        int numberColumns = contestTimeListBox.getColumnCount();
        String[] c = new String[numberColumns];

        //fill in the Site number
        c[0] = "??";
        if (contestTime != null) {
            c[0] = "Site " + contestTime.getSiteNumber();           
        }

        //fill in the "Scheduled Start" field
        c[1] = "<undefined>";
        IInternalContest contest = getContest();
        if (contest != null) {
            ContestInformation contestInfo = contest.getContestInformation();
            if (contestInfo != null) {
                GregorianCalendar scheduledStartTime = contestInfo.getScheduledStartTime();
                if (scheduledStartTime != null) {
                    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    fmt.setCalendar(scheduledStartTime);
                    String dateFormatted = fmt.format(scheduledStartTime.getTime());
                    c[1] = dateFormatted ;
                }
            }
        }
        
        //fill in the "Started?" field
        c[2] = "NO CONTACT";
        if (contestTime != null) {
            if (contestTime.isContestStarted()) {
                c[2] = "Yes";
            } else {
                c[2] = "No";
            }
        }
        
        //fill in the "Current State" field
        c[3] = "NO CONTACT";
        if (contestTime != null) {
            if (contestTime.isContestRunning()) {
                c[3] = "RUNNING";
            } else {
                c[3] = "STOPPED";
            }

            c[4] = contestTime.getRemainingTimeStr();
            c[5] = contestTime.getElapsedTimeStr();
            c[6] = contestTime.getContestLengthStr();
            
            c[7] = "No";
            if (isThisSite(contestTime.getSiteNumber())) {
                c[7] = "N/A";
            }
            c[8] = "";
            
            try {
                ClientId serverId = new ClientId(contestTime.getSiteNumber(), Type.SERVER, 0);
                if (getContest().isLocalLoggedIn(serverId)) {
                    c[7] = "YES";
                    c[8] = formatter.format(getContest().getLocalLoggedInDate(serverId));
                } else if (! isServer(getContest().getClientId())){
                    if (getContest().isRemoteLoggedIn(serverId)){
                        c[7] = "YES";
                        // TODO some day send when server was logged in
                        c[8] = "";
                    }
                }
            } catch (Exception e) {
                c[7] = "??";
                log.log(Log.WARNING, "Exception updating Contest Time for site "+contestTime.getSiteNumber(), e);
            }
            
        }

        return c;
    }

    private boolean isThisSite(int siteNumber) {
        return getContest().getSiteNumber() == siteNumber;
    }

    private void reloadListBox() {

        contestTimeListBox.removeAllRows();
        ContestTime[] contestTimes = getContest().getContestTimes();

        for (ContestTime contestTime : contestTimes) {
            addContestTimeRow(contestTime);
        }
    }

    private void addContestTimeRow(ContestTime contestTime) {
        updateContestTimeRow(contestTime);
    }

    private void updateGUIperPermissions() {

        getStartClockButton().setVisible(isAllowed(Permission.Type.START_CONTEST_CLOCK));
        getStopClockButton().setVisible(isAllowed(Permission.Type.STOP_CONTEST_CLOCK));
        getStartAllButton().setVisible(isAllowed(Permission.Type.START_CONTEST_CLOCK));
        getStopAllButton().setVisible(isAllowed(Permission.Type.STOP_CONTEST_CLOCK));
        getEditButton().setVisible(isAllowed(Permission.Type.EDIT_CONTEST_CLOCK));
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        log = getController().getLog();

        initializePermissions();

        getContest().addContestTimeListener(new ContestTimeListenerImplementation());

        getContest().addSiteListener(new SiteListenerImplementation());
        
        getContest().addLoginListener(new LoginListenerImplementation());

        getContest().addAccountListener(new AccountListenerImplementation());

        editContestTimeFrame.setContestAndController(inContest, inController);
        
        editScheduledStartTimeFrame.setContestAndController(inContest, inController);
        

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reloadListBox();
                updateGUIperPermissions();
            }
        });
    }

    /**
     * Site Listener Implementation.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    public class SiteListenerImplementation implements ISiteListener {

        public void siteProfileStatusChanged(SiteEvent event) {
            // TODO this UI does not use a change in profile status 
        }

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

        public void sitesRefreshAll(SiteEvent siteEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                    updateGUIperPermissions();
                }
            });
        }

    }
    
    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    public class LoginListenerImplementation implements ILoginListener {

        public void loginAdded(LoginEvent event) {
            if (isServer(event.getClientId())){
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        reloadListBox();
                    }
                });
            }
        }

        public void loginRemoved(LoginEvent event) {
            if (isServer(event.getClientId())){
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        reloadListBox();
                    }
                });
            }
        }

        public void loginDenied(LoginEvent event) {
          
        }
        
        public void loginRefreshAll(LoginEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
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

        public void refreshAll(ContestTimeEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                    updateGUIperPermissions();
                }
            });
        }
        
        /** This method exists to support differentiation between manual and automatic starts,
         * in the event this is desired in the future.
         * Currently it just delegates the handling to the contestStarted() method.
         */
        @Override
        public void contestAutoStarted(ContestTimeEvent event) {
            contestStarted(event);
        }

    }

    /**
     * This method initializes contestTimeRefreshButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getRefreshButton() {
        if (refreshButton == null) {
            refreshButton = new JButton();
            refreshButton.setText("Refresh");
            refreshButton.setToolTipText("Refresh All Clocks");
            refreshButton.setToolTipText("Refresh Clocks on All sites");
            refreshButton.setMnemonic(java.awt.event.KeyEvent.VK_R);
            refreshButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    reloadListBox();
                }
            });
        }
        return refreshButton;
    }

    protected boolean isServer(ClientId clientId) {
        return clientId != null && clientId.getClientType().equals(ClientType.Type.SERVER);
    }

    /**
     * This method initializes startClockButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getStartClockButton() {
        if (startClockButton == null) {
            startClockButton = new JButton();
            startClockButton.setText("Start");
            startClockButton.setToolTipText("Start the contest clock on the selected site");
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
        if (selectedSites.length == 0) {
            showMessage("Please select site");
            return;
        }

        for (int i = 0; i < selectedSites.length; i++) {
            ElementId contestTimeElementId = (ElementId) contestTimeListBox.getKeys()[selectedSites[i]];
            ContestTime contestTime = getContest().getContestTime(contestTimeElementId);
            if (contestTime != null) {
//                showMessage("START site " + contestTime.getSiteNumber());
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
        if (stopClockButton == null) {
            stopClockButton = new JButton();
            stopClockButton.setText("Stop");
            stopClockButton.setToolTipText("Stop the contest clock on the selected site");
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
            ElementId contestTimeElementId = (ElementId) contestTimeListBox.getKeys()[selectedSites[i]];
            ContestTime contestTime = getContest().getContestTime(contestTimeElementId);
            if (contestTime != null) {
//                showMessage("STOP site " + contestTime.getSiteNumber());
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
                    JOptionPane.showMessageDialog(getParentFrame(), string, "Warning", JOptionPane.WARNING_MESSAGE);
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

        public void accountsAdded(AccountEvent accountEvent) {
            // ignore, does not affect this pane
        }

        public void accountsModified(AccountEvent accountEvent) {
            // check if is this account
            boolean theyModifiedUs = false;
            for (Account account : accountEvent.getAccounts()) {
                /**
                 * If this is the account then update the GUI display per the potential change in Permissions.
                 */
                if (getContest().getClientId().equals(account.getClientId())) {
                    theyModifiedUs = true;
                    initializePermissions();
                }
            }
            if (theyModifiedUs) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        updateGUIperPermissions();
                    }
                });
            }
        }

        public void accountsRefreshAll(AccountEvent accountEvent) {
            accountsModified(accountEvent);
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
//        showMessage("START ALL sites");
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
//        int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), "Are you sure you want to stop all contest clocks?", "Confirm stop all clocks");

//        if (result == JOptionPane.YES_OPTION) {
//            showMessage("STOP ALL sites");
        
            getController().stopAllContestTimes();
            
//        }
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
            editButton.setToolTipText("Edit the contest time on the selected site");
            editButton.setMnemonic(java.awt.event.KeyEvent.VK_E);
            editButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    editSelectedContestTime();
                }
            });
        }
        return editButton;
    }

    /**
     * Edit the first selected site.
     *
     */
    protected void editSelectedContestTime() {
        int[] selectedSites = contestTimeListBox.getSelectedIndexes();
        if (selectedSites.length == 0) {
            showMessage("Please select site");
            return;
        }

        ElementId contestTimeElementId = (ElementId) contestTimeListBox.getKeys()[selectedSites[0]];
        ContestTime contestTime = getContest().getContestTime(contestTimeElementId);
        ContestInformation contestInfo = getContest().getContestInformation();

        editContestTimeFrame.setContestTime(contestTime, contestInfo);
        editContestTimeFrame.setVisible(true);

    }
    

    /**
     * This method creates and initializes the Set Scheduled Start Time button
     * 
     * @return a JButton which invokes the EditScheduledStartTime frame
     */
    private JButton getSetScheduledStartTimeButton() {
        if (setScheduledStartTimeButton == null) {
            setScheduledStartTimeButton = new JButton("Edit Start Schedule");
            setScheduledStartTimeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    editScheduledStartTime();
                }
            });
            setScheduledStartTimeButton.setMnemonic(KeyEvent.VK_C);
            setScheduledStartTimeButton.setToolTipText("Set/update the Scheduled Start Time for the contest");
        }
        return setScheduledStartTimeButton;
    }

    /**
     * Displays a frame allowing editing of the Scheduled Start Time for the contest.
     */
    protected void editScheduledStartTime() {
        ContestInformation contestInfo = getContest().getContestInformation();

        editScheduledStartTimeFrame.setContestInfo(contestInfo);
        editScheduledStartTimeFrame.setVisible(true);
    }
    
} // @jve:decl-index=0:visual-constraint="10,10"
