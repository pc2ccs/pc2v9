package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.GregorianCalendar;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.services.core.EventFeedJSON;

/**
 * Event Feed JSON report.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class EventFeedJSONReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = -3444824680719793748L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter = new Filter();
    
    private void writeContestTime(PrintWriter printWriter) {
        printWriter.println();
        GregorianCalendar resumeTime = contest.getContestTime().getResumeTime();
        if (resumeTime == null) {
            printWriter.println("Contest date/time: never started");
        } else {
            printWriter.println("Contest date/time: " + resumeTime.getTime());

        }
    }

    public void writeReport(PrintWriter printWriter) throws IllegalContestState {
      
        EventFeedJSON efEventFeedJSON = new EventFeedJSON(contest);
        
        String s = efEventFeedJSON.createJSON(contest);
        
        printWriter.print(s);
    }

    public void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println(getReportTitle() + " Report");
        
        writeContestTime(printWriter);
        printWriter.println();
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

    public String createReportXML(Filter inFilter) throws IOException {
        return Reports.notImplementedXML(this);
    }

    public String getReportTitle() {
        return "Event Feed JSON";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return  getReportTitle() + " Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
