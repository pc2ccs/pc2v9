package edu.csus.ecs.pc2.core.model;

/**
 * Message event/information.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class MessageEvent {

    /**
     * Actions for messages.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    public enum Action {

        /**
         * Run set to be deleted.
         */
        DELETED,
        /**
         * A new run submission.
         */
        ADDED,
    }

    /**
     * Message areas.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    public enum Area {
        /**
         * 
         */
        PROFILES,
        /**
         * 
         */
        OTHER,
    }

    /**
     * Action to be taken.
     */
    private Action action;

    private Area area;

    public MessageEvent(Action action, Area area, String message, ClientId source, ClientId destination) {
        super();
        this.action = action;
        this.area = area;
        this.message = message;
        this.source = source;
        this.destination = destination;
    }

    private String message;

    private ClientId source;

    private ClientId destination;

    public Action getAction() {
        return action;
    }

    public String getMessage() {
        return message;
    }

    public ClientId getSource() {
        return source;
    }

    public ClientId getDestination() {
        return destination;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSource(ClientId source) {
        this.source = source;
    }

    public void setDestination(ClientId destination) {
        this.destination = destination;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }
}
