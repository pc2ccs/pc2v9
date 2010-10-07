package edu.csus.ecs.pc2.ui.server;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.IniFile;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.ILoginListener;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.ISiteListener;
import edu.csus.ecs.pc2.core.model.LoginEvent;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.SiteEvent;
import edu.csus.ecs.pc2.plugin.ContestSummaryReports;
import edu.csus.ecs.pc2.ui.ConnectionsPane;
import edu.csus.ecs.pc2.ui.ContestTimesPane;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.JPanePlugin;
import edu.csus.ecs.pc2.ui.LoadContestPane;
import edu.csus.ecs.pc2.ui.LogWindow;
import edu.csus.ecs.pc2.ui.LoginsPane;
import edu.csus.ecs.pc2.ui.OptionsPanel;
import edu.csus.ecs.pc2.ui.PacketMonitorPane;
import edu.csus.ecs.pc2.ui.PlaybackPane;
import edu.csus.ecs.pc2.ui.PluginLoadPane;
import edu.csus.ecs.pc2.ui.ProfilesPane;
import edu.csus.ecs.pc2.ui.ReportPane;
import edu.csus.ecs.pc2.ui.SitesPanel;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * GUI for Server.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ServerView extends JFrame implements UIPlugin {

    public static final String SVN_ID = "$Id$";

    private IInternalContest model = null;

    private IInternalController controller = null;

    /**
     * 
     */
    private static final long serialVersionUID = 4547574494017009634L;

    private JPanel mainViewPane = null;

    private JTabbedPane mainTabbedPane = null;

    private Log log = null;

    private JPanel messagePanel = null;

    private JLabel messageLabel = null;

    private JPanel exitPanel = null;

    private JButton exitButton = null;

    private LogWindow securityAlertLogWindow = null;

    /**
     * This method initializes
     * 
     */
    public ServerView() {
        super();
        initialize();
    }

    private void logDebugMessage(String message) {
        log.log(Log.DEBUG, message);
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(518, 400));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setTitle("Server View");
        this.setContentPane(getMainViewPane());
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                promptAndExit();
            }
        });

        overRideLookAndFeel();
        FrameUtilities.centerFrameTop(this);
        setVisible(true);
        
        VersionInfo versionInfo = new VersionInfo();
        showMessage("Version "+versionInfo.getVersionNumber()+" (Build "+versionInfo.getBuildNumber()+")");
    }
    
    private void overRideLookAndFeel(){
        // TODO eventually move this method to on location 
        String value = IniFile.getValue("client.plaf");
        if (value != null && value.equalsIgnoreCase("java")){
            FrameUtilities.setJavaLookAndFeel();
        }
        if (value != null && value.equalsIgnoreCase("native")){
            FrameUtilities.setNativeLookAndFeel();
        }
    }


    protected void promptAndExit() {
        int result = FrameUtilities.yesNoCancelDialog(null, "Are you sure you want to exit PC^2?", "Exit PC^2 Server Module");

        if (result == JOptionPane.YES_OPTION) {

            ContestSummaryReports contestReports = new ContestSummaryReports();
            contestReports.setContestAndController(model, controller);
            
            if (contestReports.isLateInContest()){
                contestReports.generateReports();
                controller.getLog().info("Reports Generated to "+contestReports.getReportDirectory());
            }
            
            log.info("Server "+model.getSiteNumber()+" halted");
            System.exit(0);
        }
    }

    /**
     * Implementation for the Run Listener.
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    private class RunListenerImplementation implements IRunListener {

        public void runAdded(RunEvent event) {
            logDebugMessage("Run Event " + event.getRun() + " ADDED ");
        }

        public void refreshRuns(RunEvent event) {
            logDebugMessage("Run Event " + event.getRun() + " REFRESH/RESET RUNS ");
            
        }
        
        public void runChanged(RunEvent event) {
            logDebugMessage("Run Event " + event.getRun() + " CHANGED ");
        }
        
        public void runRemoved(RunEvent event) {
            logDebugMessage("Run Event " + event.getRun() + " REMOVED ");
        }
    }

    private String accountText(Account account) {
        if (account == null) {
            return "null";
        } else {
            return account.getClientId().toString();
        }
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    public class LoginListenerImplementation implements ILoginListener {

        public void loginAdded(LoginEvent event) {
            logDebugMessage("Login Event " + event.getAction() + " " + event.getClientId());
        }

        public void loginRemoved(LoginEvent event) {
            logDebugMessage("Login Event " + event.getAction() + " " + event.getClientId());
        }

        public void loginDenied(LoginEvent event) {
            logDebugMessage("Login Event " + event.getAction() + " " + event.getClientId());
        }
        
        public void loginRefreshAll(LoginEvent event) {
            logDebugMessage("Login Event " + event.getAction() + " " + event.getClientId());
        }
    }

    /**
     * Implementation for a Account Event Listener.
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    public class AccountListenerImplementation implements IAccountListener {

        public void accountAdded(AccountEvent accountEvent) {
            logDebugMessage("Account Event " + accountEvent.getAction() + " " + accountText(accountEvent.getAccount()));

        }

        public void accountModified(AccountEvent accountEvent) {
            logDebugMessage("Account Event " + accountEvent.getAction() + " " + accountText(accountEvent.getAccount()));

        }

        public void accountsAdded(AccountEvent accountEvent) {
            logDebugMessage("Account Event " + accountEvent.getAction() + " " + accountEvent.getAccounts().length + " accounts");
        }

        public void accountsModified(AccountEvent accountEvent) {
            logDebugMessage("Account Event " + accountEvent.getAction() + " " + accountEvent.getAccounts().length + " accounts");
        }

        public void accountsRefreshAll(AccountEvent accountEvent) {
            logDebugMessage("Account Event " + accountEvent.getAction() + " " + accountEvent.getAccounts().length + " accounts");
        }
    }

    /**
     * Site Listener for use by ServerView.
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    public class SiteListenerImplementation implements ISiteListener {

        public void siteAdded(SiteEvent event) {
            logDebugMessage("Site Event Event " + event.getAction() + " " + event.getSite());
        }

        public void siteRemoved(SiteEvent event) {
            logDebugMessage("Site Event " + event.getAction() + " " + event.getSite());
        }

        public void siteLoggedOn(SiteEvent event) {
            logDebugMessage("Site Event " + event.getAction() + " " + event.getSite());
        }

        public void siteLoggedOff(SiteEvent event) {
            logDebugMessage("Site Event " + event.getAction() + " " + event.getSite());
        }

        public void siteChanged(SiteEvent event) {
            logDebugMessage("Site Event " + event.getAction() + " " + event.getSite());
        }

        public void sitesRefreshAll(SiteEvent event) {
            logDebugMessage("Site Event " + event.getAction());
        }
    }

    /**
     * InternalContest Time for use by ServerView.
     * 
     * @author pc2@ecs.csus.edu
     *
     */
    public class ContestTimeListenerImplementation implements IContestTimeListener {

        private int ignoreEvent = 0;
        public void contestTimeAdded(ContestTimeEvent event) {
            ignoreEvent++;
        }

        public void contestTimeRemoved(ContestTimeEvent event) {
            ignoreEvent++;
        }

        public void contestTimeChanged(ContestTimeEvent event) {
            ignoreEvent++;
        }

        public void contestStarted(ContestTimeEvent event) {
            // only update our title if the event concerns us
            if (model.getSiteNumber() == event.getSiteNumber()) {
                updateFrameTitle(event.getContestTime().isContestRunning());
            }
        }

        public void contestStopped(ContestTimeEvent event) {
            // only update our title if the event concerns us
            if (model.getSiteNumber() == event.getSiteNumber()) {
                updateFrameTitle(event.getContestTime().isContestRunning());
            }
        }

        public void refreshAll(ContestTimeEvent event) {
            if (model.getSiteNumber() == event.getSiteNumber()) {
                updateFrameTitle(event.getContestTime().isContestRunning());
            }
        }
    }
    /**
     * This method initializes mainViewPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainViewPane() {
        if (mainViewPane == null) {
            mainViewPane = new JPanel();
            mainViewPane.setLayout(new BorderLayout());
            mainViewPane.add(getMainTabbedPane(), java.awt.BorderLayout.CENTER);
            mainViewPane.add(getMessagePanel(), java.awt.BorderLayout.NORTH);
        }
        return mainViewPane;
    }

    /**
     * Puts this frame to right of input frame.
     * 
     * @param sourceFrame
     */
    public void windowToRight(JFrame sourceFrame) {
        int rightX = sourceFrame.getX() + sourceFrame.getWidth();
        setLocation(rightX, getY());
    }

    /**
     * This method initializes mainTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getMainTabbedPane() {
        if (mainTabbedPane == null) {
            mainTabbedPane = new JTabbedPane();
        }
        return mainTabbedPane;
    }

    void updateFrameTitle(final boolean turnButtonsOn) {
        final Frame thisFrame = this;
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                FrameUtilities.setFrameTitle(thisFrame, "Server (Site "+model.getSiteNumber()+")", turnButtonsOn, new VersionInfo());
            }
        });
    }
    
    protected void registerPlugin (UIPlugin plugin){
        try {
            controller.register (plugin);
            plugin.setContestAndController(model, controller);

        } catch (Exception e) {
            controller.getLog().log(Log.WARNING, "Exception loading plugin ", e);
            e.printStackTrace(); // FIXME deubug22
            JOptionPane.showMessageDialog(this, "Error loading " + plugin.getPluginTitle());
        }
    }
    
    /**
     * Listeners for server view.
     * 
     * This provides a way to refresh the server view on refresh.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    protected class ServerListeners implements UIPlugin {

        /**
         * 
         */
        private static final long serialVersionUID = 3733076435840880891L;

        public void setContestAndController(IInternalContest inContest, IInternalController inController) {
            
            inContest.addRunListener(new RunListenerImplementation());
            inContest.addAccountListener(new AccountListenerImplementation());
            inContest.addLoginListener(new LoginListenerImplementation());
            inContest.addSiteListener(new SiteListenerImplementation());
            inContest.addContestTimeListener(new ContestTimeListenerImplementation());
        }

        public String getPluginTitle() {
            return "ServerListeners";
        }
    }
    
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.model = inContest;
        this.controller = inController;
        this.log = controller.getLog();

        updateFrameTitle(model.getContestTime().isContestRunning());

        controller.startLogWindow(model);
        
        initializeSecurityAlertWindow(inContest);

        ServerListeners serverListeners = new ServerListeners();
        registerPlugin(serverListeners);

        ConnectionsPane connectionsPane = new ConnectionsPane();
        addUIPlugin(getMainTabbedPane(), "Connections", connectionsPane);

        if (Utilities.isDebugMode()) {
            try {
                LoadContestPane loadContestPane = new LoadContestPane();
                addUIPlugin(getMainTabbedPane(), "Load v8", loadContestPane);
            } catch (Exception e) {
                logException(e);
            }
        }
        
        LoginsPane loginsPane = new LoginsPane();
        addUIPlugin(getMainTabbedPane(), "Logins", loginsPane);

        OptionsPanel optionsPanel = new OptionsPanel();
        addUIPlugin(getMainTabbedPane(), "Options", optionsPanel);
        optionsPanel.setSecurityLogWindow(securityAlertLogWindow);

        if (Utilities.isDebugMode()) {
            try {
                PacketMonitorPane packetMonitorPane = new PacketMonitorPane();
                addUIPlugin(getMainTabbedPane(), "Packets", packetMonitorPane);
            } catch (Exception e) {
                logException(e);
            }
        }            

        if (Utilities.isDebugMode()) {
            try {
                PluginLoadPane pane = new PluginLoadPane();
                pane.setParentTabbedPane(getMainTabbedPane());
                addUIPlugin(getMainTabbedPane(), "Plugin Load", pane);
            } catch (Exception e) {
                logException(e);
            }
        }            

        ProfilesPane profilePane = new ProfilesPane();
        addUIPlugin(getMainTabbedPane(), "Profiles", profilePane);

        PlaybackPane playbackPane = new PlaybackPane();
        addUIPlugin(getMainTabbedPane(), "Replay", playbackPane);
        
        ReportPane reportPane = new ReportPane();
        addUIPlugin(getMainTabbedPane(), "Reports", reportPane);

        SitesPanel sitesPanel = new SitesPanel();
        addUIPlugin(getMainTabbedPane(), "Sites", sitesPanel);

        ContestTimesPane contestTimesPane = new ContestTimesPane();
        addUIPlugin(getMainTabbedPane(), "Times", contestTimesPane);
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
        return "Server Main GUI";
    }

    protected void showLog(boolean showLogWindow) {
        controller.showLogWindow(showLogWindow);
    }

    protected void addUIPlugin(JTabbedPane tabbedPane, String tabTitle, JPanePlugin plugin) {

        try {
            plugin.setParentFrame(this);
            registerPlugin(plugin);
            tabbedPane.add(plugin, tabTitle);
        } catch (Exception e) {
            controller.getLog().log(Log.WARNING, "Exception loading plugin ", e);
            JOptionPane.showMessageDialog(this, "Error loading " + plugin.getPluginTitle());
        }

    }

    /**
     * This method initializes messagePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePanel() {
        if (messagePanel == null) {
            BorderLayout borderLayout = new BorderLayout();
            borderLayout.setHgap(5);
            messageLabel = new JLabel();
            messageLabel.setText("");
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messagePanel = new JPanel();
            messagePanel.setLayout(borderLayout);
            messagePanel.setPreferredSize(new java.awt.Dimension(40, 40));
            messagePanel.add(messageLabel, java.awt.BorderLayout.CENTER);
            messagePanel.add(getExitPanel(), java.awt.BorderLayout.EAST);
        }
        return messagePanel;
    }

    /**
     * This method initializes exitPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getExitPanel() {
        if (exitPanel == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(10);
            flowLayout.setVgap(5);
            exitPanel = new JPanel();
            exitPanel.setPreferredSize(new java.awt.Dimension(75, 36));
            exitPanel.setLayout(flowLayout);
            exitPanel.add(getExitButton(), null);
        }
        return exitPanel;
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
            exitButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    promptAndExit();
                }
            });
        }
        return exitButton;
    }

    private void showMessage(final String string) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(string);
            }
        });

    }

} // @jve:decl-index=0:visual-constraint="10,10"
