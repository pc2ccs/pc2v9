package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.list.RunCompartorByElapsed;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * Fastest solved problem.
 * 
 * Shows rank, elapsed time and team name (Site N)
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class FastestSolvedReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = 3878379207856922664L;

    private IContest contest;

    private IController controller;

    private Log log;

    private Filter filter;

    public void writeReport(PrintWriter printWriter) {

        printWriter.println();

        Run[] runs = contest.getRuns();
        printWriter.println("There are " + runs.length + " runs.");
        printWriter.println();

        Problem[] problems = contest.getProblems();

        if (problems == null || problems.length == 0) {

            printWriter.println("No problems defined, no summary");

            return;
        }

        int numDeleted = 0;

        for (Problem problem : problems) {

            Run[] runList = filterForProblem(runs, problem);

            // Then we sort by elapsed time
            Arrays.sort(runList, new RunCompartorByElapsed());

            printWriter.println("Problem " + problem);
            printWriter.println();
            printWriter.println("Rank Elap Run# Team");

            // TODO filter out when there are multiple yes for same team same problem.

            int numberSolved = 0;

            int rank = 0;
            int tieRank = 0;
            
            long lastElapsed = -1;
            for (Run run : runList) {
                if (!run.isDeleted() && run.isSolved()) {

                    numberSolved++;

                    long elapsed = run.getElapsedMins();

                    String teamName = run.getSubmitter().toString();
                    Account account = contest.getAccount(run.getSubmitter());
                    if (account != null) {
                        teamName = account.getDisplayName();
                    }

                    teamName = teamName + " (Site " + run.getSubmitter().getSiteNumber() + ")";

                    if (elapsed != lastElapsed) {
                        rank++;
                        tieRank = rank;
                        printWriter.format(" %3d %4d %4d ", tieRank, elapsed, run.getNumber());
                        printWriter.println(teamName);
                        lastElapsed = elapsed;
                    } else {
                        rank++;
                        printWriter.format(" %3d %4d %4d ", tieRank, elapsed, run.getNumber());
                        printWriter.println(teamName);
                    }
                } else {
                    numDeleted++;
                }
            }

            printWriter.println();
            if (numberSolved > 0) {
                printWriter.println("There are " + numberSolved + " runs solving "+problem);
            } else {
                printWriter.println("No solutions for problem "+problem);
            }
            printWriter.println();
        }

        if (numDeleted > 0) {
            printWriter.println("There are " + numDeleted + " runs marked deleted");
        }

    }

    private Run[] filterForProblem(Run[] runs, Problem problem) {

        Vector<Run> runVector = new Vector<Run>();

        for (Run run : runs) {
            if (run.getProblemId().equals(problem.getElementId())) {
                runVector.addElement(run);
            }
        }

        return (Run[]) runVector.toArray(new Run[runVector.size()]);
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
        return "Fastest Solved by Problem";
    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Fastest Solved by Problem";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
