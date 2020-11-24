package edu.csus.ecs.pc2.validator.inputValidator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.HashMap;

import javax.swing.JOptionPane;

import org.vanb.viva.VIVA;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.SerializedFile;

/**
 * This class acts as a wrapper for the VIVA Input Validator class defined in the external JAR file viva.jar.
 * 
 * It provides methods for clients to check whether pattern strings are valid VIVA patterns, and to test
 * files to see if they conform to specified VIVA patterns.
 * 
 * @author John Clevenger, PC2 Development Team (pc2@ecs.csus.edu)
 *
 */
public class VivaAdapter {
    
    private VIVA vivaInstance = new VIVA();
    
    //a map which tracks previously-tested Viva patterns and the result of testing those patterns for validity
    private HashMap<String,VivaPatternTestResult> knownPatterns = new HashMap<String,VivaPatternTestResult>();

    private IInternalController controller;
        
    /**
     * Constructs a VivaAdapter -- a class which provides an interface to the external VIVA Input Validator.
     * 
     * @param controller - the {@link IInternalController} which this VivaAdapter will use for logging.
     */
    public VivaAdapter (IInternalController controller) {
        this.controller = controller;
    }
    
    /**
     * Checks the specified pattern to see if it is a valid VIVA pattern.
     * Clients can determine whether the specified pattern is valid by checking the value of
     * {@link VivaPatternTestResult#isValidPattern()} in the returned {@link VivaPatternTestResult}.
     * 
     * @param pattern the pattern string to be checked.
     * 
     * @return a {@link VivaPatternTestResult} containing the result of checking the specified VIVA pattern for validity.
     */
    public VivaPatternTestResult checkPattern(String pattern) {
        
        //check to see if we've previously tested this exact pattern
        if (knownPatterns.containsKey(pattern)) {
            //yes; return the result of the previous test
            return knownPatterns.get(pattern);
            
        } else {
            //no; test the pattern, save the test result, and return the test result

            //instruct VIVA to send its output back to here (instead of the default which is stdout)
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream vivaOutputStream = new PrintStream(baos);
            vivaInstance.setOutputStream(vivaOutputStream);

            //send the pattern to VIVA as an InputStream
            InputStream patternAsInputStream = new ByteArrayInputStream(pattern.getBytes(Charset.forName("UTF-8")));
            vivaInstance.setPattern(patternAsInputStream);

            // read the VIVA response
            String vivaResponse = baos.toString();

            //construct a new VivaPatternTestResult and fill in its fields based on the value of "vivaResponse"
            VivaPatternTestResult result = new VivaPatternTestResult(pattern, vivaResponse);
        
            //save the result so we can return it in response to any future requests about the same pattern
            knownPatterns.put(pattern, result);
            
            //send back the result
            return result;
        } 
    }
    
    /**
     * Tests the specified {@link SerializedFile} to see if it is a valid data file according to the specified
     * VIVA pattern string.  Clients can find the result of the test by checking method {@link VivaDataFileTestResult#passed()}
     * in the returned {@link VivaDataFileTestResult} object.
     * 
     * @param pattern a String containing a VIVA pattern.
     * @param datafile a SerializedFile to be tested for conformance with the specified VIVA pattern.
     * 
     * @return an {@link VivaDataFileTestResult} containing the result of testing the specified data file using
     *              the specified VIVA pattern string, or null if the pattern is not a valid VIVA pattern.
     *              
     * 
     */
    public VivaDataFileTestResult testFile (String pattern, SerializedFile datafile) {
        
        //first, test the pattern to make sure it's valid
        VivaPatternTestResult patternTestResult = checkPattern(pattern);
        if (!patternTestResult.isValidPattern()) {
            String msg = "Cannot test data files; VIVA reports the specified pattern is invalid:" 
                    + "\n\n" + patternTestResult.getVivaResponseMessage();
            JOptionPane.showMessageDialog(null, msg, "Invalid Pattern", JOptionPane.WARNING_MESSAGE);
            
            //return a VivaDataFileTestResult indicating that the test failed because of an error (the pattern was invalid)
            String vivaMsg = "Invalid VIVA Pattern";
            byte [] b = vivaMsg.getBytes();
            SerializedFile vivaOutput = new SerializedFile("Viva Output", b);
            VivaDataFileTestResult result = new VivaDataFileTestResult(vivaOutput, false, Problem.InputValidationStatus.ERROR, pattern, datafile);
            return result;
        }
        
        //pattern is ok; test datafile using VIVA
        
        //instruct VIVA to send its output back to here (instead of the default which is stdout)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream vivaOutputStream = new PrintStream(baos);
        vivaInstance.setOutputStream(vivaOutputStream);
         
        //send the pattern to VIVA as an InputStream
        InputStream patternAsInputStream = new ByteArrayInputStream(pattern.getBytes(Charset.forName("UTF-8")));
        vivaInstance.setPattern(patternAsInputStream);
        
        // tell VIVA to test the data file using the previously specified pattern
        boolean passed = vivaInstance.testInputFile(datafile.getAbsolutePath());
        
        //read VIVA output stream, convert it to a SerializedFile
        SerializedFile vivaOutput = new SerializedFile("VivaStdout", baos.toByteArray());
        
        //make sure no errors/exceptions occurred during SerializedFile construction
        try {
            if (Utilities.serializedFileError(vivaOutput)) {
                //if we get here, an error occurred while constructing the VIVA output SerializedFile
                getController().getLog().severe("Internal error converting VIVA output to SerializedFile: " + vivaOutput.getErrorMessage());
                String msg = "Cannot test data files; internal error converting VIVA output to SerializedFile." 
                        + "\nPlease check logs and report this error to the PC2 Development Team (pc2@ecs.csus.edu).";
                JOptionPane.showMessageDialog(null, msg, "Internal Error", JOptionPane.ERROR_MESSAGE);
                
               //return a VivaDataFileTestResult indicating that the test failed because of an error
                VivaDataFileTestResult result = new VivaDataFileTestResult(vivaOutput, false, Problem.InputValidationStatus.ERROR, pattern, datafile);
                return result;
            }
        } catch (Exception e) {
            //if we get here, an exception was thrown while constructing the VIVA output SerializedFile (and re-thrown by Utilities.serializedFileError)
            getController().getLog().severe("Exception while converting VIVA output to SerializedFile: " + e.getMessage());
            String msg = "Cannot test data files; exception while converting VIVA output to SerializedFile: " + e.getMessage()
                    + "\nPlease check logs and report this error to the PC2 Development Team (pc2@ecs.csus.edu).";
            JOptionPane.showMessageDialog(null, msg, "Internal Error", JOptionPane.ERROR_MESSAGE);
            
            //return a VivaDataFileTestResult indicating that the test failed because of an error
            VivaDataFileTestResult result = new VivaDataFileTestResult(vivaOutput, false, Problem.InputValidationStatus.ERROR, pattern, datafile);
            return result;
        }
        
        // construct new VivaDataFileTestResult containing the Serialized Files, pass/fail flag, and a non-error status
        Problem.InputValidationStatus status = passed ? Problem.InputValidationStatus.PASSED : Problem.InputValidationStatus.FAILED;
        VivaDataFileTestResult result = new VivaDataFileTestResult(vivaOutput, passed, status, pattern, datafile);
        
        return result;
    }

    private IInternalController getController() {
        return controller;
    }


}
