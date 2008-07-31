package edu.csus.ecs.pc2.core.model;

/**
 * Event for when a password is changed.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PasswordChangeEvent {

    private ClientId clientId;

    private Action action;

    private String message;

    /**
     * Password Change Action.
     * 
     * @author pc2@ecs.csus.edu
     */
    public enum Action {

        /**
         * Password was changed.
         */
        PASSWORD_CHANGED,
        /**
         * Password was not changed.
         */
        PASSWORD_NOT_CHANGED
    }

    public PasswordChangeEvent(ClientId clientId, Action action, String message) {
        super();
        this.clientId = clientId;
        this.action = action;
        this.message = message;
    }

    public ClientId getClientId() {
        return clientId;
    }

    public void setClientId(ClientId clientId) {
        this.clientId = clientId;
    }

    /**
     * Message about the password change (attempt).
     * 
     * @return
     */
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Action getAction() {
        return action;
    }

}
