package edu.csus.ecs.pc2.imports.ccs;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import junit.framework.Test;
import junit.framework.TestSuite;
import edu.csus.ecs.pc2.ccs.CCSConstants;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.exception.YamlLoadException;
import edu.csus.ecs.pc2.core.export.ExportYAML;
import edu.csus.ecs.pc2.core.list.AutoJudgeSettingComparator;
import edu.csus.ecs.pc2.core.list.SiteComparatorBySiteNumber;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AutoJudgeSetting;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.PlaybackInfo;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit tests.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: ContestYAMLLoaderTest.java 225 2011-09-02 05:22:43Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/test/edu/csus/ecs/pc2/imports/ccs/ContestYAMLLoaderTest.java $
public class ContestYAMLLoaderTest extends AbstractTestCase {

    private boolean debugFlag = false;

    private ContestYAMLLoader loader;

    public ContestYAMLLoaderTest(String string) {
        super(string);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loader = new ContestYAMLLoader();
    }

    public void testGetTitle() throws IOException {

        String title = loader.getContestTitle(getYamlTestFileName());

        // name: ACM-ICPC World Finals 2011
        assertEquals("Contest title", "ACM-ICPC World Finals 2011", title);
    }

    @SuppressWarnings("unused")
    private void writeRow(PrintStream printWriter, Language language) {

        String deletedText = "";
        if (!language.isActive()) {
            deletedText = " [HIDDEN] ";
        }

        printWriter.println("  Language  '" + language + deletedText + "' v" + language.getElementId().getVersionNumber() + " id=" + language.getElementId());
        printWriter.println("    site number         : " + language.getSiteNumber());
        printWriter.println("    compiler command    : " + language.getCompileCommandLine());
        printWriter.println("    executable mask     : " + language.getExecutableIdentifierMask());
        printWriter.println("    program execute cmd : " + language.getProgramExecuteCommandLine());
    }

    /**
     * 
     * @throws Exception
     */
    public void testLoaderMethods() throws Exception {

        IInternalContest contest = loader.fromYaml(null, new String[0], "NAD");
        assertNotNull(contest);

        String[] contents = Utilities.loadFile(getYamlTestFileName());

        assertFalse("File missing " + getYamlTestFileName(), contents.length == 0);

        contest = loader.fromYaml(null, contents, getDataDirectory());

        assertNotNull(contest);

        // name: ACM-ICPC World Finals 2011
        assertEquals("Contest title", "ACM-ICPC World Finals 2011", contest.getContestInformation().getContestTitle());

        // short-name: ICPC WF 2011
        // start-time: 2011-02-04 01:23Z
        // duration: 5:00:00
        // scoreboard-freeze: 4:00:00

        Language[] languages = loader.getLanguages(contents);

        assertEquals("Number of languages", 3, languages.length);

        assertEquals("Expected language name ", "C++", languages[0].getDisplayName());
        assertEquals("Expected language name ", "C", languages[1].getDisplayName());
        assertEquals("Expected language name ", "Java", languages[2].getDisplayName());

        // for (Language language : languages){
        // writeRow(System.out, language);
        // }

        Problem[] problems = loader.getProblems(contents, ContestYAMLLoader.DEFAULT_TIME_OUT);

        assertEquals("Number of problems", 5, problems.length);
        
        int problemIndex = 0;

        Problem problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "apl", problem.getDisplayName());
        assertEquals("Expected default timeout ", ContestYAMLLoader.DEFAULT_TIME_OUT, problem.getTimeOutInSeconds());
        
        problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "barcodes", problem.getDisplayName());
        
        problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "biobots", problem.getDisplayName());
        
        problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "castles", problem.getDisplayName());
        
        problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "channel", problem.getDisplayName());
        assertEquals("Expected default timeout ", ContestYAMLLoader.DEFAULT_TIME_OUT, problem.getTimeOutInSeconds());
        assertFalse("Expecting not comptuer judged", problem.isComputerJudged());


    }

    private String getYamlTestFileName() {
        return getTestFilename(ExportYAML.CONTEST_FILENAME);
    }
    
    
    // TODO CCS use this
    @SuppressWarnings("unused")
    private String getYamlTestFileName(String dirname) {
        return getTestFilename(dirname + File.separator + ExportYAML.CONTEST_FILENAME);
    }


    public void testLoader() throws Exception {

        IInternalContest contest = loader.fromYaml(null, new String[0], "NAD");
        assertNotNull(contest);

        contest = loader.fromYaml(null, getDataDirectory());

        assertNotNull(contest);

        assertEquals("Contest title", "ACM-ICPC World Finals 2011", contest.getContestInformation().getContestTitle());

        // short-name: ICPC WF 2011
        // start-time: 2011-02-04 01:23Z
        // duration: 5:00:00
        // scoreboard-freeze: 4:00:00

        Language[] languages = contest.getLanguages();

        assertEquals("Number of languages", 3, languages.length);

        assertEquals("Expected language name ", "C++", languages[0].getDisplayName());
        assertEquals("Expected language name ", "C", languages[1].getDisplayName());
        assertEquals("Expected language name ", "Java", languages[2].getDisplayName());

        // for (Language language : languages){
        // writeRow(System.out, language);
        // }

        Problem[] problems = contest.getProblems();

        int problemIndex = 0;

        Problem problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "apl", problem.getDisplayName());
        assertEquals("Expected default timeout for "+problem, 20, problem.getTimeOutInSeconds());
        
        problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "barcodes", problem.getDisplayName());
        
        problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "biobots", problem.getDisplayName());
        
        problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "castles", problem.getDisplayName());
        
        problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "channel", problem.getDisplayName());
        assertEquals("Expected default timeout for "+problem, 20, problem.getTimeOutInSeconds());
        
        assertTrue("Expecting comptuer judged", problem.isComputerJudged());

        assertEquals("Number of problems", 5, problems.length);

        Account[] accounts = contest.getAccounts();
        
        ClientType.Type type = ClientType.Type.TEAM;
        assertEquals("Number of accounts " + type.toString(), 35, getClientCount(contest, type));
        type = ClientType.Type.JUDGE;
        assertEquals("Number of accounts " + type.toString(), 20, getClientCount(contest, type));
        type = ClientType.Type.ADMINISTRATOR;
        assertEquals("Number of accounts " + type.toString(), 0, getClientCount(contest, type));
        type = ClientType.Type.SCOREBOARD;
        assertEquals("Number of accounts " + type.toString(), 0, getClientCount(contest, type));

        assertEquals("Number of accounts", 55, accounts.length);

        checkPermissions (accounts);
    }

    private void checkPermissions(Account[] accounts) {
        for (Account account : accounts) {

            assertEquals("Expecting same permissions for " + account, account.getPermissionList().getList().length, getPermList(account.getClientId().getClientType()).length);
        }

    }

    public void testProblemLoader() throws Exception {

        IInternalContest contest = loader.fromYaml(null, getDataDirectory());
        
        debugPrint("Dir " + getDataDirectory());

        int problemNumber = 0;
        for (Problem problem : contest.getProblems()) {
            debugPrint("Problem "+problemNumber+" - " +problem.getDisplayName() + " cases " + problem.getNumberTestCases());

            ProblemDataFiles problemDataFiles = contest.getProblemDataFile(problem);

            problemNumber ++;
            if (problem.getNumberTestCases() > 1) {
                for (int i = 0; i < problem.getNumberTestCases(); i++) {
                    int testCaseNumber = i + 1;
                    String datafile = problem.getDataFileName(testCaseNumber);
                    String answerfile = problem.getAnswerFileName(testCaseNumber);

                    debugPrint("       Data File name " + testCaseNumber + " : " + datafile);
                    debugPrint("     Answer File name " + testCaseNumber + " : " + answerfile);

                    SerializedFile sdatafile = problemDataFiles.getJudgesDataFiles()[problemNumber - 1];
                    assertNotNull("Should find data file ", sdatafile);
                    assertNotNull("Data file should have contents ", sdatafile.getBuffer());

                    SerializedFile sanswerfile = problemDataFiles.getJudgesDataFiles()[problemNumber - 1];
                    assertNotNull("Should find answer file ", sanswerfile);
                    assertNotNull("Answer file should have contents ", sanswerfile.getBuffer());

                    debugPrint("       Data File name " + testCaseNumber + " : " + datafile+ " size "+sdatafile.getBuffer().length);
                    debugPrint("     Answer File name " + testCaseNumber + " : " + answerfile+ " size "+sanswerfile.getBuffer().length);

                }

                assertNotNull("Expecting judges data file ", problemDataFiles.getJudgesDataFile());
                assertNotNull("Expecting judges answer file ", problemDataFiles.getJudgesAnswerFile());

                assertNotNull("Expecting judges data file non empty ", problemDataFiles.getJudgesDataFile().getBuffer());
                assertNotNull("Expecting judges answer file non empty ", problemDataFiles.getJudgesAnswerFile().getBuffer());

                assertNotNull("Expecting judges data file name ", problem.getDataFileName());
                assertNotNull("Expecting judges answer file name ", problem.getAnswerFileName());

            }
        }

        String[] basenames = { "bozo", "smart", "sumit" };

        Problem testProblem = contest.getProblems()[2];

        int idx = 1;
        for (String name : basenames) {

            assertEquals("name: "+testProblem.getShortName()+ " data in name", name + ".in", testProblem.getDataFileName(idx));
            assertEquals("name: "+testProblem.getShortName()+ "data ans name", name + ".ans", testProblem.getAnswerFileName(idx));

            idx++;
        }
        
        String[] probNames = { "apl", "barcodes", "biobots", "castles", "channel" };
        int[] dataSetCount = { 1, 1, 3, 1, 1 };

        int i = 0;
        for (Problem problem : contest.getProblems()) {
            assertEquals("Expecting same problem names", probNames[i], problem.getDisplayName());
            ProblemDataFiles files = contest.getProblemDataFile(problem);
            assertNotNull("Expected data sets for " + problem.getShortName(), files);
            int dataSets = files.getJudgesDataFiles().length;
            assertEquals("Expecting same number data sets " + problem.getShortName(), dataSetCount[i], dataSets);
            i++;
        }
    }

    private void debugPrint(String string) {
        if (debugFlag){
            System.out.println(string);
        }
    }

    private Object getClientCount(IInternalContest contest, Type type) {
        return contest.getAccounts(type).size();
    }

    @SuppressWarnings("unused")
    private void printProblems(IInternalContest contest) {
        Problem[] problems = contest.getProblems();

        System.out.println("printProblems: " + problems.length + " problems");
        for (Problem problem : problems) {
            System.out.println(problem);
        }

    }

    public void testLoadClarCategories() throws Exception {

        String[] contents = Utilities.loadFile(getYamlTestFileName());

        assertFalse("File missing " + getYamlTestFileName(), contents.length == 0);

        String[] answers = loader.getClarificationCategories(contents);

        assertEquals("Expected clar category ", "General", answers[0]);
        assertEquals("Expected clar category ", "SysOps", answers[1]);
        assertEquals("Expected clar category ", "Operations", answers[2]);

    }

    public void testLoadSites() throws Exception {

        String[] contents = Utilities.loadFile(getYamlTestFileName());

        assertFalse("File missing " + getYamlTestFileName(), contents.length == 0);

        Site[] sites = loader.getSites(contents);
        Arrays.sort(sites, new SiteComparatorBySiteNumber());

        assertEquals("Expected 3 sites", 3, sites.length);

        String[] siteNames = { "Uno Site Arcadia", "Two Turtle Doves Site", "Three Blind Mice Site", };

        int port = 50002;

        for (Site site : sites) {

            // - number: 2
            // name: Two Turtle Doves Site
            // IP: localhost
            // port: 51002

            assertEquals("Expected site " + site.getSiteNumber() + " name ", siteNames[site.getSiteNumber() - 1], site.getDisplayName());
            assertEquals("Expected site " + site.getSiteNumber() + " IP ", "localhost", site.getConnectionInfo().get(Site.IP_KEY));
            assertEquals("Expected site " + site.getSiteNumber() + " port ", Integer.toString(port), site.getConnectionInfo().get(Site.PORT_KEY));
            assertEquals("Expected site " + site.getSiteNumber() + " password ", "site" + site.getSiteNumber(), site.getPassword());
            port += 1000;
        }

    }

    public void testGeneralAnswsers() throws Exception {
        String[] contents = Utilities.loadFile(getYamlTestFileName());

        assertFalse("File missing " + getYamlTestFileName(), contents.length == 0);

        String[] strings = loader.getGeneralAnswers(contents);

        assertEquals("Expected gen. clar answer ", "No comment, read problem statement.", strings[0]);
        assertEquals("Expected gen. clar answer ", "This will be answered during the answers to questions session.", strings[1]);
    }

    public void testgetSectionLines() throws Exception {

        String contestYamlFilename = getYamlTestFileName();

        assertTrue("Test file does not exist " + contestYamlFilename, Utilities.isFileThere(contestYamlFilename));
        String[] contents = Utilities.loadFile(contestYamlFilename);

        assertFalse("File missing " + contestYamlFilename, contents.length == 0);

        String key = ContestYAMLLoader.LANGUAGE_KEY;
        String[] sectionLines = loader.getSectionLines(key, contents);
        assertEquals(key + " lines.", 15, sectionLines.length);

        key = ContestYAMLLoader.DEFAULT_CLARS_KEY;
        sectionLines = loader.getSectionLines(key, contents);
        assertEquals(key + " lines.", 4, sectionLines.length);

        key = ContestYAMLLoader.CLAR_CATEGORIES_KEY;
        sectionLines = loader.getSectionLines(key, contents);
        assertEquals(key + " lines.", 5, sectionLines.length);

        key = ContestYAMLLoader.PROBLEMS_KEY;
        sectionLines = loader.getSectionLines(key, contents);
        assertEquals(key + " lines.", 26, sectionLines.length);

        key = ContestYAMLLoader.ACCOUNTS_KEY;
        sectionLines = loader.getSectionLines(key, contents);
        assertEquals(key + " lines.", 17, sectionLines.length);

        key = ContestYAMLLoader.AUTO_JUDGE_KEY;
        sectionLines = loader.getSectionLines(key, contents);
        assertEquals(key + " lines.", 23, sectionLines.length);

        
        key = ContestYAMLLoader.SITES_KEY;
        sectionLines = loader.getSectionLines(key, contents);
        assertEquals(key + " lines.", 16, sectionLines.length);
        
        key = ContestYAMLLoader.REPLAY_KEY;
        sectionLines = loader.getSectionLines(key, contents);
        assertEquals(key + " lines.", 8, sectionLines.length);
    }


    public void testgetFileNames() throws Exception {

        String[] basenames = { "bozo", "smart", "sumit" };

        String shortName = "sumit";
        String directoryName = getDataDirectory() + File.separator + shortName + File.separator + "data" + File.separator + "secret";

        String[] names = loader.getFileNames(directoryName, ".in");
        assertEquals("Number of .in files ", 3, names.length);

        String[] ansnames = loader.getFileNames(directoryName, ".ans");
        assertEquals("Number of .ans files ", 3, ansnames.length);

        int idx = 0;
        for (String name : basenames) {

            assertEquals(names[idx], name + ".in");
            assertEquals(ansnames[idx], name + ".ans");

            idx++;
        }

    }
    



    
    public void testgetProblemsFromLetters() throws Exception {

        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(1, 1, 12, 22, true);

        Problem[] contestProblems = contest.getProblems();

        Problem[] problems = loader.getProblemsFromLetters(contestProblems, "A");
        assertEquals("Should return one problem ", 1, problems.length);
        assertTrue("First problem should be returned ", problems[0].isSameAs(problems[0]));

        problems = loader.getProblemsFromLetters(contestProblems, "A,B,C");
        assertEquals("Should return three problem ", 3, problems.length);
        for (int i = 0; i < problems.length; i++) {
            assertTrue("Should return problem "+problems[i].getDisplayName(), problems[i].isSameAs(problems[i]));
        }
        
//        int i = 0;
//        for (Problem problem : contestProblems) {
//            System.out.println(SampleContest.getProblemLetter(++i) + " " + problem);
//        }
        
        try {
            problems = loader.getProblemsFromLetters(contestProblems, SampleContest.getProblemLetter(contestProblems.length + 1));
            fail("Should have thrown exception because no problem with letter "+SampleContest.getProblemLetter(contestProblems.length));
        } catch (YamlLoadException e) {
            // Ok, expecting this exception
            assertTrue(true);
        }
        
        try {
            problems = loader.getProblemsFromLetters(contestProblems, SampleContest.getProblemLetter(-1));
            fail("Should have thrown exception no problem with id = -1");
        } catch (YamlLoadException e) {
            // Ok, expecting this exception
            assertTrue(true);
        }
       

    }
    
    public void testgetAutoJudgeSettings() throws Exception {

        String contestYamlFilename = getYamlTestFileName();

        assertTrue("Test file does not exist " + contestYamlFilename, Utilities.isFileThere(contestYamlFilename));
        String[] yamlLines = Utilities.loadFile(contestYamlFilename);

        if (debugFlag) {
            System.out.println("Loading " + contestYamlFilename);
        }

        Problem[] contestProblems = loader.getProblems(yamlLines, 12);

        AutoJudgeSetting[] autoJudgeSettings = loader.getAutoJudgeSettings(yamlLines, contestProblems);
        Arrays.sort(autoJudgeSettings, new AutoJudgeSettingComparator());

        assertEquals("Expecting AutoJudgingSettings ", 7, autoJudgeSettings.length);

        ClientId firstClient = autoJudgeSettings[0].getClientId();
        assertEquals("Expecting Judge  ", "judge1", firstClient.getName());
        assertEquals("Expecting Site ", 1, firstClient.getSiteNumber());

        ClientId lastClient = autoJudgeSettings[autoJudgeSettings.length - 1].getClientId();
        assertEquals("Expecting Judge Number ", "judge7", lastClient.getName());
        assertEquals("Expecting Site ", 2, lastClient.getSiteNumber());

        if (debugFlag) {
            dumpAutoJudgeSettings(contestProblems, autoJudgeSettings);
        }
    }
    
    public void testAutoJudgeSettingsTwo() throws Exception {
        
        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(1, 2, 22, 12, true);
        
        // Test Problem.getLetter 
        
        char letter = 'A';
        for (Problem problem : contest.getProblems()) {
            assertEquals("Problem letter", Character.toString(letter), problem.getLetter());
            letter++;
        }
        
        String[] yamlLines = { //
                "auto-judging:", //
                "  - account: JUDGE", //
                "     site: 1", //
                "     number: 4", //
                "     letters: A, B, C", //
                "     enabled: yes", //
        };
        
        AutoJudgeSetting[] autoJudgeSettings = loader.getAutoJudgeSettings(yamlLines, contest.getProblems());
        
        assertEquals("Auto judge settings count ", 1, autoJudgeSettings.length);

        AutoJudgeSetting setting = autoJudgeSettings[0];
        
        Filter filter = setting.getProblemFilter();
        
        assertEquals("Judge 4 ", 4, setting.getClientId().getClientNumber());
        
        String letterList = SampleContest.getProblemLetters (contest, filter);
        
        assertEquals("Problem letters", "A, B, C", letterList);
    }
  
    
    public void testAutoJudgeSettingsAll() throws Exception {
        
        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(1, 2, 22, 12, true);
        
        String[] yamlLines = { //
                "auto-judging:", //
                "  - account: JUDGE", //
                "     site: 1", //
                "     number: 4", //
                "     letters: all", //
                "     enabled: yes", //
        };

        AutoJudgeSetting[] autoJudgeSettings = loader.getAutoJudgeSettings(yamlLines, contest.getProblems());

        assertEquals("Auto judge settings count ", 1, autoJudgeSettings.length);

        AutoJudgeSetting setting = autoJudgeSettings[0];

        Filter filter = setting.getProblemFilter();

        String letterList = SampleContest.getProblemLetters(contest, filter);

        assertEquals("Problem letters", "A, B, C, D, E, F", letterList);
  
    }
    
    public void testAllJudges() throws Exception {

        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(1, 2, 22, 12, true);

        String[] yamlLines = { //
        "accounts:", //
                "  - account: JUDGE", //
                "      site: 1", //
                "     count: 12", //
                "", //
        "auto-judging:", //
                "  - account: JUDGE", //
                "     site: 1", //
                "     number: all", //
                "     letters: A, C, E", //
                "     enabled: yes", //
        };

        AutoJudgeSetting[] autoJudgeSettings = loader.getAutoJudgeSettings(yamlLines, contest.getProblems());

        int numberJudges = 12; // acccounts from yaml
        
        assertEquals("Auto judge settings count ", numberJudges, autoJudgeSettings.length);
        
        for (AutoJudgeSetting setting : autoJudgeSettings) {
            
            Filter filter = setting.getProblemFilter();
            String letterList = SampleContest.getProblemLetters (contest, filter);
            
            assertEquals("Problem letters", "A, C, E", letterList);
        }

    }


    public void testReplayLoad() throws Exception {
        
        String contestYamlFilename = getYamlTestFileName();

        assertTrue("Test file does not exist " + contestYamlFilename, Utilities.isFileThere(contestYamlFilename));
        String[] yamlLines = Utilities.loadFile(contestYamlFilename);

        if (debugFlag) {
            System.out.println("Loading " + contestYamlFilename);
        }

        PlaybackInfo replay = loader.getReplaySettings(yamlLines);
        
        assertNotNull("Should have replay data", replay);

        assertEquals("Title for replay", "A Playback Name", replay.getDisplayName());
        assertEquals("Min runs to replay", 230, replay.getMinimumPlaybackRecords());
        assertEquals("Title for replay", "loadfile/replay.file.txt", replay.getFilename());
        assertEquals("Wait between events ms", 400, replay.getWaitBetweenEventsMS());
    }

    void dumpAutoJudgeSettings(Problem[] contestProblems, AutoJudgeSetting[] autoJudgeSettings) {

        for (AutoJudgeSetting auto : autoJudgeSettings) {

            System.out.println("-- Auto Judge setting for " + auto.getDisplayName());
            System.out.println("   " + auto.getClientId());
            System.out.print("   Assigned " + auto.getProblemFilter().getProblemIdList().length + " problems. ");
            for (ElementId id : auto.getProblemFilter().getProblemIdList()) {
                int num = 1;
                for (Problem problem : contestProblems) {
                    if (problem.getElementId().equals(id)) {
                        System.out.print(SampleContest.getProblemLetter(num + 1) + " " + problem.getDisplayName() + " ");
                    }
                    num++;
                }
            }
            System.out.println();
            System.out.println();
        }

    }
    
    private String getLatexFilename (String problemShortName) {
        return getDataDirectory() + File.separator + problemShortName + File.separator+ "problem_statement" + File.separator + ContestYAMLLoader.DEFAULT_PROBLEM_LATEX_FILENAME;
    }
    
    public void testLatextProblem() throws Exception {

        String problemName = "channel";

        String filename = getLatexFilename(problemName);

        String problemTitle = loader.getProblemNameFromLaTex(filename);

        assertNotNull("Expecting problem name for " + problemName + " in " + filename, problemTitle);
        assertEquals("Problem name in " + filename, "Channel Island Navigation", problemTitle);
        
        problemName = "castles";
        filename = getLatexFilename(problemName);

        problemTitle = loader.getProblemNameFromLaTex(filename);

        assertNotNull("Expecting problem name for " + problemName + " in " + filename, problemTitle);
        assertEquals("Problem name in " + filename, "Castles in the Sand", problemTitle);

    }
    
//    public void testdoNotLoadExternalFile() throws Exception {
//        
//         String dirname = getDataDirectory(getName());
////        String dirname = getDataDirectory("testValidatorKeys");
////        String dirname = getDataDirectory();
//        Utilities.insureDir(dirname);
//        assertDirectoryExists(dirname);
//        
//        String filename = dirname + File.separator + ContestYAMLLoader.DEFAULT_CONTEST_YAML_FILENAME;
////        System.out.println(filename);
//        assertFileExists(filename);
//        
//        IInternalContest contest;
//        
//        // Load data files
//        contest = loader.fromYaml(null, dirname, true);
//        
//        for (Problem problem : contest.getProblems()) {
//            assertTrue(problem.isUsingExternalDataFiles());
//            assertNotNull(problem.getExternalDataFileLocation());
//            ProblemDataFiles dataFiles = contest.getProblemDataFile(problem);
//            assertEquals("Expecting loaded answer files ", 12, dataFiles.getJudgesAnswerFiles().length);
//            assertEquals("Expecting loaded data files ", 12, dataFiles.getJudgesDataFiles().length);
//        }
//        
//        // Do not load data files
//        contest = loader.fromYaml(null, dirname, false);
//        
//        for (Problem problem : contest.getProblems()) {
//            assertFalse("Expecting false using data files", problem.isUsingExternalDataFiles());
//            assertNotNull(problem.getExternalDataFileLocation());
//            ProblemDataFiles dataFiles = contest.getProblemDataFile(problem);
//            assertEquals("Expecting no loaded answer files ", 0, dataFiles.getJudgesAnswerFiles().length);
//        }
//    }
    
    public void testGetBooleanValue() throws Exception {
        
        assertTrue(loader.getBooleanValue(null, true));
        assertFalse(loader.getBooleanValue(null, false));
        assertFalse(loader.getBooleanValue("boom", false));
        assertFalse(loader.getBooleanValue("", false));
        
        String [] falseTestStrings = {
                "",
                "boom",
                "false",
                "no",
                "No",
                "NO",
                "False",
                "fAlSe",
        };
        
        for (String s : falseTestStrings) {
            assertFalse(loader.getBooleanValue(s, false));
        }
        
        
        String [] trueTestStrings = {
                "Yes",
                "YES",
                "yEs",
                "true",
                "TRUE",
                "TrUe",
        };
        
        for (String s : trueTestStrings) {
            assertTrue(loader.getBooleanValue(s, false));
        }
    }

    public void testValidatorKeys() throws Exception {
        
        String directoryName = getDataDirectory("testValidatorKeys");
        assertDirectoryExists(directoryName);
        
        IInternalContest contest = loader.fromYaml(null, directoryName);
        
        Problem[] problems = contest.getProblems();
        
        assertEquals("Expect custom validator", CCSConstants.DEFAULT_CCS_VALIDATOR_COMMAND, problems[0].getValidatorCommandLine());
        assertEquals("Expect default validator", CCSConstants.DEFAULT_CCS_VALIDATOR_COMMAND, problems[1].getValidatorCommandLine());
        assertEquals("Expect custom validator", CCSConstants.DEFAULT_CCS_VALIDATOR_COMMAND, problems[2].getValidatorCommandLine());

        String [] letterList = {"S", "E", "X"};
        
        int idx = 0;
        for (Problem problem : problems) {
            String shortName = problem.getLetter();
            assertEquals("Problem letter ",letterList[idx], shortName);
            idx ++;
        }
        
    }
    
    // SOMEDAY get this JUnit working
    public void aTestOverRideValidator() throws Exception {

        String directoryName = getDataDirectory("testValidatorKeys");
        
        assertDirectoryExists(directoryName);
        
        IInternalContest contest = loader.fromYaml(null, directoryName);
        
        Problem[] problems = contest.getProblems();
        
        String overrideMTSVCommand = "/usr/local/bin/mtsv {:problemletter} {:resfile} {:basename}";
        for (Problem problem : problems) {
            assertEquals("Expect custom validator", overrideMTSVCommand, problem.getValidatorCommandLine());
        }
    }
    
    public void testMultipleDataSets() throws Exception {
        
        // TODO CCS JUnit Test Multiple Data sets.
//        String dirname = getDataDirectory(getName());
        
//        String[] contents = Utilities.loadFile(getYamlTestFileName(dirname));
//        assertFalse("File missing " + getYamlTestFileName(), contents.length == 0);
//        loader.loadCCSProblem(contest, dirname, problem, contents);
//        contest = loader.fromYaml(null, contents, getDataDirectory());

    }

    public static Test suiteAmatic() {
        TestSuite suite = new TestSuite("YAML loader test");

        String singletonTestName = "";

        singletonTestName = "testLoader";
         singletonTestName = "testProblemLoader";

        if (!"".equals(singletonTestName)) {
            suite.addTest(new ContestYAMLLoaderTest(singletonTestName));
        } else {

            suite.addTest(new ContestYAMLLoaderTest("testLoader"));
        }

        return suite;
    }
}
