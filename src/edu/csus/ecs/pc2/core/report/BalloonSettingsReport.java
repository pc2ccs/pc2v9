package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.BalloonSettingsComparatorbySite;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.Problem;

/**
 * Print All BalloonSettings Information.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class BalloonSettingsReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = -2481037621846733149L;

    private IContest contest;

    private IController controller;

    private Log log;

    private Filter filter;

    public void writeReport(PrintWriter printWriter) {

        // BalloonSettingss
        printWriter.println();
        BalloonSettings[] balloonSettings = contest.getBalloonSettings();
        Problem[] problems = contest.getProblems();

        Arrays.sort(balloonSettings, new BalloonSettingsComparatorbySite());

        printWriter.println("-- " + balloonSettings.length + " BalloonSettings--");
        for (BalloonSettings balloonSetting : balloonSettings) {
            printWriter.println("Site '" + balloonSetting.getSiteNumber() + "' id=" + balloonSetting.getElementId());
            printWriter.println("      Balloon Client     : " + balloonSetting.getBalloonClient());
            printWriter.println("      Print balloons     : " + balloonSetting.isPrintBalloons());
            printWriter.println("      Print device       : " + balloonSetting.getPrintDevice());
            printWriter.println("      Postscript capable : " + balloonSetting.isPostscriptCapable());
            printWriter.println();
            printWriter.println("      email Balloons     : " + balloonSetting.isEmailBalloons());
            printWriter.println("      email to           : " + balloonSetting.getEmailContact());
            printWriter.println("      SMTP  server       : " + balloonSetting.getMailServer());
            printWriter.println("      Lines Per Page     : " + balloonSetting.getLinesPerPage());
            printWriter.println("      send No judgements : " + balloonSetting.isIncludeNos());

            int counter = 1;
            for (Problem problem : problems) {
                printWriter.println("      ["+counter+"] "+balloonSetting.getColor(problem)+" "+problem.getDisplayName());
                counter ++;
            }
            printWriter.println();
        }
    }

    private void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
    }

    private void printFooter(PrintWriter printWriter) {
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
        return "BalloonSettings";
    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "BalloonSettingsReport";
    }
    
    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
