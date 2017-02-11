package edu.csus.ecs.pc2.core.execute;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestSuite;
import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.DataLoader;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.LanguageAutoFill;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.RunTestCase;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.imports.ccs.ContestSnakeYAMLLoader;
import edu.csus.ecs.pc2.validator.Validator;

/**
 * Test Executable class.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ExecutableTest extends AbstractTestCase {

    // SOMDAY change to using Hello.java by fixing class name in Hello.java

    public static final String MOCK_VALIDATOR_NAME = "pc2.jar edu.csus.ecs.pc2.validator.MockValidator";

    // private static int testNumber = 1;

    private IInternalContest contest;

    private IInternalController controller;

    private Problem sumitProblem = null;

    // SOMEDAY add test for hello
    private Problem helloWorldProblem = null;

    private Language javaLanguage = null;

    private String yesJudgement = Validator.JUDGEMENT_YES;

    public ExecutableTest(String string) {
        super(string);
    }

    protected void setUp() throws Exception {
        super.setUp();

        // setDebugMode(true); // log to console, debug turned on

        SampleContest sampleContest = new SampleContest();
        contest = sampleContest.createContest(2, 2, 12, 12, true);

        ClientId clientId = contest.getClientId();
        if (clientId == null) {
            clientId = getFirstJudge().getClientId();

        }
        contest.setClientId(clientId);

        controller = sampleContest.createController(contest, true, false);

        if (isDebugMode()) {
            // this will make all log output go to stdout

//            addConsoleHandler(controller.getLog());
            setDebugLevel(controller.getLog());
        }

        sumitProblem = createSumitProblem(contest);
        helloWorldProblem = createHelloProblem(contest);
        javaLanguage = createJavaLanguage(contest);

    }

    /**
     * Returns number of failed data sets.
     * 
     * @param run
     * @return
     */
    private int getFailedTestCount(Run run) {
        int count = 0;
        RunTestCase[] cases = run.getRunTestCases();
        for (RunTestCase runTestCase : cases) {
            if (!runTestCase.isPassed()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns number of failed data sets.
     * 
     * @param run
     * @return
     */
    private int getPassedTestCount(Run run) {
        int count = 0;
        RunTestCase[] cases = run.getRunTestCases();
        for (RunTestCase runTestCase : cases) {
            if (runTestCase.isPassed()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Create a language using {@link LanguageAutoFill}.
     * 
     * @param autoFillLanguageTitle
     *            title for language from {@link LanguageAutoFill}.
     * @return
     */
    private Language createLanguage(String autoFillLanguageTitle) {

        String[] values = LanguageAutoFill.getAutoFillValues(autoFillLanguageTitle);
        Language language = new Language(values[4]);
        // displayNameTextField.setText(values[0]);
        // compileCommandLineTextField.setText(values[1]);
        language.setCompileCommandLine(values[1]);
        // executableFilenameTextField.setText(values[2]);
        language.setExecutableIdentifierMask(values[2]);
        // programExecutionCommandLineTextField.setText(values[3]);
        language.setProgramExecuteCommandLine(values[3]);

        return language;
    }

    private Language createJavaLanguage(IInternalContest contest2) {
        Language language = createLanguage(LanguageAutoFill.JAVATITLE);
        contest2.addLanguage(language);
        return language;
    }

    protected void checkFileExistance(String filename) throws FileNotFoundException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new FileNotFoundException("Could not find required file: " + filename);
        }
    }

    protected void setupUsingPC2Validator(Problem problem) {

        problem.setValidatedProblem(true);
        problem.setValidatorCommandLine(Constants.DEFAULT_PC2_VALIDATOR_COMMAND);

        problem.setUsingPC2Validator(true);

        assertTrue("Expecting using pc2 validator", problem.isUsingPC2Validator());

        problem.setWhichPC2Validator(1);
        problem.setIgnoreCaseOnValidation(true);
        problem.setValidatorCommandLine(Constants.DEFAULT_PC2_VALIDATOR_COMMAND + " -pc2 " + problem.getWhichPC2Validator() + " " + problem.isIgnoreCaseOnValidation());
        problem.setValidatorProgramName(Constants.INTERNAL_VALIDATOR_NAME);
    }
    
    protected void setupMockPC2Validator(Problem problem) {

        problem.setValidatedProblem(true);
        problem.setUsingPC2Validator(false);
        assertFalse("Not Expecting using pc2 validator", problem.isUsingPC2Validator());
        String mockValidatorCommandLine = "java {:validator} {:infile} {:outfile} {:ansfile} {:resfile} ";
        problem.setValidatorCommandLine(mockValidatorCommandLine + " -pc2 " + problem.getWhichPC2Validator() + " " + problem.isIgnoreCaseOnValidation());
        problem.setValidatorProgramName(MOCK_VALIDATOR_NAME);
    }


    /**
     * Create sample Hello world problem with validator add to contest.
     * 
     * @param contest2
     * @return
     * @throws FileNotFoundException
     */
    private Problem createHelloProblem(IInternalContest contest2) throws FileNotFoundException {

        Problem problem = new Problem("Hello world");
        problem.setAnswerFileName("hello.ans");

        problem.setShowValidationToJudges(false);
        problem.setHideOutputWindow(true);
        problem.setShowCompareWindow(false);

        setupUsingPC2Validator(problem);

        ProblemDataFiles problemDataFiles = new ProblemDataFiles(problem);
        String answerFileName = super.getSamplesSourceFilename(problem.getAnswerFileName());
        checkFileExistance(answerFileName);
        problemDataFiles.setJudgesAnswerFile(new SerializedFile(answerFileName));

        contest2.addProblem(problem, problemDataFiles);

        return problem;
    }

    /**
     * 
     * @param contest2
     * @return
     * @throws FileNotFoundException
     */
    private Problem createSumitProblem(IInternalContest contest2) throws FileNotFoundException {

        Problem problem = new Problem("Sumit");

        problem.setShowValidationToJudges(false);
        problem.setHideOutputWindow(true);
        problem.setShowCompareWindow(false);
        problem.setTimeOutInSeconds(10);

        setupUsingPC2Validator(problem);

        ProblemDataFiles problemDataFiles = new ProblemDataFiles(problem);

        problem.setDataFileName("sumit.dat");
        String judgesDataFile = getSamplesSourceFilename(problem.getDataFileName());
        checkFileExistance(judgesDataFile);
        problemDataFiles.setJudgesDataFile(new SerializedFile(judgesDataFile));

        problem.setAnswerFileName("sumit.ans");
        String answerFileName = getSamplesSourceFilename(problem.getAnswerFileName());
        checkFileExistance(answerFileName);
        problemDataFiles.setJudgesAnswerFile(new SerializedFile(answerFileName));

        contest2.addProblem(problem, problemDataFiles);

        return problem;
    }

    /**
     * Create sample LargeOutput problem and add to contest.
     * 
     * @param contest2
     * @return
     * @throws FileNotFoundException
     */
    private Problem createLargeOutputProblem(IInternalContest contest2) throws FileNotFoundException {

        // this is just a program that generate a lot of output (stdout & stderr)
        // so no input or answer files.
        Problem problem = new Problem("Large Output");

        problem.setShowValidationToJudges(false);
        problem.setHideOutputWindow(true);
        problem.setShowCompareWindow(false);
        problem.setTimeOutInSeconds(4 * 60);

        setupUsingPC2Validator(problem);

        ProblemDataFiles problemDataFiles = new ProblemDataFiles(problem);

        problem.setDataFileName("sumit.dat");
        String judgesDataFile = getSamplesSourceFilename(problem.getDataFileName());
        checkFileExistance(judgesDataFile);
        problemDataFiles.setJudgesDataFile(new SerializedFile(judgesDataFile));

        problem.setAnswerFileName("sumit.ans");
        String answerFileName = getSamplesSourceFilename(problem.getAnswerFileName());
        checkFileExistance(answerFileName);
        problemDataFiles.setJudgesAnswerFile(new SerializedFile(answerFileName));

        contest2.addProblem(problem, problemDataFiles);

        return problem;
    }

    /**
     * 
     * @param contest2
     * @return
     * @throws FileNotFoundException
     */
    private Problem createLargeStdInProblem(IInternalContest contest2) throws FileNotFoundException {

        Problem problem = new Problem("LargeStdIn");

        problem.setShowValidationToJudges(false);
        problem.setHideOutputWindow(true);
        problem.setShowCompareWindow(false);

        problem.setTimeOutInSeconds(60 * 6);
        problem.setReadInputDataFromSTDIN(true);

        setupUsingPC2Validator(problem);

        ProblemDataFiles problemDataFiles = new ProblemDataFiles(problem);

        problem.setDataFileName("casting.dat");
        String judgesDataFile = getRootInputTestFile(problem.getDataFileName());
        checkFileExistance(judgesDataFile);
        problemDataFiles.setJudgesDataFile(new SerializedFile(judgesDataFile));

        problem.setAnswerFileName("casting.ans");
        String answerFileName = getRootInputTestFile(problem.getAnswerFileName());
        checkFileExistance(answerFileName);
        problemDataFiles.setJudgesAnswerFile(new SerializedFile(answerFileName));

        contest2.addProblem(problem, problemDataFiles);

        return problem;
    }

    private Account getLastAccount(Type type) {
        return getLastAccount(contest, type);
    }

    private Account getLastAccount(IInternalContest inContest, Type type) {
        return inContest.getAccounts(type).lastElement();
    }

    public void testSumit() throws Exception {

        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        Run run = createRun(submitter, javaLanguage, sumitProblem, 42, 120);
        RunFiles runFiles = new RunFiles(run, getSamplesSourceFilename("Sumit.java"));

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, yesJudgement);

    }

    public void testHello() throws Exception {

        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        Run run = createRun(submitter, javaLanguage, helloWorldProblem, 42, 120);
        RunFiles runFiles = new RunFiles(run, getSamplesSourceFilename("hello.java"));

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, yesJudgement);

    }

    public void testLargeOutput() throws Exception {

        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        Problem largeOutputProblem = createLargeOutputProblem(contest);

        Run run = createRun(submitter, javaLanguage, largeOutputProblem, 42, 120);
        RunFiles runFiles = new RunFiles(run, getSamplesSourceFilename("LargeOutput.java"));

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());

        runExecutableTest(run, runFiles, false, null);

    }

    public void testLargeStdIn() throws Exception {

        if (isFastJUnitTesting()) {
            System.err.println("FastJUnitTesting set in ATC - skipping " + getName());
            return;
        }

        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        Problem largeStdInProblem = createLargeStdInProblem(contest);

        Run run = createRun(submitter, javaLanguage, largeStdInProblem, 42, 120);

        RunFiles runFiles = new RunFiles(run, getRootInputTestFile("Casting.java"));

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, false, yesJudgement);
    }

    protected Executable runExecutableTest(Run run, RunFiles runFiles, boolean solved, String expectedJudgement) throws Exception {
        return runExecutableTest(run, runFiles, solved, expectedJudgement, true);
    }

    /**
     * Invoke a executable test.
     * 
     * @param run
     * @param runFiles
     * @param solved
     *            expecting Yes judgement, else failed somewhere in compile/execute/validate.
     * @param expectedJudgement
     * @param allTestsShallPass
     * @throws Exception
     */
    protected Executable runExecutableTest(Run run, RunFiles runFiles, boolean solved, String expectedJudgement, boolean allTestsShallPass) throws Exception {

        String executeDirectoryName = getOutputDataDirectory(getName());
        ensureDirectory(executeDirectoryName);
//        startExplorer(executeDirectoryName);

        // this allows us to set the execute directory to be under testout vs executesiteXaccountY
        ExecutableOverride executable = new ExecutableOverride(contest, controller, run, runFiles, executeDirectoryName);
        executable.setUsingGUI(false);
        executable.execute();

        ExecutionData executionData = executable.getExecutionData();
        
        // TODO 917  dumpRunTestCases(run);

        // System.out.println("expectedJudgement  = " + expectedJudgement);
        // System.out.println("expectedJudgementV = " + executionData.getValidationResults());

        // TODO: change the following println into an assert()
        // System.err.println("Execute time for " + run.getProblemId() + " (ms): " + executionData.getExecuteTimeMS());
        // XXX TODO FIXME 40000 is too low for windows, but fine for linux
        assertTrue("Excessive runtime", executionData.getExecuteTimeMS() < 40000);

        if (!executionData.isCompileSuccess()) {
            SerializedFile file = executionData.getCompileStdout();
            dumpFile("Compiler stdout", file);
            file = executionData.getCompileStderr();
            dumpFile("Compiler stderr", file);
        }

        if (executionData.getExecutionException() != null) {
            throw executionData.getExecutionException();
        }

        assertTrue("Compilation failure " + run.getLanguageId(), executionData.isCompileSuccess());
        assertTrue("Run not executed " + run.getProblemId(), executionData.isExecuteSucess());

        // If this test fails - there may not be a Validator in the path, check vstderr.pc2 for
        // java.lang.NoClassDefFoundError: edu/csus/ecs/pc2/validator/Validator

        String jarPath = executable.findPC2JarPath();

        if (!new File(jarPath).isDirectory()) {
            System.err.println("ERROR - pc2 jar path not a directory '" + jarPath + "'");
            System.out.println("TODO 636 - unable to unit test - testFindPC2Jar fails so no ability to judge run");
            fail("ERROR - pc2 jar path not a directory '" + jarPath + "'");
        } else {
            if (contest.getProblem(run.getProblemId()).isValidatedProblem()) {

                if (solved) {

                    if (!executable.isValidationSuccess()) {

                        if (executionData.getExecutionException() != null) {
                            throw executionData.getExecutionException();
                        }
                    }

                    if (allTestsShallPass) {
                        assertTrue("Expecting run to pass all tests ", executionData.isValidationSuccess());
                        assertTrue("Expected to run to be a Yes " + run.getProblemId(), ExecuteUtilities.didTeamSolveProblem(executionData));
                    }
                }
            }

            if (solved) {
                assertTrue("Judgement should be solved ", solved);
                assertEquals(expectedJudgement, executionData.getValidationResults());
            } else {
                assertFalse("Judgement should not be solved ", solved);
                if (expectedJudgement != null) {
                    assertEquals("not solved:", expectedJudgement, executionData.getValidationResults());
                }
            }
        }

        if (isDebugMode()) {
            System.err.println("DEBUG IS TURNED ON - turn it off");
        }

        return executable;

    }

    // private void dumpRunTestCases(Run run) {
    //
    // RunTestCase[] cases = run.getRunTestCases();
    //
    // System.out.println("There are "+cases.length+" test cases.");
    // int number = 0;
    // for (RunTestCase testCase : cases) {
    // System.out.println("# " + number + " solved = " + testCase.isSolved() + " " + testCase.getRunElementId());
    // number++;
    // }
    // }

    public void dumpRunTestCases(Run run) {
        System.out.println("dumpRunTestCases "+run);
        RunTestCase[] runTestCases = run.getRunTestCases();
        int count = 1;
        for (RunTestCase runTestCase : runTestCases) {
            System.out.println("[" + count + "] is " + runTestCase.isPassed() + "'" + runTestCase.getJudgementId() + "'");
            count++;
        }
        System.out.println("There are " + runTestCases.length + " test cases ");
        
    }

    private Account getFirstJudge() {
        Account[] accounts = new SampleContest().getJudgeAccounts(contest);
        Arrays.sort(accounts, new AccountComparator());
        return accounts[0];

    }

    private void dumpFile(String message, SerializedFile file) {
        if (file != null) {
            System.out.println(message);
            byte[] buf = file.getBuffer();
            System.out.println(buf.toString());
        } else {
            new Exception("No file contents for file " + message).printStackTrace();
        }

    }

    public void testFindPC2Jar() throws Exception {

        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        Run run = createRun(submitter, javaLanguage, sumitProblem, 42, 120);
        RunFiles runFiles = new RunFiles(run, getSamplesSourceFilename("Sumit.java"));

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        testFindPC2Jar(run, runFiles);

    }

    public void testFindPC2Jar(Run run, RunFiles runFiles) throws Exception {

        String executeDirectoryName = getOutputDataDirectory(getName());
        ensureDirectory(executeDirectoryName);

        Executable executable = new Executable(contest, controller, run, runFiles);
        executable.setExecuteDirectoryName(executeDirectoryName);
        executable.setUsingGUI(false);

        String jarPath = executable.findPC2JarPath();

        if (!new File(jarPath).isDirectory()) {
            fail("No such directory, using findPC2JarPath. path='" + jarPath + "'");
        }

    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Executable class with override for executable directory.
     * 
     * Executable creates a directory based on execute site and client id, this class allows for an override of that execute directory, especially for testing purposes with multi-threaded tests.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    class ExecutableOverride extends Executable {

        /**
         * 
         */
        private static final long serialVersionUID = -6045865627706850495L;

        private String executeDirectoryName = null;

        ExecutableOverride(IInternalContest inContest, IInternalController inController, Run run, RunFiles runFiles, String executionDir) {
            super(inContest, inController, run, runFiles);
            setExecuteDirectoryName(executionDir);
        }

        @Override
        public String getExecuteDirectoryName() {
            return executeDirectoryName;
        }

        public void setExecuteDirectoryName(String directory) {
            this.executeDirectoryName = directory;
        }
    }

    public void testStripSpace() throws Exception {
        String[] data = {
                // input,expected
                // "  cmd ,cmd", //
                "cmd foo boo,cmd foo boo", //
                "samps/src/sumit.dat    chew    ,execute;samps;src;sumit.dat    chew", //

        };

        for (String line : data) {
            String[] fields = line.split(",");
            String string = stripSpace(fields[0]);
            String expected = fields[0];
            if (expected.indexOf(';') > -1) {
                expected = expected.replaceAll(";", File.separator);
            }

            // System.out.println("testStripSpace: '" + expected + "," + string + "'");

            assertEquals("Expected matching strings", expected, string);
        }

    }

    public void testLanguageNameSub() throws Exception {
        // test for bug 855
        String origString = "mtsv {:languagename} {:language}";
        String expected = "mtsv gnu_c++ 8";

        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();
        Language language = createLanguage(LanguageAutoFill.GNUCPPTITLE);
        contest.addLanguage(language);
        Run run = createRun(submitter, language, helloWorldProblem, 42, 120);
        RunFiles runFiles = new RunFiles(run, getSamplesSourceFilename("hello.java"));

        // Executable executable = new Executable(contest, controller, run, runFiles);

        Problem problem = null;
        ExecuteUtilities executeUtilities = new ExecuteUtilities(contest, controller, run, runFiles, problem, language);
        String actual = executeUtilities.substituteAllStrings(origString);
        assertEquals(expected, actual);
    }

    private String stripSpace(String cmdline) throws IOException {
        /**
         * Check for a space in the command line, if there is a space then
         */

        int i; // location of first space in command line.
        String actFilename = new String(cmdline);

        i = actFilename.trim().indexOf(" ");
        if (i > -1) {
            actFilename = prefixExecuteDirname(actFilename.trim().substring(0, i));
        } else {
            actFilename = prefixExecuteDirname(actFilename.trim());
        }

        File f = new File(actFilename);
        if (f.exists()) {
            cmdline = f.getCanonicalPath();
        }

        return cmdline;

    }

    private String prefixExecuteDirname(String string) {
        return "execute/" + string;
    }

    /**
     * 
     * @param contest2
     * @return
     * @throws FileNotFoundException
     */
    private Problem createHelloProblemNoJudgesData(IInternalContest contest2) throws FileNotFoundException {

        Problem problem = new Problem("Hello-NoJudgesData");

        problem.setShowValidationToJudges(false);
        problem.setHideOutputWindow(true);
        problem.setShowCompareWindow(false);
        problem.setTimeOutInSeconds(10);

        setupUsingPC2Validator(problem);

        ProblemDataFiles problemDataFiles = new ProblemDataFiles(problem);

        // problem.setDataFileName("sumit.dat");
        // String judgesDataFile = getSamplesSourceFilename(problem.getDataFileName());
        // checkFileExistance(judgesDataFile);
        // problemDataFiles.setJudgesDataFile(new SerializedFile(judgesDataFile));

        problem.setAnswerFileName("hello.ans");
        String answerFileName = getSamplesSourceFilename(problem.getAnswerFileName());
        checkFileExistance(answerFileName);
        problemDataFiles.setJudgesAnswerFile(new SerializedFile(answerFileName));

        contest2.addProblem(problem, problemDataFiles);

        return problem;
    }

    /**
     * Bug TODO - no judge data file specified
     * 
     * @throws Exception
     */
    public void testValidateMissingJudgesDataFile() throws Exception {

        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        Problem problem = createHelloProblemNoJudgesData(contest);

        // assertFalse("Expecting using internal data files ",problem.isUsingExternalDataFiles());

        Run run = createRun(submitter, javaLanguage, problem, 42, 120);
        String helloSourceFilename = getSamplesSourceFilename("hello.java");
        assertFileExists(helloSourceFilename);
        RunFiles runFiles = new RunFiles(run, helloSourceFilename);

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, yesJudgement);
    }

    public static void dumpPath(PrintStream out) {

        Map<String, String> map = System.getenv();

        Set<String> keys = map.keySet();
        String[] names = (String[]) keys.toArray(new String[keys.size()]);
        Arrays.sort(names);

        for (String name : names) {
            String value = map.get(name);
            out.println(name + "='" + value + "'");
        }

        String path = map.get("Path");
        for (String dirname : path.split(File.pathSeparator)) {
            out.println(" dir = " + dirname);
        }

    }

    public String which(String programName) {

        Map<String, String> map = System.getenv();

        String path = map.get("PATH");
        if (path != null) {

            for (String dirname : path.split(File.pathSeparator)) {
                String fullPath = dirname + File.separator + programName;
                if (new File(fullPath).isFile()) {
                    return fullPath;
                }
                fullPath = dirname + File.separator + programName + ".exe";
                if (new File(fullPath).isFile()) {
                    return fullPath;
                }
            }
        }
        return null;
    }

    public void testMultipleTestCaseFromSTDIN() throws Exception {

        String sumitFilename = getSamplesSourceFilename("ISumit.java");

        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        Problem problem = createMultiTestCaseProblem(contest, false);

        assertEquals("Expecting data files", 4, +problem.getNumberTestCases());

        problem.setReadInputDataFromSTDIN(true);

        assertFalse("Expecting using internal data files ", problem.isUsingExternalDataFiles());

        Run run = createRun(submitter, javaLanguage, problem, 42, 120);

        assertFileExists(sumitFilename);
        RunFiles runFiles = new RunFiles(run, sumitFilename);

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        /**
         * If this method failes with ERROR - pc2 jar path not a directory '/software/pc2/cc/projects/pc2v9/build/prod:' then one must create pc2.jar, one can use createVERSIONandJar.xml to create
         * pc2.jar.
         */
        Executable executable = runExecutableTest(run, runFiles, true, yesJudgement);

        List<String> list = executable.getTeamsOutputFilenames();
        assertEquals("Expecting output filenames ", problem.getNumberTestCases(), list.size());

    }

    public void testMultipleTestCaseFromFile() throws Exception {

        String sumitFilename = getSamplesSourceFilename("Sumit.java");

        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        Problem problem = createMultiTestCaseProblem(contest, false);

        problem.setDataFileName("sumit.dat");
        problem.setAnswerFileName("sumit.ans");

        assertFalse("Expecting using internal data files ", problem.isUsingExternalDataFiles());

        Run run = createRun(submitter, javaLanguage, problem, 42, 120);

        assertFileExists(sumitFilename);
        RunFiles runFiles = new RunFiles(run, sumitFilename);

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, yesJudgement);

    }

    public void testMultipleTestCaseExternalFile() throws Exception {

        // String testBaseDirname = getDataDirectory(this.getName());
        // ensureDirectory(testBaseDirname);
        // startExplorer(testBaseDirname);

        String sumitFilename = getSamplesSourceFilename("ISumit.java");

        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        Problem problem = createMultiTestCaseProblemExternalFiles(contest, "barcodes");

        problem.setDataFileName("sumit.in");
        problem.setAnswerFileName("sumit.ans");
        problem.setTimeOutInSeconds(15);
        problem.setReadInputDataFromSTDIN(true);

        assertTrue("Expecting all problem files external ", areDataFilesExternal(contest.getProblemDataFile(problem)));

        assertTrue("Expecting using external data files ", problem.isUsingExternalDataFiles());

        Run run = createRun(submitter, javaLanguage, problem, 45, 120);

        assertFileExists(sumitFilename);
        RunFiles runFiles = new RunFiles(run, sumitFilename);

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());

        runExecutableTest(run, runFiles, true, yesJudgement);
    }

    public void testMultipleTestCaseInternalFile() throws Exception {

        // String testBaseDirname = getDataDirectory(this.getName());
        // ensureDirectory(testBaseDirname);
        // startExplorer(testBaseDirname);

        String sumitFilename = getSamplesSourceFilename("ISumit.java");

        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        Problem problem = createMultiTestCaseProblemInternalFiles(contest, "barcodes");

        problem.setDataFileName("sumit.dat");
        problem.setAnswerFileName("sumit.ans");

        assertFalse("Expecting using internal data files ", problem.isUsingExternalDataFiles());

        assertFalse("Expecting all problem files internal ", areDataFilesExternal(contest.getProblemDataFile(problem)));

        Run run = createRun(submitter, javaLanguage, problem, 45, 120);

        assertFileExists(sumitFilename);
        RunFiles runFiles = new RunFiles(run, sumitFilename);

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());

        runExecutableTest(run, runFiles, true, yesJudgement);
    }

    private boolean areDataFilesExternal(ProblemDataFiles problemDataFile) {

        SerializedFile[] files = problemDataFile.getJudgesDataFiles();
        int totalfiles = 0;
        int externalFiles = 0;

        for (SerializedFile serializedFile : files) {
            if (serializedFile.isExternalFile()) {
                externalFiles++;
            }
            totalfiles++;
        }

        files = problemDataFile.getJudgesAnswerFiles();
        for (SerializedFile serializedFile : files) {
            if (serializedFile.isExternalFile()) {
                externalFiles++;
            }
            totalfiles++;
        }

        if (isDebugMode()) {
            System.out.println("debug areDataFilesExternal total = " + totalfiles + " external " + externalFiles);
        }

        return totalfiles > 0 && totalfiles == externalFiles;
    }

    private Run createRun(ClientId submitter, Language language, Problem problem, int runNumber, int elapsedMins) {

        Run run = new Run(submitter, language, problem);
        run.setNumber(runNumber);
        run.setElapsedMins(elapsedMins);
        return run;
    }

    public void testMultipleTestCaseFailTest2() throws Exception {

        String sumitFilename = getSamplesSourceFilename("ISumit.java");

        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        Problem problem = createMultiTestCaseProblem(contest, false);

        problem.setReadInputDataFromSTDIN(true);

        assertFalse("Expecting using internal data files ", problem.isUsingExternalDataFiles());

        Run run = new Run(submitter, javaLanguage, problem);

        assertFileExists(sumitFilename);
        RunFiles runFiles = new RunFiles(run, sumitFilename);

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, false, null);

    }

    private Problem createMultiTestCaseProblemExternalFiles(IInternalContest contest2, String problemName) throws IOException {
        return createMultiTestCaseProblemExternalFiles(contest2, problemName, 13);
    }

    public String getSecretDir(String methodName, String problemName) {
        return getDataDirectory(methodName) + File.separator + problemName + File.separator + "data" + File.separator + "secret";
    }

    public String getSecretDir(String methodName, Problem problem) {
        return getSecretDir(methodName, problem.getShortName());
    }

    private Problem createMultiTestCaseProblemExternalFiles(IInternalContest contest2, String problemName, int expectedTestCases) throws IOException {
        Problem problem = new Problem(problemName);

        problem.setShowValidationToJudges(false);
        problem.setHideOutputWindow(true);
        problem.setShowCompareWindow(false);
        problem.setTimeOutInSeconds(10);

        String directory = getSecretDir(this.getName(), problemName);
        directory = new File(directory).getCanonicalPath();

        setupUsingPC2Validator(problem);

        ProblemDataFiles problemDataFiles = new ProblemDataFiles(problem);
        setDataFiles(problem, problemDataFiles, directory, true);
        problem.setUsingExternalDataFiles(true);

        int numberJudgesFiles = problemDataFiles.getJudgesDataFiles().length;
        assertEquals("Expected number of judge data files ", expectedTestCases, numberJudgesFiles);

        int numberJudgesAnswerFiles = problemDataFiles.getJudgesAnswerFiles().length;
        assertEquals("Expected number of judge answer files ", expectedTestCases, numberJudgesAnswerFiles);

        contest2.addProblem(problem, problemDataFiles);

        return problem;

    }

    private Problem createMiddleFailure(IInternalContest contest2, String problemName) throws IOException {
        Problem problem = new Problem(problemName);

        problem.setShowValidationToJudges(false);
        problem.setHideOutputWindow(true);
        problem.setShowCompareWindow(false);
        problem.setTimeOutInSeconds(10);

        String testBaseDirname = getDataDirectory(this.getName());
        testBaseDirname = testBaseDirname + File.separator + problemName + File.separator + "data" + File.separator + "secret";
        testBaseDirname = new File(testBaseDirname).getCanonicalPath();
        setupUsingPC2Validator(problem);

        ProblemDataFiles problemDataFiles = new ProblemDataFiles(problem);
        setDataFiles(problem, problemDataFiles, testBaseDirname, true);
        problem.setUsingExternalDataFiles(true);

        int numberJudgesFiles = problemDataFiles.getJudgesDataFiles().length;
        assertEquals("Expected number of judge data files ", 7, numberJudgesFiles);

        int numberJudgesAnswerFiles = problemDataFiles.getJudgesAnswerFiles().length;
        assertEquals("Expected number of judge answer files ", 7, numberJudgesAnswerFiles);

        contest2.addProblem(problem, problemDataFiles);

        return problem;
    }

    private Problem createMultiTestCaseProblemInternalFiles(IInternalContest contest2, String problemName) throws IOException {
        Problem problem = new Problem(problemName);

        problem.setShowValidationToJudges(false);
        problem.setHideOutputWindow(true);
        problem.setShowCompareWindow(false);

        String testBaseDirname = getDataDirectory(this.getName());
        // ensureDirectory(testBaseDirname);
        // startExplorer(new File(testBaseDirname));

        testBaseDirname = testBaseDirname + File.separator + problemName;

        ContestSnakeYAMLLoader loader = new ContestSnakeYAMLLoader();
        loader.loadProblemInformationAndDataFiles(contest2, testBaseDirname, problem, false);

        // TODO loadProblemInformationAndDataFiles not setting pc2 validator flag
        setupUsingPC2Validator(problem);

        ProblemDataFiles problemDataFiles = contest2.getProblemDataFile(problem);

        int numberJudgesFiles = problemDataFiles.getJudgesDataFiles().length;
        assertEquals("Expected number of judge data files ", 4, numberJudgesFiles);

        int numberJudgesAnswerFiles = problemDataFiles.getJudgesAnswerFiles().length;
        assertEquals("Expected number of judge answer files ", 4, numberJudgesAnswerFiles);

        problem.setReadInputDataFromSTDIN(true);
        contest2.addProblem(problem, problemDataFiles);

        return problem;

    }

    /**
     * Create a multiple test case sumit problem.
     * 
     * @param contest2
     * @param externalFiles
     * @return
     * @throws IOException
     */
    private Problem createMultiTestCaseProblem(IInternalContest contest2, boolean externalFiles) throws IOException {
        Problem problem = new Problem("Sumit-Multidataset");

        problem.setShowValidationToJudges(false);
        problem.setHideOutputWindow(true);
        problem.setShowCompareWindow(false);
        problem.setTimeOutInSeconds(10);

        setupUsingPC2Validator(problem);

        ProblemDataFiles problemDataFiles = new ProblemDataFiles(problem);

        String testBaseDirname = getDataDirectory(this.getName());

        setDataFiles(problem, problemDataFiles, testBaseDirname, externalFiles);

        int numberJudgesFiles = problemDataFiles.getJudgesDataFiles().length;
        assertEquals("Expected number of judge data files ", 4, numberJudgesFiles);

        int numberJudgesAnswerFiles = problemDataFiles.getJudgesAnswerFiles().length;
        assertEquals("Expected number of judge answer files ", 4, numberJudgesAnswerFiles);

        contest2.addProblem(problem, problemDataFiles);

        return problem;

    }

    // private void setDataFiles(Problem problem, ProblemDataFiles problemDataFiles, String testBaseDirname, String[] dataFileBaseNames) {

    //
    // for (String name : dataFileBaseNames) {
    // String inputFileName = testBaseDirname + File.separator + name + ".in";
    // String ansFilename = testBaseDirname + File.separator + name + ".ans";
    // System.out.println(inputFileName);
    // System.out.println(ansFilename);
    //
    // }

    // ArrayList<SerializedFile> inList = new ArrayList<SerializedFile>();
    // ArrayList<SerializedFile> ansList = new ArrayList<SerializedFile>();
    //
    // for (String name : dataFileBaseNames) {
    // String inputFileName = testBaseDirname + File.separator + name + ".in";
    // String ansFilename = testBaseDirname + File.separator + name + ".ans";
    //
    // assertFileExists(inputFileName);
    // assertFileExists(ansFilename);
    //
    // SerializedFile inFile = new SerializedFile(inputFileName);
    // inList.add(inFile);
    //
    // SerializedFile ansFile = new SerializedFile(ansFilename);
    // ansList.add(ansFile);
    // }
    //
    //
    // SerializedFile[] inArray = (SerializedFile[]) inList.toArray(new SerializedFile[inList.size()]);
    // problemDataFiles.setJudgesDataFiles(inArray);
    //
    // SerializedFile[] ansArray = (SerializedFile[]) ansList.toArray(new SerializedFile[ansList.size()]);
    // problemDataFiles.setJudgesAnswerFiles(ansArray);

    // problem.setDataFileName(inArray[0].getName());
    // problem.setAnswerFileName(ansArray[0].getName());

    // }

    /**
     * Load problemdatafiles from directory
     * 
     * @param problem
     * @param problemDataFiles
     * @param dataFilesDirectory
     * @param externalFiles
     * @throws FileNotFoundException
     */
    private void setDataFiles(Problem problem, ProblemDataFiles problemDataFiles, String dataFilesDirectory, boolean externalFiles) throws FileNotFoundException {

        // System.out.println("data dir "+dataFilesDirectory);
        // ensureDirectory(dataFilesDirectory);
        //
        // try {
        // startExplorer(dataFilesDirectory);
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        /**
         * Load problemDataFiles from directory testBaseDirname
         */
        DataLoader.loadDataSets(problemDataFiles, dataFilesDirectory, externalFiles);

        if (externalFiles) {
            problem.setExternalDataFileLocation(dataFilesDirectory);
        }

        SerializedFile[] inArray = problemDataFiles.getJudgesDataFiles();
        SerializedFile[] ansArray = problemDataFiles.getJudgesAnswerFiles();
        for (int i = 0; i < ansArray.length; i++) {
            problem.addTestCaseFilenames(inArray[i].getName(), ansArray[i].getName());
        }

        problem.setDataFileName(inArray[0].getName());
        problem.setAnswerFileName(ansArray[0].getName());
    }

    /**
     * Test Suite.
     * 
     * This only works under JUnit 3.
     * 
     * @return suite of tests.
     */
    public static TestSuite suiteA() {

        TestSuite suite = new TestSuite("ExecutableTest");
        String singletonTestName = null;
        // singletonTestName = "testMultipleTestCaseFromSTDIN";
        // singletonTestName = "testMultipleTestCaseFailTest2";
        // singletonTestName = "testMultipleTestCaseFromFile";
        // singletonTestName = "testFindPC2Jar";
        singletonTestName = "testHello";
        singletonTestName = "testMultipleTestCaseFromInternalFile";
        singletonTestName = "testMultipleTestCaseFromExternalFile";

        suite.addTest(new ExecutableTest(singletonTestName));
        return suite;
    }

    /**
     * Submit hello world program for sumit problem.
     * 
     * @throws Exception
     */
    public void testValidationFalurue() throws Exception {

        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        Run run = createRun(submitter, javaLanguage, helloWorldProblem, 45, 1220);
        RunFiles runFiles = new RunFiles(run, getSamplesSourceFilename("hello.java"));

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, false, null);
    }

    /**
     * Test validation results for runs that fail validation.
     * 
     * @param run
     * @param executable
     * @param expectedPassingTestCases
     * @param expectedFailingTestCases
     * @param expectedValidatorResults
     */
    private void assertValidationFailure(Run run, Executable executable, int expectedPassingTestCases, int expectedFailingTestCases, String expectedValidatorResults) {

        int failures = getFailedTestCount(run);
        int passed = getPassedTestCount(run);

        assertEquals("Expected failed test cases", expectedFailingTestCases, failures);
        assertEquals("Expected passed test cases", expectedPassingTestCases, passed);

        ExecutionData data = executable.getExecutionData();
        String results = data.getValidationResults();

        assertEquals("Expected validator results string", expectedValidatorResults, results);

        // isValidationSuccess is a reflection of whether the validator program ran, not the actual result of validation

    }

    public void testMiddleFailure() throws Exception {
        String sumitFilename = getSamplesSourceFilename("wrap_failure.java");

        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        Problem problem = createMiddleFailure(contest, "wrap");

        problem.setDataFileName("wrapper.in");
        problem.setAnswerFileName("wrapper.ans");
        problem.setTimeOutInSeconds(30);
        problem.setReadInputDataFromSTDIN(true);

        Run run = createRun(submitter, javaLanguage, problem, 45, 120);

        assertFileExists(sumitFilename);
        RunFiles runFiles = new RunFiles(run, sumitFilename);

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());

        Executable executable = runExecutableTest(run, runFiles, false, "No - Wrong Answer");
        
        String noJudgement = "No - Wrong Answer";
        assertValidationFailure(run, executable, 1, 6, noJudgement);

    }
}
