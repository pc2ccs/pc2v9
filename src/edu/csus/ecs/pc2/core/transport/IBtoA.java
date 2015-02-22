package edu.csus.ecs.pc2.core.transport;

import java.io.Serializable;

/**
 * Call back methods from the TransportManager to the PC<sup>2</sup> client modules.
 * 
 * This provides methods for the transport to any client/server logged into a server.
 * <P>
 * Information goes up from the transport to the server module (from B to A
 * on a long ago lost diagram).
 * 
 * @version $Id$
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9wip/trunk/src/edu/csus/ecs/pc2/core/transport/IBtoA.java$
public interface IBtoA {

    /**
     * Server receives object from Transport Manager.
     * 
     * @param object
     *            received object.
     */
    void receiveObject(Serializable object);

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

    /**
     * This connection was terminated by a server.
     */
    void connectionDropped();

}
