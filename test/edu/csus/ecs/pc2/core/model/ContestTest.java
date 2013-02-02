package edu.csus.ecs.pc2.core.model;

import java.io.IOException;

import edu.csus.ecs.pc2.core.exception.RunUnavailableException;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Test InternalContest class. 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ContestTest extends AbstractTestCase {
    
    private boolean debugMode = false;

    /**
     * Create a new run in the contest.
     * 
     * @param contest
     * @return created run.
     */
    private Run createRun (IInternalContest contest, ClientId clientId) {
        Problem problem = contest.getProblems()[0];
        Language language = contest.getLanguages()[0];
        Run run = new Run(clientId, language, problem);
        return run;
    }
    
    public void testCheckOut () throws IOException, ClassNotFoundException, FileSecurityException{
        
        SampleContest sampleContest = new SampleContest();
        
        IInternalContest contest = sampleContest.createContest(1, 1, 12, 4, true);
        
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

        String loadFile = getSamplesSourceFilename(SUMIT_SOURCE_FILENAME);
        
        assertFileExists(loadFile);
        
        RunFiles runFiles = new RunFiles(submittedRun, loadFile);
        
        contest.acceptRun(submittedRun, runFiles);
        
        ClientId judgeId = judges[0].getClientId();
        
        // Judge gets run
        
        try {
            contest.checkoutRun(submittedRun, judgeId, false, false);
            if (debugMode){
                System.out.println(judgeId+" checked out "+submittedRun);
            }
        } catch (RunUnavailableException e) {
            e.printStackTrace();
            assertTrue ("Failed to checkout run, should have checked out run to "+judgeId, false);
        }

        ClientId judgeId2 = judges[2].getClientId();

        try {
            contest.checkoutRun(submittedRun, judgeId, false, false);
            assertTrue ("Checked out run, should have not checked out run ", false);
        } catch (RunUnavailableException e) {
            // Test passes if reaches here
            if (debugMode) {
                System.out.println("Ok. Expecting exception " + e.getMessage());
            }
        }
        
        try {
            contest.cancelRunCheckOut(submittedRun, judgeId);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue ("Failed to UN checkout run, should have UN checked out from "+judgeId2, false);
        }
        
        try {
            contest.checkoutRun(submittedRun, judgeId2, false, false);
        } catch (RunUnavailableException e) {
            e.printStackTrace();
            assertTrue ("Failed to checkout run, should have checked out run to "+judgeId2, false);
        }
    }
}
