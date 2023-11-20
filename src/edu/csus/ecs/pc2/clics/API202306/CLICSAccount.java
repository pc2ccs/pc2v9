// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import javax.ws.rs.core.SecurityContext;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.services.core.JSONUtilities;
import edu.csus.ecs.pc2.services.eventFeed.WebServer;

/**
 * CLICS Team object
 * 
 * @author John Buck
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CLICSAccount {

    @JsonProperty
    private String id;

    @JsonProperty
    private String username;

    @JsonProperty
    private String password;

    @JsonProperty
    private String name;

    @JsonProperty
    private String type;

    @JsonProperty
    private String team_id;

    @JsonProperty
    private String person_id;
      
    /**
     * Fill in properties for an account as per 2023-06 spec
     * 
     * @param model The contest
     * @param account team information
     */
    public CLICSAccount(IInternalContest model, SecurityContext sc, Account account) {
        ClientId cid = account.getClientId();
        id = "" + cid.getClientNumber();
        name = account.getDisplayName();
        username = cid.getName();
        if(sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN)) {
            password = account.getPassword();
        }
        type = "" + cid.getClientType();
        type = type.toLowerCase();
        if(account.isTeam()) {
            team_id = username;
        }
    }

    public String toJSON() {

        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "Error creating JSON for account info " + e.getMessage();
        }
    }
}
