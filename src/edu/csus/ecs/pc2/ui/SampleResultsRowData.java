// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
/**
 *
 */
package edu.csus.ecs.pc2.ui;

/**
 * @author John Buck
 */
public class SampleResultsRowData {
    private String resultString;
    private String time;
    private String judgesOutputViewLabel;
    private String judgesDataViewLabel;
    /**
     * @param resultString
     * @param time
     * @param judgesOutputViewLabel
     * @param judgesDataViewLabel
     */
    public SampleResultsRowData(String resultString, String time, String judgesOutputViewLabel, String judgesDataViewLabel) {
        super();
        this.resultString = resultString;
        this.time = time;
        this.judgesOutputViewLabel = judgesOutputViewLabel;
        this.judgesDataViewLabel = judgesDataViewLabel;
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

}
