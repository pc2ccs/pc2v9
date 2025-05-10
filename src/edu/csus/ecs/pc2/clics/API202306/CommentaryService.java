// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.services.core.JSONUtilities;
import edu.csus.ecs.pc2.services.eventFeed.WebServer;

/**
 * WebService to handle clarifications.
 *
 * @author John Buck
 *
 */
@Path("/contests/{contestId}/commentary")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class CommentaryService implements Feature {

    private ArrayList<CLICSCommentary> allCommentary = new ArrayList<CLICSCommentary>();
    private CommentaryEntries commentaryEntries;

    private IInternalContest model;

    private IInternalController controller;

    public CommentaryService(IInternalContest inContest, IInternalController inController) {
        super();
        this.model = inContest;
        this.controller = inController;
        try {
            CLICSCommentary comment;
            commentaryEntries = new CommentaryEntries(model);
            for(String ndjsonLine : commentaryEntries.getCommentaryLines()) {
                comment = CLICSCommentary.fromJSON(ndjsonLine);
                if(comment != null) {
                    allCommentary.add(comment);
                }
            }
        } catch(FileNotFoundException e) {
            controller.getLog().info("No commentary file found - one will be created");
        } catch(Exception e) {
            controller.getLog().warning("Can not load commentary file " + e);
        }
    }

    /**
     * Read flat file commentary entries and convert the NDJSON to a CLICS object.
     */
    private void restoreCommentary() {

    }

    /**
     * This method returns a representation of the current contest commentary in JSON format
     * The returned value is a JSON array with one commentary description per array element, complying with 2023-06
     *
     * @param sc security info for the user making the request
     * @param contestId Contest for which info is requested
     * @return a {@link Response} object containing the clarifications in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCommentary(@Context SecurityContext sc, @PathParam("contestId") String contestId) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == false) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // only admin, judge, analyst can get commentary.
        if(!isSubmitCommentaryAllowed(sc)){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            String json = mapper.writeValueAsString(allCommentary);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for commentary " + e.getMessage()).build();
        }
    }


    /**
     * This method returns a representation of the current contest commentary requested in JSON format. The returned value is a single commentary item in json, Complying with 2023-06
     *
     * @param sc security info for the user making the request
     * @param contestId Contest for which info is requested
     * @param commentaryId the id of the desired clarification
     * @return a {@link Response} object containing the clarification in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{commentaryId}/")
    public Response getCommentary(@Context SecurityContext sc, @PathParam("contestId") String contestId, @PathParam("commentaryId") String commentaryId) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == false) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // only admin, judge, analyst can get commentary.
        if(!isSubmitCommentaryAllowed(sc)){
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        // Find the commentary to send back
        for (CLICSCommentary commentary : allCommentary) {
            if(commentary.getId().equals(commentaryId)) {
                try {
                    ObjectMapper mapper = JSONUtilities.getObjectMapper();
                    String json = mapper.writeValueAsString(commentary);
                    return Response.ok(json, MediaType.APPLICATION_JSON).build();
                } catch (Exception e) {
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for commentary " + commentaryId + " " + e.getMessage()).build();
                }
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Post a new commentary item.  (Really, from the Draft 2025-XX spec)
     *
     * @param servletRequest details of request
     * @param sc requesting user's authorization info
     * @param uriInfo requested uri
     * @param contestId The contest
     * @param jsonInputString commentary information.
     * @return Web Response for the commentary, including the (new) id
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public synchronized Response addNewCommentary(@Context HttpServletRequest servletRequest, @Context SecurityContext sc, @Context UriInfo uriInfo, @PathParam("contestId") String contestId, String jsonInputString) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == false) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // only admin, judge, analyst can get commentary.
        if(!isSubmitCommentaryAllowed(sc)){
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        // check for empty request
        if (jsonInputString == null || jsonInputString.length() == 0) {
            // return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("empty json").build();
        }

        CLICSCommentary commentary = CLICSCommentary.fromJSON(jsonInputString);
        if(commentary == null) {
            // return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("invalid json supplied").build();
        }

        // All these properties are determined by the CCS, so the user can not specify them.
        if(commentary.getId() != null) {
            // return HTTP 400 response - can't specify ID
            return Response.status(Status.BAD_REQUEST).entity("may not include id property").build();
        }
        if(commentary.getTime() != null) {
            // return HTTP 400 response - can't specify contest time
            return Response.status(Status.BAD_REQUEST).entity("may not include time property").build();
        }
        if(commentary.getContest_time() != null) {
            // return HTTP 400 response - can't specify time
            return Response.status(Status.BAD_REQUEST).entity("may not include contest_time property").build();
        }
        if(commentary.getSource_id() != null) {
            // return HTTP 400 response - can't specify source_id
            return Response.status(Status.BAD_REQUEST).entity("may not include contest_time property").build();
        }
        if(StringUtilities.isEmpty(commentary.getMessage())) {
            return Response.status(Status.BAD_REQUEST).entity("message must not be empty").build();
        }

        commentary.setSource_id(sc.getUserPrincipal().getName());

        allCommentary.add(commentary);
        commentary.setId("" + allCommentary.size());
        commentary.setTimes(model);

        try {
            // we must generate a "Location" header in the 201 (Created) response which is the
            // uri to the newly created comment.  This is the path of the original post
            // request with the new comment Id tacked on as a new component:
            // eg. https://localhost:50443/contests/SumH/commentary/5
            // In addition, we have to return the newly created object as well (entity below)
            UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
            uriBuilder.path(commentary.getId());
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            String json = mapper.writeValueAsString(commentary);
            if(commentaryEntries != null) {
                commentaryEntries.writeCommentary(json + JSON202306Utilities.NL);
            }
            return Response.created(uriBuilder.build()).entity(json).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for commentary " + e.getMessage()).build();
        }
    }

    /**
     * Tests if the supplied user context has a role to submit clarifications
     *
     * @param sc User's security context
     * @return true of the user can submit clarifications
     */
    public static boolean isSubmitCommentaryAllowed(SecurityContext sc) {
        return(sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) ||
                !sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE) ||
                !sc.isUserInRole(WebServer.WEBAPI_ROLE_ANALYST));
    }

    /**
     * Retrieve access information about this endpoint for the supplied user's security context
     *
     * @param contest The contest is included in case the inclusion of a property depends on the permissions
     *        set for the connected client.  It is included for uniformity since this method is called as a result
     *        of introspection, and the caller does not know what the callee may need.  Therefore, the contest is
     *        always included, as is the SecurityContext below (for the same reason).
     * @param sc User's security information
     * @return CLICSEndpoint object if the user can access this endpoint's properties, null otherwise
     */
    public static CLICSEndpoint getEndpointProperties(IInternalContest contest, SecurityContext sc) {
        return(new CLICSEndpoint("commentary", JSONUtilities.getJsonProperties(CLICSCommentary.class)));
    }

    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }
}
