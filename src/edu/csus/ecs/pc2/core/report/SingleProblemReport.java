package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.GregorianCalendar;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;

/**
 * Print all problems info.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: ProblemsReport.java 2405 2011-11-13 05:13:34Z boudreat $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/core/report/ProblemsReport.java $
public class SingleProblemReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = -3951365325215042021L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter;

    private Problem problem;

    private ProblemDataFiles dataFiles;

    private ProblemsReport problemsReport;

    public void setProblem(Problem inProblem, ProblemDataFiles inDataFiles) {
        this.problem = inProblem;
        this.dataFiles = inDataFiles;
    }

    public Problem getProblem() {
        return problem;
    }

    public ProblemDataFiles getProblemDataFile() {
        return dataFiles;
    }

    private void writeContestTime(PrintWriter printWriter) {
        printWriter.println();
        GregorianCalendar resumeTime = contest.getContestTime().getResumeTime();
        if (resumeTime == null) {
            printWriter.println("Contest date/time: never started");
        } else {
            printWriter.println("Contest date/time: " + resumeTime.getTime());

        }
    }

    public void writeReport(PrintWriter printWriter) {
        printWriter.println();
        problemsReport.writeRow(printWriter, problem, dataFiles);
    }

    public void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println(getReportTitle() + " Report");

        writeContestTime(printWriter);
    }

    public void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report");
    }

    public void createReportFile(String filename, Filter inFilter) throws IOException {

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);

        try {

            try {
                printHeader(printWriter);

                writeReport(printWriter);

                printFooter(printWriter);

            } catch (Exception e) {
                printWriter.println("Exception in report: " + e.getMessage());
                e.printStackTrace(printWriter);
                e.printStackTrace(); // debug 22
            }

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
        return "Single Problem";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
        
        problemsReport = new ProblemsReport();
        problemsReport.setContestAndController(inContest, inController);
        
    }

    public String getPluginTitle() {
        return "Single Problem Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }
}
