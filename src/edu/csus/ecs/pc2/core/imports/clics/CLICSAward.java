// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.imports.clics;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.model.JSONObjectMapper;

/**
 * Single CLICS Award.
 * 
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class CLICSAward {

    public CLICSAward() {
        super();
    }
    /**
     * Award with teams.
     * @param id  identifier for award, ex winner
     * @param citation ex. Contest winner
     * @param team_ids lsit of ids
     */
    public CLICSAward(String id, String citation, List<String> team_ids) {
        super();
        this.id = id;
        this.citation = citation;
        this.team_ids = team_ids;
    }
    
    /**
     * Award with team.
     * @param id  identifier for award, ex winner
     * @param citation ex. Contest winner
     * @param teamid  single team id
     */
    public CLICSAward(String id, String citation, String teamid) {
        super();
        this.id = id;
        this.citation = citation;
        this.team_ids = new ArrayList<String>();
        team_ids.add(teamid);
    }

    /**
     * Identifier of the award.
     */
    @JsonProperty
    private String id;

    /**
     * Award citation, e.g. "Gold medal winner".
     */
    @JsonProperty
    private String citation;

    /**
     * JSON array of team ids receiving this award.
     * 
     * No meaning must be implied or inferred from the order of IDs. If the value is null this means that the award is not currently being updated. If the value is the empty array this means that the
     * award is being updated, but no team has been awarded the award at this time.
     */
    @JsonProperty
    private List<String> team_ids;

    public String getId() {
        return id;
    }

    public String getCitation() {
        return citation;
    }

    public List<String> getTeamIds() {
        return team_ids;
    }

    public String toJSON() throws JsonProcessingException {
        ObjectMapper om = JSONObjectMapper.getObjectMapper();
        return om.writeValueAsString(this);
    }

}
