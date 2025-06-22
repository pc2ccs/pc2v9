// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.GregorianCalendar;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.clics.API202306.CLICSScoreboard;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Print CLICS API Scoreboard JSON
 *
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class ScoreboardJSONReport implements IReport {

    private static final long serialVersionUID = 8239873747280839847L;

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

    @Override
    public void writeReport(PrintWriter printWriter) {
        String[] ouputArray = createReport(filter);
        for (String line : ouputArray) {
            printWriter.print(line);
        }

    }

    @Override
    public void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println(getReportTitle() + " Report");

        writeContestTime(printWriter);
        printWriter.println();
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

    @Override
    public String[] createReport(Filter inFilter) {
        try {
            CLICSScoreboard clicsScoreboard = new CLICSScoreboard(contest, null, null);
            String json = clicsScoreboard.toJSON();
            String[] sa = { json };
            return sa;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String createReportXML(Filter inFilter) throws IOException {
        return Reports.notImplementedXML(this);
    }

    @Override
    public String getReportTitle() {
        return "Scoreboard JSON";
    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    @Override
    public String getPluginTitle() {
        return "Scoreboard JSON Report";
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
