package edu.csus.ecs.pc2.core.list;

import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SampleContest;
import junit.framework.TestCase;

/**
 * Test the RunFilesList class.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class RunFilesListTest extends TestCase {

    public void testSingleFile() {

        RunFilesList filesList = new RunFilesList();

        SampleContest sampleContest = new SampleContest();

        IInternalContest contest = sampleContest.createContest(2, 2, 3, 3, false);

        Run[] runs = sampleContest.createRandomRuns(contest, 4, true, true, false);

        Run theRun = runs[0];
        Run secondRun = runs[1];

        RunFiles runFiles = new RunFiles(theRun, "samps/Sumit.java");
        filesList.add(theRun, runFiles);

        RunFiles runFiles2 = filesList.getRunFiles(theRun);
        assertEquals(runFiles, runFiles2);

        filesList.clearCache();
        runFiles2 = filesList.getRunFiles(theRun);
        assertNull(runFiles2);

        // Add first
        runFiles = new RunFiles(theRun, "samps/Sumit.java");
        filesList.add(theRun, runFiles);

        /**
         * This overwrites the runFiles stored because RunFilesList holds the last runFildes only
         * 
         * In other tests it will test the cache which will contain all runFiles added.
         */

        String filename = "hello.java";
        RunFiles secondRunFiles = new RunFiles(secondRun, "samps/"+filename);
        filesList.add(secondRun, secondRunFiles);

        runFiles2 = filesList.getRunFiles(theRun);

        assertNull(runFiles2); // this runfiles is no longer stored (from run 1)

        runFiles2 = filesList.getRunFiles(secondRun);

        assertNotNull(runFiles2);
        assertEquals (runFiles2.getMainFile().getName(), filename);

    }

    public void testCache() {
        
        RunFilesList filesList = new RunFilesList();
        
        filesList.setCacheRunFiles(true); // Cache all runs in memory.

        SampleContest sampleContest = new SampleContest();

        IInternalContest contest = sampleContest.createContest(2, 2, 3, 3, false);

        Run[] runs = sampleContest.createRandomRuns(contest, 4, true, true, false);

        Run theRun = runs[0];
        Run secondRun = runs[1];

        RunFiles runFiles = new RunFiles(theRun, "samps/Sumit.java");
        filesList.add(theRun, runFiles);

        RunFiles runFiles2 = filesList.getRunFiles(theRun);
        assertEquals(runFiles, runFiles2);

        filesList.clearCache();
        runFiles2 = filesList.getRunFiles(theRun);
        assertNull(runFiles2);

        // Add first run files
        String filename1 = "Sunit.java"; 
        runFiles = new RunFiles(theRun, "samps/"+filename1);
        filesList.add(theRun, runFiles);

        /**
         * This overwrites the runFiles stored because RunFilesList holds the last runFildes only
         * 
         * In other tests it will test the cache which will contain all runFiles added.
         */

        String filename = "hello.java";
        RunFiles secondRunFiles = new RunFiles(secondRun, "samps/"+filename);
        filesList.add(secondRun, secondRunFiles);

        runFiles2 = filesList.getRunFiles(theRun);

        assertNotNull(runFiles2); 
        assertEquals (runFiles2.getMainFile().getName(), filename1);
        
        runFiles2 = filesList.getRunFiles(secondRun);

        assertNotNull(runFiles2);
        assertEquals (runFiles2.getMainFile().getName(), filename);
    }
}
