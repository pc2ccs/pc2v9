package edu.csus.ecs.pc2.core.exception;

import java.util.Date;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * Some processing problem on the Server.
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ServerProcessException extends Exception {

    public static final String UNDEFINED = "error";

    public static final String PLAYBACK = "playback";

    private ClientId clientId;

    private ConnectionHandlerID connectionHandlerID;

    private String processingMessage;
    
    private String area = UNDEFINED;
    
    private Date date = new Date();

    /**
     * 
     */
    private static final long serialVersionUID = -4262229757068255182L;

    public ServerProcessException(ClientId clientId, ConnectionHandlerID connectionHandlerID, String message) {
        super(message);
        this.clientId = clientId;
        this.connectionHandlerID = connectionHandlerID;
        this.processingMessage = message;
    }

    public ClientId getClientId() {
        return clientId;
    }

    public void setClientId(ClientId clientId) {
        this.clientId = clientId;
    }

    public ConnectionHandlerID getConnectionHandlerID() {
        return connectionHandlerID;
    }

    public void setConnectionHandlerID(ConnectionHandlerID connectionHandlerID) {
        this.connectionHandlerID = connectionHandlerID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getArea() {
        return area;
    }
    
    public void setArea(String area) {
        this.area = area;
    }

    public String getProcessingMessage() {
        return processingMessage;
    }
    
    public void setProcessingMessage(String processingMessage) {
        this.processingMessage = processingMessage;
    }
}
