// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Scanner;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit Tests.
 * 
 * @author pc2@ecs.csus.edu
 */
public class BalloonSummaryReportTest extends AbstractTestCase {

    /**
     * 
     * @author ICPC
     *
     */
    protected class ReportTitleComparator implements Comparator<IReport>, Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = -987432712228255571L;

        public int compare(IReport report1, IReport report2) {

            return report1.getReportTitle().compareTo(report2.getReportTitle());
        }
    }

    protected void printReportTitles() {

        IReport[] listOfReports = Reports.getReports();

        Arrays.sort(listOfReports, new ReportTitleComparator());

        for (IReport report : listOfReports) {
            System.out.println(report.getReportTitle());
        }

    }

    public String getFileName(IReport selectedReport) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd.SSS");
        // "yyMMdd HHmmss.SSS");
        String reportName = selectedReport.getReportTitle();

        while (reportName.indexOf(' ') > -1) {
            reportName = reportName.replace(" ", "_");
        }
        return "report." + reportName + "." + simpleDateFormat.format(new Date()) + ".txt";

    }

    /**
     * Create a report based on model/contest.
     * 
     * @param contest
     * @param report
     * @param showReportWindow
     *            - edit/view report file
     * @throws Exception
     */
    public String createAndViewReport(IInternalContest contest, IReport report, boolean showReportWindow) throws Exception {

        IInternalController controller = new SampleContest().createController(contest, true, false);

        String filename = Utilities.createReport(report, contest, controller, true);

        assertFileExists(filename);
        if (showReportWindow) {
            editFile(filename);
        }
        return (filename);
    }

    /**
     * Ensure team's names with % are handled properly
     *
     */
    public void testBalloonSummaryReportProblemName() throws Exception {
        IReport report = new BalloonSummaryReport();
        SampleContest sampleContest = new SampleContest();
        String contestPassword = "Password 101";
        Profile profile = new Profile("Default.");
        IInternalContest contest = sampleContest.createContest(1, 3, 120, 12, true, profile, contestPassword, false);
        IInternalController controller = new SampleContest().createController(contest, true, false);
        Account teamAccount = contest.getAccounts(Type.TEAM).firstElement();
        teamAccount.setDisplayName("100%Winners");
        contest.updateAccount(teamAccount);

        Problem problem = contest.getProblems()[0];
        problem.setDisplayName("0%Winners");
        contest.updateProblem(problem);
        Language language = contest.getLanguages()[0];
        Run run = new Run(teamAccount.getClientId(), language, problem);
        run.setElapsedMins(7);
        String loadFile = getSamplesSourceFilename(SUMIT_SOURCE_FILENAME);
        File loadData = new File(loadFile);
        if (!loadData.exists()) {
            System.err.println("could not find " + loadFile);
            throw new Exception("Unable to locate " + loadFile);
        }
        RunFiles runFiles = new RunFiles(run, loadData.getAbsolutePath());
        contest.addRun(run, runFiles, null);
        ClientId who = contest.getAccounts(Type.JUDGE).firstElement().getClientId();
        SampleContest.checkOutRun(contest, run, who);
        Judgement judgement = contest.getJudgements()[0]; // Judge as Yes
        JudgementRecord judgementRecord = new JudgementRecord(judgement.getElementId(), who, true, false);
        contest.addRunJudgement(run, judgementRecord, null, who);

        String filename = Utilities.getReportFilename(report);
        report.setContestAndController(contest, controller);
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(new FileOutputStream(filename, false), true);
            try {
                report.writeReport(printWriter);
            } catch (Exception e) {
                printWriter.println();
                printWriter.println("Exception in report: " + e.getMessage());
                e.printStackTrace(printWriter);
            }
            report.printFooter(printWriter);
            printWriter.flush();
        } catch (Exception e) {
            e.printStackTrace(printWriter);
        } finally {
            printWriter.close();
            printWriter = null;
        }
        // grumble scanner here was returning 1 line, the name of the file
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();
            while (line != null) {
                if (line.indexOf("Exception in report: ") != -1 || line.indexOf("Exception generating report ") != -1) {
                    System.err.println("Found Exception in " + line);
                    throw new AssertionError("BalloonSummaryReport threw an exception");
                }
                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            reader.close();
        }
        new File(filename).delete();
    }

    /**
     * Ensure team's names with % are handled properly
     *
     */
    public void testBalloonSummaryReport() throws Exception {
        IReport report = new BalloonSummaryReport();
        SampleContest sampleContest = new SampleContest();
        IInternalContest contest = sampleContest.createContest(1, 3, 120, 12, true);
        Account teamAccount = contest.getAccounts(Type.TEAM).firstElement();
        teamAccount.setDisplayName("100%Winners");
        contest.updateAccount(teamAccount);

        Problem problem = contest.getProblems()[0];
        Language language = contest.getLanguages()[0];
        Run run = new Run(teamAccount.getClientId(), language, problem);
        ;
        run.setElapsedMins(7);
        String loadFile = getSamplesSourceFilename(SUMIT_SOURCE_FILENAME);
        File loadData = new File(loadFile);
        if (!loadData.exists()) {
            System.err.println("could not find " + loadFile);
            throw new Exception("Unable to locate " + loadFile);
        }
        RunFiles runFiles = new RunFiles(run, loadData.getAbsolutePath());
        contest.addRun(run, runFiles, null);
        ClientId who = contest.getAccounts(Type.JUDGE).firstElement().getClientId();
        SampleContest.checkOutRun(contest, run, who);
        Judgement judgement = contest.getJudgements()[0]; // Judge as Yes
        JudgementRecord judgementRecord = new JudgementRecord(judgement.getElementId(), who, true, false);
        contest.addRunJudgement(run, judgementRecord, null, who);

        String[] colors = { "Orange", "TweedColor", "TreeColor", "Forest", "FBlack", "SeaFoam" };
        BalloonSettings balloonSettings = new BalloonSettings("BallonSite1", 1);
        balloonSettings.addColorList(contest.getProblems(), colors);
        balloonSettings.setEmailBalloons(false);
        balloonSettings.setPrintBalloons(false);
        balloonSettings.setIncludeNos(false);
        balloonSettings.setLinesPerPage(50);
        balloonSettings.setPostscriptCapable(true);
        contest.addBalloonSettings(balloonSettings);

        String reportResult = createAndViewReport(contest, report, false);
        File reportFile = new File(reportResult);
        Scanner myReader = new Scanner(reportFile);

        try {
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (data.indexOf("Exception in report: ") != -1 || data.indexOf("Exception generating report ") != -1) {
                    System.err.println("Found Exception in " + data);
                    throw new AssertionError("BalloonSummaryReport threw an exception");
                }
            }
        } finally {
            myReader.close();
            reportFile.delete();
        }
    }
}
