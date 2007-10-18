package edu.csus.ecs.pc2.core.execute;

import edu.csus.ecs.pc2.core.model.SerializedFile;

/**
 * Execution Data.
 * 
 * Contains data for compilation, execution and validation.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
// TODO handle multiple data sets for validator and execution.
public class ExecutionData {

    public static final String SVN_ID = "$Id$";

    // Compile call results
    private SerializedFile compileStdout;

    private SerializedFile compileStderr;

    private String compileExeFileName;

    private long compileCompilerReturnCode;

    private boolean compileSuccess;

    private long compileResultCode;

    // Execute call results
    private SerializedFile executeStderr;

    private SerializedFile executeProgramOutput; // this is STDOUT of the execution

    private int executeExitValue = 0;

    private long executeTime = -1;

    private SerializedFile validationStdout;

    private SerializedFile validationStderr; // this is STDOUT of the validation

    private long validationReturnCode;

    private boolean validationSuccess;

    private String validationResults;
    
    private Exception executionException = null;

    /**
     * @return Returns the validationReturnCode.
     */
    public long getValidationReturnCode() {
        return validationReturnCode;
    }

    /**
     * @param validationReturnCode
     *            The validationReturnCode to set.
     */
    public void setValidationReturnCode(long validationReturnCode) {
        this.validationReturnCode = validationReturnCode;
    }

    /**
     * @return Returns the validationResults.
     */
    public String getValidationResults() {
        return validationResults;
    }

    /**
     * @param validationResults
     *            The validationResults to set.
     */
    public void setValidationResults(String validationResults) {
        this.validationResults = validationResults;
    }

    /**
     * @return Returns the compileCompilerReturnCode.
     */
    public long getCompileCompilerReturnCode() {
        return compileCompilerReturnCode;
    }

    /**
     * @return Returns the compileExeFileName.
     */
    public String getCompileExeFileName() {
        return compileExeFileName;
    }

    /**
     * @return Returns the compileResultCode.
     */
    public long getCompileResultCode() {
        return compileResultCode;
    }

    /**
     * @return Returns the compileStderr.
     */
    public SerializedFile getCompileStderr() {
        return compileStderr;
    }

    /**
     * @return Returns the compileStdout.
     */
    public SerializedFile getCompileStdout() {
        return compileStdout;
    }

    /**
     * @return Returns the compileSuccess.
     */
    public boolean isCompileSuccess() {
        return compileSuccess;
    }

    /**
     * @param compileCompilerReturnCode
     *            The compileCompilerReturnCode to set.
     */
    public void setCompileCompilerReturnCode(long compileCompilerReturnCode) {
        this.compileCompilerReturnCode = compileCompilerReturnCode;
    }

    /**
     * @param compileExeFileName
     *            The compileExeFileName to set.
     */
    public void setCompileExeFileName(String compileExeFileName) {
        this.compileExeFileName = compileExeFileName;
    }

    /**
     * @param compileResultCode
     *            The compileResultCode to set.
     */
    public void setCompileResultCode(long compileResultCode) {
        this.compileResultCode = compileResultCode;
    }

    /**
     * @param compileStderr
     *            The compileStderr to set.
     */
    public void setCompileStderr(SerializedFile compileStderr) {
        this.compileStderr = compileStderr;
    }

    /**
     * @param compileStdout
     *            The compileStdout to set.
     */
    public void setCompileStdout(SerializedFile compileStdout) {
        this.compileStdout = compileStdout;
    }

    /**
     * @param compileSuccess
     *            The compileSuccess to set.
     */
    public void setCompileSuccess(boolean compileSuccess) {
        this.compileSuccess = compileSuccess;
    }

    /**
     * @return Returns the executeProgramOutput.
     */
    public SerializedFile getExecuteProgramOutput() {
        return executeProgramOutput;
    }

    /**
     * @return Returns the executeStderr.
     */
    public SerializedFile getExecuteStderr() {
        return executeStderr;
    }

    /**
     * @param executeProgramOutput
     *            The executeProgramOutput to set.
     */
    public void setExecuteProgramOutput(SerializedFile executeProgramOutput) {
        this.executeProgramOutput = executeProgramOutput;
    }

    /**
     * @param executeStderr
     *            The executeStderr to set.
     */
    public void setExecuteStderr(SerializedFile executeStderr) {
        this.executeStderr = executeStderr;
    }

    /**
     * @return Returns the executeExitValue.
     */
    public int getExecuteExitValue() {
        return executeExitValue;
    }

    /**
     * @return Returns the executeTime.
     */
    public long getExecuteTime() {
        return executeTime;
    }

    /**
     * @param executeExitValue
     *            The executeExitValue to set.
     */
    public void setExecuteExitValue(int executeExitValue) {
        this.executeExitValue = executeExitValue;
    }

    /**
     * @param executeTime
     *            The executeTime to set.
     */
    public void setExecuteTime(long executeTime) {
        this.executeTime = executeTime;
    }

    /**
     * @return Returns the validationStderr.
     */
    public SerializedFile getValidationStderr() {
        return validationStderr;
    }

    /**
     * @return Returns the validationStdout.
     */
    public SerializedFile getValidationStdout() {
        return validationStdout;
    }

    /**
     * @return Returns the validationSuccess.
     */
    public boolean isValidationSuccess() {
        return validationSuccess;
    }

    /**
     * @param validationStderr
     *            The validationStderr to set.
     */
    public void setValidationStderr(SerializedFile validationStderr) {
        this.validationStderr = validationStderr;
    }

    /**
     * @param validationStdout
     *            The validationStdout to set.
     */
    public void setValidationStdout(SerializedFile validationStdout) {
        this.validationStdout = validationStdout;
    }

    /**
     * @param validationSuccess
     *            The validationSuccess to set.
     */
    public void setValidationSuccess(boolean validationSuccess) {
        this.validationSuccess = validationSuccess;
    }

    /**
     * Get Exception that occurred during execution
     * @return the Exception
     */
    public Exception getExecutionException() {
        return executionException;
    }

    public void setExecutionException(Exception executionException) {
        this.executionException = executionException;
    }

}
