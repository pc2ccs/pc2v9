package edu.csus.ecs.pc2.core;

import java.io.File;
import java.io.IOException;

import edu.csus.ecs.pc2.core.exception.ContestSecurityException;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.packet.PacketFactory;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.security.FileStorage;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * JUnit test for PacketHandler.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PacketHandlerTest extends AbstractTestCase {
    
    private SampleContest sampleContest = new SampleContest();
    
    private String outputTestDirectory;
    
    @Override
    protected void setUp() throws Exception {
        setCreateMissingDirectories(true);
        super.setUp();
        
        if (outputTestDirectory == null) {
            // done just in case setup called for every JUnit method.
            outputTestDirectory = getTestingOutputDirectory("phy");
        }
        
    }

    /**
     * Test security setting in PacketHandler.
     *
     */
    public void testSecuritySet() throws Exception {
        
        IInternalContest contest = createContest("testSecuritySet");
        IInternalController controller = createController(contest);

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

    
    public void testDeleteRunWhenContestOver() throws Exception {
        
        IInternalContest contest = createContest("testSecuritySet");
        IInternalController controller = createController(contest);

        ContestTime contestTime = contest.getContestTime();
        
        contest.startContest(contest.getSiteNumber());

        ClientId teamId = contest.getAccounts(Type.TEAM).firstElement().getClientId();
        
        ConnectionHandlerID connectionHandlerID = new ConnectionHandlerID("Client " + teamId.toString());
        contest.addLogin(teamId, connectionHandlerID);
        
        Profile profile = new Profile("testDeleteRunWhenContestOver Profile");
        contest.setProfile(profile);

        /**
         * If contest is started and there is remaining time.
         */

        contest.startContest(contest.getSiteNumber());
        Run run = contest.getRuns()[1];
        packetHandleRun(run, contest, controller, teamId, connectionHandlerID);

        Run newRun = contest.getRun(run.getElementId());
        assertFalse("Run should NOT be deleted", newRun.isDeleted());

        /**
         * If contest is stopped do not accept run.
         */
        contest.stopContest(contest.getSiteNumber());
        run = contest.getRuns()[3];
        packetHandleRun(run, contest, controller, teamId, connectionHandlerID);

        newRun = contest.getRun(run.getElementId());
        assertTrue("Run should be deleted", newRun.isDeleted());

        /**
         * Test whether if override elapsed time > contest length, should delete.
         */
        // TODO test whether if CCS Test mode and override elapsed time > contest length, is run deleted?

        contest.startContest(contest.getSiteNumber());

        long overrideTime = contest.getContestTime().getContestLengthSecs() * 1000 + 5000; // 5 second beyond end of contest

        contestTime.setRemainingSecs(0);
        run = contest.getRuns()[5];
        setCcsTestMode(contest, true);
        packetHandleRun(run, contest, controller, teamId, connectionHandlerID, overrideTime);
        setCcsTestMode(contest, true);

        newRun = contest.getRun(run.getElementId());
        assertTrue("Run should be deleted", newRun.isDeleted());

        /**
         * If there is no remaining time but contest running, run should be deleted.
         */
        contestTime.setRemainingSecs(0);
        run = contest.getRuns()[7];
        packetHandleRun(run, contest, controller, teamId, connectionHandlerID);

        newRun = contest.getRun(run.getElementId());
        assertTrue("Run should be deleted", newRun.isDeleted());
        
    }
    
    /**
     * Set CCS test mode - ON.
     * @param contest
     * @param b
     */
    private void setCcsTestMode(IInternalContest contest, boolean b) {
        contest.getContestInformation().setCcsTestMode(b);
    }

    private void packetHandleRun(Run run, IInternalContest contest, IInternalController controller, ClientId teamId, ConnectionHandlerID connectionHandlerID) throws Exception {
        packetHandleRun(run, contest, controller, teamId, connectionHandlerID, 0);
    }
    
    private void packetHandleRun(Run run, IInternalContest contest, IInternalController controller, ClientId teamId, 
            ConnectionHandlerID connectionHandlerID, long overrideElapsedTime) throws Exception {
        RunFiles runFiles = sampleContest.createSampleRunFiles(run);

        ClientId serverId = new ClientId(contest.getSiteNumber(), Type.SERVER, 0);
        Packet packet = PacketFactory.createSubmittedRun(teamId, serverId, run, runFiles, overrideElapsedTime, 0);
        packet.setContestIdentifier(contest.getContestIdentifier());

        PacketHandler packetHandler = new PacketHandler(controller, contest);
        packetHandler.handlePacket(packet, connectionHandlerID);

    }

    protected IInternalContest createContest (String methodName) throws IOException, ClassNotFoundException, FileSecurityException {
      
        IInternalContest contest = sampleContest.createContest(2, 4, 12, 6, true);
        
        FileStorage storage = new FileStorage(outputTestDirectory + File.separator + methodName);
        contest.setStorage(storage);
        
        String testSourceFileName = getTestFilename("Sumit.java");
        assertFileExists(testSourceFileName);

        // Add 22 random runs
        Run[] runs = sampleContest.createRandomRuns(contest, 22, true, true, true);
        sampleContest.addRuns(contest, runs, testSourceFileName);
        
        return contest;
    }
    
    protected IInternalController createController(IInternalContest contest) {
        return sampleContest.createController(contest, outputTestDirectory, true, false);
    }


}
