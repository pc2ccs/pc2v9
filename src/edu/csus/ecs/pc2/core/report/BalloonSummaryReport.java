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
import edu.csus.ecs.pc2.core.list.RunComparatorByTeamProblem;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.ClientType.Type;

/**
 * Print Balloon Summary Report.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class BalloonSummaryReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = 3558660040036850297L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter;

    private Problem[] solvedProblems(Run[] runs, ClientId clientId) {

        Vector<Problem> probs = new Vector<Problem>();

        Arrays.sort(runs, new RunComparatorByTeamProblem());

        for (Run run : runs) {
            if (!run.isDeleted() && run.isSolved()) {
                if (run.getSubmitter().equals(clientId)) {

                    if (probs.size() == 0) {
                        probs.addElement(contest.getProblem(run.getProblemId()));

                    } else {
                        // Loop through to learn if problem already solved
                        boolean found = false;
                        for (int i = 0; i < probs.size(); i++) {
                            if (probs.elementAt(i).getElementId().equals(run.getProblemId())) {
                                found = true;
                            }
                        }
                        if (!found) {
                            probs.addElement(contest.getProblem(run.getProblemId()));

                        }
                    }
                }
            }
        }

        return (Problem[]) probs.toArray(new Problem[probs.size()]);
    }

    public void writeReport(PrintWriter printWriter) {

        printWriter.println();

        Run[] runs = contest.getRuns();

        Vector<Account> accountVector = contest.getAccounts(Type.TEAM);
        Account[] accounts = (Account[]) accountVector.toArray(new Account[accountVector.size()]);

        Arrays.sort(accounts, new AccountComparator());

        Problem[] solvedProbs = null;

        int totalBalloons = 0;

        for (Account account : accounts) {

            ClientId clientId = account.getClientId();
            solvedProbs = solvedProblems(runs, clientId);

            BalloonSettings balloonSettings = contest.getBalloonSettings(clientId.getSiteNumber());

            if (solvedProbs.length > 0) {
                printWriter.print("s" + clientId.getSiteNumber() + "t" + clientId.getClientNumber() + " ");
                truncPrint(printWriter, account, 9);
                printWriter.print(" has " + solvedProbs.length + ": ");

                for (Problem problem : solvedProbs) {

                    totalBalloons++;

                    if ((balloonSettings != null) && (balloonSettings.getColor(problem.getElementId()) != null)) {
                        printWriter.print(" " + balloonSettings.getColor(problem.getElementId()));
                    } else {
                        String name = problem.getDisplayName();
                        int truncIndex = Math.min(name.toString().length(), 12);
                        printWriter.format(" " + name.toString().substring(0, truncIndex));
                    }

                }

                printWriter.println();

                // } else {
                // printWriter.print("s" + clientId.getSiteNumber() + "t" + clientId.getClientNumber());
                // printWriter.print(" " + solvedProbs.length + " balloons. ");
                // printWriter.println();
            }
        }
        printWriter.println();
        printWriter.println("Total Balloons " + totalBalloons);

    }

    private void truncPrint(PrintWriter printWriter, String name, int maxlen) {
        int truncIndex = Math.min(name.toString().length(), maxlen);
        printWriter.format(name.toString().substring(0, truncIndex));
    }

    private void truncPrint(PrintWriter printWriter, Account account, int maxlen) {

        if (account != null) {
            if (account.getDisplayName() == null) {
                truncPrint(printWriter, account.getClientId().getName(), maxlen);
            } else {
                truncPrint(printWriter, account.getDisplayName(), maxlen);
            }
        }

    }

    private void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println(getReportTitle() + " Report");
    }

    private void printFooter(PrintWriter printWriter) {
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
            printWriter.println("Exception generating report " + e.getMessage());
        }
    }

    public String[] createReport(Filter inFilter) {
        throw new SecurityException("Not implemented");
    }

    public String createReportXML(Filter inFilter) {
        throw new SecurityException("Not implemented");
    }

    public String getReportTitle() {
        return "Balloons Summary";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Balloon Summary Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
