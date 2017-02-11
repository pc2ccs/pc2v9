package edu.csus.ecs.pc2.validator;

import edu.csus.ecs.pc2.core.Constants;

/**
 * This class holds the setttings for an instance of the PC2 Validator
 * (formerly known also as the "Internal Validator").
 * 
 * @author John@pc2.ecs.csus.edu
 *
 */
public class PC2ValidatorSettings {

    private String validatorProgramName ;
    private String validatorCommandLine ;
    
    public PC2ValidatorSettings() {
        
        this.validatorProgramName = Constants.PC2_VALIDATOR_NAME;
        this.validatorCommandLine = Constants.DEFAULT_PC2_VALIDATOR_COMMAND;
    }

    /**
     * @return the validatorProgramName
     */
    public String getValidatorProgramName() {
        return validatorProgramName;
    }

    /**
     * Sets the Validator Program Name in this PC2ValidatorSettings object to the specified String.
     * 
     * @param validatorProgramName the name of the Validator Program
     */
    public void setValidatorProgramName(String validatorProgramName) {
        this.validatorProgramName = validatorProgramName;
    }

    /**
     * @return the validatorCommandLine
     */
    public String getValidatorCommandLine() {
        return validatorCommandLine;
    }

    /**
     * Sets the Validator Command Line in this PC2ValidatorSettings object to the specified String.
     * 
     * @param validatorCommandLine the value to which the Validator Command Line should be set
     */
    public void setValidatorCommandLine(String validatorCommandLine) {
        this.validatorCommandLine = validatorCommandLine;
    }
    
}
