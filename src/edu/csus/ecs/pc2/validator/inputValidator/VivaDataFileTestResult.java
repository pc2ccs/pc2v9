package edu.csus.ecs.pc2.validator.inputValidator;

import edu.csus.ecs.pc2.core.model.SerializedFile;

/**
 * This class encapsulates the result of using VIVA to test a judge's data file against a specific VIVA pattern.
 * 
 * @author John Clevenger, PC2 Development Team (pc2@ecs.csus.edu)
 *
 */
public class VivaDataFileTestResult {
    
    private SerializedFile vivaOutput;
    private boolean passFail;
    private String vivaPattern;
    private SerializedFile dataFile;

    public VivaDataFileTestResult(SerializedFile vivaOutput, boolean passFail, String vivaPattern, SerializedFile dataFile) {
        this.vivaOutput = vivaOutput;
        this.passFail = passFail;
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
     * @return the passFail flag associated with this VivaDataFileTestResult.
     */
    public boolean passFail() {
        return passFail;
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

}
