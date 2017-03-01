package edu.csus.ecs.pc2.core.execute;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
import edu.csus.ecs.pc2.core.model.Problem.VALIDATOR_TYPE;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.RunTestCase;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.imports.ccs.ContestSnakeYAMLLoader;
import edu.csus.ecs.pc2.validator.ClicsValidator;
import edu.csus.ecs.pc2.validator.PC2ValidatorSettings;
import edu.csus.ecs.pc2.validator.PC2Validator;

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

    private static final String NL = System.getProperty("line.separator");

    // private static int testNumber = 1;

    private IInternalContest contest;

    private IInternalController controller;

    //a Sumit problem which reads data from a specified data file
    private Problem sumitProblem = null;

    //a Sumit problem which reads data from stdin
    private Problem iSumitProblem;
    
    //a Sumit problem which reads data from stdin and produces floating point output 
    private Problem iSumitFloatOutputProblem;
    
    // SOMEDAY add test for hello
    private Problem helloWorldProblem = null;

    private Language javaLanguage = null;

    private String pc2YesJudgement = PC2Validator.JUDGEMENT_YES;
    
    private String pc2NoJudgement = PC2Validator.JUDGEMENT_NO_WRONG_ANSWER;
    
    private String clicsYesJudgement = ClicsValidator.CLICS_CORRECT_ANSWER_MSG;
    
    private String clicsNoJudgement = ClicsValidator.CLICS_WRONG_ANSWER_MSG;
    
    private String clicsWrongOutputSpacingJudgement = ClicsValidator.CLICS_INCORRECT_OUTPUT_FORMAT_MSG;

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
        iSumitProblem = createISumitProblem(contest);
        iSumitFloatOutputProblem = createISumitFloatOutputProblem(contest);
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

        problem.setValidatorType(VALIDATOR_TYPE.PC2VALIDATOR);
        problem.setValidatorCommandLine(Constants.DEFAULT_PC2_VALIDATOR_COMMAND);
        problem.setValidatorProgramName(Constants.PC2_VALIDATOR_NAME);

        assertTrue("Expecting using pc2 validator", problem.isUsingPC2Validator());

        PC2ValidatorSettings settings = new PC2ValidatorSettings();
        settings.setWhichPC2Validator(1);
        settings.setIgnoreCaseOnValidation(true);
        settings.setValidatorCommandLine(Constants.DEFAULT_PC2_VALIDATOR_COMMAND + " -pc2 " + settings.getWhichPC2Validator() + " "
                + settings.isIgnoreCaseOnValidation());

        problem.setPC2ValidatorSettings(settings);
    }
    
    protected void setupMockPC2Validator(Problem problem) {

        problem.setValidatorType(VALIDATOR_TYPE.CUSTOMVALIDATOR);
        assertFalse("Not Expecting using pc2 validator", problem.isUsingPC2Validator());
        String mockValidatorCommandLine = "java {:validator} {:infile} {:outfile} {:ansfile} {:resfile} ";
        problem.setValidatorCommandLine(mockValidatorCommandLine + " -pc2 " + problem.getPC2ValidatorSettings().getWhichPC2Validator() 
                + " " + problem.getPC2ValidatorSettings().isIgnoreCaseOnValidation());
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
     * @see #createISumitProblem(IInternalContest)
     * @see #createISumitFloatOutputProblem(IInternalContest)
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
     * This method creates a new Problem named "ISumit" and adds it to the specified contest.
     * The newly-created problem is configured to use the PC2Validator.
     * The difference between this method and method createSumitProblem() is that the Problem created
     * by this method uses the "ISumit" program, which reads its data from stdin (the Problem
     * created by method createSumitProblem() is configured to read data from a file).
     * 
     * @param contest2 - the contest to which the Problem will be added
     * @return a new Problem (which has been added to the specified contest)
     * @throws FileNotFoundException if either the input data file "sumit.in" or the judge's 
     *          answer file "sumit.ans" cannot be found
     * @see #createISumitFloatOutputProblem(IInternalContest)
     */
    private Problem createISumitProblem(IInternalContest contest2) throws FileNotFoundException {

        Problem problem = new Problem("ISumit");

        problem.setShowValidationToJudges(false);
        problem.setHideOutputWindow(true);
        problem.setShowCompareWindow(false);
        problem.setTimeOutInSeconds(10);

        setupUsingPC2Validator(problem);
        
        problem.setReadInputDataFromSTDIN(true);

        ProblemDataFiles problemDataFiles = new ProblemDataFiles(problem);

        problem.setDataFileName("sumit.in");
        String judgesDataFile = getSamplesSourceFilename(problem.getDataFileName());
        checkFileExistance(judgesDataFile);
        problemDataFiles.setJudgesDataFile(new SerializedFile(judgesDataFile));

        problem.setAnswerFileName("sumit.ans");
        String answerFileName = getSamplesSourceFilename(problem.getAnswerFileName());
        checkFileExistance(answerFileName);
        answerFileName = convertEOLtoHostFormat(answerFileName);
        problemDataFiles.setJudgesAnswerFile(new SerializedFile(answerFileName));

        contest2.addProblem(problem, problemDataFiles);

        return problem;
    }

    /**
     * Produces a new file in the Test Data directory which has the same name as the specified
     * file but where the End-of-Line characters in the new file have been converted to the form
     * of the host OS.
     * 
     * @param originalFileName the original data file
     * @return the name of a new file with EOL converted
     */
    private String convertEOLtoHostFormat(String originalFileName) {
        File orig = new File(originalFileName);
        String newFilename=null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(orig));
            newFilename = getTestFilename(orig.getName()) ;
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(newFilename)));
            String line ;
            while ((line=br.readLine()) != null) {
                bw.write(line+NL);
            }
            br.close();
            bw.close();
            
        } catch (IOException e) {
            System.err.println ("IOException while converting input file '"+ originalFileName + "'");
            e.printStackTrace();
        }
        
        
        return newFilename;
    }

    /**
     * This method creates a new Problem named "ISumitFloatOutput" and adds it to the specified contest.
     * The newly-created problem is configured to use the PC2Validator.
     * The difference between this method and method createISumitProblem() is that the Problem created
     * by this method is configured to expect floating-point output.
     * 
     * @param contest2 - the contest to which the Problem will be added
     * @return a new Problem (which has been added to the specified contest)
     * @throws FileNotFoundException if either the input data file "sumit.in" or the judge's 
     *          answer file "sumitFloatOutput.ans" cannot be found
     */
    private Problem createISumitFloatOutputProblem(IInternalContest contest2) throws FileNotFoundException {

        Problem problem = new Problem("ISumitFloatOutput");

        problem.setShowValidationToJudges(false);
        problem.setHideOutputWindow(true);
        problem.setShowCompareWindow(false);
        problem.setTimeOutInSeconds(10);

        setupUsingPC2Validator(problem);
        
        problem.setReadInputDataFromSTDIN(true);

        ProblemDataFiles problemDataFiles = new ProblemDataFiles(problem);

        problem.setDataFileName("sumit.in");
        String judgesDataFile = getSamplesSourceFilename(problem.getDataFileName());
        checkFileExistance(judgesDataFile);
        problemDataFiles.setJudgesDataFile(new SerializedFile(judgesDataFile));

        problem.setAnswerFileName("sumitFloatOutput.ans");
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
        runExecutableTest(run, runFiles, true, pc2YesJudgement);

    }

    public void testHello() throws Exception {

        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        Run run = createRun(submitter, javaLanguage, helloWorldProblem, 42, 120);
        RunFiles runFiles = new RunFiles(run, getSamplesSourceFilename("hello.java"));

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, pc2YesJudgement);

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
        runExecutableTest(run, runFiles, false, pc2YesJudgement);
    }
    
    /**
     * Verifies that the PC2 Validator returns the correct results for all combinations
     * of the "ignoreCase" flag.  The combinations are:  a program which produces the
     * CORRECT case output, both with ignoreCase=true and ignoreCase=false  (both should succeed); 
     * and a program which produces INCORRECT case output, both with ignoreCase=true (should succeed)
     * and with ignoreCase=false (should fail). 
     * 
     * @throws Exception if an Exception occurs during the execution of the program (i.e., during invocation of Executable.execute())
     */
    public void testPC2ValidatorIgnoreCaseOption() throws Exception {
        
        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        //set the problem to ignore case and use straight "diff" on validation
        iSumitProblem.getPC2ValidatorSettings().setIgnoreCaseOnValidation(true);
        iSumitProblem.getPC2ValidatorSettings().setWhichPC2Validator(1);
        
        //submit ISumit, which should succeed
        Run run = createRun(submitter, javaLanguage, iSumitProblem, 42, 120);
        RunFiles runFiles = new RunFiles(run, getSamplesSourceFilename("ISumit.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, pc2YesJudgement);
        
        //set the problem to require case sensitivity and use straight "diff" on validation
        iSumitProblem.getPC2ValidatorSettings().setIgnoreCaseOnValidation(false);
        iSumitProblem.getPC2ValidatorSettings().setWhichPC2Validator(1);
        
        //submit ISumit, which should succeed
        run = createRun(submitter, javaLanguage, iSumitProblem, 43, 121);
        runFiles = new RunFiles(run, getSamplesSourceFilename("ISumit.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, pc2YesJudgement);
        
        //submit ISumitWrongCase, which should fail
        run = createRun(submitter, javaLanguage, iSumitProblem, 44, 122);
        runFiles = new RunFiles(run, getSamplesSourceFilename("ISumitWrongOutputCase.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, false, pc2NoJudgement );
        
        //set the problem to ignore case sensitivity and use straight "diff" on validation
        iSumitProblem.getPC2ValidatorSettings().setIgnoreCaseOnValidation(true);
        iSumitProblem.getPC2ValidatorSettings().setWhichPC2Validator(1);
        
        //submit ISumitWrongCase, which should succeed (because case is being ignored)
        run = createRun(submitter, javaLanguage, iSumitProblem, 45, 123);
        runFiles = new RunFiles(run, getSamplesSourceFilename("ISumitWrongOutputCase.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, pc2YesJudgement );
    }

    /**
     * Verifies that the PC2 Validator returns the correct results for all combinations
     * of the "ignore whitespace" option (that is, PC2Validator option 4).  
     * The combinations are:  a program which produces the
     * CORRECT spacing in its output, both with option 1 (require exact spacing, i.e. use "diff" (option 1))
     * and with option 4 (ignore whitespace in output)  (both should succeed); 
     * and a program which produces INCORRECT output spacing, both with option 1 (should fail)
     * and with option 4 (should succeed). 
     * 
     * @throws Exception if an Exception occurs during the execution of the program (i.e., during invocation of Executable.execute())
     */
    public void testPC2ValidatorIgnoreWhitespaceOption() throws Exception {
        
        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        //set the problem to use PC2 Validator with options "ignore case" and use straight "diff" on validation
        iSumitProblem.setValidatorType(VALIDATOR_TYPE.PC2VALIDATOR);
        iSumitProblem.getPC2ValidatorSettings().setIgnoreCaseOnValidation(true);
        iSumitProblem.getPC2ValidatorSettings().setWhichPC2Validator(1);
        
        //submit ISumit, which should succeed
        Run run = createRun(submitter, javaLanguage, iSumitProblem, 52, 130);
        RunFiles runFiles = new RunFiles(run, getSamplesSourceFilename("ISumit.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, pc2YesJudgement);
        
        //set the problem to ignore whitespace
        iSumitProblem.getPC2ValidatorSettings().setWhichPC2Validator(4);
        
        //submit ISumit, which should succeed
        run = createRun(submitter, javaLanguage, iSumitProblem, 53, 132);
        runFiles = new RunFiles(run, getSamplesSourceFilename("ISumit.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, pc2YesJudgement);
        
        //submit ISumitWrongOutputSpacing, which should succeed
        run = createRun(submitter, javaLanguage, iSumitProblem, 54, 133);
        runFiles = new RunFiles(run, getSamplesSourceFilename("ISumitWrongOutputSpacing.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, pc2YesJudgement );
        
        //set the problem to require whitespace to match 
        iSumitProblem.getPC2ValidatorSettings().setWhichPC2Validator(1);
        
        //submit ISumitWrongOutputSpacing, which should fail (because spacing match is being required)
        run = createRun(submitter, javaLanguage, iSumitProblem, 55, 134);
        runFiles = new RunFiles(run, getSamplesSourceFilename("ISumitWrongOutputSpacing.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, false, pc2NoJudgement );
    }
    
    /**
     * Verifies that the Clics Validator returns the correct results for all combinations
     * of the "case_sensitive" option.  
     * The combinations are:  a program which produces the
     * CORRECT case in its output, both with case-sensitivity off and on (both should succeed)
     * and a program which produces INCORRECT case in its output, both with case-sensitivity
     * off (should succeed) and with case-sensitivity on (should fail).
     * 
     * @throws Exception if an Exception occurs during the execution of the program (i.e., during invocation of Executable.execute())
     */
    public void testClicsValidatorCaseSensitiveOption() throws Exception {
        
        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        //set the problem to ignore case
        iSumitProblem.setValidatorType(VALIDATOR_TYPE.CLICSVALIDATOR);
        iSumitProblem.getClicsValidatorSettings().setCaseSensitive(false);
        
        //submit ISumit, which should succeed
        Run run = createRun(submitter, javaLanguage, iSumitProblem, 62, 140);
        RunFiles runFiles = new RunFiles(run, getSamplesSourceFilename("ISumit.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, clicsYesJudgement);
        
        //set the problem to require case match
        iSumitProblem.getClicsValidatorSettings().setCaseSensitive(true);
        
        //submit ISumit, which should succeed
        run = createRun(submitter, javaLanguage, iSumitProblem, 63, 141);
        runFiles = new RunFiles(run, getSamplesSourceFilename("ISumit.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, clicsYesJudgement);
        
        //submit ISumitWrongOutputCase, which should fail
        run = createRun(submitter, javaLanguage, iSumitProblem, 64, 142);
        runFiles = new RunFiles(run, getSamplesSourceFilename("ISumitWrongOutputCase.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, false, clicsNoJudgement );
        
        //set the problem to ignore case 
        iSumitProblem.getClicsValidatorSettings().setCaseSensitive(false);
        
        //submit ISumitWrongOutputCase, which should succeed (because case is being ignored)
        run = createRun(submitter, javaLanguage, iSumitProblem, 65, 143);
        runFiles = new RunFiles(run, getSamplesSourceFilename("ISumitWrongOutputCase.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, clicsYesJudgement );
    }
    
    /**
     * Verifies that the Clics Validator returns the correct results for all combinations
     * of the "space_sensitive" option.  
     * The combinations are:  a program which produces the
     * CORRECT spacing in its output, both with space-sensitivity off and on (both should succeed)
     * and a program which produces INCORRECT spacing in its output, both with space-sensitivity
     * off (should succeed) and with case-sensitivity on (should fail).
     * 
     * @throws Exception if an Exception occurs during the execution of the program (i.e., during invocation of Executable.execute())
     */
    public void testClicsValidatorSpaceSensitiveOption() throws Exception {
        
        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        //set the problem to ignore spacing
        iSumitProblem.setValidatorType(VALIDATOR_TYPE.CLICSVALIDATOR);
        iSumitProblem.getClicsValidatorSettings().setSpaceSensitive(false);
        
        //submit ISumit, which should succeed
        Run run = createRun(submitter, javaLanguage, iSumitProblem, 62, 140);
        RunFiles runFiles = new RunFiles(run, getSamplesSourceFilename("ISumit.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, clicsYesJudgement);
        
        //set the problem to require spacing match
        iSumitProblem.getClicsValidatorSettings().setSpaceSensitive(true);
        
        //submit ISumit, which should succeed
        run = createRun(submitter, javaLanguage, iSumitProblem, 63, 141);
        runFiles = new RunFiles(run, getSamplesSourceFilename("ISumit.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, clicsYesJudgement);
        
        //submit ISumitWrongOutputSpacing, which should fail
        run = createRun(submitter, javaLanguage, iSumitProblem, 64, 142);
        runFiles = new RunFiles(run, getSamplesSourceFilename("ISumitWrongOutputSpacing.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, false, clicsWrongOutputSpacingJudgement );
        
        //set the problem to ignore spacing 
        iSumitProblem.getClicsValidatorSettings().setSpaceSensitive(false);
        
        //submit ISumitWrongOutputSpacing, which should succeed (because spacing is being ignored)
        run = createRun(submitter, javaLanguage, iSumitProblem, 65, 143);
        runFiles = new RunFiles(run, getSamplesSourceFilename("ISumitWrongOutputSpacing.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, clicsYesJudgement );
    }
    
    /**
     * Verifies that the Clics Validator returns the correct results for all combinations
     * of the "Relative Tolerance" option. The combinations are:  
     * - a program that produces a floating-point output value that matches exactly the judge's answer when relative tolerance is disabled (should succeed)
     * - a program that produces a floating point value that doesn't match exactly the judge's answer when relative tolerance is disabled (should fail)
     * - a program that produces a floating-point value that matches exactly the judge's answer with relative tolerance enabled (should succeed regardless of tolerance value)
     * - a program that produces a floating-point value within the specified relative tolerance of the judge's answer (should succeed)
     * - a program that produces a floating-point value outside the specified relative tolerance of the judge's answer (should fail)
     * 
     * @throws Exception if an Exception occurs during the execution of the program (i.e., during invocation of Executable.execute())
     */
    public void testClicsValidatorRelativeToleranceOption() throws Exception {
        
        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        //set the problem state to ignore tolerances
        iSumitFloatOutputProblem.setValidatorType(VALIDATOR_TYPE.CLICSVALIDATOR);
        iSumitFloatOutputProblem.getClicsValidatorSettings().disableFloatRelativeTolerance();
        iSumitFloatOutputProblem.getClicsValidatorSettings().disableFloatAbsoluteTolerance();
        
        // submit a program that produces a floating-point output value that matches exactly the judge's answer 
        // when relative tolerance is disabled (should succeed)
        Run run = createRun(submitter, javaLanguage, iSumitFloatOutputProblem, 71, 171);
        RunFiles runFiles = new RunFiles(run, getSamplesSourceFilename("ISumitFloatOutput.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
//        System.out.println ("Running testClicsValidatorRelativeToleranceOption(): ISumitFloatOutput with relative tolerance disabled (should succeed)");
        runExecutableTest(run, runFiles, true, clicsYesJudgement);
        
        // submit a program that produces a floating point value that doesn't match exactly the judge's answer 
        // when relative tolerance is disabled (should fail)
        run = createRun(submitter, javaLanguage, iSumitFloatOutputProblem, 72, 172);
        runFiles = new RunFiles(run, getSamplesSourceFilename("ISumitFloatOutputTenUnitsOff.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
//        System.out.println ("Running testClicsValidatorRelativeToleranceOption(): ISumitFloatOutputTenUnitsOff with relative tolerance disabled (should fail)");
        runExecutableTest(run, runFiles, false, clicsNoJudgement);
        
        // submit a program that produces a floating-point value that matches exactly the judge's answer 
        // with relative tolerance enabled (should succeed regardless of tolerance value)
        iSumitFloatOutputProblem.getClicsValidatorSettings().setFloatRelativeTolerance(0.0);
        run = createRun(submitter, javaLanguage, iSumitFloatOutputProblem, 73, 173);
        runFiles = new RunFiles(run, getSamplesSourceFilename("ISumitFloatOutput.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
//        System.out.println ("Running testClicsValidatorRelativeToleranceOption(): ISumitFloatOutput with relative tolerance of zero (should succeed)");
        runExecutableTest(run, runFiles, true, clicsYesJudgement);
        
        // try the same thing with a wildly different relative tolerance value (should succeed)
        iSumitFloatOutputProblem.getClicsValidatorSettings().setFloatRelativeTolerance(10000.0);
        run = createRun(submitter, javaLanguage, iSumitFloatOutputProblem, 74, 174);
        runFiles = new RunFiles(run, getSamplesSourceFilename("ISumitFloatOutput.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
//        System.out.println ("Running testClicsValidatorRelativeToleranceOption(): ISumitFloatOutput with relative tolerance of 10000 (should succeed)");
        runExecutableTest(run, runFiles, true, clicsYesJudgement);
        
        // try the same thing with another wildly different relative tolerance value (should succeed)
        iSumitFloatOutputProblem.getClicsValidatorSettings().setFloatRelativeTolerance(-10000.0);
        run = createRun(submitter, javaLanguage, iSumitFloatOutputProblem, 75, 175);
        runFiles = new RunFiles(run, getSamplesSourceFilename("ISumitFloatOutput.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
//        System.out.println ("Running testClicsValidatorRelativeToleranceOption(): ISumitFloatOutput with relative tolerance of -10000 (should succeed)");
        runExecutableTest(run, runFiles, true, clicsYesJudgement);
        
        // submit a program that produces a floating-point value within the specified tolerance of the judge's answer (should succeed)
        iSumitFloatOutputProblem.getClicsValidatorSettings().setFloatRelativeTolerance(0.15);
        run = createRun(submitter, javaLanguage, iSumitFloatOutputProblem, 76, 176);
        runFiles = new RunFiles(run, getSamplesSourceFilename("ISumitFloatOutputTenPercentOff.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
//        System.out.println ("Running testClicsValidatorRelativeToleranceOption(): ISumitFloatOutputTenPercentOff with relative tolerance of 15% (should succeed)");
        runExecutableTest(run, runFiles, true, clicsYesJudgement);
        
        // submit a program that produces a floating-point value outside the specified tolerance of the judge's answer (should fail)
        iSumitFloatOutputProblem.getClicsValidatorSettings().setFloatRelativeTolerance(0.05);
        run = createRun(submitter, javaLanguage, iSumitFloatOutputProblem, 77, 177);
        runFiles = new RunFiles(run, getSamplesSourceFilename("ISumitFloatOutputTenPercentOff.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
//        System.out.println ("Running testClicsValidatorRelativeToleranceOption(): ISumitFloatOutputTenPercentOff with relative tolerance of 5% (should fail)");
        runExecutableTest(run, runFiles, false, clicsNoJudgement);
        
    }
    
    /**
     * Verifies that the Clics Validator returns the correct results for all combinations
     * of the "Absolute Tolerance" option. The combinations are:  
     * - a program that produces a floating-point output value that matches exactly the judge's answer when absolute tolerance is disabled (should succeed)
     * - a program that produces a floating point value that doesn't match exactly the judge's answer when absolute tolerance is disabled (should fail)
     * - a program that produces a floating-point value that matches exactly the judge's answer with absolute tolerance enabled (should succeed regardless of tolerance value)
     * - a program that produces a floating-point value within the specified absolute tolerance of the judge's answer (should succeed)
     * - a program that produces a floating-point value outside the specified absolute tolerance of the judge's answer (should fail)
     * 
     * @throws Exception if an Exception occurs during the execution of the program (i.e., during invocation of Executable.execute())
     */
    public void testClicsValidatorAbsoluteToleranceOption() throws Exception {
        
        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        //set the problem state to ignore tolerances
        iSumitFloatOutputProblem.setValidatorType(VALIDATOR_TYPE.CLICSVALIDATOR);
        iSumitFloatOutputProblem.getClicsValidatorSettings().disableFloatAbsoluteTolerance();
        iSumitFloatOutputProblem.getClicsValidatorSettings().disableFloatRelativeTolerance();
        
        // submit a program that produces a floating-point output value that matches exactly the judge's answer 
        // when absolute tolerance is disabled (should succeed)
        Run run = createRun(submitter, javaLanguage, iSumitFloatOutputProblem, 81, 181);
        RunFiles runFiles = new RunFiles(run, getSamplesSourceFilename("ISumitFloatOutput.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
//        System.out.println ("Running testClicsValidatorAbsoluteToleranceOption(): ISumitFloatOutput with absolute tolerance disabled (should succeed)");
        runExecutableTest(run, runFiles, true, clicsYesJudgement);
        
        // submit a program that produces a floating point value that doesn't match exactly the judge's answer 
        // when absolute tolerance is disabled (should fail)
        run = createRun(submitter, javaLanguage, iSumitFloatOutputProblem, 82, 182);
        runFiles = new RunFiles(run, getSamplesSourceFilename("ISumitFloatOutputTenUnitsOff.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
//        System.out.println ("Running testClicsValidatorAbsoluteToleranceOption(): ISumitFloatOutputTenUnitsOff with absolute tolerance disabled (should fail)");
        runExecutableTest(run, runFiles, false, clicsNoJudgement);
        
        // submit a program that produces a floating-point value that matches exactly the judge's answer 
        // with absolute tolerance enabled (should succeed regardless of tolerance value)
        iSumitFloatOutputProblem.getClicsValidatorSettings().setFloatAbsoluteTolerance(0.0);
        run = createRun(submitter, javaLanguage, iSumitFloatOutputProblem, 83, 183);
        runFiles = new RunFiles(run, getSamplesSourceFilename("ISumitFloatOutput.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
//        System.out.println ("Running testClicsValidatorAbsoluteToleranceOption(): ISumitFloatOutput with absolute tolerance of zero (should succeed)");
        runExecutableTest(run, runFiles, true, clicsYesJudgement);
        
        // try the same thing with a wildly different absolute tolerance value (should succeed)
        iSumitFloatOutputProblem.getClicsValidatorSettings().setFloatAbsoluteTolerance(10000.0);
        run = createRun(submitter, javaLanguage, iSumitFloatOutputProblem, 84, 184);
        runFiles = new RunFiles(run, getSamplesSourceFilename("ISumitFloatOutput.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
//        System.out.println ("Running testClicsValidatorAbsoluteToleranceOption(): ISumitFloatOutput with absolute tolerance of 10000 (should succeed)");
        runExecutableTest(run, runFiles, true, clicsYesJudgement);
        
        // try the same thing with another wildly different absolute tolerance value (should succeed)
        iSumitFloatOutputProblem.getClicsValidatorSettings().setFloatAbsoluteTolerance(-10000.0);
        run = createRun(submitter, javaLanguage, iSumitFloatOutputProblem, 85, 185);
        runFiles = new RunFiles(run, getSamplesSourceFilename("ISumitFloatOutput.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
//        System.out.println ("Running testClicsValidatorAbsoluteToleranceOption(): ISumitFloatOutput with absolute tolerance of -10000 (should succeed)");
        runExecutableTest(run, runFiles, true, clicsYesJudgement);
        
        // submit a program that produces a floating-point value within the specified absolute tolerance of the judge's answer (should succeed)
        iSumitFloatOutputProblem.getClicsValidatorSettings().setFloatAbsoluteTolerance(15.0);
        run = createRun(submitter, javaLanguage, iSumitFloatOutputProblem, 86, 186);
        runFiles = new RunFiles(run, getSamplesSourceFilename("ISumitFloatOutputTenUnitsOff.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
//        System.out.println ("Running testClicsValidatorAbsoluteToleranceOption(): ISumitFloatOutputTenUnitsOff with absolute tolerance of 15 (should succeed)");
        runExecutableTest(run, runFiles, true, clicsYesJudgement);
        
        // submit a program that produces a floating-point value outside the specified tolerance of the judge's answer (should fail)
        iSumitFloatOutputProblem.getClicsValidatorSettings().setFloatAbsoluteTolerance(5.0);
        run = createRun(submitter, javaLanguage, iSumitFloatOutputProblem, 87, 187);
        runFiles = new RunFiles(run, getSamplesSourceFilename("ISumitFloatOutputTenUnitsOff.java"));
        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
//        System.out.println ("Running testClicsValidatorAbsoluteToleranceOption(): ISumitFloatOutputTenUnitsOff with absolute tolerance of 5 (should fail)");
        runExecutableTest(run, runFiles, false, clicsNoJudgement);
        
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

                        System.out.println ("isValidationSuccess() returned false");
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
        runExecutableTest(run, runFiles, true, pc2YesJudgement);
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

        if (isFastJUnitTesting()){
            return;
        }

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
        Executable executable = runExecutableTest(run, runFiles, true, pc2YesJudgement);

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
        runExecutableTest(run, runFiles, true, pc2YesJudgement);

    }

    public void testMultipleTestCaseExternalFile() throws Exception {
        
        if (isFastJUnitTesting()){
            return;
        }


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

        runExecutableTest(run, runFiles, true, pc2YesJudgement);
    }

    public void testMultipleTestCaseInternalFile() throws Exception {
        
        if (isFastJUnitTesting()){
            return;
        }


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

        runExecutableTest(run, runFiles, true, pc2YesJudgement);
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
    public void testValidationFailure() throws Exception {

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
