// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contains information the geographic location of another object.
 *
 * @author John Buck
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CLICSLocation {
    private static final double MAX_LATITUDE = 90.0;
    private static final double MAX_LONGITUDE = 180.0;
    private static final double MIN_LATITUDE = -90.0;
    private static final double MIN_LONGITUDE = -180.0;

    @JsonProperty("latitude")
    private double latitude = MAX_LATITUDE + 1;

    @JsonProperty("longitude")
    private double longitude = MAX_LONGITUDE + 1;

    /**
     * Fill in location information properties
     *
     * @param latitude The location's latitude
     * @param longitude The location's longitude
     * @throws IllegalArgumentException if the latitude and/or longitude are out of range
     */
    public CLICSLocation(double latitude, double longitude) throws IllegalArgumentException {
        if(latitude < MIN_LATITUDE || latitude > MAX_LATITUDE ||
           longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE) {
            throw new IllegalArgumentException("invalid CLICS location");
        }
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
