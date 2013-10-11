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

/**
 * JSON Standings Report
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class JSONReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = 7792034304338639074L;

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
        return "JSON Standings";
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
        StandingsJSON standingsJSON = new StandingsJSON();
        try {
            String result = standingsJSON.createJSON(contest);
            String [] list = { result };
            return list;
        } catch (IllegalContestState e) {
            log.log(Log.WARNING, e.getMessage(), e.getCause());
        } finally {
            standingsJSON = null;
        }
        return new String[0];
    }

    public String createReportXML(Filter filter) throws IOException {
        throw new SecurityException("Not implemented");
    }

    public void writeReport(PrintWriter printWriter) throws Exception {
        StandingsJSON standingsJSON = new StandingsJSON();
        printWriter.print(standingsJSON.createJSON(contest));
        standingsJSON = null;
    }

    public String getReportTitle() {
        return "JSON Standings";
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


}
