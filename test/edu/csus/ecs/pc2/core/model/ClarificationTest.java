package edu.csus.ecs.pc2.core.model;

import junit.framework.TestCase;

/**
 * Unit tests for Clarifications. 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ClarificationTest extends TestCase {
    
    public void testSetDate() throws Exception {
        
        SampleContest sample = new SampleContest();
        
        IInternalContest contest = sample.createContest(1, 1, 12, 12, true);
        
        Account[] teamAccounts = SampleContest.getTeamAccounts(contest);
        ClientId team = teamAccounts[0].getClientId();
        
        Account[] judgeAccounts = sample.getJudgeAccounts(contest);
        ClientId judge = judgeAccounts[0].getClientId();
        
        Clarification clar = new Clarification(team, contest.getProblems()[0], "Why?");
        
        clar.setAnswer("Because.", judge, contest.getContestTime(), false);
        
        ClarificationAnswer answer = new ClarificationAnswer("Because 2", judge, false, contest.getContestTime());
        
        clar.addAnswer(answer);
        
        assertEquals("Failed number of answers", 2, clar.getClarificationAnswers().length);
        
        
        
        
    }
    
    
  public void testGetSetDateBug844() throws Exception {
        
        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(1, 1, 12, 12, true);
        
        ClientId team = SampleContest.getTeamAccounts(contest)[0].getClientId();
        Problem problem = contest.getProblems()[0];
        
        Clarification clarification = new Clarification(team, problem, "Foo");
        
        assertNotNull ("Expecting time", clarification.getDate());
        assertTrue ("Expecting time > 0 ", clarification.getDate().getTime() != 0);
        
        /**
         * In this test case the date time value is set to zero 
         */
        clarification.setDate(null);
        assertEquals("Expecting time",0, clarification.getDate().getTime() );
    }


}
