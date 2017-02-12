package edu.csus.ecs.pc2.core.execute;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.ConsoleHandler;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.LanguageAutoFill;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Problem.VALIDATOR_TYPE;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.validator.PC2ValidatorSettings;
import edu.csus.ecs.pc2.validator.Validator;

/**
 * Test ExecutableV9 class.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: ExecutableTest.java 2876 2014-11-19 03:02:18Z boudreat $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/test/edu/csus/ecs/pc2/core/execute/ExecutableTest.java $
public class ExecutableV9Test extends AbstractTestCase {

    // SOMDAY change to using Hello.java by fixing class name in Hello.java
    
    // private static int testNumber = 1;

    private IInternalContest contest;

    private IInternalController controller;

    private Problem sumitProblem = null;

    // SOMEDAY add test for hello
    private Problem helloWorldProblem = null;

    private Problem largeStdInProblem = null;
    
    private Language javaLanguage = null;

    private String yesJudgement = Validator.JUDGEMENT_YES;

    private Problem largeOutputProblem = null;

    public ExecutableV9Test(String string) {
        super(string);
    }

    protected void setUp() throws Exception {
        super.setUp();
        SampleContest sampleContest = new SampleContest();
        contest = sampleContest.createContest(2, 2, 12, 12, true);
        controller = sampleContest.createController(contest, true, false);

//        setDebugMode(true);
        
        if (isDebugMode()){
            // this will make all log output go to stdout
            ConsoleHandler consoleHandler = new ConsoleHandler();
            controller.getLog().addHandler(consoleHandler);
        }
        
        sumitProblem = createSumitProblem(contest);
        helloWorldProblem = createHelloProblem(contest);
        largeStdInProblem  = createLargeStdInProblem(contest);
        largeOutputProblem  = createLargeOutputProblem(contest);
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

        problem.setValidatorType(VALIDATOR_TYPE.PC2VALIDATOR);
        problem.setValidatorProgramName(Constants.PC2_VALIDATOR_NAME);
        problem.setValidatorCommandLine(Constants.DEFAULT_PC2_VALIDATOR_COMMAND);
        
        PC2ValidatorSettings settings = new PC2ValidatorSettings();
        settings.setWhichPC2Validator(1);
        settings.setIgnoreCaseOnValidation(true);
        settings.setValidatorCommandLine(Constants.DEFAULT_PC2_VALIDATOR_COMMAND + " -pc2 " + settings.getWhichPC2Validator() 
                + " " + settings.isIgnoreCaseOnValidation());
        settings.setValidatorProgramName(Constants.PC2_VALIDATOR_NAME);

        problem.setPC2ValidatorSettings(settings);
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

    public void testSumit() throws Exception {

        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        Run run = new Run(submitter, javaLanguage, sumitProblem);
        RunFiles runFiles = new RunFiles(run, getSamplesSourceFilename("Sumit.java"));

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, yesJudgement);

    }
    
    public void testHello() throws Exception {

        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        Run run = new Run(submitter, javaLanguage, helloWorldProblem);
        RunFiles runFiles = new RunFiles(run, getSamplesSourceFilename("hello.java"));

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, yesJudgement);

    }


    public void testLargeOutput() throws Exception {

        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        Run run = new Run(submitter, javaLanguage, largeOutputProblem);
        RunFiles runFiles = new RunFiles(run, getSamplesSourceFilename("LargeOutput.java"));

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, null);

    }

    public void testLargeStdIn() throws Exception {
        
        if (isFastJUnitTesting()){
            return;
        }

        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        Run run = new Run(submitter, javaLanguage, largeStdInProblem);
        
        RunFiles runFiles = new RunFiles(run,  getRootInputTestFile("Casting.java"));

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, yesJudgement);
    }


    /**
     * Invoke a executable test.
     * @param run
     * @param runFiles
     * @param solved
     * @param expectedJudgement
     * @throws Exception 
     */
    protected void runExecutableTest(Run run, RunFiles runFiles, boolean solved, String expectedJudgement) throws Exception {

        
        String executeDirectoryName = getOutputDataDirectory(getName());
        ensureDirectory(executeDirectoryName);
        
        ExecutableV9 executable = new ExecutableOverride(contest, controller, run, runFiles,executeDirectoryName);
        executable.setExecuteDirectoryName(executeDirectoryName);
        executable.setUsingGUI(false);
        executable.execute();

        ExecutionData executionData = executable.getExecutionData();

        // System.out.println("expectedJudgement  = " + expectedJudgement);
        // System.out.println("expectedJudgementV = " + executionData.getValidationResults());

        //TODO: change the following println into an assert()
//        System.err.println("Execute time for " + run.getProblemId() + " (ms): " + executionData.getExecuteTimeMS());
        // XXX TODO FIXME 40000 is too low for windows, but fine for linux
        assertTrue("Excessive runtime "+executionData.getExecuteTimeMS(), executionData.getExecuteTimeMS() < 40000);

        failIfException(executionData);
        
        assertTrue("Compile failed " + run.getProblemId(), executionData.isCompileSuccess());
        assertTrue("Run not executed " + run.getProblemId(), executionData.isExecuteSucess());
        
        // If this test fails - there may not be a Validator in the path, check vstderr.pc2 for  
        // java.lang.NoClassDefFoundError: edu/csus/ecs/pc2/validator/Validator
        
        
      String jarPath = ExecuteUtilities.findPC2JarPath();
      
      boolean isValidated = contest.getProblem(run.getProblemId()).isValidatedProblem();
      
      if (isValidated){
          
          assertNotEquals("pc2.jar (path to) not found", jarPath, ExecuteUtilities.DEFAULT_PC2_JAR_PATH);
          
      }
      
      if (isValidated){
          assertTrue("Expected run to be validated for problem " + run.getProblemId(), executionData.isValidationSuccess());
      }

      assertTrue("Judgement should be solved ", solved);
      assertEquals(expectedJudgement, executionData.getValidationResults());
      
      executionData = null;
      executable = null;
      
    }
    
    private void failIfException(ExecutionData executionData) throws Exception {
        if (executionData.getExecutionException() != null){
            throw executionData.getExecutionException();
        }
        
    }

    public void testFindPC2Jar() throws Exception {
        
        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        Run run = new Run(submitter, javaLanguage, sumitProblem);
        RunFiles runFiles = new RunFiles(run, getSamplesSourceFilename("Sumit.java"));

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        testFindPC2Jar(run,runFiles);
        
    }
    
    public void testFindPC2Jar(Run run, RunFiles runFiles) throws Exception {
        
        // TODO 636 fails unit test when findPC2JarPath fixed uncomment these lines
//        String executeDirectoryName = getOutputDataDirectory(getName());
//        ensureDirectory(executeDirectoryName);
//        
//        ExecutableV9 executable = new ExecutableV9(contest, controller, run, runFiles);
//        executable.setExecuteDirectoryName(executeDirectoryName);
//        executable.setUsingGUI(false);
//
//        String jarPath = executable.findPC2JarPath();
//        
//        if (! new File(jarPath).isDirectory()){
//            fail ("No such directory, using findPC2JarPath. path='"+jarPath+"'");
//        }
        
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * ExecutableV9 class with override for executable directory.
     * 
     * ExecutableV9 creates a directory based on execute site and client id, this class allows for an override of that execute
     * directory, especially for testing purposes with multi-threaded tests.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id: ExecutableTest.java 2876 2014-11-19 03:02:18Z boudreat $
     */

    // $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/test/edu/csus/ecs/pc2/core/execute/ExecutableTest.java $
    class ExecutableOverride extends ExecutableV9 {

        /**
         * 
         */
        private static final long serialVersionUID = -2626038377310038069L;
        private String executeDirectoryName = null;

        ExecutableOverride(IInternalContest inContest, IInternalController inController, Run run, RunFiles runFiles,
                String executionDir) {
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
        Run run = new Run(submitter, language, helloWorldProblem);
        RunFiles runFiles = new RunFiles(run, getSamplesSourceFilename("hello.java"));

//        ExecutableV9 executable = new ExecutableV9(contest, controller, run, runFiles);
        
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
     * Bug TODO - no judge data file specified
     * @throws Exception
     */
    public void testValidateMissingJudgesDataFile() throws Exception {
        
        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();
        
        Problem problem = createHelloProblemNoJudgesData(contest);
        
//        assertFalse("Expecting using internal data files ",problem.isUsingExternalDataFiles());

        Run run = new Run(submitter, javaLanguage, problem);
        String helloSourceFilename = getSamplesSourceFilename("hello.java");
        assertFileExists(helloSourceFilename);
        RunFiles runFiles = new RunFiles(run, helloSourceFilename);

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, yesJudgement);       
    }
    
    
    /**
     * Test Suite.
     * 
     * This only works under JUnit 3.
     * 
     * @return suite of tests.
     */
//    public static TestSuite suite() {
//
//        TestSuite suite = new TestSuite("ExecutableV9");
//
//        String singletonTestName = "";
////        singletonTestName = "testLanguageNameSub";
////        singletonTestName = "testHello";
////        singletonTestName = "testValidateMissingJudgesDataFile";
//      
//
//        if (!"".equals(singletonTestName)) {
//            suite.addTest(new ExecutableTest(singletonTestName));
//        } else {
//            
//            suite.addTest(new ExecutableTest("testLanguageNameSub"));
//            suite.addTest(new ExecutableTest("testFindPC2Jar"));
//            suite.addTest(new ExecutableTest("testSumit"));
//            suite.addTest(new ExecutableTest("testStripSpace"));
//            suite.addTest(new ExecutableTest("testValidateMissingJudgesDataFile"));
//            
//            // large tests last
//            suite.addTest(new ExecutableTest("testLargeOutput"));
//            suite.addTest(new ExecutableTest("testLargeStdIn"));
//            
//        }
//        return suite;
//    }

}
