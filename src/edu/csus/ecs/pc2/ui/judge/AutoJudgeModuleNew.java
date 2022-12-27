// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui.judge;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.exception.MultipleIssuesException;
import edu.csus.ecs.pc2.core.execute.JudgementUtilites;
import edu.csus.ecs.pc2.core.execute.RunJudger;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * A non-GUI Auto Judge Module.
 * 
 * @author pc2@ecs.csus.edu
 */
// TODO 496 handle judge reset (when server takes away run or expects to send a AVAILABLE_TO_AUTO_JUDGE packet
public class AutoJudgeModuleNew implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -3004503436100790316L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    RunJudger runJudger = null;

    // private AutoJudgingMonitor autoJudgingMonitor = new AutoJudgingMonitor();

    public String getPluginTitle() {
        return "Server (non-GUI)";
    }

    public AutoJudgeModuleNew() {
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
        runJudger = new RunJudger(contest, controller);

        // autoJudgingMonitor.setContestAndController(getContest(), getController());
        try {
            ContestInformation ci = contest.getContestInformation();
            if (ci != null) {
                String cdpPath = ci.getJudgeCDPBasePath();
                Utilities.validateCDP(contest, cdpPath);
            }
        } catch (MultipleIssuesException e) {
            System.err.println("Cannot perform Judging");
            String[] issueList = e.getIssueList();
            StringBuffer message = new StringBuffer();
            message.append("The following errors exist:\n");
            for (int i = 0; i < issueList.length; i++) {
                message.append(issueList[i] + "\n");
            }
            message.append("\nPlease correct and restart");
            System.err.println(message);
            System.exit(1);
        }

        // Listen for check outs
        contest.addRunListener(new AJRunListener());

        if (isAutoJudgingEnabled()) {
            // TODO 496 send AVAILABLE_TO_AUTO_JUDGE to server
            info("Sending AVAILABLE_TO_AUTO_JUDGE to server.");
            controller.sendAvailableToAutoJudge(contest.getClientId());
        }

    }

    protected String getL10nDateTime() {
        DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
        return (dateFormatter.format(new Date()));
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
        return JudgementUtilites.judgeAutoJudgeEnabled(getContest(), getContest().getClientId());
    }

    public IInternalContest getContest() {
        return contest;
    }

    public IInternalController getController() {
        return controller;
    }

    public class AJRunListener implements IRunListener {

        private boolean usingGui = false;

        @Override
        public void runAdded(RunEvent event) {
            ; // ignore, run added handled by server

        }

        @Override
        public void runChanged(RunEvent event) {

            System.out.println("debug 22 runChanged " + event.getAction().toString());

            Run run = event.getRun();
            RunFiles runFiles = event.getRunFiles();
            System.out.println("debug 22 run = " + run);
            System.out.println("debug 22 runfiles ? " + (runFiles != null));
            System.out.println("debug 22 Sent to " + event.getSentToClientId());

            if (event.getSentToClientId().equals(contest.getClientId())) {
                // run is for us.

                if (!runJudger.isJudging()) {
                    try {
                        runJudger.executeAndAutoJudgeRun(run, runFiles);
                    } catch (Exception e) {
                        warn("Problem trying to judge run " + run, e);
                    }
                } else {
                    // TODO 496 How to handle when we get a new run when already judging run
                    warn("Warning got run " + run + " but already judging " + runJudger.getRunBeingJudged());
                }

            }

        }

        @Override
        public void runRemoved(RunEvent event) {
            ; // ignore

        }

        @Override
        public void refreshRuns(RunEvent event) {
            ; // ignore

        }

        public void info(String s) {
            controller.getLog().info(s);
            if (!usingGui) {
                System.out.println(s);
            }

        }

        public void warn(String s) {
            controller.getLog().warning(s);
            if (!usingGui) {
                System.err.println(s);
            }

        }

        public void warn(String s, Exception exception) {
            controller.getLog().log(Log.WARNING, s, exception);
            System.err.println(Thread.currentThread().getName() + " " + s);
            System.err.flush();
            exception.printStackTrace(System.err);
        }

    }

    public void info(String s) {
        controller.getLog().info(s);
        System.out.println(s);
    }
}
