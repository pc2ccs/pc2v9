package edu.csus.ecs.pc2.services.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.exports.ccs.TeamsJSON;

/**
 * WebService for handling teams
 * 
 * @author ICPC
 *
 */
@Path("/teams")
@Produces(MediaType.APPLICATION_JSON)
public class TeamService {

    private IInternalContest contest;
    private IInternalController controller;

    public TeamService(IInternalContest contest, IInternalController controller) {
        super();
        this.contest = contest;
        this.controller = controller;
    }

    /**
     * This method returns a String representation of the current contest teams in JSON format. The returned string is a JSON array with one team description per array element.
     * 
     * @return a String containing the contest teams in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getTeams() {
        TeamsJSON teams = new TeamsJSON();

        String jsonTeams = "[]";
        try {
            jsonTeams = teams.createJSON(contest);
        } catch (IllegalContestState e) {
            controller.getLog().log(Log.WARNING, "Problem creating teams JSON ", e);
            e.printStackTrace();

            // TODO: return HTTP error response code
        }

        // TODO: figure out how to set the Response to "OK" (or whether this is necessary)
        // return Response.status(Response.Status.OK).build();

        // output the response to the requester (note that this actually returns it to Jersey,
        // which forwards it to the caller as the HTTP response).
        return jsonTeams;
    }

}
