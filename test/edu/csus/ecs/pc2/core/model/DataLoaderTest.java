package edu.csus.ecs.pc2.core.model;

import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit tests.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class DataLoaderTest extends AbstractTestCase {

    
    public void testLoadInternal() throws Exception {
        Problem p1 = new Problem("A");
        ProblemDataFiles problemDataFiles = new ProblemDataFiles(p1);
        
        String testDirectoryName = getDataDirectory(this.getName());
//        ensureDirectory(testDirectoryName);
//        startExplorer(testDirectoryName);
        
        DataLoader.loadDataSets(problemDataFiles, testDirectoryName, true, "dat", "ans");
        
        String[] list = DataLoader.getFileNames(testDirectoryName, "dat");

        int externalCount = countExternal(problemDataFiles.getJudgesAnswerFiles());
        assertEquals("Expecting number of answer files", 28, externalCount);
        
        externalCount = countExternal(problemDataFiles.getJudgesDataFiles());
        assertEquals("Expecting number of judge data files", 28, externalCount);
        
        assertEquals("Expecting same number of files in directory "+testDirectoryName, list.length, externalCount);
        
    }
    
    
    public void testLoadExternal() throws Exception {
        Problem p1 = new Problem("A");
        ProblemDataFiles problemDataFiles = new ProblemDataFiles(p1);

        String testDirectoryName = getDataDirectory(this.getName());
        ensureDirectory(testDirectoryName);
//        startExplorer(testDirectoryName);

        DataLoader.loadDataSets(problemDataFiles, testDirectoryName, false, "in", "ans");

        String[] list = DataLoader.getFileNames(testDirectoryName, "in");
        int externalCount;

        externalCount = countExternal(problemDataFiles.getJudgesDataFiles());
        assertEquals("Expecting number of judge data files", 0, externalCount);

        externalCount = countExternal(problemDataFiles.getJudgesAnswerFiles());
        assertEquals("Expecting number of answer files", 0, externalCount);

        assertEquals("Expecting same number of files in directory " + testDirectoryName, 7, list.length);
    }

    /**
     * Counts number of external files.
     * @param files
     * @return
     */
    private int countExternal(SerializedFile[] files) {
        if (files == null){
            return 0;
        }
        int count = 0;
        for (SerializedFile serializedFile : files) {
            if (serializedFile.isExternalFile()){
                count ++;
            }
        }
        return count;
    }
}
