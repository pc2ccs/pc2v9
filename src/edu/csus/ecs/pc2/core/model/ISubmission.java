package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;
import java.util.Date;

/**
 * A submission from the user.
 * 
 * This is a parent class to submissions like, {@link edu.csus.ecs.pc2.core.model.Run} and
 * {@link edu.csus.ecs.pc2.core.model.Clarification}.
 * 
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

// TODO SOJMEDAY rename this class to Submission

// $HeadURL$
public class ISubmission implements Serializable, IElementObject {

    /**
     * 
     */
    private static final long serialVersionUID = 7787390513757832467L;

    private long time = new Date().getTime();


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
    
    private Date createDate = new Date();

    /**
     * The run number, clar number. <br>
     * The sequence number assigned to a submission by the server as the submission is added to the server database.
     * 
     */
    private int number = 0;
    
    /**
     * Sequence number from originating playback.
     * Base one number.
     */
    private int playbackSequenceNumber = 0;
    
    /**
     * Identifier for which playback settings are used.
     */
    private char [] playbackId = new char[0]; 

    /**
     * The millisecond when the submissions was submitted.
     * 
     * This field is set by the server when the submission is added on the server.
     */
    private long elapsedMS;

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
        return elapsedMS / 60000;
    }

    /**
     * @param elapsedMins
     *            The elapsedMins to set.
     */
    public void setElapsedMins(long elapsedMins) {
        setElapsedMS(elapsedMins * 60000);
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

    public long getElapsedMS() {
        return elapsedMS;
    }

    public void setElapsedMS(long elapsedMS) {
        this.elapsedMS = elapsedMS;
    }
    
    public Date getCreateDate() {
        return createDate;
    }

    public int getPlaybackSequenceNumber() {
        return playbackSequenceNumber;
    }

    public void setPlaybackSequenceNumber(int playbackSequenceNumber) {
        this.playbackSequenceNumber = playbackSequenceNumber;
    }

    public char[] getPlaybackId() {
        return playbackId;
    }

    public void setPlaybackId(char[] playbackId) {
        this.playbackId = playbackId;
    }

    /**
     * Get wall clock time for submission.
     *      * @return
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
    
}
