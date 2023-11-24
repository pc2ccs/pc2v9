// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

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
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.services.core.JSONUtilities;
import edu.csus.ecs.pc2.services.eventFeed.WebServer;

/**
 * WebService to handle judgements endpoint
 *
 * @author John Buck
 *
 */
@Path("/contests/{contestId}/judgements")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class JudgementService implements Feature {

    private IInternalContest model;

    @SuppressWarnings("unused")
    private IInternalController controller;

    public JudgementService(IInternalContest inContest, IInternalController inController) {
        super();
        this.model = inContest;
        this.controller = inController;
    }

    /**
     * This method returns a representation of judgments for the specified contest in JSON format. The returned value is a JSON array with one judgment description per array element, complying with 2023-06
     *
     * @param sc User's information
     * @param contestId The contest
     * @return a {@link Response} object containing the contest judgments in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJudgements(@Context SecurityContext sc, @PathParam("contestId") String contestId) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == false) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        long freezeTime = Utilities.getFreezeTime(model);
        Set<String> exceptProps = new HashSet<String>();
        StringJoiner allJudgments = new StringJoiner(",");
        ObjectMapper mapper = JSONUtilities.getObjectMapper();
        CLICSRun cRun;

        for (Run run: model.getRuns()) {
            // If not admin or judge, can not see runs after freeze time
            if (!sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) && !sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE)) {
                // if run is after scoreboard freeze, do not return info for it
                if (run.getElapsedMS() / 1000 > freezeTime) {
                    continue;
                }
            }
            exceptProps.clear();
            cRun = new CLICSRun(model, run, exceptProps);
            try {
                // for this judgment, create filter to omit unused/bad properties (max_run_time in this case)
                SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(exceptProps);
                FilterProvider fp = new SimpleFilterProvider().addFilter("rtFilter", filter).setFailOnUnknownId(false);
                // generate json with only properties we want and add to CSV list.
                allJudgments.add(mapper.writer(fp).writeValueAsString(cRun));
            } catch (Exception e) {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for judgment " + run.getElementId().toString() + " " + e.getMessage()).build();
            }
        }
        return Response.ok("[" + allJudgments.toString() + "]", MediaType.APPLICATION_JSON).build();
    }

    /**
     * Returns a representation of a specified judgment for the specified contest in JSON format. The returned value compliant with 2023-06
     *
     * @param sc User's infor
     * @param contestId The contest
     * @param judgementId The judgement we're looking for
     * @return response
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{judgementId}/")
    public Response getJudgement(@Context SecurityContext sc, @PathParam("contestId") String contestId, @PathParam("judgementId") String judgementId) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == true) {
           long freezeTime = Utilities.getFreezeTime(model);
           for(Run run: model.getRuns()) {
                // If not admin or judge, can not see runs after freeze time
                if (!sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) && !sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE)) {
                    // if run is after scoreboard freeze, do not return info for it
                    if (run.getElapsedMS() / 1000 > freezeTime) {
                        continue;
                    }
                }
                // judgementId's match runId's
                if (run.getElementId().toString().equals(judgementId)) {
                    Set<String> exceptProps = new HashSet<String>();
                    CLICSRun cRun = new CLICSRun(model, run, exceptProps);
                    try {
                        ObjectMapper mapper = JSONUtilities.getObjectMapper();
                        // create filter to omit unused/bad properties (location, for example)
                        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(exceptProps);
                        FilterProvider fp = new SimpleFilterProvider().addFilter("rtFilter", filter);
                        String json = mapper.writer(fp).writeValueAsString(cRun);
                        return Response.ok(json, MediaType.APPLICATION_JSON).build();
                    } catch (Exception e) {
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for judgementId " + judgementId + " " + e.getMessage()).build();
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
        return(new CLICSEndpoint("judgements", JSONUtilities.getJsonProperties(CLICSRun.class)));
    }

    /**
     * Retrieve access information about this endpoint for the supplied user's security context
     *
     * @param sc User's security information
     * @return CLICSEndpoint object if the user can access this endpoint's properties, null otherwise
     */
    public static CLICSEndpoint getEndpointProperties(SecurityContext sc) {
        return(new CLICSEndpoint("judgements", JSONUtilities.getJsonProperties(CLICSRun.class)));
    }

    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }
}
