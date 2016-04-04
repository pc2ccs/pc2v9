package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.exports.ccs.StandingsJSON;
import edu.csus.ecs.pc2.exports.ccs.StandingsJSON2016;

/**
 * 2016 CLI ICPC JSON Standings Report
 * 
 * @author pc2@ecs.csus.edu
 */
public class JSON2016Report implements IReportFile {

    /**
     * 
     */
    private static final long serialVersionUID = -6734389749065865458L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter theFilter = null;

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        controller = inController;
        contest = inContest;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "JSON 2016 Scoreboard";
    }

    public void createReportFile(String filename, Filter filter) throws IOException {
        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);
        theFilter = filter;

        try {
            printHeader(printWriter);

            try {
                writeReport(printWriter);
                System.out.println();
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
        theFilter = filter;
        StandingsJSON2016 standings = new StandingsJSON2016();
        try {
            String result = standings.createJSON(contest, controller);
            String[] list = { result };
            return list;
        } catch (IllegalContestState e) {
            log.log(Log.WARNING, e.getMessage(), e.getCause());
        } finally {
            standings = null;
        }
        return new String[0];
    }

    public String createReportXML(Filter filter) throws IOException {
        return Reports.notImplementedXML(this);
    }

    public void writeReport(PrintWriter printWriter) throws Exception {
        StandingsJSON standingsJSON = new StandingsJSON();
        printWriter.print(standingsJSON.createJSON(contest));
        standingsJSON = null;
    }

    public String getReportTitle() {
        return "JSON 2016 Scoreboard";
    }

    public Filter getFilter() {
        return theFilter;
    }

    public void setFilter(Filter filter) {
        theFilter = filter;
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

    @Override
    public boolean suppressHeaderFooter() {
        return true;
    }

}
