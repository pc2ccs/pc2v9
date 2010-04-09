package edu.csus.ecs.pc2.core.model;


/**
 * Listener for all Problem Events.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IProblemListener {

    /**
     * New Problem.
     * @param event
     */
    void problemAdded(ProblemEvent event);

    /**
     * Problem information has changed.
     * @param event
     */
    void problemChanged(ProblemEvent event);

    /**
     * Problem has been removed.
     * @param event
     */
    void problemRemoved(ProblemEvent event);

    /**
     * Refresh all Problems.
     * @param problemEvent
     */
    void problemRefreshAll(ProblemEvent event);
}
