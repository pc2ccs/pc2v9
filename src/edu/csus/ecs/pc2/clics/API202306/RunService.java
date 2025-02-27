// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunTestCase;
import edu.csus.ecs.pc2.services.core.JSONUtilities;
import edu.csus.ecs.pc2.services.eventFeed.WebServer;

/**
 * WebService to handle CLICS runs, which are PC2 RunTestCase objects.
 *
 * @author John Buck
 *
 */
@Path("/contests/{contestId}/runs")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class RunService implements Feature {

    private IInternalContest model;

    @SuppressWarnings("unused")
    private IInternalController controller;

    public RunService(IInternalContest inContest, IInternalController inController) {
        super();
        this.model = inContest;
        this.controller = inController;
    }

    /**
     * Returns a representation of the current contest runs (testcases) in JSON format. The returned value is a JSON array with one run per array element, matching the 2023-06 API.
     * @param sc User's info
     * @param contestId The contest
     * @return a {@link Response} object containing the contest runs in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRuns(@Context SecurityContext sc, @PathParam("contestId") String contestId) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == false) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        long freezeTime = Utilities.getFreezeTime(model);
        boolean isAdmin = sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN);
        boolean isJudge = sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE);
        boolean isAnalyst = sc.isUserInRole(WebServer.WEBAPI_ROLE_ANALYST);

        ArrayList<CLICSTestCase> tclist = new ArrayList<CLICSTestCase>();

        for (Run run: model.getRuns()) {
            // Admins can see test cases all the time.
            if (!isAdmin && !isJudge){
                // If not an analyst, or is an analyst but after freeze: you can't see test cases
                if(!isAnalyst || run.getElapsedMS()/1000 > freezeTime) {
                    continue;
                }
            }
            if(run.isJudged() && !run.getJudgementRecord().isPreliminaryJudgement()) {
                for(RunTestCase testCase: run.getRunTestCases()) {
                    tclist.add(new CLICSTestCase(model, testCase));
                }
            }
        }
        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            String json = mapper.writeValueAsString(tclist);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for Runs testcases " + e.getMessage()).build();
        }
    }

    /**
     * Returns a representation of the specified test case in the specified contest in JSON format. The returned value is compliant with 2023-06 API.
     *
     * @param sc User's info
     * @param contestId The contest
     * @param runId The run of interest
     * @return response containing the test case's information in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{runId}/")
    public Response getRun(@Context SecurityContext sc, @PathParam("contestId") String contestId, @PathParam("runId") String runId) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == true) {
            long freezeTime = Utilities.getFreezeTime(model);
            boolean isAnalyst = sc.isUserInRole(WebServer.WEBAPI_ROLE_ANALYST);

            // Only admins, judges and analysts can see run test cases
            if(!sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) && !sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE) && !isAnalyst) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            for (Run run: model.getRuns()) {
                if(run.isJudged() && !run.getJudgementRecord().isPreliminaryJudgement()) {
                    for(RunTestCase testCase: run.getRunTestCases()) {
                        if (testCase.getElementId().toString().equals(runId)) {
                            // If an analyst can not see runs after freeze time
                            if (isAnalyst && run.getElapsedMS() / 1000 > freezeTime) {
                                return Response.status(Response.Status.FORBIDDEN).build();
                            }
                            return Response.ok(new CLICSTestCase(model, testCase).toJSON(), MediaType.APPLICATION_JSON).build();
                        }
                    }
                }
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Retrieve access information about this endpoint for the supplied user's security context
     *
     * @param sc User's security information
     * @return CLICSEndpoint object if the user can access this endpoint's properties, null otherwise
     */
    public static CLICSEndpoint getEndpointProperties(SecurityContext sc) {
        String [] props;

        if(sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) || sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE) || sc.isUserInRole(WebServer.WEBAPI_ROLE_ANALYST)){
            props = JSONUtilities.getJsonProperties(CLICSAccount.class);
        } else {
            // If you're not admin, judge or analyst, you can't see any properties for run test cases
            props = new String[0];
        }
        return(new CLICSEndpoint("runs", props));
    }

    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }
}
