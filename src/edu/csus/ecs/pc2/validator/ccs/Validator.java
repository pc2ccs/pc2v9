package edu.csus.ecs.pc2.validator.ccs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.ccs.CCSConstants;

/**
 * PC<sup>2</sup> Internal CCS Validator.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9wip/trunk/src/edu/csus/ecs/pc2/validator/Validator.java$
public class Validator {

    /**
     * Input data file name.
     */
    private String inputFileName;

    /**
     * Output file name.
     */
    private String outputFileName;

    /**
     * Answer file name.
     */
    private String answerFileName;

    /**
     * results output file name.
     */
    private String resultFileName;
    
    /**
     * Directory where validator stuff goes.
     */
    // TODO CCS 
//    private String feedbackDirectory = null;
    
    /**
     * Case insensitive comparison.
     */
    private boolean ignoreCaseFlag = false;

    /**
     * Yes judgement.
     */
    public static final String JUDGEMENT_YES = "accepted";

    /**
     * Wrong answer.
     * 
     * During compare, the output data/answer did not match.
     */
    public static final String JUDGEMENT_NO_WRONG_ANSWER = "No - Wrong Answer";

    /**
     * Additional description
     */
    private String extraValidatorDifference = "";

    private boolean debugFlag = false;

    private boolean allowFatalExit = true;

    /**
     * prints more info, like EOF line counts.
     */
    private boolean verbose = false;

    private void usage() {
        VersionInfo vi = new VersionInfo();
        System.out.println("Usage: java Validator [options] inputfilename answerfilename feedbackdir < teamoutput");
        System.out.println("Team's output is read from stdin");
        System.out.println();
        System.out.println("inputfilename - Judge's input filename");
        System.out.println("answerfilename - Judge's answer filename");
        System.out.println("feedbackdir - location for additional feedback information files");
        System.out.println();
        System.out.println("Where: ");
        System.out.println();
        System.out.println("options: ");
        System.out.println("  --help    this messge ");
        System.out.println("  --verbose more info like EOF: line counts");
        System.out.println("  --debug   a large amount of debugging output");
        System.out.println();
        System.out.println(vi.getSystemVersionInfo());
    }

    /**
     * Print usage message.
     */
    void printUsage() {
        usage();
    }
    
    /**
     * Exception (fatal error) in validator. 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    protected class ValidatorException extends Exception{

        private int exitCode = CCSConstants.VALIDATOR_CCS_ERROR_EXIT_CODE;
        
        /**
         * 
         */
        private static final long serialVersionUID = -3945738509765205483L;
        
        public ValidatorException(String string, int exitCode) {
            super(string);
            this.exitCode = exitCode;
        }

        public ValidatorException(String string) {
            super(string);
        }

        public int getExitCode() {
            return exitCode;
        }
    }

    /**
     * Requires that file exists, it does not exist prints description then exit.
     * 
     * @param filename
     * @param descriptionForFile
     * @throws ValidatorException 
     */
    void requireFile(String filename, String descriptionForFile) throws ValidatorException {
        File file = new File(filename);
        if (!file.isFile()) {
            throw new ValidatorException(descriptionForFile + " does not exist (" + filename + ")", 7);
        }
    }

    private void checkForOutputFile() throws ValidatorException {
        File checkFile = new File(outputFileName);
        if (!checkFile.isFile()) {
            // no output file created

            extraValidatorDifference = "NO Team output file created - nothing to read ";
            try {
                writeResultFile(resultFileName, JUDGEMENT_NO_WRONG_ANSWER, extraValidatorDifference);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new ValidatorException("Trouble writing result file " + resultFileName, 4);
            }
        }
    }
    
    private String simpleDiff(InputStream in, String filename) throws IOException  {
        InputStreamReader reader = new InputStreamReader(in);
        FileReader fileReader = new FileReader(filename);
        return simpleDiff(new BufferedReader(reader), new BufferedReader(fileReader));
        
    }

     private String simpleDiff(BufferedReader outputFile, BufferedReader answerFile) throws IOException {
        int lineNo = 1;

        String outputLine = outputFile.readLine();
        String answerLine = answerFile.readLine();

        while (outputLine != null && answerLine != null) {
            if (debugFlag) {
                System.out.println();
                System.out.println("O " + lineNo + ": " + outputLine);
                System.out.println("A " + lineNo + ": " + answerLine);
            }

            if (ignoreCaseFlag) {
                if (!outputLine.equalsIgnoreCase(answerLine)) {
                    if (debugFlag) {
                        System.out.println("  " + lineNo + "! " + compareLine(outputLine, answerLine));
                    }
                    extraValidatorDifference = "Answer and output file different (ignore case) at line  " + lineNo;
                    return JUDGEMENT_NO_WRONG_ANSWER;
                }
            } else {
                if (!outputLine.equals(answerLine)) {
                    if (debugFlag) {
                        System.out.println("  " + lineNo + "! " + compareLine(outputLine, answerLine));
                    }
                    extraValidatorDifference = "Answer and output file different at line  " + lineNo;
                    return JUDGEMENT_NO_WRONG_ANSWER;
                }
            }
            answerLine = answerFile.readLine();
            outputLine = outputFile.readLine();
            lineNo++;
        }

        if (outputLine != null && answerLine == null) {
            // more team output than judge's
            extraValidatorDifference = "Team's output file is longer than answer file, at line " + lineNo;
            return JUDGEMENT_NO_WRONG_ANSWER;
        }

        if (answerLine != null && outputLine == null) {
            // Not enough judgement
            extraValidatorDifference = "Team output file shorter than answer file, at line " + lineNo;
            return JUDGEMENT_NO_WRONG_ANSWER;
        }

        if (answerLine == null && verbose) {
            System.out.println("EOF: answer file " + lineNo + " lines.");
        }

        if (outputLine == null && verbose) {
            System.out.println("EOF: output file " + lineNo + " lines.");
        }

        return JUDGEMENT_YES;

    }

    /**
     * Write result file
     * 
     * @param filename
     * @param judgement
     * @param description
     * @throws FileNotFoundException
     */
    protected void writeResultFile(String filename, String judgement, String description) throws FileNotFoundException {

        if (debugFlag) {
            System.out.println();
            System.out.println("<result outcome = " + " \"" + judgement + "\" security = \"" + resultFileName + "\">" + description + "</result>");

        }

        PrintWriter fileWriter = new PrintWriter(new FileOutputStream(filename), true);

        fileWriter.println("<?xml version=\"1.0\"?>");
        fileWriter.println("<result outcome = " + " \"" + judgement + "\" security = \"" + resultFileName + "\">" + description + "</result>");

        fileWriter.close();
    }

    /**
     * Outputs a line of carets where line1 and line2 are different.
     * 
     * @param line1
     * @param line2
     * @return
     */
    protected String compareLine(String line1, String line2) {
        int len1 = line1.length();
        int len2 = line2.length();

        int maxLength = Math.max(len1, len2);
        int minLength = Math.min(len1, len2);
        StringBuffer outbuf = new StringBuffer();

        for (int i = 0; i < maxLength; i++) {
            char ch = ' ';

            if (i < minLength) {
                if (line1.charAt(i) != line2.charAt(i)) {
                    ch = '^';
                }
            } else {
                ch = '^';
            }

            outbuf.append(ch);
        }

        return new String(outbuf);
    }

    public int runValidator(String[] args) {
        
        int argNum = 0;
        int numberOfArgs = args.length;

        if (numberOfArgs > 0 && "--nofatal".equals(args[0])) {
            allowFatalExit = false;
            argNum++;
            numberOfArgs--;
        }

        if ("--help".equals(args[0])) {
            printUsage();
            if (allowFatalExit) {
                System.exit(0);
            }
            return CCSConstants.VALIDATOR_CCS_ERROR_EXIT_CODE;
        }

        if ("--verbose".equals(args[0])) {
            verbose = true;
            argNum++;
            numberOfArgs--;
        }

        if ("--debug".equals(args[0])) {
            debugFlag = true;
            argNum++;
            numberOfArgs--;
        }
        
        if ("--testYes".equals(args[0])) {
            return CCSConstants.VALIDATOR_JUDGED_SUCCESS_EXIT_CODE;
        }

        // TODO CCS 678 handled feedbackdir
        try {
            
            switch (argNum) {
                case 0:
                    throw new ValidatorException("Missing inputfilename");
                case 1:
                    throw new ValidatorException("Missing answerfilename");
                case 2:
                    throw new ValidatorException("Missing feedbackdir");
                default:
                    // fall through
                    break;
            }
             
            inputFileName = args[argNum++];
            outputFileName = args[argNum++];
            answerFileName = args[argNum++];
  
            // TODO CCS handle creation and writing of feedback directory.
            
//            if (argNum < numberOfArgs) {
//                feedbackDirectory = args[argNum++];
//            }

            if (!inputFileName.equals("-")) {
                requireFile(inputFileName, "input file");
            }

            if (!outputFileName.equals("-")) {
                checkForOutputFile();
            }

            if (!answerFileName.equals("-")) {
                requireFile(answerFileName, "answer file");
            }
            

            if (debugFlag) {
                System.out.println("Arguments: ");
                System.out.println("inputFileName = " + inputFileName);
                System.out.println("outputFileName = " + outputFileName);
                System.out.println("answerFileName = " + answerFileName);
                System.out.println("resultFileName = " + resultFileName);
                
//                System.out.println("pc2Option = " + pc2Option);
//                System.out.println("ignore case = " + ignoreCaseFlag);
                
                System.out.println();
            }
         
            // try {
            // writeResultFile(resultFileName, judgement, extraValidatorDifference);
            // } catch (FileNotFoundException e) {
            // System.out.println("Could not write result file" + resultFileName);
            // e.printStackTrace();
            // }

            // TODO CCS return codes for this method.
            // return CCSConstants.VALIDATOR_JUDGED_SUCCESS_EXIT_CODE;
            
            try {
                String results = simpleDiff(System.in, answerFileName);
                if (JUDGEMENT_YES.equals(results)) {
                    return CCSConstants.VALIDATOR_JUDGED_SUCCESS_EXIT_CODE;
                } else {
                    return CCSConstants.VALIDATOR_JUDGED_FAILURE_EXIT_CODE;
                }
            } catch (Exception e) {
                if (verbose) {
                    e.printStackTrace();
                }
            }

        } catch (ValidatorException e) {
            if (verbose) {
                e.printStackTrace();
            }
            System.err.println("Validator: "+e.getMessage());
            System.err.println("Validaotor error code " + e.getExitCode());
            return e.getExitCode();
        }

        return CCSConstants.VALIDATOR_JUDGED_FAILURE_EXIT_CODE;
    }
    

//    private int compareFiles() throws IOException {
//
//        // Open files
//
//        FileReader outputFileReader = new FileReader(outputFileName);
//        BufferedReader outputFile = new BufferedReader(outputFileReader);
//
//        FileReader answerFileReader = new FileReader(answerFileName);
//        BufferedReader answerFile = new BufferedReader(answerFileReader);
//
//        return simpleDiff(outputFile, answerFile);
//
//    }


    /**
     * 
     * @return true if System.exit can be used 
     */
    public boolean isAllowFatalExit() {
        return allowFatalExit;
    }

    /**
     * 
     * @param allowFatalExit
     *            if false not exit on fatal error
     */
    public void setAllowFatalExit(boolean allowFatalExit) {
        this.allowFatalExit = allowFatalExit;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public static void main(String[] args) {

        Validator validator = new Validator();
        int exitCode = validator.runValidator(args);
        System.err.println("debug 22 Exit with code "+exitCode);
        System.exit(exitCode);
    }
}

