package edu.csus.ecs.pc2.core.model;

import java.util.Date;

/**
 * A balloon delivery notification.
 * 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class Notification implements IElementObject, IGetDate {

    /**
     * 
     */
    private static final long serialVersionUID = 945222497478556335L;

    /**
     * Time notification sent to printer/email.
     */
    private long timeSent;

    private int siteNumber;
    
    private long time = new Date().getTime();

    /**
     * Number assigned to notification.
     */
    private int number;
    
    /**
     * Client/user who sent the notification (ex. board1);
     */
    private ClientId senderClientId;

    /**
     * User who submitted run who will get this notification (ex. team4).
     */
    private ClientId submitter;

    /**
     * Problem for this notification.
     */
    private ElementId problemId;

    private ElementId elementId;

    /**
     * User who confirmed delivery of balloon/email to team, (ex. board1).
     */
    private ClientId whoDelivered;

    /**
     * When the delivery was confirmed by {@link #whoDelivered}.
     */
    private int whenDelivered;

    private long elapsedMS;

    private boolean deleted = false;
   

    /**
     * Create Notification.
     * 
     * @param run
     *            the run for this notification
     * @param whoSent
     *            which pc2 module/login sent notification
     * @param timeSent
     *            when notification was sent (wall clock)
     * @param elapsedMS
     *            contest elapsed time when sent.
     */
    public Notification(Run run, ClientId whoSent, long timeSent, long elapsedMS) {
        super();
        submitter = run.getSubmitter();
        problemId = run.getProblemId();
        senderClientId = whoSent;
        this.timeSent = timeSent;
        this.elapsedMS = elapsedMS;
        elementId = new ElementId("Notif-" + getKey());
    }

    public ElementId getElementId() {
        return elementId;
    }

    public int versionNumber() {
        return elementId.getVersionNumber();
    }

    public int getSiteNumber() {
        return siteNumber;
    }

    public void setSiteNumber(int siteNumber) {
        this.siteNumber = siteNumber;
    }

    public String getKey() {
        return submitter.getTripletKey() + " " + problemId.toString();
    }

    public Date getSendDate() {
        return new Date(timeSent);
    }

    public void updateDeliveryConfirmation(ClientId id, int timeDelivered) {
        whoDelivered = id;
        whenDelivered = timeDelivered;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public long getTimeSent() {
        return timeSent;
    }

    public ElementId getProblemId() {
        return problemId;
    }

    public ClientId getWhoDelivered() {
        return whoDelivered;
    }

    public int getWhenDelivered() {
        return whenDelivered;
    }

    public long getElapsedMS() {
        return elapsedMS;
    }

    public ClientId getSubmitter() {
        return submitter;
    }

    public ClientId getSenderClientId() {
        return senderClientId;
    }
    
    public void setElapsedMS(long elapsedMS) {
        this.elapsedMS = elapsedMS;
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
