package edu.csus.ecs.pc2.core.transport;

import java.io.Serializable;

/**
 * Call back methods from the TransportManager to the PC<sup>2</sup> client modules.
 * 
 * This provides methods for the transport to send to the pc2 server module.
 * <P>
 * Information goes up from the transport to the server module.
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9wip/trunk/src/edu/csus/ecs/pc2/core/transport/IBtoA.java$
public interface IBtoA {
    String SVN_ID = "$Id: IBtoA.java 762 2006-11-29 09:04:16Z boudreat $";

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

    void connectionDropped();

}
