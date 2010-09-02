package edu.csus.ecs.pc2.api.implementation;

import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.api.IProblemDetails;
import edu.csus.ecs.pc2.api.IStanding;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class StandingImplementation implements IStanding {

    private IClient client;

    private int numProblemsSolved;

    private int penaltyPoint;

    private int rank;

    private IProblemDetails[] problemDetails = new ProblemDetailsImplementation[0];

    public StandingImplementation(IInternalContest contest, ClientId clientId, int rank, int numProblemsSolved, int penaltyPoint) {
        super();
        this.client = new ClientImplementation(clientId, contest);
        this.numProblemsSolved = numProblemsSolved;
        this.penaltyPoint = penaltyPoint;
        this.rank = rank;
    }

    public IClient getClient() {
        return client;
    }

    public int getNumProblemsSolved() {
        return numProblemsSolved;
    }

    public int getPenaltyPoints() {
        return penaltyPoint;
    }

    public int getRank() {
        return rank;
    }

    public IProblemDetails[] getProblemDetails() {
        return problemDetails;
    }

    public void setProblemDetails(IProblemDetails[] details) {
        this.problemDetails = details;
    }

}
