// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui.server;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Dialog.ModalityType;
import java.awt.event.KeyEvent;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
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
import edu.csus.ecs.pc2.core.model.IProfileListener;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.ISiteListener;
import edu.csus.ecs.pc2.core.model.LoginEvent;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.model.ProfileEvent;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.SiteEvent;
import edu.csus.ecs.pc2.core.report.ContestSummaryReports;
import edu.csus.ecs.pc2.ui.AboutPane;
import edu.csus.ecs.pc2.ui.ConnectionsPane;
import edu.csus.ecs.pc2.ui.ContestTimesPane;
import edu.csus.ecs.pc2.ui.EventFeedServerPane;
import edu.csus.ecs.pc2.ui.ExportDataPane;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.JPanePlugin;
import edu.csus.ecs.pc2.ui.LoadContestPane;
import edu.csus.ecs.pc2.ui.LogWindow;
import edu.csus.ecs.pc2.ui.LoginsPane;
import edu.csus.ecs.pc2.ui.MessageMonitorPane;
import edu.csus.ecs.pc2.ui.OptionsPane;
import edu.csus.ecs.pc2.ui.PacketMonitorPane;
import edu.csus.ecs.pc2.ui.PlaybackPane;
import edu.csus.ecs.pc2.ui.PluginLoadPane;
import edu.csus.ecs.pc2.ui.ProfilesPane;
import edu.csus.ecs.pc2.ui.ReportPane;
import edu.csus.ecs.pc2.ui.SitesPane;
import edu.csus.ecs.pc2.ui.UIPlugin;
import java.awt.Dimension;

/**
 * GUI for Server.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ServerView extends JFrame implements UIPlugin {

    public static final String SVN_ID = "$Id$";

    private IInternalContest model = null;  //  @jve:decl-index=0:

    private IInternalController controller = null;  //  @jve:decl-index=0:

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
    
    private VersionInfo versionInfo = new VersionInfo();

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
        this.setSize(new Dimension(800, 450));
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
        
        showMessage("Version "+versionInfo.getVersionNumber()+" (Build "+versionInfo.getBuildNumber()+")");
        

    }
    
    private void overRideLookAndFeel(){
        
        String value = IniFile.getValue("server.plaf");
        if (value != null && value.equalsIgnoreCase("java")){
            FrameUtilities.setJavaLookAndFeel();
        }
        if (value != null && value.equalsIgnoreCase("native")){
            FrameUtilities.setNativeLookAndFeel();
        }
    }


    protected void promptAndExit() {
        int result = FrameUtilities.yesNoCancelDialog(this, "Are you sure you want to exit PC^2?", "Exit PC^2 Server Module");

        if (result == JOptionPane.YES_OPTION) {

            try {
                ContestSummaryReports contestReports = new ContestSummaryReports();
                contestReports.setContestAndController(model, controller);
            
                if (contestReports.isLateInContest()){
                    contestReports.generateReports();
                    controller.getLog().info("Reports Generated to "+contestReports.getReportDirectory());
                }
            } catch (Exception e) {
                log.log(Log.WARNING,"Unable to create reports ", e);
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

        public void siteProfileStatusChanged(SiteEvent event) {
            // TODO this UI does not use a change in profile status 
        }

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

        public void contestTimeAdded(ContestTimeEvent event) {
        }

        public void contestTimeRemoved(ContestTimeEvent event) {
        }

        public void contestTimeChanged(ContestTimeEvent event) {
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
        
        /** This method exists to support differentiation between manual and automatic starts.
         * Currently it delegates the handling to the contestStarted() method, while
         * also popping up a notification dialog if the Server is in GUI mode and also
         * logging the auto-start event.
         */
        @Override
        public void contestAutoStarted(ContestTimeEvent event) {
            contestStarted(event);
            
            //log the automatic-start
            info("Contest automatically started due to arrival of enabled scheduled start time.");
            
            //if using a GUI, display a popup notification of the automatic-start
            if (controller != null && controller.isUsingGUI()) {
                //Note: previously the following line of code was used for the popup; however, showMessageDialog() does
                // not allow to call setAlwaysOnTop()
                // JOptionPane.showMessageDialog(null, "Scheduled Start Time has arrived; contest has been automatically started!", "Contest Started",
                // JOptionPane.INFORMATION_MESSAGE);

                JOptionPane optionPane = new JOptionPane();
                optionPane.setMessage("Scheduled Start Time has arrived; contest has been automatically started!");
                optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
                JDialog dialog = optionPane.createDialog("Contest Started");
                dialog.setLocationRelativeTo(null); // center the dialog
                dialog.setModalityType(ModalityType.APPLICATION_MODAL);

                // force the notification dialog to always be on top even if the Admin isn't the active program
                // (Note: this works on Windows; it may not work under Linux -- and/or it may be window-system dependent...)
                dialog.setAlwaysOnTop(true);

                dialog.setVisible(true);

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
     * @param sourceFrame the JFrame to which this frame is attached (that is,
     *   the frame in which this frame will be moved to the right)
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
            inContest.addProfileListener(new ProfileListenerImplementation());
            
            setModel(inContest);
            setController(inController);
            
            populateUI();
        }

        public String getPluginTitle() {
            return "ServerListeners";
        }
    }
    
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        
        setModel(inContest);
        setController(inController);
        
        this.log = controller.getLog();
        
        populateUI();

        inController.startLogWindow(model);
        
        initializeSecurityAlertWindow(inContest);

        ServerListeners serverListeners = new ServerListeners();
        registerPlugin(serverListeners);

        ConnectionsPane connectionsPane = new ConnectionsPane();
        addUIPlugin(getMainTabbedPane(), "Connections", connectionsPane);

        if (Utilities.isDebugMode()) {
            try {
                EventFeedServerPane eventFeedServerPane = new EventFeedServerPane();
                addUIPlugin(getMainTabbedPane(), "Event Feed Server", eventFeedServerPane);
            } catch (Exception e) {
                logException(e);
            }
        }
        
        ExportDataPane exportPane = new ExportDataPane();
        addUIPlugin(getMainTabbedPane(), "Export", exportPane);
        
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

        if (Utilities.isDebugMode()) {
            try {
                MessageMonitorPane messageMonitorPane = new MessageMonitorPane();
                addUIPlugin(getMainTabbedPane(), "Messages", messageMonitorPane);
            } catch (Exception e) {
                logException(e);
            }
        }

        OptionsPane optionsPanel = new OptionsPane();
        addUIPlugin(getMainTabbedPane(), "Options", optionsPanel);
        optionsPanel.setSecurityLogWindow(securityAlertLogWindow);

        if (Utilities.isDebugMode()) {
            try {
                PacketMonitorPane packetMonitorPane = new PacketMonitorPane();
                addUIPlugin(getMainTabbedPane(), "Packets", packetMonitorPane);
                
                PluginLoadPane pane = new PluginLoadPane();
                pane.setParentTabbedPane(getMainTabbedPane());
                addUIPlugin(getMainTabbedPane(), "Plugin Load", pane);
                
                ProfilesPane profilePane = new ProfilesPane();
                addUIPlugin(getMainTabbedPane(), "Profiles", profilePane);
                
                PlaybackPane playbackPane = new PlaybackPane();
                addUIPlugin(getMainTabbedPane(), "Replay", playbackPane);
            } catch (Exception e) {
                logException(e);
            }
        }            

        ReportPane reportPane = new ReportPane();
        addUIPlugin(getMainTabbedPane(), "Reports", reportPane);

        SitesPane sitesPanel = new SitesPane();
        addUIPlugin(getMainTabbedPane(), "Sites", sitesPanel);

        ContestTimesPane contestTimesPane = new ContestTimesPane();
        addUIPlugin(getMainTabbedPane(), "Times", contestTimesPane);
        
        AboutPane aboutPane = new AboutPane();
        addUIPlugin(getMainTabbedPane(), "About", aboutPane);
        
        setSelectedTab(getMainTabbedPane(), "Logins");
        
        Profile profile = inContest.getProfile();
        info("Using Profile: " + profile.getName() + " @ " + profile.getProfilePath());

    }
    
    private void info(String string) {
        System.out.println(new Date() + " " + string);
        if (log != null) {
            log.info(string);  
        }
    }

    /**
     * Update frame title and profile label.
     * 
     */
    private void populateUI() {
        updateFrameTitle(model.getContestTime().isContestRunning());
        updateProfileLabel();
    }

    public void setModel(IInternalContest inContest) {
        this.model = inContest;
    }
    
    public void setController(IInternalController controller) {
        this.controller = controller;
    }

    /**
     * Set the tab for the input name.
     * 
     * @param tabbedPane
     * @param name
     */
    protected void setSelectedTab(final JTabbedPane tabbedPane, final String name) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < tabbedPane.getComponentCount(); i++) {
                    String tabTitle = tabbedPane.getTitleAt(i);
                    if (tabTitle != null && name.equals(tabTitle)) {
                        tabbedPane.setSelectedIndex(i);
                    }
                }
            }
        });
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
    
    protected void addUIPlugin(String tabTitle, JPanePlugin plugin) {
        try {
            plugin.setParentFrame(this);
            registerPlugin(plugin);
            getMainTabbedPane().add(plugin, tabTitle);
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
            messageLabel.setFont(new Font("Dialog", Font.BOLD, 14));
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() > 1){
//                        System.out.println("debug profile is "+model.getProfile().getName());
                        updateProfileLabel();
                    }
                }
            });
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
            exitButton.setMnemonic(KeyEvent.VK_X);
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
    
    private void updateProfileLabel() {

        int numberProfiles = model.getProfiles().length;

        String s = "";

        if (numberProfiles > 1) {
            s = "Active Profile is: \"" + model.getProfile().getName() + "\"";
        }
        final String message = s;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(message);
                messageLabel.setToolTipText("Version "+versionInfo.getVersionNumber()+" (Build "+versionInfo.getBuildNumber()+")");
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
