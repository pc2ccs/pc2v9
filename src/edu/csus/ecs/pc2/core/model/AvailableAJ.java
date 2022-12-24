// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.model;

import edu.csus.ecs.pc2.core.list.ProblemList;

/**
 * This class encapsulates the notion of an AutoJudge (AJ) which is currently "available" to accept and judge runs.
 * 
 * An AvailableAJ contains a {@link ClientId}
 * and a list of {@link Problem}s which the AJ client is configured to judge.
 * 
 * @author John Clevenger, PC2 Development Team
 */
public class AvailableAJ {

    private ClientId clientId ;
    private ProblemList problemList ;
    
    /**
     * Constructs an AvailableAJ with a specified clientId, and problem list.
     * 
     * @param clientId the ClientId for this AutoJudge.
     * @param probList the list of problems which this AJ is configured to judge.
     * 
     */
    public AvailableAJ (ClientId clientId, ProblemList probList) {
        this.clientId = clientId;
        this.problemList = probList;
    }    
    
    /**
     * Add a problem to the {@link ProblemList} for this AvailableAJ.
     * 
     * @param problem the {@link Problem} to be added to this AvailableAJ's list of problems which it can judge.
     */
    public void addProblem(Problem problem) {
        
        //don't try to add a null problem
        if (problem != null) {
            
            //make sure we have a problem list to which we can add problems
            if (this.problemList==null) {
                this.problemList = new ProblemList();
            }
            
            //add the specified problem to this AvailableAJ's problem list
            this.problemList.add(problem);
        }
    }
    
    /**
     * Remove a problem from the {@link ProblemList} for this AvailableAJ.
     */
    public void removeProblem (Problem problem) {
        
        //don't try to remove null problems
        if (problem != null) {
            
            //make sure we have a problem list
            if (this.problemList != null) {
                
                //remove the specified problem from the ProblemList
                problemList.delete(problem.getElementId());
            }
        }
    }

    /**
     * Get the list of problems which this AvailableAJ can judge.
     * @return the problem list for this AJ.
     */
    public ProblemList getProblemList() {
        return problemList;
    }

    /**
     * Set the list of problems which this AvailableAJ can judge.
     * Invoking this method discards any previously-specified list of problems for this AvailableAJ.
     * @param problemList the (new) list of problems which this AvailableAJ can judge.
     */
    public void setProblemList(ProblemList problemList) {
        this.problemList = problemList;
    }

    /**
     * Get the {@link ClientId} for this AvailableAJ.
     * @return the AvailableAJ's {@link ClientId}.
     */
    public ClientId getClientId() {
        return clientId;
    }

    /**
     * Returns an indication of whether this AutoJudge client is able to judge the specified problem (that is,
     * whether or not the AJ has been configured for judging the problem).
     * Note that deciding whether this AJ "can judge" a problem is unrelated to whether or not this AJ
     * is AVAILABLE for judging.
     * 
     * @param problemId the Id of the problem about which the caller is asking.
     * @return true if this AJ is configured to judge the specified problem; false if not.
     */
    public boolean canJudge(ElementId problemId) {
        
        Problem[] problist = problemList.getList();
        for (Problem prob : problist) {
            if (prob.getElementId().equals(problemId)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof AvailableAJ) {
            AvailableAJ otherAvailableAJ = (AvailableAJ) obj;
            return clientId.equals(otherAvailableAJ.getClientId());
        } else {
            throw new ClassCastException("expected a AvailableAJ found: " + obj.getClass().getName());
        }
    }
    
    public int hashCode() {
        return clientId.toString().hashCode();
    }
}
