package edu.csus.ecs.pc2.api;

import edu.csus.ecs.pc2.core.Constants;

/**
 * Constants used in the API.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public final class APIConstants {

    // --------- name/keys ---------------
    
    /**
     * 
     */
    private APIConstants() {
        super();
    }

    /**
     * Problem Judging Type
     */
    public static final String JUDGING_TYPE = "JUDGING_TYPE";

    /**
     * Validator command line
     */
    public static final String VALIDATOR_COMMAND_LINE = "VALIDATOR_COMMAND_LINE";

    /**
     * Validator program name.
     */
    public static final String VALIDATOR_PROGRAM = "VALIDATOR_PROGRAM";
    
    
    // --------- Values ---------------

    /**
     * Judging Type value.
     */
    public static final String MANUAL_JUDGING_ONLY = "MANUAL_JUDGING_ONLY";

    /**
     * Judging Type value.
     */
    public static final String COMPUTER_JUDGING_ONLY = "COMPUTER_JUDGING_ONLY";

    /**
     * Judging Type value.
     */
    public static final String COMPUTER_AND_MANUAL_JUDGING = "COMPUTER_AND_MANUAL_JUDGING";

    /**
     * Default validator command line value.
     */
    public static final String DEFAULT_INTERNATIONAL_VALIDATOR_COMMAND = "{:validator} {:infile} {:outfile} {:ansfile} {:resfile} ";

    /**
     * PC^2 Validator program value.
     */
    public static final String PC2_VALIDATOR_PROGRAM = Constants.PC2_VALIDATOR_NAME;

}
