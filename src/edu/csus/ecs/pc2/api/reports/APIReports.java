package edu.csus.ecs.pc2.api.reports;

import java.util.Arrays;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.api.BaseClient;
import edu.csus.ecs.pc2.api.IContestClock;
import edu.csus.ecs.pc2.api.IGroup;
import edu.csus.ecs.pc2.api.IRun;
import edu.csus.ecs.pc2.api.IRunComparator;
import edu.csus.ecs.pc2.api.ITeam;
import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
import edu.csus.ecs.pc2.api.exceptions.NotLoggedInException;
import edu.csus.ecs.pc2.core.model.ContestTime;

/**
 * Print API Info.
 * 
 * @author Douglas A. Lane <laned@ecs.csus.edu>
 */
public class APIReports extends BaseClient {

    protected int toInt(String string, int defaultNumber) {

        try {

            if (string != null && string.length() > 0) {
                return Integer.parseInt(string.trim());
            }
        } catch (Exception e) {
            // ignore, will return default if a parsing error
        }

        return defaultNumber;

    }
    
    public void println(String s) {
        System.out.println(s);
    }
    
    public void print(String s) {
        System.out.print(s);

    }
    
    private void printRuns() {

        if (getContest().getRuns().length == 0) {
            println("No runs in system");
            return;
        }

        IRun[] runs = getContest().getRuns();
        Arrays.sort(runs, new IRunComparator());
        println("There are " + runs.length + " runs.");

        for (IRun run : runs) {

            print("Run " + run.getNumber() + " Site " + run.getSiteNumber());

            print(" @ " + run.getSubmissionTime() + " by " + run.getTeam().getLoginName());
            print(" problem: " + run.getProblem().getName());
            print(" in " + run.getLanguage().getName());

            if (run.isFinalJudged()) {
                print("  Final Judgement: " + run.getJudgementName());
            } else if (run.isPreliminaryJudged()) {
                print("  Preliminary Judgement: " + run.getJudgementName());
            } else {
                print("  Judgement: not judged yet ");
            }

            println();
        }
    }

    private void println() {
        System.out.println();
    }

    @Override
    public void onLoginAction() {
        
        println("Contest title: '" + getContest().getContestTitle() + "'");

        println("Site Name = " + getContest().getSiteName());
        
        printClockInfo();
        
        println("Contacted: host=" + getContest().getServerHostName() + " port=" + getContest().getServerPort());
        
        println();
        
        printRunSummary();
        
        println();
        
        printRuns();
        
        println();
        
//        printClarSummary();
        
//        printTeamSummary();
//        new PrintTeams(), //
        
        printTeamInfo();
        
        println();
    }
    
    private void printRunSummary() {

        print("Run summary: ");

        if (getContest().getRuns().length == 0) {
            println("No runs in system");
            return;
        }

        IRun[] runs = getContest().getRuns();
        Arrays.sort(runs, new IRunComparator());

        int deleted = 0;
        int judged = 0;
        int notjudged = 0;

        for (IRun run : runs) {

            if (run.isDeleted()) {
                deleted++;
            } else if (run.isFinalJudged()) {
                judged++;
            } else {
                notjudged++;
            }

        }

        if (notjudged == 0) {
            println("ALL (" + runs.length + ") runs judged.  " + judged + " judged, " + deleted + " deleted.");

        } else {
            println(notjudged + " of " + runs.length + " to be judged.  " + judged + " judged, " + deleted + " deleted.");
        }

    }

    private void printClockInfo() {
        IContestClock clock = getContest().getContestClock();
        print("Clock:");
        print(" length=" + clock.getContestLengthSecs() + " (" + ContestTime.formatTime(clock.getContestLengthSecs()) + ")");
        print(" remaining=" + clock.getRemainingSecs() + " (" + ContestTime.formatTime(clock.getRemainingSecs()) + ")");
        print(" elapsed=" + clock.getElapsedSecs() + " (" + ContestTime.formatTime(clock.getElapsedSecs()) + ")");
        println();
        
        
    }

    private void printTeamInfo() {
        println("There are " + getContest().getTeams().length + " team ");
        for (ITeam team : getContest().getTeams()) {
            IGroup group = team.getGroup();
            String name = "(no group assigned)";
            if (group != null) {
                name = group.getName();
            }
            println(team.getLoginName() + " title: " + team.getLoginName() + " group: " + name);
        }
        
    }

    // TODO dal Fix APIAbstractTest to no longer require GUI ScrollyFrame, once done rename onLoginActionOrig
//    @Override
    public void onLoginActionOrig() {

        System.out.println(new VersionInfo().getSystemVersionInfo());
        
        if (bcParseArguments.getArgCount() == 0){
            System.err.println("No report number(s) specified");
            System.exit(4);
        }

        APIPrintReports apiPrintReports = new APIPrintReports();
        APIAbstractTest[] list = apiPrintReports.getReportsList();

        String[] reportNumbers = bcParseArguments.getArgList();

        for (String reportNum : reportNumbers) {

            int reportNumber = toInt(reportNum, 0);

            if (reportNumber < 1 || reportNumber > list.length) {
                System.err.println("No such report for: " + reportNum);
            } else {

                APIAbstractTest report = list[reportNumber - 1];
                report.setAPISettings(null, getContest(), getServerConnection());
                System.out.println("API Report: " + report.getTitle());
                report.printTest();
            }

        }
        
        System.exit(0);
    }

    void printReportsList() {
        APIPrintReports apiPrintReports = new APIPrintReports();
        APIAbstractTest[] list = apiPrintReports.getReportsList();
        for (int i = 0; i < list.length; i++) {
            System.out.println(i + 1 + "\t" + list[i].getTitle());
        }
    }

    @Override
    public void printProgramUsageInformation() {

        String[] lines = { // 
        "Usage: APIReports [--help] [-F optionsfile] --login user [--password pass] reportLIst ", //
                "", // 
                "Purpose: Print contest reports/information", // 
                "", // 
                "--help    this message", // 
//                "", // 
//                "reportList - list of report numbers, see below ", // 
                "", // 
        };

        for (String s : lines) {
            System.out.println(s);
        }
        
        // TODO dal Fix APIAbstractTest to no longer require GUI ScrollyFrame, once done enable show all reports
//        System.out.println("##  Title");
//        printReportsList();

        VersionInfo info = new VersionInfo();
        System.out.println(info.getSystemVersionInfo());
    }

    public static void main(String[] args) throws LoginFailureException, NotLoggedInException {
        APIReports reports = new APIReports();
        reports.login(args);
    }

}
