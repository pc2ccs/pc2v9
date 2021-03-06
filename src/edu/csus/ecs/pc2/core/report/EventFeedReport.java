// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
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
import edu.csus.ecs.pc2.exports.ccs.EventFeedXML;

/**
 * Event Feed XML Report.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EventFeedReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = -2555946890129189222L;

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
            EventFeedXML eventFeedXML = new EventFeedXML();
            return eventFeedXML.toXML(contest, filter);
        } else{
            return Reports.notImplementedXML(this,"contest is null");
        }
    }

    public String getReportTitle() {
        return "Unused 2011 Event Feed XML";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Unused 2011 Event Feed XML Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
