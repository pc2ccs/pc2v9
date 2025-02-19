// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.SecurityContext;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.services.core.JSONUtilities;
import edu.csus.ecs.pc2.services.eventFeed.WebServer;

/**
 * CLICS Account object
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

    public CLICSAccount() {
        // jackson deserialize
    }

    /**
     * Fill in properties for an account as per 2023-06 spec
     *
     * @param model The contest
     * @param account team information
     */
    public CLICSAccount(IInternalContest model, SecurityContext sc, Account account) {
        ClientId cid = account.getClientId();
        id = cid.getName();
        username = id;
        name = account.getDisplayName();
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

    /**
     * Create account list from a accounts.json like file
     *
     * @param contest the contest (needed for groups)
     * @param jsonfile json file to deserialize
     * @param site the site to create the accounts for
     * @return array of accounts to add, or null on error
     */
    public static Account [] fromJSON(IInternalContest contest, File jsonfile, int site) {
        Account [] newaccounts = null;
        Log log = StaticLog.getLog();

        try {
            ObjectMapper mapper = new ObjectMapper();
            newaccounts = createPC2AccountsFromCLICSAccounts(contest, mapper.readValue(jsonfile, CLICSAccount[].class), site, log);
        } catch (Exception e) {
            log.log(Log.WARNING, "could not deserialize account file " + jsonfile, e);
        }
        return(newaccounts);
    }

    /**
     * Create account list from a accounts.json like file
     *
     * @param contest the contest (needed for groups)
     * @param yamlfile yaml file to deserialize
     * @param site the site to create the accounts for
     * @return array of accounts to add, or null on error
     */
    public static Account [] fromYAML(IInternalContest contest, File yamlfile, int site) {
        Account [] newaccounts = null;
        Log log = StaticLog.getLog();

        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            newaccounts = createPC2AccountsFromCLICSAccounts(contest, mapper.readValue(yamlfile, CLICSAccount[].class), site, log);
        } catch (Exception e) {
            log.log(Log.WARNING, "could not deserialize account file " + yamlfile, e);
        }
        return(newaccounts);
     }

    /**
     * Create account list from a string containing accounts json
     *
     * @param contest the contest (needed for groups)
     * @param json json to deserialize
     * @param site the site to create the accounts for
     * @return array of accounts to add, or null on error
     */
    public static Account [] fromJSON(IInternalContest contest, String json, int site) {
        Account [] newaccounts = null;
        Log log = StaticLog.getLog();

        try {
            ObjectMapper mapper = new ObjectMapper();
            newaccounts = createPC2AccountsFromCLICSAccounts(contest, mapper.readValue(json, CLICSAccount[].class), site, log);
        } catch (Exception e) {
            log.log(Log.WARNING, "could not deserialize account string", e);
        }
        return(newaccounts);
     }

    /**
     * Create account list from a string containing accounts yaml
     *
     * @param contest the contest (needed for groups)
     * @param yaml yaml to deserialize
     * @param site the site to create the accounts for
     * @return array of accounts to add, or null on error
     */
    public static Account [] fromYAML(IInternalContest contest, String yaml, int site) {
        Account [] newaccounts = null;
        Log log = StaticLog.getLog();

        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            newaccounts = createPC2AccountsFromCLICSAccounts(contest, mapper.readValue(yaml, CLICSAccount[].class), site, log);
        } catch (Exception e) {
            log.log(Log.WARNING, "could not deserialize account string", e);
        }
        return(newaccounts);
     }

    /**
     * Converts CLICS accounts into a PC2 Account array.
     *
     * @param contest The contest
     * @param accts array of CLICS accounts
     * @param site The site to create accounts on
     * @param log For errors
     * @return an Account array of accounts converted from the CLICS accounts or null if error
     */
    private static Account [] createPC2AccountsFromCLICSAccounts(IInternalContest contest, CLICSAccount [] accts, int site, Log log) {

        Account [] newaccounts = null;
        ArrayList<Account> accounts = new ArrayList<Account>();
        Account account;
        boolean error = false;

        // convert each clics account to a pc2 account, first looking up the existing account, if one exists
        for(CLICSAccount acct: accts) {
            if(StringUtilities.isEmpty(acct.id)) {
                // id is required
                log.log(Log.SEVERE, "unable to get account id");
                error = true;
                break;
            }
            if(StringUtilities.isEmpty(acct.username)) {
                // id is required
                log.log(Log.SEVERE, "unable to get account username");
                error = true;
                break;
            }
            if(StringUtilities.isEmpty(acct.type)) {
                // id is required
                log.log(Log.SEVERE, "unable to get account type");
                error = true;
                break;
            }
            Type acctType = Type.UNKNOWN;
            if(acct.type.equalsIgnoreCase("team")) {
                acctType = Type.TEAM;
            } else if(acct.type.equalsIgnoreCase("admin")) {
                acctType = Type.ADMINISTRATOR;
            } else if(acct.type.equalsIgnoreCase("judge")) {
                acctType = Type.JUDGE;
            } else if(acct.type.equalsIgnoreCase("scoreboard")) {
                acctType = Type.SCOREBOARD;
            } else if(acct.type.equalsIgnoreCase("feeder")) {
                acctType = Type.FEEDER;
            } else {
                log.log(Log.SEVERE, "unknown account type " + acct.type);
                error = true;
                break;
            }
            // break username up into type/number
            ClientId clientId = getClientIdFromUser(acct.username, contest, log);
            if(clientId == null) {
                log.log(Log.SEVERE, "bad username format " + acct.username + "; expecting type###");
                error = true;
                break;
            }
            if(acctType != clientId.getClientType()) {
                log.log(Log.SEVERE, "bad account type " + acctType.toString() + " for user " + acct.username);
                error = true;
                break;
            }
            account = contest.getAccount(clientId);
            if(account == null) {
                log.log(Log.SEVERE, "No account found for id: " + acct.id + " name " + acct.name + " username " + acct.username + " password " + acct.password);
            } else {
                accounts.add(account);
            }
        }
        // if it all worked out, then create the array of accounts to be returned
        if(!error) {
            newaccounts = accounts.toArray(new Account[0]);
        }
        return(newaccounts);
    }

    /**
     * Returns a ClientId based on the user supplied.  eg. "team99", "administrator1", etc.
     * @param user eg. team99
     * @return The ClientId created, or null if the user is bad
     */
    private static ClientId getClientIdFromUser(String user, IInternalContest contest, Log log) {
        ClientId clientId = null;

        // basically, need to match lower case letters followed by digits
        Matcher matcher = Pattern.compile("^([a-z]+)([0-9]+)$").matcher(user);
        if(matcher.matches()) {
            try {
                clientId = new ClientId(contest.getSiteNumber(), ClientType.Type.valueOf(matcher.group(1).toUpperCase()), Integer.parseInt(matcher.group(2)));
            } catch (Exception e) {
                log.log(Log.WARNING, "Can not convert the supplied user " + user + " to a ClientId", e);
            }
        }
        return clientId;
    }
}
