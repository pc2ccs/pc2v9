package edu.csus.ecs.pc2.ui.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.IniFile;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IProfileListener;
import edu.csus.ecs.pc2.core.model.ProfileEvent;
import edu.csus.ecs.pc2.ui.AccountsPane;
import edu.csus.ecs.pc2.ui.AutoJudgesPane;
import edu.csus.ecs.pc2.ui.BalloonSettingsPane;
import edu.csus.ecs.pc2.ui.CategoriesPane;
import edu.csus.ecs.pc2.ui.ClarificationsPane;
import edu.csus.ecs.pc2.ui.ConnectionsPane;
import edu.csus.ecs.pc2.ui.ContestClockDisplay;
import edu.csus.ecs.pc2.ui.ContestClockDisplay.DisplayTimes;
import edu.csus.ecs.pc2.ui.ContestInformationPane;
import edu.csus.ecs.pc2.ui.ContestPreloadPane;
import edu.csus.ecs.pc2.ui.ContestTimesPane;
import edu.csus.ecs.pc2.ui.EventFeedServerPane;
import edu.csus.ecs.pc2.ui.ExportDataPane;
import edu.csus.ecs.pc2.ui.FinalizePane;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.GroupsPane;
import edu.csus.ecs.pc2.ui.ICPCLoadPane;
import edu.csus.ecs.pc2.ui.ImportDataPane;
import edu.csus.ecs.pc2.ui.JPanePlugin;
import edu.csus.ecs.pc2.ui.JudgementsPane;
import edu.csus.ecs.pc2.ui.LanguagesPane;
import edu.csus.ecs.pc2.ui.LogWindow;
import edu.csus.ecs.pc2.ui.LoginsPane;
import edu.csus.ecs.pc2.ui.MessageMonitorPane;
import edu.csus.ecs.pc2.ui.OptionsPane;
import edu.csus.ecs.pc2.ui.PacketExplorerPane;
import edu.csus.ecs.pc2.ui.PacketMonitorPane;
import edu.csus.ecs.pc2.ui.PlaybackPane;
import edu.csus.ecs.pc2.ui.PluginLoadPane;
import edu.csus.ecs.pc2.ui.ProblemsPane;
import edu.csus.ecs.pc2.ui.ProfilesPane;
import edu.csus.ecs.pc2.ui.ReportPane;
import edu.csus.ecs.pc2.ui.RunsPane;
import edu.csus.ecs.pc2.ui.SitesPane;
import edu.csus.ecs.pc2.ui.StandingsHTMLPane;
import edu.csus.ecs.pc2.ui.StandingsPane;
import edu.csus.ecs.pc2.ui.TeamStatusPane;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Administrator GUI.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class AdministratorView extends JFrame implements UIPlugin, ChangeListener {

    private static final long serialVersionUID = 1L;

    private IInternalContest contest;

    private IInternalController controller;  //  @jve:decl-index=0:

    private JPanel jPanel = null;

    private JTabbedPane mainTabbedPanel = null;

    private JPanel statusPanel = null;

    private JPanel topPanel = null;

    private JButton exitButton = null;

    private LogWindow securityAlertLogWindow = null;

    private JPanel clockPane = null;

    private JPanel exitButtonPane = null;

    private JLabel clockLabel = null;

    private JPanel padPane = null;

    private ContestClockDisplay contestClockDisplay = null;

    private JTabbedPane configureContestTabbedPane = null;

    private JTabbedPane runContestTabbedPane = null;

    private static final Color ACTIVE_TAB_COLOR = Color.BLUE;
    private static final Color INACTIVE_TAB_COLOR = Color.GRAY ;

    private JPanel centerPane = null;

    private JPanel aMessagePane = null;

    private JLabel messageLabel = null;
    
    /**
     * This method initializes
     * 
     */
    public AdministratorView() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setBounds(new java.awt.Rectangle(0, 0, 754, 500));
        this.setContentPane(getJPanel());
        this.setTitle("PC^2 Administrator");
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                promptAndExit();
            }
        });

        getMainTabbedPanel().addChangeListener(this);
        
        overRideLookAndFeel();
    
        FrameUtilities.centerFrame(this);
    }
    
    private void overRideLookAndFeel(){
        String value = IniFile.getValue("client.plaf");
        if (value != null && value.equalsIgnoreCase("java")){
            FrameUtilities.setJavaLookAndFeel();
        }
        if (value != null && value.equalsIgnoreCase("native")){
            FrameUtilities.setNativeLookAndFeel();
        }
    }
    
    /**
     * Listeners for admin view.
     * 
     * This provides a way to refresh the admin view on refresh.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    protected class AdminListeners implements UIPlugin {

        /**
         * 
         */
        private static final long serialVersionUID = 3733076435840880891L;

        public void setContestAndController(IInternalContest inContest, IInternalController inController) {
            
            inContest.addProfileListener(new ProfileListenerImplementation());
        }

        public String getPluginTitle() {
            return "AdminListeners";
        }
    }



    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        final JFrame thisFrame = this;

        updateProfileLabel();
        
        AdminListeners adminListeners = new AdminListeners();
        adminListeners.setContestAndController(inContest, inController);
        controller.register(adminListeners);
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                controller.startLogWindow(contest);

                initializeSecurityAlertWindow(contest);

                // set the tab names and other characteristics for the main tabs
                int fontSize = getMainTabbedPanel().getFont().getSize();
                getMainTabbedPanel().setFont(getMainTabbedPanel().getFont().deriveFont(Font.BOLD, fontSize + 6));
                getMainTabbedPanel().setTitleAt(0, "Configure Contest");
                getMainTabbedPanel().setForegroundAt(0, ACTIVE_TAB_COLOR);
                getMainTabbedPanel().setTitleAt(1, "Run Contest");
                getMainTabbedPanel().setForegroundAt(1, INACTIVE_TAB_COLOR);

                /**
                 * add UI components involved with Configuration to the ConfigureContest tabbed pane
                 */

                AccountsPane accountsPane = new AccountsPane();
                addUIPlugin(getConfigureContestTabbedPane(), "Accounts", accountsPane);
                
                CategoriesPane categoriesPane = new CategoriesPane();
                addUIPlugin(getConfigureContestTabbedPane(), "Categories", categoriesPane);

                ContestPreloadPane contestPreloadPane = new ContestPreloadPane();
                addUIPlugin(getConfigureContestTabbedPane(), "Contests", contestPreloadPane);
                
                AutoJudgesPane autoJudgesPane = new AutoJudgesPane();
                addUIPlugin(getConfigureContestTabbedPane(), "Auto Judge", autoJudgesPane);

                GroupsPane groupsPane = new GroupsPane();
                addUIPlugin(getConfigureContestTabbedPane(), "Groups", groupsPane);

                ICPCLoadPane icpcPane = new ICPCLoadPane();
                addUIPlugin(getConfigureContestTabbedPane(), "ICPC", icpcPane);
                
                ImportDataPane importDataPane = new ImportDataPane();
                addUIPlugin(getConfigureContestTabbedPane(), "Import CCS", importDataPane);

                JudgementsPane judgementsPanel = new JudgementsPane();
                addUIPlugin(getConfigureContestTabbedPane(), "Judgements", judgementsPanel);

                LanguagesPane languagesPane = new LanguagesPane();
                addUIPlugin(getConfigureContestTabbedPane(), "Languages", languagesPane);
                
                BalloonSettingsPane balloonSettingsPane = new BalloonSettingsPane();
                addUIPlugin(getConfigureContestTabbedPane(), "Notifications", balloonSettingsPane);

                // XXX bug 417 hide for 9.1 release
                // EOCNotificationsPane eocNotificationsPane = new EOCNotificationsPane();
                // addUIPlugin(getConfigureContestTabbedPane(), "End of Contest Control", eocNotificationsPane);

                if (Utilities.isDebugMode()) {
                    PacketExplorerPane explorerPane = new PacketExplorerPane();
                    addUIPlugin(getConfigureContestTabbedPane(), "Packets", explorerPane);
                }

                ProblemsPane problemsPane = new ProblemsPane();
                addUIPlugin(getConfigureContestTabbedPane(), "Problems", problemsPane);

                if (Utilities.isDebugMode()) {
                    try {
                        ProfilesPane profilesPane = new ProfilesPane();
                        addUIPlugin(getConfigureContestTabbedPane(), "Profiles", profilesPane);
                    } catch (Exception e) {
                        logException(e);
                    }
                }
                

                ReportPane reportPaneC = new ReportPane();
                addUIPlugin(getConfigureContestTabbedPane(), "Reports", reportPaneC);

                ContestInformationPane contestInformationPane = new ContestInformationPane();
                addUIPlugin(getConfigureContestTabbedPane(), "Settings", contestInformationPane);

                ContestTimesPane contestTimesPane = new ContestTimesPane();
                addUIPlugin(getConfigureContestTabbedPane(), "Times", contestTimesPane);

                /**
                 * add UI components involved with Running the contest to the RunContest tabbed pane
                 */

                ConnectionsPane connectionsPane = new ConnectionsPane();
                addUIPlugin(getRunContestTabbedPane(), "Connections", connectionsPane);

                ClarificationsPane clarificationsPane = new ClarificationsPane();
                addUIPlugin(getRunContestTabbedPane(), "Clarifications", clarificationsPane);

                try {
                    EventFeedServerPane eventFeedServerPane = new EventFeedServerPane();
                    addUIPlugin(getRunContestTabbedPane(), "Event Feed Server", eventFeedServerPane);
                } catch (Exception e) {
                    logException(e);
                }
                
//                EventFeedsPane eventFeedsPane = new EventFeedsPane();
//                addUIPlugin(getRunContestTabbedPane(), "Event Feeds", eventFeedsPane);

                ExportDataPane exportPane = new ExportDataPane();
                addUIPlugin(getRunContestTabbedPane(), "Export", exportPane);
                
                FinalizePane finalizePane = new FinalizePane();
                addUIPlugin(getRunContestTabbedPane(), "Finalize", finalizePane);

                LoginsPane loginsPane = new LoginsPane();
                addUIPlugin(getRunContestTabbedPane(), "Logins", loginsPane);
                
                
                try {
                    MessageMonitorPane messageMonitorPane = new MessageMonitorPane();
                    addUIPlugin(getRunContestTabbedPane(), "Messages", messageMonitorPane);
                } catch (Exception e) {
                    logException(e);
                }

                OptionsPane optionsPanel = new OptionsPane();
                addUIPlugin(getRunContestTabbedPane(), "Options", optionsPanel);
                optionsPanel.setSecurityLogWindow(securityAlertLogWindow);

                try {
                    PacketMonitorPane pane = new PacketMonitorPane();
                    addUIPlugin(getRunContestTabbedPane(), "Packets", pane);
                } catch (Exception e) {
                    logException(e);
                }
                
                try {
                    PlaybackPane playbackPane = new PlaybackPane();
                    addUIPlugin(getRunContestTabbedPane(), "Replay", playbackPane);
                } catch (Exception e) {
                    logException(e);
                }

                if (Utilities.isDebugMode()) {
                    try {
                        PluginLoadPane pane = new PluginLoadPane();
                        pane.setParentTabbedPane(getRunContestTabbedPane());
                        addUIPlugin(getRunContestTabbedPane(), "Plugin Load", pane);
                    } catch (Exception e) {
                        logException(e);
                    }

                }

                ReportPane reportPane = new ReportPane();
                addUIPlugin(getRunContestTabbedPane(), "Reports", reportPane);

                RunsPane runsPane = new RunsPane();
                addUIPlugin(getRunContestTabbedPane(), "Runs", runsPane);

                SitesPane sitesPanel = new SitesPane();
                addUIPlugin(getRunContestTabbedPane(), "Sites", sitesPanel);

                StandingsPane standingsPane = new StandingsPane();
                addUIPlugin(getRunContestTabbedPane(), "Standings", standingsPane);

                StandingsHTMLPane standingsHTMLPane = new StandingsHTMLPane("full.xsl");
                addUIPlugin(getRunContestTabbedPane(), "Standings HTML", standingsHTMLPane);

                TeamStatusPane teamStatusPane = new TeamStatusPane();
                addUIPlugin(getRunContestTabbedPane(), "Team Status", teamStatusPane);

                setSelectedTab (getRunContestTabbedPane(), "Runs");
                setSelectedTab (getConfigureContestTabbedPane(), "Accounts");
                
                /**
                 * Clock and frame title.
                 */

                contestClockDisplay = new ContestClockDisplay(controller.getLog(), contest.getContestTime(), contest.getSiteNumber(), false, null);
                contestClockDisplay.setContestAndController(contest, controller);
                contestClockDisplay.addLabeltoUpdateList(clockLabel, DisplayTimes.REMAINING_TIME, contest.getSiteNumber());
                contestClockDisplay.setClientFrame(thisFrame);

                contest.addContestTimeListener(new ContestTimeListenerImplementation());
                controller.register(contestClockDisplay);
                
                FrameUtilities.setFrameTitle(thisFrame, contest.getTitle(), contest.getContestTime().isContestRunning(), new VersionInfo());
                setVisible(true);
            }


     });
    }
    
    /**
     * Set the tab for the input name.
     * 
     * @param tabbedPane
     * @param name
     */
    protected void setSelectedTab(JTabbedPane tabbedPane, String name) {
        
        for (int i = 0; i < tabbedPane.getComponentCount(); i ++){
            String tabTitle = tabbedPane.getTitleAt(i);
//            System.err.println("For "+tabbedPane.getName()+" found "+tabTitle);
            if (tabTitle != null && name.equals(tabTitle)){
//                System.err.println("For "+tabbedPane.getName()+"   selected "+name);
                tabbedPane.setSelectedIndex(i);
            }
        }
    }


    private void logException(Exception e) {

        if (StaticLog.getLog() != null) {
            StaticLog.getLog().log(Log.WARNING, "Exception", e);
            e.printStackTrace(System.err);
        } else {
            e.printStackTrace(System.err);
        }
    }

    protected void initializeSecurityAlertWindow(IInternalContest inContest) {
        if (securityAlertLogWindow == null){
            securityAlertLogWindow = new LogWindow(inContest.getSecurityAlertLog());
        }
        securityAlertLogWindow.setContestAndController(inContest, controller);
        securityAlertLogWindow.setTitle("Contest Security Alerts " + inContest.getClientId().toString());
        VersionInfo versionInfo = new VersionInfo();
        securityAlertLogWindow.getLog().info("Security Log Started "+versionInfo.getSystemVersionInfo());
    }

    public String getPluginTitle() {
        return "Admin GUI";
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel() {
        if (jPanel == null) {
            jPanel = new JPanel();
            jPanel.setLayout(new BorderLayout());
            jPanel.add(getMainTabbedPanel(), java.awt.BorderLayout.CENTER);
            jPanel.add(getTopPanel(), java.awt.BorderLayout.NORTH);
            jPanel.add(getStatusPanel(), java.awt.BorderLayout.SOUTH);
        }
        return jPanel;
    }

    /**
     * This method initializes mainTabbedPanel
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getMainTabbedPanel() {
        if (mainTabbedPanel == null) {
            mainTabbedPanel = new JTabbedPane();
            mainTabbedPanel.addTab(null, null, getConfigureContestTabbedPane(), null);
            mainTabbedPanel.addTab(null, null, getRunContestTabbedPane(), null);
        }
        return mainTabbedPanel;
    }

    /**
     * This method initializes statusPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getStatusPanel() {
        if (statusPanel == null) {
            statusPanel = new JPanel();
            statusPanel.setPreferredSize(new java.awt.Dimension(30, 30));
        }
        return statusPanel;
    }

    /**
     * This method initializes topPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTopPanel() {
        if (topPanel == null) {
            topPanel = new JPanel();
            topPanel.setLayout(new BorderLayout());
            topPanel.setPreferredSize(new java.awt.Dimension(45, 45));
            topPanel.add(getExitButtonPane(), java.awt.BorderLayout.EAST);
            topPanel.add(getPadPane(), java.awt.BorderLayout.WEST);
            topPanel.add(getCenterPane(), BorderLayout.CENTER);
        }
        return topPanel;
    }

    /**
     * This method initializes exitButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getExitButton() {
        if (exitButton == null) {
            exitButton = new JButton();
            exitButton.setText("Exit");
            exitButton.setToolTipText("Click here to Shutdown PC^2");
            exitButton.setMnemonic(java.awt.event.KeyEvent.VK_X);
            exitButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    promptAndExit();
                }
            });
        }
        return exitButton;
    }

    protected void promptAndExit() {
        int result = FrameUtilities.yesNoCancelDialog(null, "Are you sure you want to exit PC^2?", "Exit PC^2");

        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    protected void addUIPlugin(JTabbedPane tabbedPane, String tabTitle, JPanePlugin plugin) {

        // TODO this should throw an exception
        if (plugin == null) {
            return;
        }

        try {
            controller.register (plugin);
            plugin.setParentFrame(this);
            plugin.setContestAndController(contest, controller);
            tabbedPane.add(plugin, tabTitle);

        } catch (Exception e) {
            controller.getLog().log(Log.WARNING, "Exception loading plugin ", e);
            JOptionPane.showMessageDialog(this, "Error loading " + plugin.getPluginTitle());
        }

    }

    protected void showLog(boolean showLogWindow) {
        controller.showLogWindow(showLogWindow);
    }

    /**
     * This method initializes clockPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getClockPane() {
        if (clockPane == null) {
            clockLabel = new JLabel();
            clockLabel.setText("STOPPED");
            clockLabel.setPreferredSize(new Dimension(100, 24));
            clockLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
            clockPane = new JPanel();
            clockPane.setLayout(new BorderLayout());
            clockPane.setPreferredSize(new java.awt.Dimension(85,34));
            clockPane.add(clockLabel, BorderLayout.WEST);
        }
        return clockPane;
    }

    /**
     * This method initializes exitButtonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getExitButtonPane() {
        if (exitButtonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(10);
            flowLayout.setVgap(10);
            exitButtonPane = new JPanel();
            exitButtonPane.setLayout(flowLayout);
            exitButtonPane.add(getExitButton(), null);
        }
        return exitButtonPane;
    }

    /**
     * This method initializes padPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPadPane() {
        if (padPane == null) {
            padPane = new JPanel();
            padPane.setPreferredSize(new java.awt.Dimension(10, 10));
        }
        return padPane;
    }

    /**
     * This method initializes configureContestTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getConfigureContestTabbedPane() {
        if (configureContestTabbedPane == null) {
            configureContestTabbedPane = new JTabbedPane();
            configureContestTabbedPane.setToolTipText("");
            configureContestTabbedPane.setName("Configure Contest");
        }
        return configureContestTabbedPane;
    }

    /**
     * This method initializes runContestTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getRunContestTabbedPane() {
        if (runContestTabbedPane == null) {
            runContestTabbedPane = new JTabbedPane();
        }
        return runContestTabbedPane;
    }

    public void stateChanged (ChangeEvent e) {
        if (e.getSource()==getMainTabbedPanel()) {
            //change all mainpanel tab text to black
            int tabCount = getMainTabbedPanel().getTabCount();
            for (int i=0; i<tabCount; i++) {
                getMainTabbedPanel().setForegroundAt(i,INACTIVE_TAB_COLOR);
            }
            //change the currently selected mainpanel tab to red 
            int selectedTab = getMainTabbedPanel().getSelectedIndex();
            getMainTabbedPanel().setForegroundAt( selectedTab, ACTIVE_TAB_COLOR);
        } else {
            throw new RuntimeException ("Unexpected ChangeEvent: " + e);
        }
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
            centerPane.add(getClockPane(), BorderLayout.WEST);
            centerPane.add(getAMessagePane(), BorderLayout.CENTER);
        }
        return centerPane;
    }

    /**
     * This method initializes aMessagePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAMessagePane() {
        if (aMessagePane == null) {
            messageLabel = new JLabel();
            messageLabel.setText("");
            messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            messageLabel.setFont(new Font("Dialog", Font.BOLD, 18));
            messageLabel.setForeground(new Color(0, 186, 0));
            messageLabel.setHorizontalTextPosition(SwingConstants.CENTER);
            aMessagePane = new JPanel();
            aMessagePane.setLayout(new BorderLayout());
            aMessagePane.add(messageLabel, BorderLayout.CENTER);
        }
        return aMessagePane;
    }

    public static void main(String[] args) {
        AdministratorView administratorView = new AdministratorView();
        administratorView.setVisible(true);
    }
    
    protected boolean isThisSite(int siteNumber) {
        return contest.getSiteNumber() == siteNumber;
    }
    
    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    class ContestTimeListenerImplementation implements IContestTimeListener {

        public void contestTimeAdded(ContestTimeEvent event) {
            contestTimeChanged(event);
        }

        public void contestTimeRemoved(ContestTimeEvent event) {
            contestTimeChanged(event);
        }

        public void contestTimeChanged(ContestTimeEvent event) {
            ContestTime contestTime = event.getContestTime();
            if (isThisSite(contestTime.getSiteNumber())) {
                setFrameTitle(contestTime.isContestRunning());
            }
        }

        public void contestStarted(ContestTimeEvent event) {
            contestTimeChanged(event);
        }

        public void contestStopped(ContestTimeEvent event) {
            contestTimeChanged(event);
        }

        public void refreshAll(ContestTimeEvent event) {
            contestTimeChanged(event);
        }

    }

    private void setFrameTitle(final boolean contestStarted) {
        final JFrame thisFrame = this;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                
                FrameUtilities.setFrameTitle(thisFrame, contest.getTitle(), contestStarted, new VersionInfo());
                if (contestStarted) {
                    contestClockDisplay.fireClockStateChange(contest.getContestTime());
                } else {
                    clockLabel.setText("STOPPED");
                }

                if (contestClockDisplay.getClientFrame() == null) {
                    contestClockDisplay.setClientFrame(thisFrame);
                }
            }
        });

        FrameUtilities.regularCursor(this);
    }
    
    private void updateProfileLabel() {

        int numberProfiles = contest.getProfiles().length;

        String s = "";

        if (numberProfiles > 1) {
            s = "Active Profile is: \"" + contest.getProfile().getName() + "\"";
        }
        final String message = s;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(message);
            }
        });
    }
    
    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    protected class ProfileListenerImplementation implements IProfileListener {

        public void profileAdded(ProfileEvent event) {
            updateProfileLabel();
        }

        public void profileChanged(ProfileEvent event) {
            updateProfileLabel();
        }

        public void profileRemoved(ProfileEvent event) {
            // ignore
            
        }

        public void profileRefreshAll(ProfileEvent profileEvent) {
            updateProfileLabel();
        }
    }
        



} // @jve:decl-index=0:visual-constraint="10,10"
