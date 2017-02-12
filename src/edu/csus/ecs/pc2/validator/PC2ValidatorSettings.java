package edu.csus.ecs.pc2.validator;

import java.io.Serializable;

import edu.csus.ecs.pc2.core.Constants;

/**
 * This class holds the settings for an instance of the PC2 Validator
 * (formerly known also as the "Internal Validator").
 * 
 * @author John@pc2.ecs.csus.edu
 *
 */
public class PC2ValidatorSettings implements Cloneable, Serializable {

    private static final long serialVersionUID = 1L;
    
    private String validatorProgramName ;
    private String validatorCommandLine ;
    private int whichPC2Validator = 0;  //which pc2validator option?
    private boolean ignoreCaseOnValidation = false;    

    
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

    /**
     * @return an integer indicating which PC2 Validator Option to use
     */
    public int getWhichPC2Validator() {
        return whichPC2Validator;
    }

    /**
     * Set the integer indicating which PC2Validator option to use.
     * 
     * @param whichPC2Validator the whichPC2Validator to set
     */
    public void setWhichPC2Validator(int whichPC2Validator) {
        this.whichPC2Validator = whichPC2Validator;
    }

    /**
     * @return the ignoreCaseOnValidation flag
     */
    public boolean isIgnoreCaseOnValidation() {
        return ignoreCaseOnValidation;
    }

    /**
     * Sets whether or not the PC2Validator should ignore case differences in answers.
     * 
     * @param ignoreCaseOnValidation the ignoreCaseOnValidation to set
     */
    public void setIgnoreCaseOnValidation(boolean ignoreCaseOnValidation) {
        this.ignoreCaseOnValidation = ignoreCaseOnValidation;
    }

    /** 
     * Returns a clone (copy) of this PC2ValidatorSettings object.
     */
    @Override
    public PC2ValidatorSettings clone()  {
        PC2ValidatorSettings settings = new PC2ValidatorSettings();
        
        settings.validatorProgramName = this.validatorProgramName;
        settings.validatorCommandLine = this.validatorCommandLine;
        settings.whichPC2Validator = this.whichPC2Validator;
        settings.ignoreCaseOnValidation = this.ignoreCaseOnValidation;
        
        return settings;
    }

    /**
     * Returns a String representation of this PC2ValidatorSettings object.
     */
    @Override
    public String toString() {
        String retStr = "PC2ValidatorSettings[" ;
        
        retStr += "validatorProgramName=" + validatorProgramName;
        retStr += "; validatorCommandLine=\"" + validatorCommandLine + "\""; 
        retStr += "; whichPC2Validator=" + whichPC2Validator; 
        retStr += "; ignoreCaseOnValidation=" + ignoreCaseOnValidation;    

        retStr += "]";
        return retStr;
    }

    /**
     * Returns true if the specified Object is the same as this PC2ValidatorSettings object.
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof PC2ValidatorSettings)) {
            return false;
        }

        PC2ValidatorSettings other = (PC2ValidatorSettings) obj;

        if (!(this.validatorProgramName.equals(other.validatorProgramName))) {
            return false;
        }

        if (!(this.validatorCommandLine.equals(other.validatorCommandLine))) {
            return false;
        }

        if (!(this.whichPC2Validator == other.whichPC2Validator)) {
            return false;
        }

        if (!(this.ignoreCaseOnValidation == other.ignoreCaseOnValidation)) {
            return false;
        }

        return true;

    }

}
