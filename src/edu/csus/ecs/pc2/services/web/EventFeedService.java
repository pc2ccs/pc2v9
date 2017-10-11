package edu.csus.ecs.pc2.services.web;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;

import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.services.core.EventFeedJSON;


/**
 * Implementation of CLICS REST event-feed. 
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
@Path("/event-feed")
@Produces(MediaType.APPLICATION_JSON)
public class EventFeedService {

    private IInternalContest contest;
    private IInternalController controller;
    private Log log;
    
    public EventFeedService(IInternalContest inContest, IInternalController inController) {
        super();
        this.contest = inContest;
        this.controller = inController;
        this.log = inController.getLog();
    }


    /**
     * This method returns a JSON stream representation of the events occurring in the contest. 
     * 
     * @param type a comma-separated query parameter identifying the type(s) of events being requested (if empty or null, indicates ALL event types)
     * @param id the event-id of the earliest event being requested (i.e., an indication of the requested starting point in the event stream)
     * 
     * @return a {@link Response} object whose body contains the JSON event feed
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventFeed(@QueryParam("type") String eventTypeList, @QueryParam("id") String startintEventId, @Context SecurityContext sc) {
        
        String jsonOutput = "[]";
        
        if (eventTypeList != null) {
            System.out.println("debug 22 getEventFeed with event types " + eventTypeList);

            EventFeedJSON eventFeedJSON = new EventFeedJSON();
            eventFeedJSON.setEventTypeList(eventTypeList);

            try {
                jsonOutput = eventFeedJSON.createJSON(contest);
            } catch (IllegalContestState e) {
                //log exception
                controller.getLog().log(Log.WARNING, "Problem creating event feed JSON with events param", e);
                e.printStackTrace(System.err);

                // return HTTP error response code
                return Response.serverError().entity(e.getMessage()).build();
            }

        } else if (startintEventId != null){
            
            EventFeedJSON eventFeedJSON = new EventFeedJSON();
            eventFeedJSON.setStartEventId(startintEventId);

            try {
                jsonOutput = eventFeedJSON.createJSON(contest);
            } catch (IllegalContestState e) {
                //log exception
                controller.getLog().log(Log.WARNING, "Problem creating event feed JSON ", e);
                e.printStackTrace(System.err);

                // return HTTP error response code
                return Response.serverError().entity(e.getMessage()).build();
            }
            
        } else {
            System.out.println("debug 22 getEventFeed () ");

            EventFeedJSON eventFeedJSON = new EventFeedJSON();
            
            try {
                jsonOutput = eventFeedJSON.createJSON(contest);
            } catch (IllegalContestState e) {
                //log exception
                controller.getLog().log(Log.WARNING, "Problem creating event feed JSON ", e);
                e.printStackTrace(System.err);

                // return HTTP error response code
                return Response.serverError().entity(e.getMessage()).build();
            } 
        }
        
        // output the response to the requester (note that this actually returns it to Jersey,
        // which forwards it to the caller as the HTTP response).
        return Response.ok(jsonOutput,MediaType.APPLICATION_JSON)
              .encoding("UTF-8")
              .header("Cache-Control", "no-cache")
              .header("Pragma", "no-cache")
              .build();
    }
    
    
    /**
     * Stream with events.
     * 
     * @param asyncResponse
     * @param servletRequest
     * @throws IOException
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/stream")
    public void streamEventFeed(@Suspended
    final AsyncResponse asyncResponse,
            @Context HttpServletRequest servletRequest) throws IOException {
        final AsyncContext asyncContext = servletRequest.getAsyncContext();
        final ServletOutputStream s = asyncContext.getResponse().getOutputStream();
        final EventFeedStreamer eventFeedSteamer = new EventFeedStreamer(s, contest, controller);
        eventFeedSteamer.writeStartupEvents();
        
        while (true) {
            if (eventFeedSteamer.isFinalized()) {
                break;
            }
            try {
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.log(Level.WARNING, "During sleep " +e.getMessage());
            }
        }
    }

    public void info(String message) {
        System.out.println(new Date () + " " +message);
        if (controller.getLog() != null){
            controller.getLog().log(Level.INFO, message);
        }
    }
 
}
