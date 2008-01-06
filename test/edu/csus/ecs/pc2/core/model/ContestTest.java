package edu.csus.ecs.pc2.core.model;

import edu.csus.ecs.pc2.core.exception.RunUnavailableException;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import junit.framework.TestCase;

/**
 * Test Contest class. 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ContestTest extends TestCase {

    public ContestTest(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    /**
     * Create a new run in the contest.
     * 
     * @param contest
     * @return created run.
     */
    private Run createRun (IContest contest, ClientId clientId) {
        Problem problem = contest.getProblems()[0];
        Language language = contest.getLanguages()[0];
        Run run = new Run(clientId, language, problem);
        return run;
    }
    
    public void testCheckOut (){
        
        SampleContest sampleContest = new SampleContest();
        
        IContest contest = sampleContest.createContest(1, 1, 12, 4);
        
        ContestTime contestTime = contest.getContestTime();
        contestTime.setElapsedMins(52);
        contestTime.startContestClock();
        contest.updateContestTime(contestTime);
        
        ClientId serverId = new ClientId(1, Type.SERVER, 0);
        contest.setClientId(serverId);
        
        Account [] judges = (Account[]) contest.getAccounts(ClientType.Type.JUDGE).toArray(new Account[contest.getAccounts(ClientType.Type.JUDGE).size()]);
        
        Account account = contest.getAccounts(ClientType.Type.TEAM).firstElement();
        ClientId id = account.getClientId();

        Run submittedRun = createRun (contest, id);
        RunFiles runFiles = new RunFiles(submittedRun, "samps/Sumit.java");
        
        contest.acceptRun(submittedRun, runFiles);
        
        ClientId judgeId = judges[0].getClientId();
        
        // Judge gets run
        
        try {
            contest.checkoutRun(submittedRun, judgeId, false);
            System.out.println(judgeId+" checked out"+submittedRun);
        } catch (RunUnavailableException e) {
            e.printStackTrace();
            assertTrue ("Failed to checkout run, should have checked out run to "+judgeId, false);
        }

        ClientId judgeId2 = judges[2].getClientId();

        try {
            contest.checkoutRun(submittedRun, judgeId, false);
            assertTrue ("Checked out run, should have not checked out run ", false);
        } catch (RunUnavailableException e) {
            // Test passes if reaches here
            System.out.println("Ok. Expecting exception "+e.getMessage());
        }
        
        try {
            contest.cancelRunCheckOut(submittedRun, judgeId);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue ("Failed to UN checkout run, should have UN checked out from "+judgeId2, false);
        }
        
        try {
            contest.checkoutRun(submittedRun, judgeId2, false);
        } catch (RunUnavailableException e) {
            e.printStackTrace();
            assertTrue ("Failed to checkout run, should have checked out run to "+judgeId2, false);
        }
    }
}
