package edu.csus.ecs.pc2.core.model;

import edu.csus.ecs.pc2.core.StringUtilities;

/**
 * A class defining a set (pair) of file names for a single test case for a Problem.
 * An instance of the class contains two file names:  the name of the data file
 * for the test case, and the name of the answer file for the test case.  One or
 * both of these may be null, although typically at least one is non-null.
 * Each instance of the class also contains a "test case number" associated with
 * the test set.  All fields are mutable using accessors.
 * 
 * @author John
 *
 */
public class TestCaseFileNameSet {
    
    /**
     * The names of the data file (if any) and answer file (if any) for this Test Case.
     */
    private String dataFileName = null;
    private String answerFileName = null;
    
    /**
     * The test case number associated with this test case file name set.
     */
    private int testCaseNum ;
    
    
    /**
     * Returns the name of the input data file associated with this test case.
     * Note that since not all Problems require an input data file, the returned
     * file name could be null. If the file name is not null, a clone of the file name String is returned.
     * @return A String containing the name of the input data file for this test case, or null
     */
    public String getDataFileName() {
        return StringUtilities.cloneString(this.dataFileName);
    }
    
    /**
     * Returns the name of the answer file associated with this test case.
     * Note that since not all Problems require an answer file, the returned
     * file name could be null. If the file name is not null, a clone of the file name String is returned.
     * @return A String containing the name of the answer file for this test case, or null
     */
    public String getAnswerFileName() {
        return StringUtilities.cloneString(this.answerFileName);
    }
    
    /**
     * Returns the test case number associated with this test case.
     */
    public int getTestCaseNum() {
        return this.testCaseNum;
    }
    
    /**
     * Saves the specified name as the new name of the data file for this Test Case.
     * @param newName The new name of the Test Case data file 
     */
    public void setDataFileName(String newName) {
        this.dataFileName = StringUtilities.cloneString(newName);
    }
    
    /**
     * Saves the specified name as the new name of the answer file for this Test Case.
     * @param newName The new name of the Test Case answer file 
     */
    public void setAnswerFileName(String newName) {
        this.answerFileName = StringUtilities.cloneString(newName);
    }
    
    /**
     * Updates the test case number associated with this test case.
     */
    public void setTestCaseNum(int newNum) {
        this.testCaseNum = newNum;
    }
    
    /**
     * Returns a deep clone of this TestCaseFileNameSet.
     */
    @Override
    public Object clone() {
        TestCaseFileNameSet copy = new TestCaseFileNameSet();
        copy.setDataFileName(this.getDataFileName());
        copy.setAnswerFileName(this.getAnswerFileName());
        copy.testCaseNum = this.getTestCaseNum();
        return copy;
    }
}
