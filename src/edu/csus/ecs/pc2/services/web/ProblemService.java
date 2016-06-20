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
import edu.csus.ecs.pc2.exports.ccs.ProblemsJSON;

/**
 * WebService to handle problems
 * 
 * @author ICPC
 *
 */
@Path("/problems")
@Produces(MediaType.APPLICATION_JSON)
public class ProblemService {

    private IInternalContest contest;
    private IInternalController controller;

    public ProblemService(IInternalContest contest, IInternalController controller) {
        super();
        this.contest = contest;
        this.controller = controller;
    }

    /**
     * This method returns a String representation of the current contest problem set in JSON format. The returned string is a JSON array with one problem description per array element.
     * 
     * @return a String containing the contest problems in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getProblems(@Context SecurityContext sc) {

        // get the problems from the contest
        ProblemsJSON problems = new ProblemsJSON();

        String jsonProblems = "[]";
        try {
            ContestTime contestTime = contest.getContestTime();
            // do not show the problems if the contest has not
            // been started (unless your are an admin)
            if (contestTime.getElapsedMS() > 0 || sc.isUserInRole("admin")) {
                jsonProblems = problems.createJSON(contest);
            }
        } catch (IllegalContestState e) {
            controller.getLog().log(Log.WARNING, "Problem creating problem JSON: " + e, e);
            e.printStackTrace();
            // TODO: return HTTP error response code
        }

        // TODO: figure out how to set the Response to "OK" (or whether this is necessary)
        // return Response.status(Response.Status.OK).build();

        // output the response to the requester (note that this actually returns it to Jersey,
        // which forwards it to the caller as the HTTP response).
        return jsonProblems;
    }

}
