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
    void loginAdded(LoginEvent event);

    /**
     * Run has been removed.
     * @param event
     */
    void loginRemoved(LoginEvent event);
}
