package edu.csus.ecs.pc2.ui.team;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.exception.CommandLineErrorException;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Test Submitter.
 * 
 * Many of these tests require a server to be running and configured.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class SubmitterTest extends AbstractTestCase {

    /**
     * Tests that only work if a server is running.
     */
    private boolean serverRunning = false;

    private boolean debugFlag = false;

    private static final String NL = System.getProperty("line.separator");

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Utilities.insureDir(getDataDirectory());
        String testFilename = getTestFilename(HELLO_SOURCE_FILENAME);
        if (!new File(testFilename).exists()) {
            writeHelloFile(testFilename);
            if (debugFlag) {
                System.err.println("Created " + testFilename);
            }
        }
    }

    public void testOne() throws Exception {

        /**
         * list info for team 2
         */

        if (serverRunning) {

            new Submitter("2").listInfo();

            new Submitter("2").listRuns();
        }

    }

    private String writeHelloFile(String filename) throws FileNotFoundException {

        PrintWriter writer = new PrintWriter(new FileOutputStream(filename, false), true);
        String[] datalines = { "//" + NL, //
                "// $Id$" + NL, //
                "//" + NL, //
                "" + NL, //
                "public class hello {" + NL, //
                "    public static void main(String[] args) " + NL, //
                "    {" + NL, //
                "         System.out.println(\"Hello World.\");" + NL, //
                "    }" + NL, //
                "}" + NL, //
                "" + NL, //
                "// eof" + NL, //
                "" + NL, //
        };

        writeLines(writer, datalines);
        writer.close();
        writer = null;
        return filename;
    }

    public void testSubmit() throws Exception {

        if (serverRunning) {

            String problem = "A";
            String language = null;
            String filename = getTestFilename(HELLO_SOURCE_FILENAME);

            Submitter submitter = new Submitter("2");
            submitter.submitRun(filename, problem, language);

            System.out.println("Submitted Run for " + submitter.getSubmittedProblem().getName() + //
                    " " + filename + " using " + submitter.getSubmittedLanguage().getName() + //
                    " by " + submitter.getSubmittedUser().getLoginName());

        }

    }

    public void testSubmitNegative() throws Exception {

        if (serverRunning) {

            String problem = "A";
            String language = null;
            String filename = getTestFilename(HELLO_SOURCE_FILENAME);

            Submitter submitter = new Submitter("4");
            submitter.submitRun(filename, problem, language);

            System.out.println("Submitted Run for " + submitter.getSubmittedProblem().getName() + //
                    " " + filename + " using " + submitter.getSubmittedLanguage().getName() + //
                    " by " + submitter.getSubmittedUser().getLoginName());
        }

    }

    public void testSubmit02() throws Exception {

        // submit [--list] [--help] [--check] filename [problem [language]]", //

        if (serverRunning) {

            String filename = getTestFilename(HELLO_SOURCE_FILENAME);

            String[] args = { "--login", "3", filename };
            Submitter submitter = new Submitter(args);
            submitter.submitRun();

            System.out.println("Submitted Run for " + submitter.getSubmittedProblem().getName() + //
                    " " + filename + " using " + submitter.getSubmittedLanguage().getName() + //
                    " by " + submitter.getSubmittedUser().getLoginName());
        }
    }

    /**
     * Test if CCS options should be present.
     * 
     * If any CCS option is present on the command line then all required options should be on command line.
     * 
     */
    public void testMissingOptions() {

        String[][] testCases = { //
        { "-p", "A" }, // <problem short-name>
                { "-l", "Java" }, // <language name>
                { "-u", "team4" }, // <team id>
                { "-w", "t4Pass" }, // <team password>
                { "-m", "hello.c" }, // <main source filename>
                { "-d", "stTestLP" }, // <directory for main source and other source files>
                { "-t", "4533" }, // <contest-time for submission>
        };

        try {
            for (String[] args : testCases) {
                Submitter submitter = new Submitter();
                submitter.setShowAllMissingOptions(false);
                submitter.loadVariables(args);
                fail("Should have thrown CommandLineErrorException, missing command line option for " + Arrays.toString(args));
            }
        } catch (CommandLineErrorException e) {
            assert true;
        }
    }

    /**
     * Tests for valid CCS submitter command line.
     * 
     * @throws CommandLineErrorException
     */
    public void testLoadPositive() throws CommandLineErrorException {

        String testPath = getDataDirectory() + File.separator;

        String[] args = { //
        "-p", "A", // <problem short-name>
                "-l", "Java", // <language name>
                "-u", "team4", // <team id>
                "-w", "t4Pass", // <team password>
                "-m", "hello.c", // <main source filename>
                "-d", testPath + "stTestLP", // <directory for main source and other source files>
        };

        Submitter submitter = new Submitter();

        int numberMissingCCSArguments = submitter.numberMissingArguments(args, Submitter.CCS_REQUIRED_OPTIONS_LIST);
        assertEquals("Expecting Number of CCS Options ", 0, numberMissingCCSArguments);

        submitter.loadVariables(args);

        String[] argsWithOpt = {//
        "-p", "A", // <problem short-name>
                "-l", "Java", // <language name>
                "-u", "team4", // <team id>
                "-w", "t4Pass", // <team password>
                "-m", "hello.c", // <main source filename>
                "-d", testPath + "stTestLP", // <directory for main source and other source files>
                "-t", "4533", // <contest-time for submission>
        };

        submitter = new Submitter();

        numberMissingCCSArguments = submitter.numberMissingArguments(args, Submitter.CCS_REQUIRED_OPTIONS_LIST);
        assertEquals("Expecting Number of CCS Options ", 0, numberMissingCCSArguments);

        submitter.loadVariables(argsWithOpt);

    }
    
    
    public static void main(String[] args) {
        
//        String [] arguments = {"--helpCCS"};
        String [] arguments = {"--help"};
        
        try {
            new Submitter(arguments);
        } catch (CommandLineErrorException e) {
            e.printStackTrace();
        }
        
        
    }
}
