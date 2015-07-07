package edu.csus.ecs.pc2.ui;

import java.io.IOException;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.IInternalController;
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

/**
 * 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class MultiTestSetOutputViewerFrameTest extends TestCase {
    
    public static void main(String[] args) {
        
        //create a sample contest
        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createStandardContest();
        
        //get a judge id so we can create a log file
        ClientId judgeClientId = contest.getAccounts(Type.JUDGE).firstElement().getClientId();
        contest.setClientId(judgeClientId);
        System.out.println("Writing to log file for "+judgeClientId);
        
        //get a controller for the sample contest 
        IInternalController controller = sample.createController(contest, true, false);
        
        //get the first defined problem in the contest
        Problem problem = contest.getProblems()[0];
        
        //get the judge's data files associated with the problem
        ProblemDataFiles problemDataFiles = controller.getProblemDataFiles(problem); // this will likely not work because the problem data files are not 
        
        //get a team Id under which runs will be submitted
        ClientId teamId = contest.getAccounts(Type.TEAM).firstElement().getClientId();
        
        //create a run submitted by the specified team for the specified problem (lang defaults to first one defined)
        Run run = null;
        try {
            run = sample.createRun(contest, teamId, problem);
        } catch (ClassNotFoundException | IOException | FileSecurityException e) {
            System.err.println("Error creating run in sample contest: ");
            e.printStackTrace();
        }
        
        //add a judgment record to the run (so it appears to have been judged already)
        //the following code was excerpted from RunTestCaseTest
        ClientId judgeId = contest.getAccounts(Type.JUDGE).firstElement().getClientId();
        ElementId  judgementId = contest.getJudgements()[3].getElementId();
        JudgementRecord record = new JudgementRecord(judgementId, judgeId, true, false);    //solved=true; usedValidator=false
        run.addJudgement(record);
//        int testNumber = 5;
//        boolean solved = true;
//        IGetDate dateVar = new RunTestCase(run, record, testNumber, solved);

        //add some test cases to the run
        addTestCase(contest, run, 4);   //add four "test cases"
        
        System.out.println ("Run: " + run);
        System.out.println ("Problem: " + problem);
        System.out.println ("Data Files: " + problemDataFiles);

        //create an MTSV frame and show it
        MultiTestSetOutputViewerFrame frame = new MultiTestSetOutputViewerFrame();
        frame.setContestAndController(contest, controller);

        frame.setData(run, problem, problemDataFiles);
        
        frame.setVisible(true);
        
    }
    /**
     * Add run test cases.
     * 
     * @param inContest - the current contest model
     * @param run - the run to which test cases are to be added
     * @param count - how many test cases to add
     */
    public static void addTestCase(IInternalContest inContest, Run run, int count) {
        
        JudgementRecord judgementRecord = run.getJudgementRecord();
        if (judgementRecord == null){
            throw new RuntimeException("Run has no judgement records "+run);
        } else {
            System.out.println("adding " + count + " test cases to run: ");
        }
        
        for (int i = 0; i < count; i++) {
            RunTestCase runTestCase = new RunTestCase(run, judgementRecord, i+1, run.isSolved());
            run.addTestCase (runTestCase);
        }
    }


}


