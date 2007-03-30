package edu.csus.ecs.pc2.core.model;


/**
 * Listener for all Login Events.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public interface ILoginListener {

    /**
     * New Login.
     * @param event
     */
    void problemAdded(LoginEvent event);

    /**
     * Login information has changed.
     * @param event
     */
    void problemChanged(LoginEvent event);

    /**
     * Run has been removed.
     * @param event
     */
    void problemRemoved(LoginEvent event);
}
