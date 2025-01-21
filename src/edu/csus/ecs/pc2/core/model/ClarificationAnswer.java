// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;
import java.util.Date;

/**
 * An answer for a clarification, or a announcement "answer".
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class ClarificationAnswer implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 6355584858957926890L;

    private boolean sendToAll = false;

    /**
     * The client (usually a judge) who sent this clarification answer or announcement.
     */
    private ClientId answerClient;

    private Date date = new Date();

    private long elapsedMS;

    private String answer;

    private ElementId elementId;
    
    /**
     * A clarification could be send to multiple groups and/or a specific list of teams. 
     * Used when it is not sendToAll but "send-to-some".
     */
    private ElementId[] destinationGroups = new ElementId[0]; //groups to which the answer is to be sent
    
    private ClientId[] destinationTeams = new ClientId[0];  //specific teams to which the answer is to be sent

    public ClarificationAnswer(String answer, ClientId answerClient, boolean sendToAll, ElementId[] destinationGroups, ClientId[] destinationTeams, ContestTime contestTime) {
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
        this.destinationGroups = destinationGroups;
        this.destinationTeams = destinationTeams;
        setDate(contestTime);
    }

    public ClarificationAnswer(String answer, ClientId answerClient, boolean sendToAll, ContestTime contestTime) {
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
        return destinationGroups;
    }
    
    public void setAllDestinationsTeam(ClientId[] allDestinationsTeam) {
        this.destinationTeams = allDestinationsTeam;
    }
    
    public ClientId[] getAllDestinationsTeam() {
        return destinationTeams;
    }
    
    public void setAllDestinationsGroup(ElementId[] allDestinationsGroup) {
        this.destinationGroups = allDestinationsGroup;
    }
    
    public boolean isThereDestinationOtherThanSubmitter() {
        if (this.destinationGroups == null || this.destinationGroups == null) {
            return false;
        }
        return this.destinationGroups.length + this.destinationTeams.length != 0;
    }
    
}
