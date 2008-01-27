package edu.csus.ecs.pc2.core.exception;

import java.util.Date;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * When an user attempted to change contest data, but not allowed.
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ContestSecurityException extends Exception {

    private ClientId clientId;

    private ConnectionHandlerID connectionHandlerID;

    private String securityMessage;
    
    private Date date = new Date();

    /**
     * 
     */
    private static final long serialVersionUID = -4262229757068255182L;

    public ContestSecurityException(ClientId clientId, ConnectionHandlerID connectionHandlerID, String message) {
        super(message);
        this.clientId = clientId;
        this.connectionHandlerID = connectionHandlerID;
        this.securityMessage = message;
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

    public String getSecurityMessage() {
        return securityMessage;
    }

    public void setSecurityMessage(String string) {
        this.securityMessage = string;
    }

}
