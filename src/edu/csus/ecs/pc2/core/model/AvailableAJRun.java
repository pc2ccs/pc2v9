// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.model;

/**
 * This class encapsulates the notion of a {@link Run} which is currently waiting to be judged by an AutoJudge.
 * 
 * An AvailableAJRun contains an {@link ElementId} which is its "run ID", a time field which is the elapsed 
 * contest time at which the run was submitted, and an {@link ElementId} which is the Id of the {@link Problem}
 * for which the run was submitted.
 * 
 * @author John Clevenger, PC2 Development Team
 *
 */
public class AvailableAJRun {

    private ElementId runId ;
    private long contestSubmissionTimeMsec ;
    private ElementId problemId ;
    
    /**
     * Constructs an AvailableAJRun with a specified runId, submission time, and problem Id.
     * 
     * @param runId the id of the AvailableAJRun.
     * @param contestSubmitTimeMsec the contest elapsed time of the submission, in milliseconds.
     * @param probId the id of the problem for which the run was submitted.
     */
    
    public AvailableAJRun (ElementId runId, long contestSubmissionTimeMsec, ElementId problemId) {
        this.runId = runId;
        this.contestSubmissionTimeMsec = contestSubmissionTimeMsec;
        this.problemId = problemId;
    }

    public ElementId getRunId() {
        return runId;
    }

    public long getContestSubmissionTimeMsec() {
        return contestSubmissionTimeMsec;
    }

    public ElementId getProblemId() {
        return problemId;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof AvailableAJRun) {
            AvailableAJRun otherAvailableAJRun = (AvailableAJRun) obj;
            return runId.equals(otherAvailableAJRun.getRunId());
        } else {
            throw new ClassCastException("expected a AvailableAJRun found: " + obj.getClass().getName());
        }
    }
    
    public int hashCode() {
        return runId.toString().hashCode();
    }
}