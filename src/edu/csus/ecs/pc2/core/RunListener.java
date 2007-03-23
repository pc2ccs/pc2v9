package edu.csus.ecs.pc2.core;


/**
 * Listener for all Run Events.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public interface RunListener {
    
    /**
     * New Run.
     * @param event
     */
    void runAdded(RunEvent event);

    /**
     * Run state has changed.
     * 
     * The chage info is in the {@link edu.csus.ecs.pc2.core.RunEvent.Action} of
     * {@link RunEvent}.
     * 
     * @see RunEvent
     * @see edu.csus.ecs.pc2.core.RunEvent.Action
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
