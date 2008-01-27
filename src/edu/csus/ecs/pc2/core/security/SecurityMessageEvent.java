package edu.csus.ecs.pc2.core.security;

import edu.csus.ecs.pc2.core.exception.ContestSecurityException;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * A security message event.
 * 
 * This event is triggered when a security event occurs.
 * 
 * @see edu.csus.ecs.pc2.core.security.SecurityMessageHandler
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class SecurityMessageEvent {

    /**
     * Security Message Event State.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    public enum Action {

        /**
         * New Security Event.
         */
        NEW
    }

    private Action action;

    private ClientId whoTriggeredEvent;

    private ConnectionHandlerID connectionHandlerID;
    
    private String message;

    private String eventName;
    
    private Exception exception;

    private ContestSecurityException contestSecurityException;
    
    public SecurityMessageEvent(Action action, ClientId whoTriggeredEvent, String message, String eventName, ContestSecurityException contestSecurityException) {
        super();
        this.action = action;
        this.whoTriggeredEvent = whoTriggeredEvent;
        this.message = message;
        this.eventName = eventName;
        this.contestSecurityException = contestSecurityException;
    }


    public SecurityMessageEvent(Action action, ClientId whoTriggeredEvent, String message, String eventName, Exception exception) {
        super();
        this.action = action;
        this.whoTriggeredEvent = whoTriggeredEvent;
        this.message = message;
        this.eventName = eventName;
        this.exception = exception;
    }

    public Action getAction() {
        return action;
    }

    public ConnectionHandlerID getConnectionHandlerID() {
        return connectionHandlerID;
    }

    public String getEventName() {
        return eventName;
    }

    /**
     * Get Security Exception.
     * @return
     */
    public ContestSecurityException getContestSecurityException() {
        return contestSecurityException;
    }


    public Exception getException() {
        return exception;
    }


    public String getMessage() {
        return message;
    }

    /**
     * Which client triggered this security event.
     * @return
     */
    public ClientId getWhoTriggeredEvent() {
        return whoTriggeredEvent;
    }
}
