package edu.csus.ecs.pc2.core.execute;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.swing.JFileChooser;

import edu.csus.ecs.pc2.core.IInternalController;
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

// TODO this class contains a number of Utility methods like: baseName, replaceString... etc
// should these routines be placed in a static way in a static class ?
// TODO design decision how to handle MultipleFileViewer, display here, on TeamClient??

// $HeadURL$
public class Executable {

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

    private boolean showMessageToUser;

    public Executable(IInternalContest inContest, IInternalController inController, Run run, RunFiles runFiles) {
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
    private boolean clearDirectory(String dirName) {
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
     * @param clearDirFirst -
     *            clear the directory before unpacking and executing
     * @return FileViewer with 1 or more tabs
     */
    public IFileViewer execute(boolean clearDirFirst) {
        fileViewer = new MultipleFileViewer(log);

        try {
            controller.sendExecutingMessage(run);
            
            executionData = new ExecutionData();
            
            executeDirectoryName = getExecuteDirectoryName();

            boolean dirThere = insureDir(executeDirectoryName);
            

            if (!dirThere) {
                log.config("Directory could not be created: " + executeDirectoryName);
                showDialogToUser("Unable to create directory " + executeDirectoryName);
                setException (executionData, "Unable to create directory " + executeDirectoryName);
                return fileViewer;
            }

            if (clearDirFirst && overwriteJudgesDataFiles) {
                // Clear directory out before compiling.

                /**
                 * Do not clear directory if writeJudgesDataFiles is false, because if we are not overwriting the judge's data file, then erasing the existing files makes no sense.
                 */

                boolean cleared = clearDirectory(executeDirectoryName);
                if (!cleared) {
                    // TODO LOG error Directory could not be cleared, other process running?
                    log.config("Directory could not be cleared, other process running? ");

                    showDialogToUser("Unable to remove all files from directory " + executeDirectoryName);
                    setException (executionData, "Unable to remove all files from directory " + executeDirectoryName);
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
                     * 1) runProgram failed (errorString set)
                     * 2) compiler fails to create expecte output file (errorString empty)
                     * If there is compiler stderr or stdout we should not add the
                     * textPane saying there was an error.
                     */ 
                    if (executionData.getCompileStderr() == null && executionData.getCompileStdout() == null) {
                        int errnoIndex = errorString.indexOf('=') + 1;
                        String errorMessage;
                        if (errorString.substring(errnoIndex).equals("2")) {
                            errorMessage = "Compiler not found, contact staff.";
    
                        } else {
                            errorMessage = "Problem executing compiler, contact staff.";
                        }
                        showDialogToUser(errorMessage);
                        setException (executionData, errorMessage);
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

                    while (passed && dataSetNumber < dataFiles.length) {
                        if (executeProgram(dataSetNumber) && isValidated()) {
                            passed = validateProgram(dataSetNumber);
                        } else {
                            passed = false; // didn't execute.
                        }

                        dataSetNumber++;
                    }
                }
            } else {
                /**
                 * compileProgram returns false if
                 * 1) runProgram failed (errorString set)
                 * 2) compiler fails to create expecte output file (errorString empty)
                 * If there is compiler stderr or stdout we should not add the
                 * textPane saying there was an error.
                 */ 
                if (executionData.getCompileStderr() == null && executionData.getCompileStdout() == null) {
                    int errnoIndex = errorString.indexOf('=') + 1;
                    String errorMessage;
                    if (errorString.substring(errnoIndex).equals("2")) {
                        errorMessage = "Compiler not found, contact staff.";
    
                    } else {
                        errorMessage = "Problem executing compiler, contact staff.";
                    }
                    showDialogToUser(errorMessage);
                    setException (executionData, errorMessage);
                    fileViewer.addTextPane("Error during compile", errorMessage);
                } // else they will get a tab hopefully showing something wrong
            }

            File file;
            String outputFile;

            fileViewer.setTitle("Executable");

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
                fileViewer.addTextPane("Program output", "PC2: execution of program did not generate any output");
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

                fileViewer.setInformationLabelText("<html><font size='+1' color='red'>Team program exit code = 0x" + Long.toHexString(returnValue).toUpperCase()+"</font>");

            } else {
                fileViewer.setInformationLabelText("");
            }
            
            if (!isTestRunOnly()) {
                if (problem.isShowCompareWindow()) {
                    String teamsOutputFileName = prefixExecuteDirname(EXECUTE_STDOUT_FILENAME);

                    if (problem.getAnswerFileName() != null && problem.getAnswerFileName().length() > 0) {
                        String answerFileName = prefixExecuteDirname(problem.getAnswerFileName());
                        if (! new File(answerFileName).isFile()){
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

    /**
     * Show pop up mesage to user.
     * @param string
     */
    private void showDialogToUser(String string) {
        
        if (showMessageToUser){
            fileViewer.showMessage(string);
        }
    }

    /**
     * Insure directory exists, if does not exist create it.
     * 
     * @param dirName
     *            directory to create.
     * @return whether directory exists.
     */
    boolean insureDir(String dirName) {
        File dir = null;

        dir = new File(dirName);
        if (!dir.exists() && !dir.mkdir()) {
            log.log(Log.CONFIG, "Executable.execute(RunData): Directory " + dir.getName() + " could not be created.");
            setException (executionData, "Executable.execute(RunData): Directory " + dir.getName() + " could not be created.");
        }

        return dir.isDirectory();
    }

    private boolean validateProgram(int dataSetNumber) {

        // TODO Handle the error messages better, log and put them before the user to
        // help with debugging

        executionData.setValidationReturnCode(-1);
        executionData.setValidationSuccess(false);
        
        controller.sendValidatingMessage(run);

        if (problemDataFiles.getValidatorFile() != null) {
            // Create Validation Program

            String validatorFileName = problemDataFiles.getValidatorFile().getName();
            String validatorUnpackName = prefixExecuteDirname(validatorFileName);
            if (!createFile(problemDataFiles.getValidatorFile(), validatorUnpackName)) {
                log.info("Unable to create validator program " + validatorUnpackName);
                setException (executionData, "Unable to create validator program " + validatorUnpackName);

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

            // Create the input data file
            createFile(problemDataFiles.getJudgesDataFiles(), dataSetNumber, prefixExecuteDirname(problem.getDataFileName()));

            // Create the correct output file, aka answer file
            createFile(problemDataFiles.getJudgesAnswerFiles(), dataSetNumber, prefixExecuteDirname(problem.getAnswerFileName()));

        }

        // teams output file
        SerializedFile userOutputFile = executionData.getExecuteProgramOutput();
        createFile(userOutputFile, prefixExecuteDirname(userOutputFile.getName()));

        String secs = new Long((new Date().getTime()) % 100).toString();

        // Answer/results file name

        String resultsFileName = run.getNumber() + secs + "XRSAM.txt";

        /*
         * <validator> <input_filename> <output_filename> <answer_filename> <results_file> -pc2|-appes [other files]
         */

        /**
         * Standard command line pattern
         * 
         * String commandPattern = "{:validator} {:infile} {:outfile} {:ansfile} {:resfile} ";
         */

        String commandPattern = problem.getValidatorCommandLine();

        if (problem.isUsingPC2Validator()) {

            /**
             * The internal command is set to: <validator> <input_filename> <output_filename> <answer_filename> <results_file> -pc2|-appes [other files] Where validator is
             * Problem.INTERNAL_VALIDATOR_NAME aka "pc2.jar edu.csus.ecs.pc2.validator.Validator"
             * 
             * So we need to prefix the command with java -jar <path to jar>
             */

            String pathToPC2Jar = findPC2JarPath ();
            commandPattern = "java -cp " + pathToPC2Jar + problem.getValidatorCommandLine();

        }

        log.log(Log.DEBUG, "before substitution: " + commandPattern);

        String cmdLine = substituteAllStrings(run, commandPattern);
        cmdLine = replaceString(cmdLine, "{:resfile}", resultsFileName);

        log.log(Log.DEBUG, "after  substitution: " + cmdLine);

        if (File.separator.equals("\\")) {
            if (problem.isUsingPC2Validator()) {
                cmdLine = cmdLine.replaceFirst("-cp ", "-cp \"");
                cmdLine = cmdLine.replaceFirst("jar ", "jar\" ");
                log.log(Log.DEBUG, "after replaceFirst: " + cmdLine);
            }
        }
        
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
            setException (executionData, "Exception in validatorCall "+e.getMessage());

            log.log(Log.INFO, "Exception in validatorCall ", e);
            throw new SecurityException(e);
        }

        try {

            PrintWriter stdoutlog = new PrintWriter(new FileOutputStream(prefixExecuteDirname(VALIDATOR_STDOUT_FILENAME), false), true);
            PrintWriter stderrlog = new PrintWriter(new FileOutputStream(prefixExecuteDirname(VALIDATOR_STDERR_FILENAME), false), true);

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
            BufferedReader childOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            // The reads from the stderr of the child process
            BufferedReader childError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

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
                // TODO LOG - why are we logging this ?
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
                        // TODO LOG info
                        setException (executionData, "validationCall - results file did not contain security");

                        log.config("validationCall - results file did not contain security");
                        log.config(resultsFileName + " != " + results.get("security"));
                    }
                } else {
                    if (!done) {
                        // TODO LOG
                        // TODO show user message
                        setException (executionData, "Error parsing/reading results file, check log");

                        log.config("Error parsing/reading results file, check log");
                    } else if (results != null && (!results.containsKey("outcome"))) {
                        // TODO LOG
                        // TODO show user message
                        setException (executionData, "Error parsing/reading results file, check log");
                        log.config("Error could not find 'outcome' in results file, check log");
                    } else {
                        // TODO LOG
                        // TODO show user message
                        log.config("Error parsing results file, check log");
                    }
                }
            } else {
                // TODO LOG
                log.config("validationCall - Did not produce output results file " + resultsFileName);
//                JOptionPane.showMessageDialog(null, "Did not produce output results file " + resultsFileName + " contact staff");
            }
        } catch (Exception ex) {
            log.log(Log.INFO, "Exception in validation  ", ex);
            throw new SecurityException(ex);
        } finally {
            
            if ( executionData.isRunTimeLimitExceeded()){
                executionData.setValidationResults("No - Time Limit Exceeded");
                executionData.setValidationSuccess(true);
            }
        }

        return executionData.isValidationSuccess();
    }

    /**
     * Sets the exception for this execute().
     * 
     * @param inExecutionData
     * @param string
     */
    private void setException(ExecutionData inExecutionData, String string) {
        inExecutionData.setExecutionException(new Exception(string));
    }

    private String findPC2JarPath() {
        String jarDir = ".."+File.separator+".classes"+File.pathSeparator; // default to ..\.classes (eclipse) directory
        try {
            String cp = System.getProperty("java.class.path");
            StringTokenizer st = new StringTokenizer(cp, File.pathSeparator);
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                File dir = new File(token);
                if (dir.exists() && dir.isFile()
                        && dir.toString().endsWith("pc2.jar")) {
                    jarDir = new File(dir.getParent()).getCanonicalPath();
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Trouble locating pc2home: " + e.getMessage());
        }
        return jarDir+File.separator;
    }

    /**
     * Returns true if validator should be run/executed.
     * 
     * @return true if should be validated.
     */
    private boolean isValidated() {
        return (problem.isValidatedProblem() && (!isTestRunOnly()));
    }

    public String getFileNameFromUser() {
        String outFileName = null;
        JFileChooser chooser = new JFileChooser(mainFileDirectory);
        try {
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                mainFileDirectory = chooser.getCurrentDirectory().getAbsolutePath();
                outFileName = chooser.getSelectedFile().getCanonicalFile().toString();
            }
        } catch (Exception e) {
            // TODO log this exception
            log.log(Log.CONFIG, "Error getting selected file, try again.", e);
        }
        chooser = null;

        return outFileName;
    }

    /**
     * Select using File Open GUI file and copy to execute directory.
     * 
     */
    private void selectAndCopyDataFile(String inputFileName) throws Exception {
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
     * @param writeJudgesDataFiles
     * @return true if execution worked.
     */
    private boolean executeProgram(int dataSetNumber) {
        boolean passed = false;
        String inputDataFileName = null;

        try {
            executionTimer = new ExecuteTimer(log, problem.getTimeOutInSeconds(), executorId);
            executionTimer.startTimer();

            if (problem.getDataFileName() != null) {
                inputDataFileName = prefixExecuteDirname(problem.getDataFileName());
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
                if (inputDataFileName != null && problemDataFiles.getJudgesDataFiles() != null) {

                    if (overwriteJudgesDataFiles) {
                        // create the judges data file on disk.
                        if (!createFile(problemDataFiles.getJudgesDataFiles(), dataSetNumber, inputDataFileName)) {
                            throw new SecurityException("Unable to create data file " + inputDataFileName);
                        }
                    }
                    // Else, leave whatever data file is present.
                }
            }

            // TODO execute the language.getProgramExecuteCommandLine();

            PrintWriter stdoutlog = new PrintWriter(new FileOutputStream(prefixExecuteDirname(EXECUTE_STDOUT_FILENAME), false), true);
            PrintWriter stderrlog = new PrintWriter(new FileOutputStream(prefixExecuteDirname(EXECUTE_STDERR_FILENAME), false), true);

            String cmdline = language.getProgramExecuteCommandLine();
            log.log(Log.DEBUG, "before substitution: " + cmdline);

            cmdline = substituteAllStrings(run, cmdline);
            log.log(Log.DEBUG, "after  substitution: " + cmdline);

            // TODO comment why actfilename code is needed.

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
            
            boolean autoStop = false;
            if (! isTestRunOnly()){
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
            BufferedReader childOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            // The reads from the stderr of the child process
            BufferedReader childError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            IOCollector stdoutCollector = new IOCollector(log, childOutput, stdoutlog, executionTimer, getMaxFileSize() + ERRORLENGTH);
            IOCollector stderrCollector = new IOCollector(log, childError, stderrlog, executionTimer, getMaxFileSize() + ERRORLENGTH);

            executionTimer.setIOCollectors(stdoutCollector, stderrCollector);
            executionTimer.setProc(process);

            stdoutCollector.start();
            stderrCollector.start();

            if (isValidDataFile(problem) && problem.isReadInputDataFromSTDIN()) {
                OutputStream outs = process.getOutputStream();
                PrintWriter pwOut = new PrintWriter(outs);
                FileReader fileReader = new FileReader(inputDataFileName);
                BufferedReader in = new BufferedReader(fileReader);

                int theChar = in.read();
                while (theChar != -1) {
                    pwOut.print((char) theChar);
                    theChar = in.read();
                }

                pwOut.close();
                outs.close();
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
                    exitCodeFile.write("0x"+Long.toHexString(returnValue).toUpperCase());
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
            // TODO: handle exception
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


    /**
     * Extract source file and run compile command line script.
     * 
     * @return true if executable is created.
     */
    private boolean compileProgram() {

        try {
            
            controller.sendCompilingMessage(run);

            String programName = replaceString(language.getExecutableIdentifierMask(), "{:basename}", removeExtension(runFiles.getMainFile().getName()));

            // Check whether the team submitted a executable, if they did remove
            // it.
            File program = new File(prefixExecuteDirname(programName));
            if (program.exists()) {

                // TODO log Security Warning ?
                log.config("Team submitted an executable " + programName);
                program.delete();
            }

            log.log(Log.DEBUG, "before substitution: " + language.getCompileCommandLine());
            String cmdline = substituteAllStrings(run, language.getCompileCommandLine());
            log.log(Log.DEBUG, "after  substitution: " + cmdline);

            PrintWriter stdoutlog = new PrintWriter(new FileOutputStream(prefixExecuteDirname(COMPILER_STDOUT_FILENAME), false), true);
            PrintWriter stderrlog = new PrintWriter(new FileOutputStream(prefixExecuteDirname(COMPILER_STDERR_FILENAME), false), true);

            executionTimer = new ExecuteTimer(log, problem.getTimeOutInSeconds(), executorId);
            executionTimer.startTimer();

            long startSecs = System.currentTimeMillis();
            
            Process process = runProgram(cmdline, "Compiling...", false);
            if (process == null) {
                executionTimer.stopTimer();
                stderrlog.close();
                stdoutlog.close();
                // errorString will be set by 
                executionData.setCompileExeFileName("");
                executionData.setCompileSuccess(false);
                executionData.setCompileResultCode(1);
                return false;
            }
            // This reads from the stdout of the child process
            BufferedReader childOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            // The reads from the stderr of the child process
            BufferedReader childError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

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
                // TODO why do we care??
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
                executionData.setCompileExeFileName("");
                executionData.setCompileSuccess(false);
                executionData.setCompileResultCode(2);
                return false;
            }

        } catch (Exception e) {
            if (executionTimer != null) {
                executionTimer.stopTimer();
            }
            // TODO: handle exception
            log.log(Log.INFO, "Exception ", e);
            throw new SecurityException(e);
        }
    }

    private long getMaxFileSize() {
        // TODO code get this value from the setting on the admin
        return 512000;
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

        if (origString == null) {
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
     * </pre>
     * 
     * @param inRun
     *            submitted by team
     * @param origString -
     *            original string to be substituted.
     * @return string with values
     */
    public String substituteAllStrings(Run inRun, String origString) {
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
            newString = replaceString(newString, "{:basename}", removeExtension(runFiles.getMainFile().getName()));

            String validatorCommand = null;

            if (problem.getValidatorProgramName() != null) {
                validatorCommand = problem.getValidatorProgramName();
            }

            if (problemDataFiles != null) {
                SerializedFile validatorFile = problemDataFiles.getValidatorFile();
                if (validatorFile != null) {
                    validatorCommand = validatorFile.getName(); // validator
                }
            }
            
            if (validatorCommand != null) {
                newString = replaceString(newString, "{:validator}", validatorCommand);
            }

            // TODO LanguageId and ProblemId are now a long string not an int,
            // what should we do?

            if (inRun.getLanguageId() != null) {
                newString = replaceString(newString, "{:language}", inRun.getLanguageId().toString());
            }
            if (inRun.getProblemId() != null) {
                newString = replaceString(newString, "{:problem}", inRun.getProblemId().toString());
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
        } catch (Exception e) {
            // TODO LOG
            log.log(Log.CONFIG, "Exception ", e);
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
    private String getDirName(SerializedFile file) {
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
        errorString = "";
        
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
                errorString = "Execute Directory does not exist";
                log.config("Execute Directory does not exist");
            }
        } catch (IOException e) {
            errorString = e.getMessage();
            log.config("Note: exec failed in RunProgram " + errorString);
            return null;
        } catch (Exception e) {
            errorString = e.getMessage();
            log.log(Log.CONFIG, "Note: exec failed in RunProgram " + errorString, e);
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

    /**
     * Extracts file setNumber from list of files (fileList).
     * 
     * @param fileList -
     *            list of SerializedFile's
     * @param setNumber -
     *            index in list of file to write.
     * @param outputFileName -
     *            output file name.
     * @return true if file written to disk.
     */
    boolean createFile(SerializedFile[] fileList, int setNumber, String outputFileName) {

        if (fileList != null) {
            if (setNumber < fileList.length) {
                return createFile(fileList[setNumber], outputFileName);
            }
        }

        return false;
    }

    /**
     * Create disk file for input SerializedFile.
     * 
     * Returns true if file is written to disk and is not null.
     * 
     * @param file
     * @param outputFileName
     * @return true if file written to disk.
     * @throws IOException
     */
    boolean createFile(SerializedFile file, String outputFileName) {
        try {
            if (file != null && outputFileName != null) {
                file.writeFile(outputFileName);
                return new File(outputFileName).isFile();
            }
        } catch (Exception e) {
            log.log(Log.INFO, "Exception creating file " + outputFileName, e);
        }

        return false;
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
     * @param showMessageToUser
     */
    public void setShowMessageToUser(boolean showMessageToUser) {
        this.showMessageToUser = showMessageToUser;
    }

    public String getExecuteDirectoryNameSuffix() {
        return executeDirectoryNameSuffix;
    }

    // huh
    
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
}
