// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
/**
 * 
 */
package edu.csus.ecs.pc2.ui;

/**
 * @author ICPC
 *
 */
public class TestResultsRowData {
    private String resultString; 
    private String time; 
    private String teamOutputViewLabel;
    private String teamOutputCompareLabel;
    private String teamStderrViewLabel; 
    private String judgesOutputViewLabel; 
    private String judgesDataViewLabel;
    private String validatorOutputViewLabel; 
    private String validatorStderrViewLabel;
    /**
     * @param resultString
     * @param time
     * @param teamOutputViewLabel
     * @param teamOutputCompareLabel
     * @param teamStderrViewLabel
     * @param judgesOutputViewLabel
     * @param judgesDataViewLabel
     * @param validatorOutputViewLabel
     * @param validatorStderrViewLabel
     */
    public TestResultsRowData(String resultString, String time, String teamOutputViewLabel, String teamOutputCompareLabel, String teamStderrViewLabel, String judgesOutputViewLabel, String judgesDataViewLabel,
            String validatorOutputViewLabel, String validatorStderrViewLabel) {
        super();
        this.resultString = resultString;
        this.time = time;
        this.teamOutputViewLabel = teamOutputViewLabel;
        this.teamOutputCompareLabel = teamOutputCompareLabel;
        this.teamStderrViewLabel = teamStderrViewLabel;
        this.judgesOutputViewLabel = judgesOutputViewLabel;
        this.judgesDataViewLabel = judgesDataViewLabel;
        this.validatorOutputViewLabel = validatorOutputViewLabel;
        this.validatorStderrViewLabel = validatorStderrViewLabel;
    }

    /**
     * @return the resultString
     */
    public String getResultString() {
        return resultString;
    }
    /**
     * @return the time
     */
    public String getTime() {
        return time;
    }
    /**
     * @return the judgesDataViewLable
     */
    public String getJudgesDataViewLabel() {
        return judgesDataViewLabel;
    }
    /**
     * @return the judgesOutputViewLable
     */
    public String getJudgesOutputViewLabel() {
        return judgesOutputViewLabel;
    }
    /**
     * @return the teamOutputViewLabel
     */
    public String getTeamOutputViewLabel() {
        return teamOutputViewLabel;
    }
    /**
     * @return the teamOutputCompareLabel
     */
    public String getTeamsOutputCompareLabel() {
        return teamOutputCompareLabel;
    }
    /**
     * @return the teamOutputViewLabel
     */
    public String getTeamStderrViewLabel() {
        return teamStderrViewLabel;
    }
    /**
     * @return the judgesDataViewLabel
     */
    public String getValidatorOutputViewLabel() {
        return validatorOutputViewLabel;
    }
    /**
     * @return the validatorStderrViewLabel
     */
    public String getValidatorStderrViewLabel() {
        return validatorStderrViewLabel;
    }
}
