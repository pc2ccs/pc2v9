package edu.csus.ecs.pc2.core.model;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.list.JudgementNotificationsList;
import edu.csus.ecs.pc2.core.model.ClientType.Type;

/**
 * Testing for RunUtilies.
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

        boolean suppressed = RunUtilities.supppressJudgement(judgementNotificationsList, run, contestTime);

        assertFalse("Should be suppressed, no notifications defined", suppressed);

        // Create Yes judgement
        JudgementRecord record = new JudgementRecord(yesJudgement.getElementId(), firstJudgeId, true, false);
        record.setPreliminaryJudgement(true);
        run.addJudgement(record);
        run.setElapsedMins(minutesBeforeEnd(contest, 30));

        suppressed = RunUtilities.supppressJudgement(judgementNotificationsList, run, contestTime);
        assertFalse("Should be suppressed, no notifications defined", suppressed);

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
