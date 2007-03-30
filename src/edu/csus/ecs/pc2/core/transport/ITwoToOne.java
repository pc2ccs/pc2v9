package edu.csus.ecs.pc2.core.transport;

import java.io.Serializable;

/**
 * Call back methods from the TransportManager to the PC<sup>2 server module.
 * 
 * This provides methods to send information from the transport to the application layer (client or server).
 * <P>
 * Information goes from transport to pc2 client/server module.
 * 
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9wip/trunk/src/edu/csus/ecs/pc2/core/transport/ITwoToOne.java$
public interface ITwoToOne {
    String SVN_ID = "$Id$";

    /**
     * Server receives object from Transport Manager.
     * 
     * @param object
     *            received object.
     * @param connectionHandlerID
     *            unique identifier for connection
     */
    void receiveObject(Serializable object, ConnectionHandlerID connectionHandlerID);

    /**
     * A new connection has been established on the Transport Manager.
     * 
     * @param connectionHandlerID
     *            unique identifier for this new connection.
     */
    void connectionEstablished(ConnectionHandlerID connectionHandlerID);

    /**
     * The contact/connection to the connection has been lost.
     * 
     * @param connectionHandlerID
     *            unique identifier for connection
     */
    void connectionDropped(ConnectionHandlerID connectionHandlerID);

    /**
     * An error on a connection that needs to be communicated to the pc2 sever module.
     * 
     * @param object
     *            the object that was sent, may be null if not part of a send.
     * @param connectionHandlerID
     *            unique identifier for connection
     * @param causeDescription
     *            descriptive message about why the connection was lost
     */
    void connectionError(Serializable object, ConnectionHandlerID connectionHandlerID, String causeDescription);

}
