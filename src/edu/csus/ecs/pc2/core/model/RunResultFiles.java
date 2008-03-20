package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;

import edu.csus.ecs.pc2.core.execute.ExecutionData;

/**
 * Run results files/statistics.
 * 
 * This is a collection of files and statistics produced as output for a run,
 * judgement and validation.
 * <P>
 * Each RunResultsFile is associated with a Run and RunJudgement, because
 * each time a submitted run is executed a new judgement/set of statistics
 * can be produced.  
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
// $HeadURL$
public class RunResultFiles implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4739592801158369138L;

    private ElementId runId = null;

    private ElementId problemId = null;
    
    private ElementId judgementId = null;
        
    private ExecutionData executionData = null;

    public RunResultFiles(Run run, ElementId problemId, JudgementRecord judgementRecord, ExecutionData executionData) {
        super();
        this.runId = run.getElementId();
        this.problemId = problemId;
        this.judgementId = judgementRecord.getElementId();
        this.executionData = executionData;
        
    }

    public SerializedFile getCompilerStderrFile() {
        return executionData.getCompileStderr();
    }

    public void setCompilerStderrFile(SerializedFile compilerStderrFile) {
        executionData.setCompileStderr(compilerStderrFile);
    }

    public SerializedFile getCompilerStdoutFile() {
        return executionData.getCompileStdout();
    }

    public void setCompilerStdoutFile(SerializedFile compilerStdoutFile) {
        executionData.setCompileStderr(compilerStdoutFile);
    }

    public SerializedFile getExecuteStderrFile() {
        return executionData.getExecuteStderr();
    }

    public void setExecuteStderrFile(SerializedFile executeStderrFile) {
        executionData.setExecuteStderr(executeStderrFile);
    }

    public SerializedFile getExecuteStdoutFile() {
        return executionData.getExecuteProgramOutput();
    }

    public void setExecuteStdoutFile(SerializedFile executeStdoutFile) {
        executionData.setExecuteProgramOutput(executeStdoutFile);
    }

    public ElementId getProblemId() {
        return problemId;
    }

    public void setProblemId(ElementId problemId) {
        this.problemId = problemId;
    }

    public ElementId getRunId() {
        return runId;
    }

    public void setRunId(ElementId runId) {
        this.runId = runId;
    }

    public SerializedFile getValidatorStderrFile() {
        return executionData.getValidationStderr();
    }

    public void setValidatorStderrFile(SerializedFile validatorStderrFile) {
        executionData.setValidationStderr(validatorStderrFile);
    }

    public SerializedFile getValidatorStdoutFile() {
        return executionData.getValidationStdout();
    }

    public void setValidatorStdoutFile(SerializedFile validatorStdoutFile) {
        executionData.setValidationStdout(validatorStdoutFile);
    }
    
    public boolean failedInCompile(){
        return !executionData.isCompileSuccess();
    }
    
    public void setFailedInCompile(boolean failed){
        executionData.setCompileSuccess(!failed);
    }

    public boolean failedInExecute(){
        return !executionData.isExecuteSucess();
    }
    
    public void setFailedInExecute(boolean failed){
        executionData.setExecuteSucess(!failed);
    }

    public boolean failedInValidating(){
        return !executionData.isValidationSuccess();
    }
    
    public void setFailedInValidating(boolean failed){
        executionData.setValidationSuccess(!failed);
    }

    public long getCompileTimeMS() {
        return executionData.getCompileTimeMS();
    }

    public void setCompileTimeMS(long compileTimeMS) {
        executionData.setCompileTimeMS(compileTimeMS);
    }

    public long getExecuteTimeMS() {
        return executionData.getExecuteTimeMS();
    }

    public void setExecuteTimeMS(long executeTimeMS) {
        executionData.setExecuteTimeMS(executeTimeMS);
    }

    public long getValidateTimeMS() {
        return executionData.getvalidateTimeMS();
    }

    public void setValidateTimeMS(long validateTimeMS) {
        executionData.setvalidateTimeMS(validateTimeMS);
    }

    public ElementId getJudgementId() {
        return judgementId;
    }
    
    public long getCompileResultCode() {
        return executionData.getCompileResultCode();
    }

    public long getExecutionResultCode() {
        return executionData.getExecuteExitValue();
    }

    public long getValidationResultCode() {
        return executionData.getValidationReturnCode();
    }
}
