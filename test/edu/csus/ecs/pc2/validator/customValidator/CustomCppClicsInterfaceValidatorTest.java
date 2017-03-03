package edu.csus.ecs.pc2.validator.customValidator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.execute.Executable;
import edu.csus.ecs.pc2.core.execute.ExecuteUtilities;
import edu.csus.ecs.pc2.core.execute.ExecutionData;
import edu.csus.ecs.pc2.core.list.AccountComparator;
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
import edu.csus.ecs.pc2.validator.ClicsValidator;
import edu.csus.ecs.pc2.validator.CustomValidatorSettings;

/**
 * JUnit test cases for a Custom Validator which uses the Clics Validator Interface.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class CustomCppClicsInterfaceValidatorTest extends AbstractTestCase {

    private boolean unitDebug = true;

    private String testDataDirectoryName = null;

    private String testOutputDirectoryName = null;

    private static final String DATA_DIR_PATH = "CustomValidatorTest";

    private IInternalContest contest;

    private IInternalController controller;

    private Language javaLanguage;

    private String validatorProgramName;

    private Problem iSumitProblem;

    private static final String NL = System.getProperty("line.separator");

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        testDataDirectoryName = getRootInputTestDataDirectory() + File.separator + DATA_DIR_PATH;
        testOutputDirectoryName = getOutputDataDirectory();
        ensureOutputDirectory();

        assertDirectoryExists(testDataDirectoryName);
        assertDirectoryExists(testOutputDirectoryName);

        validatorProgramName = getValidatorProgramName();

        if (validatorProgramName == null || validatorProgramName.equals("")) {
            validatorProgramName = "null";
        }
        assertFileExists(testDataDirectoryName + File.separator + validatorProgramName, "Missing executable validator file '" + validatorProgramName + "'");

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

            // addConsoleHandler(controller.getLog());
            setDebugLevel(controller.getLog());
        }

        iSumitProblem = createISumitProblem(contest);

        javaLanguage = createJavaLanguage(contest);

    }

    /**
     * return a random filename (no path).
     * 
     * @param prefix
     *            an optional string prefixed to the filename.
     * @return
     */
    protected String randomFileName(String prefix) {
        if (prefix == null) {
            prefix = "";
        }

        UUID uuid = UUID.randomUUID();

        return prefix + uuid.toString();
    }

    protected String randomOutputFileName(String prefix) {
        return testOutputDirectoryName + File.separator + randomFileName(prefix);
    }

    private Account getFirstJudge() {
        Account[] accounts = new SampleContest().getJudgeAccounts(contest);
        Arrays.sort(accounts, new AccountComparator());
        return accounts[0];

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

    /**
     * This method creates a new Problem named "ISumit" and adds it to the specified contest. 
     * The newly-created problem is configured to use a Custom Validator which uses a Clics Validator Interface.
     * 
     * @param contest2
     *            - the contest to which the Problem will be added
     * @return a new Problem (which has been added to the specified contest)
     * @throws FileNotFoundException
     *             if either the input data file "sumit.in" or the judge's answer file "sumit.ans" cannot be found
     * @see #createISumitFloatOutputProblem(IInternalContest)
     */
    private Problem createISumitProblem(IInternalContest contest2) throws FileNotFoundException {

        Problem problem = new Problem("ISumit");

        //set general options
        problem.setShowValidationToJudges(false);
        problem.setHideOutputWindow(true);
        problem.setShowCompareWindow(false);
        problem.setTimeOutInSeconds(10);

        problem.setReadInputDataFromSTDIN(true);

        ProblemDataFiles problemDataFiles = new ProblemDataFiles(problem);

        //set judge's input data file
        problem.setDataFileName("sumit.in");
        String judgesInputDataFile = getSamplesSourceFilename(problem.getDataFileName());
        checkFileExistence(judgesInputDataFile);
        problemDataFiles.setJudgesDataFile(new SerializedFile(judgesInputDataFile));

        //set judge's answer file
        problem.setAnswerFileName("sumit.ans");
        String answerFileName = getSamplesSourceFilename(problem.getAnswerFileName());
        checkFileExistence(answerFileName);
        answerFileName = convertEOLtoHostFormat(answerFileName);
        problemDataFiles.setJudgesAnswerFile(new SerializedFile(answerFileName));

        setupValidator(problem, problemDataFiles);
        
        contest2.addProblem(problem, problemDataFiles);

        return problem;
    }

    /**
     * Configures the specified Problem to use a Custom C++ Validator utilizing a Clics interface.
     * @param problem
     * @throws FileNotFoundException 
     */
    protected void setupValidator(Problem problem, ProblemDataFiles problemDataFiles) throws FileNotFoundException {

        //mark the problem as using a Custom Validator
        problem.setValidatorType(VALIDATOR_TYPE.CUSTOMVALIDATOR);

        //construct the appropriate Custom Validator settings
        CustomValidatorSettings settings = new CustomValidatorSettings();
        settings.setUseCLICSValidatorInterface();
        settings.setValidatorCommandLine("." + File.separator + Constants.DEFAULT_CLICS_VALIDATOR_COMMAND);
        settings.setValidatorProgramName(getValidatorProgramName());
        
        //put the Custom Validator Settings in the Problem
        problem.setCustomValidatorSettings(settings);
        
        //put the SerializedFile version of the executable validator file into the problem (specifically, it goes in the "problem data files")
        String validatorFileName =  testDataDirectoryName + File.separator + validatorProgramName;
        checkFileExistence(validatorFileName);
        problemDataFiles.setValidatorFile(new SerializedFile(validatorFileName));

        //verify that the Problem Validator is properly configured
        assertTrue("Expecting problem to be marked as isValidated, but failed", problem.isValidatedProblem());
        assertTrue("Expecting using Custom Validator, but failed", problem.isUsingCustomValidator());
        assertTrue("Expecting problem validator command line to be Clics Default Command Line, but failed", 
                                problem.getValidatorCommandLine().equals(Constants.DEFAULT_CLICS_VALIDATOR_COMMAND)); 
        assertTrue("Expecting problem validator program name to be '" + getValidatorProgramName() + "' but it is '" + problem.getValidatorProgramName() + "' ", 
                                problem.getValidatorProgramName().equals(getValidatorProgramName()));
        assertTrue("Expecting problem to have a serialized validator file but it has 'null' ", problemDataFiles.getValidatorFile()!=null);

    }

    protected void checkFileExistence(String filename) throws FileNotFoundException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new FileNotFoundException("Could not find required file: " + filename);
        }
    }

    /**
     * Returns the name of the validator program which should be run. 
     * The name does not contain a path; it's just the file name. 
     * The appropriate file is chosen based on the platform.
     * 
     * @return a String containing the name of the platform-specific C++ Clics Interface Validator
     */
    private String getValidatorProgramName() {

        String valName = "";

        // find OS type
        String osName = System.getProperty("os.name");
        System.out.println("DEBUG: os name = '" + osName + "'");

        if (osName != null) {

            if (osName.toLowerCase().contains("windows")) {

                System.out.println("DEBUG: found Windows OS");
                valName = "CustomCppClicsInterfaceValidator.exe";

            } else {

                if (osName.toLowerCase().contains("linux") || osName.toLowerCase().contains("unix")) {

                    // test for architecture type
                    String archType = System.getProperty("os.arch");
                    System.out.println("DEBUG: found architecture '" + archType + "'");

                    if (archType != null) {

                        if (archType.toLowerCase().equals("i386")) {
                            valName = "CustomCppClicsInterfaceValidator.i386";
                        } else if (archType.toLowerCase().equals("amd64")) {
                            valName = "CustomCppClicsInterfaceValidator.amd64";
                        }
                    }
                }
            }
        }

        return valName;
    }

    /**
     * Produces a new file in the Test Data directory which has the same name as the specified file but where the End-of-Line characters in the new file have been converted to the form of the host OS.
     * 
     * @param originalFileName
     *            the original data file
     * @return the name of a new file with EOL converted
     */
    private String convertEOLtoHostFormat(String originalFileName) {
        File orig = new File(originalFileName);
        String newFilename = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(orig));
            newFilename = getTestFilename(orig.getName());
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(newFilename)));
            String line;
            while ((line = br.readLine()) != null) {
                bw.write(line + NL);
            }
            br.close();
            bw.close();

        } catch (IOException e) {
            System.err.println("IOException while converting input file '" + originalFileName + "'");
            e.printStackTrace();
        }

        return newFilename;
    }

    public void testYesJudgement() throws Exception {

        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();
        Run run = createRun(submitter, javaLanguage, iSumitProblem, 81, 181);
        RunFiles runFiles = new RunFiles(run, getSamplesSourceFilename("ISumit.java"));

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, ClicsValidator.CLICS_CORRECT_ANSWER_MSG);

    }

    /**
     * Invoke an Executable object to test the configured validator.
     * 
     * @param run
     * @param runFiles
     * @param solved
     *            expecting Yes judgement, else failed somewhere in compile/execute/validate.
     * @param expectedJudgement
     * @throws Exception
     */
    protected void runExecutableTest(Run run, RunFiles runFiles, boolean solved, String expectedJudgement) throws Exception {

        String executeDirectoryName = getOutputDataDirectory(getName());
        ensureDirectory(executeDirectoryName);

        // this allows us to set the execute directory to be under testout vs executesiteXaccountY
        ExecutableOverride executable = new ExecutableOverride(contest, controller, run, runFiles, executeDirectoryName);
        executable.setUsingGUI(false);

        // run the executable to compile, execute, and validate the program contained in the Run
        executable.execute();

        ExecutionData executionData = executable.getExecutionData();

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

        assertTrue("Run failed to compile " + run.getLanguageId(), executionData.isCompileSuccess());
        assertTrue("Run failed to execute " + run.getProblemId(), executionData.isExecuteSucess());

        assertTrue("Problem should have a validator (improper JUnit configuration) ", contest.getProblem(run.getProblemId()).isValidatedProblem());
        
        
        if (solved) {
            //the test indicates the problem should have been solved; the validator should have said "yes" (success)
            if (!executable.isValidationSuccess()) {

                System.out.println("isValidationSuccess() returned false");
                if (executionData.getExecutionException() != null) {
                    throw executionData.getExecutionException();
                }
            }
            //no exception was found; verify validation was successful
            assertTrue("Expecting run to receive successful validation result ", executionData.isValidationSuccess());
            assertTrue("Expected to run " + run.getProblemId() + " to be judged 'Yes' ", ExecuteUtilities.didTeamSolveProblem(executionData));

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

        if (isDebugMode()) {
            System.err.println("DEBUG IS TURNED ON - turn it off");
        }

    }

    private Run createRun(ClientId submitter, Language language, Problem problem, int runNumber, int elapsedMins) {

        Run run = new Run(submitter, language, problem);
        run.setNumber(runNumber);
        run.setElapsedMins(elapsedMins);
        return run;
    }

    private Account getLastAccount(Type type) {
        return getLastAccount(contest, type);
    }

    private Account getLastAccount(IInternalContest inContest, Type type) {
        return inContest.getAccounts(type).lastElement();
    }

    public boolean isUnitDebug() {
        return unitDebug;
    }

    public void setUnitDebug(boolean unitDebug) {
        this.unitDebug = unitDebug;
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

}
