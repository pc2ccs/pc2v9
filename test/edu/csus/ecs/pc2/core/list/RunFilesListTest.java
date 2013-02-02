package edu.csus.ecs.pc2.core.list;

import java.io.IOException;

import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Test the RunFilesList class.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class RunFilesListTest extends AbstractTestCase {

//    private File sumitSourceFilename;
//    private String testDir;

//    protected void setUp() throws Exception {
//        super.setUp();
//        
//        testDir = "testdata";
//        String projectPath = JUnitUtilities.locate(testDir);
//        if (projectPath == null) {
//            throw new Exception("Unable to locate "+testDir);
//        }
//        testDir=projectPath+File.separator+testDir+File.separator;
//        String loadFile = testDir + "Sumit.java";
//        sumitSourceFilename = new File(loadFile);
//        if (!sumitSourceFilename.exists()) {
//            System.err.println("Could not find " + loadFile);
//            throw new Exception("Could not find "+ loadFile+" in "+testDir);
//        }
//        
//        getSamplesSourceFilename()
//        
//    }

//    protected void tearDown() throws Exception {
//        super.tearDown();
//    }

    public void testSingleFile() throws IOException, ClassNotFoundException, FileSecurityException {

        RunFilesList filesList = new RunFilesList();

        SampleContest sampleContest = new SampleContest();

        IInternalContest contest = sampleContest.createContest(2, 2, 3, 3, false);

        Run[] runs = sampleContest.createRandomRuns(contest, 4, true, true, false);

        Run theRun = runs[0];
        Run secondRun = runs[1];
        
        
        String sumitFilename = getSamplesSourceFilename("Sumit.java");


        RunFiles runFiles = new RunFiles(theRun, sumitFilename);
        filesList.add(theRun, runFiles);

        RunFiles runFiles2 = filesList.getRunFiles(theRun);
        assertEquals(runFiles, runFiles2);

        filesList.clearCache();
        runFiles2 = filesList.getRunFiles(theRun);
        assertNull(runFiles2);

        // Add first
        runFiles = new RunFiles(theRun, sumitFilename);
        filesList.add(theRun, runFiles);

        /**
         * This overwrites the runFiles stored because RunFilesList holds the last runFildes only
         * 
         * In other tests it will test the cache which will contain all runFiles added.
         */

//        String filename = "hello.java";
        
        String helloFilename = getSamplesSourceFilename(HELLO_SOURCE_FILENAME);
        
        RunFiles secondRunFiles = new RunFiles(secondRun, helloFilename);
        filesList.add(secondRun, secondRunFiles);

        runFiles2 = filesList.getRunFiles(theRun);

        assertNull(runFiles2); // this runfiles is no longer stored (from run 1)

        runFiles2 = filesList.getRunFiles(secondRun);

        assertNotNull(runFiles2);
        assertEquals (runFiles2.getMainFile().getName(), HELLO_SOURCE_FILENAME);

    }

    public void testCache() throws IOException, ClassNotFoundException, FileSecurityException {
        
        RunFilesList filesList = new RunFilesList();
        
        filesList.setCacheRunFiles(true); // Cache all runs in memory.

        SampleContest sampleContest = new SampleContest();

        IInternalContest contest = sampleContest.createContest(2, 2, 3, 3, false);

        Run[] runs = sampleContest.createRandomRuns(contest, 4, true, true, false);

        Run theRun = runs[0];
        Run secondRun = runs[1];
        
        String sumitFilename = getSamplesSourceFilename(SUMIT_SOURCE_FILENAME);

        RunFiles runFiles = new RunFiles(theRun, sumitFilename);
        filesList.add(theRun, runFiles);

        RunFiles runFiles2 = filesList.getRunFiles(theRun);
        assertEquals(runFiles, runFiles2);

        filesList.clearCache();
        runFiles2 = filesList.getRunFiles(theRun);
        assertNull(runFiles2);

        // Add first run files
        runFiles = new RunFiles(theRun, sumitFilename);
        filesList.add(theRun, runFiles);

        // Add second run files
        String helloFilename = getSamplesSourceFilename(HELLO_SOURCE_FILENAME);
        RunFiles secondRunFiles = new RunFiles(secondRun, helloFilename);
        filesList.add(secondRun, secondRunFiles);

        runFiles2 = filesList.getRunFiles(theRun);

        assertNotNull(runFiles2);
        assertEquals(runFiles2.getMainFile().getName(), SUMIT_SOURCE_FILENAME);

        runFiles2 = filesList.getRunFiles(secondRun);

        assertNotNull(runFiles2);
        assertEquals(runFiles2.getMainFile().getName(), HELLO_SOURCE_FILENAME);

        runFiles2 = filesList.getRunFiles(theRun);

        assertNotNull(runFiles2);
        assertEquals(runFiles2.getMainFile().getName(), SUMIT_SOURCE_FILENAME);

    }
}
