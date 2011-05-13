package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Print Finalize Report.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class FinalizeReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = -7306522824265732770L;

    /**
     * 
     */

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter;

    public void writeReport(PrintWriter printWriter) {
        printFinalizeData(printWriter);
    }

    private void printFinalizeData(PrintWriter printWriter) {

        FinalizeData data = contest.getFinalizeData();

        printWriter.println();
        printWriter.println("-- Finalized Info -- ");

        if (data != null) {
            printWriter.println("  certified   : " + data.isCertified());
            if (data.isCertified()) {
                printWriter.println("  certify date: " + data.getCertificationDate());
            }
            printWriter.println("  gold rank   : " + data.getGoldRank());
            printWriter.println("  silver rank : " + data.getSilverRank());
            printWriter.println("  bronze rank : " + data.getBronzeRank());
            printWriter.println("  comment     : " + data.getComment());
        } else {
            printWriter.println("  certified   : false");
            printWriter.println("   ** no data ** ");

        }
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

    public String[] createReport(Filter inFilter) {
        throw new SecurityException("Not implemented");
    }

    public String createReportXML(Filter inFilter) {
        throw new SecurityException("Not implemented");
    }

    public String getReportTitle() {
        return "Finalize-Certify";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Finalize-Certify Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
