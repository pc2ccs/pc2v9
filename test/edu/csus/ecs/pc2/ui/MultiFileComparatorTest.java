package edu.csus.ecs.pc2.ui;

import java.io.File;

import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * MultiFileComparator testing.  Constructs a {@link MultiFileComparator} and fills it with
 * test data the same way a real client (e.g. {@link MultiTestSetOutputViewerPane}) would.
 * 
 * @author John
 * 
 */

public class MultiFileComparatorTest extends AbstractTestCase {

    public static void main(String[] args) {
        MultiFileComparatorTest test = new MultiFileComparatorTest();
        test.doIt();
    }

    public void doIt() {
        MultiFileComparator mfc = new MultiFileComparator();
        
        mfc.setContestAndController(null, null);
        
        //point to the folder containing test data files
        String testDir = getRootInputTestDataDirectory() + File.separator 
                + "MultiTestSetOutputViewerFrameTest" + File.separator +  "testMTSVFrame" ;
        
        //add some test case data to the mfc 
        int numTestCases = 20;
        int runID = 317;
        int [] testCaseList = new int [] {1,2,3,4,5,6,7,8,9,10,12,14,16,18,20,11,13,15,17,19};
        
        //build a specific list of test cases for this execution run
        int [] testCases = new int[numTestCases];

        //build arrays of team/judge's output and judge's data file names, of the form "datafileXX.{team,ans,dat}"
        String [] teamOutputFileNames = new String [numTestCases];
        String [] judgesDataFileNames = new String [numTestCases];
        String [] judgesOutputFileNames = new String [numTestCases];
        for (int i=0; i<numTestCases; i++) {
            testCases[i] = testCaseList[i];
            teamOutputFileNames[i] = testDir + File.separator + "datafile" + (i+1) + ".team";
            judgesDataFileNames[i] = testDir + File.separator + "datafile" + (i+1) + ".dat";
            judgesOutputFileNames[i] = testDir + File.separator + "datafile" + (i+1) + ".ans";
        }
        
        
        mfc.setData(runID, testCases, teamOutputFileNames, judgesOutputFileNames, judgesDataFileNames);
        
        mfc.setVisible(true);

    }
}
