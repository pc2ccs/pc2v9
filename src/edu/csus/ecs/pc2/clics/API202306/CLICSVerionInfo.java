// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * CLICS Version Info.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 * @author John Buck
 *
 */
public class CLICSVerionInfo {

    @JsonProperty
    private String version;

    @JsonProperty
    private String version_url;

    @JsonProperty
    private CLICSProvider provider;
 
    /**
     * Fill in the API version information properties
     * 
     * @param versionInfo
     */
    public CLICSVerionInfo(VersionInfo versionInfo) {
        provider = new CLICSProvider(versionInfo);
        version = ResourceConfig202306.CLICS_API_VERSION;
        version_url = ResourceConfig202306.CLICS_API_VERSION_URL;
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
