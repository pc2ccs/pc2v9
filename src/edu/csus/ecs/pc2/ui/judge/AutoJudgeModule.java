package edu.csus.ecs.pc2.ui.judge;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.exception.MultipleIssuesException;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.ui.AutoJudgingMonitor;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * A non-GUI Auto Judge Module.
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class AutoJudgeModule implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -8765538696525028286L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private AutoJudgingMonitor autoJudgingMonitor = new AutoJudgingMonitor();

    public String getPluginTitle() {
        return "Server (non-GUI)";
    }

    public AutoJudgeModule() {
        VersionInfo versionInfo = new VersionInfo();
        System.out.println(versionInfo.getSystemName());
        System.out.println(versionInfo.getSystemVersionInfo());
        System.out.println("Build " + versionInfo.getBuildNumber());
        System.out.println("Date: " + getL10nDateTime());
        System.out.println("Working directory is " + Utilities.getCurrentDirectory());
        System.out.println();

    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        contest = inContest;
        controller = inController;
        log = controller.getLog();

        autoJudgingMonitor.setContestAndController(getContest(), getController());
        try {
            ContestInformation ci = contest.getContestInformation();
            if (ci != null) {
                String cdpPath = ci.getJudgeCDPBasePath();
                Utilities.validateCDP(contest, cdpPath);
            }
        } catch(MultipleIssuesException e) {
            System.err.println("Cannot perform Judging");
            String[] issueList = e.getIssueList();
            StringBuffer message = new StringBuffer();
            message.append("The following errors exist:\n");
            for (int i = 0; i < issueList.length; i++) {
                message.append(issueList[i]+"\n");
            }
            message.append("\nPlease correct and restart");
            System.err.println(message);
            System.exit(1);
        }

        if (isAutoJudgingEnabled()) {
            startAutoJudging();
        }
    }

    protected String getL10nDateTime() {
        DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
        return (dateFormatter.format(new Date()));
    }

    protected void startAutoJudging() {
        if (isAutoJudgingEnabled()) {

            // Keep this off the AWT thread.
            new Thread(new Runnable() {
                public void run() {
                    // RE-enable local auto judge flag
                    autoJudgingMonitor.startAutoJudging();
                }
            }).start();
        } else {
            showMessage("Administrator has turned off Auto Judging");
        }
    }

    private void showMessage(final String string) {

        getLog().info(string);

    }

    public Log getLog() {
        return log;
    }

    /**
     * Is Auto Judging turned On for this judge ?
     * 
     * @return
     */
    private boolean isAutoJudgingEnabled() {
        ClientSettings clientSettings = getContest().getClientSettings();
        if (clientSettings != null && clientSettings.isAutoJudging()) {
            return true;
        }

        return false;
    }

    public IInternalContest getContest() {
        return contest;
    }

    public IInternalController getController() {
        return controller;
    }
}
