/**
 * 
 */
package edu.csus.ecs.pc2.core.model.inputValidation;

import java.io.Serializable;
import java.util.Vector;

import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Problem.InputValidationStatus;

/**
 * This class holds the Viva Input Validator settings for a particular Problem.
 * 
 * @author John Clevenger, PC2 Development Team (pc2@ecs.csus.edu)
 *
 */
public class VivaInputValidatorSettings implements Serializable {

    private boolean vivaInputValidatorHasBeenRun = false;
    private String [] vivaInputValidatorPattern = new String[0];
    private InputValidationStatus vivaInputValidationStatus = InputValidationStatus.UNKNOWN;
    private Vector<InputValidationResult> vivaInputValidationResults = new Vector<InputValidationResult>();
    
    
    /**
     * Returns an indication of whether this VivaInputValidatorSettings object current contains a non-zero-length
     * VIVA pattern.
     * 
     * @return true if there is a Viva Input Validator Pattern of length greater than zero; otherwise, false.
     */
    public boolean isProblemHasVivaInputValidatorPattern() {
        return vivaInputValidatorPattern != null && vivaInputValidatorPattern.length>0;
    }


//    /**
//     * Sets the flag indicating whether or not this VivaInputValidatorSettings object has a Viva Pattern stored in it.
//     * 
//     * @param hasVivaPattern the value to which the flag should be set.
//     */
//    public void setProblemHasVivaInputValidatorPattern(boolean hasVivaPattern) {
//        problemHasVivaInputValidatorPattern = hasVivaPattern;
//    }

    /**
     * Returns the Input Validation status stored in this VivaInputValidatorSettings object.
     * 
     * Use method {@link Problem#getCurrentInputValidatorType()} to determine 
     * the current type of Input Validator associated with a problem.
     * 
     * @return an element of {@link InputValidationStatus} indicating the VIVA Input Validation status stored in this object.
     */

    public InputValidationStatus getVivaInputValidationStatus() {
        return vivaInputValidationStatus;
    }


    /**
     * Sets the VIVA Input Validation status in this VivaInputValidatorSettings to the specified value.
     * 
     * @param status the value to which the VIVA Input Validation status should be set.
     */
    public void setVivaInputValidationStatus(InputValidationStatus status) {
        vivaInputValidationStatus = status;
    }


    /**
     * Returns an {@link Iterable} for the current VIVA {@link InputValidationResults} contained in this VivaInputValidatorSettings object. 
     * The returned object may be empty (that it, the Iterable may have no elements) but will never be null.
     * 
     * @return an {@link Iterable} containing VIVA InputValidationResults.
     */
    public Iterable<InputValidationResult> getVivaInputValidationResults() {
        if (this.vivaInputValidationResults == null) {
            this.vivaInputValidationResults = new Vector<InputValidationResult>() ;
        }
        return this.vivaInputValidationResults;
    }
    
    /**
     * Returns the number of VIVA Input Validator {@link InputValidationResult}s currently stored in this VivaInputValidatorSettings object.
     */
    public int getNumVivaInputValidationResults() {
        if (this.vivaInputValidationResults == null) {
            this.vivaInputValidationResults = new Vector<InputValidationResult>();
        }
        return vivaInputValidationResults.size();
    }


    /**
     * Adds the specified {@link InputValidationResult} to the current set of VIVA Input Validation Results
     * stored in this VivaInputValidatorSettings object.
     * 
     * @param result the InputValidationResult to be added.
     */
    public void addVivaInputValidationResult(InputValidationResult result) {
        if (this.vivaInputValidationResults == null) {
            this.vivaInputValidationResults = new Vector<InputValidationResult>();
        }
        vivaInputValidationResults.add(result);
    }


    /**
     * Clears (removes) all VIVA {@link InputValidationResult}s currently stored in this VivaInputValidatorSettings object.
     * Any previously-existing VIVA InputValidationResults are discarded.
     */
    public void clearVivaInputValidationResults() {
        vivaInputValidationResults = new Vector<InputValidationResult>();
    }


    /**
     * Returns a String array containing the VIVA Input Validator pattern stored in this VivaInputValidatorSettings object,
     * or null if no VIVA pattern has been assigned.
     * Note that the VIVA pattern is an array of String, one pattern line per array element.
     * 
     * @return a String [] containing the Viva pattern, or null.
     */
    public String[] getVivaInputValidatorPattern() {
        return vivaInputValidatorPattern;
    }


    /**
     * Sets the Viva Input Validator pattern stored in this VivaInputValidatorSettings object to the specified String array.
     */
    public void setVivaInputValidatorPattern(String[] pattern) {
       vivaInputValidatorPattern = pattern;
    }


    /**
     * @return the vivaInputValidatorHasBeenRun flag stored in this VivaInputValidatorSettings object.
     */
    public boolean isVivaInputValidatorHasBeenRun() {
        return this.vivaInputValidatorHasBeenRun;
    }


    /**
     * @param vivaInputValidatorHasBeenRun the value to which the vivaInputValidatorHasBeenRun flag in this VivaInputValidatorSettings object should be set.
     */
    public void setVivaInputValidatorHasBeenRun(boolean vivaInputValidatorHasBeenRun) {
        this.vivaInputValidatorHasBeenRun = vivaInputValidatorHasBeenRun ;
    }

    
}
