// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.pc2.Graders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.PrintStream;

import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.core.util.JUnitUtilities;
import edu.csus.ecs.pc2.graders.LegacyGrader;

public class LegacyGraderTest  extends AbstractTestCase {
    private static final String AllACFile = "allAC.txt";
    private static final String ACTLEWARTEFile = "acRTEWATLE.txt";
    private static final String IGNORESAMPLEFile = "ignoresample.txt";
    private static final String IGNORESAMPLENOSECRETFile = "ignoresamplenosecret.txt";

    private String loadDir = "testdata" + File.separator;


    @Override
    protected void setUp() throws Exception {
        String projectPath = JUnitUtilities.locate(loadDir);
        if (projectPath == null) {
            throw new Exception("Unable to locate " + loadDir);
        }
        File dir = new File(projectPath + File.separator + loadDir);
        if (dir.exists()) {
            loadDir = dir.toString() + File.separator;
        } else {
            System.err.println("could not find " + loadDir);
            throw new Exception("Unable to locate " + loadDir);
        }
        super.setUp();
    }

    private int runTest(String testName, String inputFile, String outputFile, String cmdline, String expResult) throws Exception
    {
        int result = 0;
        String [] args = new String[0];

        if(!cmdline.isEmpty()) {
            args = cmdline.split("\\s+");
        }

        LegacyGrader grader = new LegacyGrader();
        assertTrue("Expected no arguments to be valid ", grader.parseArguments(args));
        InputStream originalStdin = System.in;
        PrintStream originalStdout = System.out;

        System.setIn(new FileInputStream(inputFile));
        System.setOut(new PrintStream(new FileOutputStream(outputFile)));

        result = grader.processResults();
        System.setOut(originalStdout);
        System.setIn(originalStdin);

        // read answer if it worked
        if(result == 0) {
            String ans = null;
            try (BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {
                ans = reader.readLine().trim();
                assertEquals(testName + ": " + expResult + " ", expResult, ans);
            } catch(Exception e) {
                System.err.println("Can not read " + outputFile);
                result = -1;
            }
        }
        return(result);
    }

    /**
     * Tests whether all accepted works
     *
     */
    public void testAllAC() throws Exception {
        String inputTestDirectory = getDataDirectory("");
        String testDir = getOutputDataDirectory();
        String testDataFile = inputTestDirectory + AllACFile;
        String testOutputFile = testDir + File.separator + "result.txt";


        removeDirectory(testDir);
        ensureDirectory(testDir);

        assertEquals("Expected ALL AC with defaults to be 0 ", 0,
                runTest(this.getName(), testDataFile, testOutputFile, "", "AC 1000.0"));
        assertEquals("Expected ALL AC with SM avg to be 0 ", 0,
                runTest(this.getName(), testDataFile, testOutputFile, "avg", "AC 90.9090909090909"));
        assertEquals("Expected ALL AC with SM min to be 0 ", 0,
                runTest(this.getName(), testDataFile, testOutputFile, "min", "AC 0.0"));
        assertEquals("Expected ALL AC with SM max to be 0 ", 0,
                runTest(this.getName(), testDataFile, testOutputFile, "max", "AC 200.25"));

        testDataFile = inputTestDirectory + ACTLEWARTEFile;
        assertEquals("Expected ACTLEWARTE with defaults to be 0 ", 0,
                runTest(this.getName(), testDataFile, testOutputFile, "", "RTE 0"));
        assertEquals("Expected ACTLEWARTE with first_error to be 0 ", 0,
                runTest(this.getName(), testDataFile, testOutputFile, "first_error", "TLE 0"));
        assertEquals("Expected ACTLEWARTE with accept_if_any_accepted/sum to be 0 ", 0,
                runTest(this.getName(), testDataFile, testOutputFile, "accept_if_any_accepted sum", "AC 654.0"));
        assertEquals("Expected ACTLEWARTE with always_accept/avg to be 0 ", 0,
                runTest(this.getName(), testDataFile, testOutputFile, "accept_if_any_accepted avg", "AC 59.45454545454545"));

        // Test ignore_sample
        testDataFile = inputTestDirectory + IGNORESAMPLEFile;
        assertEquals("Expected ignore_sample to be 0 ", 0,
                runTest(this.getName(), testDataFile, testOutputFile, "ignore_sample", "AC 20.0"));

        // This file has exactly one line in it
        testDataFile = inputTestDirectory + IGNORESAMPLENOSECRETFile;
        assertEquals("Expected too few ignore_sample to be 8 ", LegacyGrader.GRADER_ERROR_IGNORE_SAMPLE,
                runTest(this.getName(), testDataFile, testOutputFile, "ignore_sample", "NOT USED"));

        // This file has too many lines in it if ignore_sample is used; there should be only 2 results in the file.
        testDataFile = inputTestDirectory + ACTLEWARTEFile;
        assertEquals("Expected too many ignore_sample to be 8 ", LegacyGrader.GRADER_ERROR_IGNORE_SAMPLE,
                runTest(this.getName(), testDataFile, testOutputFile, "ignore_sample", "NOT USED"));

    }
}
