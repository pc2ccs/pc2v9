package edu.csus.ecs.pc2.ui.server;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.ILoginListener;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.ISiteListener;
import edu.csus.ecs.pc2.core.model.LoginEvent;
import edu.csus.ecs.pc2.core.model.ReportPane;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.SiteEvent;
import edu.csus.ecs.pc2.ui.ContestTimesPane;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.JPanePlugin;
import edu.csus.ecs.pc2.ui.LogWindow;
import edu.csus.ecs.pc2.ui.LoginsPane;
import edu.csus.ecs.pc2.ui.OptionsPanel;
import edu.csus.ecs.pc2.ui.SitesPanel;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * GUI for Server.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class ServerView extends JFrame implements UIPlugin {

    public static final String SVN_ID = "$Id$";

    private IModel model = null;

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
        setVisible(true);

        FrameUtilities.centerFrameTop(this);
    }

    protected void promptAndExit() {
        int result = FrameUtilities.yesNoCancelDialog("Are you sure you want to exit PC^2?", "Exit PC^2");

        if (result == JOptionPane.YES_OPTION) {
            System.err.println("Server halting");
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

    public void setModelAndController(IModel inModel, IController inController) {
        this.model = inModel;
        this.controller = inController;
        this.log = controller.getLog();
        
        setTitle("PC^2 Server (Site " + model.getSiteNumber() + ")");
        
        if (logWindow == null) {
            logWindow = new LogWindow();
        }
        logWindow.setModelAndController(model, controller);
        logWindow.setTitle("Log "+model.getClientId().toString());

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

        plugin.setModelAndController(model, controller);
        tabbedPane.add(plugin, tabTitle);

    }


} // @jve:decl-index=0:visual-constraint="10,10"
