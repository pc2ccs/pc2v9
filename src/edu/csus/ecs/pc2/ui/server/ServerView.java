package edu.csus.ecs.pc2.ui.server;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.ILoginListener;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.ISiteListener;
import edu.csus.ecs.pc2.core.model.LoginEvent;
import edu.csus.ecs.pc2.core.model.ReportPane;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.SiteEvent;
import edu.csus.ecs.pc2.ui.ConnectionsPane;
import edu.csus.ecs.pc2.ui.ContestTimesPane;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.JPanePlugin;
import edu.csus.ecs.pc2.ui.LogWindow;
import edu.csus.ecs.pc2.ui.LoginsPane;
import edu.csus.ecs.pc2.ui.OptionsPanel;
import edu.csus.ecs.pc2.ui.SitesPanel;
import edu.csus.ecs.pc2.ui.UIPlugin;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.FlowLayout;

/**
 * GUI for Server.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class ServerView extends JFrame implements UIPlugin {

    public static final String SVN_ID = "$Id$";

    private IContest model = null;

    // TODO remove @SuppressWarnings for controller
    @SuppressWarnings("unused")
    private IController controller = null;

    /**
     * 
     */
    private static final long serialVersionUID = 4547574494017009634L;

    private JPanel mainViewPane = null;

    private JTabbedPane mainTabbedPane = null;

    private LogWindow logWindow = null;

    private Log log = null;

    private JPanel messagePanel = null;

    private JLabel messageLabel = null;

    private JPanel exitPanel = null;

    private JButton exitButton = null;

    /**
     * This method initializes
     * 
     */
    public ServerView() {
        super();
        initialize();
    }

    private void logDebugMessage(String message) {
        System.err.println(message);
        log.log(Log.DEBUG, message);
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(518, 327));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setTitle("Server View");
        this.setContentPane(getMainViewPane());
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                promptAndExit();
            }
        });

        FrameUtilities.centerFrameTop(this);
        setVisible(true);
        
        VersionInfo versionInfo = new VersionInfo();
        showMessage("Version "+versionInfo.getVersionNumber()+" (Build "+versionInfo.getBuildNumber()+")");
    }

    protected void promptAndExit() {
        int result = FrameUtilities.yesNoCancelDialog("Are you sure you want to exit PC^2?", "Exit PC^2 Server Module");

        if (result == JOptionPane.YES_OPTION) {
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
            logDebugMessage(event.getRun() + " ADDED ");
        }

        public void runChanged(RunEvent event) {
            logDebugMessage(event.getRun() + " CHANGED ");
        }

        public void runRemoved(RunEvent event) {
            logDebugMessage(event.getRun() + " REMOVED ");
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
            logDebugMessage("Login " + event.getAction() + " " + event.getClientId());
        }

        public void loginRemoved(LoginEvent event) {
            logDebugMessage("Login " + event.getAction() + " " + event.getClientId());
        }

        public void loginDenied(LoginEvent event) {
            logDebugMessage("Login " + event.getAction() + " " + event.getClientId());
        }
    }

    /**
     * Implementation for a Account Listener.
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    public class AccountListenerImplementation implements IAccountListener {

        public void accountAdded(AccountEvent accountEvent) {
            logDebugMessage("Account " + accountEvent.getAction() + " " + accountText(accountEvent.getAccount()));

        }

        public void accountModified(AccountEvent accountEvent) {
            logDebugMessage("Account " + accountEvent.getAction() + " " + accountText(accountEvent.getAccount()));

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
            logDebugMessage("Site " + event.getAction() + " " + event.getSite());
        }

        public void siteRemoved(SiteEvent event) {
            logDebugMessage("Site " + event.getAction() + " " + event.getSite());
        }

        public void siteLoggedOn(SiteEvent event) {
            logDebugMessage("Site " + event.getAction() + " " + event.getSite());
        }

        public void siteLoggedOff(SiteEvent event) {
            logDebugMessage("Site " + event.getAction() + " " + event.getSite());
        }

        public void siteChanged(SiteEvent event) {
            logDebugMessage("Site " + event.getAction() + " " + event.getSite());
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

    private void updateFrameTitle(final boolean turnButtonsOn) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (turnButtonsOn) {
                    setTitle("PC^2 Site " + model.getSiteNumber() + " [STARTED] Build " + new VersionInfo().getBuildNumber());
                } else {
                    setTitle("PC^2 Site " + model.getSiteNumber() + " [STOPPED] Build " + new VersionInfo().getBuildNumber());
                }
            }
        });
    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.model = inContest;
        this.controller = inController;
        this.log = controller.getLog();

        updateFrameTitle(model.getContestTime().isContestRunning());

        if (logWindow == null) {
            logWindow = new LogWindow();
        }
        logWindow.setContestAndController(model, controller);
        logWindow.setTitle("Log " + model.getClientId().toString());

        model.addRunListener(new RunListenerImplementation());
        model.addAccountListener(new AccountListenerImplementation());
        model.addLoginListener(new LoginListenerImplementation());
        model.addSiteListener(new SiteListenerImplementation());

        SitesPanel sitesPanel = new SitesPanel();
        addUIPlugin(getMainTabbedPane(), "Sites", sitesPanel);

        ContestTimesPane contestTimesPane = new ContestTimesPane();
        addUIPlugin(getMainTabbedPane(), "Times", contestTimesPane);

        LoginsPane loginsPane = new LoginsPane();
        addUIPlugin(getMainTabbedPane(), "Logins", loginsPane);

        ConnectionsPane connectionsPane = new ConnectionsPane();
        addUIPlugin(getMainTabbedPane(), "Connections", connectionsPane);

        ReportPane reportPane = new ReportPane();
        addUIPlugin(getMainTabbedPane(), "Reports", reportPane);

        OptionsPanel optionsPanel = new OptionsPanel();
        addUIPlugin(getMainTabbedPane(), "Options", optionsPanel);
        optionsPanel.setLogWindow(logWindow);

    }

    public String getPluginTitle() {
        return "Server Main GUI";
    }

    protected void showLog(boolean showLogWindow) {
        logWindow.setVisible(showLogWindow);
    }

    protected void addUIPlugin(JTabbedPane tabbedPane, String tabTitle, JPanePlugin plugin) {

        plugin.setContestAndController(model, controller);
        tabbedPane.add(plugin, tabTitle);

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
