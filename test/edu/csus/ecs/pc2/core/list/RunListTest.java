package edu.csus.ecs.pc2.core.list;

import java.io.File;

import edu.csus.ecs.pc2.core.IStorage;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.security.FileSecurity;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
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
     * A Runnable class that adds a run to a runlist.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    protected class AddRun implements Runnable {

        private RunList runList;
        private Run run;

        public AddRun(RunList runList, Run run) {
            this.runList = runList;
            this.run = run;
        }

        @Override
        public void run() {
            try {
                runList.add(run);
                runList.updateRun(run);
                
                runList.setSaveToDisk(true);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }
    
    
    /**
     * Test that backup file created when settings written to disk.
     * 
     * Kicks off a thread per new run added to runlist.
     * 
     * This is a stress test.
     * 
     * Unit test bug 876.
     * 
     * @throws Exception
     */
    public void testBackupStressUsingThreads() throws Exception {
        
        if (isFastJUnitTesting()){
            return;
        }
        
        /**
         * Number of runs to add.
         */
        int numRuns = 500;
        
        String testDir = getOutputDataDirectory("runlistbackupStress");

        removeDirectory(testDir); // remove files from previous test

        new File(testDir).mkdirs();
        
        FileStorage storage = new FileStorage(testDir);
        RunList runList = new RunList(storage);

        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(2, 12, 22, 12, true);

 

        Run[] runs = sample.createRandomRuns(contest, numRuns, true, true, true);

        Thread[] thredList = new Thread[runs.length];
        int threadCount = 0;
        try {

            for (Run run : runs) {
                AddRun addRun = new AddRun(runList, run);

                /**
                 * Create thread to add run to emulate Hail Mary speed submissions.
                 */
                Thread thread = new Thread(addRun);
                thredList[threadCount] = thread;
                thread.start();
                threadCount++;
            
            }
        } catch (OutOfMemoryError e) {
            fail("testBackupStressUsingThreads unable to start "+runs.length+" threads stopped at "+(threadCount+1));
        }

        int activeThreds = countActiveThreads(thredList);
        
        while (activeThreds > 0){
            activeThreds = countActiveThreads(thredList);
            
            if (isDebugMode()){
                Thread.sleep(500);
                System.out.println("Active threads = "+activeThreds);
            }
        }

//        startExplorer(new File(testDir));
        
        assertExpectedFileCount("Expecting dir entries ", new File(testDir), numRuns * 2 + 1);
        
        assertNoZeroSizeFiles(new File(testDir));
        
    }


    private int countActiveThreads(Thread[] array) {
        int count = 0;
        for (Thread thread : array) {
            if (thread.isAlive()){
                count++;
            }
        }
        return count;
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
        
        assertNoZeroSizeFiles(new File(testDir));
        
    }
    
    
    /**
     * Test when file is corrupted.
     * 
     * Bug 879 
     * @throws Exception
     */
    public void testCorrupt() throws Exception {
        
        String testdir = getDataDirectory("testCorrupt");
        
//        startExplorer(new File(testdir));
        
        IStorage storage = new FileSecurity(testdir);
        
        RunList runList = new RunList(storage);
        String runListFile = runList.getFileName();
        System.out.println("file is "+runListFile);
        
        createSampleAnswerFile(runListFile);
        try {
            
            runList.loadFromDisk(3);
            fail("RunList file expected to be corrupt"+runListFile);
            
        } catch (FileSecurityException e) {
            
            //Passes -  expected to not read/be corrupt
            
//            FileSecurityException: NOT_READY_TO_READ
            
        }
    }
}
