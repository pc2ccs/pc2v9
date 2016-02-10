package edu.csus.ecs.pc2.core.execute;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JFileChooser;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Plugin;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.IInternalContest;
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

    /**
     * Directory where main file is found
     */
    private String mainFileDirectory;

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
     * Interface - the file created with the process return.exit code.
     */
    private static final String EXIT_CODE_FILENAME = "EXITCODE.TXT";

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
                setException ("Unable to create directory " + executeDirectoryName);
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
                    setException ("Unable to remove all files from directory " + executeDirectoryName);
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
                    if (! executionData.isCompileSuccess()) {

                        String errorMessage = "Unable to find/execute compiler using command: "+language.getCompileCommandLine();
                        if (executionData.getExecutionException() != null) {
                            errorMessage += NL + executionData.getExecutionException().getMessage();
                        }
                        
                        showDialogToUser(errorMessage);
                        fileViewer.addTextPane("Error executing compiler", errorMessage);
                        
                    } else if (executionData.getCompileStderr() == null && executionData.getCompileStdout() == null) {
                        
                        int errnoIndex = errorString.indexOf('=') + 1;
                        String errorMessage;
                        if (errorString.substring(errnoIndex).equals("2")) {
                            errorMessage = "Compiler not found, contact staff.";
    
                        } else {
                            errorMessage = "Problem executing compiler, contact staff.";
                        }
                        showDialogToUser(errorMessage);
                        setException (errorMessage);
                        fileViewer.addTextPane("Error during compile", errorMessage);
                    } // else they will get a tab hopefully showing something wrong
                }
            } else if (compileProgram()) {
                SerializedFile[] dataFiles = null;
                if (problemDataFiles != null) {
                    dataFiles = problemDataFiles.getJudgesDataFiles();
                } // else problem has no data files
                
                int dataSetNumber = 0;
                boolean passed = true;
                
                /**
                 * Did one test case fail flag.
                 */
                boolean oneTestFailed = false;
                String failedResults = "";

                if (dataFiles == null || dataFiles.length <= 1) {
                    // Only a single (at most) data set,

                    log.info("Test cases: 1 for run " + run.getNumber());

                    passed = executeAndValidateDataSet(dataSetNumber);
                    if (!passed) {
                        oneTestFailed = true;
                        failedResults = executionData.getValidationResults();
                    }
                    
                } else {

                    log.info("Test cases: " + dataFiles.length + " for run " + run.getNumber());

                    while (dataSetNumber < dataFiles.length) {
                        passed = executeAndValidateDataSet(dataSetNumber);
                        dataSetNumber++;
                        if (!passed) {
                            log.info("FAILED test case " + dataSetNumber + " for run " + run.getNumber()+" reason "+getFailureReason());
                            oneTestFailed = true;
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

                if (oneTestFailed) {
                    // replace the final executionData with the 1st failed pass
                    executionData.setValidationResults(failedResults);
                    log.info("Test results: test failed " + run + " reason = "+getFailureReason() );
                } else {
                    log.info("Test results: ALL passed for run " + run);
                }
            } else {
                /**
                 * compileProgram returns false if
                 * 1) runProgram failed (errorString set)
                 * 2) compiler fails to create expected output file (errorString empty)
                 * If there is compiler stderr or stdout we should not add the
                 * textPane saying there was an error.
                 */ 
                if (! executionData.isCompileSuccess()) {

                    String errorMessage = "Unable to find/execute compiler using command: "+language.getCompileCommandLine();
                    if (executionData.getExecutionException() != null) {
                        errorMessage += NL + executionData.getExecutionException().getMessage();
                    }
                    
                    showDialogToUser(errorMessage);
                    fileViewer.addTextPane("Error executing compiler", errorMessage);
                    
                } else if (executionData.getCompileStderr() == null && executionData.getCompileStdout() == null) {
                    int errnoIndex = errorString.indexOf('=') + 1;
                    String errorMessage;
                    if (errorString.substring(errnoIndex).equals("2")) {
                        errorMessage = "Compiler not found, contact staff.";
    
                    } else {
                        errorMessage = "Problem executing compiler, contact staff.";
                    }
                    showDialogToUser(errorMessage);
                    setException (errorMessage);
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
    
    public String getFailureReason() {

        if (executionData.getExecutionException() != null) {
            return executionData.getExecutionException().getMessage();
        } else if (executionData.getValidationResults() != null) {
            return executionData.getValidationResults();
        }

        return "Undetermined, developer note need another condition in getFailureReason()";
    }

    /**
     * Execute and validates
     * @param dataSetNumber zero based data set number.
     * @return true if passes test
     */
    private boolean executeAndValidateDataSet(int dataSetNumber) {

        boolean passed = false;
        int testNumber = dataSetNumber + 1;

        log.info("  Test case " + testNumber + " execute, run " + run.getNumber());

        if (executeProgram(dataSetNumber) && isValidated()) {
            log.info("  Test case " + testNumber + " validate, run " + run.getNumber());
            passed = validateProgram(dataSetNumber);

            if (!ExecuteUtilities.didTeamSolveProblem(executionData)) {
                passed = false;
            }

        } else {
            passed = false;
        }
        
        String reason = getFailureReason();
        if (reason == null) {
            reason = "";
        } else {
            reason = "; validator returns: " + reason;
        }

        log.info("  Test case " + testNumber + " passed = " + Utilities.yesNoString(passed) + " " + reason);

        JudgementRecord record = JudgementUtilites.createJudgementRecord(contest, run, executionData, executionData.getValidationResults());
        
//        Judgement judgement = getContest().getJudgement(record.getJudgementId());
//        log.info("  Test case " + testNumber + " passed = " + Utilities.yesNoString(passed) + " judgement =  " + judgement);
    
        RunTestCase runTestCase = new RunTestCase(run, record, testNumber, passed);
        runTestCase.setElapsedMS(executionData.getExecuteTimeMS());
        run.addTestCase(runTestCase);
        return passed;
    }

    /**
     * Extracts file setNumber from list of files (fileList).
     * 
     * @param fileList -
     *            list of SerializedFile's
     * @param setNumber -
     *            index in list of file to write, zero based.
     * @param outputFileName -
     *            output file name.
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
            // will return false if file could not be created
            return Utilities.createFile(file, filename);
        } catch (IOException e) {
            log.log(Log.INFO, "Could not create "+filename, e);
            return false;
        }
    }

    /**
     * Show pop up mesage to user.
     * @param string
     */
    protected void showDialogToUser(String string) {
        
        if (showMessageToUser){
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
            setException ("Executable.execute(RunData): Directory " + dir.getName() + " could not be created.");
        }

        return dir.isDirectory();
    }

    protected boolean validateProgram(int dataSetNumber) {

        // SOMEDAY Handle the error messages better, log and put them before the user to
        // help with debugging

        executionData.setValidationReturnCode(-1);
        executionData.setValidationSuccess(false);
        
        if (isJudge()){
            controller.sendValidatingMessage(run);
        }

        if (problemDataFiles != null && problemDataFiles.getValidatorFile() != null) {
            // Create Validation Program

            String validatorFileName = problemDataFiles.getValidatorFile().getName();
            String validatorUnpackName = prefixExecuteDirname(validatorFileName);
            if (!createFile(problemDataFiles.getValidatorFile(), validatorUnpackName)) {
                log.info("Unable to create validator program " + validatorUnpackName);
                setException ("Unable to create validator program " + validatorUnpackName);

                throw new SecurityException("Unable to create validator, check logs");
            }

            if (!validatorFileName.endsWith(".jar")) {
                /**
                 * Unix validator programs must set the execute bit to be able to execute the program.
                 */

                setExecuteBit(prefixExecuteDirname(validatorFileName));
            }
        }
        
        /**
         * Judge input data file name, either short name or fully qualified if external file.{:infile}
         */
        String judgeDataFilename =  problem.getDataFileName();
        /**
         * Judge answer  data file name, either short name or fully qualified if external file.{:infile}
         */
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
                
                if (problemDataFiles == null){
                    throw new NullPointerException("Internal error - no data files present for problem "+problem);
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

        String teamsOutputFilename = getTeamOutputFilename(dataSetNumber);
        
        if (executionData.getExecuteExitValue() != 0) {
            long returnValue = ((long) executionData.getExecuteExitValue() << 0x20) >>> 0x20;

            PrintWriter exitCodeFile = null;
            try {
                exitCodeFile = new PrintWriter(new FileOutputStream(teamsOutputFilename, true), true);
                exitCodeFile.write("Team program exit code = 0x"+Long.toHexString(returnValue).toUpperCase()+NL);
            } catch (FileNotFoundException e) {
                log.log(Log.WARNING, "Unable to append to file "+teamsOutputFilename, e);
                exitCodeFile = null;
            } finally {
                if (exitCodeFile != null) {
                    exitCodeFile.close();
                }
            }
        }
        if (executionData.getExecuteStderr() != null) {
            byte[] errBuff = executionData.getExecuteStderr().getBuffer();
            FileOutputStream outputStream = null;
            try {
                if (errBuff != null && errBuff.length > 0) {
                    outputStream = new FileOutputStream(teamsOutputFilename, true);
                    outputStream.write(("*** Team STDERR Follows:"+NL).getBytes());
                    outputStream.write(errBuff, 0, errBuff.length);
                    outputStream.close();
                }
            } catch (IOException e) {
                log.log(Log.WARNING, "Unable to append to file "+teamsOutputFilename, e);
            }
        }
        
        String secs = Long.toString((new Date().getTime()) % 100);

        // Answer/results XML file name

        int testSetNumber = dataSetNumber + 1;
        String resultsFileName = run.getNumber() + secs + "XRSAM." + testSetNumber + ".txt";

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

            String pathToPC2Jar = findPC2JarPath ();
            if (!(new File(pathToPC2Jar+"pc2.jar")).exists()) {
                pc2JarUseDirectory = true;
            }
            commandPattern = "java -cp " + pathToPC2Jar + problem.getValidatorCommandLine();
        }

        log.log(Log.DEBUG, "before substitution: " + commandPattern);

//      orig  String cmdLine = substituteAllStrings(run, commandPattern);
        
        String cmdLine = replaceString(commandPattern, "{:infile}", judgeDataFilename);
        cmdLine = replaceString(cmdLine, "{:ansfile}", judgeAnswerFilename);
        
        cmdLine = substituteAllStrings(run, cmdLine);
        cmdLine = replaceString(cmdLine, "{:resfile}", resultsFileName);

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
            log.log(Log.INFO, "Exception while constructing validator command line ", e);
            executionData.setExecutionException(e);
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
            executionData.setExecutionException(ex);
            if (executionTimer != null) {
                executionTimer.stopTimer();
            }
            log.log(Log.CONFIG, "Exception in validator ", ex);
        }
        String validatorOutputFilename = prefixExecuteDirname("valout."+dataSetNumber + ".txt");
        createFile(executionData.getValidationStdout(), validatorOutputFilename);
        validatorOutputFilenames.add(validatorOutputFilename);
        String validatorStderrFilename = prefixExecuteDirname("valerr."+dataSetNumber + ".txt");
        createFile(executionData.getValidationStderr(), validatorStderrFilename);
        validatorStderrFilesnames.add(validatorStderrFilename);

        boolean fileThere = new File(prefixExecuteDirname(resultsFileName)).exists();

        try {
            if (fileThere) {
                
                storeValidatorResults(resultsFileName, log);
                
            } else {
                // SOMEDAY LOG
                log.config("validationCall - Did not produce output results file " + resultsFileName);
//                JOptionPane.showMessageDialog(null, "Did not produce output results file " + resultsFileName + " contact staff");
            }
        } catch (Exception ex) {
            executionData.setExecutionException(ex);
            log.log(Log.INFO, "Exception while reading results file "+resultsFileName, ex);
            throw new SecurityException(ex);
        } finally {
            
            if ( executionData.isRunTimeLimitExceeded()){
                executionData.setValidationResults("No - Time Limit Exceeded");
                executionData.setValidationSuccess(true);
            }
        }

        return executionData.isValidationSuccess();
    }

    private String getTeamOutputFilename(int dataSetNumber) {
        return prefixExecuteDirname("teamoutput." + dataSetNumber + ".txt");
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
        
        /**
         * returns true if valid XML and found outcome tag.
         */
        boolean done = parser.parseValidatorResultsFile(prefixExecuteDirname(resultsFileName));
        Hashtable<String, String> results = parser.getResults();
        
        if (parser.getException() != null){
            logger.log(Log.WARNING,"Exception parsing XML in file "+resultsFileName, parser.getException());
            
        } else if (done && results != null && results.containsKey("outcome")) {
            // non-IJRM does not require security, but if it is IJRM it better have security.
            
            if (!problem.isInternationalJudgementReadMethod() || (results.containsKey("security") && resultsFileName.equals(results.get("security")))) {
                // Found the string
                executionData.setValidationResults(results.get("outcome"));
                executionData.setValidationSuccess(true);
            } else {
                // SOMEDAY LOG info
                setException( "validationCall - results file did not contain security");

                logger.config("validationCall - results file did not contain security");
                logger.config(resultsFileName + " != " + results.get("security"));
            }
        } else {
            if (!done) {
                // SOMEDAY LOG
                // SOMEDAY show user message
                setException( "Error parsing/reading results file, check log");

                logger.config("Error parsing/reading results file, check log");
            } else if (results != null && (!results.containsKey("outcome"))) {
                // SOMEDAY LOG
                // SOMEDAY show user message
                setException( "Error parsing/reading results file, check log");
                logger.config("Error could not find 'outcome' in results file, check log");
            } else {
                // SOMEDAY LOG
                // SOMEDAY show user message
                logger.config("Error parsing results file, check log");
            }
        }

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

    protected String findPC2JarPath() {
        String jarDir = ".";
        try {
            String defaultPath = new File("./build/prod").getCanonicalPath(); 
            // for CruiseControl, will not be needed with jenkins
            if (! new File(defaultPath).exists()) {
                defaultPath = "/software/pc2/cc/projects/pc2v9/build/prod";
            }
            jarDir = defaultPath;
            String cp = System.getProperty("java.class.path");
            StringTokenizer st = new StringTokenizer(cp, File.pathSeparator);
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                File dir = new File(token);
                if (dir.exists() && dir.isFile()
                        && dir.toString().endsWith("pc2.jar")) {
                    jarDir = new File(dir.getParent()).getCanonicalPath()+File.separator;
                    break;
                }
            }
            if (defaultPath.equals(jarDir)){
               File dir = new File("dist/pc2.jar");
               if (dir.isFile()) {
                   jarDir = new File(dir.getParent()).getCanonicalPath()+File.separator;
               }
            }
        } catch (IOException e) {
            log.log(Log.WARNING, "Trouble locating pc2home: " + e.getMessage(), e);
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
     * @param dataSetNumber a zero based data set number
     * @param writeJudgesDataFiles
     * @return true if execution worked.
     */
    protected boolean executeProgram(int dataSetNumber) {
        boolean passed = false;
        String inputDataFileName = null;
        
        // a one-based test data set number
        int testSetNumber = dataSetNumber + 1;

        try {
            if (isJudge()){
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
                /**
                 * Team executing run
                 */

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
                /**
                 * Judge execute run
                 */
                
                // Extract the judge data file for this problem and dataSetNumber.
                
                if ( ! problem.isUsingExternalDataFiles() ){
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
                     * External data files (not inside of pc2).  On local disk.
                     */
                    
                    SerializedFile serializedFile = problemDataFiles.getJudgesDataFiles()[dataSetNumber];
                    String dataFileName = Utilities.locateJudgesDataFile(problem, serializedFile, getContestInformation().getJudgeCDPBasePath(), Utilities.DataFileType.JUDGE_DATA_FILE);
                    
                    if (dataFileName != null){
                        // Found file 
                        
                        File dataFile = new File(dataFileName);
                        inputDataFileName = dataFile.getCanonicalPath();
                        log.info("(External) Input data file: "+inputDataFileName);
                        
                    } else {
                        
                        // Did not find file
                        
                        String expectedFileName = serializedFile.getName();
                        log.log(Log.DEBUG,"For problem "+problem+" test case "+testSetNumber+" expecting file "+expectedFileName+" in dir "+problem.getCCSfileDirectory());
                        FileNotFoundException notFound = new FileNotFoundException(expectedFileName + " for test case "+testSetNumber);
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
                if (language.isUsingJudgeProgramExecuteCommandLine()){
                    
                    /**
                     * Use Judge execution command line (override).
                     */
                    cmdline = language.getJudgeProgramExecuteCommandLine();
                    log.info("Using judge command line "+cmdline);
                }
                
            }
            
            log.log(Log.DEBUG, "before substitution: " + cmdline);
            cmdline = substituteAllStrings(run, cmdline);
            log.log(Log.DEBUG, "after  substitution: " + cmdline);

            /**
             * Insure that the first command in the command
             * line can be executed by prepending the execute
             * directory name. 
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
                 * If the first word is a existing file, use
                 * the full path
                 */
                cmdline = f.getCanonicalPath();
            }
            
            boolean autoStop = false;
            if (! isTestRunOnly()){
                /**
                 * Auto stop on time limit exceeded - Judge (aka non-Team) only.
                 */
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

            if ( problem.isReadInputDataFromSTDIN()) {
                log.info("Using STDIN from file " +inputDataFileName);
                    
                BufferedOutputStream out = new BufferedOutputStream(process.getOutputStream());
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(inputDataFileName));
                byte[] buf = new byte[32768];
                int c;
                try {
                    while ((c = in.read(buf))!= -1) {
                        out.write(buf, 0, c);
                    }
                } catch (java.io.IOException e) {
                    log.info("Caught a "+e.getMessage()+" do not be alarmed.");
                }

                in.close();
                out.close();
            }

            stdoutCollector.join();
            stderrCollector.join();

            if (executionTimer != null) {
                executionTimer.stopTimer();
                executionData.setRunTimeLimitExceeded(executionTimer.isRunTimeLimitExceeded());
                // SOMEDAY - this happens much too much find out why time limit is 10 when should be 30 by default.
                log.info("Run exceeded time limit "+problem.getTimeOutInSeconds()+" secs, Run = "+run);
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
                    exitCodeFile.write("0x"+Long.toHexString(returnValue).toUpperCase());
                } catch (FileNotFoundException e) {
                    log.log(Log.WARNING, "Unable to open/write file "+EXIT_CODE_FILENAME, e);
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
            // SOMEDAY  handle exception
            log.log(Log.INFO, "Exception in executeProgram()", e);
            executionData.setExecutionException(e);
            throw new SecurityException(e);
        }

        return passed;
    }


    protected boolean isValidDataFile(Problem inProblem) {
        boolean result = false;
        if (inProblem.getDataFileName() != null && inProblem.getDataFileName().trim().length() > 0) {
            result = true;
        }
        if (inProblem.isUsingExternalDataFiles()){
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
     */
    protected boolean compileProgram() {

        try {
            
            if (isJudge()){
                controller.sendCompilingMessage(run);
            }

            String programName = replaceString(language.getExecutableIdentifierMask(), "{:basename}", removeExtension(runFiles.getMainFile().getName()));

            // Check whether the team submitted a executable, if they did remove
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

            executionTimer = new ExecuteTimer(log, problem.getTimeOutInSeconds(), executorId, isUsingGUI());
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
     * @return
     */
    private long getMaxFileSize() {
        return contest.getContestInformation().getMaxFileSize();
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

            // SOMEDAY LanguageId and ProblemId are now a long string not an int,
            // what should we do?

            if (inRun.getLanguageId() != null) {
                Language[] langs=contest.getLanguages();
                int index = 0;
                String displayName="";
                for (int i = 0; i < langs.length; i++) {
                    if (langs[i] != null && langs[i].getElementId().equals(inRun.getLanguageId())) {
                        displayName = langs[i].getDisplayName().toLowerCase().replaceAll(" ", "_");
                        index=i+1;
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
                Problem[] problems=contest.getProblems();
                int index = 0;
                for (int i = 0; i < problems.length; i++) {
                    if (problems[i] != null && problems[i].getElementId().equals(inRun.getProblemId())) {
                        index=i+1;
                        break;
                    }
                }
                if (index > 0) {
                    newString = replaceString(newString, "{:problem}", index);
                    newString = replaceString(newString, "{:problemletter}", Utilities.convertNumber(index));
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
            executionData.setExecutionException(e);
            return null;
        } catch (Exception e) {
            errorString = e.getMessage();
            log.log(Log.CONFIG, "Note: exec failed in RunProgram " + errorString, e);
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
    
    public void setExecuteDirectoryName(String executeDirectoryName) {
        this.executeDirectoryName = executeDirectoryName;
    }
    
    /**
     * Prepend basedirectoryname in front of executedirectory name.
     * 
     * @param baseDirectoryName
     */
    public void setExecuteBaseDirectoryName (String baseDirectoryName) throws Exception {
        insureDir(baseDirectoryName);
        if (! isDirectory(baseDirectoryName)) {
            throw new IOException ("Could not create directory "+baseDirectoryName);
        }
        String dirname = baseDirectoryName + File.separator + executeDirectoryName;
        insureDir(baseDirectoryName);
        if (! isDirectory(dirname)) {
            throw new IOException ("Could not create directory "+dirname);
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
     * Get filename for each team's output for each test case.
     * @return the list of team output file names.
     */
    public List<String> getTeamsOutputFilenames() {
        return teamsOutputFilenames;
    }

    /**
     * Get filename for each team's output for each test case.
     * @return the list of team output file names.
     */
    public List<String> getValidatorOutputFilenames() {
        return validatorOutputFilenames;
    }

    /**
     * Get filename for each team's output for each test case.
     * @return the list of team output file names.
     */
    public List<String> getValidatorErrFilenames() {
        return validatorStderrFilesnames;
    }
}

