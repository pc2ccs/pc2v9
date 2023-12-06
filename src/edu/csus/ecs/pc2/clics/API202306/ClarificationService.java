// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.log.Log;
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
     * This method returns a representation of the current contest clarifications in JSON format. The returned value is a JSON array with one clarification description per array element, complying with 2023-06
     * 
     * @param sc security info for the user making the request
     * @param contestId Contest for which info is requested
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


    /**
     * This method returns a representation of the current contest clarification requested in JSON format. The returned value is a single clarification in json, Complying with 2023-06
     * 
     * @param sc security info for the user making the request
     * @param contestId Contest for which info is requested
     * @param clarificationId the id of the desired clarification
     * @return a {@link Response} object containing the contest languages in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
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
     * Post a new clarification
     * 
     * @param servletRequest details of request
     * @param sc requesting user's authorization info
     * @param contestId The contest
     * @param jsonInputString For non-admin, must not include id, to_team_id, time or contest_time.  For admin, must not include id.
     * @return json for the clarification, including the (new) id
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNewClarification(@Context HttpServletRequest servletRequest, @Context SecurityContext sc, @PathParam("contestId") String contestId, String jsonInputString) {
        
        // only admin, team, or judge can create a clarification.
        if(!sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) && !sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE) && !sc.isUserInRole(WebServer.WEBAPI_ROLE_TEAM)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        // check for empty request
        if (jsonInputString == null || jsonInputString.length() == 0) {
            // return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("empty json").build();
        }

        CLICSClarification clar = CLICSClarification.fromJSON(jsonInputString);
        if(clar == null) {
            // return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("invalid json supplied").build();
        }
        if(clar.getId() != null) {
            // return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("may not include id property").build();
        }
        if(StringUtilities.isEmpty(clar.getText())) {
            return Response.status(Status.BAD_REQUEST).entity("text must not be empty").build();
        }
        
        if(!sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) && (clar.getTo_team_id() != null || clar.getTime() != null || clar.getContest_time() != null)) {
            return Response.status(Status.BAD_REQUEST).entity("may not include one or more properties").build();
        }
        
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }
    
    /**
     * Put updates an existing clarification (presumably an answer)
     * 
     * @param servletRequest details of request
     * @param sc requesting user's authorization info
     * @param contestId The contest
     * @param jsonInputString citation and team_ids json for the new award
     * @return json for the award, including the id
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateClarification(@Context HttpServletRequest servletRequest, @Context SecurityContext sc, @PathParam("contestId") String contestId, String jsonInputString) {
        
        // only admin or judge can update a clarification.
        if(!sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) && !sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        // check for empty request
        if (jsonInputString == null || jsonInputString.length() == 0) {
            // return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("empty json").build();
        }

        CLICSClarification clar = CLICSClarification.fromJSON(jsonInputString);
        if(clar == null) {
            // return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("invalid json supplied").build();
        }
        
        if(StringUtilities.isEmpty(clar.getText())) {
            return Response.status(Status.BAD_REQUEST).entity("text must not be empty").build();
        }
        
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
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

    /**
     * Converts the input string, assumed to be a JSON string, into a {@link Map<String,String>} of JSON key-value pairs.
     * 
     * @param contestId contest identifier
     * @param jsonRequestString
     *            a JSON string specifying a starttime request in CLICS format
     * @return a Map of the JSON string key-to-value pairs as Strings, or null if the input JSON does not parse as a Map(String->String).
     */
    private Map<String, String> parseJSONIntoMap(String contestId, String jsonRequestString) {

        // use Jackson's ObjectMapper to construct a Map of Strings-to-Strings from the JSON input
        final ObjectMapper mapper = new ObjectMapper();
        final MapType mapType = mapper.getTypeFactory().constructMapType(Map.class, String.class, String.class);
        final Map<String, String> jsonDataMap;

        try {
            jsonDataMap = mapper.readValue(jsonRequestString, mapType);
        } catch (JsonMappingException e) {
            // error parsing JSON input
            controller.getLog().log(Log.WARNING, contestId + ": parseJSONIntoMap(): JsonMappingException parsing JSON input '" + jsonRequestString + "'", e);
            return null;
        } catch (IOException e) {
            controller.getLog().log(Log.WARNING, contestId + ": parseJSONIntoMap(): IOException parsing JSON input '" + jsonRequestString + "'", e);
            return null;
        }

        return jsonDataMap;
    }
    
    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }
}
