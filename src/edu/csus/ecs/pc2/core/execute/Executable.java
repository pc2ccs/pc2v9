// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.execute;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.IniFile;
import edu.csus.ecs.pc2.core.Plugin;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.RunTestCase;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.ui.IFileViewer;
import edu.csus.ecs.pc2.ui.MultipleFileViewer;
import edu.csus.ecs.pc2.ui.NullViewer;
import edu.csus.ecs.pc2.validator.clicsValidator.ClicsValidator;
import edu.csus.ecs.pc2.validator.clicsValidator.ClicsValidatorSettings;
import edu.csus.ecs.pc2.validator.pc2Validator.PC2ValidatorSettings;

/**
 * Compile, execute and validate a run.
 * 
 * Before execute, one can use {@link #setLanguage(Language)}, {@link #setProblem(Problem)} to use a different language or problem. <br>
 * To not overwrite the judge's data files, use {@link #setOverwriteJudgesDataFiles(boolean)} to false.
 * 
 * @see #execute()
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

// SOMEDAY this class contains a number of Utility methods like: baseName, replaceString... etc
// should these routines be placed in a static way in a static class ?
// SOMEDAY design decision how to handle MultipleFileViewer, display here, on TeamClient??

// $HeadURL$
public class Executable extends Plugin implements IExecutable {

    /**
     * 
     */
    private static final long serialVersionUID = 1408367949659070087L;

    private static final String NL = System.getProperty("line.separator");

    private Run run = null;

    private Language language = null;

    private Problem problem = null;

    private ProblemDataFiles problemDataFiles = null;

    private ClientId executorId = null;
    
    private boolean killedByTimer ;

    /**
     * Directory where main file is found
     */
    private String mainFileDirectory;

    /**
     * File user selects
     */
    private String fileFromUser;

    private ExecutionData executionData = new ExecutionData();

    private ExecuteTimer executionTimer;

    private IFileViewer fileViewer = null;

    /**
     * extra buffer space for the error message to be included in any output.
     */
    private static final int ERRORLENGTH = 50;

    /**
     * Compiler stdout filename.
     */
    public static final String COMPILER_STDOUT_FILENAME = "cstdout.pc2";

    /**
     * Compile stderr filename.
     */
    public static final String COMPILER_STDERR_FILENAME = "cstderr.pc2";
    
    /**
     * The default limit (in seconds) for compilation of a submission.
     */
    public static final int DEFAULT_COMPILATION_TIME_LIMIT_SECS = 60;

    /**
     * Execution stdout filename.
     */
    public static final String EXECUTE_STDOUT_FILENAME = "estdout.pc2";

    /**
     * Execution stderr filename.
     */
    public static final String EXECUTE_STDERR_FILENAME = "estderr.pc2";

    /**
     * Validator stdout filename.
     */
    public static final String VALIDATOR_STDOUT_FILENAME = "vstdout.pc2";

    /**
     * Validator stderr filename.
     */
    public static final String VALIDATOR_STDERR_FILENAME = "vstderr.pc2";
    
    /**
     * The default limit (in seconds) for validation of a single run (test case) of a submission.
     */
    public static final int DEFAULT_VALIDATION_TIME_LIMIT_SECS = 60;

    /**
     * Interface - the file created with the process return.exit code.
     */
    private static final String EXIT_CODE_FILENAME = "EXITCODE.TXT";

    private static final long NANOSECS_PER_MILLISEC = 1_000_000;

    /**
     * Files submitted with the Run.
     */
    private RunFiles runFiles;

    private String errorString;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    /**
     * The directory where files are unpacked and the program is executed.
     */
    private String executeDirectoryName = null;

    private String executeDirectoryNameSuffix = "";

    /**
     * Overwrite judge's data and answer files.
     */
    private boolean overwriteJudgesDataFiles = true;

    private boolean testRunOnly = false;

    private boolean showMessageToUser = true;

    private boolean usingGUI = true;

    /**
     * List of Team's output filenames, created by execute method.
     */
    private ArrayList<String> teamsOutputFilenames = new ArrayList<String>();

    /**
     * List of Validator output filenames, created by execute method.
     */
    private ArrayList<String> validatorOutputFilenames = new ArrayList<String>();

    /**
     * List of Validator stderr filenames, created by execute method.
     */
    private ArrayList<String> validatorStderrFilesnames = new ArrayList<String>();

    private long startTimeNanos;
    private long endTimeNanos;

    private Process process;

    private String packageName = "";

    private String packagePath = "";

    public Executable(IInternalContest inContest, IInternalController inController, Run run, RunFiles runFiles) {
        super();
        super.setContestAndController(inContest, inController);

        this.contest = inContest;
        this.controller = inController;
        this.runFiles = runFiles;
        this.run = run;
        language = inContest.getLanguage(run.getLanguageId());
        problem = inContest.getProblem(run.getProblemId());

        initialize();
    }

    /**
     * initialize class variables.
     */
    private void initialize() {

        this.executorId = contest.getClientId();

        if (runFiles != null) {
            mainFileDirectory = getDirName(runFiles.getMainFile());
        }
        executeDirectoryName = getExecuteDirectoryName();

        log = controller.getLog();

        if (executorId.getClientType() != ClientType.Type.TEAM) {
            this.problemDataFiles = contest.getProblemDataFile(problem);
        }
    }

    /**
     * Remove all files from specified directory, including subdirectories.
     * 
     * @param dirName
     *            directory to be cleared.
     * @return true if directory was cleared.
     */
    public boolean clearDirectory(String dirName) {
        File dir = null;
        boolean result = true;

        dir = new File(dirName);
        String[] filesToRemove = dir.list();
        for (int i = 0; i < filesToRemove.length; i++) {
            File fn1 = new File(dirName + File.separator + filesToRemove[i]);
            if (fn1.isDirectory()) {
                // recurse through any directories
                result &= clearDirectory(dirName + File.separator + filesToRemove[i]);
            }
            result &= fn1.delete();
        }
        return (result);
    }

    @Override
    public IFileViewer execute() {
        return execute(true);
    }

    @Override
    public IFileViewer execute(boolean clearDirFirst) {

        teamsOutputFilenames = new ArrayList<String>();

        if (usingGUI) {
            fileViewer = new MultipleFileViewer(log);
        } else {
            fileViewer = new NullViewer();
        }

        try {
            executionData = new ExecutionData();

            executeDirectoryName = getExecuteDirectoryName();

            boolean dirThere = insureDir(executeDirectoryName);

            if (!dirThere) {
                log.config("Directory could not be created: " + executeDirectoryName);
                showDialogToUser("Unable to create directory " + executeDirectoryName);
                setException("Unable to create directory " + executeDirectoryName);
                return fileViewer;
            }

            if (clearDirFirst && overwriteJudgesDataFiles) {
                // Clear directory out before compiling.

                /**
                 * Do not clear directory if writeJudgesDataFiles is false, because if we are not overwriting the judge's data file, then erasing the existing files makes no sense.
                 */

                boolean cleared = clearDirectory(executeDirectoryName);
                if (!cleared) {
                    // SOMEDAY LOG error Directory could not be cleared, other process running?
                    log.config("Directory could not be cleared, other process running? ");

                    showDialogToUser("Unable to remove all files from directory " + executeDirectoryName);
                    setException("Unable to remove all files from directory " + executeDirectoryName);
                    return fileViewer;
                }

            }
            // Extract source file to name in Problem.getDataFileName().

            if (runFiles.getMainFile() != null) {
                createFile(runFiles.getMainFile(), prefixExecuteDirname(runFiles.getMainFile().getName()));
            }

            if (runFiles.getOtherFiles() != null) {
                // Extract other submitted files.

                for (SerializedFile file : runFiles.getOtherFiles()) {
                    if (file != null) {
                        createFile(file, prefixExecuteDirname(file.getName()));
                    }
                }
            }

            if (isTestRunOnly()) {
                // Team, just compile and execute it.

                if (compileProgram()) {
                    executeProgram(0); // execute with first data set.
                } else {
                    /**
                     * compileProgram returns false if 1) runProgram failed (errorString set) 2) compiler fails to create expecte output file (errorString empty) If there is compiler stderr or stdout
                     * we should not add the textPane saying there was an error.
                     */
                    if (!executionData.isCompileSuccess()) {

                        // exitCode will be 1 if the runprocess failed
                        // ExectionException is set if the runprocess failed
                        // exitCode will be 2 if the output was not created (and not interpreted)
                        String title = "System Configuration Error";
                        String errorMessage = "";
                        if (executionData.getCompileResultCode() == 1 || executionData.getExecutionException() != null) {
                            // compiler missing
                            errorMessage = "Unable to find/execute compiler using the command \"" + substituteAllStrings(run, language.getCompileCommandLine());
                            errorMessage += "\", contact staff";
                        } else {
                            // expected output missing
                            title = "Compilation Error";
                            errorMessage = "Compilation error when compiling program \"" + substituteAllStrings(run, "{:mainfile}");
                            errorMessage += "\" using the command \"" + substituteAllStrings(run, language.getCompileCommandLine()) + "\"";
                        }

                        showDialogToUser(errorMessage);
                        fileViewer.addTextPane(title, errorMessage);

                    } else if (executionData.getCompileStderr() == null && executionData.getCompileStdout() == null) {

                        int errnoIndex = errorString.indexOf('=') + 1;
                        String errorMessage;
                        if (errorString.substring(errnoIndex).equals("2")) {
                            errorMessage = "Compiler not found, contact staff.";

                        } else {
                            errorMessage = "Problem executing compiler, contact staff.";
                        }
                        showDialogToUser(errorMessage);
                        setException(errorMessage);
                        fileViewer.addTextPane("Error during compile", errorMessage);
                    } // else they will get a tab hopefully showing something wrong
                }
            } else if (compileProgram()) {
                // if we get here, this is a Judged Run (not a Team Test Run)

                // compile succeeded; proceed to execution

                SerializedFile[] dataFiles = null;
                if (problemDataFiles != null) {
                    dataFiles = problemDataFiles.getJudgesDataFiles();
                } // else problem has no data files

                int dataSetNumber = 0;
                boolean passed = true;

                /**
                 * Did at least one test case fail flag.
                 */
                boolean atLeastOneTestFailed = false;
                String failedResults = "";
                
                //problem indicates stop-on-first-failure
                boolean stopOnFirstFailedTestCase = problem.isStopOnFirstFailedTestCase();

                if (dataFiles == null || dataFiles.length <= 1) {

                    // the judged run has (at most) a single data set,
                    log.info("Test cases: 1 for run " + run.getNumber());

                    passed = executeAndValidateDataSet(dataSetNumber);
                    if (!passed) {
                        atLeastOneTestFailed = true;
                        failedResults = executionData.getValidationResults();
                    }

                } else {

                    // the judged run has multiple test cases
                    log.info("Test cases: " + dataFiles.length + " for run " + run.getNumber());

                    // execute the judged run against each test data set until either all test cases are run
                    // or (if the problem indicates stop on first failed test case) a test case fails
                    while ((dataSetNumber < dataFiles.length) && ( !(stopOnFirstFailedTestCase && atLeastOneTestFailed))) {

                        // execute against one specific data set
                        passed = executeAndValidateDataSet(dataSetNumber);

                        dataSetNumber++;
                        if (!passed) {
                            log.info("FAILED test case " + dataSetNumber + " for run " + run.getNumber() + " reason " + getFailureReason());
                            atLeastOneTestFailed = true;
                            if ("".equals(failedResults)) {
                                failedResults = executionData.getValidationResults();
                            }
                        }
                    }

                    // re-create 1st test set data if this is an internal file
                    if (!problem.isUsingExternalDataFiles()) {
                        // if internal the 1st file has been re-written by datasets 1..n so we need to re-write 0
                        createFile(problemDataFiles.getJudgesDataFiles(), 0, prefixExecuteDirname(problem.getDataFileName()));

                        // Create the correct output file, aka answer file
                        createFile(problemDataFiles.getJudgesAnswerFiles(), 0, prefixExecuteDirname(problem.getAnswerFileName()));
                    }
                }

                if (atLeastOneTestFailed) {
                    // replace the final executionData with the 1st failed pass
                    executionData.setValidationResults(failedResults);
                    log.info("Test results: test failed " + run + " reason = " + getFailureReason());
                } else {
                    log.info("Test results: ALL passed for run " + run);
                }

            } else {

                // if we get here it is a judged run but the compile step failed
                /**
                 * compileProgram returns false if 1) runProgram failed (errorString set) 2) compiler fails to create expected output file (errorString empty) If there is compiler stderr or stdout we
                 * should not add the textPane saying there was an error.
                 */
                if (!executionData.isCompileSuccess()) {

                    String title = "System Configuration Error";
                    String errorMessage = "";
                    if (executionData.getCompileResultCode() == 1 || executionData.getExecutionException() != null) {
                        // compiler missing
                        errorMessage = "Unable to find/execute compiler using the command \"" + substituteAllStrings(run, language.getCompileCommandLine());
                        errorMessage += "\", contact staff";
                    } else {
                        // expected output missing
                        title = "Compilation Error";
                        errorMessage = "Compilation error when compiling program \"" + substituteAllStrings(run, "{:mainfile}");
                        errorMessage += "\" using the command \"" + substituteAllStrings(run, language.getCompileCommandLine()) + "\"";
                    }

                    if (executionData.getExecutionException() != null) {
                        errorMessage += NL + executionData.getExecutionException().getMessage();
                    }
                    showDialogToUser(errorMessage);
                    fileViewer.addTextPane(title, errorMessage);
                } else if (executionData.getCompileStderr() == null && executionData.getCompileStdout() == null) {
                    int errnoIndex = errorString.indexOf('=') + 1;
                    String errorMessage;
                    if (errorString.substring(errnoIndex).equals("2")) {
                        errorMessage = "Compiler not found, contact staff.";

                    } else {
                        errorMessage = "Problem executing compiler, contact staff.";
                    }
                    showDialogToUser(errorMessage);
                    setException(errorMessage);
                    fileViewer.addTextPane("Error during compile", errorMessage);
                } // else they will get a tab hopefully showing something wrong
            }

            // we've finished the compile/execute/validate steps (for better or worse); do the required final steps to display the results

            File file;
            String outputFile;

            fileViewer.setTitle("Executable");

            outputFile = prefixExecuteDirname(VALIDATOR_STDOUT_FILENAME);
            file = new File(outputFile);
            if (file.isFile() && file.length() > 0) {
                fileViewer.addFilePane("Validator output", outputFile);
            }

            outputFile = prefixExecuteDirname(VALIDATOR_STDERR_FILENAME);
            file = new File(outputFile);
            if (file.isFile() && file.length() > 0) {
                fileViewer.addFilePane("Validator stderr", outputFile);
            }

            outputFile = prefixExecuteDirname(EXECUTE_STDOUT_FILENAME);
            file = new File(outputFile);
            boolean programGeneratedOutput = false;
            if (file.isFile() && file.length() > 0) {
                fileViewer.addFilePane("Program output", outputFile);
                programGeneratedOutput = true;
            }

            outputFile = prefixExecuteDirname(EXECUTE_STDERR_FILENAME);
            file = new File(outputFile);
            if (file.isFile() && file.length() > 0) {
                fileViewer.addFilePane("Program stderr", outputFile);
                programGeneratedOutput = true;
            }

            if (executionData.isCompileSuccess() && !programGeneratedOutput) {
                String message = "PC2: execution of program did not generate any output";
                fileViewer.addTextPane("Program output", message);
            }

            outputFile = prefixExecuteDirname(COMPILER_STDOUT_FILENAME);
            file = new File(outputFile);
            if (file.isFile() && file.length() > 0) {
                fileViewer.addFilePane("Compiler stdout", outputFile);
            }

            outputFile = prefixExecuteDirname(COMPILER_STDERR_FILENAME);
            file = new File(outputFile);
            if (file.isFile() && file.length() > 0) {
                fileViewer.addFilePane("Compiler stderr", outputFile);
            }

            if (executionData.getExecuteExitValue() != 0) {
                long returnValue = ((long) executionData.getExecuteExitValue() << 0x20) >>> 0x20;

                fileViewer.setInformationLabelText("<html><font size='+1' color='red'>Team program exit code = 0x" + Long.toHexString(returnValue).toUpperCase() + "</font>");

            } else {
                fileViewer.setInformationLabelText("");
            }

            if (!isTestRunOnly()) {
                if (problem.isShowCompareWindow()) {
                    String teamsOutputFileName = prefixExecuteDirname(EXECUTE_STDOUT_FILENAME);

                    if (problem.getAnswerFileName() != null && problem.getAnswerFileName().length() > 0) {
                        String answerFileName = prefixExecuteDirname(problem.getAnswerFileName());
                        if (!new File(answerFileName).isFile()) {
                            int dataSetNumber = 0;

                            // Create the correct output file, aka answer file
                            createFile(problemDataFiles.getJudgesAnswerFiles(), dataSetNumber, answerFileName);
                        }
                        fileViewer.setCompareFileNames(answerFileName, teamsOutputFileName);
                        fileViewer.enableCompareButton(true);
                    }
                }
            }

        } catch (Exception e) {
            log.log(Log.INFO, "Exception during execute() ", e);
            fileViewer.addTextPane("Error during execute", "Exception during execute, check log " + e.getMessage());
        }

        return fileViewer;
    }

    public String getFailureReason() {

        if (executionData.getExecutionException() != null) {
            return executionData.getExecutionException().getMessage();
        } else if (executionData.getValidationResults() != null) {
            return executionData.getValidationResults();
        } else if (executionData.getExecuteExitValue()!=0) {
            return "Runtime error; exit code " + executionData.getExecuteExitValue() ;
        }

        return "Undetermined, developer note need another condition in getFailureReason()";
    }

    /**
     * Executes the current run against a specified data set, then if the problem is marked as being validated also invokes 
     * the appropriate validator to validate (evaluate the correctness of) the program (run) output.  
     * However, validation is not invoked if an error occurs during the execution phase or if executing the run results in 
     * either a time limit exceeded or a runtime error.
     * 
     * @param dataSetNumber
     *            zero-based data set number
     * @return true if the current submission was successfully executed using the specified data set AND the validator indicates
     *              that the output of the program was correct; returns false if any of the following happens:  there was an error
     *              (such as an exception thrown) during execution of the submission; the submission either hit the time limit for 
     *              the problem or generated a runtime error during execution; the validator indicates that the program output is
     *              not correct, or if there was an error during the attempt to validate the program output.  
     */
    private boolean executeAndValidateDataSet(int dataSetNumber) {

        boolean submissionIsCorrect = false;
        int testNumber = dataSetNumber + 1;

        log.info(" "); //put space in the log for readability -- separate each test case
        log.info("  Test case " + testNumber + " execute, run " + run.getNumber());

        boolean proceedToValidation = executeProgram(dataSetNumber);
        
        if (proceedToValidation && isValidated()) {
            log.info(" "); //space for readability in the log
            log.info("  Test case " + testNumber + " validate, run " + run.getNumber());
            submissionIsCorrect = validateProgram(dataSetNumber);

            if (!ExecuteUtilities.didTeamSolveProblem(executionData)) {
                submissionIsCorrect = false;
            }

        } else {
            //if we get here we are not going to validate so there's no way we can declare the submission is correct
            submissionIsCorrect = false;
        }
        
        //at this point, "submisssionIsCorrect" is true if: 
        //  the submitted program was successfully executed 
        //  AND the problem has a validator 
        //  AND method validateProgram() returned true (indicating the problem was correctly solved for the specified test case) 
        //  AND the ExecutionData object for the run indicates that the submission solved the problem for the specified data case.
        //    (the ExecutionData object indicates the program solved the problem if:
        //       the program compiled successfully 
        //       AND the system was able to successfully execute the program 
        //       AND the program did not exceed the runtime limit 
        //       AND the validator program ran successfully 
        //       AND there were no exceptions during Validator execution 
        //       AND the result string returned by the Validator was "accepted".  
        //     The ExecutionData object returns false (the problem was NOT solved) if any of these conditions is false.
        //    )
        //If any of the above conditions is not true, "passed" is false at this point.
        //Note that the value of "passed" is stored in the RunTestCaseResult object (below), and this is what is returned by RunTestCaseResult.isPassed()

        String reason = getFailureReason();
        if (reason == null) {
            reason = "";
        } else {
            reason = "; validator returns: " + reason;
        }

        log.info("  Test case " + testNumber + " passed = " + Utilities.yesNoString(submissionIsCorrect) + " " + reason);

        JudgementRecord record = JudgementUtilites.createJudgementRecord(contest, run, executionData, executionData.getValidationResults());

        // Judgement judgement = getContest().getJudgement(record.getJudgementId());
        // log.info(" Test case " + testNumber + " passed = " + Utilities.yesNoString(passed) + " judgement = " + judgement);

        RunTestCase runTestCaseResult = new RunTestCase(run, record, testNumber, submissionIsCorrect);
        runTestCaseResult.setElapsedMS(executionData.getExecuteTimeMS());
        runTestCaseResult.setContestTimeMS(getContest().getContestTime().getElapsedMS());
        runTestCaseResult.setValidated(isValidated());
        run.addTestCase(runTestCaseResult);
        return submissionIsCorrect;
    }

    /**
     * Extracts file setNumber from list of files (fileList).
     * 
     * @param fileList
     *            - list of SerializedFile's
     * @param setNumber
     *            - index in list of file to write, zero based.
     * @param outputFileName
     *            - output file name.
     * @return true if file written to disk.
     */
    public boolean createFile(SerializedFile[] fileList, int setNumber, String outputFileName) {

        if (fileList != null) {
            if (setNumber < fileList.length) {
                return createFile(fileList[setNumber], outputFileName);
            }
        }

        return false;
    }

    protected boolean createFile(SerializedFile file, String filename) {
        try {
            // will return false if file could not be created
            return Utilities.createFile(file, filename);
        } catch (IOException e) {
            log.log(Log.INFO, "Could not create " + filename, e);
            return false;
        }
    }

    /**
     * Show pop up mesage to user.
     * 
     * @param string
     */
    protected void showDialogToUser(String string) {

        if (showMessageToUser) {
            if (usingGUI) {
                fileViewer.showMessage(string);
            }
            log.info(string);
        }
    }

    /**
     * Insure directory exists, if does not exist create it.
     * 
     * @param dirName
     *            directory to create.
     * @return whether directory exists.
     */
    public boolean insureDir(String dirName) {
        File dir = null;

        dir = new File(dirName);
        if (!dir.exists() && !dir.mkdir()) {
            log.log(Log.CONFIG, "Executable.execute(RunData): Directory " + dir.getName() + " could not be created.");
            setException("Executable.execute(RunData): Directory " + dir.getName() + " could not be created.");
        }

        return dir.isDirectory();
    }

    /**
     * Runs the problem-specified validator to compare the output of the run (team program) with the corresponding judge's answer file.
     * 
     * @param dataSetNumber
     *            a zero-based value indicating the data set against which the run was executed
     * @return true if the validator returns "success" (indicating that the problem was correctly solved)
     */
    protected boolean validateProgram(int dataSetNumber) {

        // SOMEDAY Handle the error messages better, log and put them before the user to
        // help with debugging

        int testCase = dataSetNumber + 1;
        log.info(" ");
        log.info("starting validation for test case " + testCase);
        
        executionData.setValidationReturnCode(-1);
        executionData.setValidationSuccess(false);

        if (isJudge()) {
            controller.sendValidatingMessage(run);
        }

        // get the appropriate command pattern for invoking the validator attached to the problem
        String commandPattern = "";

        if (problem.isUsingPC2Validator()) {

            commandPattern = getPC2ValidatorCommandPattern();

        } else if (problem.isUsingCLICSValidator()) {

            commandPattern = getCLICSValidatorCommandPattern();

        } else if (problem.isUsingCustomValidator()) {

            commandPattern = getCustomValidatorCommandPattern();

            // for a custom validator we also need to obtain the SerializedFile for the validator
            if (problemDataFiles != null && problemDataFiles.getOutputValidatorFile() != null) {

                // get Validation Program
                String validatorFileName = problemDataFiles.getOutputValidatorFile().getName();
                String validatorUnpackName = prefixExecuteDirname(validatorFileName);

                // create the validator program file
                if (!createFile(problemDataFiles.getOutputValidatorFile(), validatorUnpackName)) {
                    log.warning("Unable to create custom validator program " + validatorUnpackName);
                    setException("Unable to create custom validator program " + validatorUnpackName);

                    throw new SecurityException("Unable to create custom validator, check logs");
                }

                if (!validatorFileName.endsWith(".jar")) {
                    // Unix validator programs must set the execute bit to be able to execute the program.
                    setExecuteBit(prefixExecuteDirname(validatorFileName));
                }
            } else {

                log.warning("Unable to create custom validator program: no SerializedFile available from ProblemDataFiles");
                setException("Unable to create custom validator program: no SerializedFile available from ProblemDataFiles");
                throw new IllegalStateException("IllegalStateException: Problem is marked as having a Custom Validator but no "
                        + "SerializedFile for the validator could be obtained from the ProblemDataFiles");
            }

        } else {

            log.warning("Problem is marked as validated but has no defined Validator");
            setException("Problem is marked as validated but has no defined Validator");
            throw new IllegalStateException("IllegalStateException: Problem is marked as validated but has no defined Validator");
        }

        // when we get here, we have a command pattern for the specified validator and, if it is a custom validator,
        // we know there is an executable validator file

        // get Judge input data file name, either short name or fully qualified if external file. {:infile}
        String judgeDataFilename = problem.getDataFileName();

        // get Judge answer file name, either short name or fully qualified if external file. {:ansfile}
        String judgeAnswerFilename = problem.getAnswerFileName();

        if (overwriteJudgesDataFiles) {

            if (problem.isUsingExternalDataFiles()) {

                /**
                 * Set filenames if external files.
                 */

                SerializedFile serializedFile = problemDataFiles.getJudgesDataFiles()[dataSetNumber];
                judgeDataFilename = Utilities.locateJudgesDataFile(problem, serializedFile, getContestInformation().getJudgeCDPBasePath(), Utilities.DataFileType.JUDGE_DATA_FILE);

                serializedFile = problemDataFiles.getJudgesAnswerFiles()[dataSetNumber];
                judgeAnswerFilename = Utilities.locateJudgesDataFile(problem, serializedFile, getContestInformation().getJudgeCDPBasePath(), Utilities.DataFileType.JUDGE_DATA_FILE);

            } else {

                if (problemDataFiles == null) {
                    throw new NullPointerException("Internal error - no data files present for problem " + problem);
                }

                /**
                 * If not external files, must unpack files.
                 */

                // Create the correct output file, aka answer file
                createFile(problemDataFiles.getJudgesDataFiles(), dataSetNumber, prefixExecuteDirname(problem.getDataFileName()));

                // Create the correct output file, aka answer file
                createFile(problemDataFiles.getJudgesAnswerFiles(), dataSetNumber, prefixExecuteDirname(problem.getAnswerFileName()));

            } // else no need to create external data files.

        }

        // get a "random" number to be used as part of the results file name and feedback directory name, for security
        String secs = Long.toString((new Date().getTime()) % 100);

        //construct a "results file name", used by PC2 Interface validators
        int testSetNumber = dataSetNumber + 1;
        String pc2InterfaceResultsFileName = run.getNumber() + secs + "XRSAM." + testSetNumber + ".txt";

        //construct a "feedback directory" name, used by CLICS Interface validators
        String clicsInterfaceFeedbackDirName = run.getNumber() + secs + "XRSAM." + testSetNumber + File.separator;

        log.log(Log.DEBUG, "command pattern before substitution: " + commandPattern);

        // orig String cmdLine = substituteAllStrings(run, commandPattern);

        String cmdLine = replaceString(commandPattern, "{:infile}", judgeDataFilename);
        cmdLine = replaceString(cmdLine, "{:ansfile}", judgeAnswerFilename);
        cmdLine = replaceString(cmdLine, "{:outfile}", "estdout.pc2");
        cmdLine = replaceString(cmdLine, "{:resfile}", pc2InterfaceResultsFileName);
        cmdLine = replaceString(cmdLine, "{:feedbackdir}", clicsInterfaceFeedbackDirName);

        //create the feedback directory for validators using the Clics Interface
        if (problem.isUsingCLICSValidator() || (problem.isUsingCustomValidator() && problem.getCustomOutputValidatorSettings().isUseClicsValidatorInterface())) {

            String feedbackDirPath = getExecuteDirectoryName() + File.separator + clicsInterfaceFeedbackDirName;

            // get rid of any pre-existing feedback dir
            try {
                ExecuteUtilities.removeDirectory(feedbackDirPath);
            } catch (Exception e) {
                log.warning("Exception trying to remove feedback directory '" + feedbackDirPath + "': " + e.getMessage());
            }

            if (insureDir(feedbackDirPath)) {
                // clean out feedback dir
                ExecuteUtilities.clearDirectory(feedbackDirPath);
            } else {
                throw new SecurityException("Unable to create ClicsValidator feedback directory '" + feedbackDirPath + "'; check logs");
            }
        }

        cmdLine = substituteAllStrings(run, cmdLine);

        log.log(Log.DEBUG, "command pattern after substitution: " + cmdLine);

        // check if the command to be executed is a program residing in the current execute directory;
        // if so, prepend the directory name onto the command name in the command line
        try {
            String actFilename = new String(cmdLine);

            int i;

            i = actFilename.trim().indexOf(" ");
            if (i > -1) {
                actFilename = executeDirectoryName + actFilename.trim().substring(0, i);
            } else {
                actFilename = executeDirectoryName + actFilename.trim();
            }

            File f = new File(actFilename);
            if (f.exists()) {
                cmdLine = prefixExecuteDirname(cmdLine.trim());
            }

        } catch (Exception e) {
            log.log(Log.INFO, "Exception while constructing validator command line ", e);
            //TODO: this shouldn't be setting ExecutionException; it needs to set a (currently undefined) separate field such as "ValidationException"
//            executionData.setExecutionException(e);
            throw new SecurityException(e);
        }

        //execute the validator, as a separate process
        int exitcode = -1;
        //the validation phase needs its own local timer, to avoid the possibility that the TLE-Timer task from the
        // execute phase might wake up and kill the timer/IOCollectors referenced by the global "executionTimer" variable.
        // See bug 1668 for details.
        ExecuteTimer validatorExecutionTimer = null;
        BufferedOutputStream stdoutlog = null;
        BufferedOutputStream stderrlog = null;
        try {

            stdoutlog = new BufferedOutputStream(new FileOutputStream(prefixExecuteDirname(VALIDATOR_STDOUT_FILENAME), false));
            stderrlog = new BufferedOutputStream(new FileOutputStream(prefixExecuteDirname(VALIDATOR_STDERR_FILENAME), false));

            String msg = "Working...";
            if (problem.isShowValidationToJudges()) {
                msg = "Validating...";
            }

            //added per bug 1668
            validatorExecutionTimer = new ExecuteTimer(log, getValidationTimeLimit(), executorId, isUsingGUI());

            log.info("constructed new validator ExecuteTimer " + validatorExecutionTimer.toString());
            long startTime = System.currentTimeMillis();
            Process validatorProcess = runProgram(cmdLine, msg, false, validatorExecutionTimer);

            if (validatorProcess == null) {
                log.warning("validator process is null; stopping ExecuteTimer");
                validatorExecutionTimer.stopTimer();
                String errMsg = "Validator failed to run using command '" + cmdLine + "'\n";
                stderrlog.write(errMsg.getBytes());
                stderrlog.close();
                stdoutlog.close();
                return false;
            } else {
                log.info("created validator process " + getProcessID(validatorProcess));
            }

            // This reads from the stdout of the child process
            BufferedInputStream childOutput = new BufferedInputStream(validatorProcess.getInputStream());
            // The reads from the stderr of the child process
            BufferedInputStream childError = new BufferedInputStream(validatorProcess.getErrorStream());

            IOCollector stdoutCollector = new IOCollector(log, childOutput, stdoutlog, validatorExecutionTimer, getMaxFileSize() + ERRORLENGTH);
            IOCollector stderrCollector = new IOCollector(log, childError, stderrlog, validatorExecutionTimer, getMaxFileSize() + ERRORLENGTH);

            validatorExecutionTimer.setIOCollectors(stdoutCollector, stderrCollector);
            validatorExecutionTimer.setProc(validatorProcess);

            log.info("starting validator IOCollectors");
            stdoutCollector.start();
            stderrCollector.start();

            // // waiting for the process to finish execution...
            // executionData.setValidationReturnCode(process.waitFor());

            // if CLICS-style validator interface, redirect team output to STDIN
            if (problem.isUsingCLICSValidator() || (problem.isUsingCustomValidator() && problem.getCustomOutputValidatorSettings().isUseClicsValidatorInterface())) {

                String teamOutputFileName = getTeamOutputFilename(dataSetNumber);
                if (teamOutputFileName != null && new File(teamOutputFileName).exists()) {
                    log.info("Sending team output file '" + getTeamOutputFilename(dataSetNumber) + "' to Validator stdin");

                    BufferedOutputStream out = new BufferedOutputStream(validatorProcess.getOutputStream());
                    BufferedInputStream in = new BufferedInputStream(new FileInputStream(teamOutputFileName));
                    byte[] buf = new byte[32768];
                    int c;
                    try {
                        while ((c = in.read(buf)) != -1) {
                            out.write(buf, 0, c);
                        }
                    } catch (java.io.IOException e) {
                        log.info("Caught a " + e.getMessage() + " while sending team output to validator; do not be alarmed.");
                    }

                    in.close();
                    out.close();
                }
            }

            log.info("waiting for validator IOCollectors to terminate...");
            stdoutCollector.join();
            stderrCollector.join();

            // if(isJudge && executionTimer != null) {
            if (validatorExecutionTimer != null) {
                log.info("stopping validator ExecuteTimer");
                validatorExecutionTimer.stopTimer();
            }

            if (validatorProcess != null) {
                log.info("waiting for validator process to terminate...");
                exitcode = validatorProcess.waitFor();
                log.info("validator process returned exit code " + exitcode);
                executionData.setValidationReturnCode(exitcode);
                log.info("destroying validator process");
                validatorProcess.destroy();
            }


            executionData.setvalidateTimeMS(System.currentTimeMillis() - startTime);
            executionData.setValidationStdout(new SerializedFile(prefixExecuteDirname(VALIDATOR_STDOUT_FILENAME)));
            executionData.setValidationStderr(new SerializedFile(prefixExecuteDirname(VALIDATOR_STDERR_FILENAME)));

        } catch (Exception ex) {
            //TODO: this shouldn't be setting ExecutionException; it needs to set a (currently undefined) separate field such as "ValidationException"
//            executionData.setExecutionException(ex);
            if (validatorExecutionTimer != null) {
                validatorExecutionTimer.stopTimer();
            }
            log.log(Log.WARNING, "Exception running validator ", ex);
            
        } finally {
            try {
                stdoutlog.close();
                stderrlog.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        //When we get here the Validator external process has completed (one way or the other...)

        // The validator stdout for the current dataset was written to file "vstdout.pc2" and then copied into a
        // SerializedFile and stored in the executionData (by the last statements in the try/catch, above);
        // copy it from the executionData to a file named for this specific data set so it doesn't get overwritten
        // during the execution for the following data set
        String validatorOutputFilename = prefixExecuteDirname("valout." + dataSetNumber + ".txt"); // dataset-specific validator stdout file
        createFile(executionData.getValidationStdout(), validatorOutputFilename);

        // save new validator stdout file in a list of stdout files for all data sets
        validatorOutputFilenames.add(validatorOutputFilename);

        // same as above, for validator stderr
        String validatorStderrFilename = prefixExecuteDirname("valerr." + dataSetNumber + ".txt");
        createFile(executionData.getValidationStderr(), validatorStderrFilename);
        validatorStderrFilesnames.add(validatorStderrFilename);

        //check if the validator is using the "PC2 Validator Interface" Standard
        if (problem.isUsingPC2Validator() || (problem.isUsingCustomValidator() && problem.getCustomOutputValidatorSettings().isUsePC2ValidatorInterface())) {

            //it was using the PC2 Validator Interface, check the results file
            boolean fileThere = new File(prefixExecuteDirname(pc2InterfaceResultsFileName)).exists();

            try {
                if (fileThere) {

                    if (problem.isUsingPC2Validator()) {
                        updatePC2ValidatorResults(pc2InterfaceResultsFileName, log);
                    } else {
                        if (problem.isUsingCustomValidator()) {
                            updateCustomPC2InterfaceValidatorResults(pc2InterfaceResultsFileName, log);
                        }
                    }

                } else {
                    log.warning("Validator call did not produce output results file '" + pc2InterfaceResultsFileName + "'");
                    // JOptionPane.showMessageDialog(null, "Did not produce output results file " + resultsFileName + " contact staff");
                }
            } catch (Exception ex) {
                //TODO: this shouldn't be setting ExecutionException; it needs to set a (currently undefined) separate field such as "ValidationException"
//                executionData.setExecutionException(ex);
                log.log(Log.WARNING, "Exception while reading results file '" + pc2InterfaceResultsFileName + "'", ex);
                throw new SecurityException(ex);
            }
            
            //the following code shouldn't be here -- the validateProgram method should never be called if there has already been a TLE
//            } finally {
//
//                if (executionData.isRunTimeLimitExceeded()) {
//                    executionData.setValidationResults("No - Time Limit Exceeded");
//                    executionData.setValidationSuccess(true);
//                }
//            }
        } else if (problem.isUsingCLICSValidator() || (problem.isUsingCustomValidator() && problem.getCustomOutputValidatorSettings().isUseClicsValidatorInterface())) {
            //check if the Validator was using the "CLICS Validator Interface" Standard

            //it was using the CLICS Validator Interface, check the results file(s)
            try {
                String feedbackDirPath = getExecuteDirectoryName() + File.separator + clicsInterfaceFeedbackDirName;

                if (problem.isUsingCLICSValidator()) {
                    //save the ClicsValidator results
                    updateClicsValidatorResults(exitcode, feedbackDirPath, log);
                } else if (problem.isUsingCustomValidator()) {
                    //save the Custom Validator results
                    updateCustomClicsInterfaceValidatorResults(exitcode, feedbackDirPath, log);
                }

            } catch (Exception e) {
                //TODO: this shouldn't be setting ExecutionException; 
                // it needs to set a (currently undefined) separate field such as "ValidationException"
//                executionData.setExecutionException(e);
                log.log(Log.WARNING, "Exception while reading validator results file '" + clicsInterfaceFeedbackDirName + "'", e);
                throw new SecurityException(e);

            } 
            //ditto above
//            finally {
//
//                if (executionData.isRunTimeLimitExceeded()) {
//                    executionData.setValidationResults("No - Time Limit Exceeded"); //TODO: this string should NOT be hard-coded here!
//                    executionData.setValidationSuccess(true);
//                }
//
//            }
        }

        return executionData.isValidationSuccess();
    }

    /**
     * Returns a command pattern for invoking the PC2 "internal validator". The returned pattern contains "substitution variables" for the elements required by the PC2 validator (for example,
     * "{:infile}" where the judge's input data file should be substituted).
     * 
     * @return a command pattern for invoking the PC2 Validator
     */
    private String getPC2ValidatorCommandPattern() {

        // try to find a path to a pc2.jar file
        String pathToPC2Jar = findPC2JarPath();

        // if a path was found, add "pc2.jar" to the path
        if ((new File(pathToPC2Jar + "pc2.jar")).exists()) {
            pathToPC2Jar += "pc2.jar";
        }

        String options = getPC2ValidatorOptionString();

        String args = "{:infile} {:outfile} {:ansfile} {:resfile}";

        String validatorName;
        if (problem != null && problem.getPC2ValidatorSettings() != null) {
            validatorName = problem.getPC2ValidatorSettings().getValidatorProgramName();
        } else {
            validatorName = Constants.PC2_VALIDATOR_NAME;
        }

        // depending on how it is run (e.g. from a Test directory), pathToPC2Jar may or may not add a file separator at the end;
        // one is added here to insure it is present
        String cmdPattern = "java -cp " + pathToPC2Jar + " " + validatorName + " " + args + " " + options;

        // get rid of any double-fileSeparators
        String doubleFS = File.separator + File.separator;
        cmdPattern = cmdPattern.replaceAll(Matcher.quoteReplacement(doubleFS), Matcher.quoteReplacement(File.separator));

        // System.out.println("DEBUG2: PC2 Validator command pattern:  '" + cmdPattern + "'");
        log.log(Log.DEBUG, "PC2 Validator command pattern:  '" + cmdPattern + "'");

        return cmdPattern;

    }

    /**
     * Returns a command pattern for invoking the {@link ClicsValidator}. 
     * The returned pattern contains "substitution variables" for the elements required by the CLICS validator (for example,
     * "{:infile}" where the judge's input data file should be substituted).
     * 
     * @return a command pattern for invoking the CLICS Validator
     */
    private String getCLICSValidatorCommandPattern() {

        // try to find a path to a pc2.jar file
        String pathToPC2Jar = findPC2JarPath();

        // if a path was found, add "pc2.jar" to the path
        if ((new File(pathToPC2Jar + "pc2.jar")).exists()) {
            pathToPC2Jar += "pc2.jar";
        }

        String options = getClicsValidatorOptionString();

        String args = "{:infile} {:ansfile} {:feedbackdir}";

        String validatorName;
        if (problem != null && problem.getClicsValidatorSettings() != null) {
            validatorName = problem.getClicsValidatorSettings().getValidatorProgramName();
        } else {
            validatorName = Constants.CLICS_VALIDATOR_NAME;
        }

        String cmdPattern = "java -cp " + pathToPC2Jar + " " + validatorName + " " + args + " " + options;

        // get rid of any double-fileSeparators
        String doubleFS = File.separator + File.separator;
        cmdPattern = cmdPattern.replaceAll(Matcher.quoteReplacement(doubleFS), Matcher.quoteReplacement(File.separator));

        // System.out.println("DEBUG2: CLICS Validator command pattern:  '" + cmdPattern + "'");
        log.log(Log.DEBUG, "CLICS Validator command pattern:  '" + cmdPattern + "'");

        return cmdPattern;
    }

    /**
     * Returns a command pattern for invoking a Custom Validator. 
     * If the current Problem is null or has no Custom Validator Settings, null is returned.
     * Otherwise, the returned command pattern is the command pattern defined in the
     * Custom Validator Settings with "./" (or ".\") prepended, 
     * unless the current Validator Program name ends with ".jar" in which case the 
     * returned command pattern is the command pattern defined in the Custom Validator Settings
     * with "java -jar " prepended.
     * 
     * @return a command pattern for invoking the Custom Validator, or null if no command pattern could be determined
     */
    private String getCustomValidatorCommandPattern() {

        String cmdPattern = null ;

        if (problem != null && problem.getCustomOutputValidatorSettings() != null) {

            String validatorProgramName = problem.getCustomOutputValidatorSettings().getCustomValidatorProgramName();
            if (validatorProgramName.trim().toLowerCase().endsWith(".jar")) {
                cmdPattern = "java -jar " + problem.getCustomOutputValidatorSettings().getCustomValidatorCommandLine();
            } else {
                cmdPattern = "." + File.separator + problem.getCustomOutputValidatorSettings().getCustomValidatorCommandLine();   
            }
        }

        // System.out.println("DEBUG: Custom Validator command pattern: '" + cmdPattern + "'");
        log.log(Log.DEBUG, "Custom Validator command pattern:  '" + cmdPattern + "'");

        return cmdPattern;

    }

    /**
     * Returns a string containing the PC2 Validator options configured in the current problem.
     * 
     * @return a String containing the PC2 Validator options, or the empty string if the problem is null or the PC2ValidatorSettings is null
     */
    private String getPC2ValidatorOptionString() {

        String optStr = "";

        if (problem != null && problem.getPC2ValidatorSettings() != null) {

            PC2ValidatorSettings settings = problem.getPC2ValidatorSettings();

            optStr += "-pc2 " + settings.getWhichPC2Validator();

            if (settings.isIgnoreCaseOnValidation()) {
                optStr += " " + true;
            } else {
                optStr += " " + false;
            }

        }
        return optStr;
    }

    /**
     * Returns a string containing the {@link ClicsValidatorSettings} options configured in the current problem.
     * 
     * @return a String containing the Clics Validator options, or the empty string if the problem is null or the ClicsValidatorSettings is null
     */
    private String getClicsValidatorOptionString() {

        String optStr = "";

        if (problem != null && problem.getClicsValidatorSettings() != null) {

            ClicsValidatorSettings settings = problem.getClicsValidatorSettings();

            if (settings.isCaseSensitive()) {
                optStr += " " + ClicsValidatorSettings.CLICS_VTOKEN_CASE_SENSITIVE;
            }
            if (settings.isSpaceSensitive()) {
                optStr += " " + ClicsValidatorSettings.CLICS_VTOKEN_SPACE_CHANGE_SENSITIVE;
            }
            if (settings.isFloatAbsoluteToleranceSpecified()) {
                optStr += " " + ClicsValidatorSettings.CLICS_VTOKEN_FLOAT_ABSOLUTE_TOLERANCE;
                double abstol = settings.getFloatAbsoluteTolerance();
                optStr += " " + Double.toString(abstol);
            }
            if (settings.isFloatRelativeToleranceSpecified()) {
                optStr += " " + ClicsValidatorSettings.CLICS_VTOKEN_FLOAT_RELATIVE_TOLERANCE;
                double reltol = settings.getFloatRelativeTolerance();
                optStr += " " + Double.toString(reltol);
            }

        }
        return optStr;
    }

    private String getTeamOutputFilename(int dataSetNumber) {
        return prefixExecuteDirname("teamoutput." + dataSetNumber + ".txt");
    }

    /**
     * Set results of validation using the PC2Validator into executionData.
     * 
     * @param resultsFileName
     * @param logger
     */
    protected void updatePC2ValidatorResults(String resultsFileName, Log logger) {

        IResultsParser parser = new XMLResultsParser();
        parser.setLog(log);

        /**
         * returns true if valid XML and found outcome tag.
         */
        boolean done = parser.parseValidatorResultsFile(prefixExecuteDirname(resultsFileName));
        Hashtable<String, String> results = parser.getResults();

        if (parser.getException() != null) {
            logger.log(Log.WARNING, "Exception parsing XML in file '" + resultsFileName + "'", parser.getException());

        } else if (done && results != null && results.containsKey("outcome")) {
            // non-IJRM does not require security, but if it is IJRM it better have security.

            if (!problem.isInternationalJudgementReadMethod() || (results.containsKey("security") && resultsFileName.equals(results.get("security")))) {
                // Found the string
                executionData.setValidationResults(results.get("outcome"));
                executionData.setValidationSuccess(true);
            } else {
                setException("validationCall - results file did not contain security");
                logger.warning("validationCall - results file did not contain security");
                logger.warning(resultsFileName + " != " + results.get("security"));
            }
        } else {
            if (!done) {
                // SOMEDAY show user message
                setException("Error parsing/reading results file, check log");
                logger.warning("Error parsing/reading results file, check log");
            } else if (results != null && (!results.containsKey("outcome"))) {
                // SOMEDAY show user message
                setException("Error parsing/reading results file, check log");
                logger.warning("Error: could not find 'outcome' in results file, check log");
            } else {
                // SOMEDAY show user message
                logger.warning("Error parsing results file, check log");
            }
        }

    }

    /**
     * Saves the results of validation using the ClicsValidator into executionData.
     * 
     * Validation results are found in one or both of two files in the feedbackDir directory.
     * The first file, named as defined by the constant {@link ClicsValidator#CLICS_JUDGEMENT_FEEDBACK_FILE_NAME},
     * contains the judgement string assigned by the {@link ClicsValidator}.
     * The second file, named as defined by the constant {@link ClicsValidator#CLICS_JUDGEMENT_DETAILS_FEEDBACK_FILE_NAME},
     * contains judgement details for submissions which were judged "no".
     * 
     * @param exitCode the exit code returned by the ClicsValidator
     * @param feedbackDirPath the directory into which the ClicsValidator (should have) written feedback information
     * @param logger the log to be used for logging
     */
    private void updateClicsValidatorResults(int exitCode, String feedbackDirPath, Log logger) {

        // save exit code in executionData
        executionData.setValidationReturnCode(exitCode);

        // save validation success (note that this refers to whether the validator completed successfully,
        // not whether the run was judged "correct".
        if (exitCode == ClicsValidator.CLICS_VALIDATOR_JUDGED_RUN_SUCCESS_EXIT_CODE || exitCode == ClicsValidator.CLICS_VALIDATOR_JUDGED_RUN_FAILURE_EXIT_CODE) {
            executionData.setValidationSuccess(true);
        } else {
            executionData.setValidationSuccess(false);
        }

            // check for feedback from validator
        if (new File(feedbackDirPath).exists()) {

            // check for judgement feedback file
            if (!feedbackDirPath.endsWith(File.separator)) {
                feedbackDirPath += File.separator;
            }
            String judgementFileName = feedbackDirPath + ClicsValidator.CLICS_JUDGEMENT_FEEDBACK_FILE_NAME;
                File judgementFile = new File(judgementFileName);
                if (judgementFile.exists()) {

                    // get the judgement out of the feedback file
                    String judgement = readFileAsString(judgementFileName);

                //put the judgement from the validator into the executionData object
                    executionData.setValidationResults(judgement);
                    log.info("Saving CLICS Validator judgement '" + judgement + "'");

                } else {
                //we found no judgement file in the feedback dir -- that's a problem (the Validator implementation should ALWAYS create one)!
                log.warning ("No Clics Validator judgement file '" + judgementFileName + "' found in feedback directory '" + feedbackDirPath + "'");
                saveDefaultClicsValidatorResult(exitCode);
                }

            // check for a judgement details file 
            String detailsFileName = feedbackDirPath + ClicsValidator.CLICS_JUDGEMENT_DETAILS_FEEDBACK_FILE_NAME ;
                File detailsFile = new File(detailsFileName);
                if (detailsFile.exists()) {

                    // get details out of the feedback file
                    String details = readFileAsString(detailsFileName);

                    executionData.setAdditionalInformation(details);
                log.info("Saving Clics Validator 'Additional Details' string '" + details + "'");

            } else {
                log.info("Clics Validator provided no 'Additional Details'");
                executionData.setAdditionalInformation(null);
                }

            } else {
            
            //we SHOULD have had a feedback directory -- but we didn't!
            log.warning("No CLICS validator feedback directory named '" + feedbackDirPath + "' found");
            saveDefaultClicsValidatorResult(exitCode);
        }
            }

    /**
     * Saves into the current executionData object a Clics Validator result string based on the specified exitCode.
     * This method is used when there is no feedback information from the validator available in the feedback directory. 
     * 
     * @param exitCode the exit code from the validator, used to select the appropriate result string
     */
    private void saveDefaultClicsValidatorResult(int exitCode) {

        // select result based on the exit code since we apparently have no feedback file info to look at
        String resultString = "";
        if (exitCode == ClicsValidator.CLICS_VALIDATOR_JUDGED_RUN_SUCCESS_EXIT_CODE) {

            resultString = ClicsValidator.CLICS_CORRECT_ANSWER_MSG;

        } else if (exitCode == ClicsValidator.CLICS_VALIDATOR_JUDGED_RUN_FAILURE_EXIT_CODE) {

            //since we have no feedback to tell us anything about WHY it failed, we'll default to the simplest: "wrong answer"
            resultString = ClicsValidator.CLICS_WRONG_ANSWER_MSG;

        } else if (exitCode == ClicsValidator.CLICS_VALIDATOR_ERROR_EXIT_CODE) {
            
            resultString = "Clics Validator exited with error; exit code = " + ClicsValidator.CLICS_VALIDATOR_ERROR_EXIT_CODE;
            
        } else {
            log.severe("Unknown Clics Validator exit code: " + exitCode);
            resultString = "Unknown exit code from Clics Validator: " + exitCode;
        }

        executionData.setValidationResults(resultString);
        log.info("Saving Clics Validator result: '" + resultString + "'");
    }
    /**
     * Stores the results of the execution of a Custom Validator which uses the PC2 Validator Interface. 
     * Currently this method just delegates to {@link #updatePC2ValidatorResults(String, Log)}; it is
     * provided in the event of a future need to distinguish between the real (internal) PC2Validator 
     * and a Custom Validator which uses the PC2 Validator Interface.
     * 
     * @param resultsFileName
     *            the name of the file containing the results
     * @param aLog
     *            the Log to be used for logging
     */
    private void updateCustomPC2InterfaceValidatorResults(String resultsFileName, Log aLog) {
        updatePC2ValidatorResults(resultsFileName, aLog);
    }

    /**
     * Stores the results of the execution of a Custom Validator which uses the Clics Validator Interface. 
     * 
     * Currently this method just delegates to {@link #updateClicsValidatorResults(int, String, Log)}; 
     * it is provided in the event of a future need to distinguish between the real (internal) ClicsValidator 
     * and a Custom Validator which uses the Clics Validator Interface.
     * 
     * @param exitcode
     *            the exitcode returned by the Custom Validator
     * @param feedbackDirPath
     *            the path to the feedback directory
     * @param feedbackFileBaseName
     *            the base name for feedback files in the feedback directory
     * @param aLog
     *            the Log to be used for logging
     */
    private void updateCustomClicsInterfaceValidatorResults(int exitcode, String feedbackDirPath, Log aLog) {
        updateClicsValidatorResults(exitcode, feedbackDirPath, aLog);
    }

    /**
     * Reads all of the lines in the specified file and returns them concatenated as a single string. Note that this should only be used for text files, and small ones at that. If any error occurs
     * while reading the file then the empty string is returned.
     * 
     * @param fileName
     *            the name of the file to be read
     * 
     * @return a String containing the contents of the specified file
     */
    private String readFileAsString(String fileName) {
        String retStr = "";
        try {

            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                retStr += line;
            }

            bufferedReader.close();
        } catch (Exception e) {
            log.severe("Exception reading file: " + e.getMessage());
        }

        return retStr;

    }

    /**
     * Create an Execution Exception.
     * 
     * @param inExecutionData
     * @param string
     */
    private void setException(String string) {
        executionData.setExecutionException(new Exception(string));
    }

    /**
     * This method attempts to find a path to a "pc2.jar" file.
     * 
     * It starts by assuming a default return value of "." (the current directory). It then makes various attempts to update (improve) this default, as follows:
     * 
     * It starts by choosing "./build/prod" as the default path, or if that directory doesn't exist then it chooses "/software/pc2/cc/projects/pc2v9/build/prod".
     * 
     * It then searches the current CLASSPATH (Java System property "java.class.path") for a classpath element ending in "pc2.jar". If one is found, the default path is updated to be the parent
     * directory of the indicated pc2.jar file.
     * 
     * Finally, if no pc2.jar was found in the classpath it checks for the existence of file "dist/pc2.jar"; if that exists then the parent directory of that file (i.e., "dist") is returned as the
     * path.
     * 
     * @return a String giving the path to a pc2.jar file, or "." if no pc2.jar file could be found. In any case the returned String is guaranteed to end with a File.separator character
     * 
     * @throws IOException
     *             if any problem occurs accessing any of the specified paths
     */
    protected String findPC2JarPath() {
        String jarDir = ".";
        try {
            // start by assuming a default path
            String defaultPath = new File("./build/prod").getCanonicalPath();
            // for CruiseControl, will not be needed with jenkins
            if (!new File(defaultPath).exists()) {
                defaultPath = "/software/pc2/cc/projects/pc2v9/build/prod";
            }
            // assume the directory containing the pc2.jar will be the assumed default path
            jarDir = defaultPath;

            // see if "pc2.jar" can be found in the CLASSPATH
            String cp = System.getProperty("java.class.path");
            StringTokenizer st = new StringTokenizer(cp, File.pathSeparator);
            while (st.hasMoreTokens()) {
                // get one component of the classpath
                String token = st.nextToken();
                // try constructing a File from that component
                File dir = new File(token);
                // see if the component is a file that ends in "pc2.jar"
                if (dir.exists() && dir.isFile() && dir.toString().endsWith("pc2.jar")) {
                    // yes, we found a pc2.jar file in the classpath; get the path to its parent directory
                    jarDir = new File(dir.getParent()).getCanonicalPath() + File.separator;
                    break;
                }
            }

            // if we didn't find a pc2.jar in the classpath, the jarDir will still be the same as the default path
            if (defaultPath.equals(jarDir)) {
                // we didn't find a pc2.jar in the classpath; see if we can find one in directory "dist"
                File dir = new File("dist/pc2.jar");
                if (dir.isFile()) {
                    // we found a pc2.jar in "dist"; get the path to its parent directory
                    jarDir = new File(dir.getParent()).getCanonicalPath() + File.separator;
                }
            }

        } catch (IOException e) {
            log.log(Log.WARNING, "Trouble locating pc2home: " + e.getMessage(), e);
        }

        // when we get here, jarDir contains either a path to a pc2.jar, or ".".
        // In either case, make sure it ends with a file separator
        if (!jarDir.endsWith(Matcher.quoteReplacement(File.separator))) {
            jarDir += Matcher.quoteReplacement(File.separator);
        }
        return jarDir;
    }

    /**
     * Returns true if validator should be run/executed.
     * 
     * @return true if should be validated.
     */
    public boolean isValidated() {
        return (problem.isValidatedProblem() && (!isTestRunOnly()));
    }

    public String getFileNameFromUser() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    JFileChooser chooser = new JFileChooser(mainFileDirectory);
                    try {
                        chooser.setDialogTitle("Open Test Input File");
                        int returnVal = chooser.showOpenDialog(null);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            mainFileDirectory = chooser.getCurrentDirectory().getAbsolutePath();
                            fileFromUser = chooser.getSelectedFile().getCanonicalFile().toString();
                        } else {
                            fileFromUser = null;
                        }
                    } catch (Exception e) {
                        log.log(Log.CONFIG, "Error getting selected file, try again.", e);
                    }
                    chooser = null;
                }
            });
        } catch (Exception e) {
            log.throwing("Executable", "getFileNameFromUser", e);
        }
        return fileFromUser;
    }

    /**
     * Select using File Open GUI file and copy to execute directory.
     * 
     */
    protected void selectAndCopyDataFile(String inputFileName) throws Exception {
        // Prompt for filename

        String pickedFileName = getFileNameFromUser();

        if (pickedFileName != null) {
            SerializedFile data = new SerializedFile(pickedFileName);

            if (data.getBuffer() == null) {
                throw new Exception("datafile does not exist/can not be read " + pickedFileName);
            }

            createFile(data, inputFileName);
            data = null;
        }
    }

    /**
     * Execute the submission against a single data set.
     * 
     * @param dataSetNumber
     *            a zero-based data set number
     * @return true if execution worked successfully.
     */
    protected boolean executeProgram(int dataSetNumber) {
        
        boolean proceedToValidation = false;
        String inputDataFileName = null;

        // a one-based test data set number
        int testSetNumber = dataSetNumber + 1;

        try {
            if (isJudge()) {
                controller.sendExecutingMessage(run);
            }

            if (!isTestRunOnly()) {
                log.log(Log.INFO, "Executing run " + run.getNumber() + " from " + run.getSubmitter().getTripletKey() + " test set " + testSetNumber);
            }

            log.info("Constructing ExecuteTimer...");
            executionTimer = new ExecuteTimer(log, problem.getTimeOutInSeconds(), executorId, isUsingGUI());
//            executionTimer.startTimer();    //TODO: why is this here?  method runProgram() (called below) starts the timer (which is where it should be done).
            log.info("Created new ExecuteTimer: " + executionTimer.toString());
            
            if (problem.getDataFileName() != null) {
                if (problem.isReadInputDataFromSTDIN()) {
                    // we are using createTempFile just to get a temp name, not to avoid conflicts
                    File output = File.createTempFile("__t", ".in", new File(getExecuteDirectoryName()));
                    inputDataFileName = prefixExecuteDirname(output.getName());
                    output.delete(); // will be created later
                } else {
                    inputDataFileName = prefixExecuteDirname(problem.getDataFileName());
                }
            }

            if (isTestRunOnly()) {
                /**
                 * Team executing run
                 */

                if (inputDataFileName != null && problem.isReadInputDataFromSTDIN()) {
                    selectAndCopyDataFile(inputDataFileName);
                } else if (inputDataFileName != null) {
                    String sourceFileName = getDirName(runFiles.getMainFile()) + File.separator + problem.getDataFileName();

                    SerializedFile dataFile = new SerializedFile(sourceFileName);
                    if (dataFile != null) {
                        createFile(dataFile, inputDataFileName);
                    }
                    dataFile = null;
                }

                if (inputDataFileName != null) {
                    // A file must be present
                    if (!(new File(inputDataFileName).isFile())) {

                        if (executionTimer != null) {
                            executionTimer.stopTimer();
                        }

                        throw new SecurityException("Expected data file, was not created, file name is " + problem.getDataFileName());
                    }
                }

            } else {
                /**
                 * Judge execute run
                 */

                // Extract the judge data file for this problem and dataSetNumber.

                if (!problem.isUsingExternalDataFiles()) {
                    /**
                     * Only extract internal data files.
                     */
                    if (inputDataFileName != null && problemDataFiles.getJudgesDataFiles() != null) {

                        if (overwriteJudgesDataFiles) {
                            // create the judges data file on disk.

                            if (!createFile(problemDataFiles.getJudgesDataFiles(), dataSetNumber, inputDataFileName)) {
                                throw new SecurityException("Unable to create data file " + inputDataFileName);
                            }

                            String actualDataFile = null;
                            try {
                                actualDataFile = problemDataFiles.getJudgesDataFiles()[dataSetNumber].getName();
                            } catch (Exception e) {
                                actualDataFile = "Problem getting judge data file name for set " + dataSetNumber + " " + e.getMessage();
                                log.log(Log.DEBUG, e.toString(), e);
                            }

                            log.info("(Internal) Input data file: " + actualDataFile);

                        }

                    }
                    // Else, leave whatever data file is present.
                } else {

                    /*
                     * External data files (not inside of pc2). On local disk.
                     */

                    SerializedFile serializedFile = problemDataFiles.getJudgesDataFiles()[dataSetNumber];
                    String dataFileName = Utilities.locateJudgesDataFile(problem, serializedFile, getContestInformation().getJudgeCDPBasePath(), Utilities.DataFileType.JUDGE_DATA_FILE);

                    if (dataFileName != null) {
                        // Found file

                        File dataFile = new File(dataFileName);
                        inputDataFileName = dataFile.getCanonicalPath();
                        log.info("(External) Input data file: " + inputDataFileName);

                    } else {

                        // Did not find file

                        String expectedFileName = serializedFile.getName();
                        log.log(Log.DEBUG, "For problem " + problem + " test case " + testSetNumber + " expecting file " + expectedFileName + " in dir " + problem.getCCSfileDirectory());
                        FileNotFoundException notFound = new FileNotFoundException(expectedFileName + " for test case " + testSetNumber);
                        executionData.setExecutionException(notFound);
                        log.info("(External) Input data file: NOT FOUND ");
                    }
                }
            }

            // SOMEDAY execute the language.getProgramExecuteCommandLine();

            BufferedOutputStream stdoutlog = new BufferedOutputStream(new FileOutputStream(prefixExecuteDirname(EXECUTE_STDOUT_FILENAME), false));
            BufferedOutputStream stderrlog = new BufferedOutputStream(new FileOutputStream(prefixExecuteDirname(EXECUTE_STDERR_FILENAME), false));

            String cmdline = language.getProgramExecuteCommandLine();

            if (!isTestRunOnly()) {
                if (language.isUsingJudgeProgramExecuteCommandLine()) {

                    /**
                     * Use Judge execution command line (override).
                     */
                    cmdline = language.getJudgeProgramExecuteCommandLine();
                    log.info("Using judge command line " + cmdline);
                }

            }

            log.log(Log.DEBUG, "before substitution: " + cmdline);

            /**
             * Special substitution for entry_point.
             * We only do this substition for execution of the run, not compiles or validators, so it can't
             * be done by substitutionAllStrings without refactoring.
             */
            if (!StringUtilities.isEmpty(run.getEntryPoint())) {
                // change Constants.CMDSUB_BASENAME_VARNAME to entry_point rather than basename from {:mainfile} before
                // other substitutions (overrides :mainfile)
                cmdline = replaceString(cmdline, Constants.CMDSUB_BASENAME_VARNAME, run.getEntryPoint());
            }
            cmdline = substituteAllStrings(run, cmdline, dataSetNumber+1);
            log.log(Log.DEBUG, "after  substitution: " + cmdline);

            /**
             * Insure that the first command in the command line can be executed by prepending the execute directory name.
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
                /**
                 * If the first word is a existing file, use the full path
                 */
                cmdline = f.getCanonicalPath();
            }

            boolean autoStop = false;
            if (!isTestRunOnly()) {
                /**
                 * Auto stop on time limit exceeded - Judge (aka non-Team) only.
                 */
                autoStop = true;
            }

            //start the program executing.  Note that runProgram() sets the "startTimeNanos" timestamp 
            /// immediately prior to actually "execing" the process.
            log.info("starting team program...");
            process = runProgram(cmdline, "Executing...", autoStop, executionTimer);
            
            //make sure we succeeded in getting the external process going
            if (process == null) {
                log.warning("team program failed to start (runProgram() returned null process)");
                log.info("stopping ExecuteTimer " + executionTimer.toString());
                executionTimer.stopTimer();
                stderrlog.close();
                stdoutlog.close();
                executionData.setExecuteSucess(false);
                return false;
            } else {
                log.info("created new team process " + getProcessID(process));
            }
            
            //create a Timer to run the TLE kill task
            log.info("constructing new TLE-Timer...");
            Timer timeLimitKillTimer = new Timer("TLE-Timer");
            log.info("got new TLE-Timer: " + timeLimitKillTimer.toString());
            
            //create a TimerTask to kill the process if it exceeds the problem time limit
            
            killedByTimer = false ;
            
            TimerTask task = new TimerTask() {
                public void run() {
                    
                    log.info("running TLE-Timer kill task...");
                    
                    //first step: stop the process from running further
                    if (executionTimer != null) {
                        log.info("calling stopIOCollectors() in ExecuteTimer " + executionTimer.toString());
                        executionTimer.stopIOCollectors();
                    }
                    
                    //make sure the process is gone (the call to stopIOCollectors(), above, will call destroy() first --
                    // but only if the executionTimer is not null)
                    if (process != null) {
                        log.info("calling process.destroy() for process " + getProcessID(process));
                        process.destroy();
                    }
                    
                    killedByTimer = true;
                }
            };
            
            //set the TLE kill task delay to the number of milliseconds allowed by the problem
            long delay = (long) (problem.getTimeOutInSeconds() * 1000) ;
            
            //schedule the TLE kill task with the Timer -- but only for judged runs (i.e., non-team runs)
            if (autoStop) {
                log.info ("scheduling kill task with TLE-Timer with " + delay + " msec delay");
                timeLimitKillTimer.schedule(task, delay);
            }
            
            log.info("creating IOCollectors...");
            // Create a stream that reads from the stdout of the child process
            BufferedInputStream childOutput = new BufferedInputStream(process.getInputStream());
            // Create a stream that reads from the stderr of the child process
            BufferedInputStream childError = new BufferedInputStream(process.getErrorStream());

            //create collectors for reading the child process's stdout and stderr output
            IOCollector stdoutCollector = new IOCollector(log, childOutput, stdoutlog, executionTimer, getMaxFileSize() + ERRORLENGTH);
            IOCollector stderrCollector = new IOCollector(log, childError, stderrlog, executionTimer, getMaxFileSize() + ERRORLENGTH);

            //store references to the collectors in the execution timer 
            log.info("calling setIOCollectors() for ExecuteTimer " + executionTimer.toString());
            executionTimer.setIOCollectors(stdoutCollector, stderrCollector);
            log.info("calling setProc(" + getProcessID(process) + ") in ExecuteTimer " + executionTimer.toString());
            executionTimer.setProc(process);

            log.info("starting IOCollectors...");
            stdoutCollector.start();
            stderrCollector.start();

            //check if problem is configured with an input data file which the team program (process) should read from stdin
            if (inputDataFileName != null && problem.isReadInputDataFromSTDIN()) {
                
                //yes, problem needs data file sent to its stdin
                log.info("Using STDIN from file " + inputDataFileName);

                //create streams for input data file and stdin for the process
                BufferedOutputStream out = new BufferedOutputStream(process.getOutputStream());  //team's stdin stream
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(inputDataFileName));  //data file to read
                
                //copy bytes from input data file to process's stdin, 32K at a time
                byte[] buf = new byte[32768];
                int c;
                try {
                    while ((c = in.read(buf)) != -1) {
                        out.write(buf, 0, c);
                    }
                } catch (java.io.IOException e) {
                    log.info("Caught a " + e.getMessage() + " do not be alarmed.");
                }

                //close the input data file and process stdin streams if they're still open
                // (note that they could have been implicitly closed because a timer killed the process to which they were connected,
                // and that this in turn can throw IOException, at least in Java8; 
                // see https://stackoverflow.com/questions/25175882/java-8-filteroutputstream-exception/)
                try {
                    if (in!=null) {  
                        in.close();
                    }
                } catch (IOException e) {
                    log.info("Caught (and ignoring) an IOException while closing the problem data file input stream "
                            + " (this can happen if the team process was terminated by timer).");
                }

                try {
                    if (out!=null) {
                        out.close();
                    }
                } catch (IOException e) {
                    log.info("Caught (and ignoring) an IOException while closing team stdin stream "
                                    + " (this can happen if the team process was terminated by timer).");
                }
              
            }

            //wait (block this thread) until both IOCollectors terminate, which happens when either 
            //  (1) EOF is reached on the child stdout/err,
            //  (2) the collector is halted by the ExecuteTimer (either because the time limit was exceeded or the 
            //      operator presses the "Terminate" button), or
            //  (3) the collector collects maxFileSize input from the child process
            log.info("waiting for IOCollectors to terminate...");
            stdoutCollector.join();
            stderrCollector.join();

            //when we reach here we know that both IOCollectors have terminated, which means one (or more) of the three conditions above
            // is true: either the child has stopped producing output (generated EOF on both stdout and stderr), the timer has terminated the
            // IOCollectors due to either a time limit or the operator pressing the "Terminate" button, or the IOCollector reached maximum
            // output.  In all these cases we need to wait for the process to die.
            
            //wait for the process to finish
            log.info("waiting for team process " + getProcessID(process) + " to exit...");
            int exitCode = process.waitFor();
            
            //timestamp the end of the process's execution
            endTimeNanos = System.nanoTime();
            
            log.info("team process returned exit code " + exitCode);
            
            //get rid of the TLE timer (whether the TLE-kill task has been fired or not)
            log.info("cancelling TLE-Timer (note: this does not stop any already-running TLE-Timer tasks...)");
            timeLimitKillTimer.cancel();
            
//            System.out.println("  Process run time was " + getExecutionTimeInMSecs() + "ms");

            //update executionData info
            executionData.setExecuteExitValue(exitCode);
            executionData.setExecuteTimeMS(getExecutionTimeInMSecs());
            
            boolean runTimeLimitWasExceeded = getExecutionTimeInMSecs() > problem.getTimeOutInSeconds()*1000 ;
            executionData.setRunTimeLimitExceeded(runTimeLimitWasExceeded);
 
            if (executionData.isRunTimeLimitExceeded()) {
                log.info("Run exceeded problem time limit of " + problem.getTimeOutInSeconds() + " secs: actual run time = " + executionData.getExecuteTimeMS() + " msec;  Run = " + run);
            }

            boolean terminatedByOperator = false;
            if (executionTimer != null) {
                log.info("stopping ExecuteTimer " + executionTimer.toString());
                executionTimer.stopTimer();
                terminatedByOperator = executionTimer.isTerminatedByOperator();
                
            }

            log.info("calling Process.destroy() on process " + getProcessID(process) );
            process.destroy();

            // if the process generated a Runtime Error and did NOT exceed time limit, add error msg to team output
            if (!executionData.isRunTimeLimitExceeded() && !terminatedByOperator && exitCode != 0) {
                String msg = "Note: program exited with non-zero exit code '" + exitCode + "'" + NL;
                stdoutlog.write(msg.getBytes());
            }

            stdoutlog.close();
            stderrlog.close();

            executionData.setExecuteSucess(true);
                        
            executionData.setExecuteProgramOutput(new SerializedFile(prefixExecuteDirname(EXECUTE_STDOUT_FILENAME)));
            executionData.setExecuteStderr(new SerializedFile(prefixExecuteDirname(EXECUTE_STDERR_FILENAME)));

            // teams output file - single file name
            SerializedFile userOutputFile = executionData.getExecuteProgramOutput();
            createFile(userOutputFile, prefixExecuteDirname(userOutputFile.getName()));

            String teamsOutputFilename = getTeamOutputFilename(dataSetNumber);

            createFile(userOutputFile, teamsOutputFilename); // Create a per test case Team's output file
            teamsOutputFilenames.add(teamsOutputFilename); // add to list
            if (executionData.getExecuteExitValue() != 0) {
                long returnValue = ((long) executionData.getExecuteExitValue() << 0x20) >>> 0x20;

                PrintWriter exitCodeFile = null;
                try {
                    exitCodeFile = new PrintWriter(new FileOutputStream(prefixExecuteDirname(EXIT_CODE_FILENAME), false), true);
                    exitCodeFile.write("0x" + Long.toHexString(returnValue).toUpperCase());
                } catch (FileNotFoundException e) {
                    log.log(Log.WARNING, "Unable to open/write file " + EXIT_CODE_FILENAME, e);
                    exitCodeFile = null;
                } finally {
                    if (exitCodeFile != null) {
                        exitCodeFile.close();
                    }
                }
            } else {
                // exit was clean, clear old EXIT_CODE_FILENAME if it exist
                File exitFile = new File(prefixExecuteDirname(EXIT_CODE_FILENAME));
                if (exitFile.exists()) {
                    exitFile.delete();
                }
            }
            if (isAppendStderrToStdout() && executionData.getExecuteStderr() != null) {
                byte[] errBuff = executionData.getExecuteStderr().getBuffer();
                FileOutputStream outputStream = null;
                try {
                    if (errBuff != null && errBuff.length > 0) {
                        outputStream = new FileOutputStream(teamsOutputFilename, true);
                        outputStream.write(("*** Team STDERR Follows:" + NL).getBytes());
                        outputStream.write(errBuff, 0, errBuff.length);
                        outputStream.close();
                    }
                } catch (IOException e) {
                    log.log(Log.WARNING, "Unable to append to file " + teamsOutputFilename, e);
                }
            }

            proceedToValidation = true;
            
        } catch (Exception e) {
            if (executionTimer != null) {
                executionTimer.stopTimer();
            }
            // SOMEDAY handle exception
            log.log(Log.INFO, "Exception in executeProgram()", e);
            executionData.setExecutionException(e);
            throw new SecurityException(e);
        }

        if (executionData.isRunTimeLimitExceeded()) {

            Judgement judgement = JudgementUtilites.findJudgementByAcronym(contest, "TLE");
            String judgementString = "No - Time Limit Exceeded"; // default
            if (judgement != null) {
                judgementString = judgement.getDisplayName();
            }

            executionData.setValidationResults(judgementString);
            executionData.setValidationSuccess(true);
            proceedToValidation = false;
        }

        if (executionData.getExecuteExitValue() != 0  &&  !killedByTimer) {
            
            Judgement judgement = JudgementUtilites.findJudgementByAcronym(contest, "RTE");
            String judgementString = "No - Run-time Error"; // default
            if (judgement != null) {
                judgementString = judgement.getDisplayName();
            }

            executionData.setValidationResults(judgementString);
            executionData.setValidationSuccess(true);
            proceedToValidation = false;
        }
        
        return proceedToValidation;
    }

    private boolean isAppendStderrToStdout() {
        String key = "judge.appendstderr";
        try {
            return StringUtilities.getBooleanValue(IniFile.getValue(key), false);
        } catch (Exception e) {
            System.err.println("Error fetching boolean value for " + key + " " + e.getMessage());
        }
        return false;
    }

    /**
     * Returns the execution time of the child process; assumes that both {@link #startTimeNanos} and
     * {@link #endTimeNanos} have been set prior to calling this method.  The method works by computing the difference
     * between these two variables and converting it from nanoseconds to milliseconds, rounded.
     * 
     * @return a long containing the (rounded) millseconds of execution time
     */
    private long getExecutionTimeInMSecs() {
                
        long diffNanos = endTimeNanos - startTimeNanos ;
        //round and convert to msecs
        long diffMillis = (diffNanos + 500_000) / NANOSECS_PER_MILLISEC ;
        
        return diffMillis;
    }

    
    protected boolean isValidDataFile(Problem inProblem) {
        boolean result = false;
        if (inProblem.getDataFileName() != null && inProblem.getDataFileName().trim().length() > 0) {
            result = true;
        }
        if (inProblem.isUsingExternalDataFiles()) {
            return true;
        }
        return result;
    }

    private boolean isJudge() {
        return contest.getClientId().getClientType().equals(ClientType.Type.JUDGE);
    }

    /**
     * Extract source file and run compile command line script.
     * 
     * @return true if executable is created.
     * 
     * TODO: this method needs to be updated to use the nanoTimer (see method {@link #executeProgram(int)}) 
     */
    protected boolean compileProgram() {
        int exitCode = 0;
        try {

            if (isJudge()) {
                controller.sendCompilingMessage(run);
            }

            packageName = "";
            packagePath = "";
            
            String programName = language.getExecutableIdentifierMask();
            
            // the "executable" program name is the entry point, if one exists, so try to substitute that first
            if (!StringUtilities.isEmpty(run.getEntryPoint())) {
                // change Constants.CMDSUB_BASENAME_VARNAME to entry_point rather than basename from {:mainfile} before
                // other substitutions (overrides :mainfile)
                programName = replaceString(programName, Constants.CMDSUB_BASENAME_VARNAME, run.getEntryPoint());
            }

            // This used to just replace the {:basename}, but there is no reason not to run it
            // through the substituteAllStrings() especially since we now have conditional suffix
            // substitution string.
            programName = substituteAllStrings(run, programName);
            
            if (runFiles.getMainFile().getName().endsWith("java")) {
                packageName = searchForPackage(prefixExecuteDirname(runFiles.getMainFile().getName()));
                packagePath = replaceString(packageName, ".", File.separator);
            }

            // Check whether the team submitted an executable, if they did remove
            // it.
            File program = new File(prefixExecuteDirname(programName));
            if (program.exists()) {

                // SOMEDAY log Security Warning ?
                log.config("Team submitted an executable " + programName);
                program.delete();
            }

            log.log(Log.DEBUG, "before substitution: " + language.getCompileCommandLine());
            String cmdline = substituteAllStrings(run, language.getCompileCommandLine());
            log.log(Log.DEBUG, "after  substitution: " + cmdline);

            BufferedOutputStream stdoutlog = new BufferedOutputStream(new FileOutputStream(prefixExecuteDirname(COMPILER_STDOUT_FILENAME), false));
            BufferedOutputStream stderrlog = new BufferedOutputStream(new FileOutputStream(prefixExecuteDirname(COMPILER_STDERR_FILENAME), false));

            executionTimer = new ExecuteTimer(log, getCompilationTimeLimit(), executorId, isUsingGUI());
//            executionTimer.startTimer();    //TODO: why is this here, when method runProgram() invokes startTimer()?  (it should only be done in runProgram...)

            long startSecs = System.currentTimeMillis();

            process = runProgram(cmdline, "Compiling...", false, executionTimer);
            if (process == null) {
                executionTimer.stopTimer();
                stderrlog.close();
                stdoutlog.close();
                // errorString will be set by  (?huh?)
                executionData.setCompileExeFileName("");
                executionData.setCompileSuccess(false);
                executionData.setCompileResultCode(1);
                return false;
            }
            // This reads from the stdout of the child process
            BufferedInputStream childOutput = new BufferedInputStream(process.getInputStream());
            // The reads from the stderr of the child process
            BufferedInputStream childError = new BufferedInputStream(process.getErrorStream());

            IOCollector stdoutCollector = new IOCollector(log, childOutput, stdoutlog, executionTimer, getMaxFileSize() + ERRORLENGTH);
            IOCollector stderrCollector = new IOCollector(log, childError, stderrlog, executionTimer, getMaxFileSize() + ERRORLENGTH);

            executionTimer.setIOCollectors(stdoutCollector, stderrCollector);
            executionTimer.setProc(process);

            stdoutCollector.start();
            stderrCollector.start();

            // waiting for the process to finish execution...
            // executionData.setCompileCompilerReturnCode(process.waitFor());

            stdoutCollector.join();
            stderrCollector.join();

            // if(isJudge && executionTimer != null) {
            if (executionTimer != null) {
                executionTimer.stopTimer();
            } else {
                // SOMEDAY why do we care??
                log.config("compileCall() executionTimer == null");
            }

            if (process != null) {
                process.destroy();
                exitCode = process.waitFor();
            }

            stdoutlog.close();
            stderrlog.close();

            executionData.setCompileTimeMS(System.currentTimeMillis() - startSecs);
            executionData.setCompileStdout(new SerializedFile(prefixExecuteDirname(COMPILER_STDOUT_FILENAME)));
            executionData.setCompileStderr(new SerializedFile(prefixExecuteDirname(COMPILER_STDERR_FILENAME)));

            program = new File(prefixExecuteDirname(programName));
            File programWithPackage = program;
            if (packagePath.length() > 0) {
                // if there is a packagePath and javac was invoked with `-d .` it will place the class
                // under the packagePath
                programWithPackage = new File(prefixExecuteDirname(packagePath)+File.separatorChar+programName);
            }
            if ((program.exists() || programWithPackage.exists()) && !language.isInterpreted()) {
                // now check if the programWithPackage does not exist
                if (!programWithPackage.exists()) {
                    // programWithPackage != programName we have a packagePath
                    // move all .class files under packagePath
                    File path = new File(prefixExecuteDirname(packagePath));
                    if (path.mkdirs()) {
                        moveClassToPath(getExecuteDirectoryName(), path);
                    }
                }
                executionData.setCompileExeFileName(programName);
                executionData.setCompileSuccess(true);
                executionData.setCompileResultCode(0);
                return true;

            } else {
                if (language.isInterpreted() && exitCode == 0) {
                    executionData.setCompileExeFileName(runFiles.getMainFile().getName());
                    executionData.setCompileSuccess(true);
                    executionData.setCompileResultCode(0);
                    return true;
                } else {
                    executionData.setCompileExeFileName("");
                    executionData.setCompileSuccess(false);
                    executionData.setCompileResultCode(2);
                    return false;
                }
            }

        } catch (Exception e) {
            if (executionTimer != null) {
                executionTimer.stopTimer();
            }
            // SOMEDAY handle exception
            log.log(Log.INFO, "Exception ", e);
            throw new SecurityException(e);
        }
    }

    private void moveClassToPath(String folderName, File path) {
        File folder = new File(folderName);
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            File file = listOfFiles[i];
            if (file.getName().endsWith(".class")) {
                file.renameTo(new File(path+File.separator+file.getName()));                
            }
        }
    }

    private String searchForPackage(String file) {
        String name = "";
        Scanner scanner;
        try {
            scanner = new Scanner(new File(file));
            while (scanner.hasNextLine()) {
               String lineFromFile = scanner.nextLine();
               if (lineFromFile.startsWith("package ")) { 
                   // a match!
                   name = lineFromFile.substring(8).replaceAll(" ", "");
                   name = replaceString(name, ";", ".");
                   break;
               }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

    /**
     * This method returns the maximum allowed output file size for the current problem, in BYTES.
     * 
     * The method first checks to see if the current problem has a non-zero maximum output file size
     * specified.  If so, that value is converted to bytes (it is stored in the {@link Problem} class
     * as a value in KB) and returned.  If not, the current global (contest-wide) max file size value
     * (which is stored in the {@link IInternalContest} object's {@link ContestInformation} object, in
     * BYTES) is returned.
     * 
     *  If the current problem is null, an error is logged and a value of zero is returned.
     * 
     * 
     * @return max currently allowed output file size.
     */
    private long getMaxFileSize() {
        
        //make sure we have a Problem defined
        if (problem != null) {
            
            //check if the problem has its own (problem-specific) output file size limit, which is noted
            //  by having a limit value in the problem which is greater than zero
            long problemLimit = problem.getMaxOutputSizeKB();
            if (problemLimit > 0) {
                //problem has its own limit; convert from KB to BYTES and return that
                return problemLimit * 1024;
            } else {
                //problem doesn't have its own limit; return the global (contest-wide) value
                return contest.getContestInformation().getMaxOutputSizeInBytes();
            }
                
        } else {
            
            //problem is null; log error and return global value since no per-problem value is available
            long globalMaxOutput = contest.getContestInformation().getMaxOutputSizeInBytes();
            if (log != null) {
                log.log(Log.WARNING, "Problem is null, cannot determine output size limit; returning global max output value " + globalMaxOutput);
            } else {
                System.err.println("WARNING: Executable.getMaxFileSize(): log is null; cannot log message "
                        + "'Problem is null, cannot determine output size limit; returning global max output value " + globalMaxOutput + "'");
            }
            return globalMaxOutput;
        }
    }

    /**
     * Replace all instances of beforeString with afterString.
     * 
     * If before string is not found, then returns original string.
     * 
     * @param origString
     *            string to be modified
     * @param beforeString
     *            string to search for
     * @param afterString
     *            string to replace beforeString
     * @return original string with all beforeString instances replaced with afterString
     */
    public String replaceString(String origString, String beforeString, String afterString) {

        if (origString == null || afterString == null) {
            return origString;
        }

        int startIdx = origString.lastIndexOf(beforeString);

        if (startIdx == -1) {
            return origString;
        }

        StringBuffer buf = new StringBuffer(origString);

        while (startIdx != -1) {
            buf.replace(startIdx, startIdx + beforeString.length(), afterString);
            startIdx = origString.lastIndexOf(beforeString, startIdx - 1);
        }

        return buf.toString();
    }
    
    /**
     * Replace beforeString with int.
     * 
     * For details see {@link #replaceString(String, String, String)}
     * 
     * @param origString
     *            string to be modified
     * @param beforeString
     *            string to search for
     * @param afterInt
     *            integer to replace beforeString
     * @return string after replacement.
     */
    public String replaceString(String origString, String beforeString, int afterInt) {
        String afterString = new Integer(afterInt).toString();
        return replaceString(origString, beforeString, afterString);
    }

    public String substituteAllStrings(Run inRun, String origString) {
        return(substituteAllStrings(inRun, origString, 1));
    }
    
    /**
     * return string with all field variables filled with values.
     * 
     * Each variable will be filled in with values.
     * 
     * <pre>
     *             valid fields are:
     *              {:mainfile} - submitted file (hello.java)
     *              {:basename} - mainfile without extension (hello)
     *              {:validator} - validator program name
     *              {:language}
     *              {:problem}
     *              {:teamid}
     *              {:siteid}
     *              {:infile}
     *              {:outfile}
     *              {:ansfile}
     *              {:pc2home}
     *              {:ensuresuffix=...} - add supplied suffix if not present already
     * </pre>
     * 
     * @param dataSetNumber
     *            which set of judge data to use (1 in the case of only 1 file)
     * @param inRun
     *            submitted by team
     * @param origString
     *            - original string to be substituted.
     * @return string with values
     */
    public String substituteAllStrings(Run inRun, String origString, int dataSetNumber) {
        String newString = "";
        String nullArgument = "-"; /* this needs to change */

        try {
            if (inRun == null) {
                throw new IllegalArgumentException("Run is null");
            }

            if (runFiles.getMainFile() == null) {
                log.config("substituteAllStrings() main file is null (no contents)");
                return origString;
            }
            newString = replaceString(origString, "{:mainfile}", runFiles.getMainFile().getName());
            newString = replaceString(newString, "{files}", ExecuteUtilities.getAllSubmittedFilenames(runFiles));
            newString = replaceString(newString, "{:basename}", removeExtension(runFiles.getMainFile().getName()));
            newString = replaceString(newString, "{:package}", packageName);

            String validatorCommand = null;

            if (problem.getOutputValidatorProgramName() != null) {
                validatorCommand = problem.getOutputValidatorProgramName();
            }

            if (problemDataFiles != null) {
                SerializedFile validatorFile = problemDataFiles.getOutputValidatorFile();
                if (validatorFile != null) {
                    validatorCommand = validatorFile.getName(); // validator
                }
            }

            if (validatorCommand != null) {
                newString = replaceString(newString, "{:validator}", validatorCommand);
            }

            // SOMEDAY LanguageId and ProblemId are now a long string not an int,
            // what should we do?

            if (inRun.getLanguageId() != null) {
                Language[] langs = contest.getLanguages();
                int index = 0;
                String displayName = "";
                for (int i = 0; i < langs.length; i++) {
                    if (langs[i] != null && langs[i].getElementId().equals(inRun.getLanguageId())) {
                        displayName = langs[i].getDisplayName().toLowerCase().replaceAll(" ", "_");
                        index = i + 1;
                        break;
                    }
                }
                if (index > 0) {
                    newString = replaceString(newString, "{:language}", index);
                    newString = replaceString(newString, "{:languageletter}", Utilities.convertNumber(index));
                    newString = replaceString(newString, "{:languagename}", displayName);
                }
            }
            if (inRun.getProblemId() != null) {
                Problem[] problems = contest.getProblems();
                int index = 0;
                for (int i = 0; i < problems.length; i++) {
                    if (problems[i] != null && problems[i].getElementId().equals(inRun.getProblemId())) {
                        index = i + 1;
                        break;
                    }
                }
                if (index > 0) {
                    newString = replaceString(newString, "{:problem}", index);
                    newString = replaceString(newString, "{:problemletter}", Utilities.convertNumber(index));
                    if(problem != null) {
                        newString = replaceString(newString, "{:problemshort}", problem.getShortName());
                    }
                }
            }
            if (inRun.getSubmitter() != null) {
                newString = replaceString(newString, "{:teamid}", inRun.getSubmitter().getClientNumber());
                newString = replaceString(newString, "{:siteid}", inRun.getSubmitter().getSiteNumber());
            }

            if (problem != null) {
                if (problem.getDataFileName() != null && !problem.getDataFileName().equals("")) {
                    newString = replaceString(newString, "{:infile}", problem.getDataFileName());
                } else {
                    newString = replaceString(newString, "{:infile}", nullArgument);
                }
                if (problem.getAnswerFileName() != null && !problem.getAnswerFileName().equals("")) {
                    newString = replaceString(newString, "{:ansfile}", problem.getAnswerFileName());
                } else {
                    newString = replaceString(newString, "{:ansfile}", nullArgument);
                }
                
                String fileName = problem.getDataFileName(dataSetNumber);
                if (fileName != null && !fileName.equals("")) {
                    newString = replaceString(newString, "{:infilename}", fileName);
                } else {
                    newString = replaceString(newString, "{:infilename}", nullArgument);
                }
                fileName = problem.getAnswerFileName(dataSetNumber);
                if (fileName != null && !fileName.equals("")) {
                    newString = replaceString(newString, "{:ansfilename}", fileName);
                } else {
                    newString = replaceString(newString, "{:ansfilename}", nullArgument);
                }
                newString = replaceString(newString, "{:timelimit}", Long.toString(problem.getTimeOutInSeconds()));
            } else {
                log.config("substituteAllStrings() problem is undefined (null)");
            }

            if (executionData != null) {
                if (executionData.getExecuteProgramOutput() != null) {
                    if (executionData.getExecuteProgramOutput().getName() != null) {
                        newString = replaceString(newString, "{:outfile}", executionData.getExecuteProgramOutput().getName());
                    } else {
                        newString = replaceString(newString, "{:outfile}", nullArgument);
                    }
                }
                newString = replaceString(newString, "{:exitvalue}", Integer.toString(executionData.getExecuteExitValue()));
                newString = replaceString(newString, "{:executetime}", Long.toString(executionData.getExecuteTimeMS()));
            }
            String pc2home = new VersionInfo().locateHome();
            if (pc2home != null && pc2home.length() > 0) {
                newString = replaceString(newString, "{:pc2home}", pc2home);
            }
            // Check for conditional suffix (that is, the previous chars match), if not, add them
            newString = ExecuteUtilities.replaceStringConditional(newString, Constants.CMDSUB_COND_SUFFIX);
            
        } catch (Exception e) {
            log.log(Log.CONFIG, "Exception substituting strings ", e);
            // carrying on not required to save exception
        }

        return newString;
    }

    /**
     * Return string minus last extension. <br>
     * Finds last . (period) in input string, strips that period and all other characters after that last period. If no period is found in string, will return a copy of the original string. <br>
     * Unlike the Unix basename program, no extension is supplied.
     * 
     * @param original
     *            the input string
     * @return a string with all text after last . removed
     */
    public String removeExtension(String original) {
        String outString = new String(original);

        // Strip off all text after and including final dot

        int dotIndex = outString.lastIndexOf('.', outString.length() - 1);
        if (dotIndex != -1) {
            outString = outString.substring(0, dotIndex);
        }

        return outString;

    }

    /**
     * return directory name for input file.
     * 
     * @param file
     *            input file.
     * @return directory name.
     */
    protected String getDirName(SerializedFile file) {
        String absPath = file.getAbsolutePath();
        return absPath.substring(0, absPath.length() - 1 - file.getName().length());
    }

    /**
     * Run a program.
     */

    /**
     * This method accepts a String containing a command and exec's a new process running that command.
     * 
     * 
     * @param cmdline the command (program) to be executed as a new process
     * @param msg a String to be displayed on the specified ExecuteTimer GUI (if the ExecuteTimer is not null)
     * @param autoStopExecution a flag indicating whether the ExecuteTimer should stop (kill) the process when the timer expires
     * @return the newly-started process.
     */
    public Process runProgram(String cmdline, String msg, boolean autoStopExecution, ExecuteTimer myExecuteTimer) {
        
        log.info("entering runProgram() to execute command '" + cmdline + "'");
        
        Process newProcess = null;
        errorString = "";

        executeDirectoryName = getExecuteDirectoryName();

        try {
            File runDir = new File(executeDirectoryName);
            if (runDir.isDirectory()) {

                String[] env = null;

                if (myExecuteTimer != null) {
                    log.info("Notifying ExecuteTimer " + myExecuteTimer.toString() + " to set doAutoStop " + autoStopExecution);
                    myExecuteTimer.setDoAutoStop(autoStopExecution);
                    myExecuteTimer.setTitle(msg);
                }

                startTimeNanos = System.nanoTime();
                
                log.info("Invoking Runtime.exec() to execute command '" + cmdline + "'");
                newProcess = Runtime.getRuntime().exec(cmdline, env, runDir);
                
                log.info("Created new process with id " + getProcessID(newProcess));
                

                // if(isJudge && executionTimer != null) {
                if (myExecuteTimer != null) {
                    log.info("Setting new process " + getProcessID(newProcess) + " in ExecuteTimer " + myExecuteTimer.toString());
                    myExecuteTimer.setProc(newProcess);
                    log.info("Starting ExecuteTimer");
                    myExecuteTimer.startTimer();
                }
                
            } else {
                errorString = "Execute Directory does not exist";
                log.config("Execute Directory does not exist");
            }
        } catch (IOException e) {
            errorString = e.getMessage();
            log.config("Note: exec failed in runProgram() : " + errorString);
            executionData.setExecutionException(e);
            return null;
        } catch (Exception e) {
            errorString = e.getMessage();
            log.log(Log.CONFIG, "Note: exec failed in runProgram() : " + errorString, e);
            executionData.setExecutionException(e);
            return null;
        }

        return newProcess;
    }

    /**
     * This method receives a {@link Process} object and returns the id of that Process.
     * 
     * TODO: currently the implementation of this method simply returns the toString() of the received
     * Process object.  A future upgrade should use the Java 9 method Process.getProcessID() to obtain
     * the actual platform-specific id of the Process.
     *  
     * @param theProcess the Process object who's ID is to be returned
     * @return a String containing the id of the specified Process
     */
    private String getProcessID(Process theProcess) {
        if (theProcess == null) {
            return "null";
        } else {

            // TODO: return the actual process id instead of the toString()
            return theProcess.toString();
        }
    }

    /**
     * Set executable (+x) bit for input filename.
     * <P>
     * This will only set the x bit on a Unix OS. It checks for /bin/chmod, if that file does not exist, no change will be made.
     */
    private void setExecuteBit(String filename) {
        log.config("setExecuteBit for " + filename);
        try {

            File chmodFile = new File("/bin/chmod");
            if (chmodFile.exists()) {

                /**
                 * We are likely under Unix, since files may be unpacked without the execute bit set, we force that bit to be set to we can execute the file under Unix.
                 */

                String cmdline = "/bin/chmod +x " + filename;
                log.config("executing chmod: '" + cmdline + "'");

                process = Runtime.getRuntime().exec(cmdline);
                process.waitFor();
            }
        } catch (Exception ex) {
            log.log(Log.CONFIG, "Exception in setExecuteBit()  ", ex);
        }
    }

    public String getValidationResults() {
        return executionData.getValidationResults();
    }

    /**
     * @return Returns the executionData.
     */
    public ExecutionData getExecutionData() {
        return executionData;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    /**
     * Execute directory name for this client instance.
     * 
     * The name is individual for each client.
     * 
     * @see #getExecuteDirectoryNameSuffix()
     * 
     * @return the name of the execute directory for this client.
     */
    public String getExecuteDirectoryName() {
        return "executesite" + contest.getClientId().getSiteNumber() + contest.getClientId().getName() + getExecuteDirectoryNameSuffix();
    }

    /**
     * Prepends directory name onto filename.
     * 
     * @see #getExecuteDirectoryName()
     * @param filename
     *            filename without a directory name.
     * @return filename prepended with execute directory name
     */
    public String prefixExecuteDirname(String filename) {
        return getExecuteDirectoryName() + File.separator + filename;
    }

    public boolean isOverwriteJudgesDataFiles() {
        return overwriteJudgesDataFiles;
    }

    /**
     * Set whether to overwrite judge's data files, or leave them.
     * 
     * @param overwriteDataFiles
     *            if true, rewrite data files, if false do nothing.
     */
    public void setOverwriteJudgesDataFiles(boolean overwriteDataFiles) {
        this.overwriteJudgesDataFiles = overwriteDataFiles;
    }

    public boolean isTestRunOnly() {
        return testRunOnly || executorId.getClientType() == ClientType.Type.TEAM;
    }

    /**
     * Is the pc2 module executing a team?
     * 
     * If the module is a team, then execute will not extract judge's data files nor will execute run a validation.
     * 
     */

    public void setTestRunOnly(boolean testRunOnly) {
        this.testRunOnly = testRunOnly || executorId.getClientType() == ClientType.Type.TEAM;
    }

    public boolean isValidationSuccess() {
        return executionData.isValidationSuccess();
    }

    /**
     * 
     * @return true if show message to users
     */
    public boolean isShowMessageToUser() {
        return showMessageToUser;
    }

    /**
     * Show gui message to user when errors occur?
     * 
     * @param showMessageToUser
     */
    public void setShowMessageToUser(boolean showMessageToUser) {
        this.showMessageToUser = showMessageToUser;
    }

    public String getExecuteDirectoryNameSuffix() {
        return executeDirectoryNameSuffix;
    }

    public void setExecuteDirectoryName(String executeDirectoryName) {
        this.executeDirectoryName = executeDirectoryName;
    }

    /**
     * Prepend basedirectoryname in front of executedirectory name.
     * 
     * @param baseDirectoryName
     */
    public void setExecuteBaseDirectoryName(String baseDirectoryName) throws Exception {
        insureDir(baseDirectoryName);
        if (!isDirectory(baseDirectoryName)) {
            throw new IOException("Could not create directory " + baseDirectoryName);
        }
        String dirname = baseDirectoryName + File.separator + executeDirectoryName;
        insureDir(baseDirectoryName);
        if (!isDirectory(dirname)) {
            throw new IOException("Could not create directory " + dirname);
        }
        this.executeDirectoryName = dirname;
    }

    private boolean isDirectory(String dirname) {
        return new File(dirname).isDirectory();
    }

    /**
     * Set the suffix which is to be added to the execute directory name.
     * 
     * This method must be called before calling {@link #getExecuteDirectoryName()} or {@link #getExecuteDirectoryName(String)}.
     * 
     * @see #getExecuteDirectoryName()
     * 
     * @param executeDirectoryNameSuffix
     *            the suffix to be added to the name of the execute directory
     */
    public void setExecuteDirectoryNameSuffix(String executeDirectoryNameSuffix) {
        this.executeDirectoryNameSuffix = executeDirectoryNameSuffix;
    }

    public void setUsingGUI(boolean usingGUI) {
        this.usingGUI = usingGUI;
    }

    public boolean isUsingGUI() {
        return usingGUI;
    }

    @Override
    public IFileViewer execute(IInternalContest inContest, IInternalController inController, Run aRun, RunFiles aRunFiles, boolean clearDirFirst) {

        this.contest = inContest;
        this.controller = inController;
        this.runFiles = aRunFiles;
        this.run = aRun;
        language = inContest.getLanguage(aRun.getLanguageId());
        problem = inContest.getProblem(aRun.getProblemId());

        initialize();

        return execute(clearDirFirst);

    }

    @Override
    public String getPluginTitle() {
        return "Executeable";
    }

    @Override
    public void dispose() {

        executionData = null;
        executionTimer = null;
        fileViewer = null;
    }

    public ContestInformation getContestInformation() {
        return contest.getContestInformation();
    }

    /**
     * Get filenames for each team's output for each test case.
     * 
     * @return the list of team output file names.
     */
    public List<String> getTeamsOutputFilenames() {
        return teamsOutputFilenames;
    }

    /**
     * Get filenames for each validator output for each test case.
     * 
     * @return the list of validator output file names.
     */
    public List<String> getValidatorOutputFilenames() {
        return validatorOutputFilenames;
    }

    /**
     * Get filenames for each validator stderr output for each test case.
     * 
     * @return the list of validator stderr output file names.
     */
    public List<String> getValidatorErrFilenames() {
        return validatorStderrFilesnames;
    }
    
    /**
     * Returns the limit, in seconds, for the amount of time allowed for compilation of a submission.
     * 
     * TODO: currently the returned limit is the value defined by the constant DEFAULT_COMPILATION_TIME_LIMIT_SECS; 
     * this should eventually be replaced by obtaining the problem-specified compilation time limit from a system 
     * property settable either via the Admin GUI or from a problem.yaml file.  See bug 1669.
     * 
     * @return an integer giving the compilation time limit, in seconds
     */
    private int getCompilationTimeLimit() {
        
        return DEFAULT_COMPILATION_TIME_LIMIT_SECS ;
    }
    
    
    /**
     * Returns the limit, in seconds, for the amount of time allowed for validation of the output of a single run 
     * (test case) of a submission.
     * 
     * TODO: currently the returned limit is the value defined by the constant DEFAULT_VALIDATION_TIME_LIMIT_SECS; 
     * this should eventually be replaced by obtaining the problem-specified compilation time limit from a system 
     * property settable either via the Admin GUI or from a problem.yaml file. See bug 1669.
     * 
     * @return an integer giving the per-run (per-test-case) validation time limit, in seconds
     */
    private int getValidationTimeLimit() {
        
        return DEFAULT_VALIDATION_TIME_LIMIT_SECS ;
    }
}
