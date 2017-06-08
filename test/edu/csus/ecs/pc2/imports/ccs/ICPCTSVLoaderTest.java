package edu.csus.ecs.pc2.imports.ccs;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import edu.csus.ecs.pc2.convert.SampleCDP;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit Test ICPCTSVLoader.
 * 
 */
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
            assertEquals("Expecting same permissions ", permList.length, account.getPermissionList().getList().length);
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
    
    
    // TOOD Unit test for bug 1229
//    /**
//     * Test 7th and 8th column for teams.tsv.
//     * 
//     * Bug 1229 
//     * @throws Exception
//     */
//    public void testLoad78fields() throws Exception {
//        
//        String configDir = getTestSampleContestDirectory("sumitMTC") + File.separator + IContestLoader.CONFIG_DIRNAME;
//        assertDirectoryExists(configDir);
//        
////        startExplorer(configDir);
//
//        String groupFile = configDir + File.separator + "groups.tsv";
//        String teamFileName = configDir + File.separator + "teams.tsv";
//        
////        editFile(teamFileName);
//        
//        ICPCTSVLoader.loadGroups(groupFile);
//        Account[] accounts = ICPCTSVLoader.loadAccounts(teamFileName);
//
//        assertEquals("Missing CountryCode", "USA", accounts[0].getCountryCode());
//        assertEquals("Missing InstituionCode", "XXX", accounts[0].getInstitutionCode());
//    }

    private String getTestSampleContestDirectory(String contestDirName) {

        return "samps" + File.separator + "contests" + File.separator + contestDirName;
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

    /**
     * test extractTeamNumber.
     * @throws Exception
     */
    public void testextractTeamNumber() throws Exception {

        String[] data = {
                //
                "team1;1", //
                "team-001;001", //
                "team-1234;1234", //
                "team32;32", //
        };

        for (String line : data) {
            String[] fields = line.split(";");
            String actual = ICPCTSVLoader.extractTeamNumber(fields[0]);
            assertEquals("Expecting team number " + fields[1], fields[1], actual);
        }
    }


    /**
     * Test load accounts.tsv with teamN login names.
     * 
     * Bug 1241.
     * 
     * @throws Exception
     */
    public void testTeamNumberfromAccountsTSVFile() throws Exception {

        String configDir = SampleCDP.getDir() + IContestLoader.CONFIG_DIRNAME;
        //        startExplorer(configDir);

        // startExplorer(configDir);

        IInternalContest contest = new InternalContest();

        contest = loadYaml(contest, configDir);

        Problem[] problems = contest.getProblems();

        assertEquals("Expecting N problems", 13, problems.length);

        Vector<Account> va = contest.getAccounts(Type.TEAM);
        Account[] accounts =
                (Account[]) va.toArray(new Account[va.size()]);

        assertEquals("Expecting N accounts", 128, accounts.length);

        Arrays.sort(accounts, new AccountComparator());
        
        for (int i = 1; i < accounts.length + 1; i++) {
            assertEquals("Expeting team number " + i, "team" + i, accounts[i - 1].getTeamName());
        }

    }

    /**
     * Load contest from contest.yaml.
     * 
     * @param contest
     * @param configDir
     * @return
     */
    private IInternalContest loadYaml(IInternalContest contest, String configDir) {

        // startExplorer(configDir);

        String contestYamlFile = configDir + File.separator + IContestLoader.DEFAULT_CONTEST_YAML_FILENAME;

        assertFileExists(contestYamlFile, "Contest yaml file ");

        ContestSnakeYAMLLoader loader = new ContestSnakeYAMLLoader();

        contest = loader.fromYaml(contest, configDir, false);

        return contest;

    }
}
