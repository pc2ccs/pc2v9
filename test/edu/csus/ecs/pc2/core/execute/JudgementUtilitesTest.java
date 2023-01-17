// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.execute;

import java.util.List;

import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
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

    /**
     * Test with sample contest/default judgements.
     * 
     * By default will use the judgements from the current site (site number 3)
     * 
     * @throws Exception
     */
    public void testgetSingleListofJudgements() throws Exception {

        IInternalContest contest = sample.createStandardContest();
        Judgement[] judgements = contest.getJudgements();
        sample.createController(contest, true, false); // creates StaticLog instance

        assertEquals("judgement count", 9, judgements.length);

        List<Judgement> jList = JudgementUtilites.getSingleListofJudgements(contest);
        assertEquals("judgement count", 9, jList.size());

        assertEquals("judgement site number", 3, jList.get(0).getSiteNumber());
        assertEquals("judgement ", "Yes.", jList.get(0).toString());
        assertEquals("judgement ", "You have no clue", jList.get(4).toString());
        assertEquals("judgement ", "Contact Staff - you have no hope", jList.get(jList.size() - 1).toString());

    }

    /**
     * Test to ensure that all judgements found are on site 1.
     * 
     * When there are judgements from site 1, use those judgements.
     * 
     * @throws Exception
     */
    public void testgetSingleListofJudgementsWithSite1Judgements() throws Exception {

        IInternalContest contest = sample.createStandardContest();
        Judgement[] judgements = contest.getJudgements();
        sample.createController(contest, true, false); // creates StaticLog instance

        assertEquals("judgement count", 9, judgements.length);
        assertEquals("site count", 3, contest.getSites().length);

        addNewJudgements(1, contest, contest.getJudgements());

        // Model now has 18 judgements, from site 3 and site 1
        judgements = contest.getJudgements();
        assertEquals("judgement count", 18, judgements.length);

        List<Judgement> jList = JudgementUtilites.getSingleListofJudgements(contest);
        assertEquals("judgement count", 9, jList.size());

        assertEquals("judgement site number", 1, jList.get(0).getSiteNumber());
        assertEquals("judgement ", "Yes.", jList.get(0).toString());
        assertEquals("judgement ", "You have no clue", jList.get(4).toString());
        assertEquals("judgement ", "Contact Staff - you have no hope", jList.get(jList.size() - 1).toString());

        // ensure that each judgement is from site 1
        for (Judgement judgement : jList) {
            assertEquals("judgement site number", 1, judgement.getSiteNumber());
        }
    }

    private void addNewJudgements(int siteNumber, IInternalContest contest, Judgement[] judgements) {
        for (Judgement judgement : judgements) {
            Judgement newJudgement = new Judgement(judgement.toString(), judgement.getAcronym());
            newJudgement.setSiteNumber(siteNumber);
            contest.addJudgement(newJudgement);
        }
    }

    /**
     * Test JudgementUtilites.getLastTestCaseArray.
     *
     * Added two test cases (first all judged AC, second all judged WA).
     * Test that getLastTestCaseArray returns the last ("WA") RunTestCases
     *
     * @throws Exception
     */
    public void testgetLastTestCaseArray() throws Exception {
        IInternalContest contest = loadFullSampleContest(null, "mini");
        Account[] teams = getTeamAccounts(contest);
        assertNotNull(teams);
        assertEquals("Team count ", 151, teams.length);
        Account[] judges = getJudgesAccounts(contest);
        assertNotNull(judges);
        assertEquals("Judge count ", 14, judges.length);
        Language language = contest.getLanguages()[0];
        Problem problem = contest.getProblems()[0];
        Run run = new Run(teams[0].getClientId(), language, problem);
        addJudgements(contest);
        Judgement[] judgements = contest.getJudgements();
        assertEquals("Judgement count ", 2, judgements.length);
        Judgement acJudgement = getJudgement(contest, Judgement.ACRONYM_ACCEPTED);
        assertNotNull("AC Judgement", acJudgement);
        Judgement waJudgement = getJudgement(contest, Judgement.ACRONYM_WRONG_ANSWER);
        assertNotNull("WA Judgement", waJudgement);
        RunTestCase[] recs = JudgementUtilites.getLastTestCaseArray(contest, run);
        assertEquals("Expected zero test cases ", 0, recs.length);
        // Add firstset of test cases - all AC
        for (int testCaseNum = 1; testCaseNum <= problem.getNumberTestCases(); testCaseNum++) {
            addRunTestCase(contest, run, testCaseNum, acJudgement, judges[0].getClientId());
        }
        recs = JudgementUtilites.getLastTestCaseArray(contest, run);
        assertEquals("Expected test cases ", 10, recs.length);
        // Add second set of test cases - all WA
        for (int testCaseNum = 1; testCaseNum <= problem.getNumberTestCases(); testCaseNum++) {
            addRunTestCase(contest, run, testCaseNum, waJudgement, judges[0].getClientId());
        }
        assertEquals("Expected total test cases ", 20, run.getRunTestCases().length);
        recs = JudgementUtilites.getLastTestCaseArray(contest, run);
        assertEquals("Expected test cases ", 10, recs.length);
        // Test that all judgements are WA
        for (RunTestCase runTestCase : recs) {
            ElementId judgementId = runTestCase.getJudgementId();
            Judgement judgement = contest.getJudgement(judgementId);
            assertEquals("Expecting WA judgement", Judgement.ACRONYM_WRONG_ANSWER, judgement.getAcronym());
        }
    }
    /**
     * Add a small sample of judgements including AC and WA
     * @param contest
     */
    private void addJudgements(IInternalContest contest) {
        Judgement judgementYes = new Judgement("Yes.", Judgement.ACRONYM_ACCEPTED);
        contest.addJudgement(judgementYes);
        Judgement judgement = new Judgement("Yes.", Judgement.ACRONYM_WRONG_ANSWER);
        contest.addJudgement(judgement);
    }
    /**
     * Find judgement in model for the input acronym.
     * @param contest
     * @param acronym
     * @return null if no judgement found, else the judgement with the acronym
     */
    private Judgement getJudgement(IInternalContest contest, String acronym) {
        Judgement[] list = contest.getJudgements();
        for (Judgement judgement : list) {
            if (acronym.equals(judgement.getAcronym())) {
                return judgement;
            }
        }
        return null;
    }
    /**
     * Add Run Test case to run.
     *
     * @param contest
     * @param run
     * @param testNumber
     * @param judgement
     * @param judgerClientId
     */
    private void addRunTestCase(IInternalContest contest, Run run, int testNumber, Judgement judgement, ClientId judgerClientId) {
        boolean solved = judgement.getAcronym().equals(Judgement.ACRONYM_ACCEPTED);
        JudgementRecord record = new JudgementRecord(judgement.getElementId(), judgerClientId, solved, true, true);
        RunTestCase runTestCase = new RunTestCase(run, record, testNumber, solved);
        run.addTestCase(runTestCase);
    }

    
}
