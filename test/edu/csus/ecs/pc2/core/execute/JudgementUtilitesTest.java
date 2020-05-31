// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.execute;

import java.util.List;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunTestCase;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class JudgementUtilitesTest extends AbstractTestCase {
    
    private SampleContest sample = new SampleContest();
//    
    public String getDefaultJudgementAcronym(IInternalContest contest){
//        return contest.getJudgements()[1].getAcronym(); // CE
        return contest.getJudgements()[2].getAcronym(); // WA
    }

    /**
     * Test compile error.
     * 
     * @throws Exception
     */
    public void testForCE() throws Exception {

        IInternalContest contest = createContest();
        
        ExecutionData executionData = new ExecutionData();
        
        Run[] runs = sample.createRandomRuns(contest, 12, true, true, true);
        Run run = runs[0];
        
        /**
         * Test Compilation error
         */
        executionData.setCompileSuccess(false);
        JudgementRecord judgementRecord = JudgementUtilites.createJudgementRecord(contest, run, executionData, "Works for me");
        
        Judgement judgmeent = contest.getJudgement(judgementRecord.getJudgementId());
        assertEquals(Judgement.ACRONYM_COMPILATION_ERROR, judgmeent.getAcronym());
    }
    
    /**
     * Test for execute.
     * 
     * An exception is thrown during execute.
     * 
     * @throws Exception
     */
    public void testForExec() throws Exception {
        IInternalContest contest = createContest();
        
        ExecutionData executionData = new ExecutionData();
        
        Run[] runs = sample.createRandomRuns(contest, 12, true, true, true);
        Run run = runs[0];
        
        /**
         * Test Execution Error
         */
        executionData.setCompileSuccess(true);
        
        executionData.setExecutionException(new Exception("unit test M1"));
        
        JudgementRecord judgementRecord = JudgementUtilites.createJudgementRecord(contest, run, executionData, "Works for me");
        
        Judgement judgmeent = contest.getJudgement(judgementRecord.getJudgementId());
        
        assertEquals(getDefaultJudgementAcronym(contest), judgmeent.getAcronym());
        
        String expectedMessage = "Execption during execution unit test M1";
        assertEquals(expectedMessage, judgementRecord.getValidatorResultString());
    }
    
    /**
     * Test for validator.
     * 
     * Judgement matches judgement  in list.
     * 
     * @throws Exception
     */
    public void testforValidatePositive() throws Exception {
        IInternalContest contest = createContest();
        
        ExecutionData executionData = new ExecutionData();
        
        Run[] runs = sample.createRandomRuns(contest, 12, true, true, true);
        Run run = runs[0];
        
        /**
         * Test compile ok, exec and validate not so much..
         */
        executionData.setCompileSuccess(true);
        executionData.setExecuteSucess(true);
        executionData.setValidationSuccess(true);
        Judgement judgement = contest.getJudgements()[4];
        String expected = judgement.getDisplayName();
        executionData.setValidationResults(expected);
        
        JudgementRecord judgementRecord = JudgementUtilites.createJudgementRecord(contest, run, executionData, executionData.getValidationResults());
        Judgement judgmeent = contest.getJudgement(judgementRecord.getJudgementId());
        assertEquals("WA2", judgmeent.getAcronym());
        
        String expectedMessage = "You have no clue"; // this is the judgement!
        assertEquals(expectedMessage, judgementRecord.getValidatorResultString());
    }
    
    /**
     * Test for validate.
     * 
     * Where judgement does not match any contest judgement name.
     * @throws Exception
     */
    public void testforValidateNegative() throws Exception {
        
        IInternalContest contest = createContest();
        
        ExecutionData executionData = new ExecutionData();
        
        Run[] runs = sample.createRandomRuns(contest, 12, true, true, true);
        Run run = runs[0];
        
        /**
         * Test compile ok, exec and validate not so much..
         */
        executionData.setCompileSuccess(true);
        executionData.setExecuteSucess(true);
        executionData.setValidationSuccess(false);
        Judgement judgement = contest.getJudgements()[4];
        String expected = judgement.getDisplayName();
        executionData.setValidationResults(expected);
        
        JudgementRecord judgementRecord = JudgementUtilites.createJudgementRecord(contest, run, executionData, "It's alright");
        
        Judgement judgmeent = contest.getJudgement(judgementRecord.getJudgementId());
        assertEquals(getDefaultJudgementAcronym(contest), judgmeent.getAcronym());
        
        String expectedMessage = "Undetermined";
        assertEquals(expectedMessage, judgementRecord.getValidatorResultString());

    }
    
    /**
     * Test when validate nor execute was successful.
     * 
     * @throws Exception
     */
    public void testForNoExecuteNoValidate() throws Exception {
        
        IInternalContest contest = createContest();
        
        ExecutionData executionData = new ExecutionData();
        
        Run[] runs = sample.createRandomRuns(contest, 12, true, true, true);
        Run run = runs[0];
        
        /**
         * Test compile ok, exec and validate not so much..
         */
        executionData.setCompileSuccess(true);
        executionData.setExecuteSucess(false);;
        executionData.setValidationSuccess(false);
        
        JudgementRecord judgementRecord = JudgementUtilites.createJudgementRecord(contest, run, executionData, "Works for me");
        
        Judgement judgmeent = contest.getJudgement(judgementRecord.getJudgementId());
        assertEquals(getDefaultJudgementAcronym(contest), judgmeent.getAcronym());
        
        String expectedMessage = "Undetermined";
        assertEquals(expectedMessage, judgementRecord.getValidatorResultString());
    }
    
    /**
     * Test when judgement is Yes/solved.
     * 
     * @throws Exception
     */
    public void testYes() throws Exception {
        
        
        IInternalContest contest = createContest();
        
        ExecutionData executionData = new ExecutionData();
        
        Run[] runs = sample.createRandomRuns(contest, 12, true, true, true);
        Run run = runs[0];
        
        /**
         * Test compile ok, exec and validate not so much..
         */
        executionData.setCompileSuccess(true);
        executionData.setExecuteSucess(true);
        executionData.setValidationSuccess(true);
        Judgement judgement22 = contest.getJudgements()[0];
        executionData.setValidationResults("accepted");
        
        JudgementRecord judgementRecord = JudgementUtilites.createJudgementRecord(contest, run, executionData, "accepted");
        
        Judgement newJudgment = contest.getJudgement(judgementRecord.getJudgementId());
        
        String expected = judgement22.getAcronym();
        String actual = newJudgment.getAcronym();
        
        assertEquals(expected, actual);
        
        String expectedMessage = "Yes.";
        assertEquals(expectedMessage, judgementRecord.getValidatorResultString());

    }
    

    private IInternalContest createContest() {
        IInternalContest contest = sample.createStandardContest();
        return contest;
    }
    
    
    public void testgetLastTestCaseJudgementList() throws Exception {

        IInternalContest contest = createContest();
        
        Run[] runs = sample.createRandomRuns(contest, 12, true, true, true);
        Run run = runs[0];
        
        Judgement noJudgement = contest.getJudgements()[4];
        
        Problem problem = contest.getProblem(run.getProblemId());
        
        for (int i = 0; i < 8; i++) {
            problem.addTestCaseFilenames("sumit.dat", "sumit.ans");
        }
        
        assertTrue("Expecting more than four test cases, got "+problem.getNumberTestCases(), problem.getNumberTestCases() > 4);
        
        // Add all Yes judgements
        addAllTestCases(run, problem, sample.getYesJudgement(contest));
        
        /**
         * Test case, base 1, to assign a NO judgement to.
         */
        
        // add no judgement at test case 2
        addTestCases(run, problem, 2, noJudgement, sample.getYesJudgement(contest));
        
        int noTestCaseNumber = 4;
        // Add all yes, except a No at noTestCaseNumber
        addTestCases(run, problem, noTestCaseNumber, noJudgement, sample.getYesJudgement(contest));

        List<Judgement> jList = JudgementUtilites.getLastTestCaseJudgementList(contest, run);
        assertNotNull (jList);
        
        int tcn = problem.getNumberTestCases();
        assertEquals("test cases expected ", tcn, jList.size());
        
        Judgement expectedNo = jList.get(noTestCaseNumber-1);
        assertEquals(noJudgement, expectedNo);
        
        int yesCount = 0;
        for (Judgement judgement : jList) {
            if (Judgement.ACRONYM_ACCEPTED.equals(judgement.getAcronym())){
                yesCount++;
            }
        }
        
        assertEquals("Expected yes judgement count ", 7, yesCount);
    }

    /**
     * For all test cases add judgement record.
     * @param run
     * @param problem
     * @param judgement
     */
    private void addAllTestCases(Run run, Problem problem, Judgement judgement) {
        
        for (int i = 0; i < problem.getNumberTestCases(); i++) {
            boolean passed = Judgement.ACRONYM_ACCEPTED.equals(judgement.getAcronym());
            ClientId judgerClientId = new ClientId(1, Type.JUDGE, 1);
            JudgementRecord record  = new JudgementRecord(judgement.getElementId(), judgerClientId, passed, true);
            RunTestCase runTestCaseResult = new RunTestCase(run, record, i, passed);
            run.addTestCase(runTestCaseResult);
        }
        
    }

    /**
     * Add judgements to run, add single NO to list of judgements
     * 
     * @param run
     * @param problem
     * @param testCaseNumberForNoJudgement test case number to assign judgement, base 1.
     * @param noJudgement
     * @param yesJudgement
     */
    private void addTestCases(Run run, Problem problem, int testCaseNumberForNoJudgement, Judgement noJudgement, Judgement yesJudgement) {
        for (int i = 0; i < problem.getNumberTestCases(); i++) {
            
            Judgement judgement = yesJudgement;
            
            if (i + 1 == testCaseNumberForNoJudgement){
                judgement = noJudgement;
            }
            
            boolean passed = Judgement.ACRONYM_ACCEPTED.equals(judgement.getAcronym());
            ClientId judgerClientId = new ClientId(1, Type.JUDGE, 1);
            JudgementRecord record  = new JudgementRecord(judgement.getElementId(), judgerClientId, passed, true);
            RunTestCase runTestCaseResult = new RunTestCase(run, record, i, passed);
            run.addTestCase(runTestCaseResult);
        }
    }
    

}
