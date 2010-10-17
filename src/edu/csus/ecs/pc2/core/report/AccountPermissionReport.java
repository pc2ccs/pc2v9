package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Vector;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.security.Permission;

/**
 * List all permissions for clients and servers.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$

public class AccountPermissionReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = 240824146830266565L;

    private IInternalContest contest;

    @SuppressWarnings("unused")
    private IInternalController controller;

    private Filter filter = new Filter();
    
    public void createReportFile(String filename, Filter inFilter) throws IOException {

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);
        filter = inFilter;

        printHeader(printWriter);

        writeReport(printWriter);

        printFooter(printWriter);

        printWriter.close();
        printWriter = null;

    }

    public void writeReport(PrintWriter printWriter) {

        Account[] accounts = getAllAccounts();
        Arrays.sort(accounts, new AccountComparator());
        
        // Used for control break
        Type previousClientType = Type.UNKNOWN;

        for (Account account : accounts) {

            if (filter.matches(account)) {

                Type clientType = account.getClientId().getClientType();
                if (!previousClientType.equals(clientType)) {
                    printWriter.println();
                    printWriter.println("Type: " + clientType.toString());
                }
                printPermissions(printWriter, account);
            }
        }

    }
    
    /**
     * Print permission for input account.
     * @param printWriter write to this writer
     * @param account account permissions to be listed
     */
    public static final void printPermissions(PrintWriter printWriter, Account account) {
        Permission.Type[] types = Permission.Type.values();
        Permission permission = new Permission();
        
        printWriter.println("  " + account.getClientId().getName() + " (site " + account.getSiteNumber() + ") ");
        int count = 1;
        for (Permission.Type type : types) {
            if (account.isAllowed(type)) {
                printWriter.println("    " +count +" " + type + " " + permission.getDescription(type));
                count++;
            }
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

    public String[] createReport(Filter inFilter) {
        throw new SecurityException("Not implemented");
    }

    public String createReportXML(Filter inFilter) {
        throw new SecurityException("Not implemented");
    }

    public String getReportTitle() {
        return "Account Permissions Report";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;

    }

    public String getPluginTitle() {
        return "Permissions Report";
    }

    /**
     * Return all accounts for all sites.
     * 
     * @return Array of all accounts in contest.
     */
    private Account[] getAllAccounts() {

        Vector<Account> allAccounts = new Vector<Account>();

        for (ClientType.Type ctype : ClientType.Type.values()) {

            // only add account if it is not ALL which would
            // cause dups in the report.

            if (!ClientType.Type.ALL.equals(ctype)) {
                if (contest.getAccounts(ctype).size() > 0) {
                    Vector<Account> accounts = contest.getAccounts(ctype);
                    allAccounts.addAll(accounts);
                }
            }
        }

        Account[] accountList = (Account[]) allAccounts.toArray(new Account[allAccounts.size()]);
        return accountList;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
