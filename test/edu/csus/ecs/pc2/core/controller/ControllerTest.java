package edu.csus.ecs.pc2.core.controller;

import java.io.File;
import java.util.Arrays;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.PacketHandler;
import edu.csus.ecs.pc2.core.exception.ContestSecurityException;
import edu.csus.ecs.pc2.core.exception.RunUnavailableException;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.packet.PacketFactory;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.PermissionList;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * Test for InternalController.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ControllerTest extends TestCase {

    private IInternalContest contest;

    private IInternalController controller;

    private PacketHandler packetHandler;

    protected void setUp() throws Exception {
        super.setUp();
        SampleContest sampleContest = new SampleContest();
        contest = sampleContest.createContest(2, 4, 12, 6, true);
        controller = sampleContest.createController(contest, true, false);

        String loadFile = "pc2v9.ini";
        File dir = new File(loadFile);
        if (!dir.exists()) {
            // TODO, try to find this path in the environment
            dir = new File("projects" + File.separator +"pc2v9" + File.separator + loadFile);
            if (!dir.exists()) {
                System.err.println("could not find " + loadFile);
            } else {
                loadFile = dir.getAbsolutePath();
            }
        }

        // Add 22 random runs
        Run [] runs = sampleContest.createRandomRuns(contest, 22, true, true, true);
        sampleContest.addRuns(contest, runs, loadFile);
        
        packetHandler = new PacketHandler(controller, contest);

    }

    protected void tearDown() throws Exception {
        super.tearDown();
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

    public void testSecuritySet() {

        int level = controller.getSecurityLevel();
        controller.setSecurityLevel(level);
        assertEquals("Expected " + level + " got " + controller.getSecurityLevel(), controller.getSecurityLevel(), level);

        level = InternalController.SECURITY_HIGH_LEVEL;
        controller.setSecurityLevel(level);
        assertEquals("Expected " + level + " got " + controller.getSecurityLevel(), controller.getSecurityLevel(), level);

        level = InternalController.SECURITY_NONE_LEVEL;
        controller.setSecurityLevel(level);
        assertEquals("Expected " + level + " got " + controller.getSecurityLevel(), controller.getSecurityLevel(), level);
    }
    
    /**
     * Return all runs in sorted order
     * 
     * @see RunComparator
     * @param internalContest
     * @return
     */
    protected Run [] getSortedRuns(IInternalContest internalContest){
        Run [] runs = contest.getRuns();
        Arrays.sort (runs, new RunComparator());
        return runs;
    }
    
    public void testRequestRunPermission() {
        
        int level = InternalController.SECURITY_HIGH_LEVEL;
        controller.setSecurityLevel(level);
        assertEquals("Expected level " + level , controller.getSecurityLevel(), level);
        
        ClientId teamThree = new ClientId(contest.getSiteNumber(), Type.TEAM, 3);
        ClientId teamId = contest.getAccount(teamThree).getClientId();
        
        ClientId serverId = new ClientId(contest.getSiteNumber(), Type.SERVER, 0);
        ConnectionHandlerID connectionHandlerID = new ConnectionHandlerID(teamThree.getName());
        
        Run run = getSortedRuns(contest)[0];
        
        // Test Run Request
        Packet packet = PacketFactory.createRunRequest(teamId, serverId, run, teamId, false);
        
        testRequest (packet, connectionHandlerID);
        
        Account judgeAccount = contest.getAccount(new ClientId(contest.getSiteNumber(), Type.JUDGE, 2));
        ClientId judgeId = judgeAccount.getClientId();
        
        try {
            contest.checkoutRun(run, judgeId, false);
        } catch (RunUnavailableException e) {
            failTest("Judge could not checkout run "+run, e);
        }
        
        Judgement judgement = contest.getJudgements()[2];
        
        JudgementRecord judgementRecord = new JudgementRecord(judgement.getElementId(),judgeId, false, false);
        
        // Test Run Judgement
        packet = PacketFactory.createRunJudgement(judgeId, serverId, run, judgementRecord, null);
        
        removePermission (contest, judgeAccount, Permission.Type.JUDGE_RUN);
        
        contest.updateAccount(judgeAccount);
        
        testRequest(packet, connectionHandlerID);
        
    }
    
    private void removePermission(IInternalContest inContest, Account account, edu.csus.ecs.pc2.core.security.Permission.Type type) {
        
        PermissionList permissionList = account.getPermissionList();
        permissionList.removePermission(type);
        contest.updateAccount(account);

        Account checkAccount = inContest.getAccount(account.getClientId());
        
        if (checkAccount.isAllowed(type)){
            failTest("Was not able to remove permission "+type.toString()+" from "+account.getClientId());
        }
        
    }

    private void testRequest(Packet packet, ConnectionHandlerID connectionHandlerID) {
        
        try {
            packetHandler.handlePacket(packet, connectionHandlerID);
            failTest("Should have thrown exception for "+packet);
        } catch (ContestSecurityException e) {
            passText("Exception should be thrown "+e.getMessage());
        }

    }

    /**
     * Stub for cases where some function required, like in catch 
     * block where the test passed if the exception is thrown.
     * @param string
     */
    private void passText(String string) {
//        System.out.println("passed test: "+string);
    }
    
}
