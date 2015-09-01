package edu.csus.ecs.pc2.ui;

import java.io.File;
import java.io.IOException;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunTestCase;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class MultiTestSetOutputViewerFrameTest extends AbstractTestCase {

    public static void main(String[] args) {
        MultiTestSetOutputViewerFrameTest mtsv = new MultiTestSetOutputViewerFrameTest();
        mtsv.go();
    }

    private void go() {

        // create a sample contest
        SampleContest sampleContest = new SampleContest();
        IInternalContest contest = sampleContest.createStandardContest();

        // get a judge id so we can create a log file
        ClientId judgeClientId = contest.getAccounts(Type.JUDGE).firstElement().getClientId();
        contest.setClientId(judgeClientId);
        System.out.println("MTSVFrameTest: Logging to log file for " + judgeClientId);

        // get a controller for the sample contest
        IInternalController controller = sampleContest.createController(contest, true, false);
        
        Log log = controller.getLog();
        String logName = log.getLogfilename();
        System.out.println ("MTSVFrameTest: Log file name = '" + logName + "'");

        // get the first defined problem in the contest
        Problem problem = contest.getProblems()[0];

        // add some data files (judge's input data and corresponding answer files) to the problem
        // ProblemDataFiles files = sampleContest.createProblemDataFiles(problem, NUM_TEST_CASES);

        String dataFilesDir = getDataDirectory(this.getName());
        System.out.println("MTSVFrameTest: loading test data files from '" + dataFilesDir + "'");

        // ensureDirectory(dataFilesDir); // create directory
        // startExplorer(dataFilesDir); // opens up MS explorer for new/input directory
        ProblemDataFiles problemDataFiles = sampleContest.loadDataFiles(problem, null, dataFilesDir, "dat", "ans");

        contest.updateProblem(problem, problemDataFiles);

        System.out.println("MTSVFrameTest: sample contest created with problem " + problem + " containing " + problemDataFiles.getJudgesDataFiles().length + " test cases.");

        // get the judge's data files associated with the problem
        // ProblemDataFiles problemDataFiles = controller.getProblemDataFiles(problem);
        // System.out.println ("MTSVFrameTest: ProblemDataFiles from sample contest = " + problemDataFiles);

        // get a team Id under which runs will be submitted
        ClientId teamId = contest.getAccounts(Type.TEAM).firstElement().getClientId();

        // create a run submitted by the specified team for the specified problem (lang defaults to first one defined)
        Run run = null;
        try {
            run = sampleContest.createRun(contest, teamId, problem);
        } catch (ClassNotFoundException | IOException | FileSecurityException e) {
            System.err.println("Error creating run in sample contest: ");
            e.printStackTrace();
        }
        System.out.println("MTSVFrameTest: added the following run to the sample contest: " + run);

        // add a judgment record to the run (so it appears to have been judged already)
        ClientId judgeId = contest.getAccounts(Type.JUDGE).firstElement().getClientId();
        ElementId judgementId = contest.getJudgements()[3].getElementId();
        JudgementRecord record = new JudgementRecord(judgementId, judgeId, true, false); // solved=true; usedValidator=false
        run.addJudgement(record);

        // add some test cases to the run
        int numTestCases = problemDataFiles.getJudgesDataFiles().length;
        addTestCases(contest, run, numTestCases);

        // create an MTSV frame, load it with the data to be displayed
        MultiTestSetOutputViewerFrame frame = new MultiTestSetOutputViewerFrame();
        frame.setContestAndController(contest, controller);

        try {
            frame.setData(run, contest.getRunFiles(run), problem, problemDataFiles);
        } catch (ClassNotFoundException | IOException | FileSecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        //load the (hypothetical) team output file names (the only thing which can't be obtained
        // by the MTSVFrame from the problemDataFiles):
        
        //create an array with one entry for each team output file (should be same as num of data files)
        String[] teamOutputFileNames = new String[problemDataFiles.getJudgesDataFiles().length];

        //point to the folder containing test data files
        String testDir = getRootInputTestDataDirectory() + File.separator 
                + "MultiTestSetOutputViewerFrameTest" + File.separator +  "testMTSVFrame" ;

        //get the names of each of the test team output files
        for (int i=0; i<numTestCases; i++) {
            teamOutputFileNames[i] = testDir + File.separator + "datafile" + (i+1) + ".team";
        }
        
        //pass the team output file names to the MTSV frame
        frame.setTeamOutputFileNames(teamOutputFileNames);

        //show the MTSV frame
        frame.setVisible(true);

    }
    

    /**
     * Add run test cases.
     * 
     * @param inContest
     *            - the current contest model
     * @param run
     *            - the run to which test cases are to be added
     * @param numTestCases
     *            - how many test cases to add
     */
    public static void addTestCases(IInternalContest inContest, Run run, int numTestCases) {

        JudgementRecord judgementRecord = run.getJudgementRecord();
        if (judgementRecord == null) {
            throw new RuntimeException("MTSVFrameTest.addTestCases(): Run has no judgement records; " + "cannot add test cases: " + run);
        } else {
            System.out.println("MTSVFrameTest.addTestCases(): adding " + numTestCases + " test cases to run... ");
        }

        // an array of test case data. If the received test case count is greater than the
        // length of the array, subsequent test cases wrap to the beginning of the array
        // (that is, duplicate test cases are added).
        Object[][] testData = new Object[][] {
                // passed, time
                { false, 100 },// 1, 11
                { true, 200 },// 2, 12
                { false, 100 },// 3, ...
                { true, 150 },// 4
                { true, 50 },// 5
                { false, 100 },// 6
                { true, 1000 },// 7
                { false, 100 },// 8
                { true, 1500 },// 9
                { true, 10 } // 10
        };

        for (int i = 0; i < numTestCases; i++) {
            Object[] testCase = testData[i % testData.length];
            RunTestCase runTestCase = new RunTestCase(run, judgementRecord, i + 1, (boolean) testCase[0]);
            runTestCase.setElapsedMS(new Long((Integer) testCase[1]));
            run.addTestCase(runTestCase);
            // System.out.println (runTestCase);
        }
    }

    public String getName() {
        return "testMTSVFrame";
    }

}
