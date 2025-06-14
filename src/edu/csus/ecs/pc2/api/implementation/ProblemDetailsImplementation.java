// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.api.implementation;

import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.IProblemDetails;

/**
 * API IClient implementation.
 *
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProblemDetailsImplementation implements IProblemDetails {

    private IClient client;

    private IProblem problem;

    private int problemId;

    private boolean solved;

    private long solutionTime;

    private long penaltyPoints;

    private long attempts;

    private boolean fts;

    public ProblemDetailsImplementation(IClient client) {
        super();
        this.client = client;
    }

    @Override
    public long getAttempts() {
        return attempts;
    }

    @Override
    public IClient getClient() {
        return client;
    }

    @Override
    public long getPenaltyPoints() {
        return penaltyPoints;
    }

    @Override
    public IProblem getProblem() {
        return problem;
    }

    @Override
    public int getProblemId() {
        return problemId;
    }

    @Override
    public long getSolutionTime() {
        return solutionTime;
    }

    public void setClient(IClient client) {
        this.client = client;
    }

    public void setProblem(IProblem problem) {
        this.problem = problem;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public void setSolutionTime(long solutionTime) {
        this.solutionTime = solutionTime;
    }

    public void setPenaltyPoints(long penaltyPoints) {
        this.penaltyPoints = penaltyPoints;
    }

    public void setAttempts(long attempts) {
        this.attempts = attempts;
    }

    @Override
    public boolean isSolved() {
        return solved;
    }

    public void setFts(boolean fts) {
        this.fts = fts;
    }

    @Override
    public boolean isFts() {
        return fts;
    }
}
