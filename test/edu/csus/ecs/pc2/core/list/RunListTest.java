package edu.csus.ecs.pc2.core.list;

import java.io.File;

import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.security.FileStorage;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * JUnit for RunList.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class RunListTest extends AbstractTestCase {
    
    protected String getTestDirectoryName(){
        String testDir = getOutputDataDirectory();
        
        if (!new File(testDir).isDirectory()) {
            new File(testDir).mkdirs();
        }

        return testDir;
    }


    public void testClear() throws Exception {
        
        String testDir = getTestDirectoryName() + File.separator + "runlistclear";

        removeDirectory(testDir); // remove files from previous test
        
        new File(testDir).mkdirs();
        
        
        FileStorage storage = new FileStorage(testDir);
        RunList runList = new RunList(storage);

        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(2, 12, 22, 12, true);

        int numRuns = 4;

        Run[] runs = sample.createRandomRuns(contest, numRuns, true, true, true);

        for (Run run : runs) {
            runList.addNewRun(run);
            runList.updateRun(run);
        }

        assertEquals("Number of runs ", numRuns, runList.getList().length);
        assertEquals("Next run number ", numRuns + 1, runList.getNextRunNumber());

        runList.clear();  // Clear cache and disk

        assertEquals("Number of runs ", 0, runList.getList().length);
        assertEquals("Next run number ", 1, runList.getNextRunNumber());

        runList = null;
        
        // re-load from disk to test clear.
        
        RunList runList2 = new RunList(storage);

        assertEquals("Number of runs ", 0, runList2.getList().length);
        assertEquals("Next run number ", 1, runList2.getNextRunNumber());

    }
    
    /**
     * Test that backup file created when settings written to disk.
     * 
     * Unit test bug 876.
     * 
     * @throws Exception
     */
    public void testBackup() throws Exception {
        
        String testDir = getOutputDataDirectory("runlistbackup");

        removeDirectory(testDir); // remove files from previous test

        new File(testDir).mkdirs();
        
        FileStorage storage = new FileStorage(testDir);
        RunList runList = new RunList(storage);

        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(2, 12, 22, 12, true);

        int numRuns = 4;

        Run[] runs = sample.createRandomRuns(contest, numRuns, true, true, true);
        
        for (Run run : runs) {
            runList.addNewRun(run);
            runList.updateRun(run);
        }
        
        runList.setSaveToDisk(true);

//        startExplorer(new File(testDir));
        
        assertExpectedFileCount("Expecting dir entries ", new File(testDir), 9);
        
    }
}
