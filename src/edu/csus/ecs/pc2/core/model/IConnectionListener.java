package edu.csus.ecs.pc2.core.model;


/**
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public interface IConnectionListener {

    /**
     * Connection Established.
     * @param connectionEvent
     */
    void connectionEstablished(ConnectionEvent connectionEvent);

    /**
     * Connection Dropped
     * @param connectionEvent
     */
    void connectionDropped(ConnectionEvent connectionEvent);

}
