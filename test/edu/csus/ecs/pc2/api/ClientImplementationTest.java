package edu.csus.ecs.pc2.api;

import java.util.Vector;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.api.implementation.ClientImplementation;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.security.Permission;

/**
 * JUnit for ClientImplementation.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ClientImplementationTest extends TestCase {

    public void testDisplayOnScoreboard() {

        IInternalContest contest = new SampleContest().createContest(1, 1, 4, 2, false);

        Vector<Account> accounts = contest.getAccounts(ClientType.Type.TEAM);

        Account account = accounts.firstElement();

        ClientImplementation clientImplementation = new ClientImplementation(account.getClientId(), contest);

        assertTrue("Display on Scoreboard ", clientImplementation.isDisplayableOnScoreboard());

        account.removePermission(Permission.Type.DISPLAY_ON_SCOREBOARD);

        assertTrue("Do not display on Scoreboard ", clientImplementation.isDisplayableOnScoreboard());

    }

}
