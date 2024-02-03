// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202003;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;

import javax.inject.Singleton;
import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.services.web.EventFeedFilter;
import edu.csus.ecs.pc2.services.web.EventFeedStreamer;

/**
 * Implementation of CLICS REST event-feed.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
@Path("/contest/event-feed")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class EventFeedService implements Feature {

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    /**
     * Streamer that sends all JSON to clients (and sends keep alive).
     */
    private static EventFeedStreamer eventFeedSteamer;

    public EventFeedService(IInternalContest inContest, IInternalController inController) {
        super();
        this.contest = inContest;
        this.controller = inController;
        this.log = inController.getLog();
    }

    /**
     * a JSON stream representation of the events occurring in the contest.
     * 
     * @param type
     *            a comma-separated query parameter identifying the type(s) of events being requested (if empty or null, indicates ALL event types)
     * @param id
     *            the event-id of the earliest event being requested (i.e., an indication of the requested starting point in the event stream)
     * 
     * @return a {@link Response} object whose body contains the JSON event feed
     * @param asyncResponse
     * @param servletRequest
     * @throws IOException
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public void streamEventFeed(@QueryParam("types") String eventTypeList, @QueryParam("id") String startintEventId, @Suspended
    final AsyncResponse asyncResponse, @Context HttpServletRequest servletRequest, @Context HttpServletResponse response, @Context SecurityContext sc) throws IOException {

        response.setContentType("json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");

        final AsyncContext asyncContext = servletRequest.getAsyncContext();
        final ServletOutputStream servletOutputStream = asyncContext.getResponse().getOutputStream();

        if (eventFeedSteamer == null) {
            eventFeedSteamer = new EventFeedStreamer(contest, controller, servletRequest, sc);
        }

        EventFeedFilter filter = new EventFeedFilter();
        
        if (eventTypeList != null) {
            filter.addEventTypeList(eventTypeList);
            System.out.println("starting event feed, sending only event types '" + eventTypeList + "'");
        }

        if (startintEventId != null) {
            if (startintEventId.startsWith("pc2-") && Utilities.isIntegerNumber(startintEventId.substring(4))) {
                filter.addStartintEventId(startintEventId);
                System.out.println("starting event feed, Feed starting after id " + startintEventId);
            } else {
                System.err.println("NOT starting event feed (invalid startingEventId "+startintEventId+")");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid id: `"+startintEventId+"`");
                return;
            }
        } else {
            System.out.println("starting event feed (no args) ");
        }
        filter.setClient(servletRequest.getRemoteUser() + "@" + servletRequest.getRemoteAddr() + ":" + servletRequest.getRemotePort());

        /**
         * Add stream and write past events to stream.
         */
        eventFeedSteamer.addStream(servletOutputStream, filter);

        if (!eventFeedSteamer.isRunning()) {
            /**
             * Put on thread if not running on a thread.
             */
            new Thread(eventFeedSteamer).start();
        }

        while (true) {
            if (eventFeedSteamer.isFinalized()) {
                break;
            }
            try {
                Thread.sleep(1 * Constants.MS_PER_SECOND);
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.log(Level.WARNING, "During sleep " + e.getMessage());
            }
        }
    }

    public void info(String message) {
        System.out.println(new Date() + " " + message);
        if (controller.getLog() != null) {
            controller.getLog().log(Level.INFO, message);
        }
    }

    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }
    
    /**
     * Create a snapshot of the JSON event feed.
     * 
     * @param contest
     * @param controller
     * @return
     */
    public static String createEventFeedJSON(IInternalContest contest, IInternalController controller, HttpServletRequest servletRequest, SecurityContext sc) {
        EventFeedStreamer streamer = new EventFeedStreamer(contest, controller, servletRequest, sc);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        streamer.addStream(stream, new EventFeedFilter());
        streamer.removeStream(stream);
        String json = new String(stream.toByteArray());
        stream = null;
        streamer = null;
        return json;
    }

}
