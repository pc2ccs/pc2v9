// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.

/**
 * Interface that all API web servers must implement
 * @author John Buck
 */
package edu.csus.ecs.pc2.services.web;

import org.glassfish.jersey.server.ResourceConfig;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.services.eventFeed.WebServerPropertyUtils;

/**
 * @author john
 *
 */
public interface ICLICSResourceConfig {
    /**
     * This method constructs a Jersey {@link ResourceConfig} containing a Resource (Service class) for each REST service marked as "enabled" by the user on the WebServerPane GUI. Each Resource is
     * constructed with the current contest and controller so that it has access to the contest data.
     * 
     * @param aContest The contest
     * @param aController The controller
     * @param wsPropUtils Property manager utilities for web server
     * 
     * @return a ResourceConfig containing the enabled REST service resources
     */
    public ResourceConfig getResourceConfig(IInternalContest aContest, IInternalController aController, WebServerPropertyUtils wsPropUtil);

}
