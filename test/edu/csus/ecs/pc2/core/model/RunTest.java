package edu.csus.ecs.pc2.core.model;

import junit.framework.TestCase;

/**
 * Unit test
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class RunTest extends TestCase {
    
    public void testGetSetDateBug844() throws Exception {
        
        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(1, 1, 12, 12, true);
        
        ClientId team = SampleContest.getTeamAccounts(contest)[0].getClientId();
        Problem problem = contest.getProblems()[0];
        
        Run run =  sample.createRun(contest, team, problem);
        
        assertNotNull ("Expecting time", run.getDate());
        assertTrue ("Expecting time > 0 ", run.getDate().getTime() != 0);
        
        /**
         * In this test case the date time value is set to zero 
         */
        run.setDate(null);
        assertEquals("Expecting time",0, run.getDate().getTime() );
        
    }

}
