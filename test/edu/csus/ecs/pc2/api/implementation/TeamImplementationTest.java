// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.api.implementation;

import java.util.Arrays;

import edu.csus.ecs.pc2.api.ITeam;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import junit.framework.TestCase;

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
        Account[] accounts = contest.getAccounts(type, siteNumber).toArray(new Account[contest.getAccounts(type, siteNumber).size()]);
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

        assertNull("Expected Group null for "+account.getClientId().getTripletKey(),team.getPrimaryGroup());

        // Add and assign group

        Group group = new Group("Le Group");
        contest.addGroup(group);
        account.clearGroups();
        account.addGroupId(group.getElementId(), true);
        ITeam teamWithGroup= new TeamImplementation(account, contest);

        assertNotNull("Expected non-null Group for "+account.getClientId().getTripletKey(),teamWithGroup.getPrimaryGroup());

        assertEquals(group.getDisplayName(), teamWithGroup.getPrimaryGroup().getName());
    }
}
