package edu.csus.ecs.pc2.imports.ccs;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.util.JUnitUtilities;

/**
 * Unit Test ICPCCSVLoader.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ICPCCSVLoaderTest extends TestCase {

    private boolean debugMode = false;

    private String testDirectory = ".";

    protected void setUp() throws Exception {
        super.setUp();

        String testDir = "testdata" + File.separator + "ccs";
        String projectPath = JUnitUtilities.locate(testDir);
        if (projectPath == null) {
            throw new Exception("Unable to locate " + testDir);
        }

        testDirectory = projectPath + File.separator + testDir;
    }

    private String findTestDataFile(String filename) throws IOException {

        String name = testDirectory + File.separator + filename;

        if (new File(name).exists()) {
            return name;
        } else {
            System.err.println("could not find " + name);
            throw new IOException("Unable to locate " + name);
        }
    }

    private void testTeamFile(String groupFile, String teamFileName, int expectedAccounts) throws Exception {
        if (groupFile != null) {
            ICPCCSVLoader.loadGroups(findTestDataFile(groupFile));
        }
        Account[] accounts = ICPCCSVLoader.loadAccounts(findTestDataFile(teamFileName));

        assertEquals("Expected accounts in " + teamFileName, expectedAccounts, accounts.length);
    }

    /**
     * Test load of groups and teams tsv files.
     * 
     * @throws Exception
     */
    public void testLoad() throws Exception {

        testTeamFile("groups.tsv", "team1.tsv", 2);

        testTeamFile("groups.tsv", "team2.tsv", 4);

    }

    public void testNegativeTeamLoad() throws Exception {

        try {
            testTeamFile("groups.tsv", "team3bad.tsv", 4);
        } catch (InvalidValueException e) {
            ok("Expect and got InvalidValueException");
        }

    }

    private void ok(String message) {
        if (debugMode) {
            System.out.println("Test passed - " + message);
        }
    }

}
