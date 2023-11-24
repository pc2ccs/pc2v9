// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.io.IOException;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBException;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.services.core.JSONUtilities;
import edu.csus.ecs.pc2.services.core.ScoreboardJson;
import edu.csus.ecs.pc2.services.eventFeed.WebServer;
/**
 * Webservice to handle scoreboard requests
 * 
 * @author ICPC
 */
@Path("/contests/{contestId}/scoreboard")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class ScoreboardService implements Feature {

    private IInternalContest model;
    private IInternalController controller;

    public ScoreboardService(IInternalContest inContest, IInternalController inController) {
        super();
        this.model = inContest;
        this.controller = inController;
    }

    /**
     * This method returns a representation of the current contest scoreboard in JSON format. 
     * The return JSON is in the format defined by the CLICS 2023-06 spec.
     * 
     * @param servletRequest
     * @param sc
     * @param contestId
     * @param group_id - optional group id query param
     * @return {@link Response} object containing the JSON scoreboard
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getScoreboard(@Context HttpServletRequest servletRequest, @Context SecurityContext sc, @PathParam("contestId") String contestId, @QueryParam("group_id") String group_id) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == true) {
            ContestTime contestTime = model.getContestTime();
            // verify contest has started or user is special
            if (contestTime.getElapsedMS() > 0 ||
                ((sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) ||
                  sc.isUserInRole(WebServer.WEBAPI_ROLE_ANALYST) ||
                  sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE)))) {
                
                // ok to return scoreboard
                try {
                    CLICSScoreboard scoreboard = new CLICSScoreboard(model, controller);                    
                    return Response.ok(scoreboard.toJSON(), MediaType.APPLICATION_JSON).build();
                } catch (IllegalContestState | JAXBException | IOException e) {
                    controller.getLog().log(Log.WARNING, "Exception creating PC2 scoreboard JSON: " + e.getMessage(), e);
                    return Response.status(Status.INTERNAL_SERVER_ERROR).build();
                }
                
            } else {
                // do not show (return) the scoreboard if the contest has not
                // been started and the requester is not special)
                // FIXME: might be better if this returned an "empty" scoreboard?
                return Response.status(Status.FORBIDDEN).build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();        
    }
    
    /**
     * Retrieve access information about this endpoint for the supplied user's security context
     * 
     * @param sc User's security information
     * @return CLICSEndpoint object if the user can access this endpoint's properties, null otherwise
     */
    public static CLICSEndpoint getEndpointProperties(SecurityContext sc) {
        return(new CLICSEndpoint("scoreboard", JSONUtilities.getJsonProperties(CLICSScoreboard.class)));
    }

    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }
}
