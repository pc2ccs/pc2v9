package edu.csus.ecs.pc2.core.execute;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.logging.ConsoleHandler;

import junit.framework.TestSuite;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.LanguageAutoFill;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.imports.ccs.ContestYAMLLoader;
import edu.csus.ecs.pc2.validator.Validator;

/**
 * Test ExecutablePlugin class.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ExecutablePluginTest extends AbstractTestCase {

    public static final String DEFAULT_INTERNATIONAL_VALIDATOR_COMMAND = "{:validator} {:infile} {:outfile} {:ansfile} {:resfile} ";

    // private static int testNumber = 1;

    private IInternalContest contest;

    private IInternalController controller;

    private Problem sumitProblem = null;

    private Problem helloWorldProblem = null;

    private Language javaLanguage = null;

    private String yesJudgement = Validator.JUDGEMENT_YES;

    private Log log = null;

    public ExecutablePluginTest(String string) {
        super(string);
    }

    protected void setUp() throws Exception {
        super.setUp();
        SampleContest sampleContest = new SampleContest();
        contest = sampleContest.createContest(3, 2, 12, 12, true);
        
        ClientId clientId = contest.getClientId();
        if (clientId == null){
            clientId = getFirstJudge().getClientId();
                    
        }
        contest.setClientId(clientId);

        controller = sampleContest.createController(contest, true, false);
        log = controller.getLog();

//        setDebugMode(true);
        
        if (isDebugMode()){
            // this will make all log output go to stdout
            ConsoleHandler consoleHandler = new ConsoleHandler();
            log.addHandler(consoleHandler);
            log.setLevel(Log.DEBUG);
        }
        
        sumitProblem = createSumitProblem(contest);
        helloWorldProblem = createHelloProblem(contest);
        javaLanguage = createJavaLanguage(contest);

    }

    /**
     * Create a language using {@link LanguageAutoFill}.
     * 
     * @param autoFillLanguageTitle title for language from {@link LanguageAutoFill}.
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


    protected void setPC2Validator(Problem problem) {

        problem.setValidatedProblem(true);
        problem.setValidatorCommandLine(DEFAULT_INTERNATIONAL_VALIDATOR_COMMAND);

        problem.setUsingPC2Validator(true);
        problem.setWhichPC2Validator(1);
        problem.setIgnoreSpacesOnValidation(true);
        problem.setValidatorCommandLine(DEFAULT_INTERNATIONAL_VALIDATOR_COMMAND + " -pc2 " + problem.getWhichPC2Validator() + " "
                + problem.isIgnoreSpacesOnValidation());
        problem.setValidatorProgramName(Problem.INTERNAL_VALIDATOR_NAME);
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

        setPC2Validator(problem);

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

        setPC2Validator(problem);

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
        problem.setTimeOutInSeconds(4*60);
        contest2.addProblem(problem);

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

        problem.setTimeOutInSeconds(60*6);
        problem.setReadInputDataFromSTDIN(true);
        
        setPC2Validator(problem);

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
        return contest.getAccounts(type).lastElement();
    }

    public void testSumit() {

        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        Run run = createRun(submitter, javaLanguage, sumitProblem);
        RunFiles runFiles = new RunFiles(run, getSamplesSourceFilename("Sumit.java"));

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, yesJudgement);

    }
    
    public void testHello() {

        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        Run run = createRun(submitter, javaLanguage, helloWorldProblem);
        RunFiles runFiles = new RunFiles(run, getSamplesSourceFilename("hello.java"));

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, yesJudgement);

    }


    public void testLargeOutput() throws FileNotFoundException {

        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        Problem largeOutputProblem  = createLargeOutputProblem(contest);
        
        Run run = createRun(submitter, javaLanguage, largeOutputProblem);
        RunFiles runFiles = new RunFiles(run, getSamplesSourceFilename("LargeOutput.java"));

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, null);

    }

    public void testLargeStdIn() throws FileNotFoundException {

        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        Problem largeStdInProblem  = createLargeStdInProblem(contest);
        
        Run run = createRun(submitter, javaLanguage, largeStdInProblem);
        
        RunFiles runFiles = new RunFiles(run,  getRootInputTestFile("Casting.java"));

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, false, yesJudgement);
    }

    

    /**
     * Invoke a executable test.
     * @param run
     * @param runFiles
     * @param solved
     * @param expectedJudgement
     */
    protected void runExecutableTest(Run run, RunFiles runFiles, boolean solved, String expectedJudgement) {

        String executeDirectoryName = getOutputDataDirectory(getName());
        ensureDirectory(executeDirectoryName);
        
        ExecutablePlugin executablePlugin = new ExecutablePlugin(contest, controller, run, runFiles);
        executablePlugin.setExecuteDirectoryName(executeDirectoryName);
        executablePlugin.setUsingGUI(false);
        executablePlugin.execute();
        
//        startExplorer(executablePlugin.getExecuteDirectoryName());

        ExecutionData executionData = executablePlugin.getExecutionData();
        
//        dumpRunTestCases(run);

        // System.out.println("expectedJudgement  = " + expectedJudgement);
        // System.out.println("expectedJudgementV = " + executionData.getValidationResults());

        assertTrue("Excessive runtime", executionData.getExecuteTimeMS() < 40000);
        
//        if (!executionData.isCompileSuccess()){
//            SerializedFile file = executionData.getCompileStdout();
//            dumpFile("Compiler stdout", file);
//            file = executionData.getCompileStderr();
//            dumpFile("Compiler stderr", file);
//        }

        assertTrue("Compilation failure " + run.getLanguageId(), executionData.isCompileSuccess());
        assertTrue("Run not executed " + run.getProblemId(), executionData.isExecuteSucess());
        
        // If this test fails - there may not be a Validator in the path, check vstderr.pc2 for  
        // java.lang.NoClassDefFoundError: edu/csus/ecs/pc2/validator/Validator
        
        
        String jarPath = executablePlugin.findPC2JarPath();

        if (! new File(jarPath).isDirectory()){
            System.err.println("ERROR - pc2 jar path not a directory '"+jarPath+"'");
            System.out.println("TODO 636 - unable to unit test - testFindPC2Jar fails so no ability to judge run");
            fail("ERROR - pc2 jar path not a directory '"+jarPath+"'");
        } 
        else 
        {
            if (contest.getProblem(run.getProblemId()).isValidatedProblem()) {
                
                assertTrue("Expect no error running validator" , executionData.isValidationSuccess());
//                System.out.println("debug 22 executionData = "+toString(executionData));
                
                if (solved){
                    assertTrue("Expected to run to be a Yes " + run.getProblemId(), ExecuteUtilities.didTeamSolveProblem(executionData));
                }
            }

            if (solved){
                assertTrue("Judgement should be solved ", solved);
                assertEquals(expectedJudgement, executionData.getValidationResults());
            } else {
                assertFalse("Judgement should not be solved ", solved);
            }
        }
        
        executionData = null;
        executablePlugin = null;

    }
    
//    private void dumpRunTestCases(Run run) {
//        
//        RunTestCase[] cases = run.getRunTestCases();
//        
//        System.out.println("There are "+cases.length+" test cases.");
//        int number = 0;
//        for (RunTestCase testCase : cases) {
//            System.out.println("# " + number + " solved = " + testCase.isSolved() + " " + testCase.getRunElementId());
//            number++;
//        }
//    }

    public void startExplorer(String directoryName) {
        try {
            startExplorer(new File(directoryName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    private Account getFirstJudge() {
        Account[] accounts = new SampleContest().getJudgeAccounts(contest);
        Arrays.sort(accounts, new AccountComparator());
        return accounts[0];
        
    }

    protected void dumpFile(String message, SerializedFile file) {
        System.out.println(message);
        byte[] buf = file.getBuffer();
        System.out.println(buf.toString());
        
    }

    public void testFindPC2Jar() throws Exception {
        
        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        Run run = createRun (submitter, javaLanguage, sumitProblem);
        RunFiles runFiles = new RunFiles(run, getSamplesSourceFilename("Sumit.java"));

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        testFindPC2Jar(run,runFiles);
        
    }
    
    private Run createRun(ClientId submitter, Language language, Problem problem) {
        
        Run run = new Run(submitter, language, problem);
        run.setSiteNumber(contest.getSiteNumber());
        Long secs = new Long (new Date().getTime() % 100);
        run.setNumber(secs.intValue() +100);
        return run;
    }

    public void testFindPC2Jar(Run run, RunFiles runFiles) throws Exception {
        
        String executeDirectoryName = getOutputDataDirectory(getName());
        ensureDirectory(executeDirectoryName);
        
        ExecutablePlugin executable = new ExecutablePlugin(contest, controller, run, runFiles);
        executable.setExecuteDirectoryName(executeDirectoryName);
        executable.setUsingGUI(false);

        String jarPath = executable.findPC2JarPath();
        
        if (! new File(jarPath).isDirectory()){
            fail ("No such directory, using findPC2JarPath. path='"+jarPath+"'");
        }
        
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Override for executable directory.
     * 
     * Executable creates a directory based on execute site and client id, this class allows for an override of that execute
     * directory, especially for testing purposes with multi-threaded tests.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    class ExecutableOverride extends ExecutablePlugin {

        /**
         * 
         */
        private static final long serialVersionUID = -2654645310978611676L;
        /**
         **
         */
        private String executeDirectoryName = null;

        public ExecutableOverride(IInternalContest inContest, IInternalController inController, Run run, RunFiles runFiles,
                String executionDir) {
            super(inContest, inController, run, runFiles);
            setExecuteDirectoryName(executionDir);
        }

        @Override
        public String getExecuteDirectoryName() {
            return executeDirectoryName;
        }

        public void setExecuteDirectoryName(String directory) {
            this.executeDirectoryName = "execute" + directory;
        }
    }

    public void testStripSpace() throws Exception {
        String[] data = {
                // input,expected
//                "  cmd ,cmd", //
                "cmd foo boo,cmd foo boo", //
                "samps/src/sumit.dat    chew    ,execute;samps;src;sumit.dat    chew", //
                
        };

        for (String line : data) {
            String[] fields = line.split(",");
            String string = stripSpace(fields[0]);
            String expected = fields[0];
            if (expected.indexOf(';') > -1){
                expected = expected.replaceAll(";", File.separator);
            }
            
//            System.out.println("testStripSpace: '" + expected + "," + string + "'");
            
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
        Run run = createRun(submitter, language, helloWorldProblem);
        RunFiles runFiles = new RunFiles(run, getSamplesSourceFilename("hello.java"));

//        ExecutablePlugin executable = new ExecutablePlugin(contest, controller, run, runFiles);
        
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
        return "execute/"+string;
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

        setPC2Validator(problem);

        ProblemDataFiles problemDataFiles = new ProblemDataFiles(problem);

//        problem.setDataFileName("sumit.dat");
//        String judgesDataFile = getSamplesSourceFilename(problem.getDataFileName());
//        checkFileExistance(judgesDataFile);
//        problemDataFiles.setJudgesDataFile(new SerializedFile(judgesDataFile));

        problem.setAnswerFileName("hello.ans");
        String answerFileName = getSamplesSourceFilename(problem.getAnswerFileName());
        checkFileExistance(answerFileName);
        problemDataFiles.setJudgesAnswerFile(new SerializedFile(answerFileName));

        contest2.addProblem(problem, problemDataFiles);

        return problem;
    }
    
    /**
     * No judge data file specified
     * @throws Exception
     */
    public void testValidateMissingJudgesDataFile() throws Exception {
        
        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();
        
        Problem problem = createHelloProblemNoJudgesData(contest);
        
//        assertFalse("Expecting using internal data files ",problem.isUsingExternalDataFiles());

        Run run = createRun(submitter, javaLanguage, problem);
        String helloSourceFilename = getSamplesSourceFilename("hello.java");
        assertFileExists(helloSourceFilename);
        RunFiles runFiles = new RunFiles(run, helloSourceFilename);

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, yesJudgement);       
    }
    
    public static void dumpPath(PrintStream out) {
        
        Map<String, String> map = System.getenv();
        
        Set<String> keys = map.keySet();
        String [] names  = (String[]) keys.toArray(new String[keys.size()]);
        Arrays.sort(names);
        
        for (String name : names) {
            String value = map.get(name);
            out.println(name+"='"+value+"'");
        }
        
        String path = map.get("Path");
        for (String dirname : path.split(File.pathSeparator)) {
            out.println(" dir = "+dirname);
        }
        

    }

    public String which(String programName) {

        Map<String, String> map = System.getenv();

        String path = map.get("PATH");
        if (path != null){
            
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

        Problem problem = createMultiTestCaseProblem(contest);

        problem.setReadInputDataFromSTDIN(true);

        assertFalse("Expecting using internal data files ", problem.isUsingExternalDataFiles());

        Run run = createRun(submitter, javaLanguage, problem);

        assertFileExists(sumitFilename);
        RunFiles runFiles = new RunFiles(run, sumitFilename);

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, yesJudgement);

    }
    
    public void testMultipleTestCaseFromFile() throws Exception {

        String sumitFilename = getSamplesSourceFilename("Sumit.java");

        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        Problem problem = createMultiTestCaseProblem(contest);

        /**
         * These must be set, each data file is copied to these names
         * before execution.
         */
        problem.setDataFileName("sumit.dat"); // The team has been told to read from sumit.dat.
        problem.setAnswerFileName("sumit.ans");

        assertFalse("Expecting using internal data files ", problem.isUsingExternalDataFiles());

        Run run = createRun(submitter, javaLanguage, problem);

        assertFileExists(sumitFilename);
        RunFiles runFiles = new RunFiles(run, sumitFilename);

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, yesJudgement);

    }
    
    public void testMultipleTestCaseFailTest2() throws Exception {
        
        String sumitFilename = getSamplesSourceFilename("ISumit.java");

        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        Problem problem = createMultiTestCaseProblem(contest);

        problem.setReadInputDataFromSTDIN(true);

        assertFalse("Expecting using internal data files ", problem.isUsingExternalDataFiles());

        Run run = createRun(submitter, javaLanguage, problem);

        assertFileExists(sumitFilename);
        RunFiles runFiles = new RunFiles(run, sumitFilename);

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, false, yesJudgement);

    }

    /**
     * Create a multiple test case sumit problem.
     * 
     * @param contest2
     * @return
     * @throws IOException 
     */
    private Problem createMultiTestCaseProblem(IInternalContest contest2) throws IOException {
        Problem problem = new Problem("Sumit-Multidataset");

        problem.setShowValidationToJudges(false);
        problem.setHideOutputWindow(true);
        problem.setShowCompareWindow(false);
        problem.setTimeOutInSeconds(10);

        setPC2Validator(problem);

        ProblemDataFiles problemDataFiles = new ProblemDataFiles(problem);
        
        String testBaseDirname = getDataDirectory(this.getName());
        
        /**
         * 
         */
        String [] dataFileBaseNames = {
                "sumit1",
                "sumit2",
                "sumit3",
                "sumit4",
        };
        
        testBaseDirname = Utilities.getCurrentDirectory() + File.separator + testBaseDirname;
        
//        ensureDirectory(testBaseDirname);
//        startExplorer(new File(testBaseDirname));
        
        setDataFiles(problem, problemDataFiles,testBaseDirname, dataFileBaseNames);
        
        int numberJudgesFiles = problemDataFiles.getJudgesDataFiles().length;
        assertEquals("Expected number of judge data files ", dataFileBaseNames.length, numberJudgesFiles);

        int numberJudgesAnswerFiles = problemDataFiles.getJudgesAnswerFiles().length;
        assertEquals("Expected number of judge answer files ", dataFileBaseNames.length, numberJudgesAnswerFiles);

        contest2.addProblem(problem, problemDataFiles);
        
        return problem;

    }
    
    /**
     * Test multiple test cases from a CCS/problem.yaml defined problem.
     * @throws Exception
     */
    public void testExternalMultipleTestCaseFromSTDIN() throws Exception {

        String testBaseDirname = getDataDirectory(this.getName());
        
        String filename = testBaseDirname + "/arrow/submissions/accepted/Arrow.java";
        
        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

//        ensureDirectory(testBaseDirname);
//        startExplorer(new File(testBaseDirname));
        
        Problem problem = createExternalMultiTestCaseProblem("arrow", contest, 2);
        
        assertNotEquals("Expecting problem display name loaded",problem.getDisplayName(), problem.getShortName());

        problem.setReadInputDataFromSTDIN(true);
        
        problem.setShowValidationToJudges(false);
        problem.setHideOutputWindow(true);
        problem.setShowCompareWindow(false);
        problem.setTimeOutInSeconds(10);

        setPC2Validator(problem);

        assertFalse("Expecting using internal data files ", problem.isUsingExternalDataFiles());

        Run run = createRun(submitter, javaLanguage, problem);

        assertFileExists(filename);
        RunFiles runFiles = new RunFiles(run, filename);

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, yesJudgement);
                

        // Run twice more
        
        runExecutableTest(run, runFiles, true, yesJudgement);

        runExecutableTest(run, runFiles, true, yesJudgement);

    }
    
    
    /**
     * Create a multiple test case problem from problem.yaml.
     * 
     * @param contest2
     * @return
     * @throws IOException 
     */
    private Problem createExternalMultiTestCaseProblem(String problemShortName, IInternalContest contest2, int expectedDataSets) throws IOException {
        Problem problem = new Problem(problemShortName);
        problem.setShortName(problemShortName);

        ContestYAMLLoader loader = new ContestYAMLLoader();

        // where problem.yaml file is
        String testBaseDirname = getDataDirectory(this.getName());
        // testBaseDirname = Utilities.getCurrentDirectory() + File.separator + testBaseDirname;

//        ensureDirectory(testBaseDirname);
//        startExplorer(new File(testBaseDirname));

        loader.loadProblemInformationAndDataFiles(contest2, testBaseDirname, problem, false);

        ProblemDataFiles problemDataFiles = contest.getProblemDataFile(problem);

        int numberJudgesFiles = problemDataFiles.getJudgesDataFiles().length;
        assertEquals("Expected number of judge data files ", expectedDataSets, numberJudgesFiles);

        int numberJudgesAnswerFiles = problemDataFiles.getJudgesAnswerFiles().length;
        assertEquals("Expected number of judge answer files ", expectedDataSets, numberJudgesAnswerFiles);

        contest2.addProblem(problem, problemDataFiles);

        return problem;

    }
    
    private void setDataFiles(Problem problem, ProblemDataFiles problemDataFiles, String testBaseDirname, String[] dataFileBaseNames) {

 //      
//      for (String name : dataFileBaseNames) {
//          String inputFileName =  testBaseDirname + File.separator + name + ".in";
//          String ansFilename = testBaseDirname + File.separator + name + ".ans";
//          System.out.println(inputFileName);
//          System.out.println(ansFilename);
//
//      }
      
      ArrayList<SerializedFile> inList = new ArrayList<SerializedFile>();
      ArrayList<SerializedFile> ansList = new ArrayList<SerializedFile>();

      for (String name : dataFileBaseNames) {
          String inputFileName = testBaseDirname + File.separator + name + ".in";
          String ansFilename = testBaseDirname + File.separator + name + ".ans";

          assertFileExists(inputFileName);
          assertFileExists(ansFilename);
          
          SerializedFile inFile = new SerializedFile(inputFileName);
          inList.add(inFile);
          
          SerializedFile ansFile = new SerializedFile(ansFilename);
          ansList.add(ansFile);
      }
      
      
      SerializedFile[] inArray = (SerializedFile[]) inList.toArray(new SerializedFile[inList.size()]);
      problemDataFiles.setJudgesDataFiles(inArray);
      
      SerializedFile[] ansArray = (SerializedFile[]) ansList.toArray(new SerializedFile[ansList.size()]);
      problemDataFiles.setJudgesAnswerFiles(ansArray);
      
      problem.setDataFileName(inArray[0].getName());
      problem.setAnswerFileName(ansArray[0].getName());
      
    }

    /**
     * Uncomment this to run individual tests.
     * 
     * This only works under JUnit 3.
     */
    
    // rename metho to suite to run this test
//    public static TestSuite suite() {
    public static TestSuite suiteOne() {

        TestSuite suite = new TestSuite("ExecutablePlugin");
        String singletonTestName = null;
        singletonTestName = "testMultipleTestCaseFromSTDIN";
        singletonTestName = "testMultipleTestCaseFailTest2";
        singletonTestName = "testMultipleTestCaseFromFile";
        singletonTestName = "testExternalMultipleTestCaseFromSTDIN";
        
        suite.addTest(new ExecutablePluginTest(singletonTestName));
        return suite;

    }
    
    
    /**
     * Test Suite.
     * 
     * This only works under JUnit 3.
     * 
     * @return suite of tests.
     */
    // rename method to suite to run these tests
//    public static TestSuite suite() {
    public static TestSuite suiteTwo() {

        TestSuite suite = new TestSuite("ExecutablePlugin");

        String singletonTestName = "";
        //        singletonTestName = "testLanguageNameSub";
        //        singletonTestName = "testHello";
        //        singletonTestName = "testValidateMissingJudgesDataFile";


        if (!"".equals(singletonTestName)) {
            suite.addTest(new ExecutablePluginTest(singletonTestName));
        } else {

            String [] testNames = { //
                    "testSumit", //
                    "testHello", //
                    "testFindPC2Jar", //
                    "testStripSpace", //
                    "testLanguageNameSub", //
                    "testValidateMissingJudgesDataFile", //
                    "testMultipleTestCaseFromSTDIN", //
                    "testMultipleTestCaseFromFile", //
                    "testMultipleTestCaseFailTest2", //

                    "testLargeOutput", //
                    "testLargeStdIn", //
            };


            for (String testName : testNames) {
                suite.addTest(new ExecutablePluginTest(testName)); 
            }
        }

        return suite;
    }
}
