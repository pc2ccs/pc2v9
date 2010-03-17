package edu.csus.ecs.pc2.core.model;


/**
 * Listener for all Run Events.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IRunListener {

    /**
     * New Run.
     * @param event
     */
    void runAdded(RunEvent event);

    /**
     * Run state has changed.
     *
     * Run info is in the RunEvent.
     * 
     * @see RunEvent
     * @see edu.csus.ecs.pc2.core.model.RunEvent.Action
     * 
     * @param event
     */
    void runChanged(RunEvent event);

    /**
     * Run has been removed.
     * @param event
     */
    void runRemoved(RunEvent event);
}
