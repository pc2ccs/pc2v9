package edu.csus.ecs.pc2.services.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.exports.ccs.StandingsJSON2016;
/**
 * Webservice to handle scoreboard requests
 * 
 * @author ICPC
 */
@Path("/scoreboard")
@Produces(MediaType.APPLICATION_JSON)
public class ScoreboardService {

    private IInternalContest contest;
    private IInternalController controller;

    public ScoreboardService(IInternalContest inContest, IInternalController inController) {
        super();
        this.contest = inContest;
        this.controller = inController;
    }

    /**
     * This method returns a representation of the current contest scoreboard in JSON format. 
     * The return JSON is in the format defined by {@link StandingsJSON2016#createJSON(IInternalContest)}.
     * 
     * @return a {@link Response} object containing the JSON scoreboard
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getScoreboard(@Context SecurityContext sc) {

        StandingsJSON2016 standings = new StandingsJSON2016();

        String jsonScoreboard = "[]";
        try {
            ContestTime contestTime = contest.getContestTime();
            // verify contest has started or user is an admin
            if (contestTime.getElapsedMS() > 0 || sc.isUserInRole("admin")) {
                //ok to return scoreboard
                jsonScoreboard = standings.createJSON(contest,controller);
            } else {
                // do not show (return) the scoreboard if the contest has not
                // been started and the requester is not an admin)
                // FIXME: might be better if this returned an "empty" scoreboard?
                return Response.status(Status.FORBIDDEN).build();
            }

        } catch (IllegalContestState e) {
            controller.getLog().log(Log.WARNING, "ScoreboardService: problem creating scoreboard JSON:  " + e, e);
            e.printStackTrace();
            //return HTTP error response code
            return Response.serverError().entity(e.getMessage()).build();
        }

        // output the response to the requester (note that this actually returns it to Jersey,
        // which forwards it to the caller as the HTTP response).
        return Response.ok(jsonScoreboard,MediaType.APPLICATION_JSON).build();
    }
}
