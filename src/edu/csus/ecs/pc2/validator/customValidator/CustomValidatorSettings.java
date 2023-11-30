// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
/**
 * This class encapsulates settings for configuring a "custom validator".
 * Settings include the validator program name (that is, the program which acts as
 * the custom validator) and the validator command line (that is, the command line
 * used to invoke the validator, including whatever options need to be passed to the 
 * validator program). 
 * 
 * Custom Validators must be written to use one of two "Interfaces" to the PC2 system --
 * either the "PC2 Interface" or the "CLICS" interface.  Since the "Interface mode" can be
 * changed by the user at any time, this class maintains TWO versions of the "Validator Command Line"
 * (the command line used to invoke the Validator Program): one for the case of a Custom Validator
 * using the PC2 Interface and one for the case of a Custom Validator using the CLICS Interface.
 * The getters and setters associated with the Validator Command Line get/set the command line
 * associated with the current "interface mode" setting in this CustomValidatorSettings object.
 */
package edu.csus.ecs.pc2.validator.customValidator;

import java.io.Serializable;
import java.util.Objects;

import edu.csus.ecs.pc2.core.Constants;

/**
 * @author pc2@ecs.csus.edu
 *
 */
public class CustomValidatorSettings implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    
    //the name of the custom validator (the program which is to be executed)
    private String customValidatorProgramName;
    
    //the command which is used to invoke the validator (typically includes the validator program name)
    // Note that there are two variables, to hold two versions of the Command Line: one for Validators 
    // using the PC2 Interface standard and one for Validators using the CLICS Interface standard
    private String customPC2InterfaceValidatorCommandLine;
    private String customCLICSInterfaceValidatorCommandLine;
    
    // flags indicating which validator interface this validator is using - that is, which 
    // "Interface mode" is currently active
    private boolean usePC2ValidatorInterface ;
    private boolean useCLICSValidatorInterface ;
    
    /**
     * should the problem be judged interactively.  requires a custom CLICS compliant validator
     */
    private boolean useInteractiveValidatorInterface;



    /**
     * Constructs a CustomValidatorSettings object with default values of the empty string
     * for the Validator executable program name, the default PC2 Validator and CLICS Validator
     * invocation command lines, and with a default setting of "usePC2ValidatorInterface".
     * 
     * @see {@link Constants}
     */
    public CustomValidatorSettings() {
        this.customValidatorProgramName = "";
        this.customPC2InterfaceValidatorCommandLine = Constants.DEFAULT_PC2_VALIDATOR_COMMAND;
        this.customCLICSInterfaceValidatorCommandLine = Constants.DEFAULT_CLICS_VALIDATOR_COMMAND;
        this.usePC2ValidatorInterface = false ;
        this.useCLICSValidatorInterface = true;
        this.useInteractiveValidatorInterface = false;
    }


    /**
     * Returns the name of the custom validator program.
     * 
     * @return a String containing the name of the custom validator program
     */
    public String getCustomValidatorProgramName() {
        return customValidatorProgramName;
    }

    /**
     * Sets the name for the custom validator program to the specified value.
     * Note that the validator program name does not include command line options; those
     * are specified via the separate "customValidatorCommand".
     * 
     * @see {@link #setValidatorCommandLine(String)}
     * 
     * @param progName -- the custom validator program name
     */
    public void setValidatorProgramName(String progName) {
        this.customValidatorProgramName = progName;
    }


    /**
     * Returns the current Custom Validator command line; that is, the command used to invoke the Custom Validator.
     * This typically includes the name of the validator program as the first element, followed by any arguments
     * to be passed to the custom validator.
     * 
     * Note that the returned Custom Validator Command Line depends on the current Validator Interface
     * mode. That is, if the settings in this CustomValidatorSettings object indicate "usePC2ValidatorInterface", 
     * this method returns the current PC2 Validator Command Line; if the settings indicate "useCLICSValidatorInterface",
     * this method returns the current CLICS Validator Command Line.
     * 
     * @see {@link #setValidatorCommandLine(String)}
     * @see {@link #setUseClicsValidatorInterface()}
     * @see {@link #setUsePC2ValidatorInterface()}
     * 
     * @return the command line used to invoke the custom validator when using the currently-set Validator Interface mode
     * 
     * @throws {@link RuntimeException} if the settings do not contain a valid Validator Interface mode setting
     */
    public String getCustomValidatorCommandLine() {
        if (this.isUseClicsValidatorInterface() || this.isUseInteractiveValidatorInterface()) {
            return this.customCLICSInterfaceValidatorCommandLine;
        } else if (this.isUsePC2ValidatorInterface()) {
            return this.customPC2InterfaceValidatorCommandLine;
        } else {
            throw new RuntimeException("CustomValidatorSettings.getCustomValidatorCommandLine(): undefined Validator Interface mode");
        }
    }

    /**
     * Sets the command line to be used to invoke the Custom Validator when using the currently-defined Validator Interface mode.
     * The command typically includes the name of the custom validator program as the first element, followed by
     * any arguments to be passed to the custom validator.
     * 
     * Note that this class internally maintains two representations of the Validator Command Line, depending on
     * the Validator Interface mode (that is, whether the Validator uses the PC2 Interface Standard or the
     * CLICS Interface Standard).  Calling this method sets the Validator Command Line associated with the currently-set
     * Validator Interface mode.
     * 
     * It is the caller's responsibility to insure that this CustomValidatorSettings object
     * is in the desired Validator Interface mode (that is, set to use either the PC2 Interface Standard or 
     * the CLICS Interface Standard as desired) prior to invoking this method.  This method ONLY updates
     * the CustomValidatorCommandLine corresponding to the currently-specified Validator Interface mode.
     * 
     * @see {@link #setUseClicsValidatorInterface()}
     * @see {@link #setUsePC2ValidatorInterface()} 
     * 
     * @param commandString -- a String defining the command line used to invoke the Custom Validator when
     * the Validator is set to use the currently-set Validator Interface Standard.
     * 
     * @throws {@link RuntimeException} if this settings object has neither the PC2Validator Interface mode
     *              nor the CLICSValidator Interface mode set
     * 
     */
    public void setValidatorCommandLine(String commandString) {
        if (this.isUseClicsValidatorInterface() || this.isUseInteractiveValidatorInterface()) {
            this.customCLICSInterfaceValidatorCommandLine = commandString;
        } else if (this.isUsePC2ValidatorInterface()) {
            this.customPC2InterfaceValidatorCommandLine = commandString;
        } else {
            throw new RuntimeException("setCustomValidatorCommandLine(): no Validator Interface mode defined.");
        }
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
        
        //check the validator program name
        //First, check whether one is null while the other is not, using the Java XOR ("^") operator
        if (this.getCustomValidatorProgramName()==null ^ other.getCustomValidatorProgramName()==null) {
            return false;
        }
        //then, check whether, if both are non-null, both are the same
        if (this.getCustomValidatorProgramName()!=null && other.getCustomValidatorProgramName()!=null) {
            if (!(this.getCustomValidatorProgramName().equals(other.getCustomValidatorProgramName()))) {
                return false;
            }
        }
        
        //check whether the flags specifying which validator interface to use are identical
        if (this.isUsePC2ValidatorInterface() != other.isUsePC2ValidatorInterface()) {
            return false;
        }
        if (this.isUseClicsValidatorInterface() != other.isUseClicsValidatorInterface()) {
            return false;
        }
        if (this.isUseInteractiveValidatorInterface() != other.isUseInteractiveValidatorInterface()) {
            return false;
        }
        
        //check the PC2 validator interface invocation command strings
        //First, check whether one is null while the other is not, using the Java XOR ("^") operator
        if (this.customPC2InterfaceValidatorCommandLine==null ^ other.customPC2InterfaceValidatorCommandLine==null) {
            return false;
        }
        //then, check whether, if both are non-null, both are the same
        if (this.customPC2InterfaceValidatorCommandLine!=null && other.customPC2InterfaceValidatorCommandLine!=null) {
            if (!(this.customPC2InterfaceValidatorCommandLine.equals(other.customPC2InterfaceValidatorCommandLine))) {
                return false;
            }
        }
        
        //check the Clics validator interface invocation command strings
        //First, check whether one is null while the other is not, using the Java XOR ("^") operator
        if (this.customCLICSInterfaceValidatorCommandLine==null ^ other.customCLICSInterfaceValidatorCommandLine==null) {
            return false;
        }
        //then, check whether, if both are non-null, both are the same
        if (this.customCLICSInterfaceValidatorCommandLine!=null && other.customCLICSInterfaceValidatorCommandLine!=null) {
            if (!(this.customCLICSInterfaceValidatorCommandLine.equals(other.customCLICSInterfaceValidatorCommandLine))) {
                return false;
            }
        }
        // remember to update hashCode if new fields are added here

        return true;
        
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(customValidatorProgramName,usePC2ValidatorInterface,useCLICSValidatorInterface,useInteractiveValidatorInterface,
                customPC2InterfaceValidatorCommandLine,customCLICSInterfaceValidatorCommandLine);
    }
    
    /**
     * Returns a CustomValidatorSettings object which is a copy of this object.
     * 
     */
    public CustomValidatorSettings clone() {
        //create a clone with default values
        CustomValidatorSettings clone = new CustomValidatorSettings();
        
        //update the validator program name
        clone.setValidatorProgramName(this.getCustomValidatorProgramName());
        
        //update the interface mode settings
        clone.useCLICSValidatorInterface = this.isUseClicsValidatorInterface();
        clone.usePC2ValidatorInterface = this.isUsePC2ValidatorInterface();
        clone.useInteractiveValidatorInterface = this.isUseInteractiveValidatorInterface();
        
        //update the validator command lines. (Note that this should NOT be done with accessors because the
        // getters return a string that depends on the current Validator Interface mode; we need to
        // set BOTH strings.)
        clone.customPC2InterfaceValidatorCommandLine = this.customPC2InterfaceValidatorCommandLine;
        clone.customCLICSInterfaceValidatorCommandLine = this.customCLICSInterfaceValidatorCommandLine;
        
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
     * This method should be called prior to attempting to use {@link #setValidatorCommandLine(String)}
     * to set a PC2 Validator Command Line.
     * 
     * @see {@link #setValidatorCommandLine(String)}
     * 
     */
    public void setUsePC2ValidatorInterface() {
        this.usePC2ValidatorInterface = true;
        this.useCLICSValidatorInterface = false;
    }



    /**
     * @return the useCLICSValidatorInterface flag setting
     */
    public boolean isUseClicsValidatorInterface() {
        return useCLICSValidatorInterface;
    }



    /**
     * Sets the flag indicating that this validator expects to use the CLICS Validator Interface.
     * This method should be called prior to attempting to use {@link #setValidatorCommandLine(String)}
     * to set a CLICS Validator Command Line.
     * 
     * @see {@link #setValidatorCommandLine(String)}
     * 
     */
    public void setUseClicsValidatorInterface() {
        this.useCLICSValidatorInterface = true;
        this.usePC2ValidatorInterface = false;
    }

    
    /**
     * Sets the flag indicating that this validator expects to use the CLICS Interactive Validator Interface.
     * @see {@link #setValidatorCommandLine(String)}
     * 
     */
    public void setUseInteractiveValidatorInterface() {
        this.useCLICSValidatorInterface = false;
        this.usePC2ValidatorInterface = false;
        this.useInteractiveValidatorInterface = true;
    }

    /**
     * @return the useInteractiveValidatorInterface flag setting
     */
    public boolean isUseInteractiveValidatorInterface() {
        return useInteractiveValidatorInterface;
    }

    
    @Override
    public String toString() {
        String retStr = "CustomValidatorSettings[";
        
        retStr += "ValidatorProgramName=" + customValidatorProgramName;
        
        retStr += "; usePC2ValidatorInterface=" + usePC2ValidatorInterface;
        retStr += "; useCLICSValidatorInterface=" + useCLICSValidatorInterface;
        retStr += "; useInteractiveValidatorInterface=" + useInteractiveValidatorInterface;

        retStr += "; PC2InterfaceValidatorCommandLine=" + customPC2InterfaceValidatorCommandLine;
        retStr += "; CLICSInterfaceValidatorCommandLine=" + customCLICSInterfaceValidatorCommandLine;
        
        retStr += "]";
        return retStr ;
        
    }

}
