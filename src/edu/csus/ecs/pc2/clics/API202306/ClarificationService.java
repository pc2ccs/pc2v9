// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.ArrayList;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClarificationAnswer;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.services.core.JSONUtilities;
import edu.csus.ecs.pc2.services.eventFeed.WebServer;

/**
 * WebService to handle languages
 * 
 * @author ICPC
 *
 */
@Path("/contests/{contestId}/clarifications")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class ClarificationService implements Feature {

    private IInternalContest model;

    private IInternalController controller;

    public ClarificationService(IInternalContest inContest, IInternalController inController) {
        super();
        this.model = inContest;
        this.controller = inController;
    }

    /**
     * This method returns a representation of the current contest clarifications in JSON format. The returned value is a JSON array with one clarification description per array element, matching the description
     * at {@link https://clics.ecs.baylor.edu/index.php/Draft_CCS_REST_interface#GET_baseurl.2Flanguages}.
     * 
     * @return a {@link Response} object containing the contest languages in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClarifications(@Context SecurityContext sc, @PathParam("contestId") String contestId) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == false) {
            return Response.status(Response.Status.NOT_FOUND).build();        
        }
        
        // get the groups from the contest
        Clarification[] clarifications = model.getClarifications();
        
        ArrayList<CLICSClarification> clarList = new ArrayList<CLICSClarification>();
        
        // these are the only 2 that have special rules.
        boolean isStaff = sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) || sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE);
        boolean isTeam = sc.isUserInRole(WebServer.WEBAPI_ROLE_TEAM);

        String user = sc.getUserPrincipal().getName();

        // create list of clarifications to send back
        for (Clarification clarification: clarifications) {
            if (clarification.isSendToAll() || isStaff || (isTeam && isClarificationForUser(clarification, user))) {
                clarList.add(new CLICSClarification(model, clarification));
            }
        }
        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            String json = mapper.writeValueAsString(clarList);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for clarifications " + e.getMessage()).build();
        }
    }

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("{clarificationId}/")
    public Response getClarification(@Context SecurityContext sc, @PathParam("contestId") String contestId, @PathParam("clarificationId") String clarificationId) {
        // get the groups from the contest
        Clarification[] clarifications = model.getClarifications();
        
        // these are the only 2 that have special rules.
        boolean isStaff = sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) || sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE);
        boolean isTeam = sc.isUserInRole(WebServer.WEBAPI_ROLE_TEAM);

        String user = sc.getUserPrincipal().getName();

        ClarificationAnswer[] clarAnswers = null;
        
        // keep track of whether the clarificationId specified was for the question, in which case this will
        // be set to non-null
        Clarification clarNoAnswer = null;
        
        // create list of clarifications to send back
        for (Clarification clarification: clarifications) {
            if (clarification.isSendToAll() || isStaff || (isTeam && isClarificationForUser(clarification, user))) {
                if(clarification.getElementId().toString() .equals(clarificationId)) {
                    clarNoAnswer = clarification;
                }
                clarAnswers = clarification.getClarificationAnswers();
                if (clarAnswers != null) {
                    for (ClarificationAnswer clarAns: clarAnswers) {
                        if (clarAns.getElementId().toString().equals(clarificationId)) {
                            try {
                                ObjectMapper mapper = JSONUtilities.getObjectMapper();
                                String json = mapper.writeValueAsString(new CLICSClarification(model, clarification, clarAns));
                                return Response.ok(json, MediaType.APPLICATION_JSON).build();
                            } catch (Exception e) {
                                return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for clarification " + clarificationId + " " + e.getMessage()).build();
                            }
                        }
                    }
                }
            }
        }
        // if set, this means the id of the clarification was specified, not the answer, so return that
        if(clarNoAnswer != null) {
            try {
                ObjectMapper mapper = JSONUtilities.getObjectMapper();
                String json = mapper.writeValueAsString(new CLICSClarification(model, clarNoAnswer, null));
                return Response.ok(json, MediaType.APPLICATION_JSON).build();
            } catch (Exception e) {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for clarification " + clarificationId + " " + e.getMessage()).build();
            }                    
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Check if the supplied clarification is from/to the supplied user
     * 
     * @param clarification the clarification to check
     * @param user the user to check
     * @return true if the user is allowed to see this clarification
     */
    private boolean isClarificationForUser(Clarification clarification, String user) {
        return(clarification.getSubmitter().getName().equals(user));
    }
    
    /**
     * Tests if the supplied user context has a role to submit clarifications as a team
     * 
     * @param sc User's security context
     * @return true of the user can submit clarifications
     */
    public static boolean isTeamSubmitClarificationAllowed(SecurityContext sc) {
        return(sc.isUserInRole(WebServer.WEBAPI_ROLE_TEAM));
    }
    

    /**
     * Tests if the supplied user context has a role to submit clarifications
     * 
     * @param sc User's security context
     * @return true of the user can submit clarifications
     */
    public static boolean isAdminSubmitClarificationAllowed(SecurityContext sc) {
        return(sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN));
    }

    /**
     * Tests if the supplied user context has a role to submit clarifications on behalf of a team
     * 
     * @param sc User's security context
     * @return true of the user can submit clarifications on behalf of a team
     */
    public static boolean isProxySubmitClarificationAllowed(SecurityContext sc) {
        return(sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN));
    }
    
    /**
     * Retrieve access information about this endpoint for the supplied user's security context
     * 
     * @param sc User's security information
     * @return CLICSEndpoint object if the user can access this endpoint's properties, null otherwise
     */
    public static CLICSEndpoint getEndpointProperties(SecurityContext sc) {
        return(new CLICSEndpoint("clarifications", JSONUtilities.getJsonProperties(CLICSClarification.class)));
    }
    
    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }
}
