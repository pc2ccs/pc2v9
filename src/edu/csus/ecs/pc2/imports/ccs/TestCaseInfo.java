// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.imports.ccs;

/**
 * Information about a specific test case
 * 
 * @author John Buck, PC^2 Team, pc2@ecs.csus.edu
 */
public class TestCaseInfo {
    public static final String TEST_CASE_INPUT_EXTENSION = ".in";
    public static final String TEST_CASE_ANSWER_EXTENSION = ".ans";
    
    private String inputFileName;
    private String answerFileName;
    
    public TestCaseInfo(String inFile, String ansFile) {
        inputFileName = inFile;
        answerFileName = ansFile;
    }
    
    /**
     * @return the inputFileName
     */
    public String getInputFileName() {
        return inputFileName;
    }
    /**
     * @param inputFileName the inputFileName to set
     */
    public void setInputFileName(String inputFileName) {
        this.inputFileName = inputFileName;
    }
    /**
     * @return the answerFileName
     */
    public String getAnswerFileName() {
        return answerFileName;
    }
    /**
     * @param answerFileName the answerFileName to set
     */
    public void setAnswerFileName(String answerFileName) {
        this.answerFileName = answerFileName;
    }
    
}
