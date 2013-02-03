package edu.csus.ecs.pc2.core.list;

import edu.csus.ecs.pc2.core.model.EventFeedDefinition;

/**
 * Event Feed Definitions List.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EventFeedDefinitionsList extends ElementList {

    /**
     * 
     */
    private static final long serialVersionUID = -4207936310867823871L;

    public EventFeedDefinition[] getList() {
        EventFeedDefinition[] theList = new EventFeedDefinition[size()];

        if (theList.length == 0) {
            return theList;
        }

        theList = (EventFeedDefinition[]) values().toArray(new EventFeedDefinition[size()]);
        return theList;
    }

    public void add(EventFeedDefinition eventFeedDefinition) {
        super.add(eventFeedDefinition);
    }
}
