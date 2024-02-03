// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.PermissionGroup;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.PermissionList;
import edu.csus.ecs.pc2.core.util.JSONTool;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * CLICS Team object
 *
 * @author John Buck
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CLICSTeam {

    @JsonProperty
    private String id;

    @JsonProperty
    private String icpc_id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String label;

    @JsonProperty
    private String display_name;

    @JsonProperty
    private String organization_id;

    @JsonProperty
    private String [] group_ids;

    @JsonProperty
    private boolean hidden;


    // The following properties are not maintained by the CCS and therefore not included (they are optional in the spec)
    // location, photo, video, backup, key_log, tool_data, desktop, webcam, audio

    /**
     * For Jackson deserializer
     */
    public CLICSTeam() {
    }

    /**
     * Fill in properties for team as per 2023-06 spec
     *
     * @param model The contest
     * @param account team information
     */
    public CLICSTeam(IInternalContest model, Account account) {

        id = "" + account.getClientId().getClientNumber();

        if (JSONTool.notEmpty(account.getExternalId())) {
            icpc_id = account.getExternalId();
        }
        label = account.getLabel();
        name = account.getDisplayName();
        if (JSONTool.notEmpty(account.getInstitutionCode()) && !account.getInstitutionCode().equals("undefined")) {
            organization_id = JSONTool.getOrganizationId(account);
        }
        if (account.getGroupId() != null) {
            group_ids = new String[1];
            // FIXME eventually accounts should have more then 1 groupId, make sure add them
            group_ids[0] = JSONTool.getGroupId(model.getGroup(account.getGroupId()));
        }
        hidden = !account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD);
    }

    public String toJSON() {

        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "Error creating JSON for team info " + e.getMessage();
        }
    }

    /**
     * Create account list from a teams.json like file
     *
     * @param contest the contest (needed for groups)
     * @param jsonfile json file to deserialize
     * @param site the site to create the accounts for
     * @param institutionsMap hashmap mapping organization id to organization fields (to be removed once organizations
     *     are part of the contest model like groups). May be null if not interested in organizations.
     * @return array of accounts to add, or null on error
     */
    public static Account [] fromJSON(IInternalContest contest, File jsonfile, int site, HashMap<String,String[]> institutionsMap) {
        Account [] newaccounts = null;
        Log log = StaticLog.getLog();

        try {
            ObjectMapper mapper = new ObjectMapper();
            newaccounts = createTeamsFromTeamsJSON(contest, mapper.readValue(jsonfile, CLICSTeam[].class), site, institutionsMap, log);
        } catch (Exception e) {
            log.log(Log.WARNING, "could not deserialize team file " + jsonfile, e);
        }
        return(newaccounts);
     }

    /**
     * Create account list from a strings contains a teams json file
     *
     * @param contest the contest (needed for groups)
     * @param json json to deserialize
     * @param site the site to create the accounts for
     * @param institutionsMap hashmap mapping organization id to organization fields (to be removed once organizations
     *     are part of the contest model like groups).  May be null if not interested in organizations.
     * @return array of accounts to add, or null on error
     */
    public static Account [] fromJSON(IInternalContest contest, String json, int site, HashMap<String,String[]> institutionsMap) {
        Account [] newaccounts = null;
        Log log = StaticLog.getLog();

        try {
            ObjectMapper mapper = new ObjectMapper();
            newaccounts = createTeamsFromTeamsJSON(contest, mapper.readValue(json, CLICSTeam[].class), site, institutionsMap, log);
        } catch (Exception e) {
            log.log(Log.WARNING, "could not deserialize team string", e);
        }
        return(newaccounts);
     }

    /**
     * Converts CLICS teams into a PC2 Account array.
     *
     * @param contest The contest
     * @param teams array of CLICS teams
     * @param site The site to create accounts on
     * @param log For errors
     * @return an Account array of teams converted from the CLICS teams or null if error
     */
    private static Account [] createTeamsFromTeamsJSON(IInternalContest contest, CLICSTeam [] teams, int site, HashMap<String,String[]> institutionsMap, Log log) {

        Account [] newaccounts = null;
        ArrayList<Account> accounts = new ArrayList<Account>();
        Account account;
        JSONTool jsontool = new JSONTool(contest, null);
        PermissionList teamPermissionList = new PermissionGroup().getPermissionList (ClientType.Type.TEAM);
        boolean error = false;

        // convert each team to an account
        for(CLICSTeam team: teams) {
            int teamnum;
            try {
                if(StringUtilities.isEmpty(team.label)) {
                    teamnum = Integer.parseInt(team.id);
                } else {
                    teamnum = Integer.parseInt(team.label);
                }
            } catch(Exception e) {
                // Some sort of conversion error - log it and abort
                log.log(Log.SEVERE, "unable to get team number from label or id", e);
                error = true;
                break;
            }

            ClientId clientId = new ClientId(site, Type.TEAM, teamnum);
            // create barebones account
            account = new Account(clientId, clientId.getName(), site);
            account.clearListAndLoadPermissions(teamPermissionList);
            account.setLabel(team.label);
            account.setExternalId(team.icpc_id);
            if(!StringUtilities.isEmpty(team.display_name)) {
                account.setDisplayName(team.display_name);
            }
            if(!StringUtilities.isEmpty(team.name)) {
                account.setTeamName(team.name);
            }
            // now fill in any other fields we can
            if(team.group_ids != null && team.group_ids.length > 0) {
                Group group = jsontool.getGroupFromNumber(team.group_ids[0]);
                if(group == null) {
                    log.log(Log.SEVERE, "No group has been defined with GroupId=" + team.group_ids[0]);
                    error = true;
                    break;
                }
                account.setGroupId(group.getElementId());
                //TODO fix this when PC2 supports multiple groups per account
                if(team.group_ids.length > 1) {
                    log.log(Log.INFO, account.getDisplayName() + " has " + team.group_ids.length + " groups assigned - only using first one");
                }
            }
            if(team.hidden) {
                account.removePermission(Permission.Type.DISPLAY_ON_SCOREBOARD);
            }
            // TODO Fix this when organizations are done correctly and built into the contest model
            if(!StringUtilities.isEmpty(team.organization_id)) {
                if(institutionsMap != null && institutionsMap.containsKey(team.organization_id)) {
                    // lookup institution info [0]=id, [1]=formal name, [2] = short name
                    String [] orgInfo = institutionsMap.get(team.organization_id);
                    if(orgInfo != null && orgInfo.length >= 3) {
                        account.setInstitutionCode(orgInfo[0]);
                        account.setInstitutionName(orgInfo[1]);
                        account.setInstitutionShortName(orgInfo[2]);
                    }
                }
            }
            accounts.add(account);
        }
        // if it all worked out, then create the array of accounts to be returned
        if(!error) {
            newaccounts = accounts.toArray(new Account[0]);
        }
        return(newaccounts);
    }
}
