// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * CLICS File Reference
 * Contains information about a file used in the API
 * 
 * @author John Buck
 *
 */

public class CLICSFileReference {

    @JsonProperty
    private String href;

    @JsonProperty
    private String filename;

    @JsonProperty
    private String hash;

    @JsonProperty
    private String mime;
    
    @JsonProperty
    private int width;
    
    @JsonProperty
    private int height;

    public CLICSFileReference(String filename, String mime) {
        this.filename = filename;
        this.mime = mime;
    }

    public String toJSON() {

        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "Error creating JSON for FILE reference " + e.getMessage();
        }
    }
}
