package edu.csus.ecs.pc2.validator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import edu.csus.ecs.pc2.VersionInfo;

/**
 * PC<sup>2</sup> Internal Validator.
 * 
 * Internal Validator - auto judgement generator. <br>
 * Compares team output with judge's answer file and outputs a XML file with a judgement and other information.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9wip/trunk/src/edu/csus/ecs/pc2/validator/Validator.java$
public class Validator {

    public static final String SVN_ID = "$Id$";

    // System.out.println("Usage: java Validator <inputfile name> <outputfile
    // name> <answerfile name> <resultfile name> <-pc2> [options] icflag");

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
    public static final String JUDGEMENT_YES = "Yes";

    /**
     * Presentation Error.
     * 
     * A format difference white space, etc.).
     */
    public static final String JUDGEMENT_NO_PRESENTATION_ERROR = "No - Presentation Error";

    /**
     * Wrong answer.
     * 
     * During compare, the output data/answer did not match.
     */
    public static final String JUDGEMENT_NO_WRONG_ANSWER = "No - Wrong Answer";

    /**
     * Indeterminant.
     * 
     * Covers all judgments
     */
    public static final String JUDGEMENT_NO_INDETERMINANT = "No - Indeterminant";

    /**
     * Additional description
     */
    private String extraValidatorDifference = "";

    private boolean debugFlag = false;

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
        System.out.println("Usage: java Validator <inputfile name> <outputfile name> <answerfile name> <resultfile name> <-pc2> [options] icflag");
        System.out.println("\n WHERE OPTIONS INCLUDE: ");
        System.out.println("<difftype> or <XML FILE for diff configration>");
        System.out.println("icflag - ignore case flag during diff/compare (true or false)");
        System.out.println();
        System.out.println("diff types are: ");
        System.out.println("1 - diff");
        System.out.println("2 - ignore whitespace at start of file");
        System.out.println("3 - ignore leading whitespace on lines");
        System.out.println("4 - ignore all whitespace on lines");
        System.out.println("5 - ignore empty lines");
        System.out.println("6   2 & 3");
        System.out.println("7   2 & 4");
        System.out.println("8   2 & 5");
        System.out.println("9   3 & 5");
        System.out.println("10  4 & 5");
        System.out.println();
        System.out.println("If a input, output or answer file is not needed/used use - for the name, example:");
        System.out.println("java Validator - sumit.dat sumit.ans result.xml -pc2 1 true");
        System.out.println();
        System.out.println(vi.getSystemVersionInfo());
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        Validator validator = new Validator();
        validator.runValidator(args);
    }

    void printUsage() {
        usage();
        System.exit(4);
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

        if (answerLine == null) {
            System.out.println("EOF: answer file " + lineNo + " lines.");
        }

        if (outputLine == null) {
            System.out.println("EOF: output file" + lineNo + " lines.");
        }

        return JUDGEMENT_YES;

    }

    private String complexDiff(BufferedReader outputFile, BufferedReader answerFile, boolean ignoreBlankLines, boolean ignoreWhiteSpace) throws IOException {
        int lineNo = 1;

        String outputLine;
        String answerLine;

        if (ignoreBlankLines) {
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

        if (answerLine == null) {
            System.out.println("EOF: answer file " + lineNo + " lines.");
        }

        if (outputLine == null) {
            System.out.println("EOF: output file" + lineNo + " lines.");
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

        // Ignore Leading Whitespace

        String outputLine = outputFile.readLine();
        if (ignoreLeadingWhitespace && outputLine.length() == 0) {
            while (outputLine != null && outputLine.length() == 0) {
                outputLine = outputFile.readLine();
            }
        }

        String answerLine = answerFile.readLine();
        if (ignoreLeadingWhitespace && answerLine.length() == 0) {
            while (answerLine != null && answerLine.length() == 0) {
                answerLine = answerFile.readLine();
            }
        }

        return complexDiff(outputFile, answerFile, ignoreEmptyLines, ignoreWhiteSpace);

    }

    private void fatalError(String message, int exitCode) {
        System.err.println("Error: " + message);
        System.exit(exitCode);
    }

    /**
     * Write result file
     * 
     * @param filename
     * @param judgement
     * @param description
     * @throws FileNotFoundException
     */
    void writeResultFile(String filename, String judgement, String description) throws FileNotFoundException {

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
    private String compareLine(String line1, String line2) {
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

    public String[] loadFile(String filename) {
        Vector<String> lines = new Vector<String>();

        if (filename == null) {
            throw new IllegalArgumentException("filename is null");
        }

        try {
            if (!new File(filename).exists()) {
                return new String[0];
            }

            FileReader fileReader = new FileReader(filename);
            BufferedReader in = new BufferedReader(fileReader);
            String line = in.readLine();
            while (line != null) {
                lines.addElement(line);
                line = in.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (lines.size() == 0) {
            return new String[0];
        }

        String[] out = new String[lines.size()];

        for (int i = 0; i < lines.size(); i++) {
            out[i] = lines.elementAt(i);
        }

        return out;

    }

    public void runValidator(String[] args) {
        int argNum = 0;

        if (args.length == 0) {
            printUsage();
        }
        if (args[argNum].equals("--help")) {
            printUsage();
        }

        if (args[argNum].equals("--debug")) {
            debugFlag = true;
            argNum++;
            if (args.length != 6 && args.length != 8) {
                fatalError("Too few paramaters, see usage", 6);
            }
        } else {
            if (args.length != 5 && args.length != 7) {
                fatalError("Too few paramaters, see usage", 6);
            }
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

        if (debugFlag) {
            System.out.println("Arguments: ");
            System.out.println("inputFileName = " + inputFileName);
            System.out.println("outputFileName = " + outputFileName);
            System.out.println("answerFileName = " + answerFileName);
            System.out.println("resultFileName = " + resultFileName);
            System.out.println("pc2Option = " + pc2Option);
            System.out.println("ignore case = " + ignoreCaseFlag);
            System.out.println();
        }

        String judgement = JUDGEMENT_NO_INDETERMINANT;

        try {
            judgement = renderJudgement();
        } catch (IOException e1) {
            System.err.println("Error reading output or answer file ");
            e1.printStackTrace(System.err);
        }

        try {
            writeResultFile(resultFileName, judgement, extraValidatorDifference);
        } catch (FileNotFoundException e) {
            System.out.println("Could not write result file" + resultFileName);
            e.printStackTrace();
        }

    }
    
    /**
     * return validator command with input command
     * @param whichCommand command line option for validator
     * @return
     */
    public String getInternalValidatorCommandLine(int whichCommand, boolean ignoreCase) {

        String javaCmd = "java edu.csus.ecs.pc2.validator.Validator";
        String commandLine = null;
        
        commandLine = javaCmd  + " {:infile} {:outfile} {:ansfile} {:resfile} -pc2 " + whichCommand;

        if (ignoreCase) {
            commandLine = commandLine + " true";
        } else {
            commandLine = commandLine + " false";
        }

        return commandLine;
    }
}
