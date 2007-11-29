package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Hashtable;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * Summary of solutions by problem.
 * 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class SolutionsByProblemReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = 1389857029019327047L;

    private IContest contest;

    private IController controller;

    private Log log;

    private Hashtable<ElementId, Integer> problemLookup = new Hashtable<ElementId, Integer>();

    private Filter filter;

    public void writeReport(PrintWriter printWriter) {

        Problem[] problems = contest.getProblems();

        printWriter.println();

        Run[] runs = contest.getRuns();
        printWriter.println("There are " + runs.length + " runs.");
        printWriter.println();

        if (problems == null || problems.length == 0) {

            printWriter.println("No problems defined, no summary");

            return;
        }

        // Load problem order into problemLookup

        int idx = 0;
        for (Problem problem : problems) {
            problemLookup.put(problem.getElementId(), new Integer(idx));
            idx++;
        }

        int[] numberSolved = new int[problems.length];
        int[] numberAttempted = new int[problems.length];
        int[] didNotSolve = new int[problems.length];

        // Simple accumulation

        int theIndex;

        int numDeleted = 0;

        int totalNotSolved = 0;
        int totalSolved = 0;
        int totalAttempts = 0;

        for (Run run : runs) {
            if (!run.isDeleted()) {
                theIndex = problemLookup.get(run.getProblemId()).intValue();
                if (run.isSolved()) {
                    numberSolved[theIndex]++;
                } else {
                    didNotSolve[theIndex]++;
                }
                numberAttempted[theIndex]++;
            } else {
                numDeleted++;
            }
        }

        printWriter.println("Incorrect  Correct  Total     %   Problem Description");
        printWriter.println("--------- --------- ----- ------  -------------------");

        for (Problem problem : problems) {
            theIndex = problemLookup.get(problem.getElementId()).intValue();

            totalNotSolved += didNotSolve[theIndex];
            totalSolved += numberSolved[theIndex];
            totalAttempts += numberAttempted[theIndex];

            printWriter.format("   %3d       %3d     %3d ", didNotSolve[theIndex], numberSolved[theIndex], numberAttempted[theIndex]);
            printWriter.print(" " + printPercent(numberSolved[theIndex], numberAttempted[theIndex]) + "  ");
            printWriter.println(problem);

        }
        printWriter.println("--------- --------- ----- ------  -------------------");
        printWriter.format("   %3d       %3d     %3d ", totalNotSolved, totalSolved, totalAttempts);
        printWriter.print(" " + printPercent(totalSolved, totalAttempts) + "  ");
        printWriter.print("Totals");
        printWriter.println();
        printWriter.println("--------- --------- ----- ------  -------------------");

        if (numDeleted > 0) {
            printWriter.println("There are " + numDeleted + " runs marked deleted");
        }

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
        return "Solutions By Problem";
    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Solutions By Problem Report";
    }

    /**
     * Print percent for numerator and denominator.
     * 
     * Left pads with blanks in form: ###.##
     * 
     * @param num
     * @param denom
     * @return string of percentage
     */
    protected String printPercent(long numerator, long denominator) {
        long intpart;
        if (denominator > 0) {
            intpart = (numerator * 10000) / denominator;
        } else {
            intpart = 0;
        }

        long decpart = intpart % 100;

        if (numerator > 0) {
            intpart /= 100;
            return (new String(longrj(intpart, 3, " ") + "." + longrj(decpart, 2, "0")));
        }
        return (new String("  0.00"));
    }

    protected String longrj(long l, int cols, String pads) {
        return longrj(new Long(l), cols, pads);
    }

    protected String longrj(Long l, int cols, String pads) {
        String s = "" + l.toString();
        while (s.length() < cols) {
            s = pads + s;
        }
        return s;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
