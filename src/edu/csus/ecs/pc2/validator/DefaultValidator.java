/**
 * 
 */
package edu.csus.ecs.pc2.validator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;

/**
 * This class implements an "ICPC Default Validator".
 * The DefaultValidator expects to receive on its standard input stream the output of the execution of 
 * a team program on a given data set; it returns an indication of whether the team output is "valid".
 * <P>
 * The DefaultValidator can be invoked in one of two ways: as a stand-alone main program, or by instantiating
 * the class and invoking the {@link #validate()} method. If invoked as a stand-alone program the validator
 * exits with Exit Code 42 if the team output matches the judge's answer, or with Exit Code 43 if not
 * (these values are specified by the ICPC CLICS Output Validator specification).
 * <P>
 * If invoked by instantiation and then calling {@link DefaultValidator#validate()}, the {@link #validate()}
 * method returns these same values (42 for success or 43 for failure).
 * <p>
 * The validator requires arguments (either on the command line if invoked via main() 
 * or as constructor arguments if invoked via instantiation) as follows:
 * <P>
 * <ul>
 *   <li> judge_data -- the judge's input data for a single test case
 *   <li> judge_answer  -- the judge's answer file corresponding to the specified judge_data
 *   <li> feedback_dir -- the name of a “feedback directory” in which the validator can produce "feedback files" 
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
 *   <li> "float_absolute_tolerance E" -- indicates that floating-point tokens should be accepted if they are within absolute error <= E
 *   <li> "float_relative_tolerance E" -- indicates that floating-point tokens should be accepted if they are within relative error <= E
 *   <li> "float_tolerance E" -- this string is a short-hand for applying E as both relative and absolute tolerance
 * <P>
 * When supplying both a relative and an absolute tolerance, the semantics are that a token is accepted if it is within either of the 
 * two tolerances. When a floating-point tolerance has been set, any valid formatting of floating point numbers is accepted for floating 
 * point tokens. So for instance if a token in the judge_answer file says 0.0314, a token of 3.14000000e-2 in the team output would be accepted. 
 * If no floating point tolerance has been set, floating point tokens are treated just like any other token and must match exactly.
 * <P>
 * Optional arguments may also be constructed using hyphens ("-") instead of underscores ("_"), as long as the same character is 
 * used throughout a given option string.
 * <P>
 * Whether the validator is invoked via the main() method or by instantiation, the class expects the team output to be provided on the
 * standard input stream.
 * 
 * @author John@pc2.ecs.csus.edu
 *
 */
public class DefaultValidator {

    static Log log = null;
    
    private String judgeDataFile = null;
    private String judgeAnswerFile = null;
    private String feedbackDirName = null;
    
    private boolean isCaseSensitive = false;
    private boolean isSpaceSensitive = false;
    private boolean useFloatTolerance = false;
    private double floatAbsTolerance = -1;
    private double floatRelTolerance = -1;
    
    
    public DefaultValidator(String inJudgeDataFile, String inJudgeAnswerFile, String inFeedbackDirName, String... options ) {
        if (log==null) {
            log = new Log("DefaultValidator.log");
            StaticLog.setLog(log);
            log = StaticLog.getLog();            
        }
        
        String optionString = "";
        if (options.length==0) {
            optionString = "<none>";
        } else {
            for (int i=0; i<options.length; i++) {
                optionString += options[i] + " ";
            }
        }
        log.info("Constructing DefaultValidator; judgeDataFile='" + inJudgeDataFile + "', judgeAnswerFile='" + inJudgeAnswerFile 
                + "', feedbackDirName='" + inFeedbackDirName + "'"); 
        if (options.length>0) {
            log.info("   Validator options: " + optionString);
        }

        //grab file arguments
        judgeDataFile = inJudgeDataFile;
        judgeAnswerFile = inJudgeAnswerFile;
        feedbackDirName = inFeedbackDirName;

        //verify files are valid
        if (!validateFiles()) {
            log.severe("DefaultValidator received invalid file or directory name(s)");
            throw new RuntimeException("DefaultValidator received invalid file or directory name(s)");
        }
        
        //grab optional arguments (if any)
        for (int i=0; i<options.length; i++) {
            if (options[i].equals("case_sensitive") || options[i].equals("case-sensitive")) {
                isCaseSensitive = true;
            } else if (options[i].equals("space_change_sensitive") || options[i].equals("space_sensitive")
                    || options[i].equals("space-change-sensitive") || options[i].equals("space-sensitive")) {
                isSpaceSensitive = true;
            } else if (options[i].equals("float_absolute_tolerance") || options[i].equals("float-absolute-tolerance")) {
                if (i<options.length-1) {
                    i++;
                    try {
                        double epsilon = Double.parseDouble(options[i]);
                        floatAbsTolerance = epsilon;
                        useFloatTolerance = true;
                    } catch (NumberFormatException | NullPointerException e) {
                        //bad epsilon value
                        log.severe("Bad value following 'float_absolute_tolerance' option");
                        e.printStackTrace();
                        throw new RuntimeException("Bad value following 'float_absolute_tolerance' option");
                    }
                } else {
                    //missing epsilon
                    log.severe("Missing tolerance value following 'float_absolute_tolerance' option");
                    throw new RuntimeException("Missing tolerance value following 'float_absolute_tolerance' option");
                    
                }
                
            } else if (options[i].equals("float_relative_tolerance") || options[i].equals("float-relative-tolerance")) {
                if (i<options.length-1) {
                    i++;
                    try {
                        double epsilon = Double.parseDouble(options[i]);
                        floatRelTolerance = epsilon;
                        useFloatTolerance = true;
                    } catch (NumberFormatException | NullPointerException e) {
                        //bad epsilon value
                        log.severe("Bad value following 'float_relative_tolerance' option");
                        e.printStackTrace();
                        throw new RuntimeException("Bad value following 'float_relative_tolerance' option");
                    }
                } else {
                    //missing epsilon
                    log.severe("Missing tolerance value following 'float_relative_tolerance' option");
                    throw new RuntimeException("Missing tolerance value following 'float_relative_tolerance' option");
                }
                
            } else if (options[i].equals("float_tolerance") || options[i].equals("float-tolerance")) {
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
                //unknown option
                log.warning("Validator received unknown option: '" + options[i] + "'; ignored");
            }
            
        }//end grab option arguments (if any)
        
    }//end constructor
    
    /**
     * Causes this DefaultValidator to evaluate the team output (received on the validator's standard input stream)
     * using the data values configured via the constructor, and return either 42 (success) or 43 (failure).
     * 
     * @return an int indicating success (42) or failure (43), or -39 if an error occurred
     */
    public int validate() {
        
        //so far, there's no apparent need for the judge's input data file...
        // InputStream judgeDataIS = getClass().getClassLoader().getResourceAsStream(judgeDataFile);
        InputStream judgeAnswerIS = getClass().getClassLoader().getResourceAsStream(judgeAnswerFile);        
        
        //input streams for reading judge's answer and team output
        // (note the assumption that the team's output is provided on "stdin")
        PushbackInputStream judgeAnswerPushbackIS = new PushbackInputStream(judgeAnswerIS);
        PushbackInputStream teamOutputPushbackIS = new PushbackInputStream(System.in);
        
        //loop until judge's answer is exhausted (the loop breaks out when judge's answer is exhausted; 
        // it exits with a return 43 if team output fails to match
        while (true) {
            
            //if space sensitive, skip over equal whitespace in judge answer and team output
            if (isSpaceSensitive) {
                
                try {
                    //while judge's answer file has whitespace, see if team output file matches
                    byte judgeByte = (byte) judgeAnswerPushbackIS.read();
                    //check for EOF or whitespace
                    while (!(judgeByte==-1) && Character.isWhitespace(judgeByte)) {
                        //found judge whitespace byte; get next team byte
                        byte teamByte = (byte) teamOutputPushbackIS.read();
                        //if they don't match, team has failed (since we're in "isSpaceSensitive" mode)
                        if (teamByte!=judgeByte) {
                            //exit with mismatched whitespace error
                            outputWrongAnswer("Space change error: judge's answer contains '" + printableString(judgeByte) 
                                    + "' but team's output contains '" + printableString(teamByte) + "'");
                            return 43;
                        }
                        //team byte matches judge (i.e., is the same whitespace char); get next judge byte
                        judgeByte = (byte) judgeAnswerPushbackIS.read();
                    }
                    
                    //if we get here, the judge had either a non-whitespace byte or was at EOF
                    if (judgeByte == -1) {
                        //judge at EOF, check if team is also at EOF
                        byte teamByte = (byte) teamOutputPushbackIS.read();
                        if (!(teamByte==-1)) {
                            //team is not at EOF, so has excessive output; we're going to quit --
                            // but first see if it's just whitespace in the team output, or something more
                            teamOutputPushbackIS.unread(teamByte);   //necessary because streams don't support "peek()" (at least, I couldn't figure out how...)
                            String teamExcess = getNextToken(teamOutputPushbackIS);
                            if (teamExcess.length()==0) {
                                //team had trailing whitespace
                                outputWrongAnswer("Team output contains excess trailing whitespace");
                                return 43;
                            } else {
                                //team had more than just trailing whitespace
                                outputWrongAnswer("Team output contains excess characters: '" + teamExcess + "'...");
                                return 43;
                            }
                        } else {
                            //judge and team are both at EOF; we're done
                            break;
                        }
                    }
                    
                    //team matched every whitespace char to first non-whitespace in judge's answer; push back the last judge's char
                    judgeAnswerPushbackIS.unread(judgeByte);
                    
                    //make sure the NEXT thing in the team output is NOT whitespace (because we know that's not what's next in the judge's answer)
                    byte teamByte = (byte) teamOutputPushbackIS.read();
                    if (Character.isWhitespace(teamByte)) {
                        //exit with mismatched whitespace error
                        outputWrongAnswer("Space change error: judge's answer contains '" + printableString(judgeByte) 
                                + "' but team's output contains '" + printableString(teamByte) + "'");
                        return 43;
                    } else {
                        //push the (non-whitespace) char back into the team input stream
                        teamOutputPushbackIS.unread(teamByte);
                    }

                } catch (IOException e) {
                    log.severe("IOException processing judge/team files: " + e.getMessage());
                    e.printStackTrace();
                    return -39;
                }
            } //end if isSpaceSensitive
            
            //we get here either because we're not in "case-sensitive" mode, or because the judge and team output have matched
            // up to this point including whitespace; the judge and team streams should be positioned after the matching whitespace
            // (in case-sensitive mode) or at the start of the next whitespace (in non-case-sensitive mode)
            
            //get next token from judge's stream 
            String nextJudgeToken = getNextToken(judgeAnswerPushbackIS);    //getNextToken() skips leading whitespace, if any...
            
            //break loop if no judge's token (because it means we've exhausted the judge's answer input)
            if (nextJudgeToken==null || nextJudgeToken.length()==0) {
                break;
            }
            
            //we have a judge token; get next token from team's stream
            String nextTeamToken = getNextToken(teamOutputPushbackIS);
            
            //exit if there's no team token (because it means the team has incomplete output)
            if (nextTeamToken==null || nextTeamToken.length()==0) {
                outputWrongAnswer("Insufficient output");
                return 43;
            }
            
            //we have both a judge token and a team token which are not null and not zero length; compare them
            // (note that method areEquivalent() handles calling outputWrongAnswer() for various conditions if necessary)
            if (!areEquivalent(nextJudgeToken, nextTeamToken)) {
                return 43;
            }
            
            //go back and skip the next whitespace (if in case-sensitive mode), then process the next token
            
        }//end while(true) 
        
        //we get here when there's no more input in the judge's answer stream
        //check if team is at EOF
        try {
            if (teamOutputPushbackIS.read()!= -1) {
                outputWrongAnswer("Trailing output");
                return 43;
            }
        } catch (IOException e) {
            log.severe("IOException processing team input stream (stdin): " + e.getMessage());
            e.printStackTrace();
        }
        
        outputSuccess("Correct output");
        return 42;  
    }//end method validate()
    
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
                case 0x08: return new String ("<tab>");
                case 0x0A: return new String ("<LF>");
                case 0x0D: return new String ("<CR>");
                default: return new String ("<?>");
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

        byte nextChar = 0;
        try {
            nextChar = (byte) inStream.read();
            while (nextChar!= -1 && Character.isWhitespace(nextChar)) {
                nextChar = (byte) inStream.read();
            }
        } catch (IOException e) {
            log.severe("IOException while flushing leading whitespace from input stream: " + e.getMessage());
            e.printStackTrace();
            return "DefaultValidator.getNextToken(): error reading stream";
        }
        
        //if nextChar = -1 then there were no non-whitespace chars left in the stream
        if (nextChar == -1) {
            return "";
        }
        
        //we got at least one non-whitespace char; pull out consecutive non-whitespace chars
        StringBuffer buf = new StringBuffer();
        buf.append(nextChar);
        try {
            nextChar = (byte) inStream.read();
            while (nextChar!= -1 && !Character.isWhitespace(nextChar)) {
                buf.append(nextChar);
                nextChar = (byte) inStream.read();
            }
            //if we pulled a whitespace char out, put it back
            if (nextChar!= -1 && Character.isWhitespace(nextChar)) {
                inStream.unread(nextChar);
            }
        } catch (IOException e) {
            log.severe("IOException while reading non-whitespace chars from input stream: " + e.getMessage());
            e.printStackTrace();
            return "DefaultValidator.getNextToken(): error reading stream";
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
     * 
     * @param judgeToken
     *            -- a token from the judge's answer input stream
     * @param teamToken
     *            -- a token from the team output stream
     * @return true if the two tokens are equivalent under the current validator settings
     */
    private boolean areEquivalent(String judgeToken, String teamToken) {

        // check if the judge has a floating-point number and we're applying tolerance checking
        if (useFloatTolerance && isFloat(judgeToken)) {
            double judgeVal = getDoubleValue(judgeToken);
            if (!isFloat(teamToken)) {
                outputWrongAnswer("Expected float in team output, got '" + teamToken + "'");
                return false;
            } else {
                // both judge and team have doubles; see if they are equal within tolerance
                double teamVal = getDoubleValue(teamToken);
                if (withinTolerance(judgeVal, teamVal)) {
                    return true;
                } else {
                    double diff = judgeVal - teamVal;
                    outputWrongAnswer("Float out of tolerance range: judge value = " + judgeVal + "; team value = " + teamVal
                            + "; difference = " + diff + " (abs tol = " + floatAbsTolerance + "; rel tol = " + floatRelTolerance + ")");
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
                outputWrongAnswer("String tokens mismatch (case-sensitive): judge token = '" + judgeToken + "'; team token = '" + teamToken + "'");
                return false;
            }

        }

        // we're not using case-sensitivity
        else if (judgeToken.equalsIgnoreCase(teamToken)) {
            return true;
        } else {
            outputWrongAnswer("String tokens mismatch (ignoring case): judge token = '" + judgeToken + "'; team token = '" + teamToken + "'");
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
    
    private void outputWrongAnswer (String message) {
        log.info("Validator returning failure: " + message );
    }
    
    private void outputSuccess (String message) {
        log.info("Validator returning success: " + message );
    }
    

    
    /**
     * Checks the judgesDataFile, judgesAnswerFile, and feedbackDirName to make sure they exist and are 
     * accessible as required.
     * @return true if all files/folders are accessible; false otherwise
     */
    private boolean validateFiles() {
        
        if (judgeDataFile==null || judgeAnswerFile==null || feedbackDirName==null) {
            return false;
        }
        
        File dataFile = new File(judgeDataFile);
        if (!dataFile.exists() || !dataFile.canRead()) {
            return false;
        }
        
        File answerFile = new File(judgeAnswerFile);
        if (!answerFile.exists() || !answerFile.canRead()) {
            return false;
        }
        
        File feedbackDir = new File(feedbackDirName);
        if (!feedbackDir.exists() || !feedbackDir.isDirectory() || !feedbackDir.canRead() || !feedbackDir.canWrite()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * The main entry point to the DefaultValidator when running as a stand-alone program.
     * The main program constructs a DefaultValidator object passing it the arguments received
     * by main(), then invokes the validator's validate() method.  The integer result returned by
     * validate() is then used as the main program exit code.
     * 
     * @param args judgeDataFile judgeAnswerFile feedbackDir [options] < teamOutputFile
     */
    public static void main(String[] args) {
        if (args.length < 3) {
            usage();
            System.exit(43);
        }
        
        log = new Log("DefaultValidator.log");
        StaticLog.setLog(log);
        log = StaticLog.getLog();            
        
        //copy any options into an array for passing to the constructor as a vararg
        String [] options = new String[args.length-3];
        for (int i=3; i<args.length; i++) {
            options[i-3] = args[i];
        }
        
        DefaultValidator df = new DefaultValidator(args[0], args[1], args[2], options);
        
        log.info("Invoking DefaultValidator.validate()");
        
        int result = df.validate();
        
        log.info("validate() returned code " + result);
        
        System.exit(result);
        
    }

    public static void usage() {
        System.err.println("Usage:  java DefaultValidator judges_data_file judges_answer_file feedbackdir" + File.separator + "  [options]  <  teamOutput");
        System.err.println ("  where judges_data_file and judges_answer_file must exist and be readable files,\n"
                + "  feedbackdir" + File.separator + " must exist and be a directory that is both readable and writable,\n"
                + "  and where [options] include:");
        System.err.println ("    case_sensitive");
        System.err.println ("    space_change_sensitive");
        System.err.println ("    float_absolute_tolerance E");
        System.err.println ("    float_relative_tolerance E");
        System.err.println ("    float_tolerance E   (shorthand for setting both absolute and relative tolerance)");        
    }

}
