package edu.csus.ecs.pc2.core.transport;

import java.io.Serializable;

/**
 * Public interface Transport Manager
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public interface ITransportManager {
    String SVN_ID = "$Id$";

    /**
     * Interface method for Client Applications. Used to establish Connection to the Server.
     * 
     */
    void connectToMyServer() throws TransportException;

    /**
     * Interface method for Client Applcations used to send Serializable Object to the Server.
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
    void send(Serializable msgObj, ConnectionHandlerID toCID) throws TransportException;

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
     * Start a client Transport.
     * 
     * @param serverIP
     * @param port
     * @param appCallBack
     */
    void startClientTransport(String serverIP, int port, IBtoA appCallBack);

    /**
     * Start a Server Transport.
     * 
     * @param appCallBack
     */
    void startServerTransport(ITwoToOne appCallBack);
}
