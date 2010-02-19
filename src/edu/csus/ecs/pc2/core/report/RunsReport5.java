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
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * Print Runs with 5 comma delimited fields: RunID TeamID Prob Time Result.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class RunsReport5 implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = -1573641523768053182L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter = new Filter();

    int problemNumber(ElementId id) {
        Problem[] problems = contest.getProblems();
        for (int i = 0; i < problems.length; i++) {
            if (problems[i].getElementId().equals(id)) {
                return i;
            }
        }
        System.err.println("Searched " + problems.length + " problems did not find " + id);
        return 1;
    }

    private void writeRow(PrintWriter printWriter, Run run) {

        ClientId clientId = run.getSubmitter();

        // RunID TeamID Prob Time Result
        printWriter.print(run.getNumber());
        printWriter.print(",");

        printWriter.print(clientId.getClientNumber());
        printWriter.print(",");

        char let = 'A';
        let += problemNumber(run.getProblemId());
        printWriter.print(let);
        printWriter.print(",");

        printWriter.print(run.getElapsedMins());
        printWriter.print(",");

        if (run.isJudged()) {

            if (run.isSolved()) {
                printWriter.print("Yes");
            } else {
                printWriter.print("No");
            }

        } else {
            printWriter.print("New");
        }

        printWriter.println();
    }

     public void writeReport(PrintWriter printWriter) {

        // Runs
        printWriter.println();
        Run[] runs = contest.getRuns();
        Arrays.sort(runs, new RunComparator());

        if (filter.isFilterOn()) {
            printWriter.println("Filter: " + filter.toString());

            int count = 0;
            for (Run run : runs) {
                if (filter.matches(run)) {
                    count++;
                }
            }

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

    public String createReportXML(Filter inFilter) {
        throw new SecurityException("Not implemented");
    }

    public String getReportTitle() {
        return "Run 5 field";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Run 5 field Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
