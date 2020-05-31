// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.scoring;

import java.io.Serializable;

import edu.csus.ecs.pc2.core.model.ElementId;

/**
 * Problem (Scoring) Summary Info.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProblemSummaryInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4157597862536104668L;

    private long solutionTime = 0;

    private int numberSubmitted = 0;

    private boolean solved = false;

    private int penaltyPoints = 0;

    private boolean unJudgedRuns = false;

    private int pendingRunCount = 0;

    private int judgedRunCount = 0;

    private ElementId problemId = null;

    /**
     * @return Returns the problemId.
     */
    public ElementId getProblemId() {
        return problemId;
    }

    /**
     * @param problemId
     *            The problemId to set.
     */
    public void setProblemId(edu.csus.ecs.pc2.core.model.ElementId problemId) {
        this.problemId = problemId;
    }

    /**
     * @return Returns the penaltyPoints.
     */
    public int getPenaltyPoints() {
        return penaltyPoints;
    }

    /**
     * @param penaltyPoints
     *            The penaltyPoints to set.
     */
    public void setPenaltyPoints(int penaltyPoints) {
        this.penaltyPoints = penaltyPoints;
    }

    /**
     * @return Returns the numberSubmitted.
     */
    public int getNumberSubmitted() {
        return numberSubmitted;
    }

    /**
     * @param numberSubmitted
     *            The numberSubmitted to set.
     */
    public void setNumberSubmitted(int numberSubmitted) {
        this.numberSubmitted = numberSubmitted;
    }

    /**
     * @return Returns the solutionTime.
     */
    public long getSolutionTime() {
        return solutionTime;
    }

    /**
     * @param solutionTime
     *            The solutionTime to set.
     */
    public void setSolutionTime(long solutionTime) {
        this.solutionTime = solutionTime;
    }

    /**
     * @return Returns the solved.
     */
    public boolean isSolved() {
        return solved;
    }

    /**
     * @param solved
     *            The solved to set.
     */
    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public boolean isUnJudgedRuns() {
        return unJudgedRuns;
    }

    public void setUnJudgedRuns(boolean unJudgedRuns) {
        this.unJudgedRuns = unJudgedRuns;
    }

    public void setPendingRunCount(int pendingRunCount) {
        this.pendingRunCount = pendingRunCount;
    }

    public void setJudgedRunCount(int judgedRunCount) {
        this.judgedRunCount = judgedRunCount;
    }

    public int getPendingRunCount() {
        return pendingRunCount;
    }

    public int getJudgedRunCount() {
        return judgedRunCount;
    }

}
