// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.

/**
 * Interface that all EF Streamers must implement
 * @author John Buck
 */
package edu.csus.ecs.pc2.services.web;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * @author John Buck
 *
 */
public interface IEventFeedStreamer {
    public String getEventFeedJSON(IInternalContest contest, IInternalController controller, HttpServletRequest servletRequest, SecurityContext sc);
}
