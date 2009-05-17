package edu.csus.ecs.pc2.api.implementation;

import java.util.Arrays;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.api.ITeam;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.ClientType.Type;

/**
 * JUnit test for API TeamImplementation.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class TeamImplementationTest extends TestCase {

    private Account getAccount(IInternalContest contest, ClientType.Type type, int accountNumber) {
        int siteNumber = contest.getSiteNumber();
        if (siteNumber == 0) {
            siteNumber = 1;
        }
        Account[] accounts = (Account[]) contest.getAccounts(type, siteNumber).toArray(new Account[contest.getAccounts(type, siteNumber).size()]);
        Arrays.sort(accounts, new AccountComparator());
        return accounts[accountNumber];
    }

    private IInternalContest createContest() {
        SampleContest sampleContest = new SampleContest();

        IInternalContest contest = sampleContest.createContest(1, 1, 12, 4, true);

        ContestTime contestTime = contest.getContestTime();
        contestTime.setElapsedMins(52);
        contestTime.startContestClock();
        contest.updateContestTime(contestTime);

        ClientId serverId = new ClientId(1, Type.SERVER, 0);
        contest.setClientId(serverId);

        return contest;

    }

    /**
     * Test method for {@link edu.csus.ecs.pc2.api.implementation.TeamImplementation#getLoginName()}.
     */
    public void testGetLoginName() {
        IInternalContest contest = createContest();
        Account account = getAccount(contest,Type.TEAM, 3);
        ITeam team = new TeamImplementation(account, contest);
        
        assertEquals(account.getClientId().getName(), team.getLoginName());
    }

    /**
     * Test method for {@link edu.csus.ecs.pc2.api.implementation.TeamImplementation#getDisplayName()}.
     */
    public void testGetDisplayName() {
        IInternalContest contest = createContest();
        Account account = getAccount(contest,Type.TEAM, 3);
        ITeam team = new TeamImplementation(account, contest);
        
        assertEquals(account.getDisplayName(), team.getDisplayName());
    }

    /**
     * Test method for {@link edu.csus.ecs.pc2.api.implementation.TeamImplementation#getGroup()}.
     */
    public void testGetGroup() {
        IInternalContest contest = createContest();
        Account account = getAccount(contest,Type.TEAM, 3);
        ITeam team = new TeamImplementation(account, contest);
        
        assertNull("Expected Group null for "+account.getClientId().getTripletKey(),team.getGroup());
        
        // Add and assign group
        
        Group group = new Group("Le Group");
        contest.addGroup(group);
        account.setGroupId(group.getElementId());
        ITeam teamWithGroup= new TeamImplementation(account, contest);
        
        assertNotNull("Expected non-null Group for "+account.getClientId().getTripletKey(),teamWithGroup.getGroup());
        
        assertEquals(group.getDisplayName(), teamWithGroup.getGroup().getName());
    }
}
