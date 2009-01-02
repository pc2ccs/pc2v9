package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.BalloonSettingsComparatorbySite;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;

/**
 * Print All Balloon Settings Information.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class BalloonSettingsReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = -3705067685117724687L;

    private IInternalContest contest;

    private IInternalController controller;

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
            try {
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
                printWriter.println();

                int counter = 1;
                for (Problem problem : problems) {
                    printWriter.println("      ["+counter+"] "+problem.getDisplayName()+" '"+ balloonSetting.getColor(problem)+"'");
                    counter ++;
                }
                printWriter.println();
            } catch (Exception e) {
                e.printStackTrace(printWriter);
            }
        }
    }

    private void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println(getReportTitle() + " Report");
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
        return "Notification Settings";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Notification Settings Report";
    }
    
    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
