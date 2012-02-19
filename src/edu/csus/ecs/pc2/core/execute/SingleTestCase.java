package edu.csus.ecs.pc2.core.execute;

import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * A single CCS test case.
 * 
 * Use execute method to run the test case.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class SingleTestCase {

    public static final int VALIDATOR_JUDGED_SUCCESS_EXIT_CODE = 42;

    public static final int VALIDATOR_JUDGED_FAILURE_EXIT_CODE = 43;
    
    private TestCaseData testCaseData = null;
    
    public SingleTestCase (Run run, int testCaseNumber, IInternalContest contest, Problem problem, boolean testRunOnly){
        testCaseData = new TestCaseData(run, testCaseNumber);
        
        // TODO check if valid test case number if not throw 
        // throw new NoSuchTestCase ("No test case "+getTestCaseNumber
        
//        ProblemDataFiles dataFiles = contest.getProblemDataFile(problem);
        
    }

    public TestCaseData execute(String resultsDirectory) {
        
        // TODO 676 code execute
        
        // validator input judgeanswer feedbackdir < teamoutput

        // create test/validation directory
        
        // unpack data files
        
        // create validator command line
        
        // TODO handle time out
        
        // TODO handle "too much output"
        
        // TODO 
        
        // execute valiadtor command 
        
        // TODO clean up/remove directory

        // get results

     
        return testCaseData;
    }

    public TestCaseData execute() {
        return execute(null);
    }
    
    
    public int getTestCaseNumber() {
        return testCaseData.getTestCaseNumber();
    }

    
}
