// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * Contains information about an API endpoint that is supported.
 *
 * @author John Buck
 *
 */

public class CLICSEndpoint {

    @JsonProperty
    private String type;

    @JsonProperty
    private String [] properties;

    /**
     * For use with the access endpoint.  This describes the properties of a single endpoint.
     *
     * @param type String representing the name of the endpoint, eg. "teams", "groups", etc.
     * @param properties List of supported properties
     * @throws IllegalArgumentException
     */
    public CLICSEndpoint(String type, String [] properties) throws IllegalArgumentException {
        if(properties == null || properties.length <= 0) {
            throw new IllegalArgumentException("null or empty properties for CLICSEndpoint");
        }
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
