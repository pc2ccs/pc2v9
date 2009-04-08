package edu.csus.ecs.pc2.core.util;

import java.util.Calendar;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Balloon;
import edu.csus.ecs.pc2.core.model.BalloonDeliveryInfo;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.security.Permission;

/**
 * Test BalloonHandler.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class BalloonHandlerTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        // TODO Auto-generated method stub
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        // TODO Auto-generated method stub
        super.tearDown();
    }

    public void testOne() {

        IInternalContest contest;
        IInternalController controller;

        SampleContest sampleContest = new SampleContest();

        contest = sampleContest.createContest(2, 2, 20, 2, false);
        controller = new InternalController(contest);

        BalloonHandler balloonHandler = new BalloonHandler();
        balloonHandler.setContestAndController(contest, controller);

        Judgement solvedJudgement = contest.getJudgements()[0];
        ClientId judgeId = contest.getAccounts(Type.JUDGE).firstElement().getClientId();

        Run[] runs = sampleContest.createRandomRuns(contest, 5, true, true, true);

        for (Run run : runs) {
            // Set them to all solved
            JudgementRecord judgementRecord = new JudgementRecord(solvedJudgement.getElementId(), judgeId, true, false);
            run.addJudgement(judgementRecord);
            run.setStatus(RunStates.JUDGED);
        }

        int sendCount = 0; // number of teams that should be send a balloon.
        for (Run run : runs) {
            if (balloonHandler.shouldSendBalloon(run)) {
                sendCount++;
            }
        }

        assertEquals("Failed shouldSendBalloon method ", sendCount, runs.length);

        // Send a single balloon delivered

        String key = balloonHandler.getBalloonKey(runs[0].getSubmitter(), runs[0].getProblemId());
        BalloonDeliveryInfo balloonDeliveryInfo = createBalloonDeliveryInfo(runs[0]);

        balloonHandler.updateDeliveryInfo(key, balloonDeliveryInfo);

        sendCount = 0; // number of teams that should be send a balloon.
        for (Run run : runs) {
            if (balloonHandler.shouldSendBalloon(run)) {
                sendCount++;
            }
        }

        assertEquals("Failed updateDeliveryInfo and shouldSendBalloon method  ", sendCount, runs.length - 1);

        assertFalse("Failed updateDeliveryInfo and shouldRemoveBalloon ", balloonHandler.shouldRemoveBalloon(runs[0]));

        try {
            balloonHandler.buildBalloon("test", runs[0].getSubmitter(), runs[0].getProblemId(), null);
        } catch (Exception e) {
            assertTrue("Failed null run test (bug 329)", false);
        }
    }

    /**
     * Test whether BN respects the isSentToTeam flag.
     */
    public void testNotifyFlag() {

        IInternalContest contest;
        IInternalController controller;

        SampleContest sampleContest = new SampleContest();

        contest = sampleContest.createContest(2, 2, 20, 2, false);
        controller = new InternalController(contest);
        
        Account account = contest.generateNewAccounts(ClientType.Type.SCOREBOARD.toString(), 1, true).firstElement();
        contest.setClientId(account.getClientId());

        BalloonHandler balloonHandler = new BalloonHandler();
        balloonHandler.setContestAndController(contest, controller);
        

        Judgement solvedJudgement = contest.getJudgements()[0];
        ClientId judgeId = contest.getAccounts(Type.JUDGE).firstElement().getClientId();

        Run[] runs = sampleContest.createRandomRuns(contest, 5, true, true, true);

        for (Run run : runs) {
            // Set them to all solved
            JudgementRecord judgementRecord = new JudgementRecord(solvedJudgement.getElementId(), judgeId, true, false);
            if (run.getNumber() == 3) {
                // do NOT send balloon/notify for run 3
                judgementRecord.setSendToTeam(false);
            }
            run.addJudgement(judgementRecord);
            run.setStatus(RunStates.JUDGED);
        }

        int sendCount = 0; // number of teams that should be send a balloon.
        for (Run run : runs) {
            if (balloonHandler.shouldSendBalloon(run)) {
                sendCount++;
            }
        }
        
//        Arrays.sort(runs, new RunComparator());
//        for (Run run : runs) {
//            System.out.println("debug Send? " + run.getJudgementRecord().isSendToTeam()+" " +run+" "+run.getProblemId());
//        }
      
        assertEquals("Failed shouldSendBalloon method ", sendCount, runs.length);
        
        sendCount = 0; // number of teams that should be send a balloon.
        for (Run run : runs) {
            if (balloonHandler.shouldSendBalloon(run)) {
                sendCount++;
//                System.out.println("debug 1 Sending run "+run);
            }
        }
        assertEquals("Failed shouldSendBalloon method ", sendCount, runs.length);
        
        account.getPermissionList().addPermission(Permission.Type.RESPECT_NOTIFY_TEAM_SETTING);

        sendCount = 0; // number of teams that should be send a balloon.
        for (Run run : runs) {
            if (balloonHandler.shouldSendBalloon(run)) {
                sendCount++;
//                System.out.println("debug 2 Sending run "+run);
            }
        }

        assertEquals("Failed shouldSendBalloon method  ", sendCount, runs.length - 1);
        
        
        // Send a single balloon delivered

        String key = balloonHandler.getBalloonKey(runs[0].getSubmitter(), runs[0].getProblemId());
        BalloonDeliveryInfo balloonDeliveryInfo = createBalloonDeliveryInfo(runs[0]);

        balloonHandler.updateDeliveryInfo(key, balloonDeliveryInfo);
        
        assertFalse("Failed updateDeliveryInfo and shouldRemoveBalloon ", balloonHandler.shouldRemoveBalloon(runs[0]));

        try {
            balloonHandler.buildBalloon("test", runs[0].getSubmitter(), runs[0].getProblemId(), null);
        } catch (Exception e) {
            assertTrue("Failed null run test (bug 329)", false);
        }

    }

    public void testBalloonList() {

        IInternalContest contest;
        IInternalController controller;

        SampleContest sampleContest = new SampleContest();

        contest = sampleContest.createContest(1, 1, 20, 2, false);
        controller = new InternalController(contest);
        
        Account account = contest.generateNewAccounts(ClientType.Type.SCOREBOARD.toString(), 1, true).firstElement();
        contest.setClientId(account.getClientId());

        BalloonHandler balloonHandler = new BalloonHandler();
        balloonHandler.setContestAndController(contest, controller);
        

        Judgement solvedJudgement = contest.getJudgements()[0];
        ClientId judgeId = contest.getAccounts(Type.JUDGE).firstElement().getClientId();

        Run[] runs = new Run[contest.getProblems().length];
        for (int j = 0; j < runs.length; j++) {
            Run run = new Run(contest.getAccounts(ClientType.Type.TEAM).firstElement().getClientId(), contest.getLanguages()[0], contest.getProblems()[j]);
            run.setElapsedMins(9 + j);
            run.setNumber(j+1);
            runs[j] = run;
        }

        for (Run run : runs) {
            // Set them to all solved
            JudgementRecord judgementRecord = new JudgementRecord(solvedJudgement.getElementId(), judgeId, true, false);
            if (run.getNumber() == 3) {
                // do NOT send balloon/notify for run 3
                judgementRecord.setSendToTeam(false);
            }
            run.addJudgement(judgementRecord);
            run.setStatus(RunStates.JUDGED);
            contest.addRun(run);
        }
        Balloon balloon = balloonHandler.buildBalloon("Yes", runs[5].getSubmitter(), runs[5].getProblemId(), runs[5]);
        assertEquals("Balloon list failed notify test", contest.getProblems().length,balloon.getProblems().length);

        // now set the permission and check again, this time should not include 1 run
        account.getPermissionList().addPermission(Permission.Type.RESPECT_NOTIFY_TEAM_SETTING);
        balloon = balloonHandler.buildBalloon("Yes", runs[5].getSubmitter(), runs[5].getProblemId(), runs[5]);
        assertEquals("Balloon list failed respect notify test", contest.getProblems().length-1,balloon.getProblems().length);
    }

    private BalloonDeliveryInfo createBalloonDeliveryInfo(Run run) {
        BalloonDeliveryInfo balloonDeliveryInfo = new BalloonDeliveryInfo(run.getSubmitter(), run.getProblemId(), Calendar.getInstance().getTime().getTime());
        return balloonDeliveryInfo;
    }
}
