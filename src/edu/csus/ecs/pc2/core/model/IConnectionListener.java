package edu.csus.ecs.pc2.core.model;


/**
 * Listener for {@link edu.csus.ecs.pc2.core.transport.ConnectionHandlerID}s.
 * 
 * This a listener for when a server is connected to a client, when
 * a user is logged in then the {@link edu.csus.ecs.pc2.core.model.ILoginListener}
 * method will be invoked.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
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

    /**
     * Refresh all connections on profile switch or reset.
     * 
     * @param connectionEvent
     */
    void connectionRefreshAll (ConnectionEvent connectionEvent);

}
