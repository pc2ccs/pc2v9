package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.export.RunStatisticsXML;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * Print Run statistics.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: RunsReport.java 2377 2011-10-15 02:26:57Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/core/report/RunsReport.java $
public class RunStatisticsReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = -672453617982063041L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter = new Filter();

    private void writeRow(PrintWriter printWriter, Run run) {

        ClientId clientId = run.getSubmitter();
        printWriter.print("run " + run.getNumber() + " ");
        if (run.isDeleted()) {
            printWriter.print("DELETED ");
        }
        printWriter.print(run.getStatus() + " ");
        printWriter.print("s" + run.getSiteNumber() + " ");
        printWriter.print("at " + run.getElapsedMins() + " (" + run.getCreateDate() + ") ");
        printWriter.print(clientId.getName() + " (" + getClientName(clientId) + ") ");

        printWriter.print(contest.getProblem(run.getProblemId()) + " ");
        printWriter.print(contest.getLanguage(run.getLanguageId()) + " ");

        if (run.isDeleted()) {
            printWriter.print(" DELETED ");
        }
        printWriter.println();
        ClientId whoCheckedOutId = contest.getRunCheckedOutBy(run);
        if (whoCheckedOutId != null) {
            printWriter.println("    Checked out by: " + whoCheckedOutId);
        }

        if (run.getAllJudgementRecords().length > 0) {

            for (JudgementRecord judgementRecord : run.getAllJudgementRecords()) {
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
                printWriter.println();
                printWriter.print("     ");
                printWriter.println(" execute time = " + judgementRecord.getExecuteMS() + "ms");

            }
        }

    }

    private long toSeconds(long ms) {
        return ms / 1000;
    }

    public void writeReport(PrintWriter printWriter) {

        // Runs
        printWriter.println();
        Run[] runs = contest.getRuns();

        Arrays.sort(runs, new RunComparator());

        if (filter.isFilterOn()) {
            printWriter.println("Filter: " + filter.toString());
            printWriter.println();

            int count = filter.countRuns(runs);

            if (count == 0) {
                printWriter.println("-- No runs match of " + runs.length + " runs (filtered) --");

            } else {
                printWriter.println("-- " + count + " of " + runs.length + " runs (filtered) --");
                for (Run run : runs) {
                    if (filter.matches(run)) {
                        try {
                            writeRow(printWriter, run);
                        } catch (Exception e) {
                            printWriter.println("Exception in report: " + e.getMessage());
                            e.printStackTrace(printWriter);
                        }
                    }
                }
            }

        } else {
            printWriter.println("-- " + runs.length + " runs --");
            for (Run run : runs) {
                try {
                    writeRow(printWriter, run);
                } catch (Exception e) {
                    printWriter.println("Exception in report: " + e.getMessage());
                    e.printStackTrace(printWriter);
                }
            }

        }

    }

    private String getClientName(ClientId clientId) {
        Account account = contest.getAccount(clientId);
        if (account != null) {
            return account.getDisplayName();
        } else {
            return clientId.getName();
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
        filter = inFilter;

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

    public String createReportXML(Filter inFilter) throws IOException {
        RunStatisticsXML runStatisticsXML = new RunStatisticsXML();
        return runStatisticsXML.toXML(contest, filter);
    }

    public String getReportTitle() {
        return "Run Statistics";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Runs Statistics Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
