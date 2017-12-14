package edu.csus.ecs.pc2.core;

import java.io.File;

import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.security.FileStorage;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit Test
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class AutoStopContestClockThreadTest extends AbstractTestCase {

    /**
     * Test auto stop clock site Three.
     * @throws Exception
     */
    public void testAutoStopSite3() throws Exception {
        
        if (isFastJUnitTesting()){
            return;
        }
        
        testAutoStopAtSite(3);
       
    }
    
    /**
     * Test auto stop clock site One.
     * @throws Exception
     */
    public void testAutoStopSite1() throws Exception {
        
        if (isFastJUnitTesting()){
            return;
        }
        
        testAutoStopAtSite(1);
       
    }

    
    void testAutoStopAtSite (int siteNumber) throws InterruptedException{
        String outputDir = getOutputDataDirectory();

        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(siteNumber, 3, 120, 12, true);

        String contestDBdir = outputDir + File.separator + "db";
        ensureDirectory(contestDBdir);
        IStorage storage = new FileStorage(contestDBdir);
        contest.setStorage(storage);
        
//        startExplorer(contestDBdir);
        
        IInternalController controller = sample.createController(contest, true, false);
//        addConsoleHandler(controller.getLog());  // Show all log entries on console
//        Utilities.setDebugMode(true); // Dump all packets handled on console

        ContestTime time = contest.getContestTime();

        AutoStopContestClockThread thread = new AutoStopContestClockThread(controller, contest);
        assertFalse(thread.isRunning());

        int remainSecs = 5;
        time.setHaltContestAtTimeZero(true);
        time.setRemainingSecs(remainSecs);
        time.startContestClock();
        contest.updateContestTime(time);
        time = null;
        
        ContestTime newTime = contest.getContestTime();
        assertTrue("expect to halt at end of contest", newTime.isHaltContestAtTimeZero());
        assertEquals("Expected site number ", siteNumber, contest.getSiteNumber());
        assertEquals("Expected to be server ", ClientType.Type.SERVER, contest.getClientId().getClientType());
        assertEquals("Expected time to be from site  ", siteNumber, newTime.getSiteNumber());
//        System.out.println("debug remaining time is "+newTime.getRemainingSecs()+" seconds ");
        
        thread.start();

        Thread.sleep(500); // sleep so thread has time to set running true

        assertTrue("Expect contest to be running ",thread.isRunning());
        assertTrue("Expect contest clock should be running ", contest.getContestTime().isContestRunning());
        
        Thread.sleep((remainSecs + 1) * 1000);

        assertFalse("Expect contest clock should not be running ", contest.getContestTime().isContestRunning());

    }
}
