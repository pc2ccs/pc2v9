package edu.csus.ecs.pc2.imports.ccs;

import java.io.File;
import java.io.IOException;

import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit Test ICPCCSVLoader.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ICPCCSVLoaderTest extends AbstractTestCase {

    private boolean debugMode = false;


    private String findTestDataFile(String filename) throws IOException {

        String name = getRootInputTestDataDirectory() + File.separator + "ccs" + File.separator + filename;
        assertFileExists(name);
        return name;
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
