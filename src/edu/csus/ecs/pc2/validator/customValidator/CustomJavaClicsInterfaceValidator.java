/**
 * 
 */
package edu.csus.ecs.pc2.validator.customValidator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.validator.clicsValidator.ClicsValidator;
import edu.csus.ecs.pc2.validator.clicsValidator.ClicsValidatorSettings;

/**
 * This class implements a Custom Validator using the "CLICS Validator Interface".
 * It is based on the PC2 internal implementation of the Clics Validator.
 * <P>
 * The Validator can be invoked in one of two ways: as a stand-alone main program, or by instantiating
 * the class and invoking the {@link #validate()} method. If invoked as a stand-alone program the validator
 * exits with Exit Code 42 if the team output matches the judge's answer, or with Exit Code 43 if not
 * (these values are specified by the ICPC CLICS Output Validator specification).
 * <P>
 * If invoked by instantiation and then calling {@link CustomJavaClicsInterfaceValidator#validate()}, the {@link #validate()}
 * method returns these same values (42 for success or 43 for failure).
 * <p>
 * The validator requires arguments (either on the command line if invoked via main() 
 * or as constructor arguments if invoked via instantiation) as follows:
 * <ul>
 *   <li> judge_data -- the judge's input data for a single test case
 *   <li> judge_answer  -- the judge's answer file corresponding to the specified judge_data
 *   <li> feedback_dir -- the name of a "feedback directory" in which the validator can produce "feedback files" 
 *      in order to report additional information on the validation of the team output. 
 *      The feedback_dir must end with a path separator ('/' or '\' depending on operating system), 
 *      so that simply appending a filename to feedback_dir gives the path to a file in the feedback directory.
 * </ul>
 * 
 * <P>
 * Following the above required arguments the validator can accept up to four optional arguments, as follows:
 * <ul>
 *   <li> "case_sensitive" -- indicates that string comparisons should be case-sensitive
 *          (by default, case is ignored)
 *   <li> "space_change_sensitive" -- indicates that changes in the amount of whitespace should be rejected 
 *          (the default is that any sequence of 1 or more whitespace characters are equivalent)
 *   <li> "float_absolute_tolerance E" -- indicates that floating-point tokens should be accepted if they are within absolute error {@literal <=} E
 *   <li> "float_relative_tolerance E" -- indicates that floating-point tokens should be accepted if they are within relative error {@literal <=} E
 *   <li> "float_tolerance E" -- this string is a short-hand for applying E as both relative and absolute tolerance
 * </ul>
 * <P>
 * When supplying both a relative and an absolute tolerance, the semantics are that a token is accepted if it is within either of the 
 * two tolerances. When a floating-point tolerance has been set, any valid formatting of floating point numbers is accepted for floating 
 * point tokens. So for instance if a token in the judge_answer file says 0.0314, a token of 3.14000000e-2 in the team output would be accepted. 
 * If no floating point tolerance has been set, floating point tokens are treated just like any other token and must match exactly.
 * <P>
 * Optional arguments may also be constructed using hyphens ("-") instead of underscores ("_"), as long as the same character 
 * is used throughout a given option string (for example, "space-change-sensitive" is equivalent to "space_change_sensitive").
 * <P>
 * Whether the validator is invoked via the main() method or by instantiation, the class expects the team output to be provided on the
 * standard input stream.
 * <p>
 * To understand the meanings of the float_absolute_tolerance and float_relative_tolerance options, the following description 
 * (based on http://stackoverflow.com/questions/8961844/relative-and-absolute-tolerance-definitions-in-matlab-solver) may help.
 * <p>
 * There are two ways to measure the amount by which two values differ: relative difference (i.e. % change), and absolute difference.  
 * It makes a lot of sense to check for relative change, since a difference of 5 means something very different when the correct answer is around 1 
 * than when it is around 100000.  Relative tolerance is defined as the percentage of allowable difference;
 * checking relative difference between the judge's answer "j" and the team's answer "t" means checking whether 
 * {@literal abs(j-t)/j)<=relTol}, or equivalently whether {@literal abs(j-t)<=relTol*j}, where "relTol" is a percentage (fraction).
 * In other words, this checks by what fraction the team solution differs from the judge's solution. 
 * <P>  
 *  Relative tolerance, however, becomes problematic when the correct answer is very small, since as the correct answer tends toward zero
 *  the value {@literal abs(j-t)/j} grows toward infinity (because x/0 is undefined). 
 *  Thus, it makes sense to also look at the absolute change in value, and accept an answer when {@literal abs(j-t)<absTol}. 
 *  If you choose absTol small enough, it will only be relTol (percent difference) that counts for large values, 
 *  while absTol only becomes relevant if the correct answer tends to lie around 0.  
 *  <P>
 *  Since the validator stops when either of the two criterion is fulfilled, how close the team gets to a correct answer 
 *  is determined by absTol or relTol. For example, if relTol is 10% (0.1), the team will have to get within 10% of the judge's answer, 
 *  unless the answer is very small, in which case the absTol criterion (of, say, 0.0001) is satisfied before the relTol criterion.
 * 
 *  The validator writes a judgement message to a file in the specified feedback_Dir; the name of the file is defined by the 
 *  constant {@link ClicsValidator#CLICS_JUDGEMENT_FEEDBACK_FILE_NAME}.  
 *  If the result of the validation is failure (that is, the submission is being judged "no"), the validator also writes a
 *  "judgement details" message to a file in the feedback_Dir; the name of the details file is defined by the
 *  constant {@link ClicsValidator#CLICS_JUDGEMENT_DETAILS_FEEDBACK_FILE_NAME}.
 * 
 * @author John@pc2.ecs.csus.edu
 *
 */
public class CustomJavaClicsInterfaceValidator {

    public static final int CLICS_VALIDATOR_JUDGED_RUN_SUCCESS_EXIT_CODE = 42;
    public static final int CLICS_VALIDATOR_JUDGED_RUN_FAILURE_EXIT_CODE = 43;
    public static final int CLICS_VALIDATOR_ERROR_EXIT_CODE = -39;
    
    //the judgement messages which can be returned in the "judgement.txt" feedback file in the feedback directory
    public static final String CLICS_INCOMPLETE_OUTPUT_MSG = "Incomplete output";
    public static final String CLICS_EXCESSIVE_OUTPUT_MSG = "Excessive output";
    public static final String CLICS_INCORRECT_OUTPUT_FORMAT_MSG = "Incorrect output format";
    public static final String CLICS_WRONG_ANSWER_MSG = "Wrong Answer";
    public static final String CLICS_CORRECT_ANSWER_MSG = "accepted";
        
    public static final int EOF = -1;
    
    static Log log = null;
    
    private String judgeDataFile = null;
    private String judgeAnswerFile = null;
    private String feedbackDirName = null;
    
    private boolean isCaseSensitive = false;
    private boolean isSpaceSensitive = false;
    private boolean useFloatTolerance = false;
    private double floatAbsTolerance = ClicsValidatorSettings.CLICS_DEFAULT_FLOAT_ABSOLUTE_TOLERANCE;
    private double floatRelTolerance = ClicsValidatorSettings.CLICS_DEFAULT_FLOAT_RELATIVE_TOLERANCE;
    
    
    public CustomJavaClicsInterfaceValidator(String inJudgeDataFile, String inJudgeAnswerFile, String inFeedbackDirName, String... options ) {
        if (log==null) {
            log = new Log("CustomJavaClicsInterfaceValidator.log");        
        }
        
        String optionString = "";
        if (options.length==0) {
            optionString = "<none>";
        } else {
            for (int i=0; i<options.length; i++) {
                optionString += options[i] + " ";
            }
        }
        log.info("Constructing CustomJavaClicsInterfaceValidator; judgeDataFile='" + inJudgeDataFile + "', judgeAnswerFile='" + inJudgeAnswerFile 
                + "', feedbackDirName='" + inFeedbackDirName + "'"); 
        if (options.length>0) {
            log.info("   Validator options: " + optionString);
        }

        //grab file arguments
        judgeDataFile = inJudgeDataFile;
        judgeAnswerFile = inJudgeAnswerFile;
        feedbackDirName = inFeedbackDirName;
        
        //make sure the caller appended a file separator on the feedback dir name (the CLICS standard requires this, but still...)
        if (!feedbackDirName.endsWith(File.separator)) {
            feedbackDirName += File.separator;
        }

        //verify files are valid
        if (!validateFiles()) {
            log.severe("CustomJavaClicsInterfaceValidator received invalid file or directory name(s):");
            log.severe("  Judge's data file = '" + judgeDataFile + "'; Judge's answer file = '" + judgeAnswerFile + "'; Feedback Dir Name = '" + feedbackDirName + "'");
            throw new RuntimeException("CustomJavaClicsInterfaceValidator received invalid file or directory name(s)");
        }
        
//        dumpOptions(options);
        
        //grab optional arguments (if any)
        for (int i=0; i<options.length; i++) {

            if (ClicsValidatorSettings.CLICS_VTOKEN_CASE_SENSITIVE.equals(options[i]) || "case-sensitive".equals(options[i])) {
                isCaseSensitive = true;
            } else if (ClicsValidatorSettings.CLICS_VTOKEN_SPACE_CHANGE_SENSITIVE.equals(options[i]) || "space_sensitive".equals(options[i])
                    || "space-change-sensitive".equals(options[i]) || "space-sensitive".equals(options[i])) {
                isSpaceSensitive = true;
            } else if (ClicsValidatorSettings.CLICS_VTOKEN_FLOAT_ABSOLUTE_TOLERANCE.equals(options[i]) || "float-absolute-tolerance".equals(options[i])) {
                if (i<options.length-1) {
                    i++;
                    try {
                        double epsilon = Double.parseDouble(options[i]);
                        floatAbsTolerance = epsilon;
                        useFloatTolerance = true;
                    } catch (NumberFormatException | NullPointerException e) {
                        //bad epsilon value
                        log.severe("Bad value following '" + ClicsValidatorSettings.CLICS_VTOKEN_FLOAT_ABSOLUTE_TOLERANCE + "' option");
                        e.printStackTrace();
                        throw new RuntimeException("Bad value following '" + ClicsValidatorSettings.CLICS_VTOKEN_FLOAT_ABSOLUTE_TOLERANCE + "' option");
                    }
                } else {
                    //missing epsilon
                    log.severe("Missing tolerance value following '" + ClicsValidatorSettings.CLICS_VTOKEN_FLOAT_ABSOLUTE_TOLERANCE + "' option");
                    throw new RuntimeException("Missing tolerance value following '" + ClicsValidatorSettings.CLICS_VTOKEN_FLOAT_ABSOLUTE_TOLERANCE + "' option");
                    
                }
                
            } else if (ClicsValidatorSettings.CLICS_VTOKEN_FLOAT_RELATIVE_TOLERANCE.equals(options[i]) || "float-relative-tolerance".equals(options[i])) {
                if (i<options.length-1) {
                    i++;
                    try {
                        double epsilon = Double.parseDouble(options[i]);
                        floatRelTolerance = epsilon;
                        useFloatTolerance = true;
                    } catch (NumberFormatException | NullPointerException e) {
                        //bad epsilon value
                        log.severe("Bad value following '" + ClicsValidatorSettings.CLICS_VTOKEN_FLOAT_RELATIVE_TOLERANCE + "' option");
                        e.printStackTrace();
                        throw new RuntimeException("Bad value following '" + ClicsValidatorSettings.CLICS_VTOKEN_FLOAT_RELATIVE_TOLERANCE + "' option");
                    }
                } else {
                    //missing epsilon
                    log.severe("Missing tolerance value following '" + ClicsValidatorSettings.CLICS_VTOKEN_FLOAT_RELATIVE_TOLERANCE + "' option");
                    throw new RuntimeException("Missing tolerance value following '" + ClicsValidatorSettings.CLICS_VTOKEN_FLOAT_RELATIVE_TOLERANCE + "' option");
                }
                
            } else if ("float_tolerance".equals(options[i]) || "float-tolerance".equals(options[i])) {
                if (i<options.length-1) {
                    i++;
                    try {
                        double epsilon = Double.parseDouble(options[i]);
                        floatRelTolerance = epsilon;
                        floatAbsTolerance = epsilon;
                        useFloatTolerance = true;
                    } catch (NumberFormatException | NullPointerException e) {
                        //bad epsilon value
                        log.severe("Bad value following 'float_tolerance' option");
                        e.printStackTrace();
                        throw new RuntimeException("Bad value following 'float_tolerance' option");
                    }
                } else {
                    //missing epsilon
                    log.severe("Missing tolerance value following 'float_tolerance' option");
                    throw new RuntimeException("Missing tolerance value following 'float_tolerance' option");
                }
                
            } else {
                //check for empty option argument (to allow accepting null or "" as a valid -- if ignored -- argument)
                if (options[i]!=null && options[i].length()>0) {
                    //non-null, non-zero-length option -> unknown option
                    log.warning("Validator received unknown option: '" + options[i] + "'");
                }
            }
            
        }//end grab option arguments (if any)
        
    }//end constructor
    
    /**
     * Causes this CustomJavaClicsInterfaceValidator to evaluate the team output (received on System.in)
     * using the data values configured via the constructor, and return either success or failure as defined by
     * the static class constants.
     * 
     * @return an int indicating success or failure, or an error code if an error occurred
     */
    public int validate() {   
        
        //get input stream for reading judge's answer
        InputStream judgeAnswerIS=null;
        try {
            judgeAnswerIS = new FileInputStream(new File(judgeAnswerFile));
        } catch (FileNotFoundException e2) {
            log.severe("Judge's answer file '" + judgeAnswerFile + "': file not found");
            e2.printStackTrace();
            return CLICS_VALIDATOR_ERROR_EXIT_CODE;
        }
        
        return validate(judgeAnswerIS, System.in);
    }
    
    /**
     * Causes this CustomJavaClicsInterfaceValidator to evaluate the team output (received on the specified input stream)
     * against the judge's answer (received on the specified input stream), 
     * and return either success or failure as defined by the static class constants.
     * 
     * @param judgeAnswerIS an InputStream containing the values in the judge's answer file
     * @param teamOutputIS an InputStream containing the values in the team's output file 
     * 
     * @return an int indicating success or failure, or an error code if an error occurred
     */
    public int validate(InputStream judgeAnswerIS, InputStream teamOutputIS) {
        
        //we need a pushback stream so we can mimic "peek()" on the judge's answer file stream
        PushbackInputStream judgeAnswerPushbackIS = new PushbackInputStream(judgeAnswerIS);
        
        //input stream for reading team output
        // (note the assumption that the team's output is provided on "stdin")
        PushbackInputStream teamOutputPushbackIS = new PushbackInputStream(teamOutputIS);
        
        //vars to hold bytes from judge and team input streams
        byte judgeByte;
        byte teamByte;
        
        //loop until judge's answer is exhausted (the loop breaks out when judge's answer is exhausted; 
        // it exits with a failure code if team output fails to match)
        while (true) {

            //if space sensitive, skip over equal whitespace in judge answer and team output
            if (isSpaceSensitive) {
                
                try {
                    //while judge's answer file has whitespace, see if team output file has matching whitespace
                    while (isWhiteSpace(peek(judgeAnswerPushbackIS))) {
                        
                        //judge has whitespace byte; get next bytes from judge and team
                        judgeByte = (byte) judgeAnswerPushbackIS.read();
                        teamByte = (byte) teamOutputPushbackIS.read();
                        
                        //if the team byte doesn't match the judge's whitespace char, team has failed (since we're in "isSpaceSensitive" mode)
                        if (teamByte!=judgeByte) {
                            //exit with mismatched whitespace error
                            try {
                                outputFailure(CLICS_INCORRECT_OUTPUT_FORMAT_MSG, "Space change error: judge's answer contains '" + printableString(judgeByte) 
                                    + "' but team's output contains '" + printableString(teamByte) + "'");
                            } catch (Exception e) {
                                log.severe("Error outputting validator feedback answer file");
                                return CLICS_VALIDATOR_ERROR_EXIT_CODE;                                                                            
                            }
                            return CLICS_VALIDATOR_JUDGED_RUN_FAILURE_EXIT_CODE;
                        }
                    }
                    
                    //we've skipped over matching whitespace to the first thing AFTER whitespace in the judge; 
                    // make sure the team doesn't have MORE whitespace
                    if (isWhiteSpace(peek(teamOutputPushbackIS))) {
                        //exit with mismatched whitespace error
                        try {
                            outputFailure(CLICS_INCORRECT_OUTPUT_FORMAT_MSG, "Space change error: team's output contains extra whitespace char '" 
                                    + printableString((byte)teamOutputPushbackIS.read()) + "'");
                        } catch (Exception e) {
                            log.severe("Error outputting validator feedback answer file");
                            return CLICS_VALIDATOR_ERROR_EXIT_CODE;                                            
                        }
                        return CLICS_VALIDATOR_JUDGED_RUN_FAILURE_EXIT_CODE;

                    }
                } catch (IOException e) {
                    log.severe("IOException processing judge/team files: " + e.getMessage());
                    e.printStackTrace();
                    return CLICS_VALIDATOR_ERROR_EXIT_CODE;
                }
            } //end if isSpaceSensitive
                    
            //if we get here, either we're not in case-sensitive mode, or we are but the above code has stripped matching leading whitespace
            // from both the judge's answer and the team's output streams; in either case we're ready to compare the next things in the streams.
            
            //make sure the judge isn't at EOF (because if so there's nothing left to compare)
            if (peek(judgeAnswerPushbackIS) == EOF) {
                //judge is at EOF; exit loop and check if team is also at EOF
                break ;
            }
                        
            //judge not at EOF, check if team IS at EOF
            if (peek(teamOutputPushbackIS) == EOF) {
                            
                //team is at EOF when judge is not, so team is missing output
                try {
                    outputFailure(CLICS_INCOMPLETE_OUTPUT_MSG, "Incomplete output (next judge token = '" + getNextToken(judgeAnswerPushbackIS) + "')");
                } catch (Exception e) {
                    return CLICS_VALIDATOR_ERROR_EXIT_CODE;
                }
                return CLICS_VALIDATOR_JUDGED_RUN_FAILURE_EXIT_CODE;
            }
                    
            
            //if we get here we know that neither the judge nor the team streams are at EOF, and are positioned after any matching 
            // whitespace (in case-sensitive mode) or at the start of the next whitespace (in non-case-sensitive mode)
            
            //get next tokens from the streams (note that getNextToken() skips leading whitespace, if any...)
            String nextJudgeToken = getNextToken(judgeAnswerPushbackIS);    
            String nextTeamToken = getNextToken(teamOutputPushbackIS);
            
            //we have both a judge token and a team token which are not null and not zero length; compare them
            // (note that method areEquivalent() handles calling outputWrongAnswer() for various conditions if necessary)
            try {
                if (!areEquivalent(nextJudgeToken, nextTeamToken)) {
                    return CLICS_VALIDATOR_JUDGED_RUN_FAILURE_EXIT_CODE;
                }
            } catch (IOException e) {
                log.severe("Error checking areEquivalent(" + nextJudgeToken + "," + nextTeamToken + ")");
                return CLICS_VALIDATOR_ERROR_EXIT_CODE;
            }
            
            //go back and skip the next whitespace (if in case-sensitive mode), then process the next token
            
        }//end while(true) 
        
        //we get here when there's no more input in the judge's answer stream
        //check if team is at EOF
        try {
            if (teamOutputPushbackIS.read()!= EOF) {
                try {
                    outputFailure(CLICS_EXCESSIVE_OUTPUT_MSG, "Team has trailing output beyond what judge answer file contains");
                } catch (Exception e) {
                    log.severe("Error outputting validator feedback answer file");
                    return CLICS_VALIDATOR_ERROR_EXIT_CODE;                                            
                }
                return CLICS_VALIDATOR_JUDGED_RUN_FAILURE_EXIT_CODE;
            }
        } catch (IOException e) {
            log.severe("IOException processing team input stream (stdin): " + e.getMessage());
            e.printStackTrace();
        }
        
        try {
            outputSuccess(CLICS_CORRECT_ANSWER_MSG);
        } catch (Exception e) {
            return CLICS_VALIDATOR_ERROR_EXIT_CODE;
        }
        return CLICS_VALIDATOR_JUDGED_RUN_SUCCESS_EXIT_CODE;  
    }//end method validate()
    
    /**
     * Returns an int containing the next byte in the specified input stream while leaving the input stream unchanged.
     * Note that the return value contains the byte value (0-255) in the lower bits of the returned
     * 16-bit int, or contains -1 if there was no byte available in the input stream (i.e. the 
     * stream was at EOF).  This conforms to the way in which {@link InputStream#read()} returns data.
     * 
     * @param inStream - the PushbackInputStream to be peeked at
     * @return - an int containing the byte at the head of the input stream, or -1
     */
    private int peek(PushbackInputStream inStream) {
        
        int nextByte = EOF;  //yes, it's an int named nextByte; see the javadoc for InputStream.read() to understand why
        try {
            nextByte = inStream.read();
            if (nextByte != EOF) {
                inStream.unread(nextByte);
            }
        } catch (IOException e) {
            log.severe("Internal error in CustomJavaClicsInterfaceValidator.peek()");
            e.printStackTrace();
        } 
        
        return nextByte;
    }
    
    /**
     * Checks whether the received integer contains a representation of a whitespace character.
     * Note that the received integer is assumed to contain a byte read from an input stream,
     * or else the value -1 if the input stream had been at EOF.
     * If the received integer is equal to -1 then the method returns false (EOF is not whitespace).
     * Determination of whitespace for values not equal to -1 is done using 
     * {@link Character#isWhitespace(int)}.
     * @param byteVal - an int containing a byte value in the lower 8 bits, or else the value -1
     * @return true if the received integer is not equal to -1 and is a whitespace character
     */
    private boolean isWhiteSpace(int byteVal) {

        if (byteVal == EOF) {
            return false;
        } else {
            return Character.isWhitespace(byteVal);
        }
    }
    
    /**
     * Returns a printable string containing a representation of the specified byte.
     * Recognized control characters are converted to strings such as "<tab> for the byte 0x08 (tab);
     * other control characters are converted to "<?>".
     * Non-control characters are converted to a string containing their character representation.
     * 
     * @param theByte a byte representing a character
     * @return a string representation of the character represented by theByte
     */
    private String printableString(byte theByte) {
        if (Character.isISOControl(theByte)) {
            switch (theByte) {
                case 0x00: return new String ("<NUL>");
                case 0x01: return new String ("<SOH>");
                case 0x02: return new String ("<STX>");
                case 0x03: return new String ("<ETX>");
                case 0x04: return new String ("<EOT>");
                case 0x05: return new String ("<ENQ>");
                case 0x06: return new String ("<ACK>");
                case 0x07: return new String ("<BEL>");
                case 0x08: return new String ("<BS>");
                case 0x09: return new String ("<TAB>");
                case 0x0A: return new String ("<LF>");
                case 0x0B: return new String ("<VT>");
                case 0x0C: return new String ("<FF>");
                case 0x0D: return new String ("<CR>");
                case 0x0E: return new String ("<SO>");
                case 0x0F: return new String ("<SI>");
                case 0x10: return new String ("<DLE>");
                case 0x11: return new String ("<DC1>");
                case 0x12: return new String ("<DC2>");
                case 0x13: return new String ("<DC3>");
                case 0x14: return new String ("<DC4>");
                case 0x15: return new String ("<NAK>");
                case 0x16: return new String ("<SYN>");
                case 0x17: return new String ("<ETB>");
                case 0x18: return new String ("<CAN>");
                case 0x19: return new String ("<EM>");
                case 0x1A: return new String ("<SUB>");
                case 0x1B: return new String ("<ESC>");
                case 0x1C: return new String ("<FS>");
                case 0x1D: return new String ("<GS>");
                case 0x1E: return new String ("<RS>");
                case 0x1F: return new String ("<US>");
                case 0x7F: return new String ("<DEL>");
                default:   return new String ("<?>");
            }
        } else {
            char[] theChar = new char[1];
            theChar[0] = (char) theByte;
            return new String (theChar);
        }
    }

    /**
     * Returns the next whitespace-delimited token from the specified input stream.
     * Skips any leading whitespace in the stream; leaves the input stream pointing
     * at the first character after the end of the returned token (that is, either at
     * the beginning of the trailing whitespace delimiting the token or at the end of the
     * stream if there is no more data in the stream).
     * 
     * @param inStream -- the pushback stream from which a token is to be extracted
     * @return a String containing the next whitespace-delimited token, or the empty string
     *          if the stream has no more non-whitespace tokens
     */
    private String getNextToken(PushbackInputStream inStream) {

        //strip off any leading whitespace
        try {

            while (isWhiteSpace(peek(inStream)) && !(peek(inStream) == EOF)) {
                inStream.read();  //discard whitespace chars
            }
        } catch (IOException e) {
            log.severe("IOException while flushing leading whitespace from input stream: " + e.getMessage());
            e.printStackTrace();
            return "CustomJavaClicsInterfaceValidator.getNextToken(): error reading stream";
        }
        
        //if at EOF then there were no non-whitespace chars left in the stream
        if (peek(inStream) == EOF) {
            return "";
        }
        
        //there must be at least one non-whitespace char in the stream; pull out consecutive non-whitespace chars
        StringBuffer buf = new StringBuffer();
        try {
            
            // get all non-whitespace chars preceeding whitespace or EOF
            while (!(peek(inStream) == EOF)) {

                if (!isWhiteSpace(peek(inStream))) {
                    buf.append((char) inStream.read());
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            log.severe("IOException while reading non-whitespace chars from input stream: " + e.getMessage());
            e.printStackTrace();
            return "CustomJavaClicsInterfaceValidator.getNextToken(): error reading stream";
        }
        
        //return the stream characters as the next token
        String retString = new String(buf);
                
        return retString;
    }//end method getNextToken()
    
    /**
     * Returns an indication of whether the two received strings, representing tokens from the judge and 
     * team streams, are "equivalent" according to the current validator settings. 
     * Float values are equivalent if they are within either the specified relative or absolute tolerance; 
     * strings are equivalent if they are exactly the same (that is, String.equals() returns true) if in
     * "case-sensitive" mode) or are the same ignoring case when not in case-sensitive mode.
     * <P>
     * If either input string is null then they are deemed "not equivalent" (even if BOTH are null).
     * 
     * @param judgeToken
     *            -- a token from the judge's answer input stream
     * @param teamToken
     *            -- a token from the team output stream
     * @return true if the two tokens are equivalent under the current validator settings
     * @throws IOException 
     */
    private boolean areEquivalent(String judgeToken, String teamToken) throws IOException {
        
        if (judgeToken==null || teamToken==null) {
            return false;
        }

        // check if the judge has a floating-point number and we're applying tolerance checking
        if (useFloatTolerance && isFloat(judgeToken)) {
            double judgeVal = getDoubleValue(judgeToken);
            if (!isFloat(teamToken)) {
                try {
                    outputFailure(CLICS_WRONG_ANSWER_MSG, "Expected float in team output, got '" + teamToken + "'");
                } catch (Exception e) {
                    log.severe("Error outputting validator feedback answer file");
                    throw e;                                            
                }
                return false;
            } else {
                // both judge and team have doubles; see if they are equal within tolerance
                double teamVal = getDoubleValue(teamToken);
                if (withinTolerance(judgeVal, teamVal)) {
                    return true;
                } else {
                    double diff = judgeVal - teamVal;
                    try {
                        outputFailure(CLICS_WRONG_ANSWER_MSG, "Float out of tolerance range: judge value = " + judgeVal + "; team value = " + teamVal
                                        + "; difference = " + diff + " (abs tol = " + floatAbsTolerance + "; rel tol = " + floatRelTolerance + ")");
                    } catch (Exception e) {
                        log.severe("Error outputting validator feedback answer file");
                        throw e;                                            
                    }
                    return false;
                }
            }
        }

        // we're not using float tolerance checking (or we don't have a float from the judge at this moment)
        else if (isCaseSensitive) {
            // check if judge and team tokens are equal, including case
            if (judgeToken.equals(teamToken)) {
                return true;
            } else {
                try {
                    outputFailure(CLICS_WRONG_ANSWER_MSG, "String token mismatch (case-sensitive): judge token = '" 
                                                                    + judgeToken + "'; team token = '" + teamToken + "'");
                } catch (Exception e) {
                    log.severe("Error outputting validator feedback answer file");
                    throw e;                    
                }
                return false;
            }

        }

        // we're not using case-sensitivity
        else if (judgeToken.equalsIgnoreCase(teamToken)) {
            return true;
        } else {
            try {
                outputFailure(CLICS_WRONG_ANSWER_MSG, "String tokens mismatch (ignoring case): judge token = '" 
                                                                + judgeToken + "'; team token = '" + teamToken + "'");
            } catch (Exception e) {
                log.severe("Error outputting validator feedback answer file");
                throw e;
            }
            return false;
        }
    }//end method areEquivalent()
    
    /**
     * Checks whether the two received values are within either (or both) the currently specified
     * absolute and relative tolerances.
     * 
     * @param jVal, tval -- the two values to be compared
     * @return true if the two values are within either the relative or absolute tolerance
     */
    private boolean withinTolerance(double jVal, double tVal) {
        double diff = Math.abs(jVal-tVal);
        if (! (diff<=floatAbsTolerance)  &&  ! (diff<=floatRelTolerance*Math.abs(jVal))) {
            return false;
        }
        return true;
    }
    
    /**
     * Returns an indication of whether the specified string contains a valid description of a 
     * floating-point number.
     * The determination of whether the String contains a valid float is made by invoking
     * Double.parseDouble(str); if this succeeds then the number is deemed to be a valid
     * floating-point number.
     * 
     * @param str - the string to be examined
     * @return true if the string represents a floating-point number
     */
    private boolean isFloat(String str) {
        
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }
    
    /**
     * Returns the double value represented by the received string.
     * 
     * @param str -- the string to be parsed
     * @return the double value represented by the string
     * @throws RuntimeException if the received string is not a valid representation of a double
     */
    private double getDoubleValue(String str) {
        try {
            double val = Double.parseDouble(str);
            return val;
        } catch (NumberFormatException | NullPointerException e) {
            log.severe("getDoubleValue() received a string which is not a valid double value");
            throw new RuntimeException("getDoubleValue() received a string which is not a valid double value");
        }
    }
    
    private void outputFailure (String judgement, String details) throws FileNotFoundException, UnsupportedEncodingException {
        log.info("CLICS Validator returning failure: " + judgement + ", " + details );
        
        outputJudgementFile(judgement);
        outputDetailsFile(details);
    }
    
    private void outputSuccess (String message) throws FileNotFoundException, UnsupportedEncodingException {
        log.info("CLICS Validator returning success: " + message );
                
        outputJudgementFile(message);
    }
    
    /**
     * Writes the specified judgement text into the feedback judgement file in the feedback directory, using "UTF-8" encoding.
     * The name of the file into which the text is written is defined by the static constant {@link #CLICS_JUDGEMENT_FEEDBACK_FILE_NAME}.
     * 
     * Note that this method assumes that the global "feedback directory" name already has a File.separator appended to it (this is
     * handled in the constructor which receives the feedback directory name).
     * 
     * @throws UnsupportedEncodingException if UTF-8 encoding for files is not supported
     * @throws FileNotFoundException if the method is unable to create a judgement feedback file in the feedback directory
     */
    private void outputJudgementFile(String judgement) throws FileNotFoundException, UnsupportedEncodingException {
        String feedbackFileName = feedbackDirName + ClicsValidator.CLICS_JUDGEMENT_FEEDBACK_FILE_NAME;
        
        System.out.println ("CustomJavaClicsInterfaceValidator outputing judgement '" + judgement + "' to feedback file '" + feedbackFileName + "'");
        log.info("Writing judgement '" + judgement + "' to feedback file '" + feedbackFileName + "'");
        
        PrintWriter writer = new PrintWriter(feedbackFileName, "UTF-8");
        writer.println(judgement);
        writer.close();
    }
    
    /**
     * Writes the specified details text into the judgement details file in the feedback directory, using "UTF-8" encoding.
     * The name of the file into which the text is written is defined by the static constant {@link #CLICS_JUDGEMENT_DETAILS_FEEDBACK_FILE_NAME}.
     * 
     * Note that this method assumes that the global "feedback directory" name already has a File.separator appended to it (this is
     * handled in the constructor which receives the feedback directory name).
     * 
     * @throws UnsupportedEncodingException if UTF-8 encoding for files is not supported
     * @throws FileNotFoundException if the method is unable to create a judgement details feedback file in the feedback directory
     */
    private void outputDetailsFile(String details) throws FileNotFoundException, UnsupportedEncodingException {
        String detailsFileName = feedbackDirName + ClicsValidator.CLICS_JUDGEMENT_DETAILS_FEEDBACK_FILE_NAME;
        
        System.out.println ("CustomJavaClicsInterfaceValidator outputing judgement details '" + details + "' to feedback details file '" + detailsFileName + "'");
        log.info("Writing judgement details '" + details + "' to feedback details file '" + detailsFileName + "'");
        
        PrintWriter writer = new PrintWriter(detailsFileName, "UTF-8");
        writer.println(details);
        writer.close();
    }
    
    

    
    /**
     * Checks the judgesDataFile, judgesAnswerFile, and feedbackDirName to make sure they exist and are 
     * accessible as required.
     * @return true if all files/folders are accessible; false otherwise
     */
    private boolean validateFiles() {
        
        if (judgeDataFile==null || judgeAnswerFile==null || feedbackDirName==null) {
            String errStr = "";
            if (judgeDataFile==null) {
                errStr += " judgeDataFile";
            }
            if (judgeAnswerFile==null) {
                if (errStr.length()>0) {
                    errStr += ",";
                }
                errStr += " judgeAnswerFile";
            }
            if (feedbackDirName==null) {
                if (errStr.length()>0) {
                    errStr += ",";
                }
                errStr += " feedbackDirName";
            }
            log.severe("validator received null file name(s) for: " + errStr);
            return false;
        }
        
        File dataFile = new File(judgeDataFile);
        if (!dataFile.exists() || !dataFile.canRead()) {
            log.severe("judge data file '" + judgeDataFile + "' doesn't exist or isn't readable");
            return false;
        }
        
        File answerFile = new File(judgeAnswerFile);
        if (!answerFile.exists() || !answerFile.canRead()) {
            log.severe("judge answer file '" + judgeAnswerFile + "' doesn't exist or isn't readable");
            return false;
        }
        
        File feedbackDir = new File(feedbackDirName);
        if (!feedbackDir.exists() || !feedbackDir.isDirectory() || !feedbackDir.canRead() || !feedbackDir.canWrite()) {
            log.severe("feedback dir '" + feedbackDirName + "' doesn't exist, isn't a directory, or isn't readable and writeable");
            return false;
        }
        
        log.info("Verified all files/folders accessible");
        return true;
    }
    
    @SuppressWarnings("unused")
    private void dumpOptions(String [] options) {
        for (int i=0; i<options.length; i++) {
            System.out.println ("Option " + i + ": '" + options[i] + "'");
        }
    }
    
    /**
     * The main entry point to the CustomJavaClicsInterfaceValidator when running as a stand-alone program.
     * The main program constructs a CustomJavaClicsInterfaceValidator object passing to its constructor the arguments received
     * by main(), then invokes the validator's {@link #validate()} method.  The integer result returned by
     * {@link #validate()} is then used as the main program exit code.
     * <P>
     * The validator expects the output of the team program (that is, the data being validated by comparing
     * against the specified judge's answer file) to be provided on the standard input channel. 
     * 
     * @param args: {@literal judgeDataFile judgeAnswerFile feedbackDir [options]}
     */
    public static void main(String[] args) {
        
        if (args.length < 3) {
            usage();
            System.exit(CLICS_VALIDATOR_JUDGED_RUN_FAILURE_EXIT_CODE);
        }
        
        log = new Log("CustomJavaClicsInterfaceValidator.log");            
        
        //build a string of all input argument values for output/logging/debugging
        String params = "";
        params += args[0] + " ";
        params += args[1] + " ";
        params += args[2] + " ";
        
        //copy any options into an array for passing to the constructor as a vararg
        String [] options = new String[args.length-3];
        for (int i=3; i<args.length; i++) {
            options[i-3] = args[i];
            params += args[i] + " ";
        }
        
        log.info("Constructing CustomJavaClicsInterfaceValidator from CustomJavaClicsInterfaceValidator.main() with arguments '" + params + "'");
         
        CustomJavaClicsInterfaceValidator validator = null;
        try {
            validator = new CustomJavaClicsInterfaceValidator(args[0], args[1], args[2], options);
        } catch (Exception e) {
            log.severe("Exception constructing CustomJavaClicsInterfaceValidator from main(): " + e.getMessage());
            e.printStackTrace();
            System.exit (CLICS_VALIDATOR_ERROR_EXIT_CODE);
        }
        
        log.info("Invoking CustomJavaClicsInterfaceValidator.validate()");
        
        int result = validator.validate();
        
        log.info("validate() returned code " + result);
        
        System.out.println ("Exiting CustomJavaClicsInterfaceValidator main() with exit code " + result);
        
        System.exit(result);
        
    }

    public static void usage() {
        System.err.println("Usage:  java CustomJavaClicsInterfaceValidator judges_data_file judges_answer_file feedbackdir" + File.separator + "  [options]  <  teamOutput");
        System.err.println ("  where judges_data_file and judges_answer_file must exist and be readable files,\n"
                + "  feedbackdir" + File.separator + " must exist and be a directory that is both readable and writable,\n"
                + "  and where [options] include:");
        System.err.println ("    " + ClicsValidatorSettings.CLICS_VTOKEN_CASE_SENSITIVE);
        System.err.println ("    " + ClicsValidatorSettings.CLICS_VTOKEN_SPACE_CHANGE_SENSITIVE);
        System.err.println ("    " + ClicsValidatorSettings.CLICS_VTOKEN_FLOAT_ABSOLUTE_TOLERANCE + " E");
        System.err.println ("    " + ClicsValidatorSettings.CLICS_VTOKEN_FLOAT_RELATIVE_TOLERANCE + " E");
        System.err.println ("    float_tolerance E   (shorthand for setting both absolute and relative tolerance)");        
    }

}
