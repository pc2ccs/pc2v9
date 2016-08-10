package edu.csus.ecs.pc2.imports.ccs;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

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

/**
 * Unit tests.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class ContestSnakeYAMLLoaderTest extends AbstractTestCase {

    private static final String TEST_CLASS_NAME = "IContestLoader";

    private IContestLoader loader = new ContestSnakeYAMLLoader();

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
//        ensureDirectory(dir);
        
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
        assertEquals("Fri Feb 04 01:23:00 PST 2011", info.getStartDate().toString());
        
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
        assertEquals("Fri Feb 04 01:23:00 PST 2011", info.getStartDate().toString());
        
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

        ContestSnakeYAMLLoader snake = new ContestSnakeYAMLLoader();
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

    // TODO add test testdoNotLoadExternalFile ??

    // public void testdoNotLoadExternalFile() throws Exception {
    //
    // String dirname = getDataDirectory(getName());
    // // String dirname = getDataDirectory("testValidatorKeys");
    // // String dirname = getDataDirectory();
    // Utilities.insureDir(dirname);
    // assertDirectoryExists(dirname);
    //
    // String filename = dirname + File.separator + IContestLoader.DEFAULT_CONTEST_YAML_FILENAME;
    // // System.out.println(filename);
    // assertFileExists(filename);
    //
    // IInternalContest contest;
    //
    // // Load data files
    // contest = loader.fromYaml(null, dirname, true);
    //
    // for (Problem problem : contest.getProblems()) {
    // assertTrue(problem.isUsingExternalDataFiles());
    // assertNotNull(problem.getExternalDataFileLocation());
    // ProblemDataFiles dataFiles = contest.getProblemDataFile(problem);
    // assertEquals("Expecting loaded answer files ", 12, dataFiles.getJudgesAnswerFiles().length);
    // assertEquals("Expecting loaded data files ", 12, dataFiles.getJudgesDataFiles().length);
    // }
    //
    // // Do not load data files
    // contest = loader.fromYaml(null, dirname, false);
    //
    // for (Problem problem : contest.getProblems()) {
    // assertFalse("Expecting false using data files", problem.isUsingExternalDataFiles());
    // assertNotNull(problem.getExternalDataFileLocation());
    // ProblemDataFiles dataFiles = contest.getProblemDataFile(problem);
    // assertEquals("Expecting no loaded answer files ", 0, dataFiles.getJudgesAnswerFiles().length);
    // }
    // }

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
        // TODO check and remove return getTestFilename(dirname + File.separator + IContest.CONTEST_FILENAME);
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
        // TODO check and remove
        // return getTestFilename(ExportYAML.PROBLEM_SET_FILENAME);
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

    public void testYamlWriteAndLoad() throws Exception {

        String testDirectory = getOutputDataDirectory("testYamlWriteAndLoad");
        ensureDirectory(testDirectory);

        IInternalContest originalContest = sampleContest.createContest(3, 3, 12, 5, true);
        Problem[] problems = originalContest.getProblems();
        Problem problem = problems[0];
        sampleContest.setCCSValidation(originalContest, null, problem);
        ElementId problemId = problem.getElementId();

        // ExportYAML2 exportYAML = new ExportYAML2();
        ExportYAML exportYAML = new ExportYAML();

        // TODO fix exportFiles to export properly Python spaced YAML.
        exportYAML.exportFiles(testDirectory, originalContest);

        // String filename = testDirectory + File.separator + IContestLoader.DEFAULT_CONTEST_YAML_FILENAME;
        // editFile(filename);

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

        assertEquals("Expected validator name ", CCSConstants.INTERNAL_CCS_VALIDATOR_NAME, problem.getValidatorProgramName());
        assertEquals("Expected validator command ", CCSConstants.DEFAULT_CCS_VALIDATOR_COMMAND, problem.getValidatorCommandLine());

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

    /**
     * The list of the tests to run/test.
     * 
     * JUnit3 only use.
     * 
     * @return list of classes to test.
     */

    public static TestSuite SuiteNotUsed() {
        // public static TestSuite suite() {
        /**
         * This is a way to test a single test method using JUnit3
         */

        TestSuite suite = new TestSuite(TEST_CLASS_NAME);

        String singletonTestName = "";
        // singletonTestName = "testYamlWriteAndLoad";
        ;
        singletonTestName = "testLoadLanguages";
        singletonTestName = "testLoader";

        if (!"".equals(singletonTestName)) {
            suite.addTest(new ContestSnakeYAMLLoaderTest(singletonTestName));
        } else {
            suite.addTest(new ContestSnakeYAMLLoaderTest("testGetTitle"));
            suite.addTest(new ContestSnakeYAMLLoaderTest("testLoaderMethods"));
            suite.addTest(new ContestSnakeYAMLLoaderTest("testLoader"));
            suite.addTest(new ContestSnakeYAMLLoaderTest("testProblemLoader"));
            suite.addTest(new ContestSnakeYAMLLoaderTest("testLoadClarCategories"));
            suite.addTest(new ContestSnakeYAMLLoaderTest("testLoadSites"));
            suite.addTest(new ContestSnakeYAMLLoaderTest("testGeneralAnswsers"));
            suite.addTest(new ContestSnakeYAMLLoaderTest("testgetSectionLines"));
            suite.addTest(new ContestSnakeYAMLLoaderTest("testgetFileNames"));
            suite.addTest(new ContestSnakeYAMLLoaderTest("testgetProblemsFromLetters"));
            suite.addTest(new ContestSnakeYAMLLoaderTest("testgetAutoJudgeSettings"));
            suite.addTest(new ContestSnakeYAMLLoaderTest("testAutoJudgeSettingsTwo"));
            suite.addTest(new ContestSnakeYAMLLoaderTest("testAutoJudgeSettingsAll"));
            suite.addTest(new ContestSnakeYAMLLoaderTest("testAllJudges"));
            suite.addTest(new ContestSnakeYAMLLoaderTest("testReplayLoad"));
            suite.addTest(new ContestSnakeYAMLLoaderTest("testLatextProblem"));
            suite.addTest(new ContestSnakeYAMLLoaderTest("testGetBooleanValue"));
            suite.addTest(new ContestSnakeYAMLLoaderTest("testValidatorKeys"));
            suite.addTest(new ContestSnakeYAMLLoaderTest("testMultipleDataSets"));
            suite.addTest(new ContestSnakeYAMLLoaderTest("atestIncludeFile"));
            suite.addTest(new ContestSnakeYAMLLoaderTest("testLoadProblemSet"));
        }
        return suite;
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
    public void testAUTO_JUDGE_KEY() {
        
        // SOMEDAY write unit test
        failIfInDebugMode();
    }

    /**
     * Test DEFAULT_VALIDATOR_KEY aka keyword default-validator
     */
    public void testDEFAULT_VALIDATOR_KEY() {
        // SOMEDAY write unit test
        failIfInDebugMode();
    }

    /**
     * Test INPUT_KEY aka keyword input
     */
    public void testINPUT_KEY() {
        // SOMEDAY write unit test
        failIfInDebugMode();
    }

    /**
     * Test JUDGE_EXECUTE_COMMAND_KEY aka keyword judge-exec-cmd
     */
    public void testJUDGE_EXECUTE_COMMAND_KEY() {
        // SOMEDAY write unit test
        failIfInDebugMode();
    }

    /**
     * Test OVERRIDE_VALIDATOR_KEY aka keyword override-validator
     */
    public void testOVERRIDE_VALIDATOR_KEY() {
        // SOMEDAY write unit test
        failIfInDebugMode();
    }

    /**
     * Test PROBLEM_INPUT_KEY aka keyword input
     */
    public void testPROBLEM_INPUT_KEY() {
        // SOMEDAY write unit test
        failIfInDebugMode();
    }

    /**
     * Test PROBLEM_LOAD_DATA_FILES_KEY aka keyword load-data-files
     */
    public void testPROBLEM_LOAD_DATA_FILES_KEY() {
        failIfInDebugMode();
    }

    /**
     * Test READ_FROM_STDIN_KEY aka keyword readFromSTDIN
     */
    public void testREAD_FROM_STDIN_KEY() {
        // SOMEDAY write unit test
        failIfInDebugMode();

    }

    /**
     * Test SEND_PRELIMINARY_JUDGEMENT_KEY aka keyword send-prelim-judgement
     */
    public void testSEND_PRELIMINARY_JUDGEMENT_KEY() {
        // SOMEDAY write unit test
        failIfInDebugMode();
    }

    /**
     * Test USE_JUDGE_COMMAND_KEY aka keyword use-judge-cmd
     */
    public void testUSE_JUDGE_COMMAND_KEY() {
        // SOMEDAY write unit test
        failIfInDebugMode();
    }

    /**
     * Test USING_PC2_VALIDATOR aka keyword use-internal-validator
     */
    public void testUSING_PC2_VALIDATOR() {
        // SOMEDAY write unit test
        failIfInDebugMode();
    }


    /**
     * Test VALIDATOR_KEY aka keyword validator
     */
    public void testVALIDATOR_KEY() {
        // SOMEDAY write unit test
        failIfInDebugMode();
    }


    private void failIfInDebugMode() {
        if(isDebugMode()){
            fail();
        }
    }
}
