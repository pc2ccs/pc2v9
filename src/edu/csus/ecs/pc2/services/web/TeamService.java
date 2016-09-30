package edu.csus.ecs.pc2.services.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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

    public TeamService(IInternalContest inContest, IInternalController inController) {
        super();
        this.contest = inContest;
        this.controller = inController;
    }

    /**
     * This method returns a representation of the current contest teams in JSON format. 
     * The returned value is a JSON array with one team description per array element.
     * 
     * @return a {@link Response} object containing the contest teams in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTeams() {
        
        TeamsJSON teams = new TeamsJSON();

        String jsonTeams = "[]";
        try {
            jsonTeams = teams.createJSON(contest);
        } catch (IllegalContestState e) {
            //log exception
            controller.getLog().log(Log.WARNING, "TeamService: problem creating teams JSON ", e);
            e.printStackTrace();

            // return HTTP error response code
            return Response.serverError().entity(e.getMessage()).build();
        }

        // output the response to the requester (note that this actually returns it to Jersey,
        // which forwards it to the caller as the HTTP response).
        return Response.ok(jsonTeams,MediaType.APPLICATION_JSON).build();
    }

}
