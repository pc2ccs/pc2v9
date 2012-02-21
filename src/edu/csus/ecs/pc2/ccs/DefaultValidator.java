package edu.csus.ecs.pc2.ccs;

/**
 * Default CCS Validator.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class DefaultValidator {

    public DefaultValidator() {

    }

    int validateFile(String infilename, String answerFilename, String outputDirectoryName) {

        int returnValue = CCSConstants.VALIDATOR_JUDGED_FAILURE_EXIT_CODE;

        // TODO CCS code validate file

        return returnValue;
    }

    // public static final String DEFAULT_CCS_VALIDATOR_COMMAND = "{:validator} {:infile} {:ansfile} {:resultsdir} ";

    /**
     * Execute validator.
     * 
     * Arguments: infile answerfile resultdirectory
     * 
     * 
     * @param args
     */
    public static void main(String[] args) {

        DefaultValidator defaultValidator = new DefaultValidator();

        // TODO CCS Parse validator command line

        /**
         * Team's output files
         */
        String infilename = "foo";

        /**
         * Judge's answer file
         */
        String answerFilename = "foo";
        /**
         * Results directory name
         */
        String outputDirectoryName = "foo";

        System.exit(defaultValidator.validateFile(infilename, answerFilename, outputDirectoryName));
    }

}
