package edu.csus.ecs.pc2.core.transport;

import java.io.Serializable;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.packet.Packet;

/**
 * Public interface Transport Manager for a client.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IClientTransport {

    /**
     * Establish Connection to the Server.
     */
    void connectToMyServer() throws TransportException;

    /**
     * Send Serializable Object to the Server.
     * <P>
     * In practice sends a {@link Packet} to the server.
     * 
     * 
     * @param msgObj
     * @throws TransportException
     */
    void send(Serializable msgObj) throws TransportException;

    /**
     * Initiate communication with another Server.
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
