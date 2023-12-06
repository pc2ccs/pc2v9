// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.imports.clics.CLICSAward;
import edu.csus.ecs.pc2.core.imports.clics.CLICSAwardUtilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.services.core.JSONUtilities;
import edu.csus.ecs.pc2.services.eventFeed.WebServer;

/**
 * WebService to handle awards endpoint
 * @author John Buck
 *
 */
@Path("/contests/{contestId}/awards")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class AwardService implements Feature {

    private IInternalContest model;
    @SuppressWarnings("unused")
    private IInternalController controller;

    public AwardService(IInternalContest inContest, IInternalController inController) {
        super();
        this.model = inContest;
        this.controller = inController;
    }

    /**
     * This method returns a representation of the current contest awards in JSON format. 
     * The response is a JSON array with one award description per array element, complying with 2023-06
     * 
     * @param sc Security info for user making request
     * @param contestId The contest id
     * @return a {@link Response} object containing the groups in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAwards(@Context SecurityContext sc, @PathParam("contestId") String contestId) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == false) {
            return Response.status(Response.Status.NOT_FOUND).build();        
        }

        ArrayList<CLICS202306Award> alist = new ArrayList<CLICS202306Award>();
        
        // only admin can see the unfrozen awards at the current time
        boolean obeyFreeze = (sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) == false);
        try {
            // NOTE: CLICSAward is NOT an API specific object,  rather it is a generic object, and must be
            // converted to the specific API version (currently, CLICSAward == CLICS202306Award for the most part,
            // but we want to make this easy to add new CLICS versions in the future).
            List<CLICSAward> genericCLICSAwards = CLICSAwardUtilities.createAwardsList(model, obeyFreeze);
            
            if(genericCLICSAwards != null) {
                // Convert each award to this API's idea of an award
                for(CLICSAward award: genericCLICSAwards) {
                    alist.add(new CLICS202306Award(award.getId(), award.getCitation(), award.getTeam_ids()));
                }
            }
        } catch(Exception e) {
            // some error occurred and we really don't care: it's an error, just log it and return a bad reply
            controller.getLog().log(Log.WARNING, "can not get awards list", e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for awards " + e.getMessage()).build();
        }
        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            String json = mapper.writeValueAsString(alist);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for awards " + e.getMessage()).build();
        }
    }
    

    /**
     * Returns a representation of the specified award id in the specified contestid. 
     * The response is a JSON object describing the award, complying with 2023-06
     * 
     * @param sc Security info for user making request
     * @param contestId The contest
     * @param awardId The desired award in the contest
     * @return a {@link Response} object containing the awards in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{awardId}/")
    public Response getAwards(@Context SecurityContext sc, @PathParam("contestId") String contestId, @PathParam("awardId") String awardId) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == true) {
            // only admin can see the unfrozen awards at the current time
            boolean obeyFreeze = (sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) == false);
            try {
                // NOTE: CLICSAward is NOT an API specific object,  rather it is a generic object, and must be
                // converted to the specific API version (currently, CLICSAward == CLICS202306Award for the most part,
                // but we want to make this easy to add new CLICS versions in the future).
                List<CLICSAward> genericCLICSAwards = CLICSAwardUtilities.createAwardsList(model, obeyFreeze);
                
                if(genericCLICSAwards != null) {
                    // Convert each award to this API's idea of an award
                    for(CLICSAward award: genericCLICSAwards) {
                        if(awardId.equals(award.getId())) {
                            return Response.ok(new CLICS202306Award(awardId, award.getCitation(), award.getTeam_ids()).toJSON(), MediaType.APPLICATION_JSON).build();                            
                        }
                    }
                }
            } catch(Exception e) {
                // some error occurred and we really don't care: it's an error, just log it and return a bad reply
                controller.getLog().log(Log.WARNING, "can not get awards list", e);
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for awards " + e.getMessage()).build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Post a new award.
     * 
     * @param servletRequest details of request
     * @param sc requesting user's authorization info
     * @param contestId The contest
     * @param jsonInputString citation and team_ids json for the new award
     * @return json for the award, including the id
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{contestId}/awards")
    public Response addNewAward(@Context HttpServletRequest servletRequest, @Context SecurityContext sc, @PathParam("contestId") String contestId, String jsonInputString) {
        
        if(sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) == false) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }
    
    /**
     * Put updates an existing award
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
    @Path("{contestId}/awards/{awardId}")
    public Response setAwardInfo(@Context HttpServletRequest servletRequest, @Context SecurityContext sc, @PathParam("contestId") String contestId, @PathParam("awardId") String awardId, String jsonInputString) {
        
        if(sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) == false) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }
    
    /**
     * Patch updates an existing award
     * 
     * @param servletRequest details of request
     * @param sc requesting user's authorization info
     * @param contestId The contest
     * @param jsonInputString citation and team_ids json for the new award
     * @return json for the award, including the id
     */
    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{contestId}/awards/{awardId}")
    public Response updateAwardProperty(@Context HttpServletRequest servletRequest, @Context SecurityContext sc, @PathParam("contestId") String contestId, @PathParam("awardId") String awardId, String jsonInputString) {
        
        if(sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) == false) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }
    
    /**
     * Deletes an existing award
     * 
     * @param servletRequest details of request
     * @param sc requesting user's authorization info
     * @param contestId The contest
     * @param jsonInputString citation and team_ids json for the new award
     * @return Empty string (http status code)
     */
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{contestId}/awards/{awardId}")
    public Response deleteAward(@Context HttpServletRequest servletRequest, @Context SecurityContext sc, @PathParam("contestId") String contestId, @PathParam("awardId") String awardId, String jsonInputString) {
        
        if(sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) == false) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    /**
     * Tests if the supplied user context has a role to update awards
     * (Non-spec compliant as there is no spec for this yet)
     * 
     * @param sc User's security context
     * @return true of the user can modify awards
     */
    public static boolean isAwardsUpdateAllowed(SecurityContext sc) {
        return(sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN));
    }
    
    /**
     * Retrieve access information about this endpoint for the supplied user's security context
     * 
     * @param sc User's security information
     * @return CLICSEndpoint object if the user can access this endpoint's properties, null otherwise
     */
    public static CLICSEndpoint getEndpointProperties(SecurityContext sc) {
        return(new CLICSEndpoint("award", JSONUtilities.getJsonProperties(CLICS202306Award.class)));
    }

    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }
}
