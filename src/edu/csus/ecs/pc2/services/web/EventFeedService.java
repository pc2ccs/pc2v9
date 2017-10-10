package edu.csus.ecs.pc2.services.web;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.logging.Level;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.StreamingOutput;

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
     * Streaming event feed.
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/stream")
    public Response streamEventFeed() {

        StreamingOutput stream = new StreamingOutput() {
            
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                
                /**
                 * An assumption here is that the  StreamingOutput and this response is on its own 
                 * thread. 
                 */
                try {
                    
                    EventFeedStreamer eventFeedSteamer = new EventFeedStreamer(os, contest, controller);
                    
                    // Start by sending any existing events to stream
                    
                    eventFeedSteamer.writeStartupEvents();
                    
                    /**
                     * Start new thread for eventFeedSteamer instance.
                     */
                    new Thread(eventFeedSteamer).start();
                    
                    
                } catch (Exception e) {
                    System.err.println("Warning (check log) in write " + e.getMessage());
                    log.log(Level.WARNING,"Exception in streaming event feed write "+e.getMessage());
                }


                System.out.println("debug 22 DONE "+new Date());
            }
        };

        return Response.ok(stream).build();
    }
    
    public void info(String message) {
        
        System.out.println(new Date () + " " +message);
        if (controller.getLog() != null){
            controller.getLog().log(Level.INFO, message);
        }
    }
    
}
