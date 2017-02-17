package edu.csus.ecs.pc2.core.model;


/**
 * Listener for all Login/Logoff Events.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface ILoginListener {

    /**
     * New Login.
     * @param event the LoginEvent triggered by a new login
     */
    void loginAdded(LoginEvent event);

    /**
     * Logged off.
     * @param event the LoginEvent triggered by a log out
     */
    void loginRemoved(LoginEvent event);
    
    /**
     * Invalid login, contains message.
     * @param event the LoginEvent triggered by an invalid (denied) login
     */
    void loginDenied (LoginEvent event);
    
    /**
     * Refresh all logins.
     * 
     * @param event the LoginEvent triggered by a request to refresh all logins
     */
    void loginRefreshAll (LoginEvent event);
}
