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

/**
 * An abstract report that requires a minimal set of report methods to implement. 
 *
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public abstract class AbstractReport implements IReport {

    private static final long serialVersionUID = 4232024589391418206L;

    private IInternalContest contest;

    private IInternalController controller;

    /**
     * Log instance.
     */
    // use protected so child classes can use this directly.
    protected Log log;

    private Filter filter = new Filter();

    @Override
    public abstract void writeReport(PrintWriter printWriter) throws Exception;

    @Override
    public abstract String getReportTitle();

    @Override
    public abstract String getPluginTitle();

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
        }
    }

    public String[] createReport(Filter inFilter) {
        throw new RuntimeException("Not implemented");
    }

    public String createReportXML(Filter inFilter) throws IOException {
        return Reports.notImplementedXML(this);
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public IInternalContest getContest() {
        return contest;
    }
    
    public IInternalController getController() {
        return controller;
    }
    
    public Log getLog() {
        return log;
    }
}
