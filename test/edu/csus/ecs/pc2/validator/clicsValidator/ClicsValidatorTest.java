// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.validator.clicsValidator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.StringTokenizer;

import org.junit.Test;

import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit tests for CLICS Validator
 * 
 * @author john@pc2.ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ClicsValidatorTest extends AbstractTestCase {

    private static final String PC2_JARNAME = "pc2.jar";

    public ClicsValidatorTest(String name) {
        super(name);
        //insure no "exitcode" file exists
        File runtimeErrorExitCodeFile = new File(ClicsValidator.PC2_RUNTIME_ERROR_EXITCODE_FILE_NAME);
        if (runtimeErrorExitCodeFile.exists()) {
            runtimeErrorExitCodeFile.delete();
        }
    }
    
    
    @Test
    public void testMissingJudgeDataFile() throws Exception {
        
        String dataDir = getDataDirectory() + File.separator;
        assertDirectoryExists(dataDir, "Missing data directory");
        
        String judgeDataFileName = dataDir + "defaultJudgeData.in";
        assertFileExists(judgeDataFileName, "judge's data file");
        
        String judgeAnswerFileName = dataDir + "defaultJudgeAnswer.ans";
        assertFileExists(judgeAnswerFileName, "judge's answer file");
        
        String feedbackDir = getOutputDataDirectory();
        assertDirectoryExists(feedbackDir, "feedback output directory");
        
        //the required files and directories exist; verify validator can construct an instance of itself
        ClicsValidator validator = new ClicsValidator(judgeDataFileName, judgeAnswerFileName, feedbackDir);
        assertNotNull(validator);
        
        //try constructing a validator with a non-existent judge's data file
        validator = null;
        judgeDataFileName = "foo";
        try {
            validator = new ClicsValidator(judgeDataFileName, judgeAnswerFileName, feedbackDir);
            fail("Validator failed to throw exception on construction when judge's data file is missing");
        } catch (RuntimeException e) {
            //throwing an exception is the correct behavior
//            System.out.println ("Validator correctly threw exception on construction");
            String expectedMessage = "ClicsValidator received invalid file or directory name(s)";
            assertEquals("Expected exception message", expectedMessage, e.getMessage());
        }
        assertNull("Validator failed to throw exception on construction when judge's data file '"
                + judgeDataFileName + "' is missing", validator);
        
    }
    
    @Test
    public void testMissingJudgeAnswerFile() throws Exception {
    
        String dataDir = getDataDirectory() + File.separator;
        assertDirectoryExists(dataDir, "Missing data directory");
        
        String judgeDataFileName = dataDir + "defaultJudgeData.in";
        assertFileExists(judgeDataFileName, "judge's data file");
        
        String judgeAnswerFileName = dataDir + "defaultJudgeAnswer.ans";
        assertFileExists(judgeAnswerFileName, "judge's answer file");
        
        String feedbackDir = getOutputDataDirectory();
        assertDirectoryExists(feedbackDir, "feedback output directory");
        
        //the required files and directories exist; verify validator can construct an instance of itself
        ClicsValidator validator = null ;
        try {
            validator = new ClicsValidator(judgeDataFileName, judgeAnswerFileName, feedbackDir);
        } catch (RuntimeException e) {
//            System.out.println ("Validator threw erroneous exception on construction");
            assertNotNull("Validator construction erroneously throws exception", validator);
        }
        assertNotNull(validator);
        
        //try constructing a validator with a non-existent judge's answer file        
        validator = null;
        judgeAnswerFileName = "foo";
        try {
            validator = new ClicsValidator(judgeDataFileName, judgeAnswerFileName, feedbackDir);
            fail("Validator failed to throw exception on construction when judge's answer file is missing");
        } catch (RuntimeException e) {
            //throwing an exception is the correct behavior
//            System.out.println ("Validator correctly threw exception on construction");
            String expectedMessage = "ClicsValidator received invalid file or directory name(s)";
            assertEquals("Expected exception message", expectedMessage, e.getMessage());
        }
        assertNull("Validator failed to throw exception on construction when judge's answer file '"
                + judgeAnswerFileName + "' is missing", validator);
    }
    
    @Test
    public void testMissingFeedbackDir() throws Exception {
        
        String dataDir = getDataDirectory() + File.separator;
        assertDirectoryExists(dataDir, "Missing data directory");
        
        String judgeDataFileName = dataDir + "defaultJudgeData.in";
        assertFileExists(judgeDataFileName, "judge's data file");
        
        String judgeAnswerFileName = dataDir + "defaultJudgeAnswer.ans";
        assertFileExists(judgeAnswerFileName, "judge's answer file");
        
        String feedbackDir = getOutputDataDirectory();
        assertDirectoryExists(feedbackDir, "feedback output directory");
        
        //the required files and directories exist; see if constructing the validator throws an exception on them
        ClicsValidator validator = null ;
        try {
            validator = new ClicsValidator(judgeDataFileName, judgeAnswerFileName, feedbackDir);
        } catch (Exception e) {
//            System.out.println ("Validator threw erroneous exception on construction");
            assertNotNull("Validator construction erroneously throws exception", validator);
        }
        assertNotNull(validator);
        
        //try constructing a validator with a non-existent judge's answer file        
        validator = null;
        feedbackDir = "foo";
        try {
            validator = new ClicsValidator(judgeDataFileName, judgeAnswerFileName, feedbackDir);
            fail("Validator failed to throw exception on construction when feedback directory is missing");
        } catch (RuntimeException e) {
            //throwing an exception is the correct behavior
//            System.out.println ("Validator correctly threw exception on construction");
            String expectedMessage = "ClicsValidator received invalid file or directory name(s)";
            assertEquals("Expected exception message", expectedMessage, e.getMessage());
        }
        assertNull("Validator failed to throw exception on construction when feedback directory '"
                + feedbackDir + "' is missing", validator);
    }
    
    /**
     * This tests whether the validator, run as a class instance, returns "success" when using the default option values
     * (non-case-sensitive, non-space-sensitive, no float tolerance specified) and a simple
     * team output file that exactly matches the judge's answer file.
     * 
     * @throws Exception
     */
    @Test
    public void testInstanceMatchWithNoOptions() throws Exception {

        String dataDir = getDataDirectory() + File.separator;
        assertDirectoryExists(dataDir, "Missing data directory");
        
        String judgeDataFileName = dataDir + "defaultJudgeData.in";
        assertFileExists(judgeDataFileName, "judge's data file");
        
        String judgeAnswerFileName = dataDir + "defaultJudgeAnswer.ans";
        assertFileExists(judgeAnswerFileName, "judge's answer file");
        
        String feedbackDir = getOutputDataDirectory();
        assertDirectoryExists(feedbackDir, "feedback output directory");
        
        String teamOutputFileName = dataDir + "defaultTeamOutput.out";
        assertFileExists(teamOutputFileName, "team output file");

        String [] options = {""};
        
        System.out.println();
        int retCode = runValidatorInstanced(judgeDataFileName, judgeAnswerFileName, feedbackDir, options, teamOutputFileName);
        System.out.println ("testInstanceMatchWithNoOptions: Validator returned: " + retCode);
        assertEquals(ClicsValidator.CLICS_VALIDATOR_JUDGED_RUN_SUCCESS_EXIT_CODE, retCode);
        
    }
    
    /**
     * This tests whether the validator, run as a class instance, correctly rejects output which differs in case, but only
     * when the "case_senstivity" option is selected.
     * 
     * @throws Exception
     */
    @Test
    public void testInstanceHandleCaseSensitivity() throws Exception {

        String dataDir = getDataDirectory() + File.separator;
        assertDirectoryExists(dataDir, "Missing data directory");
        
        String judgeDataFileName = dataDir + "defaultJudgeData.in";
        assertFileExists(judgeDataFileName, "judge's data file");
        
        String judgeAnswerFileName = dataDir + "defaultJudgeAnswer.ans";
        assertFileExists(judgeAnswerFileName, "judge's answer file");
        
        String feedbackDir = getOutputDataDirectory();
        assertDirectoryExists(feedbackDir, "feedback output directory");
        
        String teamOutputFileName = dataDir + "defaultTeamOutput.out";
        assertFileExists(teamOutputFileName, "team output file");

        String [] options = {""};
        
        System.out.println();
        int retCode = runValidatorInstanced(judgeDataFileName, judgeAnswerFileName, feedbackDir, options, teamOutputFileName);
        System.out.println ("testInstanceHandleCaseSensitivity: with no options, Validator returned: " + retCode);
        assertEquals(ClicsValidator.CLICS_VALIDATOR_JUDGED_RUN_SUCCESS_EXIT_CODE, retCode);
        
        options[0] = "case_sensitive";
        
        System.out.println();
        retCode = runValidatorInstanced(judgeDataFileName, judgeAnswerFileName, feedbackDir, options, teamOutputFileName);
        System.out.println ("testInstanceHandleCaseSensitivity: with 'case_sensitive' option, Validator returned: " + retCode);
        assertEquals(ClicsValidator.CLICS_VALIDATOR_JUDGED_RUN_FAILURE_EXIT_CODE, retCode);
        
    }

    /**
     * This tests whether the validator, run as a class instance, correctly rejects output which differs in 
     * various forms of spacing, but only when the "space_senstivity" option is selected.
     * 
     * @throws Exception
     */
    @Test
    public void testInstanceHandleSpaceSensitivity() throws Exception {

        String dataDir = getDataDirectory() + File.separator;
        assertDirectoryExists(dataDir, "Missing data directory");
        
        String judgeDataFileName = dataDir + "defaultJudgeData.in";
        assertFileExists(judgeDataFileName, "judge's data file");
        
        String judgeAnswerFileName = dataDir + "defaultJudgeAnswer.ans";
        assertFileExists(judgeAnswerFileName, "judge's answer file");
        
        String feedbackDir = getOutputDataDirectory();
        assertDirectoryExists(feedbackDir, "feedback output directory");
        
        String teamOutputFileName = dataDir + "teamOutputWithIncorrectSpacing1.out";
        assertFileExists(teamOutputFileName, "team output file");

        String [] options = {""};
        
        System.out.println();
        int retCode = runValidatorInstanced(judgeDataFileName, judgeAnswerFileName, feedbackDir, options, teamOutputFileName);
        System.out.println ("testInstanceHandleSpaceSensitivity: with no options, Validator returned: " + retCode);
        assertEquals(ClicsValidator.CLICS_VALIDATOR_JUDGED_RUN_SUCCESS_EXIT_CODE, retCode);
        
        options[0] = "space_change_sensitive";
        
        System.out.println();
        retCode = runValidatorInstanced(judgeDataFileName, judgeAnswerFileName, feedbackDir, options, teamOutputFileName);
        System.out.println ("testInstanceHandleSpaceSensitivity: with 'space_change_sensitive' option, Validator returned: " + retCode);
        assertEquals(ClicsValidator.CLICS_VALIDATOR_JUDGED_RUN_FAILURE_EXIT_CODE, retCode);
        
        teamOutputFileName = dataDir + "teamOutputWithIncorrectSpacing2.out";
        assertFileExists(teamOutputFileName, "team output file");
        
        System.out.println();
        retCode = runValidatorInstanced(judgeDataFileName, judgeAnswerFileName, feedbackDir, options, teamOutputFileName);
        System.out.println ("testInstanceHandleSpaceSensitivity: with 'space_change_sensitive' option, Validator returned: " + retCode);
        assertEquals(ClicsValidator.CLICS_VALIDATOR_JUDGED_RUN_FAILURE_EXIT_CODE, retCode);
        
        teamOutputFileName = dataDir + "teamOutputWithIncorrectSpacing3.out";
        assertFileExists(teamOutputFileName, "team output file");
        
        System.out.println();
        retCode = runValidatorInstanced(judgeDataFileName, judgeAnswerFileName, feedbackDir, options, teamOutputFileName);
        System.out.println ("testInstanceHandleSpaceSensitivity: with 'space_change_sensitive' option, Validator returned: " + retCode);
        assertEquals(ClicsValidator.CLICS_VALIDATOR_JUDGED_RUN_FAILURE_EXIT_CODE, retCode);
        
    }

    /**
     * This tests whether the validator, run as a class instance, correctly accepts float values within a specified absolute
     * tolerance but rejects float values which are not within the specified absolute tolerance.
     * 
     * @throws Exception
     */
    @Test
    public void testInstanceHandleFloatAbsoluteTolerance() throws Exception {

        String dataDir = getDataDirectory() + File.separator;
        assertDirectoryExists(dataDir, "Missing data directory");
        
        String judgeDataFileName = dataDir + "defaultJudgeData.in";
        assertFileExists(judgeDataFileName, "judge's data file");
        
        String judgeAnswerFileName = dataDir + "judgeAnswerWithFloat.ans";
        assertFileExists(judgeAnswerFileName, "judge's answer file");
        
        String feedbackDir = getOutputDataDirectory();
        assertDirectoryExists(feedbackDir, "feedback output directory");
        
        String teamOutputFileName = dataDir + "teamOutputWithFloatInAbsoluteTolerance.out";
        assertFileExists(teamOutputFileName, "team output file");

        String []options = {""};
        
        System.out.println();
        int retCode = runValidatorInstanced(judgeDataFileName, judgeAnswerFileName, feedbackDir, options, teamOutputFileName);
        System.out.println ("testInstanceHandleFloatAbsoluteTolerance: with no options, Validator returned: " + retCode);
        assertEquals(ClicsValidator.CLICS_VALIDATOR_JUDGED_RUN_FAILURE_EXIT_CODE, retCode);
        
        options = new String [2];
        options[0] = "float_absolute_tolerance";
        options[1] = "0.001";
        
        
        System.out.println();
        retCode = runValidatorInstanced(judgeDataFileName, judgeAnswerFileName, feedbackDir, options, teamOutputFileName);
        System.out.println ("testInstanceHandleFloatAbsoluteTolerance: with 'float_absolute_tolerance 0.001' option, Validator returned: " + retCode);
        assertEquals(ClicsValidator.CLICS_VALIDATOR_JUDGED_RUN_SUCCESS_EXIT_CODE, retCode);
        
        options[1] = "0.00001";
        
        System.out.println();
        retCode = runValidatorInstanced(judgeDataFileName, judgeAnswerFileName, feedbackDir, options, teamOutputFileName);
        System.out.println ("testInstanceHandleFloatAbsoluteTolerance: with 'float_absolute_tolerance 0.00001' option, Validator returned: " + retCode);
        assertEquals(ClicsValidator.CLICS_VALIDATOR_JUDGED_RUN_FAILURE_EXIT_CODE, retCode);
        
    }

    /**
     * This tests whether the validator, run as a class instance, correctly accepts float values within a specified relative
     * tolerance but rejects float values which are not within the specified relative tolerance.
     * 
     * @throws Exception
     */
    @Test
    public void testInstanceHandleFloatRelativeTolerance() throws Exception {

        String dataDir = getDataDirectory() + File.separator;
        assertDirectoryExists(dataDir, "Missing data directory");
        
        String judgeDataFileName = dataDir + "defaultJudgeData.in";
        assertFileExists(judgeDataFileName, "judge's data file");
        
        String judgeAnswerFileName = dataDir + "judgeAnswerWithFloatForRelativeTest.ans";
        assertFileExists(judgeAnswerFileName, "judge's answer file");
        
        String feedbackDir = getOutputDataDirectory();
        assertDirectoryExists(feedbackDir, "feedback output directory");
        
        String teamOutputFileName = dataDir + "teamOutputWithFloatForRelativeTest.out";
        assertFileExists(teamOutputFileName, "team output file");

        String []options = {""};
        
        System.out.println();
        int retCode = runValidatorInstanced(judgeDataFileName, judgeAnswerFileName, feedbackDir, options, teamOutputFileName);
        System.out.println ("testInstanceHandleFloatRelativeTolerance: with no options, Validator returned: " + retCode);
        assertEquals(ClicsValidator.CLICS_VALIDATOR_JUDGED_RUN_FAILURE_EXIT_CODE, retCode);
        
        options = new String [2];
        options[0] = "float_relative_tolerance";
        options[1] = "0.101"; //10.1% tolerance (should succeed)
        
        System.out.println();
        retCode = runValidatorInstanced(judgeDataFileName, judgeAnswerFileName, feedbackDir, options, teamOutputFileName);
        System.out.println ("testInstanceHandleFloatRelativeTolerance: with option 'float_relative_tolerance 0.1' (10%), Validator returned: " + retCode);
        assertEquals(ClicsValidator.CLICS_VALIDATOR_JUDGED_RUN_SUCCESS_EXIT_CODE, retCode);
        
        options[1] = "0.05";  //5% tolerance (should fail)
        
        System.out.println();
        retCode = runValidatorInstanced(judgeDataFileName, judgeAnswerFileName, feedbackDir, options, teamOutputFileName);
        System.out.println ("testInstanceHandleFloatRelativeTolerance: with option 'float_relative_tolerance 0.05' (5%), Validator returned: " + retCode);
        assertEquals(ClicsValidator.CLICS_VALIDATOR_JUDGED_RUN_FAILURE_EXIT_CODE, retCode);
        
    }

    /**
     * This tests whether the validator, run as a class instance, correctly accepts output with floating-point values
     * which differ in representation but not value (specifically, it should accept 0.341 as being equal to 3.14000000e-2).
     * 
     * @throws Exception
     */
    @Test
    public void testInstanceHandleScientificNotation() throws Exception {

        String dataDir = getDataDirectory() + File.separator;
        assertDirectoryExists(dataDir, "Missing data directory");
        
        String judgeDataFileName = dataDir + "defaultJudgeData.in";
        assertFileExists(judgeDataFileName, "judge's data file");
        
        String judgeAnswerFileName = dataDir + "judgeAnswerWithDecimalFloat.ans";
        assertFileExists(judgeAnswerFileName, "judge's answer file");
        
        String feedbackDir = getOutputDataDirectory();
        assertDirectoryExists(feedbackDir, "feedback output directory");
        
        String teamOutputFileName = dataDir + "teamOutputWithScientificFloat.out";
        assertFileExists(teamOutputFileName, "team output file");

        String []options = {""};
        
        System.out.println();
        int retCode = runValidatorInstanced(judgeDataFileName, judgeAnswerFileName, feedbackDir, options, teamOutputFileName);
        System.out.println ("testInstanceHandleScientificNotation: with no options, Validator returned: " + retCode);
        assertEquals(ClicsValidator.CLICS_VALIDATOR_JUDGED_RUN_FAILURE_EXIT_CODE, retCode);
        
        options = new String [2];
        options[0] = "float_absolute_tolerance";
        options[1] = "0.001";
        
        
        System.out.println();
        retCode = runValidatorInstanced(judgeDataFileName, judgeAnswerFileName, feedbackDir, options, teamOutputFileName);
        System.out.println ("testInstanceHandleScientificNotation: with 'float_absolute_tolerance 0.001' option, Validator returned: " + retCode);
        assertEquals(ClicsValidator.CLICS_VALIDATOR_JUDGED_RUN_SUCCESS_EXIT_CODE, retCode);
        
    }

    /**
     * This tests whether the validator, run as a class instance, correctly rejects team output which matches the judge's
     * data file but then has additional non-whitespace data.
     * 
     * @throws Exception
     */
    @Test
    public void testInstanceHandleTeamNonWhitespaceExcessiveOutput() throws Exception {

        String dataDir = getDataDirectory() + File.separator;
        assertDirectoryExists(dataDir, "Missing data directory");
        
        String judgeDataFileName = dataDir + "defaultJudgeData.in";
        assertFileExists(judgeDataFileName, "judge's data file");
        
        String judgeAnswerFileName = dataDir + "defaultJudgeAnswer.ans";
        assertFileExists(judgeAnswerFileName, "judge's answer file");
        
        String feedbackDir = getOutputDataDirectory();
        assertDirectoryExists(feedbackDir, "feedback output directory");
        
        String teamOutputFileName = dataDir + "teamOutputWithExcessiveOutput.out";
        assertFileExists(teamOutputFileName, "team output file");

        String []options = {""};
        
        System.out.println();
        int retCode = runValidatorInstanced(judgeDataFileName, judgeAnswerFileName, feedbackDir, options, teamOutputFileName);
        System.out.println ("testInstanceHandleTeamNonWhitespaceExcessiveOutput(): with no options, Validator returned: " + retCode);
        assertEquals(ClicsValidator.CLICS_VALIDATOR_JUDGED_RUN_FAILURE_EXIT_CODE, retCode);
        
        options = new String [1];
        options[0] = "space_change_sensitive";
            
        System.out.println();
        retCode = runValidatorInstanced(judgeDataFileName, judgeAnswerFileName, feedbackDir, options, teamOutputFileName);
        System.out.println ("testInstanceHandleTeamNonWhitespaceExcessiveOutput(): with 'space_change_sensitive' option, Validator returned: " + retCode);
        assertEquals(ClicsValidator.CLICS_VALIDATOR_JUDGED_RUN_FAILURE_EXIT_CODE, retCode);
                
    }

    /**
     * This tests whether the validator, run as a class instance, correctly accepts (in non-space-sensitive mode) team output which matches the judge's
     * data file but then has additional trailing whitespace, and also correctly rejects similar trailing whitespace output in space-sensitive mode.
     * 
     * @throws Exception
     */
    @Test
    public void testInstanceHandleTeamTrailingWhitespace() throws Exception {

        String dataDir = getDataDirectory() + File.separator;
        assertDirectoryExists(dataDir, "Missing data directory");
        
        String judgeDataFileName = dataDir + "defaultJudgeData.in";
        assertFileExists(judgeDataFileName, "judge's data file");
        
        String judgeAnswerFileName = dataDir + "defaultJudgeAnswer.ans";
        assertFileExists(judgeAnswerFileName, "judge's answer file");
        
        String feedbackDir = getOutputDataDirectory();
        assertDirectoryExists(feedbackDir, "feedback output directory");
        
        String teamOutputFileName = dataDir + "teamOutputWithExtraEOL.out";
        assertFileExists(teamOutputFileName, "team output file");

        String []options = {""};
        
        System.out.println();
        int retCode = runValidatorInstanced(judgeDataFileName, judgeAnswerFileName, feedbackDir, options, teamOutputFileName);
        System.out.println ("testInstanceHandleTeamTrailingWhitespace(): with no options and one extra EOL in team output, Validator returned: " + retCode);
        assertEquals(ClicsValidator.CLICS_VALIDATOR_JUDGED_RUN_SUCCESS_EXIT_CODE, retCode);
        
        options = new String [1];
        options[0] = "space_change_sensitive";
            
        System.out.println();
        retCode = runValidatorInstanced(judgeDataFileName, judgeAnswerFileName, feedbackDir, options, teamOutputFileName);
        System.out.println ("testInstanceHandleTeamTrailingWhitespace(): with 'space_change_sensitive' option and one extra EOL in team output, Validator returned: " + retCode);
        assertEquals(ClicsValidator.CLICS_VALIDATOR_JUDGED_RUN_FAILURE_EXIT_CODE, retCode);
        
        teamOutputFileName = dataDir + "teamOutputWithTwoExtraEOLs.out";
        assertFileExists(teamOutputFileName, "team output file");

        options = new String [1];
        options[0] = "";
        
        System.out.println();
        retCode = runValidatorInstanced(judgeDataFileName, judgeAnswerFileName, feedbackDir, options, teamOutputFileName);
        System.out.println ("testInstanceHandleTeamTrailingWhitespace(): with no options and two extra EOLs in team output, Validator returned: " + retCode);
        assertEquals(ClicsValidator.CLICS_VALIDATOR_JUDGED_RUN_SUCCESS_EXIT_CODE, retCode);
        
        options = new String [1];
        options[0] = "space_change_sensitive";
            
        System.out.println();
        retCode = runValidatorInstanced(judgeDataFileName, judgeAnswerFileName, feedbackDir, options, teamOutputFileName);
        System.out.println ("testInstanceHandleTeamTrailingWhitespace(): with 'space_change_sensitive' option and two extra EOLs in team output, Validator returned: " + retCode);
        assertEquals(ClicsValidator.CLICS_VALIDATOR_JUDGED_RUN_FAILURE_EXIT_CODE, retCode);
        
    }

    /**
     * This tests whether the validator, run as a class instance, correctly returns a "runtime error" result message when the team's program
     * has generated a runtime error (as indicated by the presence of an exit-code file), and also correctly returns "yes" if the team's
     * program did not generate a runtime error (as indicated by lack of an exit-code file). 
     * 
     * @throws Exception
     */
    @Test
    public void testInstanceHandleRuntimeError() throws Exception {

        String dataDir = getDataDirectory() + File.separator;
        assertDirectoryExists(dataDir, "Missing data directory");
        
        String judgeDataFileName = dataDir + "helloJudgeData.in";
        assertFileExists(judgeDataFileName, "judge's data file");
        
        String judgeAnswerFileName = dataDir + "helloJudgeAnswer.ans";
        assertFileExists(judgeAnswerFileName, "judge's answer file");
        
        String feedbackDir = getOutputDataDirectory();
        assertDirectoryExists(feedbackDir, "feedback output directory");
        
        String teamOutputFileName = dataDir + "teamOutputHelloCorrect.out";
        assertFileExists(teamOutputFileName, "team output file");

        String []options = {""};
        
        //insure there is no "exitcode" file present
        boolean exitCodeFileExisted = false;
        File runtimeErrorExitCodeFile = new File(ClicsValidator.PC2_RUNTIME_ERROR_EXITCODE_FILE_NAME);
        if (runtimeErrorExitCodeFile.exists()) {
            exitCodeFileExisted = true;  //record that there was already an exitcode file present
            runtimeErrorExitCodeFile.delete();
        }
        
        //validate under the condition that no runtime error has been flagged as having occurred
        System.out.println();
        int retCode = runValidatorInstanced(judgeDataFileName, judgeAnswerFileName, feedbackDir, options, teamOutputFileName);
        System.out.println ("testInstanceHandleRuntimeError(): with no runtime error indicated, Validator returned: " + retCode);
        assertEquals(ClicsValidator.CLICS_VALIDATOR_JUDGED_RUN_SUCCESS_EXIT_CODE, retCode);
        
        //add a "runtime error" file to the current directory
        runtimeErrorExitCodeFile = new File(ClicsValidator.PC2_RUNTIME_ERROR_EXITCODE_FILE_NAME);
        PrintWriter pw = new PrintWriter(runtimeErrorExitCodeFile);
        pw.println("0xFFEEDDCC");
        pw.flush();
        pw.close();
        
        assertFileExists(ClicsValidator.PC2_RUNTIME_ERROR_EXITCODE_FILE_NAME, "PC2 Exitcode file doesn't exist");
        
        //validate under the condition that a runtime error has been flagged as having occurred
        System.out.println();
        retCode = runValidatorInstanced(judgeDataFileName, judgeAnswerFileName, feedbackDir, options, teamOutputFileName);
        System.out.println ("testInstanceHandleRuntimeError(): when a runtime error during execution was indicated, Validator returned: " + retCode);
        assertEquals(ClicsValidator.CLICS_VALIDATOR_JUDGED_RUN_FAILURE_EXIT_CODE, retCode);
        
        //read the feedback response generated by the CLICS Validator due to the runtime error
        String feedbackFileName = feedbackDir + File.separator + ClicsValidator.CLICS_JUDGEMENT_FEEDBACK_FILE_NAME ;
        assertFileExists(feedbackFileName, "Feedback file '" + feedbackFileName + "' not found");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(feedbackFileName)));
        String feedbackResponse = bufferedReader.readLine();
        System.out.println("testInstanceHandleRuntimeError(): feedback message from Clics Validator when a runtime error occurred: " + feedbackResponse);
        bufferedReader.close();
        
        //read the "additional details" returned by the Clics Validator
        String detailsFileName = feedbackDir + File.separator + ClicsValidator.CLICS_JUDGEMENT_DETAILS_FEEDBACK_FILE_NAME ;
        assertFileExists(detailsFileName, "Feedback details file '" + detailsFileName + "' not found");
        BufferedReader br = new BufferedReader(new FileReader(new File(detailsFileName)));
        String feedbackDetails = br.readLine();
        System.out.println("testInstanceHandleRuntimeError(): feedback details from Clics Validator when a runtime error occurred: " + feedbackDetails);
        br.close();
        
        //clean up: if there wasn't already an exitcode file present, remove the one we added
        if (!exitCodeFileExisted) {
            runtimeErrorExitCodeFile = new File(ClicsValidator.PC2_RUNTIME_ERROR_EXITCODE_FILE_NAME);
            if (runtimeErrorExitCodeFile.exists()) {
                runtimeErrorExitCodeFile.delete();
            }
        }
        
    }

    //additional tests needed:
    //  testInstanceHandleWithinRelativeButNotAbsoluteTolerance()
    //  testInstanceHandleWithinAbsoluteButNotRelativeTolerance()
    //  testInstanceHandleSetBothTolerancesWithOneOption()
    //  testInstanceHandleCaseSensitiveAndSpaceSensitiveOptionsTogetherInThatOrder()
    //  testInstanceHandleSpaceSensitiveAndCaseSensitiveOptionsTogetherInThatOrder()

    // testProcessHandleXXX()
   
    
    //sample suggested code for examining feedback directory files:
    
//    String [] feedbackFilenames = getFileEntries (inFeedbackDirName);
//    
//    System.out.println("There were "+feedbackFilenames.length+" feedback files under "+inFeedbackDirName);
//    for (String filename : feedbackFilenames) {
//        System.out.println("File: "+filename);
//        Utilities.catFile(new PrintWriter(System.out), filename);
//    }
//    

//
///**
// * Get file directory entries with full pathnames.
// *  
// * @param directory
// * @return
// */
//private String  []  getFileEntries(String directory) {
//    ArrayList<String> list = new ArrayList<>();
//    File[] files = new File(directory).listFiles();
//
//    for (File file : files) {
//        if (file.isFile()){
//            list.add(directory + file.separator + file.getName());
//        }
//    }
//    return (String[]) list.toArray(new String[list.size()]);
//
//}



    /**
     * Run the CLICS validator as a class instance. 
     */
    private int runValidatorInstanced(String inJudgeDataFile, String inJudgeAnswerFile, String inFeedbackDir, String[] inOptions, String inTeamOutputFile) {

        ClicsValidator validator = new ClicsValidator(inJudgeDataFile, inJudgeAnswerFile, inFeedbackDir, inOptions);

//        FileInputStream judgeAnswer = null;
//        try {
//            judgeAnswer = new FileInputStream(new File(inJudgeAnswerFile));
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        
        //connect the team's output to stdin
        FileInputStream teamOutput = null;
        try {
            teamOutput = new FileInputStream(new File(inTeamOutputFile));
            System.setIn(teamOutput);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int returnCode = validator.validate();

        return returnCode;

    }
    
//    /**
//     * Run the CLICS Validator as an external program, passing it the specified arguments
//     * on its command line and sending the specified teamOutputFileName to the standard
//     * input of the validator, and returning the exit code specified by the validator.
//     * 
//     * @param args - the command line arguments to be passed to the validator, including at least
//     *              the judge's data file name, judge's answer file name, and feedback directory name,
//     *              and also including any validator options (such as "case_sensitive"), all 
//     *              separated by spaces
//     * @param teamOutputFileName - the name of the file to be routed to the stdin of the validator
//     * 
//     * @return an int containing the validator process's exit code
//     */
//    private int runValidatorProcess (String judgeDataFile, String judgeAnswerFile, String feedbackDir, String teamOutputFileName) {
//        
//      String pathToPC2Jar = findPC2JarPath();
//      assertFileExists (pathToPC2Jar + PC2_JARNAME, "PC2 system jar");
//      
//      String [] tokens = args.split(" ");
//      boolean sufficientTokens = tokens.length>=3;
//      assertTrue("Insufficient arguments", sufficientTokens);
//      
//      for (int i =0; i<3; i++) {
//          assertFileExists(tokens[i]);
//      }
//      
//      String cmdLine = "java -cp " + pathToPC2Jar + " ClicsValidator " + args;
//
//      System.out.println("DEBUG: Starting validator process with command: '" + cmdLine + "'");
//
//           
//           long startSecs = System.currentTimeMillis();
//           Process process = runProgram(cmdLine, "Executing...");
//           if (process == null) {
//               return ClicsValidator.CLICS_VALIDATOR_ERROR_EXIT_CODE;
//           }
//
//           // This reads from the stdout of the child process
//           BufferedInputStream childOutput = new BufferedInputStream(process.getInputStream());
//           // The reads from the stderr of the child process
//           BufferedInputStream childError = new BufferedInputStream(process.getErrorStream());
//
//           IOCollector stdoutCollector = new IOCollector(log, childOutput, stdoutlog, null, getMaxFileSize() + ERRORLENGTH);
//           IOCollector stderrCollector = new IOCollector(log, childError, stderrlog, null, getMaxFileSize() + ERRORLENGTH);
//
//           stdoutCollector.start();
//           stderrCollector.start();
//
//           if ( inputDataFileName != null && problem.isReadInputDataFromSTDIN()) {
//               log.info("Using STDIN from file " +inputDataFileName);
//                   
//               BufferedOutputStream out = new BufferedOutputStream(process.getOutputStream());
//               BufferedInputStream in = new BufferedInputStream(new FileInputStream(inputDataFileName));
//               byte[] buf = new byte[32768];
//               int c;
//               try {
//                   while ((c = in.read(buf))!= -1) {
//                       out.write(buf, 0, c);
//                   }
//               } catch (java.io.IOException e) {
//                   log.info("Caught a "+e.getMessage()+" do not be alarmed.");
//               }
//
//               in.close();
//               out.close();
//           }
//
//           stdoutCollector.join();
//           stderrCollector.join();
//
//           if (process != null) {
//               process.destroy();
//           }
//
//        
//      
//      int exitVal = process.waitFor();
//      exitVal = process.exitValue();
//      
//      if (process.exitValue() != ClicsValidator.CLICS_VALIDATOR_SUCCESS_EXIT_CODE) {
//          /**
//           * Write out validator output
//           */
//          dumpSerializedFile(System.out, executable.getExecutionData().getValidationStdout());
//          dumpSerializedFile(System.out, executable.getExecutionData().getValidationStderr());
//      }
//      
//      String message = executable.getRunProgramErrorMessage();
//      assertNotNull("Expecting process to be created: " + message, process);
// 
//      assertEquals(ClicsValidator.CLICS_VALIDATOR_SUCCESS_EXIT_CODE, process.exitValue());
//      assertEquals(ClicsValidator.CLICS_VALIDATOR_SUCCESS_EXIT_CODE, exitVal);
//
//    }
//    
//    public Process runProgram(String cmdline, String msg) {
//        Process process = null;
//        errorString = "";
//        
//        executeDirectoryName = getExecuteDirectoryName();
//        
//        try {
//            File runDir = new File(executeDirectoryName);
//            if (runDir.isDirectory()) {
//                log.config("executing: '" + cmdline + "'");
//                
//                String[] env = null;
//
//                if (executionTimer != null) {
//                    executionTimer.setDoAutoStop(autoStopExecution);
//                    executionTimer.setTitle(msg);
//                }
//
//                process = Runtime.getRuntime().exec(cmdline, env, runDir);
//
//                // if(isJudge && executionTimer != null) {
//                if (executionTimer != null) {
//                    executionTimer.setProc(process);
//                    executionTimer.startTimer();
//                }
//
//            } else {
//                errorString = "Execute Directory does not exist";
//                log.config("Execute Directory does not exist");
//            }
//        } catch (IOException e) {
//            errorString = e.getMessage();
//            log.config("Note: exec failed in RunProgram " + errorString);
//            executionData.setExecutionException(e);
//            return null;
//        } catch (Exception e) {
//            errorString = e.getMessage();
//            log.log(Log.CONFIG, "Note: exec failed in RunProgram " + errorString, e);
//            executionData.setExecutionException(e);
//            return null;
//        }
//
//        return process;
//    }

    // old code
//    public static TestSuite suite() {
//
//        TestSuite suite = new TestSuite();
//        
//        suite.addTest(new ClicsValidatorTest("testMissingJudgeDataFile"));
//        suite.addTest(new ClicsValidatorTest("testMissingJudgeAnswerFile"));
//        suite.addTest(new ClicsValidatorTest("testMissingFeedbackDir"));
//        suite.addTest(new ClicsValidatorTest("testInstanceMatchWithNoOptions"));
//        suite.addTest(new ClicsValidatorTest("testInstanceHandleCaseSensitivity"));        
//        suite.addTest(new ClicsValidatorTest("testInstanceHandleSpaceSensitivity"));        
//        suite.addTest(new ClicsValidatorTest("testInstanceHandleFloatAbsoluteTolerance"));        
//        suite.addTest(new ClicsValidatorTest("testInstanceHandleScientificNotation"));        
//        suite.addTest(new ClicsValidatorTest("testInstanceHandleFloatRelativeTolerance"));        
//        suite.addTest(new ClicsValidatorTest("testInstanceHandleTeamTrailingWhitespace"));        
//        suite.addTest(new ClicsValidatorTest("testInstanceHandleTeamNonWhitespaceExcessiveOutput"));        
//        suite.addTest(new ClicsValidatorTest("testInstanceHandleRuntimeError"));        
//
//        return suite;
//    }
    
    
    
    protected String findPC2JarPath() {
        
        String jarDir = ".." + File.separator + ".classes" + File.pathSeparator; // default to ..\.classes (eclipse) directory

        try {
            String name = "dist";
            File dir = new File(name);
            if (dir.exists()) {
                jarDir = dir.getCanonicalPath();
            }
        } catch (IOException e) {
            System.err.println("Trouble locating pc2home: " + e.getMessage());
        }

        try {
            String cp = System.getProperty("java.class.path");
            StringTokenizer st = new StringTokenizer(cp, File.pathSeparator);
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                File dir = new File(token);
                if (dir.exists() && dir.isFile() && dir.toString().endsWith(PC2_JARNAME)) {
                    jarDir = new File(dir.getParent()).getCanonicalPath();
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Trouble locating pc2home: " + e.getMessage());
        }
        
        return jarDir + File.separator;
    }
    
    
//    // SOMEDAY fix testValidatorSuccess
//    public void atestValidatorSuccess() throws Exception {
//        
//        String answerFilename = getSamplesSourceFilename("sumit.ans");
//        String outFilename = answerFilename;
//        String inputFilename = getSamplesSourceFilename("sumit.dat");
//        
//        
//        String[] args = { inputFilename, answerFilename };
//        
//        int code = runValidator (outFilename, args);
//        
//        assertEquals(CCSConstants.VALIDATOR_JUDGED_SUCCESS_EXIT_CODE, code);
//        
//        
//    }
//    
    
    /**
     * Run CLICS validator.
     * 
     * This runs a using the same command line args as would be used if one were
     * running the validator on a command line.
     *
     * @param teamsOutputFilename
     * @param args, array with judgeDataFile, judgeAnswerFile, feedbackDirName, (list of options)
     * @return return code for validator, {@value ClicsValidator#CLICS_VALIDATOR_JUDGED_RUN_FAILURE_EXIT_CODE} or
     * {@value ClicsValidator#CLICS_VALIDATOR_JUDGED_RUN_FAILURE_EXIT_CODE}
     */
    private int runValidator(String teamsOutputFilename, String[] args) throws FileNotFoundException {

        String inJudgeDataFile = args[0];
        String inJudgeAnswerFile = args[1];
        String inFeedbackDirName = args[2];
        String[] options = new String[0];

        if (args.length > 3) {
            options = Arrays.copyOfRange(args, 3, args.length);
        }
        
        return  runValidatorInstanced(inJudgeDataFile, inJudgeAnswerFile, inFeedbackDirName, options, teamsOutputFilename);
    }

    // TODO CCS add this test back
//    /**
//     * Test run the validator.
//     * 
//     * @throws Exception
//     */
//    public void testRunValidator() throws Exception {
//
//        SampleContest sample = new SampleContest();
//        IInternalContest contest = sample.createContest(1, 1, 12, 12, true);
//
//        IInternalController controller = new NullController(this.getName());
//
//        Problem problem = contest.getProblems()[0];
//        
//        addProblemDataset (contest, problem, "sumit.in", "sumit.ans" );
//
//        sample.setCCSValidation(contest, "--testYes", problem);
//        sample.setCCSValidation(contest, CCSConstants.DEFAULT_CCS_VALIDATOR_COMMAND, problem);
//        sample.setCCSValidation(contest, "-verbose sumit.in sumit.ans", problem);
//
//        ClientId clientId = contest.getAccounts(ClientType.Type.TEAM).firstElement().getClientId();
//        Run run = sample.createRun(contest, clientId, problem);
//
//        String sourcefilename = sample.createSampleSumitStdinSource("ISumit.java");
//        
//        assertFileExists(sourcefilename, "Missing source file");
//        
//        RunFiles runFiles = new RunFiles(run, sourcefilename);
//        contest.acceptRun(run, runFiles);
//        
//        Executable executable = new Executable(contest, controller, run, runFiles);
//
//        // create execute directory
//        String executeDirectoryName = getDataDirectory() + File.separator + "execute";
//        new File(executeDirectoryName).mkdirs();
//        
//        assertTrue("Execute dir does not exist " + executeDirectoryName, new File(executeDirectoryName).isDirectory());
//
//        executable.setExecuteDirectoryName(executeDirectoryName);
//
//        String pathToPC2Jar = findPC2JarPath();
//        
//        assertFileExists (pathToPC2Jar + PC2_JARNAME, "system jar");
//        
//        String commandPattern = "java -cp " + pathToPC2Jar + problem.getValidatorCommandLine();
//
//        System.out.println("debug Started process with: "+commandPattern);
//        
//        Process process = executable.runProgram(commandPattern, null, false);
//        
////        int exitVal = process.waitFor();
////        exitVal = process.exitValue();
//        
////        if (process.exitValue() != CCSConstants.VALIDATOR_JUDGED_SUCCESS_EXIT_CODE) {
////            /**
////             * Write out validator output
////             */
////            dumpSerializedFile(System.out, executable.getExecutionData().getValidationStdout());
////            dumpSerializedFile(System.out, executable.getExecutionData().getValidationStderr());
////        }
//        
////        String message = executable.getRunProgramErrorMessage();
////        assertNotNull("Expecting process to be created: " + message, process);
//   
//        assertEquals(CCSConstants.VALIDATOR_JUDGED_SUCCESS_EXIT_CODE, process.exitValue());
////        assertEquals(CCSConstants.VALIDATOR_JUDGED_SUCCESS_EXIT_CODE, exitVal);
//    }
//
////    private void dumpSerializedFile(PrintStream out, SerializedFile serializedFile) {
////        out.println("File: "+serializedFile.getName());
////        out.println(serializedFile.getBuffer());
////    }

//    /**
//     * Creates files and adds problem data set per problem to contest.
//     * 
//     * @param contest
//     * @param problem
//     * @param datafilename
//     * @param answerfilename
//     * @throws FileNotFoundException 
//     */
//    private void addProblemDataset(IInternalContest contest, Problem problem, String datafilename , String answerfilename) throws FileNotFoundException {
//        
//        problem.setDataFileName(datafilename);
//        problem.setAnswerFileName(answerfilename);
//        
//        ProblemDataFiles files = new ProblemDataFiles(problem);
//        
//        SampleContest sample = new SampleContest();
//        
//        String name1 = sample.createSampleAnswerFile(answerfilename);
//        String name2 = sample.createSampleDataFile(datafilename);
//        
//        files.setJudgesAnswerFile(new SerializedFile(name1));
//        files.setJudgesDataFile(new SerializedFile(name2));
//
//        contest.updateProblem(problem, files);
//    }
    

    /**
     * Test for file missing LF at end of file with no space_change_sensitive setting/set.
     * 
     * Bug 1363
     * @throws Exception
     */
    public void testNoLFAtEndCompare() throws Exception {
        
        String dataDir = getDataDirectory(this.getName());
//        ensureDirectory(dataDir);
//        startExplorer(dataDir);

        String outDir = getOutputDataDirectory(this.getName());
        removeDirectory(outDir);

        ensureDirectory(outDir);

        String feedbackDir = outDir + File.separator + "feedback";
        ensureDirectory(feedbackDir);

        /**
         * Team output has no newline.
         */
        String teamsOutputDirectory = dataDir + File.separator + "nonewline.txt";

        String judgesAnswerFile = dataDir + File.separator + "newline.txt";
        
        
        String judgesDataFile = judgesAnswerFile;

        String[] args = {
                //
                judgesDataFile, //
                judgesAnswerFile, //
                feedbackDir, //
        };

        int returnCode = runValidator(teamsOutputDirectory, args);
        /**
         * Test whether the validator ignored newline at end of file
         */
        assertEquals("Expecting validator return code ", ClicsValidator.CLICS_VALIDATOR_JUDGED_RUN_SUCCESS_EXIT_CODE, returnCode);
    }
    
    /**
     * Test for file missing LF at end of file, with space_change_sensitive.
     * 
     * Bug 1363
     * 
     * @throws Exception
     */
    public void testNOEOLNSpaceChangeSensitive() throws Exception {
        
        String dataDir = getDataDirectory(this.getName());
        ensureDirectory(dataDir);
//        startExplorer(dataDir);

        String outDir = getOutputDataDirectory(this.getName());
        removeDirectory(outDir);

        ensureDirectory(outDir);

        String feedbackDir = outDir + File.separator + "feedback";
        ensureDirectory(feedbackDir);

        /**
         * Team output has no newline.
         */
        String teamsOutputDirectory = dataDir + File.separator + "nonewline.txt";

        String judgesAnswerFile = dataDir + File.separator + "newline.txt";
        
        
        String judgesDataFile = judgesAnswerFile;

        String[] args = {
                //
                judgesDataFile, //
                judgesAnswerFile, //
                feedbackDir, //
                "space_change_sensitive",
                
        };

        int returnCode = runValidator(teamsOutputDirectory, args);
        
        
        /**
         * Test whether the validator dit NOT ignore newline at end of file
         */
        assertEquals("Expecting validator return code ", ClicsValidator.CLICS_VALIDATOR_JUDGED_RUN_FAILURE_EXIT_CODE, returnCode);
    }

}
