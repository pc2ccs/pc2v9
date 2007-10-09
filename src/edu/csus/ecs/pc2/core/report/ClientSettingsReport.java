package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.list.ClientSettingsComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.Problem;

/**
 * Print all Client Settings.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ClientSettingsReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = -8850091501021448900L;

    private IContest contest;

    private IController controller;

    private Log log;

    /**
     * Return a list of comma delimited problem names.
     * 
     * <P>
     * returns "none selected" if no problems in filter <br>
     * returns "none active selected" if problems in the filter are all deactivated <br>
     * 
     * @param filter
     * @return
     */
    private String getProblemlist(Filter filter) {
        ElementId[] elementIds = filter.getProblemIdList();

        if (elementIds.length == 0) {
            return "(none selected)";
        }

        StringBuffer stringBuffer = new StringBuffer();
        for (Problem problem : contest.getProblems()) {
            for (ElementId elementId : elementIds) {
                if (problem.getElementId().equals(elementId)) {
                    stringBuffer.append(problem.getDisplayName());
                    stringBuffer.append(", ");
                }
            }
        }

        if (stringBuffer.length() > 0) {
            // stringBuffer.length() - 2 used to strip off trailing ", "
            return new String(stringBuffer).substring(0, stringBuffer.length() - 2);
        } else {
            return "(none active selected)";
        }
    }

    private String enabledString(boolean b) {
        if (b) {
            return "is ENABLED";
        } else {
            return "is not enabled";
        }
    }

    private void writeRow(PrintWriter printWriter, ClientSettings clientSettings) {

        printWriter.print(clientSettings.getClientId());

        if (isJudge(clientSettings.getClientId())) {
            printWriter.println(" auto judge " + enabledString(clientSettings.isAutoJudging()));
            Filter filter = clientSettings.getAutoJudgeFilter();
            if (filter != null) {
                ElementId[] elementIds = filter.getProblemIdList();
                printWriter.println("     Auto judging " + elementIds.length + " problems : " + getProblemlist(filter));
            }

        } else {
            printWriter.println();
        }

        String[] keys = clientSettings.getKeys();
        Arrays.sort(keys);

        for (String key : keys) {
            printWriter.println("   " + key + "='" + clientSettings.getProperty(key) + "'");
        }
    }

    private boolean isJudge(ClientId id) {
        return id != null && id.getClientType().equals(ClientType.Type.JUDGE);
    }

    private void writeReport(PrintWriter printWriter) {
        // ClientSettings
        printWriter.println();
        ClientSettings[] clientSettings = contest.getClientSettingsList();

        Arrays.sort(clientSettings, new ClientSettingsComparator());

        printWriter.println("-- " + clientSettings.length + " client settings --");
        for (ClientSettings clientSettings2 : clientSettings) {
            writeRow(printWriter, clientSettings2);
        }
    }

    private void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + new Date());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
    }

    private void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report");
    }

    public void createReportFile(String filename, Filter filter) throws IOException {

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

    public String[] createReport(Filter filter) {
        throw new SecurityException("Not implemented");
    }

    public String createReportXML(Filter filter) {
        throw new SecurityException("Not implemented");
    }

    public String getReportTitle() {
        return "Client Settings";
    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Client Settings Report";
    }

}
