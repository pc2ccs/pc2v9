package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Hashtable;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.NotificationXML;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.BalloonDeliveryInfo;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.util.XMLMemento;

/**
 * Notifications XML Report.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class NotificationsReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = -2555946890129189222L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter = new Filter();

    /**
     * Hashtable used to determine whether team/problem has solved a problem.
     */
    private Hashtable<String, Run> solvedHash = new Hashtable<String, Run>();

    public void writeReport(PrintWriter printWriter) throws IOException {

        printWriter.println(createReportXML(filter));
        printWriter.println();
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

        NotificationXML notificationXML = new NotificationXML();

        BalloonSettings balloonSettings = contest.getBalloonSettings(contest.getSiteNumber());

        if (balloonSettings == null) {
            return "<notifications><!-- No Notifications --></notifications>";
        } else {

            String xml = "<list>\n";

            ClientSettings settings = new ClientSettings(balloonSettings.getBalloonClient());
            Hashtable<String, BalloonDeliveryInfo> deliveryHash = settings.getBalloonList();

            Run[] runs = contest.getRuns();
            Arrays.sort(runs, new RunComparator());

            for (Run run : runs) {
                if (run.isSolved()) {

                    String key = notificationXML.getBalloonKey(run.getSubmitter(), run.getProblemId());
                    BalloonDeliveryInfo deliveryInfo = deliveryHash.get(key);

                    if (deliveryInfo != null && !isAlreadySolved(run)) {
                        XMLMemento xmlMemento;
                        try {
                            xmlMemento = notificationXML.createElement(contest, run);
                            xml += xmlMemento.saveToString();
                        } catch (Exception e) {
                            throw (IOException) e;
                        }
                    } else {
                        System.err.println("No delivery info for run " + run);
                    }
                }
            }

            return xml + "\n</list>\n";
        }
    }

    /**
     * 
     * @param run
     * @return true if run client has solved problem
     */
    private boolean isAlreadySolved(Run run) {

        String key = run.getSubmitter().getTripletKey() + ":" + run.getProblemId();

        if (solvedHash.get(key) != null) {
            return true;
        }
        solvedHash.put(key, run);
        return false;
    }

    public String getReportTitle() {
        return "Notifications XML";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Notifications XML Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }
}
