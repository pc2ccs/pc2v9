package edu.csus.ecs.pc2.imports.ccs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.exception.YamlLoadException;
import edu.csus.ecs.pc2.core.export.ExportYAML;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.AutoJudgeSettingComparator;
import edu.csus.ecs.pc2.core.list.SiteComparatorBySiteNumber;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AutoJudgeSetting;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Problem.VALIDATOR_TYPE;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.PlaybackInfo;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.validator.clicsValidator.ClicsValidatorSettings;

/**
 * Unit tests.
 *
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class ContestSnakeYAMLLoaderTest extends AbstractTestCase {

    private static final String YYYY_MM_DD_FORMAT1 = "yyyy-MM-dd HH:mm";

    // generica contest loader
    private IContestLoader loader = new ContestSnakeYAMLLoader();
    
    // specific snake loader
    private ContestSnakeYAMLLoader snake = new ContestSnakeYAMLLoader();

    // private IContestLoader loader = new ContestYAMLLoaderOrig(); // test original Yaml loader

    private SampleContest sampleContest = new SampleContest();

    public ContestSnakeYAMLLoaderTest(String string) {
        super(string);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // setDebugMode(true); // debug mode

    }

    @Override
    public String getRootInputTestDataDirectory() {
        // TODO remove getRootInputTestDataDirectory when moved into pc2v9 project
        String testDataDirectory = DEFAULT_PC2_TEST_DIRECTORY;
        ensureDirectory(testDataDirectory);
        assertDirectoryExists(testDataDirectory);
        return testDataDirectory;
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

    public void testLoaderMethods() throws Exception {


        String yamlFilename= getTestFilename("contest.jt.yaml");

        String[] contents = Utilities.loadFile(yamlFilename);

        assertFalse("File missing " + yamlFilename, contents.length == 0);

//        startExplorer(getDataDirectory());
//        editFile(yamlFilename);jj

        IInternalContest contest = loader.fromYaml(null, contents, getDataDirectory());

        assertNotNull(contest);

        // name: ACM-ICPC World Finals 2011
        assertEquals("Contest title", "ACM-ICPC World Finals 2011", contest.getContestInformation().getContestTitle());

        Language[] languages = loader.getLanguages(contents);

        assertEquals("Number of languages", 4, languages.length);

        assertEquals("Expected language name ", "C++", languages[0].getDisplayName());
        assertEquals("Expected language name ", "C", languages[1].getDisplayName());
        assertEquals("Expected language name ", "Java", languages[2].getDisplayName());
        assertEquals("Expected language name ", "Python", languages[3].getDisplayName());

        // for (Language language : languages){
        // writeRow(System.out, language);
        // }

        Problem[] problems = loader.getProblems(contents, IContestLoader.DEFAULT_TIME_OUT);

        assertEquals("Number of problems", 5, problems.length);

        int problemIndex = 0;

        Problem problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "apl", problem.getDisplayName());
        assertEquals("Expected default timeout ", IContestLoader.DEFAULT_TIME_OUT, problem.getTimeOutInSeconds());

        assertTrue(problem.isComputerJudged());  // IS
        assertFalse(problem.isManualReview());
        assertFalse(problem.isPrelimaryNotification());

        assertJudgementTypes(problem, true, false, false);

        problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "barcodes", problem.getDisplayName());
        assertEquals("Expecting problem timeout for " + problem, 10, problem.getTimeOutInSeconds());

        assertTrue(problem.isComputerJudged());   // IS
        assertFalse(problem.isManualReview());
        assertFalse(problem.isPrelimaryNotification());

        assertJudgementTypes(problem, true, false, false);

        problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "biobots", problem.getDisplayName());
        assertEquals("Expecting problem timeout for " + problem, 23, problem.getTimeOutInSeconds());

//        assertFalse(problem.isComputerJudged());
//        assertTrue(problem.isManualReview());     // IS
//        assertFalse(problem.isPrelimaryNotification());

        assertJudgementTypes(problem, false, true, false);

        problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "castles", problem.getDisplayName());
        assertEquals("Expecting problem timeout for " + problem, 4, problem.getTimeOutInSeconds());

//        assertTrue(problem.isComputerJudged());   // IS
//        assertTrue(problem.isManualReview());     // IS
//        assertFalse(problem.isPrelimaryNotification());
//
        assertJudgementTypes(problem, true, true, false);

        problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "channel", problem.getDisplayName());
        assertEquals("Expected default timeout ", IContestLoader.DEFAULT_TIME_OUT, problem.getTimeOutInSeconds());

//        assertTrue(problem.isComputerJudged());   // IS
//        assertTrue(problem.isManualReview());   // IS
//        assertTrue(problem.isPrelimaryNotification());   // IS

        assertJudgementTypes(problem, true, true, true);

    }

    /**
     * Test judgement types in problemset:
     */
    public void testProblemSetJudgingTypes () throws Exception {

        String[] yamlLines = { //
        "problemset:", //
                "  - letter:     A", //
                "    short-name: apl", //
                "    color:      yellow", //
                "    rgb:        #ffff00", //
                "    computer-judged: false", "    manual-review: true", "    send-prelim-judgement: false", };

        Problem[] problems = loader.getProblems(yamlLines, IContestLoader.DEFAULT_TIME_OUT);
        int problemIndex = 0;

        Problem problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "apl", problem.getDisplayName());
        assertEquals("Expected default timeout ", IContestLoader.DEFAULT_TIME_OUT, problem.getTimeOutInSeconds());

        assertJudgementTypes(problem, false, true, false);
//        assertFalse(problem.isComputerJudged());
//        assertTrue(problem.isManualReview());
//        assertFalse(problem.isPrelimaryNotification());
    }

    public void testProblemSetJudgingTypesTwo() throws Exception {
        String inputYamlFile = getProblemSetYamlTestFileName();

        String[] yamlLines = Utilities.loadFile(inputYamlFile);

        Problem[] problems = loader.getProblems(yamlLines, IContestLoader.DEFAULT_TIME_OUT);

        assertEquals("Expecting number of problems ",5, problems.length);

        Problem problem = problems[0];

        assertJudgementTypes(problem, true, false, false);
    }


    /**
     * Test Judging Type in problem.
     *
     * @throws Exception
     */
    public void testProblemSetJudgingTypesThree() throws Exception {
        String inputYamlFile = getProblemSetYamlTestFileName();

        String[] yamlLines = Utilities.loadFile(inputYamlFile);

        Problem[] problems = loader.getProblems(yamlLines, IContestLoader.DEFAULT_TIME_OUT);

        assertEquals("Expecting number of problems ",5, problems.length);

        Problem problem = problems[2];

        assertEquals("Expected problem name ", "biobots", problem.getDisplayName());

        assertTrue(problem.isComputerJudged());
        assertTrue(problem.isManualReview());
        assertTrue(problem.isPrelimaryNotification());

        assertJudgementTypes(problem, true, true, true);
    }


    /**
     * Test Judgement types in main contest.yaml.
     *
     */
    public void testDefaultJudgingTypes() throws Exception {

        String inputYamlFile = getDataDirectory("contest.yaml");
//        System.out.println("input Filename: "+inputYamlFile);

        String[] lines = Utilities.loadFile(inputYamlFile);

        IInternalContest contest = loader.fromYaml(null, lines, getDataDirectory());

        assertNotNull(contest);

        Problem[] problems = contest.getProblems();

        assertEquals("Expecting number of problems ",5, problems.length);

        Problem problem = problems[0];

        assertJudgementTypes(problem, true, false, false);
    }

    /**
     * Test default Judgement types problem.yaml
     *
     */
    public void testJudgementTypesinProblemYaml() throws Exception {

        String dir = getDataDirectory(this.getName());

        String problemShortName = "td";
        ensureDirectory(dir);

//        String filename = dir + File.separator + problemShortName + File.separator +IContestLoader.DEFAULT_PROBLEM_YAML_FILENAME;
//        System.out.println("filename " + filename);
//        editFile(filename);

//        startExplorer(dir);
        String problemTitlte = "sumit Title"; // note also set in problem.yaml
        Problem problem = new Problem(problemTitlte);
        problem.setShortName("BeforeLoad");

        assertJudgementTypes(problem, false, false, false);

        IInternalContest contest = new InternalContest();

        problem.setShortName(problemShortName);

        boolean overrideUsePc2Validator = false;

        loader.loadProblemInformationAndDataFiles(contest, dir, problem, overrideUsePc2Validator);

        Problem[] problems = contest.getProblems();

        assertEquals("Expecting number of problems ", 1, problems.length);

        Problem problem2 = problems[0];

        assertEquals("Problem title", problemTitlte, problem2.getDisplayName());

        assertJudgementTypes(problem2, false, true, true);
    }
    


    /**
     * Tests problem judgement types.
     *
     * @param problem
     * @param computerJudged
     * @param manualReview
     * @param prelimaryNotification
     */
    private void assertJudgementTypes(Problem problem, boolean computerJudged, boolean manualReview, boolean prelimaryNotification) {
        assertEquals("Expecting comptuer judged for " + problem.getShortName(), computerJudged, problem.isComputerJudged());
        assertEquals("Expecting manual review for " + problem.getShortName(), manualReview, problem.isManualReview());
        assertEquals("Expecting prelim notification  for " + problem.getShortName(), prelimaryNotification, problem.isPrelimaryNotification());
    }

    private String getYamlTestFileName() {
        return getTestFilename(IContestLoader.DEFAULT_CONTEST_YAML_FILENAME);
    }

    public void testLoader() throws Exception {

        IInternalContest contest = loader.fromYaml(null, getDataDirectory());
        assertNotNull(contest);

//        startExplorer(getDataDirectory());

        assertEquals("Contest title", "ACM-ICPC World Finals 2011", contest.getContestInformation().getContestTitle());

        ContestInformation info = contest.getContestInformation();

        // short-name: ICPC WF 2011
        assertEquals("ICPC WF 2011", info.getContestShortName());

        // start-time: 2011-02-04 01:23Z
        assertEquals("Fri Feb 04 01:23:00 PST 2011", info.getScheduledStartDate().toString());

        ContestTime time = contest.getContestTime();
        assertNotNull("Expecting non-null contest time ",time);

        // duration: 5:00:00
        assertEquals(18000, time.getContestLengthSecs());

        // scoreboard-freeze: 4:00:00
        assertEquals("14400", info.getFreezeTime());

        Language[] languages = contest.getLanguages();

        assertEquals("Number of languages", 4, languages.length);

        assertEquals("Expected language name ", "C++", languages[0].getDisplayName());
        assertEquals("Expected language name ", "C", languages[1].getDisplayName());
        assertEquals("Expected language name ", "Java", languages[2].getDisplayName());

        // for (Language language : languages){
        // writeRow(System.out, language);
        // }

        Account[] accounts = contest.getAccounts();

        ClientType.Type type = ClientType.Type.TEAM;
        assertEquals("Number of accounts " + type.toString(), 65, getClientCount(contest, type));
        type = ClientType.Type.JUDGE;
        assertEquals("Number of accounts " + type.toString(), 20, getClientCount(contest, type));
        type = ClientType.Type.ADMINISTRATOR;
        assertEquals("Number of accounts " + type.toString(), 0, getClientCount(contest, type));
        type = ClientType.Type.SCOREBOARD;
        assertEquals("Number of accounts " + type.toString(), 0, getClientCount(contest, type));

        /**
         * Test the start number for site 3 starts at 300.
         */
        Vector<Account> site3teams = contest.getAccounts(Type.TEAM, 3);
        Account[] account3 = (Account[]) site3teams.toArray(new Account[site3teams.size()]);
        for (Account account : account3) {
            assertTrue("Expecting team numbers above 299 on site 3", account.getClientId().getClientNumber() > 299);
        }

        assertEquals("Number of accounts", 85, accounts.length);

        checkPermissions(accounts);

        Problem[] problems = contest.getProblems();

        assertEquals("Number of problems", 5, problems.length);

        int problemIndex = 0;

        Problem problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "apl Title", problem.getDisplayName());
        assertEquals("Expected default timeout for " + problem, 20, problem.getTimeOutInSeconds());

        problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "barcodes Title", problem.getDisplayName());

        problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "biobots Title", problem.getDisplayName());

        problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "Castles in the Sand", problem.getDisplayName());

        problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "Channel Island Navigation", problem.getDisplayName());
        assertEquals("Expected default timeout for " + problem, 20, problem.getTimeOutInSeconds());

        assertTrue("Expecting comptuer judged", problem.isComputerJudged());

        assertJudgementTypes(problem, true, false, false);

    }


    public void testContestInformation() throws Exception {

        IInternalContest contest = loader.fromYaml(null, getDataDirectory());
        assertNotNull(contest);

        assertEquals("Contest title", "ACM-ICPC World Finals 2011", contest.getContestInformation().getContestTitle());

        ContestInformation info = contest.getContestInformation();

        // short-name: ICPC WF 2011
        assertEquals("ICPC WF 2011", info.getContestShortName());

        // start-time: 2011-02-04 01:23Z
        assertEquals("Fri Feb 04 01:23:00 PST 2011", info.getScheduledStartDate().toString());

        ContestTime time = contest.getContestTime();
        assertNotNull("Expecting non-null contest time ",time);

        // duration: 5:00:00
        assertEquals(18000, time.getContestLengthSecs());

        // scoreboard-freeze: 4:00:00
        assertEquals("14400", info.getFreezeTime());
    }

    /**
     * Test contest.yaml with double quotes.
     *
     * Test for Bug 829
     *
     * @throws Exception
     */
    public void testLoaderDoubleQuotedStrings() throws Exception {

        // kplorer(new File(getDataDirectory()));

        String inputYamlFile = getTestFilename("contest_double.yaml");

        assertFileExists(inputYamlFile);

        String[] lines = Utilities.loadFile(inputYamlFile);

        // editFile(inputYamlFile);
        IInternalContest contest = loader.fromYaml(null, lines, getDataDirectory());

        assertNotNull(contest);

        assertEquals("Contest title", "ACM-ICPC World Finals 2011", contest.getContestInformation().getContestTitle());


        Language[] languages = contest.getLanguages();

        assertEquals("Number of languages", 3, languages.length);

        assertEquals("Expected language name ", "C++", languages[0].getDisplayName());
        assertEquals("Expected language name ", "C", languages[1].getDisplayName());
        assertEquals("Expected language name ", "Java", languages[2].getDisplayName());

        // for (Language language : languages){
        // writeRow(System.out, language);
        // }

        Account[] accounts = contest.getAccounts();

        ClientType.Type type = ClientType.Type.TEAM;
        assertEquals("Number of accounts " + type.toString(), 65, getClientCount(contest, type));
        type = ClientType.Type.JUDGE;
        assertEquals("Number of accounts " + type.toString(), 20, getClientCount(contest, type));
        type = ClientType.Type.ADMINISTRATOR;
        assertEquals("Number of accounts " + type.toString(), 0, getClientCount(contest, type));
        type = ClientType.Type.SCOREBOARD;
        assertEquals("Number of accounts " + type.toString(), 0, getClientCount(contest, type));

        /**
         * Test the start number for site 3 starts at 300.
         */
        Vector<Account> site3teams = contest.getAccounts(Type.TEAM, 3);
        Account[] account3 = (Account[]) site3teams.toArray(new Account[site3teams.size()]);
        for (Account account : account3) {
            assertTrue("Expecting team numbers above 299 on site 3", account.getClientId().getClientNumber() > 299);
        }

        assertEquals("Number of accounts", 85, accounts.length);

        checkPermissions(accounts);

        Problem[] problems = contest.getProblems();
        assertEquals("Number of problems", 5, problems.length);

        int problemIndex = 0;

        Problem problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "apl Title", problem.getDisplayName());
        assertEquals("Expected default timeout for " + problem, 20, problem.getTimeOutInSeconds());

        problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "barcodes Title", problem.getDisplayName());

        problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "biobots Title", problem.getDisplayName());

        problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "Castles in the Sand", problem.getDisplayName());

        problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "Channel Island Navigation", problem.getDisplayName());
        assertEquals("Expected default timeout for " + problem, 20, problem.getTimeOutInSeconds());

        assertTrue("Expecting comptuer judged", problem.isComputerJudged());

    }

    private void checkPermissions(Account[] accounts) {
        for (Account account : accounts) {

            assertEquals("Expecting same permissions for " + account, account.getPermissionList().getList().length, getPermList(account.getClientId().getClientType()).length);
        }

    }

    /**
     * Test contest title with double quoted strings.
     *
     * Test for Bug 829
     *
     * @throws Exception
     */
    public void testDoubleQuoteImport() throws Exception {
        // startExplorer(new File(getDataDirectory()));

        String inputYamlFile = getTestFilename("double.yaml");

        String[] lines = Utilities.loadFile(inputYamlFile);
        IInternalContest contest = loader.fromYaml(null, lines, getDataDirectory());

        assertEquals("Double Quoted Contest Name", contest.getContestInformation().getContestTitle());
    }

    /**
     * Test that single quote in contest title is stripped off.
     *
     * Test for Bug 829
     *
     * @throws Exception
     */
    public void testContestTitle() throws Exception {

        String inputYamlFile = getTestFilename("singeQuoteTitle.yaml");

        String[] lines = Utilities.loadFile(inputYamlFile);

        IInternalContest contest = loader.fromYaml(null, lines, getDataDirectory());

        assertEquals("Single Quoted Contest Name", contest.getContestInformation().getContestTitle());

    }

    /**
     * Test default SerializedFile attributes.
     *
     */
    public void testDefaultSerializeFileAttributes() throws Exception {

        String filename = getDefaultContestYaml();

        // startExplorer(getDataDirectory());
        assertFileExists(filename);

        SerializedFile serializedFile = new SerializedFile(filename);

        assertNotNull("Expected instace of SerializedFile ", serializedFile);
        assertNotNull("Expected instace of SerializedFile.getFile() ", serializedFile.getFile());
        assertTrue("Expected none empty file " + filename, serializedFile.getBuffer().length > 0);
        assertFalse("Expected none empty file " + filename, serializedFile.isExternalFile());

    }

    private String getDefaultContestYaml() {
        return getDataDirectory() + File.separator + IContestLoader.DEFAULT_CONTEST_YAML_FILENAME;
    }

    public void testProblemLoader() throws Exception {

        boolean loadDataFiles = true;

        IInternalContest contest = loader.fromYaml(null, getDataDirectory(), loadDataFiles);

        debugPrint("Dir " + getDataDirectory());
        // startExplorer(getDataDirectory());

        // editFile(getDataDirectory()+"/"+IContestLoader.DEFAULT_CONTEST_YAML_FILENAME);

        Problem[] problems = contest.getProblems();
        assertEquals("Number of problems", 5, problems.length);

        int problemNumber = 0;
        for (Problem problem : problems) {
            debugPrint("Problem " + problemNumber + " - " + problem.getDisplayName() + " cases " + problem.getNumberTestCases());

            ProblemDataFiles problemDataFiles = contest.getProblemDataFile(problem);

            debugPrint("Problem id=" + problemDataFiles.getProblemId());
            problemNumber++;
            if (problem.getNumberTestCases() > 1) {
                for (int i = 0; i < problem.getNumberTestCases(); i++) {
                    int testCaseNumber = i + 1;

                    String datafile = problem.getDataFileName(testCaseNumber);
                    String answerfile = problem.getAnswerFileName(testCaseNumber);

                    debugPrint("       Data File name tc=" + testCaseNumber + " name: " + datafile);
                    debugPrint("     Answer File name tc=" + testCaseNumber + " name: " + answerfile);

                    SerializedFile sdatafile = problemDataFiles.getJudgesDataFiles()[problemNumber - 1];
                    debugPrint("       Data File tc=" + testCaseNumber + " " + sdatafile);

                    assertFalse("Expect non-exernally stored file ", sdatafile.isExternalFile());
                    assertNotNull("Should find data file tc=" + testCaseNumber + " ", sdatafile);
                    assertNotNull("Data file should have contents tc=" + testCaseNumber + " ", sdatafile.getBuffer());

                    SerializedFile sanswerfile = problemDataFiles.getJudgesDataFiles()[problemNumber - 1];
                    debugPrint("     Answer File tc=" + testCaseNumber + " " + sanswerfile);
                    assertFalse("Expect non-exernally stored file ", sanswerfile.isExternalFile());
                    assertNotNull("Should find answer file tc=" + testCaseNumber + " " + sanswerfile, sanswerfile);
                    assertNotNull("Answer file should have contents tc=" + testCaseNumber + " " + sanswerfile, sanswerfile.getBuffer());

                    debugPrint("       Data File name " + testCaseNumber + " : " + datafile + " size " + sdatafile.getBuffer().length);
                    debugPrint("     Answer File name " + testCaseNumber + " : " + answerfile + " size " + sanswerfile.getBuffer().length);

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

            assertEquals("name: " + testProblem.getShortName() + " data in name", name + ".in", testProblem.getDataFileName(idx));
            assertEquals("name: " + testProblem.getShortName() + "data ans name", name + ".ans", testProblem.getAnswerFileName(idx));

            idx++;
        }

        String[] probNames = { "apl Title", "barcodes Title", "biobots Title", "Castles in the Sand", "Channel Island Navigation" };
        int[] dataSetCount = { 1, 15, 3, 1, 1 };

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

    public void testLoadLanguages() throws Exception {

        String[] yamlLines = Utilities.loadFile(getYamlTestFileName());

        Language[] languages = loader.getLanguages(yamlLines);

        assertEquals("Expected 3 languages", 4, languages.length);

        String[] expected = { "C++", "C", "Java", "Python" };

        for (int i = 0; i < expected.length; i++) {
            assertEquals("language name", expected[i], languages[i].getDisplayName());
        }

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
            assertTrue("Should return problem " + problems[i].getDisplayName(), problems[i].isSameAs(problems[i]));
        }

        // int i = 0;
        // for (Problem problem : contestProblems) {
        // System.out.println(SampleContest.getProblemLetter(++i) + " " + problem);
        // }

        try {
            problems = loader.getProblemsFromLetters(contestProblems, SampleContest.getProblemLetter(contestProblems.length + 1));
            fail("Should have thrown exception because no problem with letter " + SampleContest.getProblemLetter(contestProblems.length));
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

        debugPrint("Loading " + contestYamlFilename);

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

        if (isDebugMode()) {
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
                "    site: 1", //
                "    number: 4", //
                "    letters: A, B, C", //
                "    enabled: yes", //
        };

        AutoJudgeSetting[] autoJudgeSettings = loader.getAutoJudgeSettings(yamlLines, contest.getProblems());

        assertEquals("Auto judge settings count ", 1, autoJudgeSettings.length);

        AutoJudgeSetting setting = autoJudgeSettings[0];

        Filter filter = setting.getProblemFilter();

        assertEquals("Judge 4 ", 4, setting.getClientId().getClientNumber());

        String letterList = SampleContest.getProblemLetters(contest, filter);

        assertEquals("Problem letters", "A, B, C", letterList);
    }

    public void testAutoJudgeSettingsAll() throws Exception {

        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(1, 2, 22, 12, true);

        String[] yamlLines = { //
        "auto-judging:", //
                "  - account: JUDGE", //
                "    site: 1", //
                "    number: 4", //
                "    letters: all", //
                "    enabled: yes", //
        };

        AutoJudgeSetting[] autoJudgeSettings = loader.getAutoJudgeSettings(yamlLines, contest.getProblems());

        assertEquals("Auto judge settings count ", 1, autoJudgeSettings.length);

        AutoJudgeSetting setting = autoJudgeSettings[0];

        Filter filter = setting.getProblemFilter();

        String letterList = SampleContest.getProblemLetters(contest, filter);

        assertEquals("Problem letters", "A, B, C, D, E, F", letterList);

    }

    public void testAllJudgesAndAutoJudge() throws Exception {

        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(1, 2, 22, 12, true);

        String[] yamlLines = { //
        "accounts:", //
                "  - account: JUDGE", //
                "    site: 1", //
                "    count: 12", //
                "", //
                "auto-judging:", //
                "  - account: JUDGE", //
                "    site: 1", //
                "    number: all", //
                "    letters: A, C, E", //
                "    enabled: yes", //
        };

        AutoJudgeSetting[] autoJudgeSettings = loader.getAutoJudgeSettings(yamlLines, contest.getProblems());

        int numberJudges = 12; // acccounts from yaml

        assertEquals("Auto judge settings count ", numberJudges, autoJudgeSettings.length);

        for (AutoJudgeSetting setting : autoJudgeSettings) {

            Filter filter = setting.getProblemFilter();
            String letterList = SampleContest.getProblemLetters(contest, filter);

            assertEquals("Problem letters", "A, C, E", letterList);
        }

    }

    /**
     * Basic tests for snake yaml loader.
     *
     * @throws IOException
     */
    public void testYamlLoad() throws IOException {

        String contestYamlFilename = getYamlTestFileName();

        ensureDirectory(getDataDirectory());
        ensureDirectory(getDataDirectory(this.getName()));
        // startExplorer(getDataDirectory(this.getName()));

        
        Map<String, Object> map = snake.loadYaml(contestYamlFilename);

        // System.out.println("object is type "+out.getClass().getName());

        assertNotNull("Expecting loaded map", map);

        Set<String> set = map.keySet();
        String[] list = (String[]) set.toArray(new String[set.size()]);

        assertTrue("Expecting more than one element ", list.length > 9);
        
    }

    public void testReplayLoad() throws Exception {

        String contestYamlFilename = getYamlTestFileName();

        assertTrue("Test file does not exist " + contestYamlFilename, Utilities.isFileThere(contestYamlFilename));
        String[] yamlLines = Utilities.loadFile(contestYamlFilename);

        debugPrint("Loading " + contestYamlFilename);

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

    private String getLatexFilename(String problemShortName) {
        return getDataDirectory() + File.separator + problemShortName + File.separator + "problem_statement" + File.separator + IContestLoader.DEFAULT_PROBLEM_LATEX_FILENAME;
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

    public void testdoNotLoadExternalFile() throws Exception {

        String dirname = getDataDirectory(getName());
        // String dirname = getDataDirectory("testValidatorKeys");
        // String dirname = getDataDirectory();
        Utilities.insureDir(dirname);
        assertDirectoryExists(dirname);

        String filename = dirname + File.separator + IContestLoader.DEFAULT_CONTEST_YAML_FILENAME;
        // System.out.println(filename);
        assertFileExists(filename);

        IInternalContest contest;

        // Load data files
        try {
            
        contest = loader.fromYaml(null, dirname, true);
        
        } catch (YamlLoadException e) {
            System.out.println("failed loading in file "+e.getFilename());
            editFile(e.getFilename());
            throw e;
        }

        for (Problem problem : contest.getProblems()) {
            assertFalse(problem.isUsingExternalDataFiles());
            assertNotNull(problem.getExternalDataFileLocation());
            ProblemDataFiles dataFiles = contest.getProblemDataFile(problem);
            assertEquals("Expecting loaded answer files ", 12, dataFiles.getJudgesAnswerFiles().length);
            assertEquals("Expecting loaded data files ", 12, dataFiles.getJudgesDataFiles().length);
            SerializedFile[] ansfiles = dataFiles.getJudgesAnswerFiles();
            
            for (SerializedFile serializedFile : ansfiles) {
                assertTrue("Expecting Loaded filee", serializedFile.getBuffer().length != 0);
            }
            
        }

        // Do not load data files
        contest = loader.fromYaml(null, dirname, false);

        for (Problem problem : contest.getProblems()) {
            assertTrue("Expecting false using data files", problem.isUsingExternalDataFiles());
            assertNotNull(problem.getExternalDataFileLocation());
            ProblemDataFiles dataFiles = contest.getProblemDataFile(problem);
            SerializedFile[] ansfiles = dataFiles.getJudgesAnswerFiles();
            for (SerializedFile serializedFile : ansfiles) {
                assertTrue("Expecting external answer file ", serializedFile.getBuffer().length == 0);
            }
            
        }
        
        assertNoAutoStart(contest);
    }

    public void testGetBooleanValue() throws Exception {

        assertTrue(loader.getBooleanValue(null, true));
        assertFalse(loader.getBooleanValue(null, false));
        assertFalse(loader.getBooleanValue("boom", false));
        assertFalse(loader.getBooleanValue("", false));

        String[] falseTestStrings = { "", "boom", "false", "no", "No", "NO", "False", "fAlSe", };

        for (String s : falseTestStrings) {
            assertFalse(loader.getBooleanValue(s, false));
        }

        String[] trueTestStrings = { "Yes", "YES", "yEs", "true", "TRUE", "TrUe", };

        for (String s : trueTestStrings) {
            assertTrue(loader.getBooleanValue(s, false));
        }
    }

    public void testValidatorKeys() throws Exception {

        String testDirectory = getDataDirectory("testValidatorKeys");
        assertDirectoryExists(testDirectory);

        // String filename = testDirectory + File.separator + IContestLoader.DEFAULT_CONTEST_YAML_FILENAME;
        // editFile(filename);

        IInternalContest contest = loader.fromYaml(null, testDirectory);

        Problem[] problems = contest.getProblems();

        // assertEquals("Expect custom validator", CCSConstants.DEFAULT_CCS_VALIDATOR_COMMAND, problems[0].getValidatorCommandLine());
        // assertEquals("Expect default validator", CCSConstants.DEFAULT_CCS_VALIDATOR_COMMAND, problems[1].getValidatorCommandLine());
        // assertEquals("Expect custom validator", CCSConstants.DEFAULT_CCS_VALIDATOR_COMMAND, problems[2].getValidatorCommandLine());

        String[] letterList = { "S", "E", "X" };

        int idx = 0;
        for (Problem problem : problems) {
            String shortName = problem.getLetter();
            assertEquals("Problem letter ", letterList[idx], shortName);
            idx++;
        }
        
        assertAutoStart(contest, "2011-02-04 01:23", false);

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

    /**
     * Get contest.yaml file in test directory.
     *
     * @see #getTestFilename(String)
     * @param dirname
     * @return
     */
    private String getYamlTestFileName(String dirname) {
        return getTestFilename(dirname + File.separator + IContestLoader.DEFAULT_CONTEST_YAML_FILENAME);
    }

    public void testMultipleDataSetsCCS() throws Exception {

        String dirname = getDataDirectory(getName());
        String yamlFileName = getYamlTestFileName(this.getName());

        ensureDirectory(dirname);

        // startExplorer(dirname);
        // editFile(yamlFileName);

        assertFileExists(yamlFileName);

        String[] lines = loader.loadFileWithIncludes(dirname, yamlFileName);

        IInternalContest contest = loader.fromYaml(null, lines, dirname);

        Problem[] problems = contest.getProblems();

        for (Problem problem : problems) {

            ProblemDataFiles problemDataFiles = new ProblemDataFiles(problem);
            String secretDataDir = loader.getCCSDataFileDirectory(dirname, problem);
            loader.loadCCSProblemFiles(contest, secretDataDir, problem, problemDataFiles);

            assertTrue("Expecting more than one data set in " + secretDataDir, problemDataFiles.getJudgesDataFiles().length > 1);
            assertTrue("Expecting more than on data set in " + secretDataDir, problemDataFiles.getJudgesAnswerFiles().length > 1);
        }
    }

    public void testSuppContestYaml2() throws Exception {

        String dirname = getDataDirectory("supp");

        IInternalContest origContest = sampleContest.createContest(1, 1, 25, 12, true);
        String[] problemNames = { "Nike", "Athena", "Gaia", "Orion", "Jasmin", "Calliope", "Aphrodite" };
        char letter = 'A';
        letter += origContest.getProblems().length;

        Problem[] problems = sampleContest.createProblems(problemNames, letter, 1);
        for (Problem problem : problems) {
            origContest.addProblem(problem);
        }

        IInternalContest contest = loader.fromYaml(origContest, dirname);
        assertNotNull(contest);
    }

    public void testIncludeFile() throws Exception {

        String dirname = getDataDirectory("inctest");

        String loadfile = dirname + File.separator + "contest.yaml";
        assertFileExists(loadfile, "contest.yaml for test");

        String[] lines = loader.loadFileWithIncludes(dirname, dirname + File.separator + "contest.yaml");

        // dumpLines ("", lines, true);

        assertEquals("expected number of lines ", 51, lines.length);
        
    }

    /**
     * Dump lines to stdout.
     *
     * @param comment
     *            phrase that identifis these lines
     * @param lines
     *            array o' string
     * @param autoNumberLines
     *            true to prefix with numbers
     */
    protected void dumpLines(String comment, String[] lines, boolean autoNumberLines) {

        System.out.println(comment + " number of liens " + lines.length);

        int i = 1;
        for (String string : lines) {
            if (autoNumberLines) {
                System.out.print(i + ": ");
            }
            System.out.println(string);
            i++;
        }
    }

    private String getProblemSetYamlTestFileName() {
        return getTestFilename(IContestLoader.DEFAULT_PROBLEM_SET_YAML_FILENAME);
    }

    /**
     * Test load problemset.yaml.
     *
     * @throws Exception
     */
    public void testLoadProblemSet() throws Exception {

        String inputYamlFilename = getProblemSetYamlTestFileName();

        // editFile(inputYamlFilename);

        assertFileExists(inputYamlFilename);

        String[] contents = Utilities.loadFile(getProblemSetYamlTestFileName());

        IInternalContest contest = loader.fromYaml(null, contents, getDataDirectory());

        assertNotNull(contest);

        Problem[] problems = contest.getProblems();
        assertEquals("Number of problems", 5, problems.length);

        for (Problem problem2 : problems) {
            assertNotNull("Expected problem short name ", problem2.getShortName());
        }

        int problemIndex = 0;

        Problem problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "apl Title", problem.getDisplayName());
        assertEquals("Expected default timeout for " + problem, 20, problem.getTimeOutInSeconds());

        problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "barcodes Title", problem.getDisplayName());

        problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "biobots Title", problem.getDisplayName());

        problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "Castles in the Sand", problem.getDisplayName());

        problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "Channel Island Navigation", problem.getDisplayName());
        assertEquals("Expected default timeout for " + problem, 20, problem.getTimeOutInSeconds());

        assertEquals("Number of problems", 5, problems.length);

        ProblemDataFiles[] problemDataFilesList = contest.getProblemDataFiles();

        for (ProblemDataFiles problemDataFiles : problemDataFilesList) {
            // assertTrue("Expecting comptuer judged", problemDataFiles.getJudgesDataFile().isExternalFile());

            String problemTitle = contest.getProblem(problemDataFiles.getProblemId()).getDisplayName();

            assertNotNull("Missing judges data file, for problem " + problemTitle, problemDataFiles.getJudgesDataFile());
            assertNotNull("Missing judges answer file, for problem " + problemTitle, problemDataFiles.getJudgesAnswerFile());

        }

    }

    /**
     * Tests CCS load.
     *
     * Especially test that the file contents are not loaded {@link SerializedFile#isExternalFile()}.
     *
     * @throws Exception
     */
    public void testCCSLoad() throws Exception {

        String inputYamlFilename = getProblemSetYamlTestFileName();
        // startExplorer(getDataDirectory());
        // editFile(inputYamlFilename);

        assertFileExists(inputYamlFilename);
        
        String[] contents = Utilities.loadFile(getProblemSetYamlTestFileName());

        assertFileExists(getProblemSetYamlTestFileName());
        // editFile(getProblemSetYamlTestFileName());

        // Load from YAML but make all data/ans files external.

        IInternalContest contest = loader.fromYaml(null, contents, getDataDirectory(), true);

        assertNotNull(contest);
        
        assertNoAutoStart (contest);

        Problem[] problems = contest.getProblems();
        assertEquals("Number of problems", 5, problems.length);

        for (Problem problem2 : problems) {
            assertNotNull("Expected problem short name ", problem2.getShortName());
        }

        int problemIndex = 0;

        Problem singleProblem = problems[problemIndex++];
        assertEquals("Expected problem name ", "apl Title", singleProblem.getDisplayName());
        assertEquals("Expected default timeout for " + singleProblem, 20, singleProblem.getTimeOutInSeconds());

        singleProblem = problems[problemIndex++];
        assertEquals("Expected problem name ", "barcodes Title", singleProblem.getDisplayName());

        singleProblem = problems[problemIndex++];
        assertEquals("Expected problem name ", "biobots Title", singleProblem.getDisplayName());

        singleProblem = problems[problemIndex++];
        assertEquals("Expected problem name ", "Castles in the Sand", singleProblem.getDisplayName());

        singleProblem = problems[problemIndex++];
        assertEquals("Expected problem name ", "Channel Island Navigation", singleProblem.getDisplayName());
        assertEquals("Expected default timeout for " + singleProblem, 20, singleProblem.getTimeOutInSeconds());

        ProblemDataFiles[] problemDataFilesList = contest.getProblemDataFiles();

        for (ProblemDataFiles problemDataFiles : problemDataFilesList) {
            String problemTitle = contest.getProblem(problemDataFiles.getProblemId()).getDisplayName();

            assertNotNull("Missing judges data file, for problem " + problemTitle, problemDataFiles.getJudgesDataFile());
            assertNotNull("Missing judges answer file, for problem " + problemTitle, problemDataFiles.getJudgesAnswerFile());

        }

        int problemNumber = 0;
        for (Problem problem : contest.getProblems()) {
            debugPrint("Problem " + problemNumber + " - " + problem.getDisplayName() + " cases " + problem.getNumberTestCases());

            ProblemDataFiles problemDataFiles = contest.getProblemDataFile(problem);

            debugPrint("Problem id=" + problemDataFiles.getProblemId());
            problemNumber++;
            if (problem.getNumberTestCases() > 1) {
                for (int i = 0; i < problem.getNumberTestCases(); i++) {
                    int testCaseNumber = i + 1;

                    String datafile = problem.getDataFileName(testCaseNumber);
                    String answerfile = problem.getAnswerFileName(testCaseNumber);

                    debugPrint("       Data File name tc=" + testCaseNumber + " name: " + datafile);
                    debugPrint("     Answer File name tc=" + testCaseNumber + " name: " + answerfile);

                    SerializedFile sdatafile = problemDataFiles.getJudgesDataFiles()[problemNumber - 1];
                    debugPrint("       Data File tc=" + testCaseNumber + " " + sdatafile);
                    assertFalse("Expect NOT internal/loaded file contents ", sdatafile.isExternalFile());
                    assertNotNull("Should find data file tc=" + testCaseNumber + " ", sdatafile);
                    assertNotNull("Data file should have contents tc=" + testCaseNumber + " ", sdatafile.getBuffer());

                    SerializedFile sanswerfile = problemDataFiles.getJudgesDataFiles()[problemNumber - 1];
                    debugPrint("     Answer File tc=" + testCaseNumber + " " + sanswerfile);
                    assertFalse("Expect internal/loaded file contents ", sanswerfile.isExternalFile());
                    assertNotNull("Expect NOT file contents ", sanswerfile.getBuffer());
                    assertNotNull("Data file should have contents tc=" + testCaseNumber + " ", sanswerfile.getBuffer());

                    assertNotNull("Should find answer file tc=" + testCaseNumber + " " + sanswerfile, sanswerfile);
                    assertNotNull("Answer file should have contents tc=" + testCaseNumber + " " + sanswerfile, sanswerfile.getBuffer());

                    debugPrint("       Data File name " + testCaseNumber + " : " + datafile + " size " + sdatafile.getBuffer().length);
                    debugPrint("     Answer File name " + testCaseNumber + " : " + answerfile + " size " + sanswerfile.getBuffer().length);

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

            assertEquals("name: " + testProblem.getShortName() + " data in name", name + ".in", testProblem.getDataFileName(idx));
            assertEquals("name: " + testProblem.getShortName() + "data ans name", name + ".ans", testProblem.getAnswerFileName(idx));

            idx++;
        }

        String[] probNames = { "apl Title", "barcodes Title", "biobots Title", "Castles in the Sand", "Channel Island Navigation" };
        int[] dataSetCount = { 1, 15, 3, 1, 1 };

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

    /**
     * Assert No Autostart
     * @param contest
     */
    private void assertNoAutoStart(IInternalContest contest) {
        assertAutoStart(contest, null, false);
    }

    /**
     * Test auto start settings.
     * 
     * @param contest
     * @param expectedTimeString - time string in format: "yyyy-MM-dd HH:mm"
     * @param shouldAutoStart - set true if expecting to auto start contest
     */
    private void assertAutoStart(IInternalContest contest,  String expectedTimeString, boolean shouldAutoStart) {
        
        String scheduledStart = null;
        Boolean actualShouldAutoStart = new Boolean(false);
        ContestInformation info = contest.getContestInformation();
        if (info != null){
            // start-time:        2011-02-04 01:23Z
            //   2011-02-04 01:23Z
//            
            SimpleDateFormat formatter = new SimpleDateFormat(YYYY_MM_DD_FORMAT1);
            if (info.getScheduledStartDate() != null){
                
                scheduledStart = formatter.format(info.getScheduledStartDate());
                actualShouldAutoStart = info.isAutoStartContest();
            }
        }
        
//        System.out.println("Auto start at: "+scheduledStart);
//        System.out.println("Will auto start: "+actualShouldAutoStart);
        
        assertEquals("Expecting auto start time ", expectedTimeString, scheduledStart);
        assertEquals("Expecting auto start ",  new Boolean( shouldAutoStart), actualShouldAutoStart);
        
    }

    public void testYamlWriteAndLoad() throws Exception {

        String testDirectory = getOutputDataDirectory("testYamlWriteAndLoad");
        ensureDirectory(testDirectory);

        IInternalContest originalContest = sampleContest.createContest(3, 3, 12, 5, true);
        Problem[] problems = originalContest.getProblems();
        Problem problem = problems[0];
        sampleContest.setClicsValidation(originalContest, null, problem);
        ElementId problemId = problem.getElementId();

        // ExportYAML2 exportYAML = new ExportYAML2();
        ExportYAML exportYAML = new ExportYAML();

        // TODO fix exportFiles to export properly Python spaced YAML.
        exportYAML.exportFiles(testDirectory, originalContest);
        
         String filename = testDirectory + File.separator + IContestLoader.DEFAULT_CONTEST_YAML_FILENAME;
//         editFile(filename);
         
         validateYamlFile(filename);

        exportYAML = null;

        loader.setLoadProblemDataFiles(false);
        IInternalContest contest = loader.fromYaml(null, testDirectory);

        assertNotNull(contest);

        Language[] languages = contest.getLanguages();

        assertEquals("Number of languages", 6, languages.length);

        //
        // languages:
        // - name: 'Java'
        // active: true
        // compilerCmd: 'javac {:mainfile}'
        // exemask: '{:basename}.class'
        // execCmd: 'java {:basename}'
        // runner: 'java'
        // runner-args: '{:basename}

        assertEquals("Expected language name ", "Java", languages[0].getDisplayName());
        assertEquals("Expected language compilerCmd ", "javac {:mainfile}", languages[0].getCompileCommandLine());
        assertEquals("Expected language exemask ", "{:basename}.class", languages[0].getExecutableIdentifierMask());
        assertEquals("Expected language execCmd ", "java {:basename}", languages[0].getProgramExecuteCommandLine());

        assertEquals("Expected language name ", "Default", languages[1].getDisplayName());
        assertEquals("Expected language name ", "GNU C++ (Unix / Windows)", languages[2].getDisplayName());

        problem = originalContest.getProblem(problemId);

        assertEquals("Expected validator name ", Constants.CLICS_VALIDATOR_NAME, problem.getValidatorProgramName());
        assertEquals("Expected validator command ", Constants.DEFAULT_CLICS_VALIDATOR_COMMAND, problem.getValidatorCommandLine());

    }
    
    String getSnakeParserDetails(MarkedYAMLException markedYAMLException) {
        // from ContetYamlLoader

        Mark mark = markedYAMLException.getProblemMark();

        int lineNumber = mark.getLine() + 1; // starts at zero
        int columnNumber = mark.getColumn() + 1; // starts at zero

        return "Parse error at line=" + lineNumber + " column=" + columnNumber + " message=" + markedYAMLException.getProblem();
    }

    /**
     * validates yaml.
     *  
     * @param filename
     * @throws YamlLoadException
     */
    private void validateYamlFile(String filename) throws YamlLoadException {
        
        try {
            Yaml yaml = new Yaml();
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) yaml.load(new FileInputStream(filename));
            assertNotNull(map);
        } catch (MarkedYAMLException e) {
            throw new YamlLoadException(getSnakeParserDetails(e));
        } catch (FileNotFoundException e) {
            throw new YamlLoadException("File not found " + filename);
        }
        
    }

    public void testUnQuote() throws Exception {

        unquoteAssertEquals(loader, "'Java'", "Java");

    }

    private void unquoteAssertEquals(IContestLoader inLoader, String input, String expected) {

        String actual = loader.unquote(input, "'");
        assertEquals("Expected unquoted string", expected, actual);

    }

    public void testJudgeCDPPath() throws Exception {

        String testDirName = getDataDirectory(this.getName());
//        ensureDirectory(testDirName);
//        startExplorer(testDirName);

        String yamlFileName = testDirName + File.separator + "contest.yaml";
        // editFile(yamlFileName);
        if (isDebugMode()) {
            System.out.println("filename = " + yamlFileName);
        }

        String path = loader.getJudgesCDPBasePath(yamlFileName);

        // name: ACM-ICPC World Finals 2011
        assertEquals("Judge CDP Basepath", "/home/pc2/judge/cdp", path);

        loader.setLoadProblemDataFiles(false);
        IInternalContest contest = loader.fromYaml(null, testDirName, false);

        assertEquals("judge CDP ", "/home/pc2/judge/cdp", contest.getContestInformation().getJudgeCDPBasePath());

    }


    public void testSetManualJudge() throws Exception {

        String testdir = getDataDirectory(this.getName());
        // ensureDirectory(testdir+File.separator+"manualProb");
        // startExplorer(testdir);

        Problem problem = new Problem("testmanualproblem");
        problem.setManualReview(false);

        IInternalContest contest = new InternalContest();

        problem.setShortName("manualProb");
        boolean overrideUsePc2Validator = false;

        loader.loadProblemInformationAndDataFiles(contest, testdir, problem, overrideUsePc2Validator);

        assertTrue("Expecting manual review", problem.isManualReview());

    }

    public void testGlobalManualOverrideSet() throws Exception {

        String testdir = getDataDirectory(this.getName());
        // ensureDirectory(testdir);
        // startExplorer(testdir);

        // String inputYamlFile = testdir + File.separator + IContestLoader.DEFAULT_CONTEST_YAML_FILENAME;
        // editFile(inputYamlFile);

        IInternalContest contest = loader.fromYaml(null, testdir, true);

        Problem[] problems = contest.getProblems();

        assertEquals("Expecting problems defined ", 4, problems.length);

        for (Problem problem : problems) {
            assertTrue("Expecting manual review for " + problem, problem.isManualReview());
        }

    }

    /**
     * Test internal load data files.
     *
     *
     * @throws Exception
     */
    public void testExternalFileLoad() throws Exception {
        String testDirectoryName = getDataDirectory(this.getName());
        // ensureDirectory(testDirectoryName);
        // startExplorer(testDirectoryName);

        // String inputYamlFile = testDirectoryName + File.separator + IContestLoader.DEFAULT_CONTEST_YAML_FILENAME;
        // editFile(inputYamlFile);

        boolean loadDataFiles = false;

        IInternalContest contest = loader.fromYaml(null, testDirectoryName, loadDataFiles);

        Problem[] problems = contest.getProblems();

        assertEquals("Expecting problems defined ", 2, problems.length);

        for (Problem problem : problems) {

            String secretDataDir = loader.getCCSDataFileDirectory(testDirectoryName, problem);
            ProblemDataFiles problemDataFiles = new ProblemDataFiles(problem);
            loader.loadCCSProblemFiles(contest, secretDataDir, problem, problemDataFiles);

            SerializedFile[] files = problemDataFiles.getJudgesAnswerFiles();
            for (SerializedFile serializedFile : files) {
                assertTrue("Expecting file to be external file " + serializedFile.getName() + " for " + problem, serializedFile.isExternalFile());
            }

            files = problemDataFiles.getJudgesDataFiles();
            for (SerializedFile serializedFile : files) {
                assertTrue("Expecting file to be external file " + serializedFile.getName() + " for " + problem, serializedFile.isExternalFile());
            }

        }

    }


    /**
     * Test AUTO_JUDGE_KEY aka keyword auto-judging
     */
    public void testAutojudgekey() {

        // SOMEDAY write unit test
        failIfInDebugMode();
    }

    /**
     * Test DEFAULT_VALIDATOR_KEY aka keyword default-validator
     */
    public void testDefaultvalidatorkey() {
        // SOMEDAY write unit test
        failIfInDebugMode();
    }

    /**
     * Test INPUT_KEY aka keyword input
     */
    public void testInputkey() {
        // SOMEDAY write unit test
        failIfInDebugMode();
    }

    /**
     * Test JUDGE_EXECUTE_COMMAND_KEY aka keyword judge-exec-cmd
     */
    public void testJudgeexecutecommandkey() {
        // SOMEDAY write unit test
        failIfInDebugMode();
    }

    /**
     * Test OVERRIDE_VALIDATOR_KEY aka keyword override-validator
     */
    public void testOverridevalidatorkey() {
        // SOMEDAY write unit test
        failIfInDebugMode();
    }

    /**
     * Test PROBLEM_INPUT_KEY aka keyword input
     */
    public void testProbleminputkey() {
        // SOMEDAY write unit test
        failIfInDebugMode();
    }

    /**
     * Test PROBLEM_LOAD_DATA_FILES_KEY aka keyword load-data-files
     */
    public void testProblemloaddatafileskey() {
        failIfInDebugMode();
    }

    /**
     * Test READ_FROM_STDIN_KEY aka keyword readFromSTDIN
     */
    public void testReadfromstdinkey() {
        // SOMEDAY write unit test
        failIfInDebugMode();

    }

    /**
     * Test SEND_PRELIMINARY_JUDGEMENT_KEY aka keyword send-prelim-judgement
     */
    public void testSendpreliminaryjudgementkey() {
        // SOMEDAY write unit test
        failIfInDebugMode();
    }

    /**
     * Test USE_JUDGE_COMMAND_KEY aka keyword use-judge-cmd
     */
    public void testUsejudgecommandkey() {
        // SOMEDAY write unit test
        failIfInDebugMode();
    }

    /**
     * Test USING_PC2_VALIDATOR aka keyword use-internal-validator
     */
    public void testUsingpc2validator() {
        // SOMEDAY write unit test
        failIfInDebugMode();
    }


    /**
     * Test VALIDATOR_KEY aka keyword validator
     */
    public void testValidatorkey() {
        // SOMEDAY write unit test
        failIfInDebugMode();
    }


    private void failIfInDebugMode() {
        if(isDebugMode()){
            fail();
        }
    }
    
    /**
     * Test load sample CCS contest.
     * 
     * @throws Exception
     */
    public void testCCS1Load() throws Exception {
        
        String entryLocation = "ccs1";
        
        InternalContest contest = new InternalContest();
        
        loader.initializeContest(contest, new File(entryLocation));
        
//        System.out.println("Loaded CDP/config values from " + entryLocation);
        
//        File cdpConfigDir = loader.findCDPConfigDirectory(new File(entryLocation));
//        String yamlFilename =cdpConfigDir.getAbsolutePath() + File.separator + IContestLoader.DEFAULT_CONTEST_YAML_FILENAME; 
//        editFile(yamlFilename);
//        System.out.println("cds config dir = "+cdpConfigDir);
        
        assertAutoStart(contest, "2020-02-04 01:23", true);
        
        Language[] languages = contest.getLanguages();
        assertEquals("Number of languages", 3, languages.length);
        
        Account[] accounts = contest.getAccounts();
        assertEquals("Number of accounts", 0, accounts.length);

        Site[] sites = contest.getSites();
        assertEquals("Number of sites", 1, sites.length);
        
        Problem[] problems = contest.getProblems();
        assertEquals("Number of problems", 5, problems.length);

        int problemIndex = 0;

        Problem problem = problems[problemIndex++];
        assertNotNull(problem);
        assertEquals("Expected problem name ", "APL", problem.getDisplayName());
        assertEquals("Expected default timeout ",  120, problem.getTimeOutInSeconds());

        assertJudgementTypes(problem, true, false, false);

        problem = problems[problemIndex++];
        assertEquals("Expected problem name ", "barcodes", problem.getShortName());
        assertEquals("Expecting problem timeout for " + problem, 120, problem.getTimeOutInSeconds());

        assertJudgementTypes(problem, true, false, false);

        
    }
    
    
    /**
     * Test testFindCDPPaths.
     * 
     * @throws Exception
     */
    public void testFindCDPPaths() throws Exception {
        
        ContestSnakeYAMLLoader snake = new ContestSnakeYAMLLoader();

        String [] dirs = {

                "ccs1", //
                "ccs2", //
                "sumithello", //
// Doug's Local test directories
//                "c:\\test\\cdps\\sum1\\config\\contest.yaml", //
//                "/test/cdps/spring2015/config/contest.yaml", //
//                "c:\\test\\cdps\\spring2015\\config\\contest.yaml", //
//                "c:\\test\\cdps\\spring2015", //
        };
        
        for (String name : dirs) {
            
            File actual = snake.findCDPConfigDirectory(new File(name));
            
            if (actual == null){
                System.err.println("For "+name+" expected to find file "+snake.getSampleContesYaml(name));
                System.err.println("CWD is "+Utilities.getCurrentDirectory());
            }
            
            assertNotNull(actual);
            assertTrue("Is a config directory? ", actual.isDirectory());
//            System.out.println("For "+name+" found "+actual);
        }
    }
    
    /**
     * Test non-existent CDP directories.
     * 
     * @throws Exception
     */
    public void testFindCDPPathsNegatives() throws Exception {

        String[] dirs = { //
                "/home/pc2/bad", // 
                "/fargo", //
                "/tmp2", //
        };

        for (String name : dirs) {

            File actual = loader.findCDPConfigDirectory(new File(name));
//            System.out.println("For "+name+" found "+actual);
            
            assertNull(actual);
        }
    }
    
    public void testBeforeNow() throws Exception {

        Date date  = new Date();
        
        // Not before now, just after now.
        assertFalse ("Expected not to be before date "+date,snake.isBeforeNow(date));
        
        // very much after now 
        date.setTime(date.getTime() + 30000);
        assertTrue ("Expected not to be before date "+date,snake.isBeforeNow(date));

        // very much before now
        date.setTime(date.getTime() - 60000);
        assertFalse ("Expected not to be before date "+date,snake.isBeforeNow(date));
        
    }
    
    /**
     * Test data parsers.
     * 
     * @throws Exception
     */
    public void testDateParsers() throws Exception {

        ContestSnakeYAMLLoader snakeYAMLLoader = new ContestSnakeYAMLLoader();

        String startTime = "2016-10-28T18:00:00-07:00";
        Date d = snakeYAMLLoader.parseISO8601Date(startTime);

        long expected = 1477702800000L;
        assertEquals(expected, d.getTime());

        startTime = "2016-10-28 18:00";
        d = ContestSnakeYAMLLoader.parseSimpleDate(startTime);

        expected = 1477702800000L;
        assertEquals(expected, d.getTime());

    }
    
    /**
     * Test ISO Start time
     * Bug 1122 - Import contest yaml does not support start-time in ISO 8601 format
     * @throws Exception
     */
    
    public void testISO8601StartTime() throws Exception {

        String testDirectoryName = getDataDirectory(this.getName());
        ensureDirectory(testDirectoryName);
        // startExplorer(testDirectoryName);

        // String inputYamlFile = testDirectoryName + File.separator + IContestLoader.DEFAULT_CONTEST_YAML_FILENAME;
        // editFile(inputYamlFile);

        boolean loadDataFiles = false;

        IInternalContest contest = loader.fromYaml(null, testDirectoryName, loadDataFiles);

        ContestInformation info = contest.getContestInformation();
        assertNotNull(info);

        Date date = info.getScheduledStartDate();

        long expected = 1477713600000L;
        assertEquals(expected, date.getTime());
    }
    
    /**
     * Using problem key for problems unit test.
     * 
     * Bug 1177 - When loading yaml allow problems or problemset (key).
     * 
     * @throws Exception
     */
    public void testProblemsYamlKey() throws Exception {

        String[] yamlLines = { //
                IContestLoader.PROBLEMSET_PROBLEMS_KEY + ":", // test using problems: key
                "  - letter:     A", //
                "    short-name: apl", //
                "    color:      yellow", //
                "    rgb:        #ffff00", //
                "", //
                "  - letter:     B", //
                "    short-name: barcodes", //
                "    color:      red", //
                "    rgb:        #ff0000", //
                "    timeout:    1", //
        };

        Problem[] problems = loader.getProblems(yamlLines, 12, false, "{:validator}");

        assertEquals("Expecting same number of problems", 2, problems.length);

//        for (Problem problem : problems) {
//            System.out.println("problems = " + problem.getShortName());
//        }

        assertEquals("Problem short name apl", "apl", problems[0].getShortName());
        assertEquals("Problem short name barcode", "barcodes", problems[1].getShortName());
    }
    
    public void testProblemsYamlKeyFromFile() throws Exception {

        String dirname = getDataDirectory(this.getName());
//        ensureDirectory(dirname);
//        startExplorer(dirname);;
        
        String yamlFilename= dirname + File.separator + "contest.ps.yaml";
//        editFile(yamlFilename);

        String[] contents = Utilities.loadFile(yamlFilename);
        assertFileExists(yamlFilename);
        IInternalContest contest = loader.fromYaml(null, contents, dirname, false);
        assertNotNull(contest);

        Problem[] problems = contest.getProblems();

        assertEquals("Expecting same number of problems", 2, problems.length);

        assertEquals("Problem short name apl", "apl", problems[0].getShortName());
        assertEquals("Problem short name barcode", "barcodes", problems[1].getShortName());
    }
    
    /**
     * Test loading of 
     * @throws Exception
     */
    public void testCLICSJudgementOptionsLoad() throws Exception {
        
        String dir = getDataDirectory(this.getName());

        String problemShortName = "prob1";
//        ensureDirectory(dir);

//        String filename = dir + File.separator + problemShortName + File.separator +IContestLoader.DEFAULT_PROBLEM_YAML_FILENAME;
//        System.out.println("filename " + filename);
//        editFile(filename);

//        startExplorer(dir);
        String problemTitlte = "CLICS Options"; // note also set in problem.yaml
        Problem problem = new Problem(problemTitlte);
        problem.setShortName("BeforeLoad2");

        assertJudgementTypes(problem, false, false, false);

        IInternalContest contest = new InternalContest();

        problem.setShortName(problemShortName);

        boolean overrideUsePc2Validator = false;

        loader.loadProblemInformationAndDataFiles(contest, dir, problem, overrideUsePc2Validator);
        
        Problem problemDos = new Problem("Le Deux");
        problemDos.setShortName("prob2");
        
        loader.loadProblemInformationAndDataFiles(contest, dir, problemDos, overrideUsePc2Validator);

        Problem[] problems = contest.getProblems();

        assertEquals("Expecting number of problems ", 2, problems.length);

        Problem problem1 = problems[0];

        assertEquals("Problem One Title", problem1.getDisplayName());

        // validator_flags: float_tolerance 1e-6
        String expecting = "float_absolute_tolerance 1.0E-6 float_relative_tolerance 1.0E-6";
        
        assertEquals(expecting, problem1.getClicsValidatorSettings().toString());

        ClicsValidatorSettings settings = problem1.getClicsValidatorSettings();
        assertFalse("case_sensitive", settings.isCaseSensitive());
        assertTrue("float_absolute_tolerance specified", settings.isFloatAbsoluteToleranceSpecified());
        assertTrue("float_relative_tolerance", settings.isFloatRelativeToleranceSpecified());
        assertFalse("space_change_sensitive ", settings.isSpaceSensitive());
        
        assertEquals(1.0E-6, settings.getFloatAbsoluteTolerance());
        assertEquals(1.0E-6, settings.getFloatRelativeTolerance());
        

        Problem prob2 = problems[1];

        assertEquals("Two too to tutu", prob2.getDisplayName());

        // validator_flags: case_sensitive space_change_sensitive float_absolute_tolerance 4.0011122 float_relative_tolerance 4.0042354
        expecting = "case_sensitive space_change_sensitive float_absolute_tolerance 4.0011122 float_relative_tolerance 4.0042354";
        assertEquals(expecting, prob2.getClicsValidatorSettings().toString());

        settings = prob2.getClicsValidatorSettings();
        assertTrue("case_sensitive", settings.isCaseSensitive());
        assertTrue("float_absolute_tolerance specified", settings.isFloatAbsoluteToleranceSpecified());
        assertTrue("float_relative_tolerance", settings.isFloatRelativeToleranceSpecified());
        assertTrue("space_change_sensitive ", settings.isSpaceSensitive());
        
        assertEquals(4.0011122, settings.getFloatAbsoluteTolerance());
        assertEquals(4.0042354, settings.getFloatRelativeTolerance());
    }
    
    /**
     * Test load input validator command line and validator. 
     * 
     * @throws Exception
     */
    @Test
    public void testLoadInputValidator() throws Exception {
        
//        String inputYamlFilenameOld = getProblemSetYamlTestFileName();
//        editFile(inputYamlFilenameOld);
        
//        String datadir = getDataDirectory();
//        startExplorer(datadir);
        
        String inputDir = getDataDirectory(this.getName());
        ensureDirectory(inputDir);
//        startExplorer(inputDir);
        
//        short-name: apl
//        short-name: barcodes
//        short-name: biobots
//        short-name: castles
//        short-name: channel

        String configDir = inputDir + File.separator + IContestLoader.CONFIG_DIRNAME;
        
//        startExplorer(configDir);
        
        IInternalContest contest = loader.fromYaml(null, configDir);
        assertNotNull(contest);

        Problem[] problems = contest.getProblems();
        assertEquals("Number of problems", 5, problems.length);

        String[] validatorNames = { //
                //
                
                "", //
                "", //
                "", //
                "", //
                "", //
                "", //
        };
        
        String[] validatorCommandLine = {
                //
                "", //
                "", //
                "", //
                "", //
                "", //
                "", //
        };
        
//        String INPUT_VALIDATOR_NAME_KEY = "inputValidatorName";
//        String INPUT_VALIDATOR_COMMAND_LINE_KEY = "inputValidatorCommandLine";
        
        int idx = 0;
        for (Problem problem2 : problems) {
            assertNotNull("Expected problem short name ", problem2.getShortName());
            
            String expectedInputValidatorName = validatorNames[idx];
            String expectedInputValidatorCommandLine = validatorCommandLine[idx];
            idx ++;
           
            assertEquals("Expected input validator name ", expectedInputValidatorName, problem2.getInputValidatorProgramName());
            assertEquals("Expected input validator command ", expectedInputValidatorCommandLine, problem2.getInputValidatorCommandLine());
        }
    }
    
    public void testfindInputValidator() throws Exception {
        
        String dataDir = getDataDirectory(this.getName());
        ensureDirectory(dataDir);
        
        String shortDirName = "one";
        Problem problem = new Problem("Title 1");
        problem.setShortName(shortDirName);
        
        String inputFormatValidatorDir =  snake.getInputValidatorDir(dataDir, problem  ); 
        ensureDirectory(inputFormatValidatorDir);
//        startExplorer(inputFormatValidatorDir);
        
        String validatorProgramName = snake.findInputValidator(dataDir, problem);
        
        String expected = "testdata/ContestSnakeYAMLLoaderTest/testfindInputValidator/one/input_format_validators/one.sh";
        
        assertEquals("Expecting input dir name ", expected, toUnixFS(validatorProgramName));
        
    }

    /**
     * Load all sample contests/cdps.
     * 
     * 
     * 
     * @throws Throwable
     */
    public void testLoadAllSampleCDPS() throws Throwable {

        String[] contestDirs = getSampleContestsDirs();
        
        assertDirectoryExists(Utilities.getCurrentDirectory() + File.separator + getSampleContestsDirectory());

        assertTrue("Expecting at least one sample contest directory at " + getSampleContestsDirectory(), contestDirs.length > 0);

        for (String directoryName : contestDirs) {

            assertDirectoryExists(directoryName);
            try {
                IInternalContest contest = snake.fromYaml(null, directoryName + File.separator + IContestLoader.CONFIG_DIRNAME, false);

                Problem[] problems = contest.getProblems();
                assertTrue("Expecting at least one problem in contest in " + directoryName, problems.length > 0);

            } catch (YamlLoadException e) {
                System.err.println("Failed to load config from " + directoryName + " " + e.getCause().getMessage());
                throw e.getCause();
            }

        }

    }
    
    public void testSampleCDP() throws Throwable {
        
        String directoryName = getRootInputTestDataDirectory() + File.separator + "samplecdp";
        
        assertDirectoryExists(directoryName);
//        startExplorer(directoryName);
        
        IInternalContest contest;
        
        try {
            contest = snake.fromYaml(null, directoryName + File.separator + IContestLoader.CONFIG_DIRNAME, false);

            Problem[] problems = contest.getProblems();
            assertEquals("Expecting number of problems " + directoryName, 13, problems.length );

        } catch (YamlLoadException e) {
            System.err.println("Failed to load config from " + directoryName + " " + e.getCause().getMessage());
            throw e.getCause();
        }
        
        Vector<Account> accounts = contest.getAccounts(Type.TEAM);
        Collections.sort(accounts, new AccountComparator());
        
        int teamAccountNumber = 1;
        for (Account account : accounts) {
            assertEquals("Account number ", teamAccountNumber, account.getClientId().getClientNumber());
            teamAccountNumber++;
        }
    }
    
    
    /**
     * Test pc2 validator section: validator
     * @throws Exception
     */
    public void testpc2ValidatorSection() throws Exception {
        
        String [] section = {
                IContestLoader.VALIDATOR_KEY + ":", //
                "   validatorProg: pc2.jar edu.csus.ecs.pc2.validator.Validator", //
                "   validatorCmd: \"{:validator} {:infile} {:outfile} {:ansfile} {:resfile}  -pc2 1 true\"", //
                "   usingInternal: true", //
                "   validatorOption: 1", //
        };
        
        
        Map<String, Object> content = snake.loadYaml(null, section);

        Problem problem = createNewProblem(this.getName());
        snake.assignValidatorSettings(content, problem);
        
        assertEquals("validator type",  VALIDATOR_TYPE.PC2VALIDATOR, problem.getValidatorType());
        assertEquals("validator program name", Constants.PC2_VALIDATOR_NAME, problem.getValidatorProgramName());
    }
    

    /**
     * Test pc2 validator key: validator_flags
     * @throws Exception
     */
    public void test3pc2ValidatorFlags() throws Exception {
        
        String [] section = {
                IContestLoader.VALIDATOR_FLAGS_KEY + ": float_tolerance 1e-6", //
        };
        
        Map<String, Object> content = snake.loadYaml(null, section);

        Problem problem = createNewProblem(this.getName());
        snake.assignValidatorSettings(content, problem);
        
        assertEquals("validator type",  VALIDATOR_TYPE.CLICSVALIDATOR, problem.getValidatorType());
        assertEquals("validator program name", Constants.CLICS_VALIDATOR_NAME, problem.getValidatorProgramName());
        
        // huh
    }
    
    /**
     * Test CLICS validator key: validator
     * @throws Exception
     */
    public void testCLICSValidatorOptions() throws Exception {
        
        String [] section = {
                IContestLoader.VALIDATOR_KEY + ": float_tolerance 1e-6", //
        };
        
        Map<String, Object> content = snake.loadYaml(null, section);

        Problem problem = createNewProblem(this.getName());
        snake.assignValidatorSettings(content, problem);
        
        assertEquals("validator type",  VALIDATOR_TYPE.CLICSVALIDATOR, problem.getValidatorType());

        assertEquals("validator program name", Constants.CLICS_VALIDATOR_NAME, problem.getValidatorProgramName());
        
    }
    
    private Problem createNewProblem(String name) {
        Problem problem = new Problem(name);
        problem.setShortName(name);
        problem.setLetter("Z");
        return problem;
    }


    /**
     * 
     * @param directoryName
     * @param extension
     * @return
     */
    public String[] getFileNames(String directoryName, String extension) {

        ArrayList<String> list = new ArrayList<String>();
        File dir = new File(directoryName);

        String[] entries = dir.list();
        if (entries == null) {
            return new String[0];
        }
        Arrays.sort(entries);

        for (String name : entries) {
            if (name.endsWith(extension)) {
                list.add(name);
            }
        }

        return (String[]) list.toArray(new String[list.size()]);
    }

    public String[] getDirNames(String directoryName) {

        ArrayList<String> list = new ArrayList<String>();
        File dir = new File(directoryName);

        File[] files = dir.listFiles();
        if (files == null) {
            return new String[0];
        }
        Arrays.sort(files);
        for (File file : files) {
            if (file.isDirectory()) {
                list.add(directoryName + File.separator + file.getName());
            }
        }

        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * Directory names for sample contests.
     * @return list of directory names.
     */
    private String[] getSampleContestsDirs() {
        
        String sampContestDir = getSampleContestsDirectory();
        String[] sampContestDirNames = getDirNames(sampContestDir);
        return sampContestDirNames;
    }
    
    public void testCLICSLanguageLoad() throws Exception {
        
        String [] section = {
                //
                "languages:", //
                " - name: Python 2", //
                "   compiler: /usr/bin/python2", //
                "   compiler-args: -m py_compile {files}", //
                "   runner: /usr/bin/pypy", //
                "", //

        };
        
        IInternalContest contest = loader.fromYaml(null, section, null, false);
        Language[] languages = contest.getLanguages();
        
        assertEquals(1,languages.length);
        
    }
    
    /**
     * Test max-output-size-K.
     *  
     * Bug 1149.
     * 
     * @throws Exception
     */
    public void testImportMayFileSize() throws Exception {
        
        String [] section = {
                //
                "max-output-size-K:  128",
                "", //
        };
        
        IInternalContest contest = loader.fromYaml(null, section, null, false);
        
        ContestInformation info = contest.getContestInformation();
        assertNotNull("Expecting ContestInformation ", info);
        assertEquals("Expected max file size ",128000,info.getMaxFileSize());
    }

    
    /**
     * Test invalid max-output-size-K.
     *  
     * Bug 1149.
     * 
     * @throws Exception
     */
    public void testImportMayFileSizeErrorHandling() throws Exception {
        
        String [] data = //
            {
                "max-output-size-K:  0", //
                "max-output-size-K:  -21",
            };
        
        for (String line : data) {
            String [] section = { line };
            try {
                loader.fromYaml(null, section, null, false);
                System.err.println("Expecting exception when loading yaml line: "+line);
                fail("Expecting exception when loading yaml line: "+line);
            } catch (YamlLoadException e) {
                assertTrue("Expecting exception staring with phrase", e.getMessage().startsWith("Invalid max-output-size-K value"));
            }
        }
    }
    
    /**
     * Test load of input format validator using sumitMTC sample.
     * 
     * @throws Exception
     */
    public void testLoadInputFormatValidator() throws Exception {

        IInternalContest con = loadSampleContest(null, "sumitMTC");
        assertNotNull(con);

        // expecting input format validator defined

        Problem[] problems = con.getProblems();
        assertEquals("Problem couhnt ", 1, problems.length);
        
        String valiatorFileName = "valid.bat";

        String ifvfilename = "samps/contests/sumitMTC/config/sumit/input_format_validators/"+valiatorFileName;
        assertFileExists(ifvfilename);

        Problem p = problems[0];
        assertEquals("InValCmdLine", "cmd /c valid.bat", p.getInputValidatorCommandLine());

//        String expProgFileName = unixifyPath(Utilities.getCurrentDirectory() + File.separator + ifvfilename);
        String expProgFileName = unixifyPath(valiatorFileName);
        assertEquals("ProgName", expProgFileName, unixifyPath(p.getInputValidatorProgramName()));


        ProblemDataFiles pdf = con.getProblemDataFile(p);
        SerializedFile file = pdf.getInputValidatorFile();
        assertNotNull(" getInputValidatorFile ", file);

    }

    private String unixifyPath(String string) {
        return Utilities.unixifyPath(string);
    }

    private IInternalContest loadSampleContest(IInternalContest contest, String sampleName) throws Exception {
        String configDir = getTestSampleContestDirectory(sampleName) + File.separator + IContestLoader.CONFIG_DIRNAME;
        try {
            return loader.fromYaml(contest, configDir);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw e;
        }
    }

    private String getTestSampleContestDirectory(String dirname) {
        return getSampleContestsDirectory() + File.separator + dirname;
    }

    private String getSampleContestsDirectory() {
        return "samps" + File.separator + "contests";
    }

    private String toUnixFS(String path) {
        return path.replace('\\', '/');
    }
}

