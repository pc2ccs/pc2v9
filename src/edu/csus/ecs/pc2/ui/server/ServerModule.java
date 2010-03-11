package edu.csus.ecs.pc2.ui.server;

import java.io.PrintStream;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.ILoginListener;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.ISiteListener;
import edu.csus.ecs.pc2.core.model.LoginEvent;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.SiteEvent;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * non-GUI Server.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ServerModule implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private IInternalContest model;

    private IInternalController controller;

    private Log log;

    public ServerModule() {
        writeVersionInfo(System.out);
    }

    private void writeVersionInfo(PrintStream printStream) {
        VersionInfo versionInfo = new VersionInfo();

        printStream.println(versionInfo.getSystemName());
        printStream.println("Date: " + Utilities.getL10nDateTime());
        printStream.println(versionInfo.getSystemVersionInfo());
        printStream.println();
    }

    public String getPluginTitle() {
        return "Server (non-GUI)";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.model = inContest;
        this.controller = inController;
        this.log = controller.getLog();

        model.addRunListener(new RunListenerImplementation());
        // model.addAccountListener(new AccountListenerImplementation());
        model.addLoginListener(new LoginListenerImplementation());
        model.addSiteListener(new SiteListenerImplementation());
        model.addContestTimeListener(new ContestTimeListenerImplementation());

        info("Server started");
        
    }

    // public class AccountListenerImplementation implements IAccountListener { }

    private void info(String string) {
        System.out.println(string);
        log.info(string);
    }

    private class RunListenerImplementation implements IRunListener {

        public void runAdded(RunEvent event) {
            // TODO Auto-generated method stub

        }

        public void runChanged(RunEvent event) {
            // TODO Auto-generated method stub

        }

        public void runRemoved(RunEvent event) {
            // TODO Auto-generated method stub

        }
    }

    public class LoginListenerImplementation implements ILoginListener {

        public void loginAdded(LoginEvent event) {
            // TODO Auto-generated method stub

        }

        public void loginDenied(LoginEvent event) {
            // TODO Auto-generated method stub

        }

        public void loginRemoved(LoginEvent event) {
            // TODO Auto-generated method stub

        }
    }

    public class SiteListenerImplementation implements ISiteListener {

        public void siteAdded(SiteEvent event) {
            // TODO Auto-generated method stub

        }

        public void siteChanged(SiteEvent event) {
            // TODO Auto-generated method stub

        }

        public void siteLoggedOff(SiteEvent event) {
            // TODO Auto-generated method stub

        }

        public void siteLoggedOn(SiteEvent event) {
            // TODO Auto-generated method stub

        }

        public void siteRemoved(SiteEvent event) {
            // TODO Auto-generated method stub

        }
    }

    public class ContestTimeListenerImplementation implements IContestTimeListener {

        public void contestStarted(ContestTimeEvent event) {
            // TODO Auto-generated method stub

        }

        public void contestStopped(ContestTimeEvent event) {
            // TODO Auto-generated method stub

        }

        public void contestTimeAdded(ContestTimeEvent event) {
            // TODO Auto-generated method stub

        }

        public void contestTimeChanged(ContestTimeEvent event) {
            // TODO Auto-generated method stub

        }

        public void contestTimeRemoved(ContestTimeEvent event) {
            // TODO Auto-generated method stub

        }
    }

} // @jve:decl-index=0:visual-constraint="10,10"
