package edu.csus.ecs.pc2.core;

import java.util.Vector;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.packet.PacketFactory;

/**
 * Unit Tests.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class InternalControllerTest extends TestCase {

    int siteNum = 1;

    /**
     * Test for Bug 825 for an Admin.
     * 
     * @throws Exception
     */
    public void testAdminRemoteExploit() throws Exception {

        ClientId requestingClientId = new ClientId(siteNum, Type.ADMINISTRATOR, 1);
        remoteExploit(requestingClientId);
    }

    /**
     * Test for Bug 825 for an Admin.
     * 
     * @throws Exception
     */
    public void testServerRemoteExploit() throws Exception {

        ClientId requestingClientId = new ClientId(siteNum, Type.SERVER, 1);
        remoteExploit(requestingClientId);
    }

    /**
     * Tests for Bug 825.
     * 
     * @param requestingClientId
     * 
     * @throws Exception
     */
    private void remoteExploit(ClientId requestingClientId) throws Exception {

        SampleContest sample = new SampleContest();

        IInternalContest contest = sample.createContest(siteNum, 1, 3, 1, true);

        contest.generateNewAccounts(Type.ADMINISTRATOR.toString(), 1, true);

        ClientId adminId = new ClientId(siteNum, Type.ADMINISTRATOR, 42);

        Account admin1 = contest.getAccount(adminId);
        assertNull(admin1);

        InternalController controller = new InternalController(contest);
        controller.setLog(new Log("ICT.log"));

        ClientId serverId = new ClientId(2, Type.SERVER, 1);

        int adminNum = 42;

        Packet packet = PacketFactory.createGenerateAccounts(
        /* source */requestingClientId,
        /* destination */serverId,
        /* siteNumber */siteNum,
        /* type */ClientType.Type.ADMINISTRATOR,
        /* count */1,
        /* startNumber */adminNum,
        /* isActive */true);

        controller.receiveObject(packet, null);

        admin1 = contest.getAccount(adminId);
        assertNull("There should be no admin admin42 account created", admin1);

        Vector<Account> accVector = contest.getAccounts(Type.ADMINISTRATOR);

        assertTrue("There should be one admin account ", accVector.size() == 1);

    }
}
