// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.ArrayList;

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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.util.JSONTool;
import edu.csus.ecs.pc2.services.core.JSONUtilities;
import edu.csus.ecs.pc2.services.eventFeed.WebServer;

/**
 * WebService to handle problems
 * 
 * @author John Buck
 *
 */
@Path("/contests/{contestId}/problems")
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
     * This method returns a representation of the current contest problems in JSON format. The returned value is a JSON array with one problems description per array element, compying with 2023-06
     * 
     * @param sc User information
     * @param contestId The contest
     * @return a {@link Response} object containing the contest problems in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProblems(@Context SecurityContext sc, @PathParam("contestId") String contestId) {

        System.err.println("getProblems from " + sc.getUserPrincipal().getName() + " for contest " + contestId);
        
        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == false) {
            return Response.status(Response.Status.NOT_FOUND).build();        
        }
        // get the problems from the contest
        Problem[] problems = model.getProblems();
        int ord = 1;
        ArrayList<CLICSProblem> problist = new ArrayList<CLICSProblem>();
        
        // public only gets the problems when the contest starts
        if (sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) || sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE) || model.getContestTime().isContestStarted()) {
            for (Problem problem: problems) {
                if (problem.isActive()) {
                    problist.add(new CLICSProblem(model, problem, ord));
                    ord++;
                }
            }
        }
        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            String json = mapper.writeValueAsString(problist);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for problems " + e.getMessage()).build();
        }
    }

    /**
     * Return the reponse to a request for a single problem's information for the specified contest and problem id.
     * 
     * @param sc user info
     * @param contestId the contest
     * @param problemId the problem id desired
     * @return response
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{problemId}/")
    public Response getProblem(@Context SecurityContext sc, @PathParam("contestId") String contestId, @PathParam("problemId") String problemId) {
        // public only gets the problems when the contest starts
        if (sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) || sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE) || model.getContestTime().isContestStarted()) {
            // get the problems from the contest
            Problem[] problems = model.getProblems();
            int ord = 1;
            for (Problem problem: problems) {
                // match by ID
                if (problem.isActive()) {
                    if(JSONTool.getProblemId(problem).equals(problemId)) {
                        try {
                            return Response.ok(JSONUtilities.getObjectMapper().writeValueAsString(new CLICSProblem(model, problem, ord)), MediaType.APPLICATION_JSON).build();
                        } catch(Exception e) {
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for problem " + problemId + " in contest " + contestId + ": " + e.getMessage()).build();                    
                        }
                    }
                    ord++;
                }
            }
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.FORBIDDEN).build();
    }
    
    /**
     * Retrieve access information about this endpoint for the supplied user's security context
     * 
     * @param sc User's security information
     * @return CLICSEndpoint object if the user can access this endpoint's properties, null otherwise
     */
    public static CLICSEndpoint getEndpointProperties(SecurityContext sc) {
        return(new CLICSEndpoint("problems", JSONUtilities.getJsonProperties(CLICSProblem.class)));
    }

    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }
}
