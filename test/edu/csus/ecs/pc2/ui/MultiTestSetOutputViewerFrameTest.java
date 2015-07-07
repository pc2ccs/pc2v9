package edu.csus.ecs.pc2.ui;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunTestCase;
import edu.csus.ecs.pc2.core.model.SampleContest;

/**
 * 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class MultiTestSetOutputViewerFrameTest extends TestCase {
    
    public static void main(String[] args) {
        
        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createStandardContest();
        ClientId judgeClientId = contest.getAccounts(Type.JUDGE).firstElement().getClientId();
        contest.setClientId(judgeClientId);
        System.out.println("Writing to log file for "+judgeClientId);
        
        IInternalController controller = sample.createController(contest, true, false);
        
        MultiTestSetOutputViewerFrame frame = new MultiTestSetOutputViewerFrame();
        frame.setContestAndController(contest, controller);
        
        Problem problem = contest.getProblems()[0];
        ProblemDataFiles problemDataFiles = controller.getProblemDataFiles(problem); // this will likely not work because the problem data files are not 
        ClientId teamId = contest.getAccounts(Type.TEAM).firstElement().getClientId();
        
        Run run = new Run(teamId, contest.getLanguages()[0], problem);
        
//        addTestCase(contest, run, 4);   //add four "test cases"
        
        System.out.println ("Run: " + run);
        System.out.println ("Problem: " + problem);
        System.out.println ("Data Files: " + problemDataFiles);
        
        frame.setData(run, problem, problemDataFiles);
        
        frame.setVisible(true);
        
    }
    /**
     * Add run test cases.
     * 
     * @param inContest
     * @param run
     * @param count
     */
    public static void addTestCase(IInternalContest inContest, Run run, int count) {
        
        JudgementRecord judgementRecord = run.getJudgementRecord();
        if (judgementRecord == null){
            throw new RuntimeException("Run has no judgement records "+run);
        }
        
        for (int i = 0; i < count; i++) {
            RunTestCase runTestCase = new RunTestCase(run, judgementRecord, i+1, run.isSolved());
            run.addTestCase (runTestCase);
        }
    }


}


