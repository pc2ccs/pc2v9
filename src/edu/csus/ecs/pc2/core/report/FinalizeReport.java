// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
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

    @Override
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
            printWriter.println("  WF Rankings : " + data.isUseWFGroupRanking());
        } else {
            printWriter.println("  certified   : false");
            printWriter.println("   ** no data ** ");

        }
    }

    @Override
    public void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println(getReportTitle() + " Report");
    }

    @Override
    public void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report");
    }

    @Override
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

    @Override
    public String[] createReport(Filter inFilter) {
        throw new SecurityException("Not implemented");
    }

    @Override
    public String createReportXML(Filter inFilter) throws IOException {
        return Reports.notImplementedXML(this);
    }

    @Override
    public String getReportTitle() {
        return "Finalize-Certify";
    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    @Override
    public String getPluginTitle() {
        return "Finalize-Certify Report";
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
