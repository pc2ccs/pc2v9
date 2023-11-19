// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * CLICS Organization
 * Contains information about an Organization (institution)
 * 
 * @author John Buck
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CLICSOrganization {

    @JsonProperty
    private String id;

    @JsonProperty
    private String icpc_id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String formal_name;

    @JsonProperty
    private String country;

    @JsonProperty
    private CLICSFileReference [] country_flag;
    
    @JsonProperty
    private String url;

    @JsonProperty
    private String twitter_hashtag;

    @JsonProperty
    private String twitter_account;

    @JsonProperty
    private CLICSLocation location;
    
    @JsonProperty
    private CLICSFileReference [] logo;
    
    /**
     * Fill in organization information properties
     * TODO: fix to use organization obtained from organizations.json.  From insitutions.tsv, we get this:
     * [0]:INST-U-1329 [1]:New York University [2]:NYU
     * but only need [1].  We get the rest from the a typical account.
     * 
     * @param account typical account using this organization
     * @param orgFields String array of fields that correspond to institutions.tsv.  Gack!
     */
    public CLICSOrganization(String orgId, Account account, String [] orgFields) {
        id = orgId;
        icpc_id = orgId;
        name = orgFields[2];
        formal_name = orgFields[1];
        country = account.getCountryCode();
    }

    public String toJSON() {

        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "Error creating JSON for organization info " + e.getMessage();
        }
    }
}
