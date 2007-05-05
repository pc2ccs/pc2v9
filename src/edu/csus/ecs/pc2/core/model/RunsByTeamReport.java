package edu.csus.ecs.pc2.core.model;

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

    private String getSiteTitle(String string) {
        return "Site " + string;
    }

    private void printRuns(PrintWriter printWriter, ClientId currentTeam, Vector<Run> accumRuns) {

        printWriter.println();
        printWriter.println(currentTeam.getName() + " Site " + currentTeam.getSiteNumber() + " - '" + getClientName(currentTeam) + "'");

        int solved = 0;
        int attempts = 0;
        ElementId lastProblemId = null;

        for (int i = 0; i < accumRuns.size(); i++) {
            Run run = accumRuns.elementAt(i);

            if (!run.getProblemId().equals(lastProblemId)) {
                if (solved > 0 || attempts > 0) {
                    printWriter.print("             " + attempts + " attempts. ");
                    if (solved > 0) {
                        printWriter.print(" SOLVED");
                    } else {
                        printWriter.print(" NOT solved");
                    }
                    printWriter.println();
                    printWriter.println();
                }
                lastProblemId = run.getProblemId();
                printWriter.println("   Problem " + getProblemTitle(run.getProblemId()));
                solved = 0;
                attempts = 0;
            }

            printWriter.format("     %3d %3d ", run.getNumber(), run.getElapsedMins());
            printWriter.print(run.getStatus());
            attempts++;
            if (run.isJudged()) {

                JudgementRecord judgementRecord = run.getJudgementRecord();
                Judgement judgement = contest.getJudgement(judgementRecord.getJudgementId());
                printWriter.print(" " + judgement.toString().substring(0, 3));

                if (run.isSolved()) {
                    solved++;
                }

            }
            printWriter.print(" " + getLanguageTitle(run.getLanguageId()));
            printWriter.println();
        }

        if (solved > 0 || attempts > 0) {
            printWriter.print("             " + attempts + " attempts. ");
            if (solved > 0) {
                printWriter.print(" SOLVED");
            } else {
                printWriter.print(" NOT solved");
            }
            printWriter.println();
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
