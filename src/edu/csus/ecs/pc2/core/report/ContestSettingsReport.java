package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.ContestInformation.TeamDisplayMask;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;

/**
 * Print/Report a number internal settings.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ContestSettingsReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = 8596231586054536246L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter = new Filter();

    public void printContestInformation(PrintWriter printWriter) {

        ContestInformation contestInformation = contest.getContestInformation();

        printWriter.println();
        printWriter.println("-- Contest Information --");
        printWriter.println("  Title : '" + contestInformation.getContestTitle() + "'");
        printWriter.println("  URL   : '" + contestInformation.getContestURL() + "'");

        printWriter.println();
        printWriter.println("  C.C.S. Test Mode                                    : " + Utilities.yesNoString(contestInformation.isCcsTestMode()));
        printWriter.println("  Auto-registration enabled                           : " + Utilities.yesNoString(contestInformation.isEnableAutoRegistration()));
        printWriter.println("  Include Preliminary Judgements in Scoring Algorithm : " + Utilities.yesNoString(contestInformation.isPreliminaryJudgementsUsedByBoard()));
        printWriter.println("  Send Notifications for Preliminary Judgements       : " + Utilities.yesNoString(contestInformation.isPreliminaryJudgementsTriggerNotifications()));
        printWriter.println("  Send Additional Run Status Information              : " + Utilities.yesNoString(contestInformation.isSendAdditionalRunStatusInformation()));
        printWriter.println();

        printWriter.println("  Judges' Default Answer: '" + contestInformation.getJudgesDefaultAnswer() + "'");

        if (contestInformation.getTeamDisplayMode() != null) {
            printWriter.println("  Judges see: " + contestInformation.getTeamDisplayMode());
        } else {
            printWriter.println("  Judges see: " + TeamDisplayMask.LOGIN_NAME_ONLY);
        }

        printWriter.println();
        Properties properties = contestInformation.getScoringProperties();
        if (properties == null || properties.isEmpty()) {
            printWriter.println("  Note: Scoring Properties are null, using default");
            properties = DefaultScoringAlgorithm.getDefaultProperties();
        }

        Set<Object> set = properties.keySet();

        String[] keys = (String[]) set.toArray(new String[set.size()]);

        Arrays.sort(keys);
        printWriter.println("  Scoring Properties, there are " + keys.length + " keys");

        for (String key : keys) {
            printWriter.println("     " + key + "='" + properties.get(key) + "'");
        }

    }

    public void writeReport(PrintWriter printWriter) {

        printContestInformation(printWriter);

    }

    public void printHeader(PrintWriter printWriter) {
        VersionInfo versionInfo = new VersionInfo();
        printWriter.println(versionInfo.getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(versionInfo.getSystemVersionInfo());
        printWriter.println("Build " + versionInfo.getBuildNumber());

    }

    public void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report");
    }

    public void createReportFile(String filename, Filter inFilter) throws IOException {

        if (inFilter == null) {
            throw new IllegalArgumentException("filter must not be null");
        }
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

        } catch (Exception e) {
            log.log(Log.INFO, "Exception writing report", e);
            printWriter.println("Exception generating report " + e.getMessage());
        }

        printWriter.close();
        printWriter = null;
    }

    public String[] createReport(Filter inFilter) {
        throw new SecurityException("Not implemented");
    }

    public String createReportXML(Filter inFilter) throws IOException {
        return Reports.notImplementedXML(this);
    }

    public String getReportTitle() {
        return "Contest Settings";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Contest Settings Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    /**
     * Count the accounts that match this filter.
     * 
     * @param accounts
     * @return number of accounts matching this filter.
     */
    public int countClientSettings(ClientSettings[] clientSettings) {
        int count = 0;
        for (ClientSettings clientSettings2 : clientSettings) {
            if (filter.matches(clientSettings2.getClientId())) {
                count++;
            }
        }
        return count;
    }

}
