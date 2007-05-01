package edu.csus.ecs.pc2.core.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.list.SiteComparatorBySiteNumber;
import edu.csus.ecs.pc2.core.log.Log;

/**
 * Print all languages info.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class AccountsReport implements IReport {

    private IContest contest;

    private IController controller;

    private Log log;

    private void writeSummaryRow(PrintWriter printWriter, int site) {

        int total = 0;
        
        ClientType.Type[] types = ClientType.Type.values();
        for (ClientType.Type type : types) {
            Vector<Account> vector = contest.getAccounts(type, site);
            total += vector.size();
            String countString = vector.size() + "";
            if (vector.size() == 0) {
                countString = "-";
            }
            printWriter.format("%7s", countString);
        }
        printWriter.format("%7s", total);
        printWriter.println();
    }

    private void writeReport(PrintWriter printWriter) {

        Site[] sites = contest.getSites();
        Arrays.sort(sites, new SiteComparatorBySiteNumber());

        printWriter.println();
        ClientType.Type[] types = ClientType.Type.values();
        printWriter.print("Site # ");
        for (ClientType.Type type : types) {
            int truncIndex = Math.min(type.toString().length(), 5);
            printWriter.format("%7s", type.toString().substring(0, truncIndex).toLowerCase());
        }
        printWriter.format("%7s", "total");
        printWriter.println();

        for (Site site : sites) {
            printWriter.print("Site " + site.getSiteNumber());
            writeSummaryRow(printWriter, site.getSiteNumber());
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
        return "Accounts";
    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Accounts Report";
    }

}
