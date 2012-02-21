package edu.csus.ecs.pc2.ccs;

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
     * The default CCS validator command line.
     * 
     * <pre>Command line: validator input judgeanswer feedbackdir < teamoutput </pre>
     * 
     * Assumes that input is from stdin and output is to stdout.
     */
    public static final String DEFAULT_CCS_VALIDATOR_COMMAND = "{:validator} {:infile} {:ansfile} {:resultsdir} ";
    
    
    /**
     * The default CCS validator program name. 
     */
    public static final String INTERNAL_CCS_VALIDATOR_NAME = "pc2.jar edu.csus.ecs.pc2.ccs.DefaultValidator";



}
