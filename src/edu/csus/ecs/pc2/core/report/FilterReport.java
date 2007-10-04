package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run.RunStates;

/**
 * Print All Filter Information.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class FilterReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = -1515524938023295459L;

    private IContest contest;

    private IController controller;

    private Log log;

    private void writeReport(PrintWriter printWriter) {
        
        // find any filters in contes and dump them here.
        
        throw new UnsupportedOperationException(); // TODO code
    }

    public void writeReportDetailed(PrintWriter printWriter, Filter filter) {

        printWriter.println("  '" + filter + " " + filter);

        printWriter.println("     Filter problems: " + filter.isFilteringProblems());
        printWriter.println("   Filter run states: " + filter.isFilteringRunStates());
        printWriter.println();

        try {
            ElementId[] elementIds = filter.getProblemIdList();
            printWriter.println("-- " + elementIds.length + " Problems filtered --");
            for (ElementId elementId : elementIds) {
                Problem problem = contest.getProblem(elementId);
                if (problem == null) {
                    printWriter.println("   Not displayed " + problem);
                } else {
                    printWriter.println("   " + problem);
                }
            }

        } catch (UnsupportedOperationException notex) {
            printWriter.println(notex.getMessage());
        }

        printWriter.println();
        
        try {
            RunStates[] runStatesList = filter.getRunStates();
            printWriter.println("-- " + runStatesList.length + " Run states filtered --");
            for (RunStates runStates : runStatesList) {
                printWriter.println("   " + runStates);
            }
        } catch (UnsupportedOperationException notex) {
            printWriter.println(notex.getMessage());
        }

    }

    public void writeReport(PrintWriter printWriter, Filter filter) {

        // Filters
        printWriter.println();

        printWriter.println("  '" + filter + " " + filter);
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
        return "Filters";
    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Filters Report";
    }

}
