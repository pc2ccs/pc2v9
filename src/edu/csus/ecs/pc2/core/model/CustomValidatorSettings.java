/**
 * This class encapsulates settings for configuring a "custom validator".
 * Settings include the validator program execution command line (that is, the command used to invoke
 * the custom validator) and the command line options to be passed to the custom validator. 
 */
package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;

/**
 * @author pc2@ecs.csus.edu
 *
 */
public class CustomValidatorSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    private String customValidatorInvocationCommand;
    
    private String customValidatorCommandOptions;
    
    /**
     * Constructs a CustomValidatorSettings object with default values of empty strings
     * for both the invocation command and the command line options.
     */
    public CustomValidatorSettings() {
        this.customValidatorInvocationCommand = "";
        this.customValidatorCommandOptions = "";
    }



    /**
     * Returns the current custom validator invocation command.
     * @return the invocation command for the custom validator
     */
    public String getCustomValidatorInvocationCommand() {
        return customValidatorInvocationCommand;
    }

    /**
     * Sets the invocation command for the custom validator to the specified value.
     * Note that the invocation command does not include command line options; those
     * are specified via the separate "customValidatorCommandOptions" field.
     * @param command -- the command used to invoke the custom validator
     */
    public void setCustomValidatorInvocationCommand(String command) {
        this.customValidatorInvocationCommand = command;
    }


    /**
     * Returns the current custom validator command options.
     * @return the command options to be passed to the custom validator
     */
    public String getCustomValidatorCommandOptions() {
        return customValidatorCommandOptions;
    }

    /**
     * Sets the command options for the custom validator to the specified value.
     * @param optionString -- a String defining the options to be passed to the custom validator
     */
    public void setCustomValidatorCommandOptions(String optionString) {
        this.customValidatorInvocationCommand = optionString;
    }

    /**
     * Returns true if the custom validator settings in this object match those in
     * the specified "other" object; false otherwise.
     * @param other -- the object against which to compare this object's settings
     * @return true if this object matches the other object
     */
    public boolean isSameAs(CustomValidatorSettings other) {
        //first, check the invocation command lines
        //check whether one is null while the other is not, using the Java XOR ("^") operator
        if (this.getCustomValidatorInvocationCommand()==null ^ other.getCustomValidatorInvocationCommand()==null) {
            return false;
        }
        //check whether, if both are non-null, both are the same
        if (this.getCustomValidatorInvocationCommand()!=null && other.getCustomValidatorInvocationCommand()!=null) {
            if (!(this.getCustomValidatorInvocationCommand().equals(other.getCustomValidatorInvocationCommand()))) {
                return false;
            }
        }
        
        //next, check the options string
        //check whether one is null while the other is not, using the Java XOR ("^") operator
        if (this.getCustomValidatorCommandOptions()==null ^ other.getCustomValidatorCommandOptions()==null) {
            return false;
        }
        //check whether, if both are non-null, both are the same
        if (this.getCustomValidatorCommandOptions()!=null && other.getCustomValidatorCommandOptions()!=null) {
            if (!(this.getCustomValidatorCommandOptions().equals(other.getCustomValidatorCommandOptions()))) {
                return false;
            }
        }

        return true;
        
    }
    
    /**
     * Returns a CustomValidatorSettings object which is a copy of this object.
     */
    public CustomValidatorSettings clone() {
        CustomValidatorSettings clone = new CustomValidatorSettings();
        //the following should work because Strings are immutable...
        clone.setCustomValidatorInvocationCommand(this.getCustomValidatorInvocationCommand());
        clone.setCustomValidatorCommandOptions(this.getCustomValidatorCommandOptions());
        return clone;
    }

}
