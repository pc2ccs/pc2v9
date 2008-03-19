package edu.csus.ecs.pc2.core.list;

import java.io.File;

import edu.csus.ecs.pc2.core.execute.ExecutionData;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunResultFiles;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import junit.framework.TestCase;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class RunResultsFileListTest extends TestCase {

    private RunResultsFileList runResultsFileList;
    
    private int siteNumber = 45;
    
    public static void main(String[] args) {
    }

    protected void setUp() throws Exception {
        super.setUp();
        runResultsFileList = new RunResultsFileList(siteNumber);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method for 'edu.csus.ecs.pc2.core.list.RunResultsFileList.RunResultsFileList(int)'
     */
    public void testRunResultsFileList() {
        
        SampleContest sampleContest = new SampleContest();
        IInternalContest contest = sampleContest.createContest(siteNumber, siteNumber, 22, 5, false);

        ClientId clientId = contest.getAccounts(Type.TEAM).firstElement().getClientId();
        ClientId judgeId = contest.getAccounts(Type.JUDGE).firstElement().getClientId();
        
        RunList runList = new RunList(siteNumber, true);
        
        Problem problem = contest.getProblems()[0];
        
        Run run = new Run(clientId, contest.getLanguages()[0], problem);
        runList.addNewRun(run);

        System.out.println("Added run "+run);
        
        run = new Run(clientId, contest.getLanguages()[0], problem);
        runList.addNewRun(run);

        System.out.println("Added run "+run);
        
        Judgement judgement = contest.getJudgements()[3];
        JudgementRecord judgementRecord = new JudgementRecord(judgement.getElementId(), judgeId, false, false);
        
        ExecutionData executionData = null;
        
        RunResultFiles runResultFiles = new RunResultFiles(run, problem.getElementId(), judgementRecord, executionData);
        
        runResultsFileList.add(run, judgementRecord, runResultFiles);    
        
        String fileName = runResultsFileList.getFileName(run, judgementRecord);
        
        File file = new File(fileName);
        
        assertTrue ("Expecting to create file: "+fileName, file.exists());
        

    }

    /*
     * Test method for 'edu.csus.ecs.pc2.core.list.RunResultsFileList.stripChar(String, char)'
     */
    public void testStripChar() {

    }

    /*
     * Test method for 'edu.csus.ecs.pc2.core.list.RunResultsFileList.add(Run, JudgementRecord, RunResultFiles)'
     */
    public void testAdd() {

    }

    /*
     * Test method for 'edu.csus.ecs.pc2.core.list.RunResultsFileList.getRunResultFiles(Run, JudgementRecord)'
     */
    public void testGetRunResultFiles() {

    }

}
