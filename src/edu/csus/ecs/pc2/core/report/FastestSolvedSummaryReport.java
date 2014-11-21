package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Vector;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.RunCompartorByElapsed;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * Shows in descending time order which teams solved which problems fastest.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class FastestSolvedSummaryReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = 3878379207856922664L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter = new Filter();

    public void writeReport(PrintWriter printWriter) {

        printWriter.println();

        Run[] runs = contest.getRuns();

        Vector<Run> fastestRuns = new Vector<Run>();

        int count = filter.countRuns(runs);

        printWriter.println("There are " + count + " runs.");
        printWriter.println();

        Problem[] problems = contest.getProblems();
        if (problems == null || problems.length == 0) {

            printWriter.println("No problems defined, nothing to report");
            return;
        }

        int totalSolved = 0;
        for (Run run : runs) {
            if (!run.isDeleted() && run.isSolved()) {
                totalSolved++;
            }
        }

        if (totalSolved < 1) {
            printWriter.println("No runs have been solved, nothing to report");
            return;
        }

        int solved = 0;
        printWriter.println();
        printWriter.println(" ** Summarized by team");
        printWriter.println();

        printWriter.println("   At Team                                Problem");
        printWriter.println("  --- ----------------------------------- ------------------");

        for (Problem problem : problems) {

            Run[] runList = filterForProblem(runs, problem);

            // Then we sort by elapsed time
            Arrays.sort(runList, new RunCompartorByElapsed());

            for (Run run : runList) {
                if (!run.isDeleted() && run.isSolved() && filter.matches(run)) {
                    fastestRuns.add(run);
                    solved++;
                    break;
                }
            }
        }

        runs = (Run[]) fastestRuns.toArray(new Run[fastestRuns.size()]);
        Arrays.sort(runs, new RunCompartorByElapsed());

        for (Run run : runs) {

            try {
                Account account = contest.getAccount(run.getSubmitter());

                String accountName = run.getSubmitter().getName() + " (site " + run.getSiteNumber() + ")";
                if (account != null) {
                    accountName = account.getDisplayName();
                }
                Problem problem = contest.getProblem(run.getProblemId());
                printWriter.format("%5d %-35s %s", run.getElapsedMins(), accountName, problem.getDisplayName());
                printWriter.println();

            } catch (Exception e) {
                printWriter.println("Run " + run + " Exception " + e.getMessage());
                e.printStackTrace(printWriter);
            }

        }

        // printWriter.format("      %-25s %s", "(Not solved)", problem.getDisplayName());
        // printWriter.println();

        printWriter.println("  --- ----------------------------------- ------------------");

        printWriter.println();
        printWriter.println();
        printWriter.println(" ** Summarized by problem");
        printWriter.println();
        printWriter.println("   At Problem                             Team");
        printWriter.println("  --- ----------------------------------- ---------------------------------");

        Vector<Problem> unsolvedProblems = new Vector<Problem>();

        for (Problem problem : problems) {

            boolean foundRun = false;

            String hiddenText = "";
            if (!problem.isActive()) {
                hiddenText = " [HIDDEN] ";
            }

            for (Run run : runs) {
                try {
                    if (run.getProblemId().equals(problem.getElementId())) {

                        Account account = contest.getAccount(run.getSubmitter());

                        String accountName = run.getSubmitter().getName() + " (site " + run.getSiteNumber() + ")";
                        if (account != null) {
                            accountName = account.getDisplayName();
                        }
                        printWriter.format("%5d %-35s %s", run.getElapsedMins(), hiddenText + problem.getDisplayName(), accountName);
                        printWriter.println();
                        foundRun = true;
                    }

                } catch (Exception e) {
                    printWriter.println("Run " + run + " Exception " + e.getMessage());
                    e.printStackTrace(printWriter);
                }
            }

            if (!foundRun) {
                printWriter.format("      %-35s %s", hiddenText + problem.getDisplayName(), "(Not Solved)");
                printWriter.println();
                unsolvedProblems.add(problem);
            }
        }
        printWriter.println("  --- ----------------------------------- ---------------------------------");

        printWriter.println();

        int notsolved = problems.length - solved;

        if (notsolved > 0) {
            printWriter.println("  ** " + notsolved + " problems were not solved");
            Problem[] problemList = (Problem[]) unsolvedProblems.toArray(new Problem[unsolvedProblems.size()]);

            for (Problem problem : problemList) {
                String hiddenText = "";
                if (!problem.isActive()) {
                    hiddenText = " [HIDDEN] ";
                }
                printWriter.println("     " + hiddenText + problem.getDisplayName());
            }

            printWriter.println();
        }

        if (problems.length == solved) {
            printWriter.println("ALL problems solved");
        } else if (solved == 0) {
            printWriter.println("NO problems solved");
        } else {
            printWriter.println("There were " + solved + " of " + problems.length + " problems solved.");
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

    public void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
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

    public void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report");
    }

    public String[] createReport(Filter arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public String createReportXML(Filter arg0) throws IOException {
        return Reports.notImplementedXML(this);  
    }

    public String getReportTitle() {
        return "Fastest Solutions Summary";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Fastest Solutions Summary Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
