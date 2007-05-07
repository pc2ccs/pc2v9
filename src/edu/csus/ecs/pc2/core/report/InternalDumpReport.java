package edu.csus.ecs.pc2.core.report;

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
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * Internal dump report.
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class InternalDumpReport implements IReport {

    private IContest contest;

    private IController controller;

    private Log log;

    private Filter accountFilter = new Filter();

    private void printAccounts(PrintWriter printWriter) {

        printWriter.println();
        printWriter.println("-- Accounts --");
        for (ClientType.Type ctype : ClientType.Type.values()) {

            Vector<Account> accounts;

            if (accountFilter.isThisSiteOnly()) {
                accounts = contest.getAccounts(ctype, accountFilter.getSiteNumber());
            } else {
                accounts = contest.getAccounts(ctype);
            }

            if (accounts.size() > 0) {
                printWriter.print("Accounts " + ctype.toString() + " there are " + accounts.size());
                if (accountFilter.isThisSiteOnly()) {
                    printWriter.print(" for site " + accountFilter.getSiteNumber());
                }
                printWriter.println();


                for (int i = 0; i < accounts.size(); i++) {
                    Account account = accounts.elementAt(i);
                    printWriter.print("   Site " + account.getSiteNumber());
                    printWriter.format(" %-15s", account.getClientId().getName());
                    printWriter.println(" id=" + account.getElementId());
                }
            }
        }

        Vector<Account> allAccounts = contest.getAccounts(ClientType.Type.ALL);
        if (accountFilter.isThisSiteOnly()) {
            contest.getAccounts(ClientType.Type.ALL, accountFilter.getSiteNumber());
        } 

        Account[] accountList = (Account[]) allAccounts.toArray(new Account[allAccounts.size()]);
        Arrays.sort(accountList, new AccountComparator());

        printWriter.println();
        printWriter.println("-- " + accountList.length + " Accounts --");
        for (int i = 0; i < accountList.length; i++) {
            Account account = accountList[i];
            printWriter.print("   Site " + account.getSiteNumber());
            printWriter.format(" %-15s", account.getClientId().getName());
            printWriter.println(" id=" + account.getElementId());
        }
    }

    private void writeReport(PrintWriter printWriter) {

        ContestTime localContestTime = contest.getContestTime();

        printWriter.println();
        if (localContestTime != null) {
            if (localContestTime.isContestRunning()) {
                printWriter.print("Contest is RUNNING");
            } else {
                printWriter.print("Contest is STOPPED");
            }

            printWriter.print(" elapsed = " + localContestTime.getElapsedTimeStr());
            printWriter.print(" remaining = " + localContestTime.getRemainingTimeStr());
            printWriter.print(" length = " + localContestTime.getContestLengthStr());
            printWriter.println();
        } else {
            printWriter.println("Contest Time is undefined (null)");

        }

        // Sites
        printWriter.println();
        printWriter.println("-- " + contest.getSites().length + " sites --");
        Site[] sites = contest.getSites();
        Arrays.sort(sites, new SiteComparatorBySiteNumber());
        for (Site site1 : sites) {
            String hostName = site1.getConnectionInfo().getProperty(Site.IP_KEY);
            String portStr = site1.getConnectionInfo().getProperty(Site.PORT_KEY);

            printWriter.println("Site " + site1.getSiteNumber() + " " + hostName + ":" + portStr + " '" + site1.getDisplayName() + "' " 
                    + " password='" + site1.getPassword() + "' id=" + site1.getElementId());
        }
        
        // Contest Times
        printWriter.println();
        ContestTime[] contestTimes = contest.getContestTimes();
        Arrays.sort(contestTimes, new ContestTimeComparator());
        printWriter.println("-- " + contestTimes.length + " Contest Times --");
        for (ContestTime contestTime : contestTimes) {

            if (contest.getSiteNumber() == contestTime.getSiteNumber()) {
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


        // Problem
        printWriter.println();
        printWriter.println("-- " + contest.getProblems().length + " problems --");
        for (Problem problem : contest.getProblems()) {
            printWriter.println("  '" + problem + "' id=" + problem.getElementId());

        }

        // Language
        printWriter.println();
        printWriter.println("-- " + contest.getLanguages().length + " languages --");
        for (Language language : contest.getLanguages()) {
            printWriter.println("  '" + language + "' id=" + language.getElementId());
        }
        
        printJudgements (printWriter);

        printAccounts(printWriter);

        printRuns(printWriter);
        
        printClarifications(printWriter);


        // Logins
        printWriter.println();
        printWriter.println("-- Logins -- ");
        for (ClientType.Type ctype : ClientType.Type.values()) {

            Enumeration<ClientId> enumeration = contest.getLoggedInClients(ctype);
            if (contest.getLoggedInClients(ctype).hasMoreElements()) {
                printWriter.println("Logged in " + ctype.toString());
                while (enumeration.hasMoreElements()) {
                    ClientId aClientId = (ClientId) enumeration.nextElement();
                    ConnectionHandlerID connectionHandlerID = contest.getConnectionHandleID(aClientId);
                    printWriter.println("   " + aClientId + " on " + connectionHandlerID);
                }
            }
        }

        printConnections(printWriter);

    }

    private void printJudgements(PrintWriter printWriter) {
        
        // Judgements
        printWriter.println();
        Judgement [] judgements = contest.getJudgements();
        
        printWriter.println("-- " + judgements.length + " Judgements --");
        for (Judgement judgement : judgements) {
            printWriter.println("  '" + judgement + "' id=" + judgement.getElementId());
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
        VersionInfo versionInfo = new VersionInfo();
        printWriter.println(versionInfo.getSystemName());
        printWriter.println("Date: " + new Date());
        printWriter.println(versionInfo.getSystemVersionInfo());
        printWriter.println("Build " + versionInfo.getBuildNumber());

    }

    private void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report");
    }

    public void createReportFile(String filename, Filter filter) throws IOException {

        accountFilter = filter;

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

        } catch (Exception e) {
            log.log(Log.INFO, "Exception writing report", e);
            printWriter.println("Exception generating report " + e.getMessage());
        }

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

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Internal Dump Report";
    }

    private void printRuns(PrintWriter printWriter) {
        // Runs
        printWriter.println();
        Run[] runs = contest.getRuns();
        Arrays.sort(runs, new RunComparator());

        int count = 0;
        for (Run run : runs) {
            if (accountFilter.matches(run)) {
                count++;
            }
        }

        printWriter.print("-- " + count + " runs --");
        if (accountFilter.isThisSiteOnly()) {
            printWriter.print(" for site " + accountFilter.getSiteNumber());
        }
        printWriter.println();
        if (count > 0) {
            for (Run run : runs) {
                if (accountFilter.matches(run)) {
                    printWriter.println("  " + run);
                }
            }
        }
    }

    private void printClarifications(PrintWriter printWriter) {
        // Clarifications
        printWriter.println();
        Clarification[] clarifications = contest.getClarifications();
        Arrays.sort(clarifications, new ClarificationComparator());

        int count = 0;
        for (Clarification clarification : clarifications) {
            if (accountFilter.matches(clarification)) {
                count++;
            }
        }
        printWriter.print("-- " + clarifications.length + " clarifications --");
        if (accountFilter.isThisSiteOnly()) {
            printWriter.print(" for site " + accountFilter.getSiteNumber());
        }
        printWriter.println();
        for (Clarification clarification : clarifications) {
            if (accountFilter.matches(clarification)) {
                printWriter.println("  " + clarification);
            }
        }
    }

}
