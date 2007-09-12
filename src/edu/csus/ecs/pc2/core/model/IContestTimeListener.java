package edu.csus.ecs.pc2.core.model;

/**
 * Listener for all Login Events.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public interface IContestTimeListener {

    void contestTimeAdded (ContestTimeEvent event);
    
    void contestTimeRemoved (ContestTimeEvent event);
    
    void contestTimeChanged(ContestTimeEvent event);

    void contestStarted(ContestTimeEvent event);

    void contestStopped(ContestTimeEvent event);

}
