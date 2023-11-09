// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.Calendar;
import java.util.Date;

import javax.ws.rs.core.SecurityContext;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.util.JSONTool;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * CLICS Contest State
 * 
 * @author John Buck
 *
 */
public class CLICSContestAccess {

    @JsonProperty
    private String [] capabilities;

    @JsonProperty
    private CLICSEndpoint [] endpoints;

    /**
     * Fill in properties for contest state as per 2023-06 spec
     * 
     * @param model place to get contest times from
     */
    public CLICSContestAccess(SecurityContext sc, IInternalContest model, String contestId) {
        // For each role the connected user has, we enumerate what they can do with each endpoint.
    }

    public String toJSON() {

        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "Error creating JSON for contest state info " + e.getMessage();
        }
    }
}
