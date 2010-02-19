package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.ClarificationComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Print list of clarifications.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ClarificationsReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = -273649856656225241L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter = new Filter();

    public void writeReport(PrintWriter printWriter) {

        // Clarifications
        printWriter.println();
        Clarification[] clarifications = contest.getClarifications();
        Arrays.sort(clarifications, new ClarificationComparator());

        int count = filter.countClarifications(clarifications);

        if (filter.isFilterOn()) {

            printWriter.println("Filter is: " + filter.toString());
            printWriter.println();
            printWriter.println("-- " + count + " clarifications (filtered) --");

        } else {
            printWriter.println("-- " + clarifications.length + " clarifications --");

        }

        for (Clarification clarification : clarifications) {

            if (filter.matches(clarification)) {

                printWriter.println();
                printWriter.println("  Clarification " + clarification.getNumber() + " (Site " + clarification.getSiteNumber() + ") " + clarification.getElementId());
                printWriter.println("           From  : " + clarification.getSubmitter() + " \"" + getDisplayName(clarification.getSubmitter()) + "\"");
                printWriter.println("         Problem : " + contest.getProblem(clarification.getProblemId()).toString());
                printWriter.println("         Elapsed : " + clarification.getElapsedMins());
                printWriter.print("           State   : " + clarification.getState());
                if (clarification.getWhoJudgedItId() != null) {
                    printWriter.print(" by " + clarification.getWhoJudgedItId());
                }
                if (clarification.getWhoCheckedItOutId() != null) {
                    printWriter.print(" checked out by " + clarification.getWhoCheckedItOutId());
                }
                printWriter.println();
                printWriter.println("         To ALL? : " + clarification.isSendToAll());
                printWriter.println("         Question: " + clarification.getQuestion());
                printWriter.println("         Answer  : " + clarification.getAnswer());
                printWriter.println();
                printWriter.println();
                printWriter.println();
            }

        }
    }

    /**
     * Get display name.
     * 
     * If account not defined will return Account.getName(). 
     * 
     * @param submitter
     * @return a display name
     */
    private String getDisplayName(ClientId submitter) {

        Account account = contest.getAccount(submitter);
        if (account == null) {
            return submitter.getName();
        } else {
            return account.getDisplayName();
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
        }
    }

    public String[] createReport(Filter inFilter) {
        throw new SecurityException("Not implemented");
    }

    public String createReportXML(Filter inFilter) {
        throw new SecurityException("Not implemented");
    }

    public String getReportTitle() {
        return "Clarifications";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Clarifications Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
