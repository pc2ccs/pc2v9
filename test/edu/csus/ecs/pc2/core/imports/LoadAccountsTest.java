package edu.csus.ecs.pc2.core.imports;

import java.io.File;

import edu.csus.ecs.pc2.core.list.AccountList;
import edu.csus.ecs.pc2.core.list.AccountList.PasswordType;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.security.Permission;

import junit.framework.TestCase;

/**
 * @author PC2
 * 
 */
public class LoadAccountsTest extends TestCase {

    private String loadDir = "testdata" + File.separator;

    private Site[] sites = new Site[2];

    private AccountList accountList = new AccountList();

    public LoadAccountsTest() {
        super();
    }

    public LoadAccountsTest(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        File dir = new File(loadDir);
        if (!dir.exists()) {
            // TODO, try to find this path in the environment
            dir = new File("projects" + File.separator + "pc2-9.1" + File.separator + loadDir);
            if (dir.exists()) {
                loadDir = dir.toString() + File.separator;
            } else {
                System.err.println("could not find " + loadDir);
            }
        }
        sites[0] = new Site("SOUTH", 2);
        sites[1] = new Site("NORTH", 1);
        accountList.generateNewAccounts(ClientType.Type.TEAM, 45, PasswordType.JOE, 1, true);
        accountList.generateNewAccounts(ClientType.Type.TEAM, 45, PasswordType.JOE, 2, true);
        accountList.generateNewAccounts(ClientType.Type.JUDGE, 1, PasswordType.JOE, 1, true);
    }

    public void testOne() {
        try {
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
            Account[] accounts = loadAccounts.fromTSVFile(loadDir + "loadaccount" + File.separator + "accounts.txt", accountList.getList(), new Group[0]);
            for (Account account2 : accounts) {
                assertTrue("account clone " + account2.getClientId(), accountList.getAccount(account2.getClientId()).isSameAs(account2));

            }
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue("exception", false);
        }
    }

    public void testTwo() {
        try {
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
            Account[] accounts = loadAccounts.fromTSVFile(loadDir + "loadaccount" + File.separator + "accounts.min.txt", accountList.getList(), new Group[0]);
            for (Account account2 : accounts) {
                assertTrue("account clone " + account2.getClientId(), accountList.getAccount(account2.getClientId()).isSameAs(account2));

            }
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue("exception", false);
        }
    }

    public void testThree() {
        try {
            LoadAccounts loadAccounts = new LoadAccounts();
            Account teamAccount = accountList.getAccount(new ClientId(1, ClientType.Type.TEAM, 1));
            accountList.update(teamAccount);
            assertFalse("team1 change_password default", teamAccount.isAllowed(Permission.Type.CHANGE_PASSWORD));
            // perm1 has site & account & permpassword & permlogin
            Account[] accounts = loadAccounts.fromTSVFile(loadDir + "loadaccount" + File.separator + "accounts.perm1.txt", accountList.getList(), new Group[0]);
            checkPermissions(accounts);
            // test for bug 154
            // perm2 has site & account & permpassword & permdisplay
            accounts = loadAccounts.fromTSVFile(loadDir + "loadaccount" + File.separator + "accounts.perm2.txt", accountList.getList(), new Group[0]);
            checkPermissions(accounts);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue("exception", false);
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
}
