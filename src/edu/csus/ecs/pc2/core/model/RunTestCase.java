// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.model;

import java.util.Date;

import edu.csus.ecs.pc2.clics.CLICSJudgementType.CLICS_JUDGEMENT_ACRONYM;
import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.execute.Executable;
import edu.csus.ecs.pc2.imports.ccs.TestDataGroup;

/**
 * Test Case results.
 *  
 * @author pc2@ecs.csus.edu
 */

public class RunTestCase implements IElementObject, IGetDate{

    private static final long serialVersionUID = -9160568142729051113L;
    
    private boolean active = true;
    
    private boolean validated = false;
    
    public static final String TESTCASE_RECORD_ID = "TestCase";
    
    /**
     * A Unique contest-wide identifier this JudgementRecord instance.
     * 
     */
    private ElementId elementId = new ElementId (TESTCASE_RECORD_ID);
    
    private ElementId runElementId;
    
    private ElementId judgementId;
    
    private int testNumber;
    
    /**
     * The "score" associated with this particular test case.
     * (For point-scoring contests, each test case includes a "score" determined by the problem validator.
     * Once all test cases have been executed, a separate "Grader" program is invoked by the {@link Executable}
     * class to examine the scores for all test cases and determine, based on the scoring attributes of the problem,
     * what the "actual score" for the Run (Submission) will be.)
     */
    private double score = 0.0;
    
    /**
     * The CLICS judgement acronym associated with this RunTestCase once the submission (run) has 
     * been executed using this RunTestCase.
     */
    private CLICS_JUDGEMENT_ACRONYM judgementAcronym = null;
    
    /**
     * The TestDataGroup with which the test case associated with this RunTestCase is associated.
     */
    private TestDataGroup testDataGroup = null;
    
    /**
     * Whether or not the run passed this particular Test Case.
     */
    private boolean passed = false;
    
    /**
     * Number of elapsed MS.
     */
    private long elapsedMS;
    
    private long time = new Date().getTime();
    private long contestTimeMS;
    
    /**
     * Constructor defining a Test Case Result for a run.  Each Test Case Result saves
     * a link to the Run and JudgmentRecord with which it is associated;
     * each Test Case Result is marked as the
     * indicated Test Case Number and with the indicated status (either the Run
     * passed the Test Case or it failed the Test Case).
     * 
     * @param run - the Run for which this test case applies.
     * @param record - the JudgementRecord created when the run was executed using this test case as input.
     * @param testNumber - the id of this test case.
     * @param passed - boolean indicating whether the Run passed this test case or not.
     */
    public RunTestCase(Run run, JudgementRecord record, int testNumber, boolean passed) {
        runElementId = run.getElementId();
        judgementId = record.getJudgementId();
        this.testNumber = testNumber;
        this.passed = passed;
    }
    
    public ElementId getElementId() {
        return elementId;
    }

    public int versionNumber() {
        return elementId.getVersionNumber();
    }
    
    public int getSiteNumber() {
        return elementId.getSiteNumber();
    }

    public void setSiteNumber(int siteNumber) {
        elementId.setSiteNumber(siteNumber);
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public int getTestNumber() {
        return testNumber;
    }
    
    public boolean matchesJudgement(JudgementRecord judgementRecord){
        if (judgementRecord == null){
            return false;
        } else {
            return judgementId.equals(judgementRecord.getJudgementId());
        }
    }

    /**
     * Returns true if Run to which this Test Case Result is attached passed this
     * Test Case; false otherwise.
     * @return whether the run passed this Test Case
     */
    public boolean isPassed() {
        return passed;
    }
    
    public ElementId getRunElementId() {
        return runElementId;
    }

    public long getElapsedMins() {
        return elapsedMS / Constants.MS_PER_MINUTE;
    }
    
    public void setElapsedMS(long elapsedMS) {
        this.elapsedMS = elapsedMS;
    }
    
    public long getElapsedMS() {
        return elapsedMS;
    }
    

    /**
     * Get wall clock time for submission.
     * 
     * @return
     */
    public  Date getDate() {
        return new Date(time);
    }
    
    /**
     * Set submission date.
     * 
     * This field does not affect {@link #getElapsedMS()} or {@link #getElapsedMins()}.
     * 
     * @param date Date, if null then sets Date long value to zero
     */
    public void setDate (Date date){
        time = 0;
        if (date != null){
            time = date.getTime();
        }
    }
    
    public ElementId getJudgementId() {
        return judgementId;
    }
    
    /**
     * Returns a String representing the contents of this Test Case.
     */
    public String toString() {
        String ret = "";
        ret += "[Test Case: ";
        ret += "num=" + testNumber + ", ";
        ret += "passed=" + passed + ", ";
        ret += "time(ms)=" + elapsedMS + "]";
        
        return ret ;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public long getContestTimeMS() {
        return contestTimeMS;
    }

    public void setContestTimeMS(long contestTimeMS) {
        this.contestTimeMS = contestTimeMS;
    }

    public double getScore() {
        return score;
    }
    
    public void setScore(double runTestCaseScore) {
        this.score = runTestCaseScore;
    }

    public CLICS_JUDGEMENT_ACRONYM getJudgementAcronym() {
        return judgementAcronym;
    }

    public void setJudgementAcronym(CLICS_JUDGEMENT_ACRONYM judgementAcronym) {
        this.judgementAcronym = judgementAcronym;
    }

    public void setTestDataGroup(TestDataGroup testDataGroup) {
        this.testDataGroup = testDataGroup ;
    }

    public TestDataGroup getTestDataGroup() {
        return testDataGroup;
    }

}
