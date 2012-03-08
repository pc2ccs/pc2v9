package edu.csus.ecs.pc2.validator.ccs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
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
     * PC<sup>2</sup> comparison option.
     */
    private String pc2Option = "1";

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

    /**
     * Used by render judgements.
     */
    private static final String REGULAR_DIFF = "1";

    private static final String IGNORE_WHITESPACE_HEAD = "2";

    // private static final String IGNORE_LEADING_WHITESPACE = "3";

    private static final String IGNORE_ALL_WHITESPACE = "4";

    private static final String IGNORE_EMPTY_LINES = "5";

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
     * Requires that file exists, it does not exist prints description then exit.
     * 
     * @param filename
     * @param descriptionForFile
     */
    void requireFile(String filename, String descriptionForFile) {
        File file = new File(filename);
        if (!file.isFile()) {
            fatalError(descriptionForFile + " does not exist (" + filename + ")", 7);
        }
    }

    private void checkForOutputFile() {
        File checkFile = new File(outputFileName);
        if (!checkFile.isFile()) {
            // no output file created

            extraValidatorDifference = "NO Team output file created - nothing to read ";
            try {
                writeResultFile(resultFileName, JUDGEMENT_NO_WRONG_ANSWER, extraValidatorDifference);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                fatalError("Trouble writing result file " + resultFileName, 4);
            }
        }
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

    private String complexDiff(BufferedReader outputFile, BufferedReader answerFile, boolean ignoreLeadingWhitespace, boolean ignoreBlankLines, boolean ignoreWhiteSpace) throws IOException {
        int lineNo = 1;

        String outputLine;
        String answerLine;

        if (ignoreLeadingWhitespace || ignoreBlankLines) {
            outputLine = readPastBlankLines(outputFile);
            answerLine = readPastBlankLines(answerFile);
        } else {
            answerLine = answerFile.readLine();
            outputLine = outputFile.readLine();
        }

        while (outputLine != null && answerLine != null) {
            if (debugFlag) {
                System.out.println();
                System.out.println("O " + lineNo + ": " + outputLine);
                System.out.println("A " + lineNo + ": " + answerLine);
            }

            if (ignoreWhiteSpace) {
                outputLine = stripWhiteSpace(outputLine);
                answerLine = stripWhiteSpace(answerLine);
            }

            if (debugFlag) {
                System.out.println("o " + lineNo + ": " + outputLine);
                System.out.println("a " + lineNo + ": " + answerLine);
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
            if (ignoreBlankLines) {
                outputLine = readPastBlankLines(outputFile);
                answerLine = readPastBlankLines(answerFile);
            } else {
                answerLine = answerFile.readLine();
                outputLine = outputFile.readLine();
            }
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
     * returns string with no whitespace.
     * 
     * @see Character#isWhitespace(char)
     * @param outputLine
     * @return String without whitespace
     */
    private String stripWhiteSpace(String outputLine) {
        String outline = outputLine.trim();
        StringBuffer buf = new StringBuffer();

        if (outline.length() == 0) {
            return outline;
        }

        for (int i = 0; i < outputLine.length(); i++) {
            char ch = outputLine.charAt(i);
            if (!Character.isWhitespace(ch)) {
                buf.append(ch);
            }
        }

        return new String(buf);
    }

    /**
     * Read past blank lines.
     * 
     * @param buffReader
     * @param line
     * @return
     * @throws IOException
     */
    private String readPastBlankLines(BufferedReader buffReader) throws IOException {
        String line = buffReader.readLine();
        while (line != null && line.trim().length() == 0) {
            line = buffReader.readLine();
        }

        return line;
    }

    /**
     * Read team's output and judge's answer file, compare, return judgement.
     * 
     * @return judgement string.
     * @throws IOException
     */
    private String renderJudgement() throws IOException {
        boolean ignoreLeadingWhitespace = pc2Option.equals(IGNORE_WHITESPACE_HEAD);
        boolean ignoreWhiteSpace = pc2Option.equals(IGNORE_ALL_WHITESPACE);
        boolean ignoreEmptyLines = pc2Option.equals(IGNORE_EMPTY_LINES);

        boolean basicDiff = pc2Option.equals(REGULAR_DIFF);

        // Open files

        FileReader outputFileReader = new FileReader(outputFileName);
        BufferedReader outputFile = new BufferedReader(outputFileReader);

        FileReader answerFileReader = new FileReader(answerFileName);
        BufferedReader answerFile = new BufferedReader(answerFileReader);

        if (basicDiff) {
            return simpleDiff(outputFile, answerFile);
        }

        return complexDiff(outputFile, answerFile, ignoreLeadingWhitespace, ignoreEmptyLines, ignoreWhiteSpace);
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

        if (args.length == 0) {
            printUsage();
            if (allowFatalExit) {
                System.exit(0);
            }
        }
        
        if (args[argNum].equals("--help")) {
            printUsage();
            return 4;
        }

        int numberOfArgs = args.length;

        if (args[argNum].equals("--nofatal")) {
            allowFatalExit = false;
            argNum++;
            numberOfArgs--;
        }

        if (args[argNum].equals("--verbose")) {
            verbose = true;
            argNum++;
            numberOfArgs--;
        }

        if (args[argNum].equals("--debug")) {
            debugFlag = true;
            argNum++;
            numberOfArgs--;
        }

        /**
         * Internal override for validator for testing purposes,
         * return Yes as validator results.
         */
        if (args[argNum].equals("--testUsingYes")) {
            return CCSConstants.VALIDATOR_JUDGED_SUCCESS_EXIT_CODE;
        }
        
        
        if (numberOfArgs == 0) {
            return CCSConstants.VALIDATOR_CCS_ERROR_EXIT_CODE;
        }

        // TODO CCS 678 command line processing
        
        if (numberOfArgs != 5 && numberOfArgs != 7) {
            fatalError("Too few paramaters, see usage", 6);
        }

        inputFileName = args[argNum++];
        outputFileName = args[argNum++];
        answerFileName = args[argNum++];
        resultFileName = args[argNum++];

        if (!inputFileName.equals("-")) {
            requireFile(inputFileName, "input file");
        }

        if (!outputFileName.equals("-")) {
            checkForOutputFile();
        }

        if (!answerFileName.equals("-")) {
            requireFile(answerFileName, "answer file");
        }

        if (resultFileName.equals("-")) {
            fatalError("Invalid result filename: -  is not allowed.", 7);
        }

        if (args[argNum].equalsIgnoreCase("-pc2")) {
            argNum++;
            pc2Option = args[argNum++];
        }

        if (args[argNum].equalsIgnoreCase("true")) {
            ignoreCaseFlag = true;
        } else if (args[argNum].equalsIgnoreCase("false")) {
            ignoreCaseFlag = false;
        } else {
            fatalError("Invalid icflag expecting true or false, not: " + args[argNum], 8);
        }

//        if (debugFlag) {
//            System.out.println("Arguments: ");
//            System.out.println("inputFileName = " + inputFileName);
//            System.out.println("outputFileName = " + outputFileName);
//            System.out.println("answerFileName = " + answerFileName);
//            System.out.println("resultFileName = " + resultFileName);
//            System.out.println("pc2Option = " + pc2Option);
//            System.out.println("ignore case = " + ignoreCaseFlag);
//            System.out.println();
//        }
//
//        try {
//            writeResultFile(resultFileName, judgement, extraValidatorDifference);
//        } catch (FileNotFoundException e) {
//            System.out.println("Could not write result file" + resultFileName);
//            e.printStackTrace();
//        }
        
        // TODO CCS return codes for this method.
//        return CCSConstants.VALIDATOR_JUDGED_SUCCESS_EXIT_CODE;
        return CCSConstants.VALIDATOR_JUDGED_FAILURE_EXIT_CODE;
    }


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

    private void fatalError(String message, int exitCode) {
        System.err.println("Error: " + message);
        if (allowFatalExit) {
            System.exit(exitCode);
        }
    }

    public static void main(String[] args) {

        Validator validator = new Validator();
        int exitCode = validator.runValidator(args);
        System.exit(exitCode);
    }
}
