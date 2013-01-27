package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * accounts.tsv report/file output.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class AccountsTSVReport implements IReportFile {

    /**
     * 
     */
    private static final long serialVersionUID = -2573139102854929516L;

    private static final String DELIMITER = "\t";

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter = new Filter();

    private void writeRow(PrintWriter printWriter, Account account) {
        printWriter.println(createRow(account));
    }

    public void writeReport(PrintWriter printWriter) {

        // Accounts
        printWriter.println();
        Account[] accounts = contest.getAccounts();
        Arrays.sort(accounts, new AccountComparator());

        if (filter.isFilterOn()) {
            // printWriter.println("Filter: " + filter.toString());

            int count = 0;
            for (Account account : accounts) {
                if (filter.matches(account)) {
                    count++;
                }
            }

            if (count > 0) {
                // printWriter.println("-- " + count + " of " + accounts.length +
                // " accounts (filtered) --");
                for (Account account : accounts) {
                    if (filter.matches(account)) {
                        try {
                            writeRow(printWriter, account);
                        } catch (Exception e) {
                            printWriter.println("Exception in report: " + e.getMessage());
                            e.printStackTrace(printWriter);
                        }
                    }
                }
            }

        } else {
            // printWriter.println("-- " + accounts.length + " accounts --");
            for (Account account : accounts) {
                try {
                    writeRow(printWriter, account);
                } catch (Exception e) {
                    printWriter.println("Exception in report: " + e.getMessage());
                    e.printStackTrace(printWriter);
                }
            }
        }
    }

    public void printHeader(PrintWriter printWriter) {
        // printWriter.println(new VersionInfo().getSystemName());
        // printWriter.println("Date: " + Utilities.getL10nDateTime());
        // printWriter.println(new VersionInfo().getSystemVersionInfo());
        // printWriter.println();
        // printWriter.println(getReportTitle() + " Report");

        printWriter.print("accounts");
        printWriter.print(DELIMITER);
        printWriter.print("1");
        printWriter.print(DELIMITER);
        printWriter.println();

    }

    public void printFooter(PrintWriter printWriter) {
        // printWriter.println();
        // printWriter.println("end report");
    }

    public void createReportFile(String filename, Filter inFilter) throws IOException {

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);

        try {
            try {
                printHeader(printWriter);

                writeReport(printWriter);

                printFooter(printWriter);

                printWriter.close();
            } catch (Exception e) {
                printWriter.println("Exception in report: " + e.getMessage());
                e.printStackTrace(printWriter);
            }

            printWriter = null;

        } catch (Exception e) {
            log.log(Log.INFO, "Exception writing report", e);
        }
    }

    public String[] createReport(Filter inFilter) {
        
        if (inFilter != null){
            filter = inFilter;
        }
        
        
        StringBuffer buf = new StringBuffer();
        ArrayList<String> list = new ArrayList<String>();
        
        buf.append("accounts");
        buf.append(DELIMITER);
        buf.append("1");
        buf.append(DELIMITER);

        list.add(buf.toString());
        
        buf = new StringBuffer();

        // Accounts
        Account[] accounts = contest.getAccounts();
        Arrays.sort(accounts, new AccountComparator());

        for (Account account : accounts) {
            if (filter.matches(account)) {
                    list.add(createRow(account));
            }
        }
        
        return (String[]) list.toArray(new String[list.size()]);
    }
    

    private String createRow(Account account) {
        
        // from CCS spec and http://pc2.ecs.csus.edu/wiki/Accounts.tsv
        // Field Description Example Type
        // 1 Label accounts fixed string (always same value)
        // 2 Version number 1 integer
        //
        // Then follow several lines with the following format (one per account).
        // Field Description Example Type
        // 1 Account Type judge string
        // 2 Account Number 42 integer
        // 3 Full Name Per Austrin string
        // 4 Username austrin string
        // 5 Password B!5MWJiy string

        StringBuffer buf = new StringBuffer();
        
        ClientId clientId = account.getClientId();

        // 1 Account Type judge string
        buf.append(clientId.getClientType().toString().toLowerCase());
        buf.append(DELIMITER);

        // 2 Account Number 42 integer
        buf.append(clientId.getClientNumber());
        buf.append(DELIMITER);

        // 3 Full Name Per Austrin string
        buf.append(clientId.getClientNumber());
        buf.append(DELIMITER);

        // 4 Username austrin string
        String alias = account.getAliasName();
        if (alias == null || "".equals(alias)) {
            alias = clientId.getName(); // login name
        }
        buf.append(alias);
        buf.append(DELIMITER);

        // 5 Password B!5MWJiy string
        buf.append(account.getPassword());

        
        return buf.toString();
        
    }

    public String createReportXML(Filter inFilter) {
        throw new SecurityException("Not implemented");
    }

    public String getReportTitle() {
        return "accounts.tsv";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "accounts.tsv Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public boolean suppressHeaderFooter() {
        return true;
    }

}
