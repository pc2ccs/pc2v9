package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;

/**
 * The files that were submitted with a Run.
 * 
 * @see Run
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class RunFiles implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7676377417419464772L;
    
    Submission submission = null;

    /**
     * Unique id for this instance.
     */
    private ElementId elementId = new ElementId("RunFiles");

    /**
     * The runId for the Run associated with these files.
     */
    private ElementId runId;

    /**
     * The main file that was submitted.
     */
    private SerializedFile mainFile;

    /**
     * 
     */
    private SerializedFile[] otherFiles;

    /**
     * @param run
     * @param filename
     */
    public RunFiles(Run run, String filename) {
        super();
        this.runId = run.getElementId();
        this.mainFile = new SerializedFile(filename);
        this.submission = run;
    }

    /**
     * @param run
     * @param mainFile
     * @param otherFiles
     */
    public RunFiles(Run run, SerializedFile mainFile, SerializedFile[] otherFiles) {
        super();
        this.runId = run.getElementId();
        this.mainFile = mainFile;
        this.otherFiles = otherFiles;
        this.submission = run;
    }

    /**
     * Unique identifier for this class.
     * 
     * @return Returns the elementId.
     */
    public ElementId getElementId() {
        return elementId;
    }

    /**
     * @return Returns the mainFile.
     */
    public SerializedFile getMainFile() {
        return mainFile;
    }

    /**
     * @return Returns the otherFiles.
     */
    public SerializedFile[] getOtherFiles() {
        return otherFiles;
    }

    /**
     * @return Returns the runId.
     */
    public ElementId getRunId() {
        return runId;
    }
    
    public void setSubmission(Submission submission) {
        this.submission = submission;
    }
    
    public Submission getSubmission() {
        return submission;
    }

}
