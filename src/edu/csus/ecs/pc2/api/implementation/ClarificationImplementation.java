package edu.csus.ecs.pc2.api.implementation;

import edu.csus.ecs.pc2.api.IClarification;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.ITeam;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Implementation for IClarification.
 * 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
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

    public ClarificationImplementation(Clarification clarification, IInternalContest contest, IInternalController controller) {

        answered = clarification.isAnswered();
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

}
