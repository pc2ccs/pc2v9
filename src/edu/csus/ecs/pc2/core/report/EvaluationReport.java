package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.RunCompartorByElapsed;
import edu.csus.ecs.pc2.core.log.EvaluationLog;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.Run.RunStates;

/**
 * Print evaluations.log output.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
// $HeadURL$
public class EvaluationReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = 8682295845820082842L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter = new Filter();

    public void writeReport(PrintWriter printWriter) throws IOException {

        Run[] runs = contest.getRuns();

        Arrays.sort(runs, new RunCompartorByElapsed());

        int matchingRuns = 0;
        for (Run run : runs) {
            if (filter.matches(run)) {
                EvaluationLog.printEvaluationLine(printWriter, run, contest);
                matchingRuns ++;
            }
        }
        printWriter.println();
        printWriter.println(matchingRuns+" runs output (of "+runs.length+" runs)");
    }

    private void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println(getReportTitle() + " Report");
    }

    public void createReportFile(String filename, Filter inFilter) throws IOException {

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);
        
        if (inFilter != null){
            setFilter(inFilter);
        }

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
        return "Evaluations";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
        
        filter.addRunState(RunStates.JUDGED);
    }

    public String getPluginTitle() {
        return "Evaluations Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter inFilter) {
        this.filter = inFilter;
    }

}
