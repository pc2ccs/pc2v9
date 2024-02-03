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

/**
 * WebService to handle "state" REST endpoint as described by the CLICS wiki.
 * 
 * @author John Buck
 *
 */
@Path("/contests/{contestId}/access")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class AccessService implements Feature {

    private IInternalContest model;

    private IInternalController controller;

    public AccessService(IInternalContest inModel, IInternalController inController) {
        super();
        this.model = inModel;
        this.controller = inController;
    }

    /**
     * This method returns a representation of the access that the connected user has
     * 
     * @return a {@link Response} object containing a JSON String giving the access information for the connected user
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccess(@Context SecurityContext sc, @PathParam("contestId") String contestId) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == true) {
            return Response.ok(new CLICSContestAccess(sc, model, controller, contestId).toJSON(), MediaType.APPLICATION_JSON).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();        
    }

    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }
}
