package edu.csus.ecs.pc2.core;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.exception.ContestSecurityException;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.packet.PacketFactory;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * JUnit test for PacketHandler.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PacketHandlerTest extends TestCase {

    private IContest contest;

    private IController controller;

    private PacketHandler packetHandler;

    protected void setUp() throws Exception {
        super.setUp();
        SampleContest sampleContest = new SampleContest();
        contest = sampleContest.createContest(2, 4, 12, 6, true);
        controller = sampleContest.createController(contest, true, false);

        // Add 22 random runs
        Run[] runs = sampleContest.createRandomRuns(contest, 22, true, true, true);
        sampleContest.addRuns(contest, runs, "pc2v9.ini");

        packetHandler = new PacketHandler(controller, contest);

    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test security setting in PacketHandler.
     *
     */
    public void testSecuritySet() {

        // Security Leve HIGH
        controller.setSecurityLevel(Controller.SECURITY_HIGH_LEVEL);

        ClientId teamId = contest.getAccounts(Type.TEAM).firstElement().getClientId();
        ClientId serverId = new ClientId(contest.getSiteNumber(), Type.SERVER, 0);

        Run run = contest.getRuns()[1];

        Packet packet = PacketFactory.createRunRequest(teamId, serverId, run, teamId, false);
        try {

            ConnectionHandlerID connectionHandlerID = new ConnectionHandlerID("Client " + teamId.toString());
            packetHandler.handlePacket(packet, connectionHandlerID);

            failTest("Expected packet to be NOT be allowed for: " + packet);

        } catch (ContestSecurityException sec) {

            packet = null; // fake statement to satisfy CodeStyle

        } catch (Exception e) {

            failTest("Expected packet to be NOT be allowed for: " + packet, e);

        }

        // Security Leve OFF
        controller.setSecurityLevel(Controller.SECURITY_NONE_LEVEL);

        packet = PacketFactory.createRunRequest(teamId, serverId, run, teamId, false);
        try {

            ConnectionHandlerID connectionHandlerID = new ConnectionHandlerID("Client " + teamId.toString());
            packetHandler.handlePacket(packet, connectionHandlerID);

            // success

        } catch (ContestSecurityException sec) {

            failTest("Expected packet to be NOT be allowed for: " + packet, sec);

        } catch (Exception e) {

            failTest("Expected packet to be NOT be allowed for: " + packet, e);
        }

    }

    private void failTest(String string, Exception e) {

        if (e != null) {
            e.printStackTrace(System.err);
        }
        assertTrue(string, false);
    }

    /**
     * Force a failure of the test
     * 
     * @param string
     */
    private void failTest(String string) {
        failTest(string, null);
    }
}
