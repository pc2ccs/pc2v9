package edu.csus.ecs.pc2.core.transport.connection;

import java.io.Serializable;

import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * Event from a ConnectionManager. 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ConnectionManagerEvent {

    /**
     * Actions for a Connection Event. 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    public enum Action {

        /**
         * Successful initial connection.
         * 
         * First time connected to another module.
         */
        CONNECTED,

        /**
         * A re-connection attempt timed out.
         * 
         * A connection was dropped, retried and failed to reconnect within a time limit.
         */
        TIMED_OUT,

        /**
         * A connection was dropped.
         * 
         * A connection was dropped, and no retries or time out was configured.
         */
        DISCONNECTED,

        /**
         * A Connection Attempt failed.
         * 
         * A initial connection attempt to another module failed.
         */
        FAILED,

        /**
         * A object was received.
         */
        RECIEVED_OBJECT,

    };

    /**
     * Action for this event.
     */
    private Action action;

    /**
     * ConnectionHandlerId for this event
     */
    private ConnectionHandlerID connectionHandlerID;

    /**
     * An object received.
     */
    private Serializable serializableObject;

    public ConnectionManagerEvent(Action action, ConnectionHandlerID connectionHandlerID, Serializable serializableObject) {
        super();
        this.action = action;
        this.connectionHandlerID = connectionHandlerID;
        this.serializableObject = serializableObject;
    }

    public ConnectionManagerEvent(Action action, ConnectionHandlerID connectionHandlerID) {
        super();
        this.action = action;
        this.connectionHandlerID = connectionHandlerID;
    }

    public Action getAction() {
        return action;
    }

    public ConnectionHandlerID getConnectionHandlerID() {
        return connectionHandlerID;
    }

    public Serializable getSerializableObject() {
        return serializableObject;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setConnectionHandlerID(ConnectionHandlerID connectionHandlerID) {
        this.connectionHandlerID = connectionHandlerID;
    }

    public void setSerializableObject(Serializable serializableObject) {
        this.serializableObject = serializableObject;
    }

}
