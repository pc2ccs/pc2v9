// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.util.JSONTool;
import edu.csus.ecs.pc2.services.core.JSONUtilities;
import edu.csus.ecs.pc2.core.model.Group;

/**
 * CLICS Group description
 * Contains information about a group
 * 
 * @author John Buck
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CLICSGroup {
    
    @JsonProperty
    private String id;

    @JsonProperty
    private String icpc_id;
    
    @JsonProperty
    private String name;

    @JsonProperty
    private String type;

    @JsonProperty
    private CLICSLocation location;

    /**
     * Fill in properties for a group
     * @param group The group 
     */
    public CLICSGroup(Group group) {
 
        id = JSONTool.getGroupId(group);
        name = group.getDisplayName();
        if (group.getGroupId() != -1) {
            icpc_id = Integer.toString(group.getGroupId());
        }
        type = "site" + group.getSiteNumber();
    }
    
    /**
     * This is mostly for testing ATM, since the groups in PC2 do not have lat/long.
     * But... a test unit could set these.
     * 
     * @param latitude
     * @param longitude
     */
    public void setLocation(double latitude, double longitude) {
        location = new CLICSLocation(latitude, longitude);
    }
    

    public String toJSON() {

        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "Error creating JSON for group info " + e.getMessage();
        }
    }
}
