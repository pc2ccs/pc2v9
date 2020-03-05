// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core;

import java.io.IOException;
import java.util.List;

import edu.csus.ecs.pc2.core.exception.ContestSecurityException;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
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
    
//    private String outputTestDirectory;
    
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

    //The following method generates a security exception:
    //  edu.csus.ecs.pc2.core.exception.ContestSecurityException: Client TEAM3 @ site 2 attempted to submit run for team TEAM1 @ site 2
    //It has been temporarily renamed (so that it won't appear as a JUnit test method) until this can be investigated.  JLC
    //TODO: investigate how to fix this -- perhaps by using the new "proxy-team" property?
//    public void testDeleteRunWhenContestOver() throws Exception {
    public void TODOtestDeleteRunWhenContestOver() throws Exception {
        
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
        ClientId id = run.getSubmitter();
        packetHandleRun(run, contest, controller, id, connectionHandlerID);

        Run newRun = contest.getRun(run.getElementId());
        assertFalse("Run should NOT be deleted", newRun.isDeleted());

        /**
         * If contest is stopped do not accept run.
         */
        contest.stopContest(contest.getSiteNumber());
        run = contest.getRuns()[3];
        teamId = run.getSubmitter();
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
        teamId = run.getSubmitter();
        packetHandleRun(run, contest, controller, teamId, connectionHandlerID, overrideTime);
        setCcsTestMode(contest, true);

        newRun = contest.getRun(run.getElementId());
        assertTrue("Run should be deleted", newRun.isDeleted());

        /**
         * If there is no remaining time but contest running, run should be deleted.
         */
        contestTime.setRemainingSecs(0);
        run = contest.getRuns()[7];
        teamId = run.getSubmitter();
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
        
        FileStorage storage = new FileStorage(getOutputTestFilename(methodName));
        contest.setStorage(storage);
        
        String testSourceFileName = getSamplesSourceFilename("Sumit.java");
        assertFileExists(testSourceFileName);

        // Add 22 random runs
        Run[] runs = sampleContest.createRandomRuns(contest, 22, true, true, true);
        sampleContest.addRuns(contest, runs, testSourceFileName);
        
        return contest;
    }
    
    protected IInternalController createController(IInternalContest contest) {
        String outputTestDirectory = getOutputDataDirectory();
        return sampleContest.createController(contest, outputTestDirectory, true, false);
    }
  
  
    
    
    public void testgetProblemsForTeam() throws Exception {

        IInternalContest contest = createContest("testFilterProblemsByTeam");
        IInternalController controller = createController(contest);
        
        sampleContest.assignSampleGroups(contest,"One", "Two");
        
        Group[] groups = contest.getGroups();
        assertNotNull(groups);
        assertTrue("More than 1 group "+groups.length, groups.length > 1);
        
        Group group3 = new Group("Three");
        contest.addGroup(group3);
        
        
        Problem[] problems = contest.getProblems();
        assertTrue(problems.length > 4);

        PacketHandler packetHandler = new PacketHandler(controller, contest);
        
        Account[] teams = getTeamAccounts(contest);
        
        Account team = teams[0];
        
        Problem[] prob = packetHandler.getProblemsForTeam(contest, team.getClientId());
        
        assertEquals(prob.length, problems.length);
        
        team.setGroupId(group3.getElementId());
        contest.updateAccount(team);
        
        Problem np1 = assignOnlyGroupToProblem(contest, problems[1], groups[1]);

        assertFalse(np1.isAllView());
        List<Group> np1Groups = np1.getGroups();
        assertTrue(np1Groups.size() == 1);
        assertEquals(groups[1], np1Groups.get(0));
        
        Account admin = getAdministratorAccounts(contest)[0];
        Problem[] npl = packetHandler.getProblemsForTeam(contest, admin.getClientId());
        assertEquals(problems.length, npl.length);
        
        npl = packetHandler.getProblemsForTeam(contest, team.getClientId());
        assertEquals(problems.length-1, npl.length);
        
        Account team2 = teams[1];
        team2.setGroupId(groups[1].getElementId());
        contest.updateAccount(team2);
        
        npl = packetHandler.getProblemsForTeam(contest, team2.getClientId());
        assertEquals(problems.length, npl.length);
    }


    /**
     * Assign only this single group for this problem.
     * 
     * @param contest
     * @param problem
     * @param group
     * @return 
     */
    private Problem assignOnlyGroupToProblem(IInternalContest contest, Problem problem, Group group) {
        problem.clearGroups();
        problem.addGroup(group);
        contest.updateProblem(problem);
        return contest.getProblem(problem.getElementId());
    }


}
