package edu.csus.ecs.pc2.core.model;

import edu.csus.ecs.pc2.core.model.ClientType.Type;
import junit.framework.TestCase;

/**
 * Unit Test.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class NotificationTest extends TestCase {
    
    private void leTestGetSetDate (IGetDate dateVar){
        
        assertNotNull ("Expecting time", dateVar.getDate());
        assertTrue ("Expecting time > 0 ", dateVar.getDate().getTime() != 0);
        
        /**
         * In this test case the date time value is set to zero 
         */
        dateVar.setDate(null);
        assertEquals("Expecting time",0, dateVar.getDate().getTime() );
    }
    

    public void testGetSetDateBug844() throws Exception {
        
        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(1, 1, 12, 12, true);
        
        ClientId team = SampleContest.getTeamAccounts(contest)[0].getClientId();
        Problem problem = contest.getProblems()[0];

        Run run =  sample.createRun(contest, team, problem);
        
        ClientId whoSent = contest.getAccounts(Type.SCOREBOARD).firstElement().getClientId();
        long elapsedMS = 2323;
        long timeSent = 2445;
        IGetDate dateVar = new Notification(run, whoSent, timeSent, elapsedMS);
        
        leTestGetSetDate(dateVar);
    }


}
