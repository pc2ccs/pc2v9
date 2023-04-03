// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
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

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.util.CLICSVerionInfo;

/**
 * Web Service to handle "version" REST endpoint as described by the CLICS wiki.
 * 
 * @author pc2@ecs.csus.edu
 *
 */
@Path("/contest/version")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class VersionService implements Feature {

    private IInternalContest model;

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
     * @return a {@link Response} object containing a JSON String 
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
