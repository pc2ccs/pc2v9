package edu.csus.ecs.pc2.core.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Run.RunStates;

/**
 * Internal dump report.
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class RunsReport implements IReport {

    private IModel model;

    private IController controller;

    private Log log;
    
    private void writeRowOld (PrintWriter printWriter, Run run){
        
        ClientId clientId = run.getSubmitter();
        printWriter.print("run "+run.getNumber()+"|");
        printWriter.print("site "+run.getSiteNumber()+"|");
        printWriter.print("proxy |");
        printWriter.print("team "+clientId.getClientNumber()+"|");
        printWriter.print(clientId.getName()+":"+getClientName(clientId)+"|");
        
        printWriter.print("prob "+run.getProblemId()+":"+model.getProblem(run.getProblemId())+"|");
        printWriter.print("lang "+run.getLanguageId()+":"+model.getLanguage(run.getLanguageId())+"|");
        
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
        printWriter.print(run.getStatus() + " ");
        printWriter.print("s" + run.getSiteNumber() + " ");
        printWriter.print("at " + run.getElapsedMins() + " ");
        printWriter.print(clientId.getName() + " (" + getClientName(clientId) + ") ");

        printWriter.print(model.getProblem(run.getProblemId()) + " ");
        printWriter.print(model.getLanguage(run.getLanguageId()) + " ");

        if (run.isDeleted()) {
            printWriter.print(" DELETED ");
        }
        printWriter.println();
        if (run.isJudged()) {

            for (JudgementRecord judgementRecord : run.getAllJudgementRecords()) {
                Judgement judgement = model.getJudgement(judgementRecord.getJudgementId());
                printWriter.print("     ");
                printWriter.print(" '" + judgement + "'");
                printWriter.print(" by " + judgementRecord.getJudgerClientId().getName());
                printWriter.print(" at " + judgementRecord.getWhenJudgedTime());
                printWriter.println();
            }
            printWriter.println();
        }

    }
    
    private void writeReport(PrintWriter printWriter) {
        
        // Runs
        printWriter.println();
        Run[] runs = model.getRuns();
        Arrays.sort(runs, new RunComparator());
        printWriter.println("-- " + runs.length + " runs --");
        for (Run run : runs) {
            writeRow(printWriter, run);
        }
    }

    private String getClientName(ClientId clientId) {
        Account account = model.getAccount(clientId);
        if (account != null){
            return account.getDisplayName();
        }else {
            return clientId.getName();
        }
    }

    private void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + new Date());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
    }

    private void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report");
    }

    public void createReportFile(String filename, Filter filter) throws IOException {

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

    public String[] createReport(Filter filter) {
        throw new SecurityException("Not implemented");
    }

    public String createReportXML(Filter filter) {
        throw new SecurityException("Not implemented");
    }

    public String getReportTitle() {
        return "Runs";
    }

    public void setModelAndController(IModel inModel, IController inController) {
        this.model = inModel;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Runs Report";
    }

}
