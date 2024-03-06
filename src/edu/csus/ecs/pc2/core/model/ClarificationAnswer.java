// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;
import java.util.Date;

/**
 * A answer for a clarification.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ClarificationAnswer implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 6355584858957926890L;

    private boolean sendToAll = false;

    /**
     * The client (usually a judge) who sent this clar.
     */
    private ClientId answerClient;

    private Date date = new Date();

    private long elapsedMS;

    private String answer;

    private ElementId elementId;
    
    /**
     * An clarification could be send to multiple groups, teams. Used when it is not sendToAll but some.
     */
    private ElementId[] allDestinationsGroup;

    public ClarificationAnswer(String answer, ClientId answerClient, boolean sendToAll, ElementId[] allDestinationsGroup, ContestTime contestTime) {
        super();

        if (answer == null) {
            throw new IllegalArgumentException("answer can not be null");
        }

        if (contestTime == null) {
            throw new IllegalArgumentException("contestTime can not be null");
        }

        setElementId(new ElementId("reply"));
        this.answer = answer;
        this.answerClient = answerClient;
        this.sendToAll = sendToAll;
        this.allDestinationsGroup = allDestinationsGroup;
        setDate(contestTime);
    }

    /**
     * Set date/elapsed time for this submission.
     * 
     * @param contest
     */
    protected void setDate(ContestTime contestTime) {
        date = new Date();
        elapsedMS = contestTime.getElapsedMS();
    }

    public boolean isSendToAll() {
        return sendToAll;
    }

    public ClientId getAnswerClient() {
        return answerClient;
    }

    public Date getDate() {
        return date;
    }

    public long getElapsedMS() {
        return elapsedMS;
    }

    public String getAnswer() {
        return answer;
    }

    /**
     * @return the elementId
     */
    public ElementId getElementId() {
        return elementId;
    }

    /**
     * @param elementId the elementId to set
     */
    public void setElementId(ElementId elementId) {
        this.elementId = elementId;
    }
    
    public ElementId[] getAllDestinationsGroup() {
        return allDestinationsGroup;
    }
    
    public void setAllDestinationsGroup(ElementId[] allDestinationsGroup) {
        this.allDestinationsGroup = allDestinationsGroup;
    }
}
