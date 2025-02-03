// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.api.implementation;

import java.util.ArrayList;
import java.util.Arrays;

import edu.csus.ecs.pc2.api.IClarification;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.ITeam;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Implementation for IClarification.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class ClarificationImplementation implements IClarification {

    private boolean answered = false;

    private String question = null;

    private String answer = null;

    private boolean deleted;

    private ITeam team = null;

    private IProblem problem = null;

    private int number;

    private int siteNumber;

    private long submissionTime;

    private boolean sendToAll;
    
    private ArrayList<ClientId> destinationTeams;
    
    private ArrayList<ElementId> destinationGroups;

    public ClarificationImplementation(Clarification clarification, IInternalContest contest, IInternalController controller) {

        answered = clarification.isAnsweredorAnnounced();
        question = new String(clarification.getQuestion());
        answer = null;
        if (clarification.getAnswer() != null) {
            answer = new String(clarification.getAnswer());
        }
        deleted = clarification.isDeleted();
        team = new TeamImplementation(clarification.getSubmitter(), contest);
        problem = new ProblemImplementation(clarification.getProblemId(), contest);
        number = clarification.getNumber();
        siteNumber = clarification.getSiteNumber();
        submissionTime = clarification.getElapsedMins();
        sendToAll = clarification.isSendToAll();
        
        if (clarification.isAnsweredorAnnounced()) {
            destinationTeams = new ArrayList<ClientId>(Arrays.asList(clarification.getAllDestinationsTeam()));
            destinationGroups = new ArrayList<ElementId>(Arrays.asList(clarification.getAllDestinationsGroup()));
        }
    }

    public String getAnswer() {
        return answer;
    }

    public boolean isAnswered() {
        return answered;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public int getNumber() {
        return number;
    }

    public IProblem getProblem() {
        return problem;
    }

    public String getQuestion() {
        return question;
    }

    public int getSiteNumber() {
        return siteNumber;
    }

    public long getSubmissionTime() {
        return submissionTime;
    }

    public ITeam getTeam() {
        return team;
    }
    
    public boolean isSendToAll() {
        return sendToAll;
    }

    @Override
    public ArrayList<ClientId> getDestinationTeams() {
        return destinationTeams;
    }

    @Override
    public ArrayList<ElementId> getDestinationGroups() {
        return destinationGroups;
    }
}
