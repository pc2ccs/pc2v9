package edu.csus.ecs.pc2.core.transport;

import java.io.Serializable;

/**
 * Call back methods from the TransportManager to the PC<sup>2 server module.
 * 
 * This provides methods to send information from the transport to the Server application layer.
 * <P>
 * Information goes from transport to pc2 server module (From 2 (transport) to 1 (server) on a
 * long ago lost diagram).
 * 
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9wip/trunk/src/edu/csus/ecs/pc2/core/transport/ITwoToOne.java$
public interface ITwoToOne {

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
     * A new connection from a client (or another server) to this server.
     * 
     * @param connectionHandlerID
     *            unique identifier for this new connection.
     */
    void connectionEstablished(ConnectionHandlerID connectionHandlerID);

    /**
     * The contact/connection to the server has been lost.
     * 
     * @param connectionHandlerID
     *            unique identifier for connection
     */
    void connectionDropped(ConnectionHandlerID connectionHandlerID);

    /**
     * An error on a connection that needs to be communicated to the pc2 server module.
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
