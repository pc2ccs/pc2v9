package edu.csus.ecs.pc2.services.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.exports.ccs.StandingsJSON2016;

/**
 * WebService to handle scoreboard
 * @author ICPC
 *
 */
@Path("/scoreboard")
@Produces(MediaType.APPLICATION_JSON)
public class ScoreboardService {

    private IInternalContest contest;

    public ScoreboardService(IInternalContest contest, IInternalController controller) {
        super();
        this.contest = contest;
    }

    /**
     * This method returns a representation of the current contest scoreboard in JSON format. The return JSON is in the format defined by {@link StandingsJSON#createJSON(IInternalContest)}.
     * 
     * @return a String containing the JSON scoreboard
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getScoreboard() {

        StandingsJSON2016 standings = new StandingsJSON2016();

        String jsonScoreboard = "{" + "\"scoreboard\"" + ":" + "null" + "}";
        try {
            jsonScoreboard = standings.createJSON(contest);
        } catch (IllegalContestState e) {
            // TODO: log error
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
