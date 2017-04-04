package edu.csus.ecs.pc2.ui;

/**
 * This class holds the result of performing Input Validation on a single Judge's Input Data file.
 * 
 * The contents of the class include the full path/name of the file which was validated, a boolean indicating
 * whether the validation passed or failed, and strings holding the standard output and standard error results
 * when the Input Validator was run against the specified file.
 * 
 * @author John
 *
 */
public class InputValidationResult {
    
    private String fullPathFilename ;
    private boolean passed ;
    private String validatorStdOut ;
    private String validatorStdErr ;
    
    public InputValidationResult(String filename, boolean passed, String validatorStdOut, String validatorStdErr) {
        this.fullPathFilename = filename;
        this.passed = passed;
        this.validatorStdOut = validatorStdOut;
        this.validatorStdErr = validatorStdErr;
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
     * Returns the output produced on stdout by the Input Validator when run against the file specified in this object.
     * 
     * @return a String giving the standard output produced by the Input Validator when run against the specified file.
     */
    public String getValidatorStdOut() {
        return validatorStdOut;
    }

    /**
     * Sets the stored value of String giving the standard output produced by the Input Validator when run against the specified file.
     * 
     * @param validatorStdOut the validatorStdOut to set
     */
    public void setValidatorStdOut(String validatorStdOut) {
        this.validatorStdOut = validatorStdOut;
    }

    /**
     * Returns the output produced on stderr by the Input Validator when run against the file specified in this object.
     * 
     * @return the validatorStdErr result
     */
    public String getValidatorStdErr() {
        return validatorStdErr;
    }

    /**
     * Sets the stored value of String giving the standard error channel output produced by the Input Validator 
     * when run against the specified file.
     * 
     * @param validatorStdErr the validatorStdErr to set
     */
    public void setValidatorStdErr(String validatorStdErr) {
        this.validatorStdErr = validatorStdErr;
    }

}
