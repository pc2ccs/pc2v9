package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;

/**
 * A run submission.
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class SubmittedRun implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1798419856473207255L;

    public static final String SVN_ID = "$Id$";

    private int number;
    
    private ClientId clientId;

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


    public SubmittedRun(ClientId clientId, String problemName, String languageName, SerializedFile submittedFile) {
        super();
        this.clientId = clientId;
        this.problemName = problemName;
        this.languageName = languageName;
        this.submittedFile = submittedFile;
    }

    public String toString() {
        return "Run " + number + " "+clientId + " " + problemName + " " + languageName + " " + submittedFile.getName();
    }

    public ClientId getClientId() {
        return clientId;
    }

    public void setClientId(ClientId clientId) {
        this.clientId = clientId;
    }

}
