// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFilter;
import edu.csus.ecs.pc2.core.util.JSONTool;
import edu.csus.ecs.pc2.core.model.Group;

/**
 * CLICS Group description
 * Contains information about a group
 * 
 * @author John Buck
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonFilter("locFilter")
public class CLICSGroup {

    private static final double MAX_LATITUDE = 90.0;
    private static final double MAX_LONGITUDE = 180.0;
    
    @JsonProperty
    private String id;

    @JsonProperty
    private String icpc_id;
    
    @JsonProperty
    private String name;

    @JsonProperty
    private String type;

    @JsonProperty("location.latitude")
    private double latitude = MAX_LATITUDE + 1;
    
    @JsonProperty("location.longitude")
    private double longitude = MAX_LONGITUDE + 1;

    /**
     * This is mostly for testing ATM, since the groups in PC2 do not have lat/long.
     * But... a test unit could set these.
     * 
     * @param latitude
     * @param longitude
     */
    public void setLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    /**
     * Fill in properties for a group
     * @param group The group 
     * @param exceptProps set to be filled in with properties we do not want serialized
     */
    public CLICSGroup(Group group, Set<String> exceptProps) {
 
        id = JSONTool.getGroupId(group);
        name = group.getDisplayName();
        if (group.getGroupId() != -1) {
            icpc_id = Integer.toString(group.getGroupId());
        }
        type = "site" + group.getSiteNumber();
        // if location is out of range, omit it from serialization.
        if(exceptProps != null && (latitude > MAX_LATITUDE || longitude > MAX_LONGITUDE)) {
            exceptProps.add("location.latitude");
            exceptProps.add("location.longitude");
        }
    }
}
