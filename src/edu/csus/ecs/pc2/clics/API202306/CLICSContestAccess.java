// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.

package edu.csus.ecs.pc2.clics.API202306;

import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.ws.rs.core.SecurityContext;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
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
    // tentative - not official, but mentioned in the spec
    public static final String API_CAPABILITY_AWARDS_UPDATE = "awards_update";

    public static final String ENDPOINT_ACCESS_METHOD = "getEndpointProperties";
    
    @JsonProperty
    private String [] capabilities;

    @JsonProperty
    private CLICSEndpoint [] endpoints;

    IInternalController controller = null;
       
    /**
     * Fill in properties for contest state as per 2023-06 spec
     * 
     * @param sc security info for user making request
     * @param model
     * @param controller
     * @param contestId
     */
    public CLICSContestAccess(SecurityContext sc, IInternalContest model, IInternalController controller, String contestId) {
        this.controller = controller;
        
        // For each role the connected user has, we enumerate what they can do with each endpoint.
        ArrayList<String> cap = new ArrayList<String>();
        
        if(ContestService.isContestStartAllowed(sc)) {
            cap.add(API_CAPABILITY_CONTEST_START);
        }
        if(ContestService.isContestThawAllowed(sc)) {
            cap.add(API_CAPABILITY_CONTEST_THAW);
        }
        if(SubmissionService.isTeamSubmitAllowed(sc)) {
            cap.add(API_CAPABILITY_TEAM_SUBMIT);
        }
        if(ClarificationService.isTeamSubmitClarificationAllowed(sc)) {
            cap.add(API_CAPABILITY_TEAM_CLAR);
        }
        if(SubmissionService.isProxySubmitAllowed(sc)) {
            cap.add(API_CAPABILITY_PROXY_SUBMIT);
        }
        if(ClarificationService.isProxySubmitClarificationAllowed(sc)) {
            cap.add(API_CAPABILITY_PROXY_CLAR);
        }
        if(SubmissionService.isAdminSubmitAllowed(sc)) {
            cap.add(API_CAPABILITY_ADMIN_SUBMIT);
        }
        if(ClarificationService.isAdminSubmitClarificationAllowed(sc)) {
            cap.add(API_CAPABILITY_ADMIN_CLAR);
        }
        if(AwardService.isAwardsUpdateAllowed(sc)) {
            cap.add(API_CAPABILITY_AWARDS_UPDATE);
        }
        capabilities = cap.toArray(new String[0]);
        
        /*
         * As per the spec, here are the possible endpoints.
         * These should match the class names in this package, eg. Contest -> ContestService, Run -> RunService, etc.
         */
        String [] serviceNames = { "Contest", "JudgementType", "Language", "Problem", "Group", "Organization",
                "Team", "Person", "Account", "State", "Submission", "Judgement", "Run", "Clarification",
                "Scoreboard", "EventFeed", "Award", "Commentary"
        };
        
        ArrayList<CLICSEndpoint> epList = new ArrayList<CLICSEndpoint>();
        
        for (String service: serviceNames) {
            if(!invokeServiceEndpointAccessMethod(epList, service, sc)) {
                System.err.println("No endpoing access for service " + service);
                controller.getLog().log(Log.WARNING, "No endpoint access for service " + service);
            }
        }
        if(!epList.isEmpty()) {
            endpoints = epList.toArray(new CLICSEndpoint[0]);
        }
    }

    /**
     * Create a fully qualified class name for the supplied web service endpoint.
     * eg. Group would create: edu.csus.ecs.pc2clics.API202306.GroupService
     * Then call its getEndpointProperties(SecurityContext) static method to see what properties the
     * service supports for the supplied SecurityContext (user).
     * Note: If Java reflection makes you queasy, please stop here and just skip reading the code in this method.
     * 
     * @param epList List to add the CLICSEndpointObject to, if one was returned
     * @param serviceName The service name only (excluding the trailing "Service")
     * @param sc User's security context
     * @return true if this endpoint is accessible by the user in the SecurityContext
     */
    private boolean invokeServiceEndpointAccessMethod(ArrayList<CLICSEndpoint> epList, String serviceName, SecurityContext sc) {

        Class c = this.getClass();
        String className = c.getPackage().getName() + "." + serviceName + "Service";
        try {
            Class<?> newClass = Class.forName(className);
            try {
                Method m = newClass.getMethod(ENDPOINT_ACCESS_METHOD, SecurityContext.class);
                if(m != null) {
                    // invoke static method with the SecurityContext as an argument
                    Object epRet = m.invoke(null, sc);
                    if(epRet instanceof CLICSEndpoint) {
                        // we got one back, so add it to the list
                        CLICSEndpoint ep = (CLICSEndpoint)epRet;
                        epList.add(ep);
                        return(true);
                    } else {
                        controller.getLog().log(Log.WARNING, "CLICS Service class " + className + "." + ENDPOINT_ACCESS_METHOD + "(SecurityContext) does not return a CLICSEndpoint");                    
                    }
                }
            } catch (Exception eMethod) {
                controller.getLog().log(Log.WARNING, "Unable to invoke method " + className + "." + ENDPOINT_ACCESS_METHOD + "(SecurityContext)");
            }
        } catch (Exception e) {
            controller.getLog().log(Log.WARNING, "Unable to load CLICS Service class " + className);
        }

        return false;
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
