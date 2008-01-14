package edu.csus.ecs.pc2.core.transport.connection;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IConnectionManagerListener {
    
    /**
     * Connection Established.
     * @param connectionEvent
     */
    void connectionEstablished(ConnectionManagerEvent connectionManagerEvent);

    /**
     * Connection Dropped
     * @param connectionEvent
     */
    void connectionDropped(ConnectionManagerEvent connectionManagerEvent);

    void connectionFailed(ConnectionManagerEvent connectionManagerEvent);

    void receivedObject(ConnectionManagerEvent connectionManagerEvent);

    void connectionTimedOut(ConnectionManagerEvent connectionManagerEvent);

    void connectionChanged(ConnectionManagerEvent connectionManagerEvent);

}
