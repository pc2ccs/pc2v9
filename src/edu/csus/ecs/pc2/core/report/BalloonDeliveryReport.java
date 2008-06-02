package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Hashtable;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.ClientSettingsComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.BalloonDeliveryInfo;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;

/**
 * Print Balloon Delivery Summary Report.
 * 
 * This fetches the Balloon Delivery Info.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class BalloonDeliveryReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = -5374491950286834509L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter = new Filter();

    public void writeReport(PrintWriter printWriter) {

        printWriter.println();

        if (filter.isFilterOn()) {
            printWriter.println(filter.toString());
        }

        ClientSettings[] clientSettingsList = contest.getClientSettingsList();
        Arrays.sort(clientSettingsList, new ClientSettingsComparator());

        int balloonDeliveries = 0;
        int foundMatching = 0;

        ClientId lastClientId = null;

        for (ClientSettings clientSettings : clientSettingsList) {
            Hashtable<String, BalloonDeliveryInfo> hashtable = clientSettings.getBalloonList();
            String[] keyList = (String[]) hashtable.keySet().toArray(new String[hashtable.keySet().size()]);
            Arrays.sort(keyList);
            for (String key : keyList) {

                BalloonDeliveryInfo balloonDeliveryInfo = hashtable.get(key);
                balloonDeliveries++;

                boolean matchesFilter = true;

                if (filter.isFilterOn()) {
                    matchesFilter = filter.matches(balloonDeliveryInfo.getClientId()) && filter.matchesProblem(balloonDeliveryInfo.getProblemId());
                }

                if (matchesFilter) {

                    foundMatching++;

                    if (lastClientId == null || lastClientId.equals(balloonDeliveryInfo.getClientId())) {
                        printWriter.println();
                        lastClientId = balloonDeliveryInfo.getClientId();
                        printWriter.println("     Client " + lastClientId.toString());
                    }

                    try {
                        printBalloonDeliveryInfo(printWriter, balloonDeliveryInfo);
                    } catch (Exception e) {
                        printWriter.println("For " + key + " exception " + e.getMessage());
                        controller.getLog().log(Log.WARNING, "Exception logged ", e);
                    }
                } // else ignore this
            }
        }

        printWriter.println();
        printWriter.println("There were " + balloonDeliveries + " delivered");
        if (balloonDeliveries > 0){
            if (foundMatching > 0) {
                printWriter.println("There were only " + foundMatching + " deliveries that matched the filter.");
            } else {
                printWriter.println("There were no matching balloons for this report");
            }
        }

    }

    private void printBalloonDeliveryInfo(PrintWriter printWriter, BalloonDeliveryInfo balloonDeliveryInfo) {
        Problem problem = contest.getProblem(balloonDeliveryInfo.getProblemId());
        printWriter.println("            " + problem + " at " + balloonDeliveryInfo.getTimeSent());

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
        return "Balloons Delivery";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Balloon Delivery Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }
}
