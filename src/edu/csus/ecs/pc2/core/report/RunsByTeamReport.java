package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.list.RunComparatorByTeamProblem;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * Print all runs grouped by team.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class RunsByTeamReport implements IReport {

    private IContest contest;

    private IController controller;

    private Log log;

    private Filter accountFilter = new Filter();

    private void writeReport(PrintWriter printWriter) {

        // Runs
        Run[] runs = contest.getRuns();
        Arrays.sort(runs, new RunComparatorByTeamProblem());

        int count = 0;
        for (Run run : runs) {
            if (accountFilter.matches(run)) {
                count++;
            }
        }

        if (accountFilter.isThisSiteOnly()) {
            printWriter.print(" for site " + accountFilter.getSiteNumber());
        }

        ClientId currentTeam = null;

        Vector<Run> accumRuns = new Vector<Run>();
        printWriter.println();
        if (count > 0) {
            for (Run run : runs) {
                if (accountFilter.matches(run)) {
                    if (run.getSubmitter().equals(currentTeam)) {
                        accumRuns.add(run);

                    } else {
                        if (accumRuns.size() > 0) {
                            printRuns(printWriter, currentTeam, accumRuns);
                        }
                        currentTeam = run.getSubmitter();
                        accumRuns = new Vector<Run>();
                        accumRuns.add(run);
                    }
                }
            }

            if (accumRuns.size() > 0) {
                printRuns(printWriter, currentTeam, accumRuns);
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

    private String getLanguageTitle(ElementId languageId) {
        Language language = contest.getLanguage(languageId);
        if (language != null) {
            return language.getDisplayName();
        }
        return "UndefLang";
    }

    private String getProblemTitle(ElementId problemId) {
        Problem problem = contest.getProblem(problemId);
        if (problem != null) {
            return problem.toString();
        }
        return "UndefProb";
    }

    @SuppressWarnings("unused")
    private String getSiteTitle(String string) {
        return "Site " + string;
    }
    
    private void printProblemSummary(PrintWriter printWriter, int attempts, int solved, int deleteCount){
        printWriter.print("             " + attempts + " attempts. ");
        if (deleteCount > 0) {
            printWriter.print(" "+deleteCount+" deleted");
        }
        if (solved > 0) {
            printWriter.print(" SOLVED");
        } else {
            printWriter.print(" NOT solved");
        }

    }

    /**
     * Print run by problem for team.
     * 
     * @param printWriter
     * @param currentTeam
     * @param accumRuns
     */
    private void printRuns(PrintWriter printWriter, ClientId currentTeam, Vector<Run> accumRuns) {

        printWriter.println();
        printWriter.println();
        printWriter.println(currentTeam.getName() + " Site " + currentTeam.getSiteNumber() + " - '" + getClientName(currentTeam) + "'");
        
        long firstSolveTime = 0;
        int solved = 0;
        int attempts = 0;
        int deleteCount = 0;
        ElementId lastProblemId = null;

        for (int i = 0; i < accumRuns.size(); i++) {
            Run run = accumRuns.elementAt(i);

            if (!run.getProblemId().equals(lastProblemId)) {
                if (solved > 0 || attempts > 0) {
                    
                    printProblemSummary(printWriter, attempts, solved, deleteCount);
                    printWriter.println();
                    printWriter.println();
                }
                    
                lastProblemId = run.getProblemId();
                printWriter.println("   Problem " + getProblemTitle(run.getProblemId()));
                solved = 0;
                attempts = 0;
                firstSolveTime = 0;
                deleteCount = 0;
            }

            printWriter.format("     %3d %3d", run.getNumber(), run.getElapsedMins());
            if (run.isDeleted()){
                printWriter.print(" DELETED");
                deleteCount++;
            }
            printWriter.print(" " +run.getStatus());
            attempts++;
            if (run.isJudged()) {

                JudgementRecord judgementRecord = run.getJudgementRecord();
                Judgement judgement = contest.getJudgement(judgementRecord.getJudgementId());
                printWriter.print(" " + judgement.toString().substring(0, 3));

                if (run.isSolved() && (! run.isDeleted())) {
                    if (firstSolveTime == 0){
                        firstSolveTime = run.getElapsedMins();
                    }
                    solved++;
                }

            }
            printWriter.print(" " + getLanguageTitle(run.getLanguageId()));
            printWriter.println();
        }

        if (solved > 0 || attempts > 0) {
            printProblemSummary(printWriter, attempts, solved, deleteCount);
        }
    }

    private void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + new Date());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println(getReportTitle());
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

    public String[] createReport(Filter filter) {
        throw new SecurityException("Not implemented");
    }

    public String createReportXML(Filter filter) {
        throw new SecurityException("Not implemented");
    }

    public String getReportTitle() {
        return "Runs grouped by team Report";
    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Runs grouped by team Report";
    }

}
