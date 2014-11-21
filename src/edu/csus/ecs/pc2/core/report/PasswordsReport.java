package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Print passwords for teams.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PasswordsReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = -817826475144454899L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter = null;

    public void writeReport(PrintWriter printWriter) {
        
        // Passwords
        
        String[] passwords = getPasswords();
  
        for (String string : passwords) {
            printWriter.println(string);
        }
    }

    private String[] getPasswords() {

        ArrayList<String> list = new ArrayList<String>();

        Vector<Account> vector = contest.getAccounts(Type.TEAM);
        Account[] accounts = (Account[]) vector.toArray(new Account[vector.size()]);
        Arrays.sort(accounts, new AccountComparator());

        if (filter != null) {

            for (Account account : accounts) {
                if (filter.matches(account)) {
                    ClientId clientId = account.getClientId();
                    if (Type.TEAM.equals(clientId.getClientType())) {
                        list.add(account.getPassword());
                    }
                }
            }

        } else {
            /**
             * Just output team passwords for this site.
             */

            int thisSite = contest.getSiteNumber();

            for (Account account : accounts) {
                ClientId clientId = account.getClientId();
                if (thisSite == clientId.getSiteNumber()) {
                    if (Type.TEAM.equals(clientId.getClientType())) {
                        list.add(account.getPassword());
                    }
                }
            }
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    public void printHeader(PrintWriter printWriter) {
//        printWriter.println(new VersionInfo().getSystemName());
//        printWriter.println("Date: " + Utilities.getL10nDateTime());
//        printWriter.println(new VersionInfo().getSystemVersionInfo());
//        printWriter.println();
//        printWriter.println(getReportTitle() + " Report");
    }

    public void printFooter(PrintWriter printWriter) {
//        printWriter.println();
//        printWriter.println("end report");
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
        return getPasswords();
    }

    public String createReportXML(Filter inFilter) throws IOException {
        return Reports.notImplementedXML(this);
    }

    public String getReportTitle() {
        return "Passwords";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Passwords Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
