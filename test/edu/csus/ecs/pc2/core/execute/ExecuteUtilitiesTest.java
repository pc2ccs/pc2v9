package edu.csus.ecs.pc2.core.execute;

import java.io.File;
import java.io.FileNotFoundException;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.LanguageAutoFill;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Problem.VALIDATORTYPE;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.validator.pc2Validator.PC2ValidatorSettings;

/**
 * Unit test.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class ExecuteUtilitiesTest extends AbstractTestCase {

    public ExecuteUtilitiesTest(String string) {
        super(string);
    }

    /**
     * Create a language using {@link LanguageAutoFill}.
     * 
     * @param autoFillLanguageTitle
     *            title for language from {@link LanguageAutoFill}.
     * @return
     */
    private Language createAutoFillLanguage(String autoFillLanguageTitle) {
        // TODO Promote to AbstractTestCase

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

    protected void setPC2Validator(Problem problem) {
        // TODO Promote to AbstractTestCase

        problem.setValidatorType(VALIDATORTYPE.PC2VALIDATOR);
        problem.setValidatorCommandLine(Constants.DEFAULT_PC2_VALIDATOR_COMMAND);
        problem.setOutputValidatorProgramName(Constants.PC2_VALIDATOR_NAME);
        
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
    private Problem createHello(IInternalContest contest2) throws FileNotFoundException {

        Problem problem = new Problem("Hello world");
        problem.setAnswerFileName("hello.ans");

        problem.setShowValidationToJudges(false);
        problem.setHideOutputWindow(true);
        problem.setShowCompareWindow(false);

        setPC2Validator(problem);

        ProblemDataFiles problemDataFiles = new ProblemDataFiles(problem);
        String answerFileName = super.getSamplesSourceFilename(problem.getAnswerFileName());
        assertFileExists(answerFileName);
        problemDataFiles.setJudgesAnswerFile(new SerializedFile(answerFileName));

        contest2.addProblem(problem, problemDataFiles);

        return problem;
    }

    public void testLanguageNameSub() throws Exception {
        // test for bug 855

        SampleContest sampleContest = new SampleContest();
        IInternalContest contest = sampleContest.createContest(2, 2, 12, 12, true);
        IInternalController controller = sampleContest.createController(contest, true, false);
        
        Language language = createAutoFillLanguage(LanguageAutoFill.JAVATITLE);
        contest.addLanguage(language);

        String origString = "mtsv {:languagename} {:language}";
        String expected = "mtsv gnu_c++ 8";

        ClientId submitter = contest.getAccounts(Type.TEAM).lastElement().getClientId();
        language = createAutoFillLanguage(LanguageAutoFill.GNUCPPTITLE);
        contest.addLanguage(language);
        
        assertEquals(language.getDisplayName(),"GNU C++");
        
//        dumpArray(contest.getLanguages());

        Problem helloWorldProblem = createHello(contest);
        Run run = new Run(submitter, language, helloWorldProblem);
        RunFiles runFiles = new RunFiles(run, getSamplesSourceFilename("hello.java"));

        Problem problem = null;
        ExecuteUtilities executeUtilities = new ExecuteUtilities(contest, controller, run, runFiles, problem, language);
        String actual = executeUtilities.substituteAllStrings(origString);
        assertEquals(expected, actual);
    }

    public void dumpArray(Object[] array) {
        System.out.println("dumpArray : ");
        int i = 0;
        for (Object object : array) {
            System.out.print(" " + i + ":" + object);
            i++;
        }
        System.out.println();
    }
    
    /**
     * Test first team, problem and language.
     * @throws Exception
     */
    public void testSubstituteStringTest() throws Exception {

        SampleContest sampleContest = new SampleContest();
        IInternalContest contest = sampleContest.createContest(2, 2, 12, 12, true);
        IInternalController controller = sampleContest.createController(contest, true, false);
        
        String resultsFileName = ExecuteUtilities.createResultsFileName(56);
        // this is used by the createMockFileContents to write the file to disk
        String outFilename = getOutputDataDirectory()+File.separator+"teams.output.txt";

        // but in the testData use it without a path
        String[] testData = { //
        // input;expected
                "A foggy bottom day runs far;A foggy bottom day runs far", //
                Constants.DEFAULT_PC2_VALIDATOR_COMMAND + ";edu.csus.ecs.pc2.validator.pc2Validator.PC2Validator sumit.dat teams.output.txt sumit.ans "+resultsFileName+" ", // 
                "{:ansfile};sumit.ans", // 
                "{:basename};Sumit", // 
                "{:executetime};45321", // 
                "{:exitvalue};45", // 
                "{:infile};sumit.dat", // 
                "{:language};1", // 
                "{:language};1", // 
                "{:languageletter};A", // 
                "{:languageletter};A", // 
                "{:languagename};java", // 
                "{:languagename};java", // 
                "{:mainfile};Sumit.java", //
                "{:outfile};teams.output.txt", // 
                "{:problem};1", // 
                "{:problemletter};A", // 
                "{:siteid};2", // 
                "{:teamid};1", // 
                "{files};Sumit.java", // 
                "{:timelimit};30", // 
                "{:validator};edu.csus.ecs.pc2.validator.pc2Validator.PC2Validator", // 
                "{:pc2home};"+ExecuteUtilities.getPC2Home(), // 
        };

        Account[] teams = SampleContest.getTeamAccounts(contest);

        Problem[] problems = contest.getProblems();
        Language[] languages = contest.getLanguages();

        Problem problem = problems[0];
        Language language = languages[0];
        
        ProblemDataFiles dataFiles = createMockDataFiles (problem, "sumit");
        
        setPC2Validator(problem);

        ClientId submitter = teams[0].getClientId();

        Run run = new Run(submitter, language, problem);
        RunFiles runFiles = new RunFiles(run, getSamplesSourceFilename("Sumit.java"));

        ExecuteUtilities executeUtilities = new ExecuteUtilities(contest, controller, run, runFiles, problem, language);
        executeUtilities.setProblemDataFiles(dataFiles);
        executeUtilities.setResultsFileName(resultsFileName);
        
        ExecutionData executionData = createMockExecutionData(outFilename);
        executeUtilities.setExecutionData(executionData);

        for (String line : testData) {

            String[] fields = line.split(";");
            String input = fields[0];
            String expected = fields[1];

            String actual = executeUtilities.substituteAllStrings(input);
            assertEquals("Expecting substitution for '"+input+"'", expected, actual);

//            System.out.println("\"" + input + ";" + actual + "\", // ");

        }

    }
    
    /**
     * Tests last problem and language and team.
     * @throws Exception
     */
    public void testLastEntries() throws Exception {
        
        SampleContest sampleContest = new SampleContest();
        IInternalContest contest = sampleContest.createContest(2, 2, 12, 12, true);
        IInternalController controller = sampleContest.createController(contest, true, false);
        
        String resultsFileName = ExecuteUtilities.createResultsFileName(56);
        // this is used by the createMockFileContents to write the file to disk
        String outFilename = getOutputDataDirectory()+File.separator+"teams.output.txt";

        // but in the testData use it without a path
        String[] testData = { //
        // input;expected
                "A foggy bottom day runs far;A foggy bottom day runs far", // 
                Constants.DEFAULT_PC2_VALIDATOR_COMMAND + ";edu.csus.ecs.pc2.validator.pc2Validator.PC2Validator sumit.dat teams.output.txt sumit.ans "+resultsFileName+" ", // 
                "{:ansfile};sumit.ans", // 
                "{:basename};Sumit", // 
                "{:executetime};45321", // 
                "{:exitvalue};45", // 
                "{:infile};sumit.dat", // 
                "{:language};6", // 
                "{:language};6", // 
                "{:languageletter};F", // 
                "{:languageletter};F", // 
                "{:languagename};apl", // 
                "{:languagename};apl", // 
                "{:mainfile};Sumit.java", // 
                "{:outfile};teams.output.txt", // 
                "{:problem};6", // 
                "{:problemletter};F", // 
                "{:siteid};2", // 
                "{:teamid};12", // 
                "{:timelimit};30", // 
                "{:validator};edu.csus.ecs.pc2.validator.pc2Validator.PC2Validator", // 
                "{:pc2home};"+ExecuteUtilities.getPC2Home(), //
        };

        Account[] teams = SampleContest.getTeamAccounts(contest);

        Problem[] problems = contest.getProblems();
        Language[] languages = contest.getLanguages();

        Problem problem = problems[problems.length-1];
        Language language = languages[languages.length-1];
        
        ProblemDataFiles dataFiles = createMockDataFiles (problem, "sumit");
        
        setPC2Validator(problem);

        ClientId submitter = teams[teams.length-1].getClientId();

        Run run = new Run(submitter, language, problem);
        RunFiles runFiles = new RunFiles(run, getSamplesSourceFilename("Sumit.java"));

        ExecuteUtilities executeUtilities = new ExecuteUtilities(contest, controller, run, runFiles, problem, language);
        executeUtilities.setProblemDataFiles(dataFiles);
        executeUtilities.setResultsFileName(resultsFileName);
        
        ExecutionData executionData = createMockExecutionData(outFilename);
        executeUtilities.setExecutionData(executionData);

        for (String line : testData) {

            String[] fields = line.split(";");
            String input = fields[0];
            String expected = fields[1];

            String actual = executeUtilities.substituteAllStrings(input);
            assertEquals("Expecting substitution", expected, actual);

//            System.out.println("\"" + input + ";" + actual + "\", // ");

        }
        
    }
    
    public void testEmptySubstituteStringTest() throws Exception {


        SampleContest sampleContest = new SampleContest();
        IInternalContest contest = sampleContest.createContest(2, 2, 12, 12, true);
        IInternalController controller = sampleContest.createController(contest, true, false);

        Problem problem = null;
        Language language = null;
//        ClientId submitter = null;

        Run run = null;
        RunFiles runFiles = null;

        try {
            new ExecuteUtilities(contest, controller, run, runFiles, problem, language);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(e != null);
            // expecting IllegalArgumentException
        }
     
    }
    
    public void testResultsFile() throws Exception {
        
        
        SampleContest sampleContest = new SampleContest();
        IInternalContest contest = sampleContest.createContest(2, 2, 12, 12, true);
        
        Account[] teams = SampleContest.getTeamAccounts(contest);

        Problem[] problems = contest.getProblems();
        Language[] languages = contest.getLanguages();

        Problem problem = problems[0];
        Language language = languages[0];

        Run run = new Run(teams[0].getClientId(), language, problem);
                
        String previous = "";
        for (int i = 0; i < 12; i++) {
            Thread.sleep(100);
            run.setNumber(i*42);
            
            String filename = ExecuteUtilities.createResultsFileName(run);
            assertNotEquals("Expecting unique name", previous, filename);
            previous = filename;
        }
    }
    
    private SerializedFile createMockFileContents (String filename){
        String[] datalines = getSampleDataLines();
        try {
            writeFileContents(filename, datalines);
            return new SerializedFile(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ExecutionData createMockExecutionData(String resultsFile) {
        ExecutionData executionData = new ExecutionData();
        
//        newString = replaceString(newString, "{:outfile}", executionData.getExecuteProgramOutput().getName());
//        newString = replaceString(newString, "{:exitvalue}", Integer.toString(executionData.getExecuteExitValue()));
//        newString = replaceString(newString, "{:executetime}", Long.toString(executionData.getExecuteTimeMS()));

//        String resultsFile = ExecuteUtilities.createResultsFileName(42);
        
        executionData.setExecuteProgramOutput(createMockFileContents(resultsFile));
        
        executionData.setExecuteExitValue(45);
        executionData.setExecuteTimeMS(45321);
        return executionData;
    }

    private ProblemDataFiles createMockDataFiles(Problem problem, String baseName) {
        
        ProblemDataFiles dataFiles = new ProblemDataFiles(problem);
        

        ProblemDataFiles problemDataFiles = new ProblemDataFiles(problem);

        problem.setDataFileName(baseName+".dat");
        String judgesDataFile = getSamplesSourceFilename(problem.getDataFileName());
        assertFileExists(judgesDataFile);
        problemDataFiles.setJudgesDataFile(new SerializedFile(judgesDataFile));

        problem.setAnswerFileName(baseName+".ans");
        String answerFileName = getSamplesSourceFilename(problem.getAnswerFileName());
        assertFileExists(answerFileName);
        problemDataFiles.setJudgesAnswerFile(new SerializedFile(answerFileName));
        
        return dataFiles;
    }

    public void testFindPC2JarPath() throws Exception {
        
        String path = ExecuteUtilities.findPC2JarPath();
        
        /**
         * If this fails then no pc2.jar could be found
         */
        assertNotEquals("pc2.jar (path to) not found", path, ExecuteUtilities.DEFAULT_PC2_JAR_PATH);
    }
    
}
