// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.io.IOException;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
import edu.csus.ecs.pc2.services.core.ScoreboardJson;
/**
 * Webservice to handle scoreboard requests
 * 
 * @author ICPC
 */
@Path("/contest/scoreboard")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class ScoreboardService implements Feature {

    private IInternalContest contest;
    private IInternalController controller;

    public ScoreboardService(IInternalContest inContest, IInternalController inController) {
        super();
        this.contest = inContest;
        this.controller = inController;
    }

    /**
     * This method returns a representation of the current contest scoreboard in JSON format. 
     * The return JSON is in the format defined by {@link ScoreboardJson#createJSON(IInternalContest)}.
     * 
     * @return a {@link Response} object containing the JSON scoreboard
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getScoreboard(@Context HttpServletRequest servletRequest, @Context SecurityContext sc) {

        String jsonScoreboard;
        try {
            ContestTime contestTime = contest.getContestTime();
            // verify contest has started or user is an admin
            if (contestTime.getElapsedMS() > 0 || sc.isUserInRole("admin")) {
                //ok to return scoreboard

                try {
                    ScoreboardJson sbJsonObject = new ScoreboardJson();                    
                    jsonScoreboard = sbJsonObject.createJSON(contest, controller.getLog());
                } catch (IllegalContestState | JAXBException | IOException e) {
                    controller.getLog().log(Log.WARNING,"Exception creating PC2 scoreboard JSON: " + e.getMessage(), e);
                    return Response.status(Status.INTERNAL_SERVER_ERROR).build();
                }
                
            } else {
                // do not show (return) the scoreboard if the contest has not
                // been started and the requester is not an admin)
                // FIXME: might be better if this returned an "empty" scoreboard?
                return Response.status(Status.FORBIDDEN).build();
            }

        } catch (Exception e) {
            controller.getLog().log(Log.WARNING, "ScoreboardService: problem creating scoreboard JSON:  " + e, e);
            e.printStackTrace();
            //return HTTP error response code
            return Response.serverError().entity(e.getMessage()).build();
        }

        // output the response to the requester (note that this actually returns it to Jersey,
        // which forwards it to the caller as the HTTP response).
        return Response.ok(jsonScoreboard,MediaType.APPLICATION_JSON).build();
    }

    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }
}
