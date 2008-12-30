package edu.csus.ecs.pc2.api.listener;

/**
 * This interface describes the set of methods when a client is disconnected.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IConnectionEventListener {

    /**
     * Connection Dropped.
     * 
     * @param connectionEvent
     */
    void connectionDropped(ConnectionEvent connectionEvent);

}
