package edu.csus.ecs.pc2.api;

import java.util.Vector;

import edu.csus.ecs.pc2.api.implementation.ClientImplementation;
import edu.csus.ecs.pc2.api.implementation.Contest;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * JUnit for ClientImplementation.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ClientImplementationTest extends AbstractTestCase {

    public void testDisplayOnScoreboard() {

        IInternalContest contest = new SampleContest().createContest(1, 1, 4, 2, false);

        Vector<Account> accounts = contest.getAccounts(ClientType.Type.TEAM);

        Account account = accounts.firstElement();

        ClientImplementation clientImplementation = new ClientImplementation(account.getClientId(), contest);

        assertTrue("Display on Scoreboard ", clientImplementation.isDisplayableOnScoreboard());

        account.removePermission(Permission.Type.DISPLAY_ON_SCOREBOARD);

        assertTrue("Do not display on Scoreboard ", clientImplementation.isDisplayableOnScoreboard());

    }

    public void testAllClientTypes() throws Exception {
        
        IInternalContest contest = new SampleContest().createContest(1, 1, 4, 2, false);
        
        Account account = getFirstAccount(contest, ClientType.Type.JUDGE);
        assertType(contest, account, edu.csus.ecs.pc2.api.IClient.ClientType.JUDGE_CLIENT);
        
        account = getFirstAccount(contest, ClientType.Type.TEAM);
        assertType(contest, account, edu.csus.ecs.pc2.api.IClient.ClientType.TEAM_CLIENT);

        account = getFirstAccount(contest, ClientType.Type.SCOREBOARD);
        assertType(contest, account, edu.csus.ecs.pc2.api.IClient.ClientType.SCOREBOARD_CLIENT);

        account = getFirstAccount(contest, ClientType.Type.ADMINISTRATOR);
        assertType(contest, account, edu.csus.ecs.pc2.api.IClient.ClientType.ADMIN_CLIENT);


        
    }

    private Account getFirstAccount(IInternalContest contest, Type type) {
        Vector<Account> accounts = contest.getAccounts(type);
        Account account = accounts.firstElement();
        return account;
    }

    private void assertType(IInternalContest contest, Account account, edu.csus.ecs.pc2.api.IClient.ClientType expected) {
        
        ClientImplementation clientImplementation = new ClientImplementation(account.getClientId(), contest);
        assertEquals("Expecting "+expected+" client type ", expected, clientImplementation.getType());
        
    }
    
    public void testAdminClientType() throws Exception {
        
        SampleContest sampleContest = new SampleContest();
        
        IInternalContest contest = sampleContest.createStandardContest();
        
        ensureOutputDirectory();
        String storageDirectory = getOutputDataDirectory();

        IInternalController controller = sampleContest.createController(contest, storageDirectory, true, false);
        Log log = createLog(getName());
        
        Account account = getFirstAccount(contest, ClientType.Type.ADMINISTRATOR);
        contest.setClientId(account.getClientId());

        Contest apiContestInst = new Contest(contest, controller, log);
        IContest apiContest = apiContestInst;

        IClient client = apiContest.getMyClient();
        
        assertType(contest, account, edu.csus.ecs.pc2.api.IClient.ClientType.ADMIN_CLIENT);
        
        assertEquals("Expecting admin ", edu.csus.ecs.pc2.api.IClient.ClientType.ADMIN_CLIENT, client.getType());
    }

    
}
