package edu.csus.ecs.pc2.core.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.security.Permission;

/**
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class AccountPermissionReport implements IReport {

    private IModel model;

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

    public void setModelAndController(IModel inModel, IController inController) {
        this.model = inModel;
        this.controller = inController;

    }

    public String getPluginTitle() {
        return "Permissions Report";
    }

    /**
     * Return all accounts for all sites.
     * 
     * @return Array of all accounts in model.
     */
    private Account[] getAllAccounts() {

        Vector<Account> allAccounts = new Vector<Account>();

        for (ClientType.Type ctype : ClientType.Type.values()) {
            if (model.getAccounts(ctype).size() > 0) {
                Vector<Account> accounts = model.getAccounts(ctype);
                allAccounts.addAll(accounts);
            }
        }

        Account[] accountList = (Account[]) allAccounts.toArray(new Account[allAccounts.size()]);
        return accountList;
    }

}
