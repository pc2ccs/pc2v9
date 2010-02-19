package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.ClientIdComparator;
import edu.csus.ecs.pc2.core.list.ClientSettingsComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.BalloonDeliveryInfo;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Site;

/**
 * Print Balloon Delivery Report.
 * 
 * This fetches the Balloon Delivery information sorts in the following order: site number, team number, problem
 * <P>
 * The report is control break on team and then problem. Each team info is printed, then indented for each balloon deliver is: problem letter - problem title at datestring (where datestring is
 * HH:MM:SS TZ YYYY-MM-DD DOW, for example: 15:02:00 -0800 2009-11-07 Sat)
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

    private ClientIdComparator comparator = new ClientIdComparator();
    
    private SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss Z yyyy-MM-dd E");

    /**
     * Comparator to sort the balloonKeys.
     * 
     * @author pc2@ecs.csus.edu
     *
     */
    protected class BalloonKeyComparator implements Comparator<String>, Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = 3204840271423881062L;

        /**
         * 
         * @param clientIDString
         *            a string in form 1TEAM12 for Team 12 Site 1
         * @return
         */
        public ClientId getClientId(String clientIDString) {

            String clientTypeName = ClientType.Type.TEAM.toString();
            int teamIndex = clientIDString.indexOf(clientTypeName);

            if (teamIndex > 0) {
                int siteNumber = Integer.parseInt(clientIDString.substring(0, teamIndex));
                int teamNumber = Integer.parseInt(clientIDString.substring(teamIndex + clientTypeName.length()));
                return new ClientId(siteNumber, ClientType.Type.TEAM, teamNumber);

            } else {
                return new ClientId(0, ClientType.Type.TEAM, 0);
            }
        }

        public int compare(String key1, String key2) {

            String[] fields1 = key1.split(" ");
            String[] fields2 = key2.split(" ");

            ClientId clientId1 = getClientId(fields1[0]);
            ClientId clientId2 = getClientId(fields2[0]);

            return comparator.compare(clientId1, clientId2);
        }
    }

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
            Arrays.sort(keyList, new BalloonKeyComparator());
            for (String key : keyList) {

                BalloonDeliveryInfo balloonDeliveryInfo = hashtable.get(key);
                balloonDeliveries++;

                boolean matchesFilter = true;

                if (filter.isFilterOn()) {
                    matchesFilter = filter.matches(balloonDeliveryInfo.getClientId()) && filter.matchesProblem(balloonDeliveryInfo.getProblemId());
                }

                if (matchesFilter) {

                    foundMatching++;

                    if (lastClientId == null || (!lastClientId.equals(balloonDeliveryInfo.getClientId()))) {
                        printWriter.println();
                        lastClientId = balloonDeliveryInfo.getClientId();
                        Account account = contest.getAccount(lastClientId);
                        String accountTitle = "";
                        if (account != null) {
                            accountTitle = " (" + account.getDisplayName();
                            Site site = contest.getSite(lastClientId.getSiteNumber());
                            if (site != null) {
                                accountTitle += " at " + site.getDisplayName();
                            }
                            accountTitle += ")";

                        }

                        printWriter.println("     Client " + lastClientId.toString() + accountTitle);
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
        if (balloonDeliveries > 0) {
            if (foundMatching > 0) {
                printWriter.println("There were only " + foundMatching + " deliveries that matched the filter.");
            } else {
                printWriter.println("There were no matching balloons for this report");
            }
        }

    }

    private void printBalloonDeliveryInfo(PrintWriter printWriter, BalloonDeliveryInfo balloonDeliveryInfo) {
        Problem problem = contest.getProblem(balloonDeliveryInfo.getProblemId());
        Date date = new Date(balloonDeliveryInfo.getTimeSent());
        printWriter.println("            " + problem + " at " + formatter.format(date));
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
