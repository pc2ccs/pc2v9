package edu.csus.ecs.pc2.core.model;

import edu.csus.ecs.pc2.core.model.ClientType.Type;
import junit.framework.TestCase;

/**
 * Unit test.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class RunTestCaseTest extends TestCase {

    private void leTestGetSetDate(IGetDate dateVar) {

        assertNotNull("Expecting time", dateVar.getDate());
        assertTrue("Expecting time > 0 ", dateVar.getDate().getTime() != 0);

        /**
         * In this test case the date time value is set to zero
         */
        dateVar.setDate(null);
        assertEquals("Expecting time", 0, dateVar.getDate().getTime());
    }

    public void testGetSetDateBug844() throws Exception {

        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(1, 1, 12, 12, true);

        ClientId team = sample.getTeamAccounts(contest)[0].getClientId();
        Problem problem = contest.getProblems()[0];

        Run run =  sample.createRun(contest, team, problem);

        ClientId judgeId = contest.getAccounts(Type.JUDGE).firstElement().getClientId();

        ElementId  judgementId = contest.getJudgements()[3].getElementId();

        JudgementRecord record = new JudgementRecord(judgementId, judgeId, true, false);

        int testNumber = 5;
        boolean solved = true;
        IGetDate dateVar = new RunTestCase(run, record, testNumber, solved);

        leTestGetSetDate(dateVar);
    }
    
    public void testMatchesJudgement() throws Exception {
        
        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(1, 1, 12, 12, true);

        ClientId team = sample.getTeamAccounts(contest)[0].getClientId();
        Problem problem = contest.getProblems()[0];

        Run run = sample.createRun(contest, team, problem);

        ClientId judgeId = contest.getAccounts(Type.JUDGE).firstElement().getClientId();

        ElementId judgementId = contest.getJudgements()[3].getElementId();

        JudgementRecord record = new JudgementRecord(judgementId, judgeId, true, false);

        int testNumber = 5;
        boolean solved = true;
        RunTestCase runTestCase = new RunTestCase(run, record, testNumber, solved);

        assertTrue("Expcting a match ", runTestCase.matchesJudgement(record));
    }
}
