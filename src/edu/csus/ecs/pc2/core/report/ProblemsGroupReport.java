package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.GregorianCalendar;
import java.util.Vector;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;

/**
 * Print summary of groups which can view/use problems.
 * 
 * @author pc2@ecs.csus.edu
 */
public class ProblemsGroupReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = 5635989177738081431L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter;

    private Account[] accounts = null;

    private void writeContestTime(PrintWriter printWriter) {
        printWriter.println();
        GregorianCalendar resumeTime = contest.getContestTime().getResumeTime();
        if (resumeTime == null) {
            printWriter.println("Contest date/time: never started");
        } else {
            printWriter.println("Contest date/time: " + resumeTime.getTime());
        }
    }

    private String getYesNo(boolean b) {
        if (b) {
            return "Yes";
        } else {
            return "";
        }
    }

    //todo: pc2 problem group report
    //Add summary report, problem by group
    //_
    //All (N) Problems assigned to All (N) groups
    //_
    //Warning there are N groups with NO problems assigned
    //  1 -
    //  2 -
    //  3 -
    //Warning there are N problems with no groups assigned
    //  Prob
    //  Prob
    //                                              A        B       C
    //ExtId Pac NW Spokane North...   All Probs
    //ExtId Pac NW Spokane North...   N Probs    Yes      Yes     Yes
    //_
    //ExtId (NN teams) Full Group name
    //_
    //A - problem title - All Groups
    //_
    //B - problem title - X of N Groups
    //      Group  1
    //      Group  2
    //      Group  3

    public void writeReport(PrintWriter printWriter) {

        Problem[] problems = contest.getProblems();

        Group[] contestGroups = contest.getGroups();

        if (problems.length == 0) {
            printWriter.println("No problems defined.");
        } else if (contestGroups.length == 0) {
            printWriter.println("No groups defined.");
        }
        else {

            int maxGroupColumnLength = 42;
            printWriter.print(StringUtilities.lpad(' ', maxGroupColumnLength + 21 + 6, "ID     Group Title                                  Problems Viewable"));

            for (Problem problem : problems) {
                printWriter.print(centerString(problem.getLetter(), 8));
            }

            printWriter.println();

            Group[] groups = contest.getGroups();

            for (Group group : groups) {
                

                String title = StringUtilities.trunc(group.getDisplayName(), maxGroupColumnLength);
                title = StringUtilities.rpad(' ', 6, group.getGroupId()) + " " + StringUtilities.rpad(' ', maxGroupColumnLength + 1, title);
                printWriter.print(title);

                int problemsInGroupCount = 0;
                for (Problem problem : problems) {
                    boolean b = problem.canView(group) || problem.isAllView();
                    if (b) {
                        problemsInGroupCount++;
                    }
                }

                boolean allproblems = problemsInGroupCount == problems.length;

                if (allproblems) {
                    printWriter.print(StringUtilities.lpad(' ', 20, "All Problems "));
                } else {
                    printWriter.print(StringUtilities.lpad(' ', 3, problemsInGroupCount) + " of " +
                            StringUtilities.lpad(' ', 3, problems.length)
                            + " problems ");

                    String blankString = StringUtilities.lpad(' ', 8, "");

                    for (Problem problem : problems) {
                        boolean b = problem.canView(group);

                        if (b)
                        {
                            printWriter.print(centerString("" + getYesNo(b), 8));
                        } else {
                            printWriter.print(blankString);
                        }

                    }
                }
                
                printWriter.println();
            }
            

            printWriter.println();

            printProblems(printWriter);

            printWriter.println();

            printGroupsAndViewableProblems(printWriter, groups);
        }

    }

    private void printProblems(PrintWriter printWriter) {
        Problem[] problems = contest.getProblems();

        printWriter.println(problems.length + " problems");
        printWriter.println();

        for (Problem problem : problems) {
            printWriter.println(problem.getLetter() + " - " + problem.getDisplayName());
        }

    }

    private void printGroupsAndViewableProblems(PrintWriter printWriter, Group[] groups) {

        Problem[] problems = contest.getProblems();

        printWriter.println(groups.length + " groups");
        printWriter.println();

        for (Group group : groups) {

            int teamCount = teamCountPerGroup(group);
            String teamsString = Integer.toString(teamCount);
            if (teamCount == 0)
            {
                teamsString = "NO";
            }

            printWriter.println(StringUtilities.rpad(' ', 6, group.getGroupId()) + " " + group.getDisplayName() +
                    " -  " + teamsString + " teams in group");
            for (Problem problem : problems) {
                if (problem.canView(group)) {
                    printWriter.println("    Viewed " + problem.getLetter() + " " + problem.getDisplayName());
                } else
                {
                    printWriter.println("NOT Viewed " + problem.getLetter() + " " + problem.getDisplayName());
                }
            }
            printWriter.println();
        }

    }

    private int teamCountPerGroup(Group group) {
        int teamCount = 0;

        Account[] teams = getTeamAccounts();
        for (Account account : teams) {
            if (account.getGroupId() != null) {
                if (group.getElementId().equals(account.getGroupId())) {
                    teamCount++;
                }
            }
        }

        return teamCount;
    }

    private Account[] getTeamAccounts() {

        if (accounts == null) {
            Type type = Type.TEAM;
            Vector<Account> accountVector = contest.getAccounts(type);
            accounts = (Account[]) accountVector.toArray(new Account[accountVector.size()]);
        }

        return accounts;
    }

    private String centerString(String string, int length) {

        int pad = length - string.length();

        if (pad > 0) {

            int left = pad / 2;
            int right = pad - left;

            return StringUtilities.rpad(' ', left, "") + string + StringUtilities.rpad(' ', right, "");
        }

        return string;

    }

    public void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println(getReportTitle() + " Report");

        writeContestTime(printWriter);
    }

    public void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report");
    }

    public void createReportFile(String filename, Filter inFilter) throws IOException {

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);

        try {

            try {
                printHeader(printWriter);

                writeReport(printWriter);

                printFooter(printWriter);

            } catch (Exception e) {
                printWriter.println("Exception in report: " + e.getMessage());
                e.printStackTrace(printWriter);
            }

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

    public String createReportXML(Filter inFilter) throws IOException {
        return Reports.notImplementedXML(this);
    }

    public String getReportTitle() {
        return "Groups for Problems";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Groups for Problems Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }
}
