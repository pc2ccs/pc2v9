package edu.csus.ecs.pc2.ui.server;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.ILoginListener;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.ISiteListener;
import edu.csus.ecs.pc2.core.model.LoginEvent;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.Site;
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

    private static final long serialVersionUID = 1L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    public String getPluginTitle() {
        return "Server (non-GUI)";
    }

    public ServerModule() {
        VersionInfo versionInfo = new VersionInfo();
        String [] lines = versionInfo.getSystemVersionInfoMultiLine();
        for (String line : lines) {
            System.out.println(line);
        }
        System.out.println();
        System.out.println("Date: " + getL10nDateTime());
        System.out.println("Working directory is " + Utilities.getCurrentDirectory());
        System.out.println();
        

    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        this.log = controller.getLog();

        contest.addRunListener(new RunListenerImplementation());
        // contest.addAccountListener(new AccountListenerImplementation());
        contest.addLoginListener(new LoginListenerImplementation());
        contest.addSiteListener(new SiteListenerImplementation());
        contest.addContestTimeListener(new ContestTimeListenerImplementation());
        
        Profile profile = inContest.getProfile();
        if (! inController.isUsingGUI()){
            System.out.println(new Date()+" Using Profile: "+profile.getName()+" @ "+profile.getProfilePath());
        }
        log.info("Using Profile: "+profile.getName()+" @ "+profile.getProfilePath());

        ClientId clientId = contest.getClientId();
        Site site = contest.getSite(clientId.getSiteNumber());

        // date Type (Site X - Title) started
        info(clientId.getClientType().toString().toLowerCase() + " (Site " + site.getSiteNumber() + " - " + site.getDisplayName() + ") started");
    }

    // public class AccountListenerImplementation implements IAccountListener { }

    private void info(String string) {
        System.out.println(new Date() + " " + string);
        log.info(string);
    }

    /**
     * Run Listener.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    private class RunListenerImplementation implements IRunListener {

        public void runAdded(RunEvent event) {
            // TODO Auto-generated method stub

        }
        
        public void refreshRuns(RunEvent event) {
            // TODO Auto-generated method stub
            
        }

        public void runChanged(RunEvent event) {
            // TODO Auto-generated method stub

        }

        public void runRemoved(RunEvent event) {
            // TODO Auto-generated method stub

        }
    }

    /**
     * Login Listener.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
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

        public void loginRefreshAll(LoginEvent event) {
            // TODO Auto-generated method stub
            
        }
    }

    /**
     * Site Listener.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    public class SiteListenerImplementation implements ISiteListener {

        public void siteProfileStatusChanged(SiteEvent event) {
            // TODO this UI does not use a change in profile status 
        }

        public void siteAdded(SiteEvent event) {
            logSiteInfo(event.getAction().toString(), event.getSite());
        }

        private void logSiteInfo(String string, Site site) {
            infoLog("Site: " + site.getSiteNumber() + " " + string + " " + site.getDisplayName());
        }

        public void siteChanged(SiteEvent event) {
            // TODO Auto-generated method stub
        }

        public void siteLoggedOff(SiteEvent event) {
            logSiteInfo(event.getAction().toString(), event.getSite());
        }

        public void siteLoggedOn(SiteEvent event) {
            logSiteInfo(event.getAction().toString(), event.getSite());
        }

        public void siteRemoved(SiteEvent event) {
            logSiteInfo(event.getAction().toString(), event.getSite());
        }

        public void sitesRefreshAll(SiteEvent event) {
            infoLog("Site: none " +  event.getAction().toString());
        }
    }

    /**
     * Contest Listener.
     *  
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    public class ContestTimeListenerImplementation implements IContestTimeListener {

        private void logClockInfo(String string, ContestTime contestTime) {
            infoLog("Clock: " + contestTime.getSiteNumber() + " " + string);
        }

        public void contestStarted(ContestTimeEvent event) {
            logClockInfo(event.getAction().toString(), event.getContestTime());
        }

        public void contestStopped(ContestTimeEvent event) {
            logClockInfo(event.getAction().toString(), event.getContestTime());
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

        public void refreshAll(ContestTimeEvent event) {
            logClockInfo(event.getAction().toString(), event.getContestTime());
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

    protected void infoLog(String string) {
        System.out.println(getL10nDateTime() + ": " + string);
    }

    protected String getL10nDateTime() {
        DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
        return (dateFormatter.format(new Date()));
        // System.out.println(dateFormatter.format(new Date()));
    }

} // @jve:decl-index=0:visual-constraint="10,10"
