package edu.csus.ecs.pc2.core.imports.clics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A representation of CLICS Event Feed JSON line.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class EventFeedLine {

    @JsonProperty
    private String id;

    @JsonProperty
    private String type;

    @JsonProperty
    private String op;

    /**
     * Individual data for event feed.
     */
    @JsonProperty
    private Object data;

    @JsonProperty
    private String time;

    private static ObjectMapper mapper = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Parse jsonLine into EventFeedLine.
     * 
     * @param jsonLine
     * @return
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static EventFeedLine fromJSON(String jsonLine) throws JsonParseException, JsonMappingException, IOException {
        return getMapper().readValue(jsonLine, EventFeedLine.class);
    }

    /**
     * Convert object to TeamAccount
     * 
     * @param obj
     * @return
     */
    public static TeamAccount convertTo(Object obj) {
        TeamAccount teamAccount = getMapper().convertValue(obj, TeamAccount.class);
        return teamAccount;
    }

    /**
     * Extract team information from CLICS Event Feed JSON.
     * 
     * @param eventFeedLines
     *            CLICS Event Feed JSON
     * @return list of TeamAccounts/info
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static List<TeamAccount> getTeams(String[] eventFeedLines) throws JsonParseException, JsonMappingException, IOException {

        /**
         * Map of team id to TeamAccount
         */
        Map<String, TeamAccount> existingTeams = new HashMap<String, TeamAccount>();

        for (String line : eventFeedLines) {
            EventFeedLine efl = EventFeedLine.fromJSON(line);
            if ("teams".equals(efl.getType())) {
                TeamAccount teamAccount = getMapper().convertValue(efl.getData(), TeamAccount.class);
                existingTeams. put(teamAccount.getId(), teamAccount);
            }
        }

        Collection<TeamAccount> values = existingTeams.values();
        List<TeamAccount> list = new ArrayList<TeamAccount>(values);
        return list;
    }

    private static ObjectMapper getMapper() {
        if (mapper == null) {
            mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
        return mapper;
    }
}
