package edu.csus.ecs.pc2.ui;

import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit tests.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class FinalizePaneTest extends AbstractTestCase {

    /**
     * Test for zero JE runs.
     */
    public void testgetNumberJERunsNoRuns() throws Exception {

        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(5, 5, 12, 12, true);

        int actual = FinalizePane.getNumberJERuns(contest);

        assertEquals("Expecting no JE runs found for ", 0, actual);

        Run runListOne = sample.createRandomJudgedRunSolved(contest);
        contest.addRun(runListOne);

        assertEquals("Expecting no JE runs found for ", 0, actual);
    }

    /**
     * Find the judgement JE.
     * 
     * @param contest
     * @return null or the JE judgement if found.
     */
    public Judgement findJudgingErrorJudgement(IInternalContest contest) {
        Judgement judgementJE = null;
        Judgement[] judgeList = contest.getJudgements();
        for (Judgement judgement : judgeList) {
            if (judgement.getAcronym() != null) {
                if (judgement.getAcronym().equalsIgnoreCase("JE")) {
                    judgementJE = judgement;
                }
            }
        }
        return judgementJE;
    }

    /**
     * Test for many JE runs.
     */
    public void testgetNumberJERunsManyRuns() throws Exception {

        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(5, 5, 12, 12, true);

        String[] judgementNames = { "Yes", "No - incorrect output", "No - compilation error", "Judging Error", "Contact staff", "No - Security Violation" };

        String[] acronyms = { "AC", "WA", "CE", "JE", "CS", "SV" };

        for (int i = 0; i < judgementNames.length; i++) {
            Judgement judgement = new Judgement(judgementNames[i], acronyms[i]);
            contest.addJudgement(judgement);
        }

        Judgement jeJudgement = findJudgingErrorJudgement(contest);
        assertNotNull("Expecting JE in list of judgements", jeJudgement);

        int actual = FinalizePane.getNumberJERuns(contest);

        assertEquals("Expecting no JE runs found for ", 0, actual);

        String[] runsData = { "1,1,A,1,No,No,4", // 0 (a No before first yes Security Violation)
                "2,1,A,1,No,No,2", // 0 (a No before first yes Compilation Error)
                "3,1,A,1,No,No,1", // 20 (a No before first yes)
                "4,1,A,3,Yes,No,0", // 3 (first yes counts Minute points but never Run Penalty points)
                "5,1,A,5,No,No,1", // zero -- after Yes
                "6,1,A,7,Yes,No,0", // zero -- after Yes
                "7,1,A,9,No,No,1", // zero -- after Yes
                "8,1,B,11,No,No,1", // zero -- not solved
                "9,2,A,48,No,No,4", // 0 (a No before first yes Security Violation)
                "10,2,A,50,Yes,No,0", // 50 (minute points; no Run points on first Yes)
                "11,2,B,35,No,No,1", // zero -- not solved
                "12,2,B,40,No,No,1", // zero -- not solved
        };

        for (String runInfoLine : runsData) {

            sample.addARun(contest, runInfoLine);
        }

        assertEquals("Expecting runs", runsData.length, contest.getRuns().length);

        assertEquals("Expecting no JE runs  ", 0, actual);

        Account judge = getFirstJudge(contest);

        assertNotNull("Expecting judge", judge);

        int[] runIds = { 2, 4, 6, 8 };

        // Add JE judgement to runs.
        addJudgement(runIds, contest, jeJudgement, judge);

        actual = FinalizePane.getNumberJERuns(contest);

        assertEquals("Expecting JE runs  ", runIds.length, actual);

        int[] runIds2 = { 1, 9, 10 };

        addJudgement(runIds2, contest, jeJudgement, judge);

        actual = FinalizePane.getNumberJERuns(contest);

        assertEquals("Expecting JE runs ", runIds.length + runIds2.length, actual);

        /**
         * Delete some JE records, test whether number of runs decreases.
         */

        for (int runid : runIds2) {
            Run run = contest.getRuns()[runid];
            run.setDeleted(true);
        }

        actual = FinalizePane.getNumberJERuns(contest);

        assertEquals("Expecting no JE runs found for ", runIds.length, actual);

    }

    /**
     * For input runIds add judgement.
     */
    private void addJudgement(int[] runIds, IInternalContest contest, Judgement judgement, Account judge) {

        for (int id : runIds) {
            Run run = contest.getRuns()[id];
            JudgementRecord judgementRecord = new JudgementRecord(judgement.getElementId(), judge.getClientId(), true, false);
            run.addJudgement(judgementRecord);
        }
    }

}
