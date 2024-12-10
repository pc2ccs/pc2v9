// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

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

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * WebService to handle "state" REST endpoint as described by the CLICS wiki.
 * 
 * @author pc2@ecs.csus.edu
 *
 */
@Path("/contests/{contestId}/state")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class StateService implements Feature {

    private IInternalContest model;

    private IInternalController controller;

    public StateService(IInternalContest inModel, IInternalController inController) {
        super();
        this.model = inModel;
        this.controller = inController;
    }

    /**
     * This method returns a representation of the current contest state in JSON format as described on the CLICS wiki.
     * 
     * @param sc user information
     * @param contestId The contest
     * @return a {@link Response} object containing a JSON String giving the scheduled contest start time as a Unix Epoch value, or as the string "undefined" if no start time is currently scheduled.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getState(@Context SecurityContext sc, @PathParam("contestId") String contestId) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == false) {
            return Response.status(Response.Status.NOT_FOUND).build();        
        }
        return Response.ok(new CLICSContestState(model).toJSON(), MediaType.APPLICATION_JSON).build();
    }
    
    /**
     * Retrieve access information about this endpoint for the supplied user's security context
     * 
     * @param sc User's security information
     * @return CLICSEndpoint object if the user can access this endpoint's properties, null otherwise
     */
    public static CLICSEndpoint getEndpointProperties(SecurityContext sc) {
        return(new CLICSEndpoint("state", JSONUtilities.getJsonProperties(CLICSContestState.class)));
    }

    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }
}
