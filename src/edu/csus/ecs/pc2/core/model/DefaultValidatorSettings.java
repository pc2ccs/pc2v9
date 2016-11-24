/**
 * This class encapsulates a set of options for configuring the PC2/CLICS "default validator".
 * Options include whether validation should be case-sensitive and/or space-sensitive, and
 * what tolerances should be used for any floating-point values in the team output processed
 * by the validator. 
 */
package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;

/**
 * @author pc2@ecs.csus.edu
 *
 */
public class DefaultValidatorSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean isCaseSensitive;
    
    private boolean isSpaceSensitive;
    
    private boolean isFloatRelativeToleranceSpecified;
    
    private boolean isFloatAbsoluteToleranceSpecified;
    
    private double floatRelativeTolerance = -1.0;
    
    private double floatAbsoluteTolerance = -1.0;
    
    /**
     * Constructs a DefaultValidatorSettings object with default values specifying
     * NOT case-sensitive, NOT space-sensitive, and with neither absolute and relative
     * float tolerances specified.
     */
    public DefaultValidatorSettings() {
        this.isCaseSensitive = false;
        this.isSpaceSensitive = false;
        this.isFloatRelativeToleranceSpecified = false;
        this.isFloatAbsoluteToleranceSpecified = false;
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
     * Returns a DefaultValidatorSettings object which is a copy of this object.
     */
    public DefaultValidatorSettings clone() {
        DefaultValidatorSettings clone = new DefaultValidatorSettings();
        clone.setCaseSensitive(this.isCaseSensitive());
        clone.setSpaceSensitive(this.isSpaceSensitive());
        clone.setFloatAbsoluteTolerance(this.getFloatAbsoluteTolerance());
        clone.setFloatRelativeTolerance(this.getFloatRelativeTolerance());
        return clone;
    }

}
