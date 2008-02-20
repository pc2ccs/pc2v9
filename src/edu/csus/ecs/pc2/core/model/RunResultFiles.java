package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;

/**
 * Run results files.
 * 
 * This is a collection of files produced as output for a run.
 * 
 * @author pc2@ecs.csus.edu
 */
// $HeadURL$
public class RunResultFiles implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4739592801158369138L;

    /**
     * 
     */
    public static final String SVN_ID = "$Id$";

    private ElementId runId = null;

    private ElementId problemId = null;
    
    private long compileTimeMS = 0;
    
    private long executeTimeMS = 0;
    
    private long validateTimeMS = 0;
    
    private SerializedFile compilerStdoutFile = null;

    private SerializedFile compilerStderrFile = null;

    private SerializedFile executeStdoutFile = null;

    private SerializedFile executeStderrFile = null;

    private SerializedFile validatorStdoutFile = null;

    private SerializedFile validatorStderrFile = null;

    private boolean failedCompile = true;

    private boolean failedExecute = true;

    private boolean failedValidating = true;

    public RunResultFiles(ElementId runId, ElementId problemId) {
        super();
        // TODO Auto-generated constructor stub
        this.runId = runId;
        this.problemId = problemId;
    }

    public SerializedFile getCompilerStderrFile() {
        return compilerStderrFile;
    }

    public void setCompilerStderrFile(SerializedFile compilerStderrFile) {
        this.compilerStderrFile = compilerStderrFile;
    }

    public SerializedFile getCompilerStdoutFile() {
        return compilerStdoutFile;
    }

    public void setCompilerStdoutFile(SerializedFile compilerStdoutFile) {
        this.compilerStdoutFile = compilerStdoutFile;
    }

    public SerializedFile getExecuteStderrFile() {
        return executeStderrFile;
    }

    public void setExecuteStderrFile(SerializedFile executeStderrFile) {
        this.executeStderrFile = executeStderrFile;
    }

    public SerializedFile getExecuteStdoutFile() {
        return executeStdoutFile;
    }

    public void setExecuteStdoutFile(SerializedFile executeStdoutFile) {
        this.executeStdoutFile = executeStdoutFile;
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
        return validatorStderrFile;
    }

    public void setValidatorStderrFile(SerializedFile validatorStderrFile) {
        this.validatorStderrFile = validatorStderrFile;
    }

    public SerializedFile getValidatorStdoutFile() {
        return validatorStdoutFile;
    }

    public void setValidatorStdoutFile(SerializedFile validatorStdoutFile) {
        this.validatorStdoutFile = validatorStdoutFile;
    }
    
    public boolean failedInComile(){
        return failedCompile;
    }
    
    public void setFailedInCompile(boolean failed){
        failedCompile = failed;
    }

    public boolean failedInExecute(){
        return failedExecute;
    }
    
    public void setFailedInExecute(boolean failed){
        failedExecute = failed;
    }

    public boolean failedInValidating(){
        return failedValidating;
    }
    
    public void setFailedInValidating(boolean failed){
        failedValidating = failed;
    }

    public long getCompileTimeMS() {
        return compileTimeMS;
    }

    public void setCompileTimeMS(long compileTimeMS) {
        this.compileTimeMS = compileTimeMS;
    }

    public long getExecuteTimeMS() {
        return executeTimeMS;
    }

    public void setExecuteTimeMS(long executeTimeMS) {
        this.executeTimeMS = executeTimeMS;
    }

    public long getValidateTimeMS() {
        return validateTimeMS;
    }

    public void setValidateTimeMS(long validateTimeMS) {
        this.validateTimeMS = validateTimeMS;
    }

}
