/**
 * This class encapsulates a set of options for configuring the PC2/CLICS "default validator".
 * Options include whether validation should be case-sensitive and/or space-sensitive, and
 * what tolerances should be used for any floating-point values in the team output processed
 * by the validator. 
 */
package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;

/**
 * @author pc2@ecs.csus.edu
 *
 */
public class DefaultValidatorSettings implements Serializable, Cloneable {

    static Log log = null;

    private static final long serialVersionUID = 1L;
    
    public static final String VTOKEN_CASE_SENSITIVE = "case_sensitive";
    public static final String VTOKEN_SPACE_CHANGE_SENSITIVE = "space_change_sensitive";
    public static final String VTOKEN_FLOAT_RELATIVE_TOLERANCE = "float_relative_tolerance";
    public static final String VTOKEN_FLOAT_ABSOLUTE_TOLERANCE = "float_absolute_tolerance";
    
    public static final boolean DEFAULT_CASE_SENSITIVITY = false;
    public static final boolean DEFAULT_SPACE_SENSITIVITY = false;
    public static final boolean DEFAULT_IS_FLOAT_RELATIVE_TOLERANCE_SPECIFIED = false;
    public static final boolean DEFAULT_IS_FLOAT_ABSOLUTE_TOLERANCE_SPECIFIED = false;
    public static final double DEFAULT_FLOAT_RELATIVE_TOLERANCE = -1.0;
    public static final double DEFAULT_FLOAT_ABSOLUTE_TOLERANCE = -1.0;
    
    private boolean isCaseSensitive;
    
    private boolean isSpaceSensitive;
    
    private boolean isFloatRelativeToleranceSpecified;
    
    private boolean isFloatAbsoluteToleranceSpecified;
    
    private double floatRelativeTolerance;
    
    private double floatAbsoluteTolerance;
    
    /**
     * Constructs a DefaultValidatorSettings object with default values matching the 
     * defined public default constants.
     */
    public DefaultValidatorSettings() {
        if (log==null) {
            log = new Log("DefaultValidatorSettings.log");
            StaticLog.setLog(log);
            log = StaticLog.getLog();            
        }

        this.isCaseSensitive = DEFAULT_CASE_SENSITIVITY;
        this.isSpaceSensitive = DEFAULT_SPACE_SENSITIVITY;
        this.isFloatRelativeToleranceSpecified = DEFAULT_IS_FLOAT_RELATIVE_TOLERANCE_SPECIFIED;
        this.isFloatAbsoluteToleranceSpecified = DEFAULT_IS_FLOAT_RELATIVE_TOLERANCE_SPECIFIED;
        this.floatRelativeTolerance = DEFAULT_FLOAT_RELATIVE_TOLERANCE;
        this.floatAbsoluteTolerance = DEFAULT_FLOAT_ABSOLUTE_TOLERANCE;
    }
    
    /**
     * Constructs a DefaultValidatorSettings object containing the values specified in the received String.
     * The received String is expected to contain zero or more space-separated "validator setting" options
     * as defined in the CLICS <A href="https://clics.ecs.baylor.edu/index.php/Problem_format#Default_Validator_Capabilities">
     * <I>default validator options</i></a> specification.
     * Any options not specified are set to the defaults values defined by the public default constants,
     * as if the object was constructed by calling {@link DefaultValidator#DefaultValidator()}. 
     * Any values in the received string not matching the CLICS validator options are logged but
     * otherwise silently ignored.  The same is true if valid options are specified (e.g. "float_tolerance")
     * but the required following tolerance value ("epsilon") is missing.
     */
    public DefaultValidatorSettings(String options) {
        
        //start with the default settings
        this();
        
        //see if the input string has anything to offer
        if (options!=null && options.length()>0) {
            
            //split the string into space-delimited strings
            String [] opts = options.split(" ");
            
            //process each separate string option
            for (int i=0; i<opts.length; i++) {
                
                switch (opts[i]) {
                    case VTOKEN_CASE_SENSITIVE:
                    case "case-sensitive":
                        this.setCaseSensitive(true);
                        break;
                        
                    case VTOKEN_SPACE_CHANGE_SENSITIVE:
                    case "space-change-sensitive":
                        this.setSpaceSensitive(true);
                        break;
                        
                    case VTOKEN_FLOAT_RELATIVE_TOLERANCE:
                    case "float-relative-tolerance":
                        if (i<opts.length-1) {
                            try {
                                double epsilon = Double.parseDouble(opts[i+1]) ;
                                this.setFloatRelativeTolerance(epsilon);
                            } catch (NumberFormatException | NullPointerException e) {
                                log.warning("DefaultValidatorSettings(String) constructor: invalid float tolerance epsilon value; option ignored");
                            }
                            i++;
                        } else {
                            log.warning("DefaultValidatorSettings(String) constructor missing float tolerance epsilon value; option ignored"); 
                        }
                        break;
                        
                    case VTOKEN_FLOAT_ABSOLUTE_TOLERANCE:
                    case "float-absolute-tolerance":
                        if (i<opts.length-1) {
                            try {
                                double epsilon = Double.parseDouble(opts[i+1]) ;
                                this.setFloatAbsoluteTolerance(epsilon);
                            } catch (NumberFormatException | NullPointerException e) {
                                log.warning("DefaultValidatorSettings(String) constructor: invalid float tolerance epsilon value; option ignored");
                            }
                            i++;
                        } else {
                            log.warning("DefaultValidatorSettings(String) constructor missing float tolerance epsilon value; option ignored"); 
                        }
                        break;
                        
                    case "float_tolerance":
                    case "float-tolerance":
                        if (i<opts.length-1) {
                            try {
                                double epsilon = Double.parseDouble(opts[i+1]) ;
                                this.setFloatRelativeTolerance(epsilon);
                                this.setFloatAbsoluteTolerance(epsilon);
                            } catch (NumberFormatException | NullPointerException e) {
                                log.warning("DefaultValidatorSettings(String) constructor: invalid float tolerance epsilon value; option ignored");
                            }
                            i++;
                        } else {
                            log.warning("DefaultValidatorSettings(String) constructor missing float tolerance epsilon value; option ignored"); 
                        }
                        break;
                        
                    default:
                        log.warning("DefaultValidatorSettings(String) constructor received unknown option '" + opts[i] +"'; ignored");
                        break;
                }//end switch
            }//end for each option field
        }
    }

    /**
     * @return whether or not the isCaseSensitive option is set.
     */
    public boolean isCaseSensitive() {
        return isCaseSensitive;
    }

    /**
     * Sets the case-sensitivity option.
     * @param isCaseSensitive the case-sensitivity value to set
     */
    public void setCaseSensitive(boolean isCaseSensitive) {
        this.isCaseSensitive = isCaseSensitive;
    }

    /**
     * @return whether or not the isSpaceSensitive option is set.
     */
    public boolean isSpaceSensitive() {
        return isSpaceSensitive;
    }

    /**
     * Sets the space-sensitivity option.
     * @param isSpaceSensitive the space-sensitivity value to set
     */
    public void setSpaceSensitive(boolean isSpaceSensitive) {
        this.isSpaceSensitive = isSpaceSensitive;
    }

    /**
     * Returns the current float relative tolerance value.
     * @return the floatRelativeTolerance
     */
    public double getFloatRelativeTolerance() {
        return floatRelativeTolerance;
    }

    /**
     * Sets the float relative tolerance to the specified value.
     * @param floatRelativeTolerance the float relative tolerance value to set
     */
    public void setFloatRelativeTolerance(double floatRelativeTolerance) {
        this.floatRelativeTolerance = floatRelativeTolerance;
        this.isFloatRelativeToleranceSpecified = true;
    }

    /**
     * Returns the current float absolute tolerance value.
     * @return the floatAbsoluteTolerance
     */
    public double getFloatAbsoluteTolerance() {
        return floatAbsoluteTolerance;
    }

    /**
     * Sets the float absolute tolerance to the specified value.
     * @param floatAbsoluteTolerance the float absolute tolerance value to set
     */
    public void setFloatAbsoluteTolerance(double floatAbsoluteTolerance) {
        this.floatAbsoluteTolerance = floatAbsoluteTolerance;
        this.isFloatAbsoluteToleranceSpecified = true;
    }

    /**
     * Returns the flag indicating whether float relative tolerance has been specified for this collection of settings.
     * @return the isFloatRelativeToleranceSpecified flag
     */
    public boolean isFloatRelativeToleranceSpecified() {
        return isFloatRelativeToleranceSpecified;
    }


    /**
     * Returns the flag indicating whether float absolute tolerance has been specified for this collection of settings.
     * @return the isFloatAbsoluteToleranceSpecified flag
     */
    public boolean isFloatAbsoluteToleranceSpecified() {
        return isFloatAbsoluteToleranceSpecified;
    }

    /**
     * Returns true if the default validator settings in this object match those in
     * the "other" object; false otherwise.
     * Note that if both objects agree that float absolute and/or relative tolerance is NOT
     * specified, then the objects will otherwise compare as "equal" regardless of the actual values
     * stored in the absolute and/or relative float tolerance fields (in other words, if both objects
     * agree that a tolerance is "not specified", then no check against the corresponding values is
     * performed).
     * 
     * @param other -- the object against which to compare this object's settings
     * @return true if this object matches the other object
     */
    @Override
    public boolean equals (Object obj) {
        
        if (obj==null || !(obj instanceof DefaultValidatorSettings)) {
            return false;
        }
        
        DefaultValidatorSettings other = (DefaultValidatorSettings) obj;
        
        
        if (this.isCaseSensitive()!=other.isCaseSensitive()) {
            return false;
        }
        
        if (this.isSpaceSensitive()!=other.isSpaceSensitive()) {
            return false;
        }
        
        if (this.isFloatAbsoluteToleranceSpecified() != other.isFloatAbsoluteToleranceSpecified()) {
            return false;
        }
        
        if (this.isFloatRelativeToleranceSpecified() != other.isFloatRelativeToleranceSpecified()) {
            return false;
        }
        
        if (this.isFloatAbsoluteToleranceSpecified() && this.getFloatAbsoluteTolerance()!=other.getFloatAbsoluteTolerance()) {
            return false;
        }
        
        if (this.isFloatRelativeToleranceSpecified() && this.getFloatRelativeTolerance()!=other.getFloatRelativeTolerance()) {
            return false;
        }
        
       
        return true;
        
    }
    
    /**
     * Returns a DefaultValidatorSettings object which is a clone of this object.
     */
    public DefaultValidatorSettings clone() {
        DefaultValidatorSettings clone = new DefaultValidatorSettings();
        clone.setCaseSensitive(this.isCaseSensitive());
        clone.setSpaceSensitive(this.isSpaceSensitive());
        clone.setFloatAbsoluteTolerance(this.getFloatAbsoluteTolerance());
        clone.setFloatRelativeTolerance(this.getFloatRelativeTolerance());
        return clone;
    }
    
    /**
     * Returns a String representation of this DefaultValidatorSettings object.
     * The format of the string is as if the validator options were specified on a command line
     * as per the CLICS Default Validator invocation specification; that is, a series of space-delimited
     * options.  Note that this means that if an option is currently not specified, the option will not
     * be present in the returned string.  This in turn means, for example, that the string returned for
     * a DefaultValidatorSettings object for which no options have been specified will be the empty string.
     */
    public String toString() {
        String retStr = "";
        if (this.isCaseSensitive()) {
            retStr += "case_sensitive";
        }
        if (this.isSpaceSensitive()) {
            if (retStr.length()>0) {
                retStr += " ";
            }
            retStr += "space_change_sensitive";
        }
        if (this.isFloatAbsoluteToleranceSpecified()) {
            if (retStr.length()>0) {
                retStr += " ";
            }
            retStr += "float_absolute_tolerance ";
            retStr += Double.toString(getFloatAbsoluteTolerance());
        }
        if (this.isFloatRelativeToleranceSpecified()) {
            if (retStr.length()>0) {
                retStr += " ";
            }
            retStr += "float_relative_tolerance ";
            retStr += Double.toString(getFloatRelativeTolerance());
        }
        return retStr;
    }
}
