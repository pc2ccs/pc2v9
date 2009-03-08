package edu.csus.ecs.pc2.core.model;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.list.JudgementNotificationsList;
import edu.csus.ecs.pc2.core.model.ClientType.Type;

/**
 * Testing for RunUtilities.
 * 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class RunUtilitiesTest extends TestCase {

    /**
     * Test Suppress Judgement.
     */
    public void testSuppressJudgement() {

        SampleContest sampleContest = new SampleContest();

        IInternalContest contest = sampleContest.createContest(1, 3, 33, 12, true);

        JudgementNotificationsList judgementNotificationsList = null;

        ContestTime contestTime = contest.getContestTime();

        ClientId firstJudgeId = contest.getAccounts(Type.JUDGE).firstElement().getClientId();

        Run[] runs = sampleContest.createRandomRuns(contest, 12, true, true, false);

        Judgement yesJudgement = sampleContest.getYesJudgement(contest);

        Run run = runs[0];

        boolean suppressed = RunUtilities.supppressJudgement(null, run, contestTime);

        assertFalse("Should be suppressed, no notifications defined", suppressed);

        /**
         * Add Preliminary Judgement Yes.
         */
        
        JudgementRecord record = new JudgementRecord(yesJudgement.getElementId(), firstJudgeId, true, false);
        record.setPreliminaryJudgement(true);
        run.addJudgement(record);

        suppressed = RunUtilities.supppressJudgement(null, run, contestTime);
        assertFalse("Should be suppressed, no notifications defined", suppressed);
        
        judgementNotificationsList = new JudgementNotificationsList();
        NotificationSetting notificationSetting = new NotificationSetting(run.getProblemId());
        judgementNotificationsList.add(notificationSetting);
        
        NotificationSetting notificationSetting2 = (NotificationSetting) judgementNotificationsList.get(run.getProblemId());
        assertTrue("Notification not properly inserted into list", notificationSetting.isSameAs(notificationSetting2));
        
        suppressed = RunUtilities.supppressJudgement(judgementNotificationsList, run, contestTime);
        assertFalse("Should be suppressed, notifications defined but turned ON", suppressed);
        
        JudgementNotification judgementNotification = new JudgementNotification(true, 30);
        notificationSetting.setPreliminaryNotificationYes(judgementNotification);
        
        JudgementNotification judgementNotificationNo = new JudgementNotification(false, 30);
        notificationSetting.setPreliminaryNotificationNo(judgementNotificationNo);
        
        run.setElapsedMins(minutesBeforeEnd(contest, 31));
        suppressed = RunUtilities.supppressJudgement(judgementNotificationsList, run, contestTime);
        assertFalse("Should be NOT suppressed, run.elapsed = 31", suppressed);
        
        run.setElapsedMins(minutesBeforeEnd(contest, 30));
        suppressed = RunUtilities.supppressJudgement(judgementNotificationsList, run, contestTime);
        assertTrue("Should be suppressed, run.elapsed = 30", suppressed);
        
        run.setElapsedMins(minutesBeforeEnd(contest, 29));
        suppressed = RunUtilities.supppressJudgement(judgementNotificationsList, run, contestTime);
        assertTrue("Should be suppressed, run.elapsed = 29", suppressed);
        
        
        /**
         * Add Final Judgement Yes.
         */
        
        record = new JudgementRecord(yesJudgement.getElementId(), firstJudgeId, true, false);
        // final judgement
//        record.setPreliminaryJudgement(true);
        run.addJudgement(record);
        
        run.setElapsedMins(minutesBeforeEnd(contest, 31));
        suppressed = RunUtilities.supppressJudgement(judgementNotificationsList, run, contestTime);
        assertFalse("Should be NOT suppressed, run.elapsed = 31", suppressed);
        
        run.setElapsedMins(minutesBeforeEnd(contest, 30));
        suppressed = RunUtilities.supppressJudgement(judgementNotificationsList, run, contestTime);
        assertFalse("Should be NOT suppressed, run.elapsed = 30", suppressed);
        
        run.setElapsedMins(minutesBeforeEnd(contest, 29));
        suppressed = RunUtilities.supppressJudgement(judgementNotificationsList, run, contestTime);
        assertFalse("Should be NOT suppressed, run.elapsed = 29", suppressed);

        // Add Final suppress for yes
        
        judgementNotification = new JudgementNotification(true, 30);
        notificationSetting.setFinalNotificationYes(judgementNotification);
        
        run.setElapsedMins(minutesBeforeEnd(contest, 31));
        suppressed = RunUtilities.supppressJudgement(judgementNotificationsList, run, contestTime);
        assertFalse("Should be NOT suppressed, run.elapsed = 31", suppressed);
        
        run.setElapsedMins(minutesBeforeEnd(contest, 30));
        suppressed = RunUtilities.supppressJudgement(judgementNotificationsList, run, contestTime);
        assertTrue("Should be suppressed, run.elapsed = 30", suppressed);
        
        run.setElapsedMins(minutesBeforeEnd(contest, 29));
        suppressed = RunUtilities.supppressJudgement(judgementNotificationsList, run, contestTime);
        assertTrue("Should be suppressed, run.elapsed = 29", suppressed);    
        
        /**
         * Loop through all runs, set Final Judgmenet Yes for each run,
         * except the first one which is ignored.
         */
        for (Run run2 : runs){
            if (! run2.equals(run)){ 
                suppressed = RunUtilities.supppressJudgement(judgementNotificationsList, run2, contestTime);
                assertFalse("Should be NOT suppressed, not judged "+run2, suppressed);   
                
                // Add final judgement
                record = new JudgementRecord(yesJudgement.getElementId(), firstJudgeId, true, false);
                run2.addJudgement(record);
                
                suppressed = RunUtilities.supppressJudgement(judgementNotificationsList, run2, contestTime);
                assertFalse("Should be NOT suppressed, judged "+run2, suppressed);

                run2.setElapsedMins(minutesBeforeEnd(contest, 20));
                suppressed = RunUtilities.supppressJudgement(judgementNotificationsList, run2, contestTime);
                
                if (run2.getProblemId().equals(run.getProblemId())){
                    /**
                     * Same Problem as run, so the settings are to suppress the run.
                     */
                    assertTrue("Should be suppressed, judged at 20 min "+run2, suppressed);
                } else {
                    assertFalse("Should be NOT suppressed, judged at 20 min "+run2, suppressed);
                }

            }
        }

        // TODO test all combinations of final/prelim/no/yes and cuttoff time
    }

    /**
     * Return the elapsed time for mins before end of contest.
     * @param contest
     * @param mins
     * @return
     */
    private long minutesBeforeEnd(IInternalContest contest, int mins) {
        return contest.getContestTime().getConestLengthMins() - mins;
    }

}
