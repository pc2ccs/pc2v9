// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.imports;

import java.io.File;
import java.util.Arrays;

import edu.csus.ecs.pc2.core.imports.ExportAccounts.Formats;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.AccountList;
import edu.csus.ecs.pc2.core.list.AccountList.PasswordType;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test.
 * 
 * @author Troy
 */
public class LoadAccountsTest extends AbstractTestCase {

    private Site[] sites = new Site[2];

    private AccountList accountList = new AccountList();
    
    private SampleContest sample = new SampleContest();

    public LoadAccountsTest() {
        super();
    }

    public LoadAccountsTest(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        
        sites[0] = new Site("SOUTH", 2);
        sites[1] = new Site("NORTH", 1);
        accountList.generateNewAccounts(ClientType.Type.TEAM, 45, PasswordType.JOE, 1, true);
        accountList.generateNewAccounts(ClientType.Type.TEAM, 45, PasswordType.JOE, 2, true);
        accountList.generateNewAccounts(ClientType.Type.JUDGE, 1, PasswordType.JOE, 1, true);
    }

    public void testOne() throws Exception {
            
            String testDataDir = getRootInputTestDataDirectory();
            String accountsFilename = testDataDir + File.separator +  "loadaccount" + File.separator + "accounts.txt";
            assertFileExists(accountsFilename);
            
            LoadAccounts loadAccounts = new LoadAccounts();
            Account account = accountList.getAccount(new ClientId(1, ClientType.Type.TEAM, 1));
            Group group = new Group("Group 1");
            account.setGroupId(group.getElementId());
            // these were broken in 1052
            account.setLongSchoolName("California State University, Sacramento");
            account.setShortSchoolName("CSUS");
            account.setExternalId("1234");
            account.setAliasName("orange");
            account.setExternalName("Hornet 1");
            accountList.update(account);
            Account[] accounts = loadAccounts.fromTSVFile(accountsFilename, accountList.getList(), new Group[0]);
            for (Account account2 : accounts) {
                assertTrue("account clone " + account2.getClientId(), accountList.getAccount(account2.getClientId()).isSameAs(account2));

            }
    }

    public void testTwo() throws Exception {
        
            String testDataDir = getRootInputTestDataDirectory();
            String accountsFilename = testDataDir + File.separator + File.separator + "loadaccount" + File.separator + "accounts.min.txt";
            assertFileExists(accountsFilename);
            
            LoadAccounts loadAccounts = new LoadAccounts();
            Account account = accountList.getAccount(new ClientId(1, ClientType.Type.TEAM, 1));
            Group group = new Group("Group 1");
            account.setGroupId(group.getElementId());
            // these were broken in 1052
            account.setLongSchoolName("California State University, Sacramento");
            account.setShortSchoolName("CSUS");
            account.setExternalId("1234");
            account.setAliasName("orange");
            account.setExternalName("Hornet 1");
            accountList.update(account);
            // min only has site & account
            Account[] accounts = loadAccounts.fromTSVFile(accountsFilename, accountList.getList(), new Group[0]);
            for (Account account2 : accounts) {
                assertTrue("account clone " + account2.getClientId(), accountList.getAccount(account2.getClientId()).isSameAs(account2));

            }
    }

    public void testThree() throws Exception {
        
        String testDataDir = getRootInputTestDataDirectory();
        String accountsFilename = testDataDir + File.separator + File.separator + "loadaccount" + File.separator + "accounts.perm1.txt";
        assertFileExists(accountsFilename);
        
        
            LoadAccounts loadAccounts = new LoadAccounts();
            Account teamAccount = accountList.getAccount(new ClientId(1, ClientType.Type.TEAM, 1));
            accountList.update(teamAccount);
            assertFalse("team1 change_password default", teamAccount.isAllowed(Permission.Type.CHANGE_PASSWORD));
            // perm1 has site & account & permpassword & permlogin
            Account[] accounts = loadAccounts.fromTSVFile(accountsFilename, accountList.getList(), new Group[0]);
            checkPermissions(accounts);
            // test for bug 154
            // perm2 has site & account & permpassword & permdisplay
            accounts = loadAccounts.fromTSVFile(accountsFilename, accountList.getList(), new Group[0]);
            checkPermissions(accounts);
    }
    public void testFour() throws Exception {
        
        String testDataDir = getRootInputTestDataDirectory();
        String accountsFilename = testDataDir + File.separator + "loadaccount" + File.separator + "accounts.649.txt";
        assertFileExists(accountsFilename);

            LoadAccounts loadAccounts = new LoadAccounts();
            Account teamAccount = accountList.getAccount(new ClientId(1, ClientType.Type.TEAM, 1));
            accountList.update(teamAccount);
            
            
            // 649 is checking group column vs externalid column
            Group[] groups = new Group[1];
            groups[0] = new Group("Lower");
            Account[] accounts = loadAccounts.fromTSVFile(accountsFilename, accountList.getList(), groups);
            for (int i = 0; i < accounts.length; i++) {
                if (accounts[i].getClientId().equals(teamAccount.getClientId())) {
                    assertEquals("group load",groups[0].getElementId(),accounts[i].getGroupId());
                    assertEquals("externalId load","10", accounts[i].getExternalId());
                    break;
                }
            }
    }
    public void testScoreAdjustment() throws Exception {
        
        String testDataDir = getRootInputTestDataDirectory();
        String accountsFilename = testDataDir + File.separator + "loadaccount" + File.separator + "accounts.scoreadjustment.txt";
        assertFileExists(accountsFilename);
        
        LoadAccounts loadAccounts = new LoadAccounts();
        Account teamAccount = accountList.getAccount(new ClientId(1, ClientType.Type.TEAM, 1));
        accountList.update(teamAccount);

            loadAccounts = new LoadAccounts();
            Account[] accounts = loadAccounts.fromTSVFile(accountsFilename, accountList.getList(), new Group[0]);
            for (int i = 0; i < accounts.length; i++) {
                Account account = accounts[i];
                if (account.getDisplayName().equals("team1")) {
                    assertEquals("scoreadjustment change expected", 10, account.getScoringAdjustment());
                } else if (account.getDisplayName().equals("team2")) {
                    assertEquals("scoreadjustment change not expected", 0, account.getScoringAdjustment());
                } else if (account.getDisplayName().equals("team3")) {
                    assertEquals("scoreadjustment negative change expected", -5, account.getScoringAdjustment());
                }
            }
    }
    
    public void checkPermissions(Account[] accounts) {
        for (Account account : accounts) {
            if (account.getClientId().getClientType().equals(ClientType.Type.TEAM)) {
                assertTrue("team1 change_password", account.isAllowed(Permission.Type.CHANGE_PASSWORD));
            }
            if (account.getClientId().getClientType().equals(ClientType.Type.JUDGE)) {
                assertFalse("judge1 change_password", account.isAllowed(Permission.Type.CHANGE_PASSWORD));
            }
            assertTrue(account.getClientId()+" permLogin", account.isAllowed(Permission.Type.LOGIN));
        }

    }
    
    protected void generateFile(IInternalContest contest,  Formats format, String outputFile) throws Exception {
        
        Group[] groups = contest.getGroups();
        Account[] accounts = SampleContest.getTeamAccounts(contest);
        
        assertEquals("Team accounts ", 120, accounts.length);
        assertEquals("Groups ", 2, groups.length);
        
        ExportAccounts.saveAccounts(format, accounts, groups, new File(outputFile));
        
        if (ExportAccounts.getException() != null){
            throw ExportAccounts.getException();
        }
        
    }
    
    /**
     * Test load for 3 new institution fields.
     * 
     * Bug 1067 test.
     * 
     * @throws Exception
     */
    public void testLoadTXTFile() throws Exception {

        String dataDir = getDataDirectory(this.getName());
        ensureDirectory(dataDir);
        // startExplorer(dataDir);

        /**
         * TXT is tsv file format.
         */
        Formats format = Formats.TXT;

        IInternalContest contest = new SampleContest().createStandardContest();
        sample.assignSampleGroups(contest, "Group Thing One", "Group Thing Two");

        String inputTSVFilename = dataDir + File.separator + this.getName() + "." + format.toString().toLowerCase();

        // editFile(inputTSVFilename);
        // generateFile(contest, format, inputTSVFilename);

        assertFileExists(inputTSVFilename);

        Account[] existingAccounts = contest.getAccounts();
        Group[] groups = contest.getGroups();

        LoadAccounts loadAccounts = new LoadAccounts();

        Account[] newAccounts = loadAccounts.fromTSVFile(inputTSVFilename, existingAccounts, groups);
        contest.updateAccounts(newAccounts);

        existingAccounts = contest.getAccounts();
        testAccountFields(contest, 3, "Univerity 3 Long", "University 3 Short", "USA");

    }

    private void testAccountFields( IInternalContest contest, int teamNumber, String longInst, String shortInst, String countryCode) {
        
        Account[] accounts = SampleContest.getTeamAccounts(contest, 3);
        Arrays.sort(accounts, new AccountComparator());
        
        Account team = accounts[teamNumber - 1];
        
        testAccountFields(team, longInst, shortInst, countryCode);

//        dumpTeam(team);
//        String teamId = team.getClientId().getTripletKey();
//        assertEquals("int long name, " +teamId, longInst, team.getLongSchoolName());
//        assertEquals("int short name, team " + teamId, shortInst, team.getShortSchoolName());
//        assertEquals("country code, team " + teamId, countryCode, team.getCountryCode());
        
    }

    private void testAccountFields(Account team, String longInst, String shortInst, String countryCode) {

        // dumpTeam(team);
        String teamId = team.getClientId().getTripletKey();
        assertEquals("int long name, " + teamId, longInst, team.getLongSchoolName());
        assertEquals("int short name, team " + teamId, shortInst, team.getShortSchoolName());
        assertEquals("country code, team " + teamId, countryCode, team.getCountryCode());

    }

    protected void dumpTeam(Account team) {

        String teamId = team.getClientId().getTripletKey();

        System.out.println("Account       = " + team + " " + teamId);
        System.out.println("Long name     = " + team.getLongSchoolName());
        System.out.println("short name    = " + team.getShortSchoolName());
        System.out.println("country code = " + team.getCountryCode());

    }

  
}
