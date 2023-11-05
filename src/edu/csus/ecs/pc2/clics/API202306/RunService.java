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
 * WebService to handle runs
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

        ArrayList<CLICSTestCase> tclist = new ArrayList<CLICSTestCase>();

        for (Run run: model.getRuns()) {
            // If not admin or judge, can not see runs after freeze time
            if (!sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) && !sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE)) {
                // if run is after scoreboard freeze, do not return info for it
                if (run.getElapsedMS() / 1000 > freezeTime) {
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
     * @return response
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{runId}/")
    public Response getRun(@Context SecurityContext sc, @PathParam("contestId") String contestId, @PathParam("runId") String runId) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == true) {
            long freezeTime = Utilities.getFreezeTime(model);

            for (Run run: model.getRuns()) {
                // If not admin or judge, can not see runs after freeze time
                if (!sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) && !sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE)) {
                    // if run is after scoreboard freeze, do not return info for it
                    if (run.getElapsedMS() / 1000 > freezeTime) {
                        continue;
                    }
                }
                if(run.isJudged() && !run.getJudgementRecord().isPreliminaryJudgement()) {
                    for(RunTestCase testCase: run.getRunTestCases()) {
                        if (testCase.getElementId().toString().equals(runId)) {
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
        return(new CLICSEndpoint("runs", JSONUtilities.getJsonProperties(CLICSTestCase.class)));
    }

    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }
}
