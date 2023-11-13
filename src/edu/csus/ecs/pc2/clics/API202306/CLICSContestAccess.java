// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.

import java.util.ArrayList;

package edu.csus.ecs.pc2.clics.API202306;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * CLICS Contest State
 * 
 * @author John Buck
 *
 */
public class CLICSContestAccess {

    // capabilities for API 2023-06
    public static final String API_CAPABILITY_CONTEST_START = "contest_start";
    public static final String API_CAPABILITY_CONTEST_THAW = "contest_thaw";
    public static final String API_CAPABILITY_TEAM_SUBMIT = "team_submit";
    public static final String API_CAPABILITY_TEAM_CLAR = "team_clar";
    public static final String API_CAPABILITY_PROXY_SUBMIT = "proxy_submit";
    public static final String API_CAPABILITY_PROXY_CLAR = "proxy_clar";
    public static final String API_CAPABILITY_ADMIN_SUBMIT = "admin_submit";
    public static final String API_CAPABILITY_ADMIN_CLAR = "admin_clar";

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
        ArrayList<String> cap = new ArrayList<string>();
        
        if(ContestService.isContestStartAllowed(sc)) {
            cap.add(API_CAPABILITY_CONTEST_START);
        }
        if(ContestService.isContestThawAllowed(sc)) {
            cap.add(API_CAPABILITY_CONTEST_THAW);
        }
        if(SubmissionService.isTeamSubmitAllowed(sc)) {
            cap.add(API_CAPABILITY_TEAM_SUBMIT);
        }
        if(ClarificationService.isTeamClarificationAllowed(sc)) {
            cap.add(API_CAPABILITY_TEAM_CLAR);
        }
        if(SubmissionService.isProxySubmitAllowed(sc)) {
            cap.add(API_CAPABILITY_PROXY_SUBMIT);
        }
        if(ClarificationService.isTeamClarificationAllowed(sc)) {
            cap.add(API_CAPABILITY_PROXY_CLAR);
        }
        if(SubmissionService.isAdminSubmitAllowed(sc)) {
            cap.add(API_CAPABILITY_ADMIN_SUBMIT);
        }
        if(ClarificationService.isAdminClarificationAllowed(sc)) {
            cap.add(API_CAPABILITY_ADMIN_CLAR);
        }
        capabilities = cap.toArray(new String[0]);
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
