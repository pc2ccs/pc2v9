package edu.csus.ecs.pc2.core.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * CLICS Version Info.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class CLICSVerionInfo {

    public CLICSVerionInfo(VersionInfo versionInfo) {
        version = versionInfo.getPC2Version() + " build " + versionInfo.getBuildNumber();
        version_url = "https://ccs-specs.icpc.io/2022-07/contest_api";
    }

    @JsonProperty
    private String version;

    @JsonProperty
    private String version_url;

    @JsonProperty
    private String name;

    // TODO Add logo when we have olne to supply
    //    @JsonProperty
    //    private String logo;

    //{
    //   "version": "2022-07",
    //   "version_url": "https://ccs-specs.icpc.io/2022-07/contest_api",
    //   "name": "Kattis",
    //   "logo": [{
    //      "href": "/api/logo",
    //      "hash": "36dcf7975b179447783cdfc857ce9ae0",
    //      "filename": "logo.png",
    //      "mime": "image/png",
    //      "width": 600,
    //      "height": 600
    //   }]
    //}

    public String toJSON() {

        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "Error creating JSON for version info " + e.getMessage();
        }

    }

}
