// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.io.File;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
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
    
    public CLICSOrganization() {
        // for Jackson deserializer
    }
    
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
    
    /**
     * Create Organization (institution) map from a organizations.json like file
     * 
     * @param jsonfile json file to deserialize
     * @return Hashmap that maps institution id to an array[3] of Strings: id/code,formal name,name or null on error
     */
    public static HashMap<String, String[]> fromJSON(File jsonfile) {
        HashMap<String, String[]>institutionsMap = new HashMap<String, String[]>();
        Log log = StaticLog.getLog();
        boolean error = false;
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            institutionsMap = createInstitutionsFromJSON(mapper.readValue(jsonfile, CLICSOrganization[].class), log);
        } catch (Exception e) {
            // deserialize exceptions
            log.log(Log.WARNING, "could not deserialize organizations file " + jsonfile.toString(), e);
        }        
        return(institutionsMap);
     }
    
    /**
     * Create Organization (institution) map from a json string
     * 
     * @param contest the contest
     * @param json string to deserialize
     * @param site the site to create the groups for
     * @return Hashmap that maps institution id to an array[3] of Strings: id/code,formal name,name or null on error
     */
    public static HashMap<String, String[]> fromJSON(String json) {
        HashMap<String, String[]>institutionsMap = null;
        Log log = StaticLog.getLog();
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            institutionsMap = createInstitutionsFromJSON(mapper.readValue(json, CLICSOrganization[].class), log);
            // deserialize exceptions
        } catch (Exception e) {
            log.log(Log.WARNING, "could not deserialize organizations string", e);
        }        
        return(institutionsMap);
    }

    /**
     * Convert CLICS organizations to PC2 hashma
     * 
     * @param corgs array CLICS organization objects
     * @param log For errors
     * @return Hashmap that maps institution id to an array[3] of Strings: id/code,formal name,name or null on error
     */
    private static HashMap<String, String[]> createInstitutionsFromJSON(CLICSOrganization [] corgs, Log log) {
        HashMap<String, String[]>institutionsMap = new HashMap<String, String[]>();
        boolean error = false;
        
        // convert each json organization to a string array containing the information that PC2 needs
        for(CLICSOrganization org: corgs) {
            
            if(StringUtilities.isEmpty(org.id)) {
                log.log(Log.SEVERE, "no id property in organization");
                error = true;
                break;
            }
            String [] fields = new String[3];
            fields[0] = org.id;
            fields[1] = org.formal_name;
            fields[2] = org.name;
            institutionsMap.put(org.id, fields);
        }
        // if it all worked out, then create the array of accounts to be returned
        if(error || corgs.length == 0) {
            institutionsMap = null;
        }
        return(institutionsMap);
    }
}
