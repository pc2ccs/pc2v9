package edu.csus.ecs.pc2.services.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.exports.ccs.StandingsJSON;
import edu.csus.ecs.pc2.exports.ccs.StandingsJSON2016;
/*
 * @author ICPC
 *
 */
@Path("/scoreboard")
@Produces(MediaType.APPLICATION_JSON)
public class ScoreboardService {

    private IInternalContest contest;
    private IInternalController controller;

    public ScoreboardService(IInternalContest contest, IInternalController controller) {
        super();
        this.contest = contest;
        this.controller = controller;
    }

    /**
     * This method returns a representation of the current contest scoreboard in JSON format. The return JSON is in the format defined by {@link StandingsJSON#createJSON(IInternalContest)}.
     * 
     * @return a String containing the JSON scoreboard
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getScoreboard(@Context SecurityContext sc) {

        StandingsJSON2016 standings = new StandingsJSON2016();

        String jsonScoreboard = "[]";
        try {
            ContestTime contestTime = contest.getContestTime();
            // do not show the scoreboard if the contest has not
            // been started (unless your are an admin)
            // though really we should show a 0 problem 0 run scoreboard in the else case
            if (contestTime.getElapsedMS() > 0 || sc.isUserInRole("admin")) {
                jsonScoreboard = standings.createJSON(contest,controller);
            }
        } catch (IllegalContestState e) {
            controller.getLog().log(Log.WARNING, "Problem creating scoreboard JSON:  " + e, e);
            e.printStackTrace();
            // TODO: return HTTP error response code
        }

        // TODO: figure out how to set the Response to "OK" (or whether this is necessary)
        // return Response.status(Response.Status.OK).build();

        // output the response to the requester (note that this actually returns it to Jersey,
        // which forwards it to the caller as the HTTP response).
        return jsonScoreboard;
    }
}
