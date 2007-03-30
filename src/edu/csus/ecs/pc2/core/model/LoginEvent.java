package edu.csus.ecs.pc2.core.model;

import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;


/**
 * 
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
    }

    private Action action = Action.NEW_LOGIN;
    
    private ClientId clientId;
    
    private ConnectionHandlerID connectionHandlerID;

    public LoginEvent(Action action, ClientId clientId, ConnectionHandlerID connectionHandlerID) {
        super();
        this.action = action;
        this.clientId = clientId;
        this.connectionHandlerID = connectionHandlerID;
    }

    public LoginEvent(ClientId clientId, ConnectionHandlerID connectionHandlerID) {
        super();
        this.clientId = clientId;
        this.connectionHandlerID = connectionHandlerID;
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


}
