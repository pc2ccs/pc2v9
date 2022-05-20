package edu.csus.ecs.pc2.core.imports.clics;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.model.JSONObjectMapper;

/**
 * A JSON representation for the ICPC teams.json data.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class TeamAccount {

    @JsonProperty
    private String id;

    @JsonProperty
    private String icpc_id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String display_name;

    @JsonProperty
    private String organization_id;

    @JsonProperty
    private List< String> group_ids;

    // {"id": "1", "icpc_id": "449759", "name": "Kansas State University", "display_name": "Kansas State University", "organization_id": "k-state_edu", "group_ids": ["4"]},
    /**
     * 
     * 21 id "22" icpc_id "449752" name "Emory University" display_name "Emory University" organization_id "emory_edu" group_ids 0 "3"
     */

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getIcpc_id() {
        return icpc_id;
    }

    public void setIcpc_id(String icpc_id) {
        this.icpc_id = icpc_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getOrganization_id() {
        return organization_id;
    }

    public void setOrganization_id(String organization_id) {
        this.organization_id = organization_id;
    }

   
    public List<String> getGroup_ids() {
        return group_ids;
    }
    
    public void setGroup_ids(List<String> group_ids) {
        this.group_ids = group_ids;
    }
    
    public String toJSON() throws JsonProcessingException {

        ObjectMapper om = JSONObjectMapper.getObjectMapper();
        return om.writeValueAsString(this);
    }
    
    @Override
    public String toString() {
        try {
            return toJSON();
        } catch (Exception e) {
            e.printStackTrace();
            return super.toString();
        }
    }
}
