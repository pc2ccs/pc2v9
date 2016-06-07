package edu.csus.ecs.pc2.core.report;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.IStorage;
import edu.csus.ecs.pc2.core.export.ExportYAML;
import edu.csus.ecs.pc2.core.list.FileComparator;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.security.FileStorage;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit Test.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class CDPReportTest extends AbstractTestCase {

    // TODO promote to ATC
    public void listFiles(PrintStream out, String prefix, String directory) throws Exception {

        File[] entries = new File(directory).listFiles();
        Arrays.sort(entries, new FileComparator());

        for (File entry : entries) {
            if (entry.isFile()) {
                out.println(prefix + directory + File.separator + entry.getName());
            }
        }

        for (File entry : entries) {
            if (entry.isDirectory()) {
                listFiles(out, prefix, directory + File.separator + entry.getName());
            }
        }
    }

    /**
     * create Finalize data.
     */
    // TODO promote to AbstractTestCase
    private FinalizeData createFinalizeData(int numberGolds, int numberSilvers, int numberBronzes) {
        FinalizeData data = new FinalizeData();

        data.setGoldRank(numberGolds);
        data.setSilverRank(numberSilvers);
        data.setBronzeRank(numberBronzes);

        data.setComment("Finalized by Director of Operations, no, really!");

        // if (debugMode) {
        // System.out.println("  gold rank   : " + data.getGoldRank());
        // System.out.println("  silver rank : " + data.getSilverRank());
        // System.out.println("  bronze rank : " + data.getBronzeRank());
        // System.out.println("  comment     : " + data.getComment());
        // }

        return data;
    }

    public void testCreateCDP() throws Exception {

        // String testDir = getOutputDataDirectory(); // TODO replace
        String testDir = "testout\\CDPReportTest";

        removeDirectory(testDir);
        ensureDirectory(testDir);

        // startExplorer(testDir);
        SampleContest sample = new SampleContest();

        IInternalContest contest = sample.createStandardContest();

        finalizeContest(contest);

        assertEquals("Number of teams", 120, contest.getAccounts(Type.TEAM).size());

        IInternalController controller = sample.createController(contest, true, false);

        Run run = sample.createRandomJudgedRunSolved(contest);
        contest.acceptRun(run, new RunFiles(run, getSamplesSourceFilename(SUMIT_SOURCE_FILENAME)));

        run = sample.createRandomJudgedRunSolved(contest);
        // contest.addRun(run);
        contest.acceptRun(run, new RunFiles(run, getSamplesSourceFilename(SUMIT_SOURCE_FILENAME)));
        run = sample.createRandomJudgedRunSolved(contest);
        // contest.addRun(run);
        contest.acceptRun(run, new RunFiles(run, getSamplesSourceFilename(SUMIT_SOURCE_FILENAME)));

        assertTrue("Expecting a run ", contest.getRuns().length > 0);

        // IReport report = new CDPReport();
        CDPReport report = new CDPReport();
        report.setContestAndController(contest, controller);
        report.setDirectoryName(testDir);

        String reportFileName = testDir + File.separator + "cdreport.txt";
        report.createReportFile(reportFileName, null);

        // listFiles(System.out, "", testDir);

        String[] expectedFiles = { "cdreport.txt",

        "userdata.tsv", "scoreboard.tsv", "groups.tsv", "teams.tsv",

        ExportYAML.CONTEST_FILENAME, ExportYAML.PROBLEM_SET_FILENAME,
                // "system.yaml",

                "s3r1/run.properties",

        };

        for (String filename : expectedFiles) {
            assertFileExists(testDir + File.separator + filename);
        }

        checkRunDirs(testDir, run);

        // editFile(testDir + File.separator + "cdreport.txt");
    }

    // TODO promote to ATC
    public void finalizeContest(IInternalContest contest) {
        FinalizeData data = createFinalizeData(4, 4, 5);
        contest.setFinalizeData(data);
    }

    /**
     * Ensure that there are run files created.
     * 
     * @param testDir
     * @param run
     */
    private void checkRunDirs(String testDir, Run run) {

        String runDirname = CDPReport.getRunDirectory(testDir, run);

        assertDirectoryExists(runDirname);

        String infoFilename = runDirname + File.separator + "run.properties";
        // TODO add ExportYAML.RUN_PROPERTIES_FILENAME
        // String infoFilename = runDirectory + File.separator + ExportYAML.RUN_PROPERTIES_FILENAME;

        assertFileExists(infoFilename);

        File[] files = new File(runDirname).listFiles();

        assertTrue("Expecting 2 or more files", files.length > 1);
    }

    /**
     * Test export site 1.
     * 
     * @throws Exception
     */
    public void testCreateCDPWithStorage1() throws Exception {

        // String testDir = getOutputDataDirectory(); // TODO replace
        String testDir = "testout/testCreateCDPSite1";

        removeDirectory(testDir);
        ensureDirectory(testDir);

        // startExplorer(testDir);
        SampleContest sample = new SampleContest();

        IInternalContest contest = sample.createContest(1, 3, 44, 12, true);
        finalizeContest(contest);

        String contestDBdir = testDir + File.separator + "db";
        ensureDirectory(contestDBdir);

        IStorage storage = new FileStorage(contestDBdir);
        contest.setStorage(storage);
        contest.initializeStartupData(contest.getSiteNumber());
        contest.initializeSubmissions(contest.getSiteNumber());

        assertEquals("Number of teams", 44, contest.getAccounts(Type.TEAM).size());

        IInternalController controller = sample.createController(contest, true, false);

        Run run = sample.createRandomJudgedRunSolved(contest);
        contest.acceptRun(run, new RunFiles(run, getSamplesSourceFilename(SUMIT_SOURCE_FILENAME)));

        int numberRuns = 24;
        for (int i = 0; i < numberRuns - 1; i++) {
            run = sample.createRandomJudgedRunSolved(contest);
            // contest.addRun(run);
            contest.acceptRun(run, new RunFiles(run, getSamplesSourceFilename(SUMIT_SOURCE_FILENAME)));
        }

        /**
         * Check runs were saved.
         */
        Run[] runs = contest.getRuns();
        for (Run run2 : runs) {

            RunFiles files2 = contest.getRunFiles(run2);
            assertNotNull("Expeting filse for run " + run2, files2);
        }

        assertEquals("Expecting runs ", numberRuns, contest.getRuns().length);

        CDPReport report = new CDPReport();
        report.setContestAndController(contest, controller);
        report.setDirectoryName(testDir);

        String reportFileName = testDir + File.separator + "cdreport.txt";
        report.createReportFile(reportFileName, null);

        checkRunDirs(testDir, run);

    }

    /**
     * Test export site 1.
     * 
     * @throws Exception
     */
    public void testCreateCDPSite1() throws Exception {

        // String testDir = getOutputDataDirectory(); // TODO replace
        String testDir = "testout/testCreateCDPSite1";

        removeDirectory(testDir);
        ensureDirectory(testDir);

        // startExplorer(testDir);
        SampleContest sample = new SampleContest();

        IInternalContest contest = sample.createContest(1, 3, 44, 12, true);

        finalizeContest(contest);

        assertEquals("Number of teams", 44, contest.getAccounts(Type.TEAM).size());

        IInternalController controller = sample.createController(contest, true, false);

        Run run = sample.createRandomJudgedRunSolved(contest);
        contest.acceptRun(run, new RunFiles(run, getSamplesSourceFilename(SUMIT_SOURCE_FILENAME)));

        int numberRuns = 24;
        for (int i = 0; i < numberRuns - 1; i++) {
            run = sample.createRandomJudgedRunSolved(contest);
            // contest.addRun(run);
            contest.acceptRun(run, new RunFiles(run, getSamplesSourceFilename(SUMIT_SOURCE_FILENAME)));
        }

        assertEquals("Expecting runs ", numberRuns, contest.getRuns().length);

        // IReport report = new CDPReport();
        CDPReport report = new CDPReport();
        report.setContestAndController(contest, controller);
        report.setDirectoryName(testDir);

        String reportFileName = testDir + File.separator + "cdreport.txt";
        report.createReportFile(reportFileName, null);

        checkRunDirs(testDir, run);

    }
}
