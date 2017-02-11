/**
 * This class encapsulates settings for configuring a "custom validator".
 * Settings include the validator program name (that is, the program which acts as
 * the custom validator) and the validator command line (that is, the command line
 * used to invoke the validator, including whatever options need to be passed to the 
 * validator program). 
 */
package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;

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
    
    // flags indicating which validator interface this validator is using
    private boolean usePC2ValidatorInterface ;
    private boolean useCLICSValidatorInterface ;


    
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
        this.usePC2ValidatorInterface = true ;
        this.useCLICSValidatorInterface = false;
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
     * Sets the name for the custom validator to the specified value.
     * Note that the validator program name does not include command line options; those
     * are specified via the separate "customValidatorCommand".
     * 
     * @see {@link #setCustomValidatorCommandLine(String)}
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
     * Note that the returned Custom Validator Command Line depends on the current Validator Interface Standard mode.
     * That is, if the Validator Settings indicate "usePC2ValidatorInterface", this method returns the current
     * PC2 Validator Command Line; if the Validator Settings indicate "useCLICSValidatorInterface",
     * this method returns the current CLICS Validator Command Line.
     * 
     * @see {@link #setCustomValidatorPC2InterfaceCommandLine(String)}
     * @see {@link #setCustomValidatorCLICSInterfaceCommandLine(String)}
     * @see {@link #setUseCLICSValidatorInterface()}
     * @see {@link #setUsePC2ValidatorInterface()}
     * 
     * @return the command line used to invoke the custom validator when using the currently-set Validator Interface
     * 
     * @throws {@link RuntimeException} if the settings do not contain a valid Validator Interface mode setting
     */
    public String getCustomValidatorCommandLine() {
        if (this.isUseCLICSValidatorInterface()) {
            return this.customCLICSInterfaceValidatorCommandLine;
        } else if (this.isUsePC2ValidatorInterface()) {
            return this.customPC2InterfaceValidatorCommandLine;
        } else {
            throw new RuntimeException("CustomValidatorSettings.getCustomValidatorCommandLine(): undefined Validator Interface mode");
        }
    }

    /**
     * Sets the command line to be used to invoke the Custom Validator when using the PC2 Validator Interface Standard.
     * The command typically includes the name of the custom validator program as the first element, followed by
     * any arguments to be passed to the custom validator.
     * 
     * Note that this class internally maintains two representations of the Validator Command Line, depending on
     * the Validator Interface mode (that is, whether the Validator uses the PC2 Interface Standard or the
     * CLICS Interface Standard).  Calling this method sets the Validator Command Line for the PC2 Validator Interface mode.
     * 
     * It is the caller's responsibility to insure that this CustomValidatorSettings object
     * is in the desired Validator Interface Standard mode (that is, set to use either the PC2 Interface Standard or 
     * the CLICS Interface Standard as desired). 
     * 
     * @see {@link #setUseCLICSValidatorInterface()}
     * @see {@link #setUsePC2ValidatorInterface()} 
     * 
     * @param commandString -- a String defining the command line used to invoke the Custom Validator when
     * the Validator is set to use the PC2 Validator Interface Standard.
     * 
     */
    public void setCustomValidatorPC2InterfaceCommandLine(String commandString) {
            this.customPC2InterfaceValidatorCommandLine = commandString;
    }

    /**
     * Sets the command line to be used to invoke the Custom Validator when using the CLICS Validator Interface Standard.
     * The command typically includes the name of the custom validator program as the first element, followed by
     * any arguments to be passed to the custom validator.
     * 
     * Note that this class internally maintains two representations of the Validator Command Line, depending on
     * the Validator Interface mode (that is, whether the Validator uses the PC2 Interface Standard or the
     * CLICS Interface Standard).  Calling this method sets the Validator Command Line for the CLICS Validator Interface mode.
     * 
     * It is the caller's responsibility to insure that this CustomValidatorSettings object
     * is in the desired Validator Interface Standard mode (that is, set to use either the PC2 Interface Standard or 
     * the CLICS Interface Standard as desired). 
     * 
     * @see {@link #setUseCLICSValidatorInterface()}
     * @see {@link #setUsePC2ValidatorInterface()} 
     * 
     * @param commandString -- a String defining the command line used to invoke the Custom Validator when
     * the Validator is set to use the CLICS Validator Interface Standard.
     * 
     */
    public void setCustomValidatorCLICSInterfaceCommandLine(String commandString) {
            this.customPC2InterfaceValidatorCommandLine = commandString;
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
     * 
     * @throws {@link RuntimeException} if this CustomValidatorSettings object does not have a Validator Interface mode set
     */
    public CustomValidatorSettings clone() {
        CustomValidatorSettings clone = new CustomValidatorSettings();
        clone.setCustomValidatorProgramName(this.getCustomValidatorProgramName());  //should work because Strings are immutable...
        if (this.isUseCLICSValidatorInterface()) {
            clone.setUseCLICSValidatorInterface();
        } else if (this.isUsePC2ValidatorInterface()){
            clone.setUsePC2ValidatorInterface();
        } else {
            throw new RuntimeException ("CustomValidatorSettings.clone(): unknown Validator Interface state");
        }
        clone.setCustomValidatorPC2InterfaceCommandLine(this.customPC2InterfaceValidatorCommandLine);
        clone.setCustomValidatorCLICSInterfaceCommandLine(this.customCLICSInterfaceValidatorCommandLine);

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
     * This method should be called prior to attempting to use {@link #setCustomValidatorCommandLine(String)}
     * to set a PC2 Validator Command Line.
     * 
     */
    public void setUsePC2ValidatorInterface() {
        this.usePC2ValidatorInterface = true;
        this.useCLICSValidatorInterface = false;
    }



    /**
     * @return the useCLICSValidatorInterface flag setting
     */
    public boolean isUseCLICSValidatorInterface() {
        return useCLICSValidatorInterface;
    }



    /**
     * Sets the flag indicating that this validator expects to use the CLICS Validator Interface.
     * This method should be called prior to attempting to use {@link #setCustomValidatorCommandLine(String)}
     * to set a CLICS Validator Command Line.
     * 
     */
    public void setUseCLICSValidatorInterface() {
        this.useCLICSValidatorInterface = true;
        this.usePC2ValidatorInterface = false;
    }
    
    @Override
    public String toString() {
        String retStr = "CustomValidatorSettings[";
        
        retStr += "ValidatorProgramName=" + customValidatorProgramName;
        
        retStr += "; PC2InterfaceValidatorCommandLine=" + customPC2InterfaceValidatorCommandLine;
        
        retStr += "; CLICSInterfaceValidatorCommandLine=" + customCLICSInterfaceValidatorCommandLine;
        
        retStr += "; usePC2ValidatorInterface=" + usePC2ValidatorInterface;
        retStr += "; useCLICSValidatorInterface=" + useCLICSValidatorInterface;

        retStr += "]";
        return retStr ;
        
    }

}
