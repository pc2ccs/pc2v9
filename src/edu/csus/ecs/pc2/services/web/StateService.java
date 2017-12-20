package edu.csus.ecs.pc2.services.web;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.util.JSONTool;

/**
 * WebService to handle "state" REST endpoint as described by the CLICS wiki.
 * 
 * @author pc2@ecs.csus.edu
 *
 */
@Path("/contest/state")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class StateService implements Feature {

    private IInternalContest model;

    private IInternalController controller;

    private JSONTool jsonTool;

    public StateService(IInternalContest inModel, IInternalController inController) {
        super();
        this.model = inModel;
        this.controller = inController;
        jsonTool = new JSONTool(model, controller);
    }

    /**
     * This method returns a representation of the current contest state in JSON format as described on the CLICS wiki.
     * 
     * @return a {@link Response} object containing a JSON String giving the scheduled contest start time as a Unix Epoch value, or as the string "undefined" if no start time is currently scheduled.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getState() {
        return Response.ok(jsonTool.toStateJSON(model.getContestInformation()).toString(), MediaType.APPLICATION_JSON).build();
    }

    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }
}
