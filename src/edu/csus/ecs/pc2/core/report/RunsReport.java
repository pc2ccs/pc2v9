package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
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

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter = new Filter();
    
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
        ClientId whoCheckedOutId = contest.getRunCheckedOutBy(run);
        if (whoCheckedOutId != null){
            printWriter.println("    Checked out by: "+whoCheckedOutId);
        }
        
        if (run.isSendToTeams()) {
            printWriter.println("    Judgement was sent to team.");
        } else {
            printWriter.println("    Judgement was _not_ sent to team.");
        }

        if (isThisSite(run.getSiteNumber())) {

            try {

                // TODO getRunFiles should throw an exception which is handled by this and other methods.
                RunFiles runFiles = contest.getRunFiles(run);
                if (runFiles == null) {
                    printWriter.println("    No submitted files found.");
                } else {
                    SerializedFile mainFile = runFiles.getMainFile();
                    int bytes = 0;
                    if (mainFile.getBuffer() != null) {
                        bytes = mainFile.getBuffer().length;
                    }
                    printWriter.println("      main file '" + mainFile.getName() + "' " + bytes + " bytes");
                    if (runFiles.getOtherFiles() == null) {
                        printWriter.println("                no additional submitted files");
                    } else {
                        printWriter.println("                " + runFiles.getOtherFiles().length + " additional submitted files");

                        if (runFiles.getOtherFiles().length > 0) {
                            for (SerializedFile serializedFile : runFiles.getOtherFiles()) {
                                bytes = 0;
                                if (serializedFile.getBuffer() != null) {
                                    bytes = serializedFile.getBuffer().length;
                                }
                                printWriter.println("                '" + serializedFile.getName() + "' " + bytes + " bytes");
                            }
                        }
                    }
                }
            } catch (java.lang.NullPointerException nullPointerException) {
                printWriter.println("    No submitted files found (not on server?).");
            } catch (Exception ex) {

                if (ex.getMessage().startsWith("Unable to read object from file")) {
                    if (run.getSiteNumber() == contest.getSiteNumber()) {
                        // If this site then there is a error
                        printWriter.println("    Error - no submitted files found for this run.");
                    } else {
                        // If not this site this is ok.
                        printWriter.println("    No submitted files found (Run from site " + run.getSiteNumber() + ")");
                    }
                } else {
                    ex.printStackTrace(printWriter);
                }
            }
        } else {
            printWriter.println("    No submitted files found (Run from site " + run.getSiteNumber() + ")");
        }
        
        
        if (run.getAllJudgementRecords().length > 0){

            for (JudgementRecord judgementRecord : run.getAllJudgementRecords()) {
                if (contest == null){
                    printWriter.println("Contest is null");
                }
                if (judgementRecord.getJudgementId() == null){
                    printWriter.println("Judgement is null for "+run);
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
                printWriter.print(" by " + judgementRecord.getJudgerClientId().getName()+"/s"+judgementRecord.getJudgerClientId().getSiteNumber());
                if (judgementRecord.isComputerJudgement()){
                    printWriter.print("/Computer");
                } else if (judgementRecord.isUsedValidator()) {
                    printWriter.print("/Validator");
                }
                printWriter.print(" at " + judgementRecord.getWhenJudgedTime());
                if (isPreliminaryJudgement(run, judgementRecord)) {
                    printWriter.print(" (preliminary)");
                }
                printWriter.println();
            }
        }
        
        printWriter.println();
    }
    
    private boolean isPreliminaryJudgement(Run run, JudgementRecord record) {

        Problem problem = contest.getProblem(run.getProblemId());
        if (problem.isManualReview() && problem.isComputerJudged()) {
            /**
             * Only preliminary possible is if is manual review AND computer judged.
             */

            JudgementRecord[] records = run.getAllJudgementRecords();
            if (records != null) {
                /**
                 * If there are judgements, only the first (computer judged) will be a preliminary judged run.
                 */
                return records[0].getElementId().equals(record.getElementId());
            }
        }
        // else - not possible at this time to be anything else but final judgement

        return false;

    }
    
    private boolean isThisSite(int siteNumber) {
        return contest.getSiteNumber() == siteNumber;
    }

    public void writeReport(PrintWriter printWriter) {
        
        // Runs
        printWriter.println();
        Run[] runs = contest.getRuns();
        
        Arrays.sort(runs, new RunComparator());

        if (filter.isFilterOn()){
            printWriter.println("Filter: "+filter.toString());
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
                            printWriter.println("Exception in report: "+e.getMessage());
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
                    printWriter.println("Exception in report: "+e.getMessage());
                    e.printStackTrace(printWriter);
                }
            }
            
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

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
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
