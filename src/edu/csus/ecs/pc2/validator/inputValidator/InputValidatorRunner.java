package edu.csus.ecs.pc2.validator.inputValidator;

import java.io.File;
import java.io.IOException;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.execute.Executable;
import edu.csus.ecs.pc2.core.execute.ExecutionData;
import edu.csus.ecs.pc2.core.execute.ProgramRunner;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.ui.InputValidationResult;

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
     * @param validator a SerializedFile containing the validator program
     * @param validatorCommand a String (possibly containing PC2 substitution keywords) to be used to invoke the validator
     * @param executeDir the directory in which the execution is to take place (this is created if necessary, and it is cleared before execution)
     * @param dataFile the judge's input data file to be validated
     * 
     * @return an InputValidationResult containing the results of the validator execution
     * 
     * @throws NullPointerException if any of the input parameters is null
     */
    public InputValidationResult runInputValidator(SerializedFile validatorProg, String validatorCommand,
            String executeDir, SerializedFile dataFile) {

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

        int exitCode = runner.runProgram(executionData, executeDir, cmdline, msTimeout, null, stdinFilePath, stdoutFilePath, stderrFilePath);

        boolean passed = exitCode == Constants.INPUT_VALIDATOR_SUCCESS_EXIT_CODE ? true : false;
        
        //TODO: deal with the issue that the SerializedFile constructor doesn't throw exceptions; it just "sets a message"
        // Need to query the SerializedFiles to make sure there are no "messages" or exceptions
        SerializedFile stdoutResults = new SerializedFile(stdoutFilePath);
        SerializedFile stderrResults = new SerializedFile(stderrFilePath);
        
        return new InputValidationResult(Utilities.basename(stdinFilename), passed, stdoutResults, stderrResults);

    }
    
    /**
     * Runs the specified validator using the specified command, in the specified execution directory, feeding each  
     * specified data files to the validator as input and returning an array of {@link InputValidationResult}s 
     * containing the results of the validator execution on each data set.
     * 
     * The elements of the returned array correspond in position to the data files in the specified data files array.
     * 
     * @param validator a SerializedFile containing the validator program
     * @param validatorCommand a String (possibly containing PC2 substitution keywords) to be used to invoke the validator
     * @param executeDir the directory in which the execution is to take place (this is created if necessary, and it is cleared before execution)
     * @param dataFiles an array containing judge's input data files to be validated
     * 
     * @return an array of InputValidationResults containing the results of the validator execution on each of the specified data files, 
     *          or a zero-length array if no data files were provided
     */
    public InputValidationResult [] runInputValidator(SerializedFile validator, String validatorCommand, 
                                                    String executeDir, SerializedFile [] dataFiles ) {
        
        if (dataFiles == null) {
            return new InputValidationResult [0];
        }
        
        InputValidationResult [] results = new InputValidationResult [dataFiles.length];
        
        for (int i=0; i<dataFiles.length; i++) {
            
            results[i] = runInputValidator(validator, validatorCommand, executeDir, dataFiles[i]);
        }
        
        return results;
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


}
