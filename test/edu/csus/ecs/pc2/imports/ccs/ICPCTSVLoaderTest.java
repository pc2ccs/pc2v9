package edu.csus.ecs.pc2.imports.ccs;

import java.io.File;
import java.io.IOException;

import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit Test ICPCTSVLoader.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ICPCTSVLoaderTest extends AbstractTestCase {

    private boolean debugMode = false;


    private String findTestDataFile(String filename) throws IOException {

        String name = getRootInputTestDataDirectory() + File.separator + "ccs" + File.separator + filename;
        assertFileExists(name);
        return name;
    }

    private void testTeamFile(String groupFile, String teamFileName, int expectedAccounts) throws Exception {
        if (groupFile != null) {
            ICPCTSVLoader.loadGroups(findTestDataFile(groupFile));
        }
        Account[] accounts = ICPCTSVLoader.loadAccounts(findTestDataFile(teamFileName));
        
        edu.csus.ecs.pc2.core.security.Permission.Type[] permList = getPermList(Type.TEAM);
        for (Account account : accounts) {
            assertEquals("Expecting same permissions ",  permList.length, account.getPermissionList().getList().length);
        }
        
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

    public void testTeams2load() throws Exception {
        String groupFile = "systest11.groups.tsv";
        String teamFileName = "systest11.teams.tsv";
        ICPCTSVLoader.loadGroups(findTestDataFile(groupFile));
        Account[] accounts = ICPCTSVLoader.loadAccounts(findTestDataFile(teamFileName));
        
        assertNotEquals("Missing InstituionCode", "", accounts[0].getInstitutionCode());
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
