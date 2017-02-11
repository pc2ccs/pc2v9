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
        
        this.validatorProgramName = "";
        this.validatorCommandLine = Constants.DEFAULT_PC2_VALIDATOR_COMMAND;
    }
}
