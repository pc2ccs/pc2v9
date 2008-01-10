package edu.csus.ecs.pc2.core.transport;

import java.io.Serializable;

import edu.csus.ecs.pc2.core.log.Log;

/**
 * Transport methods needed by a server module.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$

public interface IServerTransport {

    /**
     * Interface method for Client Applications used to send Serializable Object to the Server.
     * 
     * @param msgObj
     * @throws TransportException
     */
    void send(Serializable msgObj) throws TransportException;

    // Server methods
    /**
     * Interface method for Server Applications used to start the Transport listening on the specified listingPort
     * 
     */
    void accecptConnections(int listeningPort) throws TransportException;

    /**
     * Interface method for Server Applications used to send a Serializable Object to a another Tranport. Requires that the
     * ConnectionHandlerID has previously been registered with the Server Application.
     * 
     * @param msgObj
     * @param toCID
     * @throws TransportException
     */
    void send(Serializable msgObj, ConnectionHandlerID connectionHandlerID) throws TransportException;

    /**
     * Interface method for Server Applications used to drop a ConnectionHandlerID.
     * 
     * @param myConnectionID
     */
    void unregisterConnection(ConnectionHandlerID myConnectionID);

    /**
     * Interface method for Server Applications used to Initiate communication with another Server.
     * 
     * @param serverIP
     * @param port
     * @return a handler ID
     * @throws TransportException
     */
    ConnectionHandlerID connectToServer(String serverIP, int port) throws TransportException;
    
    /**
     * Start a Server Transport.
     * 
     * @param appCallBack
     */
    void startServerTransport(ITwoToOne appCallBack);
    
    /**
     * Shutdown transport gracefully.
     */
    void shutdownTransport();

    /**
     * Update the log where messages go.
     * @param log
     */
    void setLog(Log log);
}
