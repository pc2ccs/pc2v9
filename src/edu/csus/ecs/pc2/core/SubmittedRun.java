package edu.csus.ecs.pc2.core;

/**
 * A run submission.
 * 
 * @author Douglas A. Lane
 * 
 */

// $HeadURL$
public class SubmittedRun {

    public static final String SVN_ID = "$Id$";

    private int number;

    private int teamNumber;

    private String problemName;

    private String languageName;

    private SerializedFile submittedFile;

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getProblemName() {
        return problemName;
    }

    public void setProblemName(String problemName) {
        this.problemName = problemName;
    }

    public SerializedFile getSubmittedFile() {
        return submittedFile;
    }

    public void setSubmittedFile(SerializedFile submittedFile) {
        this.submittedFile = submittedFile;
    }

    public int getTeamNumber() {
        return teamNumber;
    }

    public void setTeamNumber(int teamNumber) {
        this.teamNumber = teamNumber;
    }

    public SubmittedRun(int teamNumber, String problemName, String languageName, SerializedFile submittedFile) {
        super();
        // TODO Auto-generated constructor stub
        this.teamNumber = teamNumber;
        this.problemName = problemName;
        this.languageName = languageName;
        this.submittedFile = submittedFile;
    }

    public String toString() {
        return "Run " + number + " team" + teamNumber + " " + problemName + " " + languageName + " " + submittedFile.getName();
    }

}
