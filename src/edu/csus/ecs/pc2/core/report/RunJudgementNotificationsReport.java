package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.JudgementNotificationsList;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.JudgementNotification;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.NotificationSetting;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunUtilities;

/**
 * Print runs and show whether they are suppressed or not.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class RunJudgementNotificationsReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = -287199138291014045L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter = new Filter();

    public void writeReport(PrintWriter printWriter) {

        printWriter.println();

        ContestInformation contestInformation = contest.getContestInformation();
        JudgementNotificationsList judgementNotificationsList = contestInformation.getJudgementNotificationsList();
        Problem[] problems = contest.getProblems();

        printWriter.println("-- " + problems.length + " Problems --");

        for (Problem problem : problems) {

            printWriter.println("Notification for " + problem.getDisplayName());

            NotificationSetting notificationSetting = null;

            if (judgementNotificationsList != null) {
                notificationSetting = judgementNotificationsList.get(problem);
            }

            dump(printWriter, notificationSetting);

            printWriter.println();
        }

    }

    private void dump(PrintWriter printWriter, NotificationSetting notificationSetting) {

        if (notificationSetting == null) {
            printWriter.println("    No notification delivery settings defined.");
        } else {

            JudgementNotification judgementNotification = null;

            judgementNotification = notificationSetting.getPreliminaryNotificationYes();
            printWriter.println("          Prelim Yes suppress " + judgementNotification.isNotificationSupressed() + " cuttoff at " + judgementNotification.getCuttoffMinutes());

            judgementNotification = notificationSetting.getPreliminaryNotificationNo();
            printWriter.println("          Prelim No  suppress " + judgementNotification.isNotificationSupressed() + " cuttoff at " + judgementNotification.getCuttoffMinutes());

            judgementNotification = notificationSetting.getFinalNotificationYes();
            printWriter.println("          Final  Yes suppress " + judgementNotification.isNotificationSupressed() + " cuttoff at " + judgementNotification.getCuttoffMinutes());

            judgementNotification = notificationSetting.getFinalNotificationNo();
            printWriter.println("          Final  No  suppress " + judgementNotification.isNotificationSupressed() + " cuttoff at " + judgementNotification.getCuttoffMinutes());
        }
    }

    public void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println(getReportTitle() + " Report");
    }

    public void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report");
    }

    public void createReportFile(String filename, Filter inFilter) throws IOException {

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);

        try {
            printHeader(printWriter);

            try {
                writeReport(printWriter);
            } catch (Exception e) {
                printWriter.println("Exception in report: " + e.getMessage());
                e.printStackTrace(printWriter);
            }

            try {
                writeRunsSuppressedReport(printWriter);
            } catch (Exception e) {
                printWriter.println("Exception in report: " + e.getMessage());
                e.printStackTrace(printWriter);
            }

            printFooter(printWriter);

            printWriter.close();
            printWriter = null;

        } catch (Exception e) {
            log.log(Log.INFO, "Exception writing report", e);
            printWriter.println("Exception generating report " + e.getMessage());
        }
    }

    private void writeRunsSuppressedReport(PrintWriter printWriter) {

        Run[] runs = contest.getRuns();

        int count = filter.countRuns(runs);

        printWriter.println("There are " + count + " runs.");
        printWriter.println();

        ContestInformation contestInformation = contest.getContestInformation();
        JudgementNotificationsList judgementNotificationsList = contestInformation.getJudgementNotificationsList();

        ContestTime contestTime = contest.getContestTime();

        Arrays.sort(runs, new RunComparator());

        for (Run run : runs) {

            if (filter.matches(run)) {
                boolean suppressed = RunUtilities.supppressJudgement(judgementNotificationsList, run, contestTime);

                printWriter.print("run " + run.getNumber() + " ");

                if (run.isDeleted()) {
                    printWriter.print("DELETED ");
                }
                printWriter.print(run.getStatus() + " ");

                if (run.getAllJudgementRecords().length > 0) {
                    printWriter.print("(");
                    if (suppressed) {
                        printWriter.print("NOT ");
                    }
                    printWriter.print("sent to team) ");
                }
                
                printWriter.print(run.getSubmitter().getName()+" ");

                printWriter.print("s" + run.getSiteNumber() + " ");
                printWriter.print("at " + run.getElapsedMins() + " ");
                printWriter.print("(rem = " + (contestTime.getConestLengthMins() - run.getElapsedMins()) + ") ");
                printWriter.print(contest.getProblem(run.getProblemId()) + " ");
                printWriter.println();

                printJudgements(printWriter, run);
            }
        }
    }

    private void printJudgements(PrintWriter printWriter, Run run) {

        if (run.getAllJudgementRecords().length > 0) {

            for (JudgementRecord judgementRecord : run.getAllJudgementRecords()) {

                printWriter.print("     ");

                if (contest == null) {
                    printWriter.println("Contest is null");
                }
                if (judgementRecord.getJudgementId() == null) {
                    printWriter.println("Judgement is null for " + run);
                }
                ElementId elmentId = judgementRecord.getJudgementId();
                String judgementText = contest.getJudgement(elmentId).toString();
                String validatorJudgementName = judgementRecord.getValidatorResultString();
                if (judgementRecord.isUsedValidator() && validatorJudgementName != null) {
                    if (validatorJudgementName.trim().length() == 0) {
                        validatorJudgementName = "undetermined";
                    }
                    judgementText = validatorJudgementName;
                }

                printWriter.print("     ");
                printWriter.print(" '" + judgementText + "'");
                printWriter.print(" by " + judgementRecord.getJudgerClientId().getName() + "/s" + judgementRecord.getJudgerClientId().getSiteNumber());
                if (judgementRecord.isComputerJudgement()) {
                    printWriter.print("/Computer");
                } else if (judgementRecord.isUsedValidator()) {
                    printWriter.print("/Validator");
                }
                printWriter.print(" at " + judgementRecord.getWhenJudgedTime());
                if (judgementRecord.isPreliminaryJudgement()) {
                    printWriter.print(" (preliminary)");
                }
                
                if (judgementRecord.isSendToTeam()){
                    printWriter.print(" (send to team)");
                }
                
                printWriter.println();
            }
        } else {
            printWriter.println("     Run is not judged.");
        }
    }

    public String[] createReport(Filter inFilter) {
        throw new SecurityException("Not implemented");
    }

    public String createReportXML(Filter inFilter) {
        throw new SecurityException("Not implemented");
    }

    public String getReportTitle() {
        return "Run Notifications Sent";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Run Notifications Sent Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
