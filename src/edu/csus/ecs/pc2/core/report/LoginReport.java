package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
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

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter;

    public void writeReport(PrintWriter printWriter) {

        // Local Logins
        printWriter.println();
        printWriter.println("-- Local Logins -- ");
        for (ClientType.Type ctype : ClientType.Type.values()) {

            ClientId[] clientIds = contest.getLocalLoggedInClients(ctype);
            if (clientIds.length > 0) {

                printWriter.println("Logged in " + ctype.toString());

                for (ClientId clientId : clientIds) {
                    try {
                        ConnectionHandlerID connectionHandlerID = contest.getConnectionHandleID(clientId);
                        printWriter.println("   " + clientId + " on " + connectionHandlerID);
                    } catch (Exception e) {
                        printWriter.println("Exception in report: " + e.getMessage());
                        e.printStackTrace(printWriter);
                    }
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
                    try {
                        ConnectionHandlerID connectionHandlerID = contest.getConnectionHandleID(clientId);
                        printWriter.println("   " + clientId + " on " + connectionHandlerID);
                    } catch (Exception e) {
                        printWriter.println("Exception in report: " + e.getMessage());
                        e.printStackTrace(printWriter);
                    }
                }
            }
        }

        printWriter.println();
        printWriter.println("-- Connection Ids  -- ");

        for (ConnectionHandlerID connectionHandlerID : contest.getConnectionHandleIDs()) {

            try {
                ClientId clientId = contest.getClientId(connectionHandlerID);
                if (clientId != null) {
                    printWriter.print(" client " + clientId);
                }
                printWriter.print("   " + connectionHandlerID);
                printWriter.println();
            } catch (Exception e) {
                printWriter.println("Exception in report: " + e.getMessage());
                e.printStackTrace(printWriter);
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
        return "Logins";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Logins Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
