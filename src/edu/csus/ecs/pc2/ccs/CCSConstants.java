package edu.csus.ecs.pc2.ccs;

import edu.csus.ecs.pc2.core.Constants;

/**
 * Contest Control System (Standard) constants.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public final class CCSConstants {

    private CCSConstants() {

    }

    /**
     * Validator success exit code.
     */
    public static final int VALIDATOR_JUDGED_SUCCESS_EXIT_CODE = 42;

    /**
     * Validator failure exit code.
     */
    public static final int VALIDATOR_JUDGED_FAILURE_EXIT_CODE = 43;
    
    /**
     * An error during CCS judging/validating exit code.
     * 
     * {@link #VALIDATOR_JUDGED_SUCCESS_EXIT_CODE} and {@link #VALIDATOR_JUDGED_FAILURE_EXIT_CODE} are
     * valid return codes.
     */
    public static final int VALIDATOR_CCS_ERROR_EXIT_CODE = 255;
    
    /**
     * The default CCS validator command line.
     * 
     * <pre>Command line: validator input judgeanswer feedbackdir &lt; teamoutput </pre>
     * 
     * Assumes that input is from stdin and output is to stdout.
     */
    public static final String DEFAULT_CCS_VALIDATOR_COMMAND = Constants.DEFAULT_CLICS_VALIDATOR_COMMAND;
    
    
    /**
     * The default CCS validator program name. 
     */
    public static final String INTERNAL_CCS_VALIDATOR_NAME = Constants.CLICS_VALIDATOR_NAME;

    /**
     * Wrong answer judgment string.
     */
    public static final String JUDGEMENT_WRONG_ANSWER = "No - Wrong Answer";

    /**
     * Accepted/passed (Yes) judgement string.
     */
    public static final String JUDGEMENT_YES = "Yes";


}
