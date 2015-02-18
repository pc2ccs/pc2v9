package edu.csus.ecs.pc2.api.implementation;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.api.ILanguage;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.IRun;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunUtilities;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Run.RunStates;

/**
 * Unit Test.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class RunImplementationTest extends TestCase {

    /**
     * Tests {@link RunUtilities#createNewRun(Run, IInternalContest)}.
     */
    public void testCreateNewRun() {

        SampleContest sampleContest = new SampleContest();

        IInternalContest contest = sampleContest.createContest(3, 3, 33, 12, true);

        ClientId firstJudgeId = contest.getAccounts(Type.JUDGE).firstElement().getClientId();

        Run[] runs = sampleContest.createRandomRuns(contest, 12, true, true, false);

        IInternalController controller = sampleContest.createController(contest, true, false);

        Judgement yesJudgement = sampleContest.getYesJudgement(contest);

        Run run = runs[0];
        run.setElapsedMins(40);

        // prelim judgement
        JudgementRecord record = new JudgementRecord(yesJudgement.getElementId(), firstJudgeId, true, true);
        record.setPreliminaryJudgement(true);
        run.addJudgement(record);
        run.setStatus(RunStates.JUDGED);

        // final judgement
        record = new JudgementRecord(yesJudgement.getElementId(), firstJudgeId, true, false);
        run.addJudgement(record);

        Run run2 = RunUtilities.createNewRun(run, contest);

        IRun irun = new RunImplementation(run2, contest, controller);

        assertEquals(irun.isDeleted(), false);
        assertEquals(irun.isFinalJudged(), false);
        assertEquals(irun.isPreliminaryJudged(), false);
        assertEquals(irun.isSolved(), false);

        assertEquals(irun.getSiteNumber(), run.getSiteNumber());
        assertEquals(irun.getJudgementName(), "");
        assertEquals(irun.getNumber(), run.getNumber());
        assertEquals(irun.getSubmissionTime(), run.getElapsedMins());
        assertEquals(irun.getRunJudgements().length, 0);
        assertEquals(irun.getNumber(), run.getNumber());

        IProblem problem = new ProblemImplementation(run.getProblemId(), contest);
        ILanguage language = new LanguageImplementation(run.getLanguageId(), contest);

        assertEquals(problem.getName(), contest.getProblem(run.getProblemId()).toString());
        assertEquals(language.getName(), contest.getLanguage(run.getLanguageId()).toString());

    }
    
    
    /**
     * Test for getJudge, for null (not judged judge) and judge name.
     * 
     * Bug 890 - add getJudge for judged runs.
     * 
     * @throws Exception
     */
    public void testGetJudge() throws Exception {

        SampleContest sampleContest = new SampleContest();

        IInternalContest contest = sampleContest.createContest(3, 3, 33, 12, true);

        ClientId firstJudgeId = contest.getAccounts(Type.JUDGE).firstElement().getClientId();

        Run[] runs = sampleContest.createRandomRuns(contest, 12, true, true, false);

        IInternalController controller = sampleContest.createController(contest, true, false);

        Judgement yesJudgement = sampleContest.getYesJudgement(contest);

        Run run = runs[0];
        run.setElapsedMins(40);

        IRun apiRun = new RunImplementation(run, contest, controller);

        assertNull("Run not judged expect null judge ", apiRun.getJudge());

        // prelim judgement
        JudgementRecord record = new JudgementRecord(yesJudgement.getElementId(), firstJudgeId, true, true);
        record.setPreliminaryJudgement(true);
        run.addJudgement(record);
        run.setStatus(RunStates.JUDGED);

        // final judgement
        record = new JudgementRecord(yesJudgement.getElementId(), firstJudgeId, true, false);
        run.addJudgement(record);

        IRun irun = new RunImplementation(run, contest, controller);

        String expected = firstJudgeId.getName();
        String actual = irun.getJudge().getDisplayName();

        assertEquals("Expecting judge name ", expected, actual);

    }
}
