package edu.csus.ecs.pc2.validator.inputValidator;

import java.io.File;
import java.io.IOException;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.execute.Executable;
import edu.csus.ecs.pc2.core.execute.ExecuteException;
import edu.csus.ecs.pc2.core.execute.ExecutionData;
import edu.csus.ecs.pc2.core.execute.ProgramRunner;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.inputValidation.InputValidationResult;

/**
 * This class provides support for running (executing) CLICS-compliant Input Validators.
 * 
 * It provides methods for executing an Input Validator against a single input data file,
 * or against a set of multiple input data files.
 * 
 * @author John
 *
 */
public class InputValidatorRunner {
    
    private IInternalContest contest;
    private IInternalController controller;

    /**
     * Constructs an InputValidatorRunner for running Input Validators based on the specified contest (model) and controller.
     * 
     * Throws a null pointer exception if either constructor parameter is null.
     * 
     * @param contest - the contest model holding the data to be used by this InputValidatorRunner
     * @param controller - the controller for the contest
     * 
     * @throws NullPointerException if a null parameter is received
     */
    public InputValidatorRunner (IInternalContest contest, IInternalController controller) {
        if (contest == null || controller == null) {
            throw new NullPointerException("null passed to InputValidatorRunner constructor") ;
        }
        
        this.contest = contest;
        this.controller = controller;
    }
    
    /**
     * Runs the specified validator using the specified command, in the specified execution directory, feeding the 
     * specified data file to the validator as input and returning an {@link InputValidationResult} containing the results of
     * the validator execution.
     * 
     * @param problem the {@link Problem} associated with the validator program being run
     * @param validator a SerializedFile containing the validator program
     * @param validatorCommand a String (possibly containing PC2 substitution keywords) to be used to invoke the validator
     * @param executeDir the directory in which the execution is to take place (this is created if necessary, and it is cleared before execution)
     * @param dataFile the judge's input data file to be validated
     * 
     * @return an InputValidationResult containing the results of the validator execution
     * 
     * @throws NullPointerException if any of the input parameters is null
     * @throws {@link ExecuteException} if an exception occurs running the specified validator command 
     * @throws Exception if an exception occurs in serializing the validator execution stdout or stderr output
     */
    public InputValidationResult runInputValidator(Problem problem, SerializedFile validatorProg, String validatorCommand,
            String executeDir, SerializedFile dataFile) throws ExecuteException, Exception {

        if (validatorProg == null || validatorCommand == null || executeDir == null || dataFile == null) {
            controller.getLog().log(Log.INFO, "null parameter passed to runInputValidator()");
            throw new NullPointerException("null parameter passed to runInputValidator()");
        }

        ProgramRunner runner = new ProgramRunner(contest, controller);

        String cmdline = replaceString(validatorCommand, "{:validator}", validatorProg.getAbsolutePath());
        cmdline = replaceString(cmdline, "{:basename}", validatorProg.getName());

        Utilities.insureDir(executeDir);
        clearDirectory(executeDir);

        // copy the validator program to the execution directory
        try {
            validatorProg.writeFile(executeDir + File.separator + validatorProg.getName());
            controller.getLog().info("Copied validator file '" + validatorProg.getName() + "' to '" + executeDir + "'");
        } catch (IOException e) {
            controller.getLog().severe("Exception creating input validator program '" + validatorProg.getName() + "' in execution folder: " + e.getMessage());
        }

        // copy the input data file to the execution directory
        try {
            dataFile.writeFile(executeDir + File.separator + dataFile.getName());
            controller.getLog().info("Copied data file '" + dataFile.getName() + "' to '" + executeDir + "'");
        } catch (IOException e) {
            controller.getLog().severe("Exception creating input data file '" + dataFile.getName() + "' in execution folder: " + e.getMessage());
        }

        ExecutionData executionData = new ExecutionData();

        int msTimeout = 30000;

        String stdinFilename = dataFile.getName();
        String stdoutFilename = "runnerStdout.pc2";
        String stderrFilename = "runnerStderr.pc2";

        String stdinFilePath = executeDir + File.separator + stdinFilename;
        String stdoutFilePath = executeDir + File.separator + stdoutFilename;
        String stderrFilePath = executeDir + File.separator + stderrFilename;

        int exitCode = Constants.INPUT_VALIDATOR_EXECUTION_ERROR_CODE;
        try {
            exitCode = runner.runProgram(executionData, executeDir, cmdline, msTimeout, null, stdinFilePath, stdoutFilePath, stderrFilePath);
        } catch (ExecuteException e) {
           throw new ExecuteException("Error executing Input Validator command '" + cmdline + "': \n" + e.getMessage());
        }

        boolean passed = exitCode == Constants.INPUT_VALIDATOR_SUCCESS_EXIT_CODE ? true : false;
        
        SerializedFile stdoutResults = new SerializedFile(stdoutFilePath);
        SerializedFile stderrResults = new SerializedFile(stderrFilePath);
        
        //NOTE: the SerializedFile constructor doesn't throw exceptions; it just "sets a message"
        // Check the stdoutResults SerializedFile to make sure there are no "messages" or exceptions
        try {
            if (Utilities.serializedFileError(stdoutResults)) {
                throw new RuntimeException("Error creating SerializedFile from file ' " + stdoutFilePath + " '");
            }
        } catch (ExecuteException e) {
            System.err.println ("Exception constructing SerializedFile containing validator stdout: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println ("Exception constructing SerializedFile containing validator stdout: " + e.getMessage());
            throw e;
        }
        
        // check the stderrResults SerializedFile to make sure there are no "messages" or exceptions
        try {
            if (Utilities.serializedFileError(stderrResults)) {
                throw new RuntimeException("Error creating SerializedFile from file ' " + stderrFilePath + " '");
            }
        } catch (ExecuteException e) {
            System.err.println("Exception constructing SerializedFile containing validator stderr: " + e.getMessage());
            throw e;
        } catch (Exception e) {
             System.err.println ("Exception constructing SerializedFile containing validator stderr: " + e.getMessage());
            throw e;
        }

        return new InputValidationResult(problem, Utilities.basename(stdinFilename), passed, stdoutResults, stderrResults);

    }
    
    /**
     * Runs the specified validator using the specified command, in the specified execution directory, feeding each  
     * specified data files to the validator as input and returning an array of {@link InputValidationResult}s 
     * containing the results of the validator execution on each data set.
     * 
     * The elements of the returned array correspond in position to the data files in the specified data files array.
     * 
     * @param problem the {@link Problem} to which the validator program being run applies
     * @param validator a SerializedFile containing the validator program
     * @param validatorCommand a String (possibly containing PC2 substitution keywords) to be used to invoke the validator
     * @param executeDir the directory in which the execution is to take place (this is created if necessary, and it is cleared before execution)
     * @param dataFiles an array containing judge's input data files to be validated
     * 
     * @return an array of InputValidationResults containing the results of the validator execution on each of the specified data files, 
     *          or a zero-length array if no data files were provided
     *          
     * @throws {@link ExecuteException} if an exception occurs while running an input validator
     */
    public InputValidationResult [] runInputValidator(Problem problem, SerializedFile validator, String validatorCommand, 
                                                    String executeDir, SerializedFile [] dataFiles ) throws ExecuteException {
        
        if (dataFiles == null) {
            return new InputValidationResult [0];
        }
        
        InputValidationResult [] results = new InputValidationResult [dataFiles.length];
        
        for (int i=0; i<dataFiles.length; i++) {
            
            try {
                results[i] = runInputValidator(problem, validator, validatorCommand, executeDir, dataFiles[i]);
            } catch (ExecuteException e) {
//                System.err.println("ExecuteException running input validator: " + e.getMessage());
                throw e;
            } catch (Exception e) {
//                System.err.println ("Exception running input validator: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        return results;
    }
    
    /**
     * Runs the Input Validator contained in the specified {@link ProblemDataFiles} against the Judge's data files contained in the 
     * specified {@link Problem},
     * returning an array of {@link InputValidationResults} (one element for each data file).
     * 
     * @param problem the problem whose Input Validator is to be run
     * @param problemDataFiles the ProblemDataFiles associated with the problem
     * 
     * @return an array of InputValidationResults (length zero if the problem has no data files), 
     *              or null if the problem has no input validator or no input validator command line
     *              
     * @throws {@link ExecuteException} if an exception occurs while running the input validator for the specified problem
     */
    public InputValidationResult [] runInputValidator (Problem problem, ProblemDataFiles problemDataFiles) throws ExecuteException {
        
        SerializedFile validator = problemDataFiles.getInputValidatorFile();
        
        if (validator == null) {
            return null ;
        }
        
        String validatorCommand = problem.getInputValidatorCommandLine();
        if (validatorCommand == null || validatorCommand.equals("")) {
            return null ;
        }
        
        SerializedFile [] dataFiles = problemDataFiles.getJudgesDataFiles();
        if (dataFiles == null || dataFiles.length <=0) {
            return new InputValidationResult [0];
        }
        
        String executeDir = getExecuteDirectoryName();
        
        InputValidationResult[] results;
        try {
            results = runInputValidator(problem, validator, validatorCommand, executeDir, dataFiles);
        } catch (ExecuteException e) {
            System.err.println ("Exception running validator: " + e.getMessage());
            throw e ;
        }
        
        return results ;
        
    }

    /**
     * Replace all instances of beforeString with afterString.
     * 
     * Copied from class {@link Executable}; should be moved to {@link Utilities}.
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
     * Remove all files from specified directory, including subdirectories.
     * 
     * Copied from class {@link Executable}; should be moved to {@link Utilities}.
     * 
     * @param dirName
     *            directory to be cleared.
     * @return true if directory was cleared.
     */
    public boolean clearDirectory(String dirName) {
        File dir = null;
        boolean result = true;

        dir = new File(dirName);
        //TODO: need to handle if the new FILE() returns null (e.g. for an empty filename string)
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

    public String getExecuteDirectoryName() {
        return "inputValidate" + contest.getClientId().getSiteNumber() + contest.getClientId().getName() ;
    }


}
