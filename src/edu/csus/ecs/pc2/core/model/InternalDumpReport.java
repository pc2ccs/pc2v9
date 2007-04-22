package edu.csus.ecs.pc2.core.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.ClarificationComparator;
import edu.csus.ecs.pc2.core.list.ContestTimeComparator;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.list.SiteComparatorBySiteNumber;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * Internal dump report.
 * @author pc2@ecs.csus.edu
 *
 */

// $HeadURL$
public class InternalDumpReport implements IReport {

    private IModel model;
    private IController controller;


    private void writeReport(PrintWriter printWriter) {

            Vector<Account> allAccounts = new Vector<Account>();

            printWriter.println();
            printWriter.println("-- Accounts --");
            for (ClientType.Type ctype : ClientType.Type.values()) {
                if (model.getAccounts(ctype).size() > 0) {
                    printWriter.println("Accounts " + ctype.toString() + " there are " + model.getAccounts(ctype).size());
                    Vector<Account> accounts = model.getAccounts(ctype);
                    allAccounts.addAll(accounts);
                    for (int i = 0; i < accounts.size(); i++) {
                        Account account = accounts.elementAt(i);
                        printWriter.println("   " + account + " Site " + account.getClientId().getSiteNumber() + " id=" + account.getElementId());
                    }
                }
            }

            Account[] accountList = (Account[]) allAccounts.toArray(new Account[allAccounts.size()]);
            Arrays.sort(accountList, new AccountComparator());
            
            printWriter.println();
            printWriter.println("-- "+accountList.length+" Accounts --");
            for (int i = 0; i < accountList.length; i++) {
                Account account = accountList[i];
                printWriter.println("   " + account + " Site " + account.getClientId().getSiteNumber() + " id=" + account.getElementId());
            }


            // Sites
            printWriter.println();
            printWriter.println("-- " + model.getSites().length + " sites --");
            Site[] sites = model.getSites();
            Arrays.sort(sites, new SiteComparatorBySiteNumber());
            for (Site site1 : sites) {
                String hostName = site1.getConnectionInfo().getProperty(Site.IP_KEY);
                String portStr = site1.getConnectionInfo().getProperty(Site.PORT_KEY);

                printWriter.println("Site " + site1.getSiteNumber() + " " + hostName + ":" + portStr + " " + site1.getDisplayName() + "/" + site1.getPassword() + " id=" + site1.getElementId());
            }

            // Problem
            printWriter.println();
            printWriter.println("-- " + model.getProblems().length + " problems --");
            for (Problem problem : model.getProblems()) {
                printWriter.println("  Problem " + problem + " id=" + problem.getElementId());
            }

            // Language
            printWriter.println();
            printWriter.println("-- " + model.getLanguages().length + " languages --");
            for (Language language : model.getLanguages()) {
                printWriter.println("  Language " + language + " id=" + language.getElementId());
            }

            // Runs
            printWriter.println();
            Run[] runs = model.getRuns();
            Arrays.sort(runs, new RunComparator());
            printWriter.println("-- " + runs.length + " runs --");
            for (Run run : runs) {
                printWriter.println("  Run " + run);
            }

            // Clarifications
            printWriter.println();
            Clarification[] clarifications = model.getClarifications();
            Arrays.sort(clarifications, new ClarificationComparator());
            printWriter.println("-- " + clarifications.length + " clarifications --");
            for (Clarification clarification : clarifications) {
                printWriter.println("  " + clarification);
            }

            // Contest Times
            printWriter.println();
            ContestTime[] contestTimes = model.getContestTimes();
            Arrays.sort(contestTimes, new ContestTimeComparator());
            printWriter.println("-- " + contestTimes.length + " Contest Times --");
            for (ContestTime contestTime : contestTimes) {

                if (model.getSiteNumber() == contestTime.getSiteNumber()) {
                    printWriter.print("  * ");
                } else {
                    printWriter.print("    ");
                }
                String state = "STOPPED";
                if (contestTime.isContestRunning()) {
                    state = "STARTED";
                }

                printWriter.println("  Site " + contestTime.getSiteNumber() + " " + state + " " + contestTime.getElapsedTimeStr() + " " + contestTime.getRemainingTimeStr() + " "
                        + contestTime.getContestLengthStr());
            }

            // Logins
            printWriter.println();
            printWriter.println("-- Logins -- ");
            for (ClientType.Type ctype : ClientType.Type.values()) {

                Enumeration<ClientId> enumeration = model.getLoggedInClients(ctype);
                if (model.getLoggedInClients(ctype).hasMoreElements()) {
                    printWriter.println("Logged in " + ctype.toString());
                    while (enumeration.hasMoreElements()) {
                        ClientId aClientId = (ClientId) enumeration.nextElement();
                        ConnectionHandlerID connectionHandlerID = model.getConnectionHandleID(aClientId);
                        printWriter.println("   " + aClientId + " on " + connectionHandlerID);
                    }
                }
            }

            // Connections
            printWriter.println();
            ConnectionHandlerID[] connectionHandlerIDs = model.getConnectionHandleIDs();
            // Arrays.sort(connectionHandlerIDs, new ConnectionHanlderIDComparator());
            printWriter.println("-- " + connectionHandlerIDs.length + " Connections --");
            for (ConnectionHandlerID connectionHandlerID : connectionHandlerIDs) {
                printWriter.println("  " + connectionHandlerID);
            }

            printWriter.println();
            printWriter.println("*end*");

            printWriter.close();
            printWriter = null;
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

        printHeader(printWriter);

        writeReport(printWriter);

        printFooter(printWriter);

        printWriter.close();
        printWriter = null;
    }

    public String[] createReport(Filter filter) {
        throw new SecurityException("Not implemented");
    }

    public String createReportXML(Filter filter) {
        throw new SecurityException("Not implemented");
    }

    public String getReportTitle() {
        return "Internal Dump";
    }
    public void setModelAndController(IModel inModel, IController inController) {
        this.model = inModel;
        this.controller = inController;
    }

    public String getPluginTitle() {
        return "Internal Dump Report";
    }

}
