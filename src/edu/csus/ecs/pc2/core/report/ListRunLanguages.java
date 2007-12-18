package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Hashtable;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.SummaryCounts;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.ClientIdComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ListRunLanguages implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = 1389857029019327047L;

    private IContest contest;

    private IController controller;

    private Log log;

    private Hashtable<ElementId, Integer> langLookup = new Hashtable<ElementId, Integer>();

    private Filter filter;

    /**
     * List clients in individual comma delimited form.
     * 
     * Prints in the form team#ssite#, ex 9s1 (team 9 site 1)
     * 
     * <pre>
     *    
     *    1s1,2s1,4s1,6s1,8s1,9s1,21s1,22s1,23s1,25s1
     * </pre>
     * 
     * @param printWriter
     * @param clientIds
     */
    protected void printClientsShort(PrintWriter printWriter, ClientId[] clientIds) {
        ClientId clientId = null;
        for (int i = 0; i < clientIds.length - 1; i++) {
            clientId = clientIds[i];
            printWriter.print(clientId.getClientNumber() + "s" + clientId.getSiteNumber() + ",");
        }
        clientId = clientIds[clientIds.length - 1];
        printWriter.print(clientId.getClientNumber() + "s" + clientId.getSiteNumber() + ".");

    }

    /**
     * Print client list with ranges.
     * 
     * Expects sort of clients by site id and client number. <br>
     * Prints client list, if 1 through 10 shows 1-10. Before each site of teams, will prefix with the site number.
     * <P>
     * Example:
     * 
     * <pre>
     *    Site 1 team 3,5-7,9,24,26,28-29,31 Site 2 team 1-3,8-17,19-20,23-24 
     * </pre>
     * 
     * @param printWriter
     * @param clientIds
     */
    protected void printClients(PrintWriter printWriter, ClientId[] clientIds) {

        ClientId clientId = null;

        ClientId lastClientId = clientIds[0];
        printWriter.print("Site " + lastClientId.getSiteNumber() + " team " + lastClientId.getClientNumber());

        boolean inRange = false;

        for (int i = 1; i < clientIds.length; i++) {
            clientId = clientIds[i];

            if (lastClientId.getSiteNumber() != clientId.getSiteNumber()) {
                if (inRange) {
                    printWriter.print(lastClientId.getClientNumber());
                    inRange = false;
                }

                printWriter.print(" Site " + clientId.getSiteNumber() + " team " + clientId.getClientNumber());

            } else if (lastClientId.getSiteNumber() == clientId.getSiteNumber() && lastClientId.getClientNumber() + 1 == clientId.getClientNumber()) {
                if (!inRange) {
                    printWriter.print("-");
                    inRange = true;
                }
            } else {
                if (inRange) {
                    printWriter.print(lastClientId.getClientNumber());
                }
                printWriter.print("," + clientId.getClientNumber());
                inRange = false;
            }
            lastClientId = clientId;
        }

        if (inRange) {
            printWriter.print(clientId.getClientNumber());
        }
    }

    public void writeReport(PrintWriter printWriter) {

        SummaryCounts summaryCounts = new SummaryCounts();

        Language[] languages = contest.getLanguages();

        printWriter.println();

        Run[] runs = contest.getRuns();
        printWriter.println("There are " + runs.length + " runs.");
        printWriter.println();

        if (languages == null || languages.length == 0) {

            printWriter.println("No languages defined, no summary");

            return;
        }

        // Load language order into langLookup

        int idx = 0;
        for (Language language : languages) {
            langLookup.put(language.getElementId(), new Integer(idx));
            idx++;
        }

        int[] numberSolved = new int[languages.length];
        int[] numberAttempted = new int[languages.length];
        int[] didNotSolve = new int[languages.length];
        int numDeleted = 0;

        // Simple accumulation

        int theIndex;

        for (Run run : runs) {
            if (!run.isDeleted()) {
                theIndex = langLookup.get(run.getLanguageId()).intValue();
                if (run.isSolved()) {
                    numberSolved[theIndex]++;
                    summaryCounts.increment(run.getSubmitter(), run.getLanguageId());
                } else {
                    didNotSolve[theIndex]++;
                }
                numberAttempted[theIndex]++;
            } else {
                numDeleted++;
            }
        }

        for (Language language : languages) {
            theIndex = langLookup.get(language.getElementId()).intValue();

            printWriter.println("Language " + language);
            ClientId[] clientIds = summaryCounts.getClients(language.getElementId());
            printWriter.print("      " + clientIds.length + " teams: ");

            Arrays.sort(clientIds, new ClientIdComparator());

            // printClientsShort(printWriter, clientIds);
            printClients(printWriter, clientIds);

            printWriter.println();
            printWriter.println("      " + numberSolved[theIndex] + " submissions solved problems");
            printWriter.println("      " + didNotSolve[theIndex] + " submissions did not solved problems");
            printWriter.println("      " + numberAttempted[theIndex] + " total ");
            printWriter.println();

        }

        if (numDeleted > 0) {
            printWriter.println("There are " + numDeleted + " runs marked deleted");
        }
    }

    private void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println(getReportTitle() + " Report");
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

    private void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report");
    }

    public String[] createReport(Filter arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public String createReportXML(Filter arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getReportTitle() {
        return "Submissions by Language";
    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Submissions by Language Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
