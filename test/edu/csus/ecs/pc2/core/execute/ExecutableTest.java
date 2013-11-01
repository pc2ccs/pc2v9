package edu.csus.ecs.pc2.core.execute;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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

    private Problem sumitProblem = null;

    // SOMEDAY add test for hello
    @SuppressWarnings("unused")
    private Problem helloWorldProblem = null;

    private Problem largeStdInProblem = null;
    
    private Language javaLanguage = null;

    private String yesJudgement = Validator.JUDGEMENT_YES;


    protected void setUp() throws Exception {
        super.setUp();
        SampleContest sampleContest = new SampleContest();
        contest = sampleContest.createContest(2, 2, 12, 12, true);
        controller = sampleContest.createController(contest, true, false);

        sumitProblem = createSumitProblem(contest);
        helloWorldProblem = createHelloProblem(contest);
        largeStdInProblem  = createLargeStdInProblem(contest);
        javaLanguage = createJavaLanguage(contest);

    }

    /**
     * Create a language using {@link LanguageAutoFill}.
     * 
     * @param autoFillLanguageTitle title for language from {@link LanguageAutoFill}.
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

        problem.setReadInputDataFromSTDIN(true);
        
        setPC2Validator(problem);

        ProblemDataFiles problemDataFiles = new ProblemDataFiles(problem);

        //TODO: change from sumit.dat to a LARGE input data file
        problem.setDataFileName("sumit.dat");
        String judgesDataFile = getSamplesSourceFilename(problem.getDataFileName());
        checkFileExistance(judgesDataFile);
        problemDataFiles.setJudgesDataFile(new SerializedFile(judgesDataFile));

        //TODO: change answer file to correct answer for Large input data file
        problem.setAnswerFileName("sumit.ans");
        String answerFileName = getSamplesSourceFilename(problem.getAnswerFileName());
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

        Run run = new Run(submitter, javaLanguage, sumitProblem);
        RunFiles runFiles = new RunFiles(run, getSamplesSourceFilename("Sumit.java"));

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, yesJudgement);

    }

    public void testLargeStdIn() {

        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();

        Run run = new Run(submitter, javaLanguage, largeStdInProblem);
        
        //TODO: change source file name to a new large-stdin problem source
        RunFiles runFiles = new RunFiles(run, getSamplesSourceFilename("Sumit.java"));

        contest.setClientId(getLastAccount(Type.JUDGE).getClientId());
        runExecutableTest(run, runFiles, true, yesJudgement);

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
        
        Executable executable = new Executable(contest, controller, run, runFiles);
        executable.setExecuteDirectoryName(executeDirectoryName);
        executable.setUsingGUI(false);
        executable.execute();

        ExecutionData executionData = executable.getExecutionData();

        // System.out.println("expectedJudgement  = " + expectedJudgement);
        // System.out.println("expectedJudgementV = " + executionData.getValidationResults());

        assertTrue("Source file not compiled " + run.getProblemId(), executionData.isCompileSuccess());
        assertTrue("Run not executed " + run.getProblemId(), executionData.isExecuteSucess());
        
        // If this test fails - there may not be a Validator in the path, check vstderr.pc2 for  
        // java.lang.NoClassDefFoundError: edu/csus/ecs/pc2/validator/Validator
        assertTrue("Run not validated " + run.getProblemId(), executionData.isValidationSuccess());

        assertTrue("Judgement should be solved ", solved);
        assertEquals(expectedJudgement, executionData.getValidationResults());
        
        //TODO: change the following println into an assert()
        System.err.println("Execute time for " + run.getProblemId() + " (ms): " + executionData.getExecuteTimeMS());
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Executable class with override for executable directory.
     * 
     * Executable creates a directory based on execute site and client id, this class allows for an override of that execute
     * directory, especially for testing purposes with multi-threaded tests.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    class ExecutableOverride extends Executable {

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

}
