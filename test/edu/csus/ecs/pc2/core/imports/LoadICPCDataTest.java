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
public class LoadICPCDataTest extends TestCase {

    private String loadDir = "testdata"+File.separator;
    private Site[] sites = new Site[2];
    private AccountList accountList = new AccountList();

    public LoadICPCDataTest() {
        super();
    }

    public LoadICPCDataTest(String arg0) {
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
            ICPCImportData importData = LoadICPCData.loadSites(loadDir+"icpcimport1", null);
            assertEquals("contestTitle", "The 2004 ACM Pacific Northwest Programming Contest", importData.getContestTitle());
            // this threw an exception on 1051 due to the unassociated groups
            Group[] groups = importData.getGroups();
            importData = LoadICPCData.loadAccounts(loadDir+"icpcimport1", groups, null);
            ICPCAccount account = importData.getAccounts()[1];
            assertEquals("2nd account short school", "SFU", account.getShortSchoolName());
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue("exception", false);
        }
    }
    public void testTwo() {
        try {
            Account[] accounts = accountList.getList();
            ICPCImportData importData = LoadICPCData.loadSites(loadDir+"icpcimport2", sites);
            assertEquals("contestTitle", "The 2004 ACM Pacific Northwest Programming Contest", importData.getContestTitle());
            Group[] groups = importData.getGroups();
            assertNotNull("groups", groups);
            for (int i = 0; i < groups.length; i++) {
                assertNotNull("group"+i+" not null", groups[i]);
            }
            assertEquals("site1name", "North - DeVry Seattle", groups[0].getDisplayName());
            assertEquals("site2name", "South - DeVry Fremont", groups[1].getDisplayName());
            assertEquals("site1 external id", 972, groups[0].getGroupId());
            for (int i = 0; i < groups.length; i++) {
                assertNotNull("group"+i+" site not null", groups[i].getSite());
            }
            assertEquals("site1", groups[0].getSite(), sites[0].getElementId());
            assertEquals("site2", groups[1].getSite(), sites[1].getElementId());
            importData = LoadICPCData.loadAccounts(loadDir+"icpcimport2", groups, accounts);
            ICPCAccount account = importData.getAccounts()[1];
            assertEquals("2nd account number", 2, account.getAccountNumber());
            assertNotNull("icpc account2 clientid not null", account.getClientId());
            for (int i = 0; i < accounts.length; i++) {
                ClientId clientId = accounts[i].getClientId();
                if (clientId.getClientNumber() == 2 && clientId.getSiteNumber() == 1) {
                    assertEquals(clientId, account.getClientId());
                    break;
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue("exception", false);
        }
    }
}
