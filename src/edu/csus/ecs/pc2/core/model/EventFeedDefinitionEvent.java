package edu.csus.ecs.pc2.core.model;

/**
 * Event Feed Definition Event.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EventFeedDefinitionEvent {

    /**
     * Event Feed States(s).
     * 
     * @author pc2@ecs.csus.edu
     */
    public enum Action {

        /**
         * Run set to be deleted.
         */
        DELETED,
        /**
         * Problem added.
         */
        ADDED,
        /**
         * Problem modified.
         */
        CHANGED,
        /**
         * Reload/Refresh all problems.
         */
        REFRESH_ALL,
    }

    private Action action;

    private EventFeedDefinition eventFeedDefinition;

    public EventFeedDefinitionEvent(Action action, EventFeedDefinition eventFeedDefinition) {
        super();
        this.action = action;
        this.eventFeedDefinition = eventFeedDefinition;
    }

    public Action getAction() {
        return action;
    }

    public EventFeedDefinition getEventFeedDefinition() {
        return eventFeedDefinition;
    }

}
