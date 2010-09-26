package edu.csus.ecs.pc2.core.list;

import java.io.File;
import java.io.IOException;

import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.util.JUnitUtilities;
import junit.framework.TestCase;

/**
 * Test the RunFilesList class.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class RunFilesListTest extends TestCase {

    private File loadData;
    private String testDir;

    protected void setUp() throws Exception {
        testDir = "testdata";
        String projectPath = JUnitUtilities.locate(testDir);
        if (projectPath == null) {
            throw new Exception("Unable to locate "+testDir);
        }
        testDir=projectPath+File.separator+testDir+File.separator;
        String loadFile = testDir + "Sumit.java";
        loadData = new File(loadFile);
        if (!loadData.exists()) {
            System.err.println("Could not find " + loadFile);
            throw new Exception("Could not find "+ loadFile+" in "+testDir);
        }
       super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSingleFile() throws IOException, ClassNotFoundException, FileSecurityException {

        RunFilesList filesList = new RunFilesList();

        SampleContest sampleContest = new SampleContest();

        IInternalContest contest = sampleContest.createContest(2, 2, 3, 3, false);

        Run[] runs = sampleContest.createRandomRuns(contest, 4, true, true, false);

        Run theRun = runs[0];
        Run secondRun = runs[1];


        RunFiles runFiles = new RunFiles(theRun, loadData.getAbsolutePath());
        filesList.add(theRun, runFiles);

        RunFiles runFiles2 = filesList.getRunFiles(theRun);
        assertEquals(runFiles, runFiles2);

        filesList.clearCache();
        runFiles2 = filesList.getRunFiles(theRun);
        assertNull(runFiles2);

        // Add first
        runFiles = new RunFiles(theRun, loadData.getAbsolutePath());
        filesList.add(theRun, runFiles);

        /**
         * This overwrites the runFiles stored because RunFilesList holds the last runFildes only
         * 
         * In other tests it will test the cache which will contain all runFiles added.
         */

        String filename = "hello.java";
        RunFiles secondRunFiles = new RunFiles(secondRun, testDir+filename);
        filesList.add(secondRun, secondRunFiles);

        runFiles2 = filesList.getRunFiles(theRun);

        assertNull(runFiles2); // this runfiles is no longer stored (from run 1)

        runFiles2 = filesList.getRunFiles(secondRun);

        assertNotNull(runFiles2);
        assertEquals (runFiles2.getMainFile().getName(), filename);

    }

    public void testCache() throws IOException, ClassNotFoundException, FileSecurityException {
        
        RunFilesList filesList = new RunFilesList();
        
        filesList.setCacheRunFiles(true); // Cache all runs in memory.

        SampleContest sampleContest = new SampleContest();

        IInternalContest contest = sampleContest.createContest(2, 2, 3, 3, false);

        Run[] runs = sampleContest.createRandomRuns(contest, 4, true, true, false);

        Run theRun = runs[0];
        Run secondRun = runs[1];

        RunFiles runFiles = new RunFiles(theRun, loadData.getAbsolutePath());
        filesList.add(theRun, runFiles);

        RunFiles runFiles2 = filesList.getRunFiles(theRun);
        assertEquals(runFiles, runFiles2);

        filesList.clearCache();
        runFiles2 = filesList.getRunFiles(theRun);
        assertNull(runFiles2);

        // Add first run files
        String filename1 = "Sumit.java"; 
        runFiles = new RunFiles(theRun, testDir+filename1);
        filesList.add(theRun, runFiles);

        // Add second run files
        String filename2 = "hello.java";
        RunFiles secondRunFiles = new RunFiles(secondRun, testDir+filename2);
        filesList.add(secondRun, secondRunFiles);

        runFiles2 = filesList.getRunFiles(theRun);

        assertNotNull(runFiles2); 
        assertEquals (runFiles2.getMainFile().getName(), filename1);
        
        runFiles2 = filesList.getRunFiles(secondRun);

        assertNotNull(runFiles2);
        assertEquals (runFiles2.getMainFile().getName(), filename2);
        
        runFiles2 = filesList.getRunFiles(theRun);

        assertNotNull(runFiles2); 
        assertEquals (runFiles2.getMainFile().getName(), filename1);

    }
}
