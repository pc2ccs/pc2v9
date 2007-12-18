package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.Run.RunStates;

/**
 * Print Run lists.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$

public class RunsReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = 2758681678568110981L;

    private IContest contest;

    private IController controller;

    private Log log;

    private Filter filter;
    
    @SuppressWarnings("unused")
    private void writeRowOld (PrintWriter printWriter, Run run){
        
        ClientId clientId = run.getSubmitter();
        printWriter.print("run "+run.getNumber()+"|");
        printWriter.print("site "+run.getSiteNumber()+"|");
        printWriter.print("proxy |");
        printWriter.print("team "+clientId.getClientNumber()+"|");
        printWriter.print(clientId.getName()+":"+getClientName(clientId)+"|");
        
        printWriter.print("prob "+run.getProblemId()+":"+contest.getProblem(run.getProblemId())+"|");
        printWriter.print("lang "+run.getLanguageId()+":"+contest.getLanguage(run.getLanguageId())+"|");
        
        printWriter.print("tocj |");
        printWriter.print("os "+run.getSystemOS()+"|");
        printWriter.print("sel "+run.getStatus().equals(RunStates.BEING_JUDGED)+"|");
        
        printWriter.print("tocj false|");
        printWriter.print("jc " + run.isJudged() + "|");
        printWriter.print(run.getElapsedMins() + "|");
        printWriter.print("rid " + run.getElementId() + "|");
        printWriter.print("mmfr " + run.isSolved() + "|");
        printWriter.print("del? " + run.isDeleted() + "|");
        
        String jciString = "";
        String jbyString = "";
        String jtString = "";
        
        if (run.isJudged()) {
            
            JudgementRecord judgementRecord = run.getJudgementRecord();

            jciString = judgementRecord.getJudgementId().toString();
            jbyString = judgementRecord.getJudgerClientId().getName();
            jtString = new Long(judgementRecord.getJudgedMinutes()).toString();
        }

        printWriter.print("jt " + jtString + "|");
        printWriter.print("jby " + jbyString + "|");
        printWriter.print("jci " + jciString + "|");
        
        printWriter.println();
    
    }
    
    private void writeRow(PrintWriter printWriter, Run run) {

        ClientId clientId = run.getSubmitter();
        printWriter.print("run " + run.getNumber() + " ");
        if (run.isDeleted()){
            printWriter.print("DELETED ");
        }
        printWriter.print(run.getStatus() + " ");
        printWriter.print("s" + run.getSiteNumber() + " ");
        printWriter.print("at " + run.getElapsedMins() + " ");
        printWriter.print(clientId.getName() + " (" + getClientName(clientId) + ") ");

        printWriter.print(contest.getProblem(run.getProblemId()) + " ");
        printWriter.print(contest.getLanguage(run.getLanguageId()) + " ");

        if (run.isDeleted()) {
            printWriter.print(" DELETED ");
        }
        printWriter.println();
        if (run.isJudged()) {

            for (JudgementRecord judgementRecord : run.getAllJudgementRecords()) {
                String judgementText = contest.getJudgement(judgementRecord.getJudgementId()).toString();
                String validatorJudgementName = judgementRecord.getValidatorResultString();
                if (judgementRecord.isUsedValidator() && validatorJudgementName != null) {
                    if (validatorJudgementName.trim().length() == 0) {
                        validatorJudgementName = "undetermined";
                    }
                    judgementText = validatorJudgementName;
                }

                printWriter.print("     ");
                printWriter.print(" '" + judgementText + "'");
                printWriter.print(" by " + judgementRecord.getJudgerClientId().getName()+"/s"+judgementRecord.getJudgerClientId().getSiteNumber());
                if (judgementRecord.isUsedValidator()) {
                    printWriter.print("/Validator");
                }
                printWriter.print(" at " + judgementRecord.getWhenJudgedTime());
                printWriter.println();
            }
        }
        printWriter.println();

    }
    
    public void writeReport(PrintWriter printWriter) {
        
        // Runs
        printWriter.println();
        Run[] runs = contest.getRuns();
        Arrays.sort(runs, new RunComparator());
        printWriter.println("-- " + runs.length + " runs --");
        for (Run run : runs) {
            writeRow(printWriter, run);
        }
    }

    private String getClientName(ClientId clientId) {
        Account account = contest.getAccount(clientId);
        if (account != null){
            return account.getDisplayName();
        }else {
            return clientId.getName();
        }
    }

    private void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
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
                printWriter.println("Exception in report: "+e.getMessage());
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
        return "Runs";
    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Runs Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
