package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.security.Permission;

/**
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class AccountPermissionReport implements IReport {

    private IContest contest;

    @SuppressWarnings("unused")
    private IController controller;

    public void createReportFile(String filename, Filter filter) throws IOException {

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);

        printHeader(printWriter);

        writeReport(printWriter);

        printFooter(printWriter);

        printWriter.close();
        printWriter = null;

    }

    private void writeReport(PrintWriter printWriter) {

        printWriter.println();
        printWriter.println("Accounts Permissions Report");

        Account[] accounts = getAllAccounts();
        Arrays.sort(accounts, new AccountComparator());

        Permission.Type[] types = Permission.Type.values();
        Permission permission = new Permission();

        for (Account account : accounts) {
            printWriter.println("  " + account.getClientId().getName() + " (site " + account.getSiteNumber() + ") ");
            int count = 1;
            for (Permission.Type type : types) {
                if (account.isAllowed(type)) {
                    printWriter.println("    " +count +" " + type + " " + permission.getDescription(type));
                    count++;
                }
            }
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

    public String[] createReport(Filter filter) {
        throw new SecurityException("Not implemented");
    }

    public String createReportXML(Filter filter) {
        throw new SecurityException("Not implemented");
    }

    public String getReportTitle() {
        return "Account Permissions Report";
    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;

    }

    public String getPluginTitle() {
        return "Permissions Report";
    }

    /**
     * Return all accounts for all sites.
     * 
     * @return Array of all accounts incontest.
     */
    private Account[] getAllAccounts() {

        Vector<Account> allAccounts = new Vector<Account>();

        for (ClientType.Type ctype : ClientType.Type.values()) {
            if (contest.getAccounts(ctype).size() > 0) {
                Vector<Account> accounts = contest.getAccounts(ctype);
                allAccounts.addAll(accounts);
            }
        }

        Account[] accountList = (Account[]) allAccounts.toArray(new Account[allAccounts.size()]);
        return accountList;
    }

}
