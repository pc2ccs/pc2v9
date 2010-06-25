package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;

/**
 * A submission from the user.
 * 
 * This is a parent class to submissions like, {@link edu.csus.ecs.pc2.core.model.Run} and
 * {@link edu.csus.ecs.pc2.core.model.Clarification}.
 * 
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class ISubmission implements Serializable, IElementObject {

    /**
     * 
     */
    private static final long serialVersionUID = 7787390513757832467L;

    public static final String SVN_ID = "$Id$";

    /**
     * Who submitted this submission.
     */
    private ClientId submitter = null;

    /**
     * a id to uniquely identify this submission.
     */
    private ElementId elementId;

    /**
     * language {@link ElementId}
     */
    private ElementId languageId = null;

    /**
     * problem {@link ElementId}
     */
    private ElementId problemId = null;

    /**
     * The run number, clar number. <br>
     * The sequence number assigned to a submission by the server as the submission is added to the server database.
     * 
     */
    private int number = 0;

    /**
     * The minute when the run was submitted.
     * 
     * This field is set by the server when the submission is added on the server.
     */
    private long elapsedMins;

    /**
     * @return Returns the elementId.
     */
    public final ElementId getElementId() {
        return elementId;
    }

    /**
     * @return Returns the languageId.
     */
    public final ElementId getLanguageId() {
        return languageId;
    }

    /**
     * @return Returns the problemId.
     */
    public ElementId getProblemId() {
        return problemId;
    }

    /**
     * @return Returns the submitter.
     */
    public ClientId getSubmitter() {
        return submitter;
    }

    /**
     * @return Returns the number.
     */
    public int getNumber() {
        return number;
    }

    /**
     * @param number
     *            The number to set.
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * @return Returns the elapsedMins.
     */
    public long getElapsedMins() {
        return elapsedMins;
    }

    /**
     * @param elapsedMins
     *            The elapsedMins to set.
     */
    public void setElapsedMins(long elapsedMins) {
        this.elapsedMins = elapsedMins;
    }

    public int versionNumber() {
        return elementId.getVersionNumber();
    }

    public int getSiteNumber() {
        return elementId.getSiteNumber();
    }

    public final void setSiteNumber(int siteNumber) {
        elementId.setSiteNumber(siteNumber);
    }

    public void setSubmitter(ClientId submitter) {
        this.submitter = submitter;
    }

    public void setProblemId(ElementId problemId) {
        this.problemId = problemId;
    }

    public void setLanguageId(ElementId languageId) {
        this.languageId = languageId;
    }

    public void setElementId(ElementId elementId) {
        this.elementId = elementId;
    }

}
