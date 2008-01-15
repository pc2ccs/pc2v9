package edu.csus.ecs.pc2.core.transport.connection;

import java.io.Serializable;
import java.util.Vector;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;
import edu.csus.ecs.pc2.core.transport.IBtoA;
import edu.csus.ecs.pc2.core.transport.ITransportManager;
import edu.csus.ecs.pc2.core.transport.ITwoToOne;
import edu.csus.ecs.pc2.core.transport.TransportException;
import edu.csus.ecs.pc2.core.transport.TransportManager;

/**
 * Manages Connections.
 * 
 * Class to manage initiation, dropping and handling of various transport events.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ConnectionManager implements ITransportManager {

    public static final String DEFAULT_PC2_PORT = TransportManager.DEFAULT_PC2_PORT;

    private TransportManager transportManager = null;
    
    private Log cmLog = null;

    public ConnectionManager(Log log) {
        cmLog = log;
        transportManager = new TransportManager(cmLog);
    }

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

    public void connectToMyServer() throws TransportException {
        transportManager.connectToMyServer();
    }

    public void send(Serializable msgObj) throws TransportException {
        transportManager.send(msgObj);
    }

    public ConnectionHandlerID connectToServer(String serverIP, int port) throws TransportException {
        return transportManager.connectToServer(serverIP, port);
    }

    public void startClientTransport(String serverIP, int port, IBtoA appCallBack) {
        transportManager.startClientTransport(serverIP, port, appCallBack);
    }

    public void shutdownTransport() {
        transportManager.shutdownTransport();
    }

    public void setLog(Log log) {
        cmLog = log;
        transportManager.setLog(log);
    }

    public void accecptConnections(int listeningPort) throws TransportException {
        transportManager.accecptConnections(listeningPort);
    }

    public void send(Serializable msgObj, ConnectionHandlerID connectionHandlerID) throws TransportException {
        transportManager.send(msgObj,connectionHandlerID);
    }

    public void unregisterConnection(ConnectionHandlerID myConnectionID) {
        transportManager.unregisterConnection(myConnectionID);
    }

    public void startServerTransport(ITwoToOne appCallBack) {
        transportManager.startServerTransport(appCallBack);
    }
}
