package edu.csus.ecs.pc2.validator.inputValidator;

/**
 * This class encapsulates the results of testing a pattern string to see whether it is a valid VIVA
 * pattern.
 * 
 * @author John Clevenger, PC2 Development Team (pc2@ecs.csus.edu)
 *
 */
public class VivaPatternTestResult {

    private String pattern ;
    private String vivaResponse ;
    private boolean isValid ;
    
    public VivaPatternTestResult(String pattern, String vivaOutput) {
        this.pattern = pattern;
        this.vivaResponse = vivaOutput;
        this.isValid = vivaOutput==null || vivaOutput.trim().equals("");
    }
    
    /**
     * Returns the pattern which was used to generate this VivaPatternTestResult.
     * 
     * @return a String containing a Viva pattern.
     */
    public String getPattern() {
        return pattern;
    }
    
    /**
     * Returns an indication of whether the pattern used to generate this VivaPatternTestResult
     * is a valid VIVA pattern.
     * 
     * @return true if the pattern used to construct this VivaPatternTestResult is a valid VIVA pattern; false otherwise.
     */
    public boolean isValidPattern() {
        return isValid;
    }
    
    /**
     * Returns the message returned by VIVA in response to a request to test a pattern for validity.
     * Note that the response from VIVA to a valid pattern will be an empty message.
     * 
     * @return a String containing VIVA's response to a request to check a pattern for validity.
     */
    public String getVivaResponseMessage() {
        return vivaResponse;
    }

}
