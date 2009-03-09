package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.JudgementNotificationsList;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.JudgementNotification;
import edu.csus.ecs.pc2.core.model.NotificationSetting;
import edu.csus.ecs.pc2.core.model.Problem;

/**
 * Print All Group Information.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class JudgementNotificationsReport implements IReport {

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

    private void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println(getReportTitle() + " Report");
    }

    private void printFooter(PrintWriter printWriter) {
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

            printFooter(printWriter);

            printWriter.close();
            printWriter = null;

        } catch (Exception e) {
            log.log(Log.INFO, "Exception writing report", e);
            printWriter.println("Exception generating report " + e.getMessage());
        }
    }

    public String[] createReport(Filter inFilter) {
        throw new SecurityException("Not implemented");
    }

    public String createReportXML(Filter inFilter) {
        throw new SecurityException("Not implemented");
    }

    public String getReportTitle() {
        return "Judgement Notifications";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Judgement Notifications Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
