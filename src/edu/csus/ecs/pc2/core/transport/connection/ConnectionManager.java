package edu.csus.ecs.pc2.core.transport.connection;

import java.io.Serializable;
import java.util.Vector;

import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * Manages Connections.
 * 
 * Class to manage initiation, dropping and handling of various transport events.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ConnectionManager {

    private Vector<IConnectionManagerListener> connectionListenerList = new Vector<IConnectionManagerListener>();

    private void fireConnectionListener(ConnectionManagerEvent connectionManagerEvent) {
        for (int i = 0; i < connectionListenerList.size(); i++) {

            switch (connectionManagerEvent.getAction()) {
                case CONNECTED:
                    connectionListenerList.elementAt(i).connectionEstablished(connectionManagerEvent);
                    break;
                case DISCONNECTED:
                    connectionListenerList.elementAt(i).connectionDropped(connectionManagerEvent);
                    break;
                case RECIEVED_OBJECT:
                    connectionListenerList.elementAt(i).receivedObject(connectionManagerEvent);
                    break;
                case FAILED:
                    connectionListenerList.elementAt(i).connectionFailed(connectionManagerEvent);
                    break;
                case TIMED_OUT:
                    connectionListenerList.elementAt(i).connectionTimedOut(connectionManagerEvent);
                    break;
                default:
                    connectionListenerList.elementAt(i).connectionChanged(connectionManagerEvent);
                    break;
            }
        }
    }

    public void addConnectionListener(IConnectionManagerListener listener) {
        connectionListenerList.addElement(listener);
    }

    public void removeConnectionListener(IConnectionManagerListener listener) {
        connectionListenerList.removeElement(listener);
    }

    public void establishConnection(ConnectionHandlerID connectionHandlerID) {
        ConnectionManagerEvent connectionManagerEvent = new ConnectionManagerEvent(ConnectionManagerEvent.Action.CONNECTED, connectionHandlerID);
        fireConnectionListener(connectionManagerEvent);
    }

    public void connectionDropped(ConnectionHandlerID connectionHandlerID) {
        ConnectionManagerEvent connectionManagerEvent = new ConnectionManagerEvent(ConnectionManagerEvent.Action.DISCONNECTED, connectionHandlerID);
        fireConnectionListener(connectionManagerEvent);
    }

    public void connectionTimedOut(ConnectionHandlerID connectionHandlerID) {
        ConnectionManagerEvent connectionManagerEvent = new ConnectionManagerEvent(ConnectionManagerEvent.Action.DISCONNECTED, connectionHandlerID);
        fireConnectionListener(connectionManagerEvent);
    }

    public void connectionFailed(ConnectionHandlerID connectionHandlerID) {
        ConnectionManagerEvent connectionManagerEvent = new ConnectionManagerEvent(ConnectionManagerEvent.Action.FAILED, connectionHandlerID);
        fireConnectionListener(connectionManagerEvent);
    }

    public void receivedObject(ConnectionHandlerID connectionHandlerID, Serializable serializableObject) {
        ConnectionManagerEvent connectionManagerEvent = new ConnectionManagerEvent(ConnectionManagerEvent.Action.RECIEVED_OBJECT, connectionHandlerID, serializableObject);
        fireConnectionListener(connectionManagerEvent);
    }

}
