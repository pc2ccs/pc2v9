package edu.csus.ecs.pc2.core;


/**
 * Specifies methods used to manipulate contest data.
 * 
 * @see edu.csus.ecs.pc2.Starter
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public interface IModel {

    void addRunListener(RunListener runListener);

    void removeRunListener(RunListener runListener);

    /**
     * Add Run into TeamModel's data or receive run from Server.
     * 
     * @param submittedRun
     */
    void addRun(SubmittedRun submittedRun);
    
    /**
     * Add a run into the contest data, return updated Submitted Run.
     * 
     * @param submittedRun
     * @return
     */
    SubmittedRun acceptRun(SubmittedRun submittedRun) throws Exception;

}
