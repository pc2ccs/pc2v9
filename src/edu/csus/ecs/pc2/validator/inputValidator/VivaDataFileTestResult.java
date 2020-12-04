package edu.csus.ecs.pc2.validator.inputValidator;

import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.SerializedFile;

/**
 * This class encapsulates the result of using VIVA to test a judge's data file against a specific VIVA pattern.
 * 
 * @author John Clevenger, PC2 Development Team (pc2@ecs.csus.edu)
 *
 */
public class VivaDataFileTestResult {
    
    private SerializedFile vivaOutput;
    private boolean passed;
    private Problem.InputValidationStatus status;
    private String vivaPattern;
    private SerializedFile dataFile;

    public VivaDataFileTestResult(SerializedFile vivaOutput, boolean passed, Problem.InputValidationStatus status, 
                                        String vivaPattern, SerializedFile dataFile) {
        this.vivaOutput = vivaOutput;
        this.passed = passed;
        this.status = status;
        this.vivaPattern = vivaPattern;
        this.dataFile = dataFile;
    }

    /**
     * @return the vivaOutput which was produced during the generation of this VivaDataFileTestResult.
     */
    public SerializedFile getVivaOutput() {
        return vivaOutput;
    }

    /**
     * @return the passed flag associated with this VivaDataFileTestResult.
     */
    public boolean passed() {
        return passed;
    }

    /**
     * @return the vivaPattern associated with the generation of this VivaDataFileTestResult.
     */
    public String getVivaPattern() {
        return vivaPattern;
    }

    /**
     * @return the Data File associated with the generation of this VivaDataFileTestResult.
     */
    public SerializedFile getDataFile() {
        return dataFile;
    }

    /**
     * @return the {@link Problem.InputValidationStatus} associated with this VivaDataFileTestResult. 
     */
    public Problem.InputValidationStatus getStatus() {
        return status;
    }
    
    public String toString() {
        
        String retStr = "";
        
        retStr += "VivaDataFileTestResult[";
        retStr += "pattern=" + getVivaPattern();
        retStr += ", datafile=" + getDataFile().getName();
        retStr += ", status=" + getStatus();
        retStr += ", passed=" + passed();
        
        String vivaOutput = new String (getVivaOutput().getBuffer());
        if (vivaOutput==null || vivaOutput.trim().equals("")) {
            vivaOutput = "<empty>";
        }
        retStr += ", vivaOutput=" + vivaOutput;
        retStr += "]";
        
        return retStr;
    }

}
