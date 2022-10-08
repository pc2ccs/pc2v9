// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.imports.clics;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.model.JSONObjectMapper;

/**
 * 
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */

//@JsonIgnoreProperties(ignoreUnknown = true)
public class CLICSEventFeedEvent {

    // {"type": "teams", "id": "289de609-b1a0-40d8-a9e0-54a30e752e69", "op": "create", "data": {"id": "169347", "name": "Colin Peppler", "icpc_id": null, "group_ids": [], "display_name": "Virginia
    // Tech", "organization_id": "vt_edu"}}

    @JsonProperty
    private String type;

    @JsonProperty
    private String id;

    @JsonProperty
    private String op;

    @JsonProperty
    HashMap<String, Object> data;

    // @JsonProperty
    // private String data;

    // @JsonProperty
    // private List< String> group_ids;

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getOp() {
        return op;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public String toJSON() throws JsonProcessingException {

        ObjectMapper om = JSONObjectMapper.getObjectMapper();
        return om.writeValueAsString(this);
    }

}
