// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * CLICS Provider
 * Contains information about the system providing the API feed (PC2, in this case)
 * 
 * @author John Buck
 *
 */

public class CLICSProvider {

    @JsonProperty
    private String name;

    @JsonProperty
    private String version;

//  @JsonProperty
//  private CLICSProviderLogo [] logos;

    /**
     * Fill in API Provider information properties (for the version endpoint)
     * 
     * @param versionInfo
     */
    public CLICSProvider(VersionInfo versionInfo) {
        name = "pc2";
        version = versionInfo.getPC2Version() + " build " + versionInfo.getBuildNumber(); 
    }

    public String toJSON() {

        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "Error creating JSON for version info " + e.getMessage();
        }
    }
}