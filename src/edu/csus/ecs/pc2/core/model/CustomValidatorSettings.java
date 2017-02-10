/**
 * This class encapsulates settings for configuring a "custom validator".
 * Settings include the validator program name (that is, the program which acts as
 * the custom validator) and the validator command line (that is, the command line
 * used to invoke the validator, including whatever options need to be passed to the 
 * validator program). 
 */
package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;

/**
 * @author pc2@ecs.csus.edu
 *
 */
public class CustomValidatorSettings implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    // the name of the validator program
    private String customValidatorProgramName;
    
    //the command which is used to invoke the validator (typically includes the validator program name)
    private String customValidatorCommandLine;
    
    // flags indicating which validator interface to use
    private boolean usePC2ValidatorInterface ;
    private boolean useCLICSValidatorInterface ;
    
    /**
     * Constructs a CustomValidatorSettings object with default values of empty strings
     * for both the Validator executable program name and the Validator invocation command line,
     * and with a default setting of "usePC2ValidatorInterface".
     */
    public CustomValidatorSettings() {
        this.customValidatorProgramName = "";
        this.customValidatorCommandLine = "";
        this.usePC2ValidatorInterface = true ;
        this.useCLICSValidatorInterface = false;
    }



    /**
     * Returns the name of the custom validator program.
     * @return a String containing the name of the custom validator program
     */
    public String getCustomValidatorProgramName() {
        return customValidatorProgramName;
    }

    /**
     * Sets the name for the custom validator to the specified value.
     * Note that the validator name does not include command line options; those
     * are specified via the separate "customValidatorCommand" field.
     * 
     * @param progName -- the custom validator program name
     */
    public void setCustomValidatorProgramName(String progName) {
        this.customValidatorProgramName = progName;
    }


    /**
     * Returns the current Custom Validator command line; that is, the command used to invoke the Custom Validator.
     * This typically includes the name of the validator program as the first element, followed by any arguments
     * to be passed to the custom validator.
     * 
     * @return the command line used to invoke the custom validator
     */
    public String getCustomValidatorCommandLine() {
        return customValidatorCommandLine;
    }

    /**
     * Sets the command line to be used to invoke the Custom Validator.
     * The command typically includes the name of the custom validator program as the first element, followed by
     * any arguments to be passed to the custom validator.
     * 
     * @param commandString -- a String defining the command line used to invoke the Custom Validator
     */
    public void setCustomValidatorCommandLine(String commandString) {
        this.customValidatorCommandLine = commandString;
    }

    /**
     * Returns true if the custom validator settings in this object match those in
     * the specified "other" object; false otherwise.
     * @param other -- the object against which to compare this object's settings
     * @return true if this object matches the other object
     */
    @Override
    public boolean equals(Object obj) {
        
        //are they the exact same object?
        if (this==obj) {
            return true;
        }
        
        if (!(obj instanceof CustomValidatorSettings)) {
            return false;
        }
        
        CustomValidatorSettings other = (CustomValidatorSettings) obj;
        
        //first, check the validator program name
        //check whether one is null while the other is not, using the Java XOR ("^") operator
        if (this.getCustomValidatorProgramName()==null ^ other.getCustomValidatorProgramName()==null) {
            return false;
        }
        //check whether, if both are non-null, both are the same
        if (this.getCustomValidatorProgramName()!=null && other.getCustomValidatorProgramName()!=null) {
            if (!(this.getCustomValidatorProgramName().equals(other.getCustomValidatorProgramName()))) {
                return false;
            }
        }
        
        //next, check the validator invocation command string
        //check whether one is null while the other is not, using the Java XOR ("^") operator
        if (this.getCustomValidatorCommandLine()==null ^ other.getCustomValidatorCommandLine()==null) {
            return false;
        }
        //check whether, if both are non-null, both are the same
        if (this.getCustomValidatorCommandLine()!=null && other.getCustomValidatorCommandLine()!=null) {
            if (!(this.getCustomValidatorCommandLine().equals(other.getCustomValidatorCommandLine()))) {
                return false;
            }
        }
        
        //check whether the flags specifying which validator interface to use are identical
        if (this.isUsePC2ValidatorInterface() != other.isUsePC2ValidatorInterface()) {
            return false;
        }
        if (this.isUseCLICSValidatorInterface() != other.isUseCLICSValidatorInterface()) {
            return false;
        }

        return true;
        
    }
    
    /**
     * Returns a CustomValidatorSettings object which is a copy of this object.
     */
    public CustomValidatorSettings clone() {
        CustomValidatorSettings clone = new CustomValidatorSettings();
        //the following should work because Strings are immutable...
        clone.setCustomValidatorProgramName(this.getCustomValidatorProgramName());
        clone.setCustomValidatorCommandLine(this.getCustomValidatorCommandLine());
        return clone;
    }



    /**
     * @return the usePC2ValidatorInterface flag setting
     */
    public boolean isUsePC2ValidatorInterface() {
        return usePC2ValidatorInterface;
    }



    /**
     * Sets the flag indicating that this validator expects to use the PC2 Validator Interface.
     * Note that it is the caller's responsibility to also call method {@link #setUseCLICSValidatorInterface(boolean)}
     * to set the useCLICSValidatorInterface to the complement of the value passed to this method. 
     * 
     * @param yesNo the value to which the usePC2ValidatorInterface flag should be set
     */
    public void setUsePC2ValidatorInterface(boolean yesNo) {
        this.usePC2ValidatorInterface = yesNo;
    }



    /**
     * @return the useCLICSValidatorInterface
     */
    public boolean isUseCLICSValidatorInterface() {
        return useCLICSValidatorInterface;
    }



    /**
     * Sets the flag indicating that this validator expects to use the CLICS Validator Interface.
     * Note that it is the caller's responsibility to also call method {@link #setUsePC2ValidatorInterface(boolean)}
     * to set the usePC2ValidatorInterface to the complement of the value passed to this method. 
     * 
     * @param yesNo the value to which the useCLICSValidatorInterface flag should be set
     */
    public void setUseCLICSValidatorInterface(boolean yesNo) {
        this.useCLICSValidatorInterface = yesNo;
    }

}
