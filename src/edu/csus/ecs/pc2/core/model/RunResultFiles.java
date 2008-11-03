package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;

import edu.csus.ecs.pc2.core.execute.ExecutionData;

/**
 * Run results files/statistics.
 * 
 * This is a collection of files and statistics produced as output for a run, judgement and validation.
 * <P>
 * Each RunResultsFile is associated with a Run and RunJudgement, because each time a submitted run is executed a new judgement/set of statistics can be produced.
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
        if (judgementRecord != null) {
            this.judgementId = judgementRecord.getJudgementId();
        }
        this.executionData = executionData;

    }

    public SerializedFile getCompilerStderrFile() {
        if (executionData == null) {
            return null;
        } else {
            return executionData.getCompileStderr();
        }
    }

    public void setCompilerStderrFile(SerializedFile compilerStderrFile) {
        if (executionData != null) {
            executionData.setCompileStderr(compilerStderrFile);
        }
    }

    public SerializedFile getCompilerStdoutFile() {
        if (executionData == null) {
            return null;
        } else {
            return executionData.getCompileStdout();
        }
    }

    public void setCompilerStdoutFile(SerializedFile compilerStdoutFile) {
        if (executionData != null) {
            executionData.setCompileStderr(compilerStdoutFile);
        }
    }

    public SerializedFile getExecuteStderrFile() {
        if (executionData == null) {
            return null;
        } else {
            return executionData.getExecuteStderr();
        }
    }

    public void setExecuteStderrFile(SerializedFile executeStderrFile) {
        if (executionData != null) {
            executionData.setExecuteStderr(executeStderrFile);
        }
    }

    public SerializedFile getExecuteStdoutFile() {
        if (executionData == null) {
            return null;
        } else {
            return executionData.getExecuteProgramOutput();
        }
    }

    public void setExecuteStdoutFile(SerializedFile executeStdoutFile) {
        if (executionData != null) {
            executionData.setExecuteProgramOutput(executeStdoutFile);
        }
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
        if (executionData == null) {
            return null;
        } else {
            return executionData.getValidationStderr();
        }
    }

    public void setValidatorStderrFile(SerializedFile validatorStderrFile) {
        if (executionData != null) {
            executionData.setValidationStderr(validatorStderrFile);
        }
    }

    public SerializedFile getValidatorStdoutFile() {
        if (executionData == null) {
            return null;
        } else {
            return executionData.getValidationStdout();
        }
    }

    public void setValidatorStdoutFile(SerializedFile validatorStdoutFile) {
        if (executionData != null) {
            executionData.setValidationStdout(validatorStdoutFile);
        }
    }

    public boolean failedInCompile() {
        if (executionData == null) {
            return false;
        } else {
            return !executionData.isCompileSuccess();
        }
    }

    public void setFailedInCompile(boolean failed) {
        if (executionData == null) {
            executionData.setCompileSuccess(!failed);
        }
    }

    public boolean failedInExecute() {
        if (executionData == null) {
            return false;
        } else {
            return !executionData.isExecuteSucess();
        }
    }

    public void setFailedInExecute(boolean failed) {
        if (executionData == null) {
            executionData.setExecuteSucess(!failed);
        }
    }

    public boolean failedInValidating() {
        if (executionData == null) {
            return false;
        } else {
            return !executionData.isValidationSuccess();
        }
    }

    public void setFailedInValidating(boolean failed) {
        if (executionData == null) {
            executionData.setValidationSuccess(!failed);
        }
    }

    public long getCompileTimeMS() {
        if (executionData == null) {
            return 0;
        } else {
            return executionData.getCompileTimeMS();
        }
    }

    public void setCompileTimeMS(long compileTimeMS) {
        if (executionData != null) {
            executionData.setCompileTimeMS(compileTimeMS);
        }
    }

    public long getExecuteTimeMS() {
        if (executionData == null) {
            return 0;
        } else {
            return executionData.getExecuteTimeMS();
        }
    }

    public void setExecuteTimeMS(long executeTimeMS) {
        if (executionData != null) {
            executionData.setExecuteTimeMS(executeTimeMS);
        }
    }

    public long getValidateTimeMS() {
        if (executionData == null) {
            return 0;
        } else {
            return executionData.getvalidateTimeMS();
        }
    }

    public void setValidateTimeMS(long validateTimeMS) {
        if (executionData != null) {
            executionData.setvalidateTimeMS(validateTimeMS);
        }
    }

    public ElementId getJudgementId() {
        return judgementId;
    }

    public long getCompileResultCode() {
        if (executionData == null) {
            return 0;
        } else {
            return executionData.getCompileResultCode();
        }
    }

    public long getExecutionResultCode() {
        if (executionData == null) {
            return 0;
        } else {
            return executionData.getExecuteExitValue();
        }
    }

    public long getValidationResultCode() {
        if (executionData == null) {
            return 0;
        } else {
            return executionData.getValidationReturnCode();
        }
    }

    public String getValidationResults() {
        if (executionData == null) {
            return "";
        } else {
            return executionData.getValidationResults();
        }
    }
}
