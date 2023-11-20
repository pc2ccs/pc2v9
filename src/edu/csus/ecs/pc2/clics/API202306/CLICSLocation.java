// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * CLICS geographic location
 * Contains information the location of another object
 * 
 * @author John Buck
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CLICSLocation {
    private static final double MAX_LATITUDE = 90.0;
    private static final double MAX_LONGITUDE = 180.0;

    @JsonProperty("latitude")
    private double latitude = MAX_LATITUDE + 1;
    
    @JsonProperty("longitude")
    private double longitude = MAX_LONGITUDE + 1;

    /**
     * Fill in command information properties
     * 
     * @param command The command to execute
     * @param args Arguments to command, null if none
     */
    public CLICSLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
}
