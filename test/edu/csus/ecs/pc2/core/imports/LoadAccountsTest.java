package edu.csus.ecs.pc2.core.imports;

import java.io.File;

import edu.csus.ecs.pc2.core.list.AccountList;
import edu.csus.ecs.pc2.core.list.AccountList.PasswordType;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.Site;

import junit.framework.TestCase;

/**
 * @author PC2
 *
 */
public class LoadAccountsTest extends TestCase {

    private String loadDir = "testdata"+File.separator;
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
            dir = new File("projects" + File.separator +"pc2v9" + File.separator + loadDir);
            if (dir.exists()) {
                loadDir=dir.toString() + File.separator;
            } else {
                System.err.println("could not find " + loadDir);
            }
        }
        sites[0] = new Site("SOUTH", 2);
        sites[1] = new Site("NORTH", 1);
        accountList.generateNewAccounts(ClientType.Type.TEAM, 45 ,PasswordType.JOE, 1, true);
        accountList.generateNewAccounts(ClientType.Type.TEAM, 45 ,PasswordType.JOE, 2, true);
    }

    public void testOne() {
        try {
            LoadAccounts loadAccounts = new LoadAccounts();
            Account account = accountList.getAccount(new ClientId(1,ClientType.Type.TEAM, 1));
            Group group = new Group("Group 1");
            account.setGroupId(group.getElementId());
            // these were broken in 1052
            account.setLongSchoolName("California State University, Sacramento");
            account.setShortSchoolName("CSUS");
            account.setExternalId("1234");
            account.setAliasName("orange");
            account.setExternalName("Hornet 1");
            accountList.update(account);
            Account[] accounts = loadAccounts.fromTSVFile(loadDir+"loadaccount"+File.separator+"accounts.txt", accountList.getList(), new Group[0]);
            for (Account account2 : accounts) {
                assertTrue("account clone "+account2.getClientId(), accountList.getAccount(account2.getClientId()).isSameAs(account2));
                
            }
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue("exception", false);
        }
    }
}
