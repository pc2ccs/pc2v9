package edu.csus.ecs.pc2.core.transport;

import java.io.Serializable;

import edu.csus.ecs.pc2.core.log.Log;

/**
 * Public interface Transport Manager for a client.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public interface IClientTransport {

    /**
     * Interface method for Client Applications. Used to establish Connection to the Server.
     */
    void connectToMyServer() throws TransportException;

    /**
     * Interface method for Client Applications used to send Serializable Object to the Server.
     * 
     * @param msgObj
     * @throws TransportException
     */
    void send(Serializable msgObj) throws TransportException;

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
     * Shutdown transport gracefully.
     * 
     */
    void shutdownTransport();

    /**
     * Update the log where messages go.
     * 
     * @param log
     */
    void setLog(Log log);
}
