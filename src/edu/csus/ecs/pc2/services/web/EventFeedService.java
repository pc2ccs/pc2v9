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

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;

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
     * @param type a comma-separated query parameter identifying the type(s) of events being requested (if empty or null, indicates ALL event types)
     * @param id the event-id of the earliest event being requested (i.e., an indication of the requested starting point in the event stream)
     * 
     * @return a {@link Response} object whose body contains the JSON event feed
     * @param asyncResponse
     * @param servletRequest
     * @throws IOException
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public void streamEventFeed(
            @QueryParam("type") String eventTypeList, 
            @QueryParam("id") String startintEventId, 
            @Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest servletRequest) throws IOException {
        
        final AsyncContext asyncContext = servletRequest.getAsyncContext();
        final ServletOutputStream s = asyncContext.getResponse().getOutputStream();
        
        if (eventFeedSteamer == null){
            eventFeedSteamer = new EventFeedStreamer(s, contest, controller);
        }
        
        EventFeedFilter filter = new EventFeedFilter();
        
        if (eventTypeList != null) {
            filter.addEventTypeList(eventTypeList);
            System.out.println("starting event feed, sending only event types '"+eventTypeList+"'");
            eventFeedSteamer.setEventTypeList(eventTypeList);
        } 
        
        if (startintEventId != null){
            filter.addStartintEventId(startintEventId);
            System.out.println("starting event feed, Feed starting at id "+startintEventId);
            eventFeedSteamer.setStartEventId(startintEventId);
            
        } else {
            System.out.println("starting event feed (no args) ");
        }
        
        /**
         * Write past events to stream.
         */
        eventFeedSteamer.writeStartupEvents(s);       
        
        eventFeedSteamer.addStream(s, filter);
        
        if (! eventFeedSteamer.isRunning()){
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
                log.log(Level.WARNING, "During sleep " +e.getMessage());
            }
        }
     
        // SOMEDAY Do we need some sort of reponse code for this stream?
        
//        // output the response to the requester (note that this actually returns it to Jersey,
//        // which forwards it to the caller as the HTTP response).
//        return Response.ok(jsonOutput,MediaType.APPLICATION_JSON)
//              .encoding("UTF-8")
//              .header("Cache-Control", "no-cache")
//              .header("Pragma", "no-cache")
//              .build();
    }

    public void info(String message) {
        System.out.println(new Date () + " " +message);
        if (controller.getLog() != null){
            controller.getLog().log(Level.INFO, message);
        }
    }
 
}
