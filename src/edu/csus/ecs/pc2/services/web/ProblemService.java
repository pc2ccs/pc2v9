package edu.csus.ecs.pc2.services.web;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.util.JSONTool;

/**
 * WebService to handle problems
 * 
 * @author ICPC
 *
 */
@Path("/contest/problems")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class ProblemService implements Feature {

    private IInternalContest model;

    @SuppressWarnings("unused")
    private IInternalController controller;

    private JSONTool jsonTool;

    public ProblemService(IInternalContest inContest, IInternalController inController) {
        super();
        this.model = inContest;
        this.controller = inController;
        jsonTool = new JSONTool(inContest, inController);
    }

    /**
     * This method returns a representation of the current contest problems in JSON format. The returned value is a JSON array with one problems description per array element, matching the description
     * at {@link https://clics.ecs.baylor.edu/index.php/Draft_CCS_REST_interface#.2Fproblems}.
     * 
     * @return a {@link Response} object containing the contest problems in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProblems(@Context SecurityContext sc) {

        // get the problems from the contest
        Problem[] problems = model.getProblems();

        // get an object to map the problems descriptions into JSON form
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode childNode = mapper.createArrayNode();
        // public only gets the problems when the contest starts
        if (!sc.isUserInRole("public") || model.getContestTime().isContestStarted()) {
            for (int i = 0; i < problems.length; i++) {
                Problem problem = problems[i];
                if (problem.isActive()) {
                    childNode.add(jsonTool.convertToJSON(problem, i));
                }
            }
        }
        // output the response to the requester (note that this actually returns it to Jersey,
        // which forwards it to the caller as the HTTP response).
        return Response.ok(childNode.toString(), MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("{problemId}/")
    public Response getProblem(@Context SecurityContext sc, @PathParam("problemId") String problemId) {
        // get the problems from the contest
        Problem[] problems = model.getProblems();

        // get an object to map the problems descriptions into JSON form
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode childNode = mapper.createArrayNode();
        for (int i = 0; i < problems.length; i++) {
            Problem problem = problems[i];
            // match by ID
            if (problem.isActive() && jsonTool.getProblemId(problem).equals(problemId)) {
                childNode.add(jsonTool.convertToJSON(problem, i));
                break;
            }
        }
        return Response.ok(childNode.toString(), MediaType.APPLICATION_JSON).build();

    }

    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }
}
