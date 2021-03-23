package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;

/**
 * Problem Group Assignments.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class ProblemGroupAssignmentReport implements IReport {

    private static final String PAD = "    ";

    /**
     * 
     */
    private static final long serialVersionUID = -1889505494967631248L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter;

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    @Override
    public void createReportFile(String filename, Filter filter) throws IOException {

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);

        try {

            try {
                printHeader(printWriter);

                printWriter.println();

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
    
    public static Account[] getAccounts(IInternalContest contest, ClientType.Type type) {
        Vector<Account> accountVector = contest.getAccounts(type);
        Account[] accounts = (Account[]) accountVector.toArray(new Account[accountVector.size()]);
        return accounts;
    }

    private void writeGroupSummary(PrintWriter printWriter) {

        int groupCount = contest.getGroups().length;

        printWriter.println("--- " + groupCount + " groups ---");
        printWriter.println();

        Group[] groups = contest.getGroups();

        // print groups by group Id
        Arrays.sort(groups, new GroupComparatorById());
        
        int longestGroupName = 0;
        for (Group group : groups) {
            if (group.getDisplayName().length() > longestGroupName) {
                longestGroupName = group.getDisplayName().length();
            }
        }

        for (Group group : groups) {
            String paddedName = String.format("%-"+longestGroupName+"s", group.getDisplayName());
            String paddedGroupId = String.format("%-8d", group.getGroupId());
            printWriter.println(paddedGroupId + " - " + paddedName + " ; assigned to " + //
                    getGroupProblemCount(group) + " problems " + getGroupProblemLetters(group) + "");
        }

        Account[] teamAccounts = getAccounts(contest, Type.TEAM);
        printWriter.println();
        printWriter.println("--- " + teamAccounts.length + " teams ---");
        printWriter.println();
        
        
//        for (Account account : teamAccounts) {
//            printWriter.println("Account "+account +" " +account.getGroupId() + " "+contest.getGroup(account.getGroupId()).getDisplayName());
//        }
//        
        for (Group group : groups) {

            int teamsInGroupCount = 0;
            for (Account account : teamAccounts) {
                if (account.getGroupId() != null && group.getElementId().equals(account.getGroupId())){
                    teamsInGroupCount++;
                }
//                printWriter.println("Group "+group.getGroupId()+" vs "  +account.getGroupId() + " aka "+contest.getGroup(account.getGroupId()).getDisplayName());
                
            }

            String paddedName = String.format("%-" + longestGroupName + "s", group.getDisplayName());
            String paddedGroupId = String.format("%-8d", group.getGroupId());
            printWriter.println(paddedGroupId + " - " + paddedName + " ; number of teams " + teamsInGroupCount);
        }

    }

    private List<Problem> getGroupProblems(Group group) {

        List<Problem> list = new ArrayList<Problem>();

        Problem[] problems = contest.getProblems();
        for (Problem problem : problems) {
            if (problem.getGroups().contains(group)) {
                list.add(problem);
            }
        }
        return list;
    }

    private int getGroupProblemCount(Group group) {
        List<Problem> problems = getGroupProblems(group);
        return problems.size();
    }

    private List<String> getGroupProblemLetters(Group group) {

        List<String> list = new ArrayList<String>();
        List<Problem> problems = getGroupProblems(group);
        for (Problem problem : problems) {
            list.add(problem.getLetter());
        }
        return list;
    }

    @Override
    public String[] createReport(Filter filter) {
        throw new SecurityException("Not implemented");
    }

    @Override
    public String createReportXML(Filter filter) throws IOException {
        return Reports.notImplementedXML(this);
    }

    @Override
    public void writeReport(PrintWriter printWriter) throws Exception {

        writeGroupSummary(printWriter);

        // Problem
        printWriter.println();
        printWriter.println("-- " + contest.getProblems().length + " problems --");

        for (Problem problem : contest.getProblems()) {
            printWriter.println();
            writeRow(printWriter, problem);
        }
    }

    public void writeRow(PrintWriter printWriter, Problem problem) {

        printWriter.println(problem.getLetter() + " " + problem.getDisplayName());
        printWriter.println(PAD + problem.getGroups().size() + " groups: " + problem.getGroups());
    }

    @Override
    public String getReportTitle() {
        return "Problem Group Assignment Report";
    }

    private void writeContestTime(PrintWriter printWriter) {
        printWriter.println();
        GregorianCalendar resumeTime = contest.getContestTime().getResumeTime();
        if (resumeTime == null) {
            printWriter.println("Contest date/time: never started");
        } else {
            printWriter.println("Contest date/time: " + resumeTime.getTime());
        }
    }

    @Override
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

    @Override
    public String getPluginTitle() {
        return "Problem Group Assignment Report";
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}