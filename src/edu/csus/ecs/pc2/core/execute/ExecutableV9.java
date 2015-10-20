package edu.csus.ecs.pc2.core.execute;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import javax.swing.JFileChooser;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Plugin;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.ui.IFileViewer;
import edu.csus.ecs.pc2.ui.MultipleFileViewer;
import edu.csus.ecs.pc2.ui.NullViewer;

/**
 * Compile, execute and validate a run.
 * 
 * Before execute, one can use {@link #setLanguage(Language)}, {@link #setProblem(Problem)} to use a different language or problem. <br>
 * To not overwrite the judge's data files, use {@link #setOverwriteJudgesDataFiles(boolean)} to false.
 * 
 * @see #execute()
 * @version $Id: Executable.java 2875 2014-11-19 03:01:10Z boudreat $
 * @author pc2@ecs.csus.edu
 */

// SOMEDAY this class contains a number of Utility methods like: baseName, replaceString... etc
// should these routines be placed in a static way in a static class ?
// SOMEDAY design decision how to handle MultipleFileViewer, display here, on TeamClient??

// $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/core/execute/Executable.java $
public class ExecutableV9 extends Plugin implements IExecutable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final String NL = System.getProperty("line.separator");

    private ExecuteUtilities executeUtilities = null;

    private Run run = null;

    private Language language = null;

    private Problem problem = null;

    private ProblemDataFiles problemDataFiles = null;

    private ClientId executorId = null;

    /**
     * Directory where main file is found
     */
    private String mainFileDirectory;

    private ExecutionData executionData;

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
     * Execution stdout filename.
     */
    public static final String EXECUTE_STDOUT_FILENAME = "estdout.pc2";

    /**
     * Execution stderr filename.
     */
    public static final String EXECUTE_STDERR_FILENAME = "estderr.pc2";

    /**
     * Execution stdout filename.
     */
    public static final String VALIDATOR_STDOUT_FILENAME = "vstdout.pc2";

    /**
     * Execution stderr filename.
     */
    public static final String VALIDATOR_STDERR_FILENAME = "vstderr.pc2";

    /**
     * Files submitted with the Run.
     */
    private RunFiles runFiles;

    private String runProgramErrorString;

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

    public ExecutableV9(IInternalContest inContest, IInternalController inController, Run run, RunFiles runFiles) {
        super();
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

        mainFileDirectory = getDirName(runFiles.getMainFile());
        executeDirectoryName = getExecuteDirectoryName();

        log = controller.getLog();

        executeUtilities = new ExecuteUtilities(contest, controller, run, runFiles, problem, language);

        if (executorId.getClientType() != ClientType.Type.TEAM) {
            this.problemDataFiles = contest.getProblemDataFile(problem);
            executeUtilities.setProblemDataFiles(problemDataFiles);
        }
    }

    /**
     * Remove all files from specified directory, including subdirectories.
     * 
     * @param dirName
     *            directory to be cleared.
     * @return true if directory was cleared.
     */
    protected boolean clearDirectory(String dirName) {
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

    /**
     * Compile, execute and validate run - using Judge's data file(s).
     * 
     * @see #execute(boolean)
     */
    public IFileViewer execute() {
        return execute(true);
    }

    /**
     * Compile, execute and validate run.
     * 
     * execute does the following:
     * <ol>
     * <li>Creates and clears execute directory (if clearDirFirst == true)
     * <li>Extracts source file(s)
     * <li>Compiles source
     * <li>If executable created, will executes program
     * <li>If not a team module, and successful execution, run validator.
     * </ol>
     * 
     * <br>
     * Will only run the validation on a run if not a {@link edu.csus.ecs.pc2.core.model.ClientType.Type#TEAM} client.
     * 
     * @param clearDirFirst
     *            - clear the directory before unpacking and executing
     * @return FileViewer with 1 or more tabs
     */
    public IFileViewer execute(boolean clearDirFirst) {
        if (usingGUI) {
            fileViewer = new MultipleFileViewer(log);
        } else {
            fileViewer = new NullViewer();
        }

        try {
            executionData = new ExecutionData();
            executeUtilities.setExecutionData(executionData);

            executeDirectoryName = getExecuteDirectoryName();

            boolean dirThere = insureDir(executeDirectoryName);

            if (!dirThere) {
                log.config("Directory could not be created: " + executeDirectoryName);
                showDialogToUser("Unable to create directory " + executeDirectoryName);
                setException(executionData, "Unable to create directory " + executeDirectoryName);
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
                    setException(executionData, "Unable to remove all files from directory " + executeDirectoryName);
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
                     * compileProgram returns false if 
                     * 1) runProgram failed (runProgramErrorString set) 
                     * 2) compiler fails to create expecte output file (runProgramErrorString empty) 
                     * If there is compiler stderr or stdout
                     * we should not add the textPane saying there was an error.
                     */
                    if (!executionData.isCompileSuccess()) {

                        String errorMessage = "Unable to find/execute compiler using command: " + language.getCompileCommandLine();
                        if (executionData.getExecutionException() != null) {
                            errorMessage += NL + executionData.getExecutionException().getMessage();
                        }

                        showDialogToUser(errorMessage);
                        fileViewer.addTextPane("Error executing compiler", errorMessage);

                    } else if (executionData.getCompileStderr() == null && executionData.getCompileStdout() == null) {

                        int errnoIndex = runProgramErrorString.indexOf('=') + 1;
                        String errorMessage;
                        if (runProgramErrorString.substring(errnoIndex).equals("2")) {
                            errorMessage = "Compiler not found, contact staff.";

                        } else {
                            errorMessage = "Problem executing compiler, contact staff.";
                        }
                        showDialogToUser(errorMessage);
                        setException(executionData, errorMessage);
                        fileViewer.addTextPane("Error during compile", errorMessage);
                    } // else they will get a tab hopefully showing something wrong
                }
            } else if (compileProgram()) {
                SerializedFile[] dataFiles = null;
                if (problemDataFiles != null) {
                    dataFiles = problemDataFiles.getJudgesDataFiles();
                } // else problem has no data files
                int dataSetNumber = 0;

                if (dataFiles == null || dataFiles.length <= 1) {
                    // Only a single (at most) data set,
                    if (executeProgram(dataSetNumber) && isValidated()) {
                        validateProgram(dataSetNumber);
                    }
                } else {
                    // getting here when not in validator mode results in a blank execute results window
                    boolean passed = true;

                    // TODO CCS SOMEDAY make this work properly - aka not depend on mtsv
                    // while (passed && dataSetNumber < dataFiles.length) {
                    if (executeProgram(dataSetNumber) && isValidated()) {
                        passed = validateProgram(dataSetNumber);
                    } else {
                        passed = false; // didn't execute.
                    }

                    log.info("Run " + run.getNumber() + " test case passes = " + passed);

                    dataSetNumber++;
                    // }
                }
            } else {
                /**
                 * compileProgram returns false if 1) runProgram failed (runProgramErrorString set) 
                 * 2) compiler fails to create expected output file (runProgramErrorString empty) 
                 * If there is compiler stderr or stdout we
                 * should not add the textPane saying there was an error.
                 */
                if (!executionData.isCompileSuccess()) {

                    String errorMessage = "Unable to find/execute compiler using command: " + language.getCompileCommandLine();
                    if (executionData.getExecutionException() != null) {
                        errorMessage += NL + executionData.getExecutionException().getMessage();
                    }

                    showDialogToUser(errorMessage);
                    fileViewer.addTextPane("Error executing compiler", errorMessage);

                } else if (executionData.getCompileStderr() == null && executionData.getCompileStdout() == null) {
                    int errnoIndex = runProgramErrorString.indexOf('=') + 1;
                    String errorMessage;
                    if (runProgramErrorString.substring(errnoIndex).equals("2")) {
                        errorMessage = "Compiler not found, contact staff.";

                    } else {
                        errorMessage = "Problem executing compiler, contact staff.";
                    }
                    showDialogToUser(errorMessage);
                    setException(executionData, errorMessage);
                    fileViewer.addTextPane("Error during compile", errorMessage);
                } // else they will get a tab hopefully showing something wrong
            }

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

            if (!programGeneratedOutput) {
                String message = "PC2: execution of program did not generate any output";

                if (!executionData.isCompileSuccess()) {
                    String errorMessage = "Unable to find/execute compiler using command: " + language.getCompileCommandLine();
                    if (executionData.getExecutionException() != null) {
                        errorMessage += NL + executionData.getExecutionException().getMessage();
                    }
                    message += NL + errorMessage;
                }

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
            executionData.setExecutionException(e);
            log.log(Log.INFO, "Exception during execute() ", e);
            fileViewer.addTextPane("Error during execute", "Exception during execute, check log " + e.getMessage());
        }

        return fileViewer;
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
     * @throws IOException
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
            Utilities.createFile(file, filename);
            return true;
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
            setException(executionData, "Executable.execute(RunData): Directory " + dir.getName() + " could not be created.");
        }

        return dir.isDirectory();
    }

    protected boolean validateProgram(int dataSetNumber) {

        // SOMEDAY Handle the error messages better, log and put them before the user to
        // help with debugging

        executionData.setValidationReturnCode(-1);
        executionData.setValidationSuccess(false);

        if (isJudge()) {
            controller.sendValidatingMessage(run);
        }

        if (problemDataFiles.getValidatorFile() != null) {
            // Create Validation Program

            String validatorFileName = problemDataFiles.getValidatorFile().getName();
            String validatorUnpackName = prefixExecuteDirname(validatorFileName);
            if (!createFile(problemDataFiles.getValidatorFile(), validatorUnpackName)) {
                log.info("Unable to create validator program " + validatorUnpackName);
                setException(executionData, "Unable to create validator program " + validatorUnpackName);

                throw new SecurityException("Unable to create validator, check logs");
            }

            if (!validatorFileName.endsWith(".jar")) {
                /**
                 * Unix validator programs must set the execute bit to be able to execute the program.
                 */

                setExecuteBit(prefixExecuteDirname(validatorFileName));
            }
        }

        if (overwriteJudgesDataFiles) {

            if (!problem.isUsingExternalDataFiles()) {
                /**
                 * If not external files, must unpack files.
                 */
                // TODO remove these old lines
                // createFile(problemDataFiles.getJudgesDataFiles()[dataSetNumber], prefixExecuteDirname(problem.getDataFileName()));
                // createFile(problemDataFiles.getJudgesAnswerFiles()[dataSetNumber], prefixExecuteDirname(problem.getAnswerFileName()));

                // Create the correct output file, aka answer file
                createFile(problemDataFiles.getJudgesDataFiles(), dataSetNumber, prefixExecuteDirname(problem.getDataFileName()));

                // Create the correct output file, aka answer file
                createFile(problemDataFiles.getJudgesAnswerFiles(), dataSetNumber, prefixExecuteDirname(problem.getAnswerFileName()));

            } // else no need to create external data files.

        }

        // teams output file
        SerializedFile userOutputFile = executionData.getExecuteProgramOutput();

        createFile(userOutputFile, prefixExecuteDirname(userOutputFile.getName()));

        // Answer/results file name

        String resultsFileName = executeUtilities.getResultsFileName();

        /*
         * <validator> <input_filename> <output_filename> <answer_filename> <results_file> -pc2|-appes [other files]
         */

        /**
         * Standard command line pattern
         * 
         * String commandPattern = "{:validator} {:infile} {:outfile} {:ansfile} {:resfile} ";
         */

        String commandPattern = problem.getValidatorCommandLine();
        boolean pc2JarUseDirectory = false;

        if (problem.isUsingPC2Validator()) {

            /**
             * The internal command is set to: <validator> <input_filename> <output_filename> <answer_filename> <results_file> -pc2|-appes [other files] Where validator is
             * Problem.INTERNAL_VALIDATOR_NAME aka "pc2.jar edu.csus.ecs.pc2.validator.Validator"
             * 
             * So we need to prefix the command with java -jar <path to jar>
             */

            String pathToPC2Jar = ExecuteUtilities.findPC2JarPath();
            if (!(new File(pathToPC2Jar+"pc2.jar")).exists()) {
                pc2JarUseDirectory = true;
            }
            commandPattern = "java -cp " + pathToPC2Jar + problem.getValidatorCommandLine();
        }

        log.log(Log.DEBUG, "before substitution: " + commandPattern);

        String cmdLine = executeUtilities.substituteAllStrings(commandPattern);

        if (File.separator.equals("\\")) {
            if (problem.isUsingPC2Validator()) {
                cmdLine = cmdLine.replaceFirst("-cp ", "-cp \"");
                cmdLine = cmdLine.replaceFirst("jar ", "jar\" ");
                log.log(Log.DEBUG, "after replaceFirst: " + cmdLine);
            }
        }
        if (pc2JarUseDirectory) {
            // this is a directory, remove "pc2.jar" from string
            cmdLine = ExecuteUtilities.replaceString(cmdLine, "pc2.jar", "");
        }

        log.log(Log.DEBUG, "after  substitution: " + cmdLine);

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
            setException(executionData, "Exception in validatorCall " + e.getMessage());

            log.log(Log.INFO, "Exception in validatorCall ", e);
            throw new SecurityException(e);
        }

        try {

            BufferedOutputStream stdoutlog = new BufferedOutputStream(new FileOutputStream(prefixExecuteDirname(VALIDATOR_STDOUT_FILENAME), false));
            BufferedOutputStream stderrlog = new BufferedOutputStream(new FileOutputStream(prefixExecuteDirname(VALIDATOR_STDERR_FILENAME), false));

            String msg = "Working...";
            if (problem.isShowValidationToJudges()) {
                msg = "Validating...";
            }

            long startSecs = System.currentTimeMillis();
            Process process = runProgram(cmdLine, msg, false);

            if (process == null) {
                executionTimer.stopTimer();
                stderrlog.close();
                stdoutlog.close();
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

            // // waiting for the process to finish execution...
            // executionData.setValidationReturnCode(process.waitFor());

            stdoutCollector.join();
            stderrCollector.join();

            // if(isJudge && executionTimer != null) {
            if (executionTimer != null) {
                executionTimer.stopTimer();
            } else {
                // SOMEDAY LOG - why are we logging this ?
                log.config("validatorCall() executionTimer == null");
            }

            if (process != null) {
                process.destroy();
            }

            stdoutlog.close();
            stderrlog.close();

            executionData.setvalidateTimeMS(System.currentTimeMillis() - startSecs);
            executionData.setValidationStdout(new SerializedFile(prefixExecuteDirname(VALIDATOR_STDOUT_FILENAME)));
            executionData.setValidationStderr(new SerializedFile(prefixExecuteDirname(VALIDATOR_STDERR_FILENAME)));

        } catch (Exception ex) {
            if (executionTimer != null) {
                executionTimer.stopTimer();
            }
            log.log(Log.CONFIG, "Exception in validator ", ex);
        }

        boolean fileThere = new File(prefixExecuteDirname(resultsFileName)).exists();

        try {
            if (fileThere) {

                storeValidatorResults(resultsFileName, log);

            } else {
                // SOMEDAY LOG
                log.config("validationCall - Did not produce output results file " + resultsFileName);
                // JOptionPane.showMessageDialog(null, "Did not produce output results file " + resultsFileName + " contact staff");
            }
        } catch (Exception ex) {
            log.log(Log.INFO, "Exception in validation  ", ex);
            throw new SecurityException(ex);
        } finally {

            if (executionData.isRunTimeLimitExceeded()) {
                executionData.setValidationResults("No - Time Limit Exceeded");
                executionData.setValidationSuccess(true);
            }
        }

        return executionData.isValidationSuccess();
    }

    /**
     * Set results of validation into executionData.
     * 
     * @param resultsFileName
     * @param logger
     */
    protected void storeValidatorResults(String resultsFileName, Log logger) {

        IResultsParser parser = new XMLResultsParser();
        parser.setLog(log);
        boolean done = parser.parseValidatorResultsFile(prefixExecuteDirname(resultsFileName));
        Hashtable<String, String> results = parser.getResults();

        if (done && results != null && results.containsKey("outcome")) {
            // non-IJRM does not require security, but if it is IJRM it better have security.
            if (!problem.isInternationalJudgementReadMethod() || (results.containsKey("security") && resultsFileName.equals(results.get("security")))) {
                // Found the string
                executionData.setValidationResults(results.get("outcome"));
                executionData.setValidationSuccess(true);
            } else {
                // SOMEDAY LOG info
                setException(executionData, "validationCall - results file did not contain security");

                logger.config("validationCall - results file did not contain security");
                logger.config(resultsFileName + " != " + results.get("security"));
            }
        } else {
            if (!done) {
                // SOMEDAY LOG
                // SOMEDAY show user message
                setException(executionData, "Error parsing/reading results file, check log");

                logger.config("Error parsing/reading results file, check log");
            } else if (results != null && (!results.containsKey("outcome"))) {
                // SOMEDAY LOG
                // SOMEDAY show user message
                setException(executionData, "Error parsing/reading results file, check log");
                logger.config("Error could not find 'outcome' in results file, check log");
            } else {
                // SOMEDAY LOG
                // SOMEDAY show user message
                logger.config("Error parsing results file, check log");
            }
        }

    }

    /**
     * Sets the exception for this execute().
     * 
     * @param inExecutionData
     * @param string
     */
    private void setException(ExecutionData inExecutionData, String string) {
        log.log(Log.WARNING, string);
        inExecutionData.setExecutionException(new Exception(string));
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
        String outFileName = null;
        JFileChooser chooser = new JFileChooser(mainFileDirectory);
        try {
            chooser.setDialogTitle("Open Test Input File");
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                mainFileDirectory = chooser.getCurrentDirectory().getAbsolutePath();
                outFileName = chooser.getSelectedFile().getCanonicalFile().toString();
            }
        } catch (Exception e) {
            // SOMEDAY log this exception
            log.log(Log.CONFIG, "Error getting selected file, try again.", e);
        }
        chooser = null;

        return outFileName;
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
     * execute the submission
     * 
     * @return true if program successfully executes.
     */
    /**
     * Execute the submission.
     * 
     * @param dataSetNumber
     *            a zero based data set number
     * @param writeJudgesDataFiles
     * @return true if execution worked.
     */
    protected boolean executeProgram(int dataSetNumber) {
        boolean passed = false;
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

            executionTimer = new ExecuteTimer(log, problem.getTimeOutInSeconds(), executorId, isUsingGUI());
            executionTimer.startTimer();

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

                if (problem.isReadInputDataFromSTDIN()) {
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
                        }

                    }
                    // Else, leave whatever data file is present.
                } else {

                    /*
                     * External data files (not inside of pc2). On local disk.
                     */

                    File dataFile = problem.locateJudgesDataFile(testSetNumber);
                    if (dataFile == null) {
                        String name = problem.getDataFileName(testSetNumber);
                        log.log(Log.DEBUG, "For problem " + problem + " test number " + testSetNumber + " expecting file " + name + " in dir " + problem.getCCSfileDirectory());
                        throw new SecurityException("Unable to find/extract data file " + name + " for data set " + dataSetNumber + " check log");
                    }
                    inputDataFileName = dataFile.getCanonicalPath();
                }
            }

            // SOMEDAY execute the language.getProgramExecuteCommandLine();

            BufferedOutputStream stdoutlog = new BufferedOutputStream(new FileOutputStream(prefixExecuteDirname(EXECUTE_STDOUT_FILENAME), false));
            BufferedOutputStream stderrlog = new BufferedOutputStream(new FileOutputStream(prefixExecuteDirname(EXECUTE_STDERR_FILENAME), false));

            String cmdline = language.getProgramExecuteCommandLine();
            log.log(Log.DEBUG, "before substitution: " + cmdline);

            cmdline = executeUtilities.substituteAllStrings(cmdline);
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
                // TODO is this a bug in that the rest of the command line is thrown away?
                cmdline = f.getCanonicalPath();
            }

            boolean autoStop = false;
            if (!isTestRunOnly()) {
                // This autostops all executions except Test Run (team)
                autoStop = true;
            }

            long startSecs = System.currentTimeMillis();
            Process process = runProgram(cmdline, "Executing...", autoStop);
            if (process == null) {
                executionTimer.stopTimer();
                stderrlog.close();
                stdoutlog.close();
                executionData.setExecuteSucess(false);
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

            if (isValidDataFile(problem) && problem.isReadInputDataFromSTDIN()) {
                BufferedOutputStream out = new BufferedOutputStream(process.getOutputStream());
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(inputDataFileName));
                byte[] buf = new byte[32768];
                int c;
                try {
                    while ((c = in.read(buf)) != -1) {
                        out.write(buf, 0, c);
                    }
                } catch (java.io.IOException e) {
                    log.info("Caught a " + e.getMessage() + " do not be alarmed.");
                }

                in.close();
                out.close();
            }

            stdoutCollector.join();
            stderrCollector.join();

            if (executionTimer != null) {
                executionTimer.stopTimer();
                executionData.setRunTimeLimitExceeded(executionTimer.isRunTimeLimitExceeded());
            }

            if (process != null) {
                int returnValue = process.waitFor();
                executionData.setExecuteExitValue(returnValue);
                process.destroy();
            }

            stdoutlog.close();
            stderrlog.close();

            executionData.setExecuteSucess(true);
            executionData.setExecuteTimeMS(System.currentTimeMillis() - startSecs);
            executionData.setExecuteProgramOutput(new SerializedFile(prefixExecuteDirname(EXECUTE_STDOUT_FILENAME)));
            executionData.setExecuteStderr(new SerializedFile(prefixExecuteDirname(EXECUTE_STDERR_FILENAME)));

            if (executionData.getExecuteExitValue() != 0) {
                long returnValue = ((long) executionData.getExecuteExitValue() << 0x20) >>> 0x20;

                PrintWriter exitCodeFile = null;
                try {
                    exitCodeFile = new PrintWriter(new FileOutputStream(prefixExecuteDirname("EXITCODE.TXT"), false), true);
                    exitCodeFile.write("0x" + Long.toHexString(returnValue).toUpperCase());
                } catch (FileNotFoundException e) {
                    StaticLog.log("Unable to open file EXITCODE.TXT", e);
                    exitCodeFile = null;
                } finally {
                    if (exitCodeFile != null) {
                        exitCodeFile.close();
                    }
                }
            }
            passed = true;
        } catch (Exception e) {
            if (executionTimer != null) {
                executionTimer.stopTimer();
            }
            // SOMEDAY handle exception
            log.log(Log.INFO, "executeProgram() Exception ", e);
            throw new SecurityException(e);
        }

        return passed;
    }

    private boolean isValidDataFile(Problem inProblem) {
        boolean result = false;
        if (inProblem.getDataFileName() != null && inProblem.getDataFileName().trim().length() > 0) {
            result = true;
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
     */
    protected boolean compileProgram() {

        try {

            if (isJudge()) {
                controller.sendCompilingMessage(run);
            }

            String programName = ExecuteUtilities.replaceString(language.getExecutableIdentifierMask(), "{:basename}", ExecuteUtilities.removeExtension(runFiles.getMainFile().getName()));

            // Check whether the team submitted a executable, if they did remove
            // it.
            File program = new File(prefixExecuteDirname(programName));
            if (program.exists()) {

                // SOMEDAY log Security Warning ?
                log.config("Team submitted an executable " + programName);
                program.delete();
            }

            log.log(Log.DEBUG, "before substitution: " + language.getCompileCommandLine());
            String cmdline = executeUtilities.substituteAllStrings(language.getCompileCommandLine());

            log.log(Log.DEBUG, "after  substitution: " + cmdline);

            BufferedOutputStream stdoutlog = new BufferedOutputStream(new FileOutputStream(prefixExecuteDirname(COMPILER_STDOUT_FILENAME), false));
            BufferedOutputStream stderrlog = new BufferedOutputStream(new FileOutputStream(prefixExecuteDirname(COMPILER_STDERR_FILENAME), false));

            executionTimer = new ExecuteTimer(log, problem.getTimeOutInSeconds(), executorId, isUsingGUI());
            executionTimer.startTimer();

            long startSecs = System.currentTimeMillis();

            Process process = runProgram(cmdline, "Compiling...", false);
            if (process == null) {
                executionTimer.stopTimer();
                stderrlog.close();
                stdoutlog.close();
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
            }

            stdoutlog.close();
            stderrlog.close();

            executionData.setCompileTimeMS(System.currentTimeMillis() - startSecs);
            executionData.setCompileStdout(new SerializedFile(prefixExecuteDirname(COMPILER_STDOUT_FILENAME)));
            executionData.setCompileStderr(new SerializedFile(prefixExecuteDirname(COMPILER_STDERR_FILENAME)));

            program = new File(prefixExecuteDirname(programName));
            if (program.exists()) {
                executionData.setCompileExeFileName(programName);
                executionData.setCompileSuccess(true);
                executionData.setCompileResultCode(0);
                return true;

            } else {
                if (language.isInterpreted()) {
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

    /**
     * Get max output file size.
     * 
     * @return
     */
    private long getMaxFileSize() {
        return contest.getContestInformation().getMaxFileSize();
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
     * Run a program with ExecutionTimer.
     * 
     * 
     * @param cmdline
     * @param msg
     * @param autoStopExecution
     * @return the process started.
     */
    public Process runProgram(String cmdline, String msg, boolean autoStopExecution) {
        Process process = null;
        runProgramErrorString = "";

        executeDirectoryName = getExecuteDirectoryName();

        try {
            File runDir = new File(executeDirectoryName);
            if (runDir.isDirectory()) {
                log.config("executing: '" + cmdline + "'");
                String[] env = null;

                if (executionTimer != null) {
                    executionTimer.setDoAutoStop(autoStopExecution);
                    executionTimer.setTitle(msg);
                }

                process = Runtime.getRuntime().exec(cmdline, env, runDir);

                // if(isJudge && executionTimer != null) {
                if (executionTimer != null) {
                    executionTimer.setProc(process);
                    executionTimer.startTimer();
                }

            } else {
                executionData.setExecutionException(new Exception("Execute Directory does not exist"));
                log.config("Execute Directory does not exist");
            }
        } catch (IOException e) {
            runProgramErrorString = e.getMessage();
            log.config("Note: exec failed in RunProgram " + runProgramErrorString);
            executionData.setExecutionException(e);
            return null;
        } catch (Exception e) {
            runProgramErrorString = e.getMessage();
            log.log(Log.CONFIG, "Note: exec failed in RunProgram " + runProgramErrorString, e);
            executionData.setExecutionException(e);
            return null;
        }

        return process;
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

                Process process = Runtime.getRuntime().exec(cmdline);
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

    public Problem getProblem() {
        return problem;
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
     * This suffix is added to the execute directory nanme.
     * 
     * This must be used before using {@link #(String, String)}.
     * 
     * @see #getExecuteDirectoryName()
     * 
     * @param executeDirectoryNameSuffix
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
        return "Executable Version 9";
    }

    @Override
    public void dispose() {
        
        // TODO 164 add code
        
    }
    
}
