// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Web Service to handle "version" REST endpoint as described by the CLICS wiki.
 * 
 * @author pc2@ecs.csus.edu
 *
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class VersionService implements Feature {

    @SuppressWarnings("unused")
    private IInternalContest model;

    @SuppressWarnings("unused")
    private IInternalController controller;

    private CLICSVerionInfo clicsVerionInfo = new CLICSVerionInfo(new VersionInfo());

    public VersionService(IInternalContest inModel, IInternalController inController) {
        super();
        this.model = inModel;
        this.controller = inController;
    }

    /**
     * This method returns a representation of the current contest version in JSON format as described on the CLICS wiki.
     * 
     * @return a {@link Response} object containing a JSON String with API version information
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVersion() {
        String versionJson = clicsVerionInfo.toJSON();
        return Response.ok(versionJson, MediaType.APPLICATION_JSON).build();
    }

    @Override
    public boolean configure(FeatureContext arg0) {
        return false;
    }
}
