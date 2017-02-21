package edu.csus.ecs.pc2.core;

import java.io.File;

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

    public void testAutoStop() throws Exception {

        String outputDir = getOutputDataDirectory();

        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createStandardContest();

        String contestDBdir = outputDir + File.separator + "db";
        ensureDirectory(contestDBdir);
        IStorage storage = new FileStorage(contestDBdir);
        contest.setStorage(storage);

        IInternalController controller = sample.createController(contest, true, false);

        addConsoleHandler(controller.getLog());

        ContestTime time = contest.getContestTime();

        AutoStopContestClockThread thread = new AutoStopContestClockThread(controller, time);
        assertFalse(thread.isRunning());

        int remainSecs = 5;
        time.setRemainingSecs(remainSecs);
        time.startContestClock();
        contest.updateContestTime(time);

        thread = new AutoStopContestClockThread(controller, time);
        thread.start();

        Thread.sleep(100); // sleep so thread has time to set running true

        assertTrue(thread.isRunning());

        Thread.sleep((remainSecs + 1) * 1000);

        assertFalse(thread.isRunning());
        assertFalse(contest.getContestTime().isContestRunning());

    }
}
