package edu.csus.ecs.pc2.core.model;

/**
 * Event Feed Definition changed event.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IEventFeedDefinitionListener {

    void eventFeedDefinitionAdded(EventFeedDefinitionEvent event);

    void eventFeedDefinitionChanged(EventFeedDefinitionEvent event);

    void eventFeedDefinitionRemoved(EventFeedDefinitionEvent event);

    /**
     * Refresh All Event Feed Definitions
     * 
     * @param event
     *            event feed info
     */
    void eventFeedDefinitionRefreshAll(EventFeedDefinitionEvent event);
}
