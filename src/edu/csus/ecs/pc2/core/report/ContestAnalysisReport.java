package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.list.RunCompartorByTeamProblemElapsed;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.security.Permission.Type;

/**
 * Contest Analsysis Report.
 * 
 * Prints summaries of each site submissions.
 * <P>
 * Shows how many runs/clars are not judged/answered.<br>
 * Shows runs that are submitted after first yes.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ContestAnalysisReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = 8827529273455158045L;

    private IContest contest;

    private IController controller;

    private Log log;

    private Filter filter;

    public void writeReport(PrintWriter printWriter) throws IOException {

        printWriter.println();
        printWriter.println("Contest Title: " + contest.getContestInformation().getContestTitle());
        printWriter.println();

        printWriter.println(contest.getSites().length + " sites.");

        printRunsClarsBySite(printWriter);

    }

    protected void printRunsFromDisabledTeams(PrintWriter printWriter) {

        printWriter.println("Runs submitted by Teams not shown on scoreboard/standings");

        Run[] runs = contest.getRuns();
        Arrays.sort(runs, new RunCompartorByTeamProblemElapsed());

        // clientId which is not displayed
        // ClientId lastClientId = null;

        for (Run run : runs) {

            ClientId clientId = run.getSubmitter();

            if (!contest.getAccount(clientId).isAllowed(Type.DISPLAY_ON_SCOREBOARD)) {

                printWriter.format("#%3d %3d %-10.10s ", run.getNumber(), run.getElapsedMins(), getJudgementName(run));
                printWriter.print(clientId + " '" + contest.getAccount(clientId).getDisplayName() + "'");
                printWriter.println();
            }
        }

    }

    protected void printRunsClarsBySite(PrintWriter printWriter) {

        int numSites = contest.getSites().length;
        int[] numRuns = new int[numSites];
        int[] numRunsDeleted = new int[numSites];
        int[] numClars = new int[numSites];
        int[] numClarsDeleted = new int[numSites];
        int[] numClarsForAll = new int[numSites];

        int[] numUnjudgedRuns = new int[numSites];
        int totalUnjudgedRuns = 0;
        int[] numClarsUnAnswered = new int[numSites];
        int totalUnanswered = 0;

        for (Run run : contest.getRuns()) {

            int siteNum = run.getSiteNumber() - 1;
            numRuns[siteNum]++;
            if (run.isDeleted()) {
                numRunsDeleted[siteNum]++;
            }

            if (!run.isJudged()) {
                numUnjudgedRuns[siteNum]++;
                totalUnjudgedRuns++;
            }
        }

        for (Clarification clarification : contest.getClarifications()) {
            int siteNum = clarification.getSiteNumber() - 1;
            numClars[siteNum]++;
            if (clarification.isSendToAll()) {
                numClarsForAll[siteNum]++;
            }

            if (!clarification.isAnswered()) {
                numClarsUnAnswered[siteNum]++;
                totalUnanswered++;
            }

            if (clarification.isDeleted()) {
                numClarsDeleted[siteNum]++;
            }
        }

        int totalRunsDeleted = 0;
        int totalClars = 0;
        int totalRuns = 0;
        int totalClarsDeleted = 0;
        int totalClarsToAll = 0;

        for (int i = 0; i < numSites; i++) {
            int siteNum = i + 1;
            printWriter.format("%2d %-15s ", siteNum, contest.getSites()[i].getDisplayName());

            printWriter.format("%4d runs (%2d unjudged, %2d deleted)", numRuns[i], numUnjudgedRuns[i], numRunsDeleted[i]);
            printWriter.format("%4d clars, %d to All (%2d unanswered, %2d deleted)", numClars[i], numClarsForAll[i], numClarsUnAnswered[i], numClarsDeleted[i]);

            totalRunsDeleted += numRunsDeleted[i];
            totalClars += numClars[i];
            totalRuns += numRuns[i];
            totalClarsDeleted += numClarsDeleted[i];
            totalClarsToAll += numClarsForAll[i];

            printWriter.println();
        }

        printWriter.format("   %-15s ", "Total");
        printWriter.format("%4d runs (%2d unjudged, %2d deleted)", totalRuns, totalUnjudgedRuns, totalRunsDeleted);
        printWriter.format("%4d clars, %d to All (%2d unanswered, %2d deleted)", totalClars, totalClarsToAll, totalUnanswered, totalClarsDeleted);
        printWriter.println();

        printWriter.println();

        if (totalUnanswered == 0) {
            printWriter.println("There are NO unanswered clars");
        } else {
            printWriter.println("Note: " + totalUnanswered + " unanswered clars");
        }

        if (totalUnjudgedRuns == 0) {
            printWriter.println("There are NO unjudged runs");
        } else {
            printWriter.println("Note: " + totalUnjudgedRuns + " unjudged runs");
        }

        printWriter.println();
        printWriter.println("Runs after first Yes ");

        Run solvedRun = null;

        Run[] runs = contest.getRuns();

        Arrays.sort(runs, new RunCompartorByTeamProblemElapsed());

        ElementId lastProblemId = null;
        ClientId lastClientId = null;

        int numYesAfterYes = 0;
        int numNoAfterYes = 0;

        for (Run run : runs) {

            if (!run.isDeleted()) {

                if (lastClientId == null) {
                    lastProblemId = run.getProblemId();
                    lastClientId = run.getSubmitter();
                    if (run.isSolved()) {
                        solvedRun = run;
                    }
                } else {

                    if (lastClientId.equals(run.getSubmitter()) && run.getProblemId().equals(lastProblemId)) {
                        // Same team problem
                        if (solvedRun != null) {
                            if (run.isSolved()) {
                                numYesAfterYes++;
                            } else {
                                numNoAfterYes++;
                            }
                            // printWriter.println(" Found " + run + " after " + solvedRun);

                            printWriter.print("Found   ");
                            printWriter.format("#%3d %3d %-10.10s ", run.getNumber(), run.getElapsedMins(), getJudgementName(run));
                            printWriter.print(lastClientId + " '" + contest.getAccount(lastClientId).getDisplayName() + "'");
                            printWriter.println();

                            printWriter.print("  After ");
                            printWriter.format("#%3d %3d %-10.10s ", solvedRun.getNumber(), solvedRun.getElapsedMins(), getJudgementName(solvedRun));
                            printWriter.print(contest.getProblem(run.getProblemId()));
                            printWriter.println();

                            printWriter.println();
                        }
                    }

                    if (run.isSolved()) {
                        solvedRun = run;
                    } else {
                        solvedRun = null;
                    }
                    lastProblemId = run.getProblemId();
                    lastClientId = run.getSubmitter();
                }
            }
        }

        if (numYesAfterYes > 0) {
            printWriter.println("Total Yes judgements after earlier Yes judgement: " + numYesAfterYes);
        } else {
            printWriter.println("There are NO 'Yes' judgements after earlier Yes judgements");
        }

        if (numNoAfterYes > 0) {
            printWriter.println("Total No judgements after earlier Yes judgement: " + numNoAfterYes);
        } else {
            printWriter.println("There are NO 'No' judgements after earlier Yes judgements");
        }

        printWriter.println();
        printRunsFromDisabledTeams(printWriter);
        printWriter.println();

    }

    private String getJudgementName(Run run) {

        if (run.isJudged()) {
            JudgementRecord judgementRecord = run.getJudgementRecord();
            if (judgementRecord != null) {
                ElementId judgementId = judgementRecord.getJudgementId();
                Judgement judgement = contest.getJudgement(judgementId);
                return judgement.toString();
            } else if (run.isSolved()) {
                return "yes";
            } else {
                return "no";
            }
        } else {
            return run.getStatus().toString();
        }
    }

    private void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + new Date());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println(getReportTitle() + " Report");
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

    private void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report");
    }

    public String[] createReport(Filter arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public String createReportXML(Filter arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getReportTitle() {
        return "Contest Analysis";
    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Contest Analysis Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
