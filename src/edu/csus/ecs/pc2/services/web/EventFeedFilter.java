package edu.csus.ecs.pc2.services.web;

import java.util.ArrayList;
import java.util.List;

import edu.csus.ecs.pc2.services.core.EventFeedJSON;

/**
 * Event Feed filter.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class EventFeedFilter {

    private String eventTypeList = null;

    private String startingEventId = null;

    private String clientInfo;
    
    public EventFeedFilter(){
        this(null, null);
    }
    

    /**
     * Create filter.
     * 
     * eventTypeList events are:  contests, judgement-types, languages, problems, groups, organizations, 
     * teams, team-members, submissions, judgements, runs, clarifications, awards.   
     * 
     * <br>
     * The complete list of events are at: {@link EventFeedType}
     * 
     * @param startingEventId start after event id, null allowed to indicate to not filter
     * @param eventTypeList eventtype list, null allowed to indicate to not filter
     */
    public EventFeedFilter(String startintEventId, String eventTypeList) {
        super();
        this.startingEventId = startintEventId;
        this.eventTypeList = eventTypeList;
    }


    public void addEventTypeList(String addEventTypeList) {
        this.eventTypeList = addEventTypeList;
    }

    public void addStartintEventId(String addStartingEventId) {
        this.startingEventId = addStartingEventId;
    }

    boolean matchesFilter(String eventId, EventFeedType type) {

        boolean matched = true;

        if (startingEventId != null) {
            long startId = EventFeedJSON.extractSequence(startingEventId);
            long actual = EventFeedJSON.extractSequence(eventId);
            matched &= actual > startId;
        }

        if (eventTypeList != null) {
            matched &= eventTypeList.indexOf(type.toString()) > -1;
        }

        return matched;
    }

    /**
     * match JSON line.
     * 
     * @param string JSON string, ex. 
     * @return
     */
    public boolean matchesFilter(String string) {

        if (startingEventId != null || eventTypeList != null) {
            return matchesFilter(getEventFeedEequence(string), getEventFeedType(string));
        }

        return true;
    }

    /**
     * Extract event feed type.
     * @param string JSON string, {"event":"languages", "id":"pc2-11", "op":"create", "data": {"id":"1","name":"Java"}}
     * @return type for event, ex. "languages" as EventFeedType.LANGUAGES.
     */
    protected EventFeedType getEventFeedType(String string) {
        // {"event":"teams", "id":"pc2-136", "op":"create", "data": {"id":"114","icpc_id":"3114","name":"team114"}}

        String[] fields = string.split(",");
        String fieldValue = fields[0].replaceAll("\"", "").replace("{type:", "").trim();
        return EventFeedType.valueOf(fieldValue.toUpperCase().replace("-", "_"));
    }

    /**
     * Extract value for id from JSON string
     * @param string ex. {"event":"languages", "id":"pc2-11", "op":"create", "data": {"id":"1","name":"Java"}}
     * @return value for id, ex pc2-11
     */
    protected String getEventFeedEequence(String string) {
        // {"event":"teams", "id":"pc2-136", "op":"create", "data": {"id":"114","icpc_id":"3114","name":"team114"}}
        String[] fields = string.split(",");
        String fieldValue = fields[1].replaceAll("\"", "").replace("id:", "").trim();
        return fieldValue;
    }
    
    /**
     * Filter JSON lines.
     * 
     * @param jsonLines
     * @return list of lines matching filter.
     */
    public List<String> filterJson(String [] jsonLines){
        return filterJson(jsonLines, this);
    }
    
    /**
     * Filter JSON lines.
     * 
     * @param jsonLines
     * @param filter
     * @return list of lines matching filter.
     */
    public static List<String> filterJson(String [] jsonLines, EventFeedFilter filter){
        List<String> filteredLines = new ArrayList<String>();
        for (String string : jsonLines) {
            if (filter.matchesFilter(string)){
                filteredLines.add(string);
            }
        }
        return filteredLines;
    }


    @Override
    public String toString() {

        String strStartingEventId = startingEventId;
        if (strStartingEventId == null) {
            strStartingEventId = "<none set>";
        }
        String strEventTypeList = eventTypeList;
        if (strEventTypeList == null) {
            strEventTypeList = "<none set>";
        }
        return "startid = " + strStartingEventId + ", event types = " + strEventTypeList;
    }


    /**
     * Set identifying information for the client using this filter
     * 
     * @param string
     */
    public void setClient(String string) {
        clientInfo = string;
    }
    
    /**
     * Return the identifying information for this user of this filter.
     * 
     * @return
     */
    public String getClient() {
        return clientInfo;
    }
}
