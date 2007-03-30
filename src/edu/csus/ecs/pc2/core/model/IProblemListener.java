package edu.csus.ecs.pc2.core.model;


/**
 * Listener for all Problem Events.
 * 
 * @author pc2@ecs.csus.edu
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
     * Run has been removed.
     * @param event
     */
    void problemRemoved(ProblemEvent event);
}
