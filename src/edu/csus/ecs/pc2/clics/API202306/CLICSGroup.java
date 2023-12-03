// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.io.File;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.util.JSONTool;
import edu.csus.ecs.pc2.services.core.JSONUtilities;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;

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

    public CLICSGroup() {
        // For jackson deserialize
    }
    
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
    
    /**
     * Create Group array from a groups.json like file
     * 
     * @param contest the contest
     * @param filename json file to deserialize
     * @param site the site to create the groups for
     * @return array of accounts to add, or null on error
     */
    public static Group [] fromJSON(IInternalContest contest, File jsonfile, int site) {
        Group [] newgroups = null;
        Log log = StaticLog.getLog();
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            newgroups = createGroupsFromJSON(mapper.readValue(jsonfile, CLICSGroup[].class), site, log);
        } catch (Exception e) {
            log.log(Log.WARNING, "could not deserialize group file " + jsonfile.toString(), e);
        }        
        return(newgroups);
    }
    
    /**
     * Create Group array from a groups.json like file
     * 
     * @param contest the contest
     * @param json json string to deserialize
     * @param site the site to create the groups for
     * @return array of accounts to add, or null on error
     */
    public static Group [] fromJSON(IInternalContest contest, String json, int site) {
        Group [] newgroups = null;
        Log log = StaticLog.getLog();
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            newgroups = createGroupsFromJSON(mapper.readValue(json, CLICSGroup[].class), site, log);
        } catch (Exception e) {
            log.log(Log.WARNING, "could not deserialize group", e);
        }        
        return(newgroups);
    }

    /**
     * Convert an array of CLICS Groups into a PC2 Group [] array.
     * 
     * @param cgroups CLICSGroup array
     * @param site The site to use for groups
     * @param log for error login
     * @return array of Group converted from the supplied clics cgroups
     */
    private static Group [] createGroupsFromJSON(CLICSGroup [] cgroups, int site, Log log) {
        Group [] newgroups = null;
        ArrayList<Group> groups = new ArrayList<Group>();
        Group group;
        boolean error = false;
        
        // convert each json group to a pc2 Group
        for(CLICSGroup cgroup: cgroups) {
            
            if(StringUtilities.isEmpty(cgroup.name)) {
                log.log(Log.SEVERE, "no name property in group");
                error = true;
                break;
            }
            group = new Group(cgroup.name);
            group.setSiteNumber(site);
            int groupnum;
            try {
                groupnum = Integer.parseInt(cgroup.id);
            } catch(Exception e) {
                // Some sort of conversion error - log it and abort
                log.log(Log.SEVERE, "unable to get group number from id " + cgroup.id, e);
                error = true;
                break;
            }
            group.setGroupId(groupnum);
            groups.add(group);
        }
        // if it all worked out, then create the array of accounts to be returned
        if(!error) {
            newgroups = groups.toArray(new Group[0]);
        }
        
        return newgroups;
    }
}
