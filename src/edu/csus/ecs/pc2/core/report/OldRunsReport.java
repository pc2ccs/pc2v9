package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
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

public class OldRunsReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = 2758681678568110981L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter;
    
    private void writeRow (PrintWriter printWriter, Run run){
        
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

            jbyString = judgementRecord.getJudgerClientId().getName();
            jtString = new Long(judgementRecord.getHowLongToJudgeInSeconds()).toString();
            
            ElementId elementId = judgementRecord.getJudgementId();
            jciString = "??";
            if (elementId != null) {
                Judgement judgement = contest.getJudgement(elementId);
                if (judgement != null){
                    jciString = judgement.toString();
                } else {
                    System.err.println(" Run "+run+" judgement not found for judgement id "+elementId);
                }
            }
            
            String validatorJudgementName = judgementRecord.getValidatorResultString();
            if (judgementRecord.isUsedValidator() && validatorJudgementName != null) {
                if (validatorJudgementName.trim().length() == 0) {
                    validatorJudgementName = "undetermined";
                }
                jciString = validatorJudgementName;
            }
        }

        printWriter.print("jt " + jtString + "|");
        printWriter.print("jby " + jbyString + "|");
        printWriter.print("jci " + jciString + "|");
        
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

    public void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + new Date());
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
        return "Runs (Version 8 content and format)";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Runs Report (Version 8)";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
