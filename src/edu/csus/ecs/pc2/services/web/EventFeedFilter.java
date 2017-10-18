package edu.csus.ecs.pc2.services.web;

/**
 * Event Feed filter.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class EventFeedFilter {

    private String eventTypeList;
    private String addStartintEventId;

    public void addEventTypeList(String eventTypeList) {
        this.eventTypeList = eventTypeList;
    }

    public void addStartintEventId(String startintEventId) {
        this.addStartintEventId = startintEventId;
    }
    
    boolean matchesFilter(String eventId, EventFeedType type, String data){
        
        /**
         * TODO compareTo for eventId
         */
        
        /**
         * TODO is filtering on type.
         */
        return true;
    }

    /**
     * match JSON line.
     * @param string
     * @return
     */
    public boolean matchesFilter(String string) {
        
        // TODO decompose java and call   boolean matchesFilter(String eventId, EventFeedType type, String data);
        
        return true;
    }

}
