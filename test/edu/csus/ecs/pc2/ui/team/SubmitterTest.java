package edu.csus.ecs.pc2.ui.team;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.util.JUnitUtilities;

/**
 * Test Submitter.
 * 
 * Many of these tests require a server to be running and configured.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class SubmitterTest extends TestCase {

    /**
     * Tests that only work if a server is running.
     */
    private boolean serverRunning = false;

    private static final String NL = System.getProperty("line.separator");

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        String testFilename = getTestName("Hello.java");
        if (!new File(testFilename).exists()) {
            writeHelloFile(testFilename);
            System.err.println("Created " + testFilename);
        }

    }

    private String getTestName(String filename) throws IOException {

        String testDir = "testdata";
        String projectPath = JUnitUtilities.locate(testDir);

        String testPath = projectPath + File.separator + testDir;

        File dir = new File(testPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        if (projectPath == null) {
            throw new IOException("Unable to locate " + testDir);
        }

        return testPath + File.separator + filename;
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

    private void writeLines(PrintWriter writer, String[] datalines) {
        for (String s : datalines) {
            writer.println(s);
        }
    }

    public void testSubmit() throws Exception {

        if (serverRunning) {

            String problem = "A";
            String language = null;
            String filename = getTestName("Hello.java");

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
            String filename = getTestName("Hello.java");

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

            String filename = getTestName("Hello.java");

            String[] args = { "--login", "3", filename };
            Submitter submitter = new Submitter(args);
            submitter.submitRun();

            System.out.println("Submitted Run for " + submitter.getSubmittedProblem().getName() + //
                    " " + filename + " using " + submitter.getSubmittedLanguage().getName() + //
                    " by " + submitter.getSubmittedUser().getLoginName());
        }
    }
}
