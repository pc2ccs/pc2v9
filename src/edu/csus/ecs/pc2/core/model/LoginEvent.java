package edu.csus.ecs.pc2.core.model;

import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;


/**
 * A login/logoff event.
 *
 * @author pc2@ecs.csus.edu
 */
// $HeadURL

public class LoginEvent {

    public static final String SVN_ID = "$Id$";

    /**
     * Run Event States(s).
     * 
     * @author pc2@ecs.csus.edu
     */
    public enum Action {

        /**
         * A client logoff. 
         */
        LOGOFF,
        /**
         * A new client login.
         */
        NEW_LOGIN,
        
        /**
         *  A client was not allowed to login
         */
        LOGIN_DENIED,
    }

    private Action action = Action.NEW_LOGIN;
    
    private ClientId clientId;
    
    private ConnectionHandlerID connectionHandlerID;
    
    /**
     * Message that gives more description
     */
    private String message;

    public LoginEvent(Action action, ClientId clientId, ConnectionHandlerID connectionHandlerID) {
        this(action,clientId,connectionHandlerID,null);
    }

    public LoginEvent(Action action, ClientId clientId, ConnectionHandlerID connectionHandlerID, String message) {
        super();
        this.action = action;
        this.clientId = clientId;
        this.connectionHandlerID = connectionHandlerID;
        this.message = message;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
