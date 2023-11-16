// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * CLICS Endpoint
 * Contains information about an API endpoint that is supported
 * 
 * @author John Buck
 *
 */

public class CLICSEndpoint {

    @JsonProperty
    private String type;

    @JsonProperty
    private String [] properties;

    public CLICSEndpoint(String type, String [] properties) {
        this.type = type;
        this.properties = properties;
    }

    public String toJSON() {

        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "Error creating JSON for CLICSEndpoint " + e.getMessage();
        }
    }
}
