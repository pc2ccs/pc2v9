package edu.csus.ecs.pc2.core.execute;

import java.io.Serializable;

import edu.csus.ecs.pc2.core.model.SerializedFile;

/**
 * Execution Data.
 * 
 * Contains data for compilation, execution and validation.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
// TODO handle multiple data sets for validator and execution.
public class ExecutionData implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3095803815304273632L;

    // Compile call results
    private SerializedFile compileStdout;

    private SerializedFile compileStderr;

    private String compileExeFileName;

    private boolean compileSuccess;

    private long compileResultCode;

    // Execute call results
    private SerializedFile executeStderr;

    private SerializedFile executeProgramOutput; // this is STDOUT of the execution

    private int executeExitValue = 0;
    
    private boolean executeSucess;
    
    private SerializedFile validationStdout;

    private SerializedFile validationStderr; // this is STDOUT of the validation

    private long validationReturnCode;

    private boolean validationSuccess;

    private String validationResults;
    
    private long compileTimeMS = 0;
    
    private long executeTimeMS = 0;
    
    private long validateTimeMS = 0;
    
    private Exception executionException = null;
    
    private boolean runTimeLimitExceeded = false;

    private boolean failedToCompile = false;

    private String additionalInformation = "";
    
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
     * @param executeExitValue
     *            The executeExitValue to set.
     */
    public void setExecuteExitValue(int executeExitValue) {
        this.executeExitValue = executeExitValue;
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
     * Did the validator program run without an error ?.
     * 
     * This does NOT indicate that the team's run was a Yes/accepted.  This
     * only returns whether the validator program ran without any OS or other sort of error.
     * 
     * @return Returns whether the validator program executed successfully.
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

    public void setCompileTimeMS(long compileTime) {
        compileTimeMS = compileTime; 
    }

    public long getCompileTimeMS() {
        return compileTimeMS;
    }
    
    public void setExecuteTimeMS(long inExecuteTime){
        executeTimeMS = inExecuteTime;
    }
    
    public long getExecuteTimeMS(){
        return executeTimeMS;
    }

    public void setvalidateTimeMS(long validateTime){
        validateTimeMS = validateTime;
    }
    
    public long getvalidateTimeMS(){
        return validateTimeMS;
    }
    
    public boolean isExecuteSucess() {
        return executeSucess;
    }
    
    public void setExecuteSucess(boolean executeSucess) {
        this.executeSucess = executeSucess;
    }

    /**
     * Did team's submission fail to compile?.
     * @return
     * @deprecated use {@link #isCompileSuccess()}.
     */
    public boolean isFailedToCompile() {
        return failedToCompile;
    }

    /**
     * 
     * @param failedToCompile
     * @deprecated use {@link #setCompileSuccess(boolean)}
     */
    public void setFailedToCompile(boolean failedToCompile) {
        this.failedToCompile = failedToCompile;
    }

    /**
     * During execution was there a time limit exceeded?.
     * @return
     */
    public boolean isRunTimeLimitExceeded() {
        return runTimeLimitExceeded;
    }

    public void setRunTimeLimitExceeded(boolean runTimeLimitExceeded) {
        this.runTimeLimitExceeded = runTimeLimitExceeded;
    }
    
    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    /**
     * Additional information for validation.
     * @return
     */
    public String getAdditionalInformation() {
        return additionalInformation;
    }
    
    @Override
    public String toString() {
        String retStr = "[";
        retStr += "compileStdout=" + compileStdout + ",";
        retStr += "compileStderr=" + compileStderr + ",";
        retStr += "compileExeFileName=" + compileExeFileName + ",";
        retStr += "compileSuccess=" + compileSuccess + ",";
        retStr += "compileResultCode=" + compileResultCode + ",";
        retStr += "executeStderr=" + executeStderr + ",";
        retStr += "executeProgramOutput=" + executeProgramOutput + ",";
        retStr += "executeExitValue=" + executeExitValue + ",";
        retStr += "executeSucess=" + executeSucess + ",";
        retStr += "validationStdout=" + validationStdout + ",";
        retStr += "validationStderr=" + validationStderr + ",";
        retStr += "validationReturnCode" + validationReturnCode + ",";
        retStr += "validationSuccess=" + validationSuccess + ",";
        retStr += "validationResults=" + validationResults + ",";
        retStr += "compileTimeMS=" + compileTimeMS + ",";
        retStr += "executeTimeMS=" + executeTimeMS + ",";
        retStr += "validateTimeMS=" + validateTimeMS + ",";
        retStr += "executionException=" + executionException + ",";
        retStr += "runTimeLimitExceeded=" + runTimeLimitExceeded + ",";
        retStr += "failedToCompile=" + failedToCompile + ",";
        retStr += "additionalInformation=" + additionalInformation;
        retStr += "]";
        
        return retStr;
    }
}
