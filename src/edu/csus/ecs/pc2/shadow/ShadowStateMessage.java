// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contest State change from a remote CCS via the REST event-feed API.
 *
 * @author John Buck, PC^2 Team, pc2@ecs.csus.edu
 */
public class ShadowStateMessage {

    @JsonProperty
    private Date started;
    @JsonProperty
    private Date ended;
    @JsonProperty
    private Date frozen;
    @JsonProperty
    private Date thawed;
    @JsonProperty
    private Date finalized;
    @JsonProperty
    private Date end_of_updates;

    public Date getStarted() {
        return started;
    }
    public Date getEnded() {
        return ended;
    }
    public Date getFrozen() {
        return frozen;
    }
    public Date getThawed() {
        return thawed;
    }
    public Date getFinalized() {
        return finalized;
    }
    public Date getEndOfUpdates( ) {
        return end_of_updates;
    }
}
