package edu.csus.ecs.pc2.core.execute;

import java.io.File;
import java.io.FileNotFoundException;

import edu.csus.ecs.pc2.core.IInternalController;
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
import edu.csus.ecs.pc2.validator.Validator;

/**
 * Test Executable class.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ExecutableTest extends AbstractTestCase {

    public static final String DEFAULT_INTERNATIONAL_VALIDATOR_COMMAND = "{:validator} {:infile} {:outfile} {:ansfile} {:resfile} ";

    // private static int testNumber = 1;

    private IInternalContest contest;

    private IInternalController controller;

    private String testDirectoryName = null;

    private Problem sumitProblem = null;

    private Language javaLanguage = null;

    private String yesJudgement = Validator.JUDGEMENT_YES;

    protected void setUp() throws Exception {
        super.setUp();
        SampleContest sampleContest = new SampleContest();
        contest = sampleContest.createContest(2, 2, 12, 12, true);
        controller = sampleContest.createController(contest, true, false);

//        deriveProjectDirectory();

        sumitProblem = createSumitProblem(contest);
        // helloWorldProblem = createHelloProblem(contest);
        javaLanguage = createJavaLanguage(contest);

    }
//
//    /**
//     * To derive the project directory.
//     * 
//     * @throws Exception
//     * 
//     */
//    private void deriveProjectDirectory() throws Exception {
//
//        String sampsDir = "testdata";
//        String projectPath = JUnitUtilities.locate(sampsDir);
//        if (projectPath == null) {
//            throw new Exception("Unable to locate " + sampsDir);
//        }
//
//        testDirectoryName = projectPath + File.separator + sampsDir;
//
//    }

    /**
     * Create a language using {@link LanguageAutoFill}.
     * 
     * @param autoFillLanguageTitle
     *            title for language from {@link LanguageAutoFill}.
     * @return
     */
    private Language createLanguage(String autoFillLanguageTitle) {

        Language language = new Language(autoFillLanguageTitle);
        String[] values = LanguageAutoFill.getAutoFillValues(autoFillLanguageTitle);

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

    public String getSampsFilename(String filename) {
        return getTestDirectoryName() + File.separator + filename;
    }

    protected void setPC2Validator(Problem problem) {

        problem.setValidatedProblem(true);
        problem.setValidatorCommandLine(DEFAULT_INTERNATIONAL_VALIDATOR_COMMAND);

        problem.setUsingPC2Validator(true);
        problem.setWhichPC2Validator(1);
        problem.setIgnoreSpacesOnValidation(true);
        problem.setValidatorCommandLine(DEFAULT_INTERNATIONAL_VALIDATOR_COMMAND + " -pc2 " + problem.getWhichPC2Validator() + " " + problem.isIgnoreSpacesOnValidation());
        problem.setValidatorProgramName(Problem.INTERNAL_VALIDATOR_NAME);
    }

    /**
     * Create sample Hello world problem with validator add to contest.
     * 
     * @param contest2
     * @return
     * @throws FileNotFoundException
     */
    // TODO test for hello Problem
    @SuppressWarnings("unused")
    private Problem createHelloProblem(IInternalContest contest2) throws FileNotFoundException {

        Problem problem = new Problem("Hello world");
        problem.setAnswerFileName("hello.ans");

        problem.setShowValidationToJudges(false);
        problem.setHideOutputWindow(true);
        problem.setShowCompareWindow(false);

        setPC2Validator(problem);

        ProblemDataFiles problemDataFiles = new ProblemDataFiles(problem);
        String answerFileName = getSampsFilename(problem.getAnswerFileName());
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

        setPC2Validator(problem);

        ProblemDataFiles problemDataFiles = new ProblemDataFiles(problem);

        problem.setDataFileName("sumit.dat");
        String judgesDataFile = getSampsFilename(problem.getDataFileName());
        checkFileExistance(judgesDataFile);
        problemDataFiles.setJudgesDataFile(new SerializedFile(judgesDataFile));

        problem.setAnswerFileName("sumit.ans");
        String answerFileName = getSampsFilename(problem.getAnswerFileName());
        checkFileExistance(answerFileName);
        problemDataFiles.setJudgesAnswerFile(new SerializedFile(answerFileName));

        contest2.addProblem(problem, problemDataFiles);

        return problem;
    }

    private Account getLastAccount(Type type) {
        return contest.getAccounts(type).lastElement();
    }

    
    public void testUsingGUI() throws Exception {
        
        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        Run run = new Run(submitter, javaLanguage, sumitProblem);
        RunFiles runFiles = new RunFiles(run, getSampsFilename("Sumit.java"));

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        
        Executable executable = new Executable(contest, controller, run, runFiles);
        executable.setUsingGUI(false);
        
        assertFalse(executable.isUsingGUI());
    }
    

    // TODO CCS fix executable test, runExecutableTest was spinning under ant
    
    public void debugTTtestSumit() {

        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        Run run = new Run(submitter, javaLanguage, sumitProblem);
        RunFiles runFiles = new RunFiles(run, getSampsFilename("Sumit.java"));

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, yesJudgement, false);
    }

    /**
     * Runs test on
     * 
     * @param run
     * @param runFiles
     * @param solved
     * @param expectedJudgement
     */
    protected void runExecutableTest(Run run, RunFiles runFiles, boolean solved, String expectedJudgement, boolean usingGUI) {

        Executable executable = new Executable(contest, controller, run, runFiles);
        executable.setUsingGUI(usingGUI);
        
        try {
            
            executable.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        ExecutionData executionData = executable.getExecutionData();

        // System.out.println("expectedJudgement  = " + expectedJudgement);
        // System.out.println("expectedJudgementV = " + executionData.getValidationResults());

        assertTrue("Souce file not compiled " + run.getProblemId(), executionData.isCompileSuccess());
        assertTrue("Run not executed " + run.getProblemId(), executionData.isExecuteSucess());

        // If this test fails - there may not be a Validator in the path, check vstderr.pc2 for
        // java.lang.NoClassDefFoundError: edu/csus/ecs/pc2/validator/Validator
        assertTrue("Run not validated " + run.getProblemId(), executionData.isValidationSuccess());

        assertTrue("Judgement should be solved ", solved);
        assertEquals(expectedJudgement, executionData.getValidationResults());
        
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

        private String executableDir = null;

        public ExecutableOverride(IInternalContest inContest, IInternalController inController, Run run, RunFiles runFiles, String executionDir) {
            super(inContest, inController, run, runFiles);
            setExecuteDirectoryName(executionDir);
        }

        @Override
        public String getExecuteDirectoryName() {
            return executableDir;
        }

        public void setExecuteDirectoryName(String dirname) {
            this.executableDir = "execute" + dirname;
        }

    }

    protected String getTestDirectoryName() {
        return testDirectoryName;
    }
}
