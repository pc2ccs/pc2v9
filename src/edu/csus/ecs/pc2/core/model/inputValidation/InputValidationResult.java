package edu.csus.ecs.pc2.core.model.inputValidation;

import java.io.Serializable;

import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.SerializedFile;

/**
 * This class holds the result of performing Input Validation on a single Judge's Input Data file.
 * 
 * The contents of the class include the full path/name of the file which was validated, a boolean indicating
 * whether the validation passed or failed, and {@link SerializedFile}s holding the standard output and standard error results
 * when the Input Validator was run against the specified file.
 * 
 * @author John
 *
 */
public class InputValidationResult implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Problem problem ;
    private String fullPathFilename ;
    private boolean passed ;
    private SerializedFile validatorStdOut ;
    private SerializedFile validatorStdErr ;
    
    /**
     * Constructs an InputValidationResult containing the specified data.
     * The validatorStdOut and validatorStdErr strings are converted to {@link SerializedFile}s before being saved.
     * 
     * @param problem the {@link Problem} for which this result applies
     * @param dataFilePathName the full path name of the data file on which the Input Validator was run
     * @param passed a boolean indicating whether the Input Validator returned success or failure when checking the input data file
     * @param validatorStdOutFilename a String containing the standard output of the Input Validator (this is stored as a SerializedFile)
     * @param validatorStdErrFilename a String containing the standard error output of the Input Validator (this is stored as a SerializedFile)
     */
    public InputValidationResult(Problem problem, String dataFilePathName, boolean passed, String validatorStdOutFilename, String validatorStdErrFilename)  {
        this.problem = problem;
        this.fullPathFilename = dataFilePathName;
        this.passed = passed;
        this.validatorStdOut = new SerializedFile(validatorStdOutFilename);
        this.validatorStdErr = new SerializedFile(validatorStdErrFilename);
        //TODO: deal better with the fact that the SerializedFile constructor might fail due to the files not being found,
        //  but SerializedFile fails to throw exceptions -- you have to call its getErrorMessage() and getException() methods!
        if (this.validatorStdErr.getErrorMessage() != null || this.validatorStdErr.getException() != null 
                || this.validatorStdOut.getErrorMessage() != null || this.validatorStdOut.getException() != null ) {
            throw new RuntimeException("InputValidationResult: specified file not found");
        }
    }

    /**
     * Constructs an InputValidationResult containing the specified data.
     * 
     * @param problem the {@link Problem} for which this result applies
     * @param dataFilePathName the full path name of the data file on which the Input Validator was run
     * @param passed a boolean indicating whether the Input Validator returned success or failure when checking the input data file
     * @param validatorStdOutFile a {@link SerializedFile} containing the standard output of the Input Validator
     * @param validatorStdErrFile a {@link SerializedFile} containing the standard error output of the Input Validator
     */
    public InputValidationResult(Problem problem, String dataFilePathName, boolean passed, SerializedFile validatorStdOutFile, SerializedFile validatorStdErrFile) {
        this.problem = problem;
        this.passed = passed;
        this.fullPathFilename = dataFilePathName;
        this.validatorStdOut = validatorStdOutFile;
        this.validatorStdErr = validatorStdErrFile;
    }
    
    /**
     * Returns the {@link Problem} associated with this result.
     */
    public Problem getProblem() {
        return problem;
    }
    
    /**
     * Allows setting the {@link Problem} associated with this result.
     */
    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    /**
     * Returns the name of the file on which the Input Validator was run.
     * 
     * @return the fullPathFilename for the validated file
     */
    public String getFullPathFilename() {
        return fullPathFilename;
    }

    /**
     * Sets the name of the file on which the Input Validator was run.  The specified file name
     * should be the full (absolute) path to the file.
     * 
     * @param fullPathFilename the fullPathFilename to set
     */
    public void setFullPathFilename(String fullPathFilename) {
        this.fullPathFilename = fullPathFilename;
    }

    /**
     * Returns an indication of whether or not the file represented in this object passed Input Validation.
     * 
     * @return the flag indicating whether the file passed Input Validation
     */
    public boolean isPassed() {
        return passed;
    }

    /**
     * Sets the value of the flag indicating whether or not the file represented in this object passed Input Validation.
     * 
     * @param passed the value to which the flag should be set
     */
    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    /**
     * Returns a {@link SerializedFile} containing the output produced on stdout by the Input Validator when run against the data file specified in this object.
     * 
     * @return a SerializedFile containing the standard output produced by the Input Validator when run against the specified file.
     */
    public SerializedFile getValidatorStdOut() {
        return validatorStdOut;
    }

    /**
     * Saves the {@link SerializedFile} containing the standard output produced by the Input Validator when run against the specified data file.
     * 
     * @param validatorStdOut the validatorStdOut to save
     */
    public void setValidatorStdOut(SerializedFile validatorStdOut) {
        this.validatorStdOut = validatorStdOut;
    }

    /**
     * Returns a {@link SerializedFile} containing the output produced on stderr by the Input Validator when run against the data file specified in this object.
     * 
     * @return a SerializedFile containing the validatorStdErr result
     */
    public SerializedFile getValidatorStdErr() {
        return validatorStdErr;
    }

    /**
     * Saves a {@link SerializedFile} containing the standard error channel output produced by the Input Validator 
     * when run against the specified data file.
     * 
     * @param validatorStdErr the validatorStdErr to save
     */
    public void setValidatorStdErr(SerializedFile validatorStdErr) {
        this.validatorStdErr = validatorStdErr;
    }
    
    @Override
    public String toString() {
        String retStr = "InputValidatorResult[";
        
        retStr += "problem=" + this.problem;
        retStr += " dataFile=" + this.fullPathFilename;
        retStr += " passed=" + this.passed;
        retStr += " validatorStdOutFile=" + this.validatorStdOut.getName();
        retStr += " validatorStdErrFile=" + this.validatorStdErr.getName();

        retStr += "]";
        
        return retStr;
    }

}
