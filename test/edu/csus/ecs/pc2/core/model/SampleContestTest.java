package edu.csus.ecs.pc2.core.model;

import java.io.IOException;

import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * 
 * @author ICPC
 *
 */
public class SampleContestTest extends AbstractTestCase {

    public void testLoadDataFiles() throws IOException  {

        SampleContest sampleContest = new SampleContest();

        // Standard input data directory under testdata/
        String dataFilesDir = getDataDirectory(this.getName());
//        ensureDirectory(dataFilesDir); // create directory
//        startExplorer(dataFilesDir); // opens up MS explorer for new/input directory

        Problem problem = new Problem("Foo");
        problem.setUsingExternalDataFiles(false);

        // load from test directory dataFilesDir with .in and .ans files
        ProblemDataFiles problemDataFiles = sampleContest.loadDataFiles(problem, null, dataFilesDir, "in", "ans");
        
        int testCases = problemDataFiles.getJudgesAnswerFiles().length;
        assertEquals("Expected judge data files ", 7, testCases);
        
        testCases = problemDataFiles.getJudgesDataFiles().length;
        assertEquals("Expected judge anser files ", 7, testCases);
        
        testCases = problem.getNumberTestCases();
        assertEquals("Expected problem test cases ", 7, testCases);
        
        SerializedFile[] files = problemDataFiles.getJudgesDataFiles();
        String lastname = "";
        for (SerializedFile serializedFile : files) {
            assertFalse("Internal files", serializedFile.isExternalFile());
            
            String name = serializedFile.getName();
            assertNotEquals("Names not the same ",name, lastname);
            lastname = name;
        }
        

        try {
            problemDataFiles = sampleContest.loadDataFiles(problem, null, dataFilesDir, "dat", "ans");
            fail("Expecting no input data files");
        } catch (RuntimeException e) {
            String msg = "No input files with extension dat in";
            assertEquals(msg, e.getMessage().substring(0,msg.length()));
        }

        try {
            problemDataFiles = sampleContest.loadDataFiles(problem, null, dataFilesDir, "in", "foo");
            fail("Expecting no input data files");
        } catch (RuntimeException e) {
            String msg = "No answer  files with extension foo in";
            assertEquals(msg, e.getMessage().substring(0,msg.length()));
        }
        
        try {
            problemDataFiles = sampleContest.loadDataFiles(problem, null, dataFilesDir, "test1", "ans");
            fail("Expecting no input data files");
        } catch (RuntimeException e) {
            assertEquals("Miss match expecting same test1 and ans files (1 vs 7", e.getMessage());
        }


    }
}
