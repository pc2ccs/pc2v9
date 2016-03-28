package edu.csus.ecs.pc2.services.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;

/**
 * WebService to handle problems
 * @author ICPC
 *
 */
@Path("/problems")
@Produces(MediaType.APPLICATION_JSON)
public class ProblemService {

    private IInternalContest contest;

    public ProblemService(IInternalContest contest, IInternalController controller) {
        super();
        this.contest = contest;
    }

    /**
     * This method returns a String representation of the current contest problem set in JSON format. The returned string is a JSON array with one problem description per array element.
     * 
     * @return a String containing the contest problems in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getProblems() {

        // get the problems from the contest
        Problem[] problems = contest.getProblems();

        // map the problem descriptions into JSON form
        ObjectMapper mapper = new ObjectMapper();
        String jsonProblems = "{" + "\"problems\"" + ":" + "\"empty\"" + "}";
        try {
            jsonProblems = mapper.writeValueAsString(problems);
        } catch (JsonProcessingException e) {
            // TODO: log exception
            // TODO Auto-generated catch block
            e.printStackTrace();
            // TODO: return HTTP error
        }

        // TODO: figure out how to set the Response to "OK" (or whether this is necessary)
        // return Response.status(Response.Status.OK).build();

        // output the response to the requester (note that this actually returns it to Jersey,
        // which forwards it to the caller as the HTTP response).
        return jsonProblems;

    }

}
