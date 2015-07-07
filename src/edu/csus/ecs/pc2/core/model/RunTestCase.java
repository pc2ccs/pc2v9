package edu.csus.ecs.pc2.core.model;

import java.util.Date;

import edu.csus.ecs.pc2.core.Constants;

/**
 * Test Case results.
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class RunTestCase implements IElementObject, IGetDate{

    /**
     * 
     */
    private static final long serialVersionUID = -9160568142729051113L;
    /**
     * 
     */
    
    private boolean active = true;
    
    public static final String TESTCASE_RECORD_ID = "TestCase";
    
    /**
     * A Unique contest-wide identifier this JudgementRecord instance.
     * 
     */
    private ElementId elementId = new ElementId (TESTCASE_RECORD_ID);
    
    private ElementId runElementId;
    
    private ElementId judgementId;
    
    private int testNumber;
    
    private boolean solved = false;
    
    /**
     * Number of elapsed MS.
     */
    private long elapsedMS;
    
    private long time = new Date().getTime();
    
    public RunTestCase(Run run, JudgementRecord record, int testNumber, boolean solved) {
        runElementId = run.getElementId();
        judgementId = record.getElementId();
        this.testNumber = testNumber;
        this.solved = solved;
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
            return judgementId.equals(judgementRecord.getElementId());
        }
    }

    public boolean isSolved() {
        return solved;
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
    
    /**
     * Returns a String representing the contents of this Test Case.
     */
    public String toString() {
        String ret = "";
        ret += "Test Case: ";
        ret += "num=" + testNumber + ", ";
        ret += "solved=" + solved + ", ";
        ret += "time(ms)=" + elapsedMS + ", ";
        
        return ret ;
    }
}
