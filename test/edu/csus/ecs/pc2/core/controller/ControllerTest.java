package edu.csus.ecs.pc2.core.controller;

import java.io.IOException;
import java.util.Arrays;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.exception.RunUnavailableException;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.packet.PacketFactory;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.PermissionList;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Test for InternalController.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ControllerTest extends AbstractTestCase {

    private IInternalContest contest;

    private IInternalController controller;

//    private PacketHandler packetHandler;

    protected void setUp() throws Exception {
        super.setUp();
        SampleContest sampleContest = new SampleContest();
        contest = sampleContest.createContest(2, 4, 12, 6, true);
        controller = sampleContest.createController(contest, true, false);

//        // Directory where test data is
//        String testDir = "testdata";
//        String projectPath=JUnitUtilities.locate(testDir);
//        if (projectPath == null) {
//            throw new Exception("Unable to locate "+testDir);
//        }
//
//        String loadFile = projectPath + File.separator+ testDir + File.separator + "Sumit.java";
//        File dir = new File(loadFile);
//        if (!dir.exists()) {
//            System.err.println("could not find " + loadFile);
//            throw new Exception("Unable to locate "+loadFile);
//        }

        String loadFile = getSamplesSourceFilename("Sumit.java");
        
        // Add 22 random runs
        Run[] runs = sampleContest.createRandomRuns(contest, 22, true, true, true);
        sampleContest.addRuns(contest, runs, loadFile);

//        packetHandler = new PacketHandler(controller, contest);

    }

    protected void tearDown() throws Exception {
        super.tearDown();
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
    protected Run[] getSortedRuns(IInternalContest internalContest) {
        Run[] runs = contest.getRuns();
        Arrays.sort(runs, new RunComparator());
        return runs;
    }

    public void testRequestRunPermission() throws IOException, ClassNotFoundException, FileSecurityException {

        int level = InternalController.SECURITY_HIGH_LEVEL;
        controller.setSecurityLevel(level);
        assertEquals("Expected level " + level, controller.getSecurityLevel(), level);

        ClientId teamThree = new ClientId(contest.getSiteNumber(), Type.TEAM, 3);
        ClientId teamId = contest.getAccount(teamThree).getClientId();

        ClientId serverId = new ClientId(contest.getSiteNumber(), Type.SERVER, 0);
        ConnectionHandlerID connectionHandlerID = new ConnectionHandlerID(teamThree.getName());

        Run run = getSortedRuns(contest)[0];

        // Test Run Request
        Packet packet = PacketFactory.createRunRequest(teamId, serverId, run, teamId, false, false);

        testRequest(packet, connectionHandlerID);

        Account judgeAccount = contest.getAccount(new ClientId(contest.getSiteNumber(), Type.JUDGE, 2));
        ClientId judgeId = judgeAccount.getClientId();

        try {
            contest.checkoutRun(run, judgeId, false, false);
        } catch (RunUnavailableException e) {
            failTest("Judge could not checkout run " + run, e);
        }

        Judgement judgement = contest.getJudgements()[2];

        JudgementRecord judgementRecord = new JudgementRecord(judgement.getElementId(), judgeId, false, false);

        // Test Run Judgement
        packet = PacketFactory.createRunJudgement(judgeId, serverId, run, judgementRecord, null);

        removePermission(contest, judgeAccount, Permission.Type.JUDGE_RUN);

        contest.updateAccount(judgeAccount);

        testRequest(packet, connectionHandlerID);

    }

    private void removePermission(IInternalContest inContest, Account account, edu.csus.ecs.pc2.core.security.Permission.Type type) {

        PermissionList permissionList = account.getPermissionList();
        permissionList.removePermission(type);
        contest.updateAccount(account);

        Account checkAccount = inContest.getAccount(account.getClientId());

        if (checkAccount.isAllowed(type)) {
            failTest("Was not able to remove permission " + type.toString() + " from " + account.getClientId());
        }

    }

    private void testRequest(Packet packet, ConnectionHandlerID connectionHandlerID) {

//        try {
//            packetHandler.handlePacket(packet, connectionHandlerID);
//            failTest("Should have thrown exception for " + packet);
//        } catch (ContestSecurityException e) {
//            passText("Exception should be thrown " + e.getMessage());
//        }

    }

    public void testLoginExpansion() {

        String[] positiveTests = { "r:administrator1", "judge1:judge1", "j3:judge3", "1:team1", "33:team33", "b1:scoreboard1", "r:administrator1", "spectator1:spectator1", "executor1:executor1",
                "t2:team2", "s4:server0", "s:server0" };

        int siteNumber = contest.getSiteNumber();

        for (String rowInfo : positiveTests) {

            String loginName = rowInfo.split(":")[0];
            String expectedLogin = rowInfo.split(":")[1];

            ClientId clientId = InternalController.loginShortcutExpansion(siteNumber, loginName);
            assertEquals("Login name expansion didn't match", expectedLogin, clientId.getName());
        }
    }

    private void compareStrings(String title, String expected, String result) {

        assertFalse(title + "expected <" + expected + "> got <" + result + ">", expected.equals(result));
    }

    public void testLoginExpansionNegative() {

        System.err.flush();
        
        int siteNumber = contest.getSiteNumber();

        String[] negativeTests = { "r:administrtor4", "judge1:jud", "j3:judge2", "1:tea1", "33:team3", "b1:scoreboard3", 
                "r:judge2", "spectator1:spector1", "executor1:executor12", "t2:team1",
                "s4:serve", "s:server3" };

        for (String rowInfo : negativeTests) {

            String loginName = rowInfo.split(":")[0];
            String expectedLogin = rowInfo.split(":")[1];

            ClientId clientId = InternalController.loginShortcutExpansion(siteNumber, loginName);
            compareStrings("Login name expansion shouldn't match " + rowInfo, expectedLogin, clientId.getName());

        }
    }
}
