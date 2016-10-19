package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.exports.ccs.ResolverEventFeedXML;

/**
 * The Event Feed - Event Feed XML for ICPC Tools.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: EventFeedReport.java 2363 2011-09-27 01:37:28Z laned $
 */

public class ResolverEventFeedReport implements IReport {

    private static final long serialVersionUID = 635385259159143084L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter = new Filter();
    

    public void writeReport(PrintWriter printWriter) throws IOException {
        
        printWriter.println("-- Start Event Feed XML --");
        printWriter.println(createReportXML(filter));
        printWriter.println();
        printWriter.println("-- End Event Feed XML --");
    }

    public void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println(getReportTitle() + " Report");
    }

    /**
     * 
     */
    public void createReportFile(String filename, Filter inFilter) throws IOException {

        filter = inFilter;

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

    public String[] createReport(Filter inFilter) {
        filter = inFilter;
        return new String[0];
    }

    public String createReportXML(Filter inFilter) throws IOException {
        if (contest != null){
            ResolverEventFeedXML resolverEventFeedXML = new ResolverEventFeedXML();
            return resolverEventFeedXML.toXML(contest, filter);
        } else {
            return Reports.notImplementedXML(this, "contest is not set (null)");
        }
    }

    public String getReportTitle() {
        return "ICPC Tools Event Feed";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "ICPC Tools Event Feed Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
