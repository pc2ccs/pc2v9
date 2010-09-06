package edu.csus.ecs.pc2.core.model;

import java.io.File;
import java.io.IOException;

import edu.csus.ecs.pc2.core.exception.RunUnavailableException;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.util.JUnitUtilities;
import junit.framework.TestCase;

/**
 * Test InternalContest class. 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ContestTest extends TestCase {
    
    private boolean debugMode = false;

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
        // Directory where test data is
        String testDir = "testdata";
        String projectPath=JUnitUtilities.locate(testDir);
        if (projectPath == null) {
            throw new IOException("Unable to locate "+testDir);
        }

        String loadFile = projectPath + File.separator+ testDir + File.separator + "Sumit.java";
        File dir = new File(loadFile);
        if (!dir.exists()) {
            System.err.println("could not find " + loadFile);
            throw new IOException("Unable to locate "+loadFile);
        }
        RunFiles runFiles = new RunFiles(submittedRun, dir.getAbsolutePath());
        
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
