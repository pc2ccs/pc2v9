package edu.csus.ecs.pc2.imports.ccs;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.util.JUnitUtilities;

/**
 * Test TeamList class.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: TeamListTest.java 178 2011-04-08 19:33:23Z laned $
 */

// $HeadURL:
// http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/test/edu/csus/ecs/pc2/imports/ccs/TeamListTest.java
// $
public class TeamListTest extends TestCase {

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

    private void testTeamFile(String groupFile, String teamFileName, int expectedAccounts) throws InvalidValueException, InvalidFileFormat, InvaildNumberFields, IOException {
        if (groupFile != null) {
            Group[] groups = new GroupList().loadlist(findTestDataFile(groupFile));
            TeamList.setGroups(groups);
        }
        Account[] accounts = TeamList.loadList(findTestDataFile(teamFileName));

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

    public void testFirstLine() throws Exception {
        // TODO write JUnit
    }

    public void testNegativeFirstLine() throws Exception {
        // TODO write JUnit
    }

    public void testNegativeSecondLine() throws Exception {
        // TODO write JUnit
    }

    public void testNegativeLastLine() throws Exception {
        // TODO write JUnit
    }

    public void testNegativeGroupNotFound() throws Exception {
        // TODO write JUnit
    }

}
