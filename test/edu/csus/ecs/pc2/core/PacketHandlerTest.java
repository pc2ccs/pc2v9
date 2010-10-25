package edu.csus.ecs.pc2.core;

import java.io.File;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.exception.ContestSecurityException;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.packet.PacketFactory;
import edu.csus.ecs.pc2.core.security.FileStorage;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;
import edu.csus.ecs.pc2.core.util.JUnitUtilities;

/**
 * JUnit test for PacketHandler.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PacketHandlerTest extends TestCase {

    private IInternalContest contest;

    private IInternalController controller;

    protected void setUp() throws Exception {
        super.setUp();
        SampleContest sampleContest = new SampleContest();
        contest = sampleContest.createContest(2, 4, 12, 6, true);
        
        String testDirectory = SampleContest.getTestDirectoryName("pht");
        
        FileStorage storage = new FileStorage(testDirectory);
        contest.setStorage(storage);
        
        controller = sampleContest.createController(contest, testDirectory, true, false);

        // Directory where test data is
        String testDir = "testdata";
        String projectPath=JUnitUtilities.locate(testDir);
        if (projectPath == null) {
            throw new Exception("Unable to locate "+testDir);
        }

        String loadFile = projectPath + File.separator+ testDir + File.separator + "Sumit.java";
        File dir = new File(loadFile);
        if (!dir.exists()) {
            System.err.println("could not find " + loadFile);
            throw new Exception("Unable to locate "+loadFile);
       } else {
            loadFile = dir.getAbsolutePath();
        }

        // Add 22 random runs
        Run[] runs = sampleContest.createRandomRuns(contest, 22, true, true, true);
        sampleContest.addRuns(contest, runs, loadFile);


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
        controller.setSecurityLevel(InternalController.SECURITY_HIGH_LEVEL);

        ClientId teamId = contest.getAccounts(Type.TEAM).firstElement().getClientId();
        ClientId serverId = new ClientId(contest.getSiteNumber(), Type.SERVER, 0);

        Run run = contest.getRuns()[1];
        
        Profile profile = new Profile("testSecuritySet Profile");
        contest.setProfile(profile);

        Packet packet = PacketFactory.createRunRequest(teamId, serverId, run, teamId, false, false);
        packet.setContestIdentifier(contest.getContestIdentifier());
        try {

            ConnectionHandlerID connectionHandlerID = new ConnectionHandlerID("Client " + teamId.toString());
            PacketHandler packetHandler = new PacketHandler(controller, contest);
            packetHandler.handlePacket(packet, connectionHandlerID);

            failTest("Expected packet to be NOT be allowed for: " + packet);

        } catch (ContestSecurityException sec) {

            packet = null; // fake statement to satisfy CodeStyle

        } catch (Exception e) {

            failTest("Expected packet to be NOT be allowed for: " + packet, e);

        }

        // Security Level OFF
        controller.setSecurityLevel(InternalController.SECURITY_NONE_LEVEL);

        packet = PacketFactory.createRunRequest(teamId, serverId, run, teamId, false, false);
        packet.setContestIdentifier(contest.getContestIdentifier());
        try {

            ConnectionHandlerID connectionHandlerID = new ConnectionHandlerID("Client " + teamId.toString());
            contest.addLogin(teamId, connectionHandlerID);

            PacketHandler packetHandler = new PacketHandler(controller, contest);
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
