package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * Print All Login and Connection Information.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class LoginReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = 8813054233796013087L;

    private IContest contest;

    private IController controller;

    private Log log;

    private void writeReport(PrintWriter printWriter) {

        // Local Logins
        printWriter.println();
        printWriter.println("-- Local Logins -- ");
        for (ClientType.Type ctype : ClientType.Type.values()) {

            ClientId[] clientIds = contest.getLocalLoggedInClients(ctype);
            if (clientIds.length > 0) {

                printWriter.println("Logged in " + ctype.toString());

                for (ClientId clientId : clientIds) {
                    ConnectionHandlerID connectionHandlerID = contest.getConnectionHandleID(clientId);
                    printWriter.println("   " + clientId + " on " + connectionHandlerID);
                }
            }
        }

        // Remote Logins
        printWriter.println();
        printWriter.println("-- Remote Logins -- ");
        for (ClientType.Type ctype : ClientType.Type.values()) {

            ClientId[] clientIds = contest.getRemoteLoggedInClients(ctype);
            if (clientIds.length > 0) {

                printWriter.println("Logged in " + ctype.toString());

                for (ClientId clientId : clientIds) {
                    ConnectionHandlerID connectionHandlerID = contest.getConnectionHandleID(clientId);
                    printWriter.println("   " + clientId + " on " + connectionHandlerID);
                }
            }
        }

    }

    public void printConnections(PrintWriter printWriter) {

        // Connections
        printWriter.println();
        ConnectionHandlerID[] connectionHandlerIDs = contest.getConnectionHandleIDs();
        // Arrays.sort(connectionHandlerIDs, new ConnectionHanlderIDComparator());
        printWriter.println("-- " + connectionHandlerIDs.length + " Connections --");
        for (ConnectionHandlerID connectionHandlerID : connectionHandlerIDs) {
            printWriter.println("  " + connectionHandlerID);
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
        return "Logins";
    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Logins Report";
    }

}
