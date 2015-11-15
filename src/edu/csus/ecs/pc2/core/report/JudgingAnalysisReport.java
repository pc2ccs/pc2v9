package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * Judging Analysis Report.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: ContestAnalysisReport.java 2881 2014-11-21 16:46:38Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/core/report/ContestAnalysisReport.java $
public class JudgingAnalysisReport implements IReport {

    private static final long serialVersionUID = 8827529273455158045L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter = new Filter();

    public void writeReport(PrintWriter printWriter) throws IOException {

        printWriter.println("Contest Title: " + contest.getContestInformation().getContestTitle());
        printWriter.println();

        printWriter.println(contest.getSites().length + " sites.");
        printWriter.println();

        Run[] runs = filter.getRuns(contest.getRuns());

        int count = runs.length;

        if (filter.isFilterOn()) {
            printWriter.println("   " + count + " runs.");
        } else {
            printWriter.println("   " + count + " filtered runs runs.");
            printWriter.println("     " + filter);
        }

        // No report on
        if (count == 0) {
            return;
        }

        /**
         * key is clientId (judge login), value is number of runs judged
         */
        Map<ClientId, Integer> judgedRunsMap = new HashMap<ClientId, Integer>();
        /**
         * key is clientId, value is true if has AJ'd run
         */
        Map<ClientId, Boolean> autoJudgeMap = new HashMap<ClientId, Boolean>();

        int unjudgedRuns = 0;
        int judgedRuns = 0;

        for (Run run : runs) {
            if (!run.isDeleted()) {

                if (run.isJudged()) {

                    JudgementRecord[] records = run.getAllJudgementRecords();

                    for (JudgementRecord judgementRecord : records) {
                        ClientId clientId = judgementRecord.getJudgerClientId();
                        boolean isAJ = judgementRecord.isComputerJudgement();

                        int newValue = incrementValue(judgedRunsMap.get(clientId));
                        judgedRunsMap.put(clientId, newValue);
                        autoJudgeMap.put(clientId, isAJ);
                    }

                    judgedRuns++;
                } else {
                    unjudgedRuns++;
                }
            }
        }

        printWriter.println();
        printWriter.println("      Judged runs: " + judgedRuns);
        printWriter.println("    Unjudged runs: " + unjudgedRuns);
        printWriter.println();

        Vector<Account> vector = contest.getAccounts(Type.JUDGE);

        Account[] accounts = (Account[]) vector.toArray(new Account[vector.size()]);

        Arrays.sort(accounts, new AccountComparator());
        
        printWriter.println("       JUDGE   RUNS");

        for (Account account : accounts) {

            ClientId clientId = account.getClientId();
            Integer runCount = judgedRunsMap.get(clientId);
            if (runCount == null) {
                runCount = 0;
            }

            Boolean autoJudge = autoJudgeMap.get(clientId);
            if (autoJudge == null) {
                autoJudge = false;
            }

            String ajSTring = "  ";
            if (autoJudge) {
                ajSTring = "AJ";
            }
            String runString = "   ";
            if (runCount.intValue() > 0) {
                runString = runCount.toString();
            }

            printWriter.format("   %3s %-8s %3s ", ajSTring, clientId.getClientType().toString() + " " + clientId.getClientNumber(), runString);
            printWriter.println();
        }
    }

    
    private int incrementValue(Integer integer) {
        if (integer == null){
            return 1;
        }
        return integer.intValue() + 1;
    }

    public void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
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

    public void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report");
    }

    public String[] createReport(Filter arg0) {
        // SOMEDAY 
        return null;
    }

    public String createReportXML(Filter arg0) throws IOException {
        return Reports.notImplementedXML(this);
    }

    public String getReportTitle() {
        return "Judging Analysis";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Judging Analysis Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
