package edu.csus.ecs.pc2.services.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
 * Implementation of CCS REST event-feed. 
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
@Path("/event-feed")
@Produces(MediaType.APPLICATION_JSON)
public class EventFeedService {

    private IInternalContest contest;
    private IInternalController controller;
    
    public EventFeedService(IInternalContest inContest, IInternalController inController) {
        super();
        this.contest = inContest;
        this.controller = inController;
        
    }

    /**
     * This method returns a JSON representation of the submitted run identified by {id}. 
     * 
     * @return a {@link Response} object containing the submitted run in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventFeed(@QueryParam("events") String eventList, @QueryParam("id") String eventId, @Context SecurityContext sc) {
        
        String jsonOutput = "[]";
        
        if (eventList != null) {
            System.out.println("debug 22 getEventFeed with events "+eventList);

            EventFeedJSON eventFeedJSON = new EventFeedJSON();
            eventFeedJSON.setEventFeedList(eventList);

            try {
                jsonOutput = eventFeedJSON.createJSON(contest);
            } catch (IllegalContestState e) {
                //log exception
                controller.getLog().log(Log.WARNING, "Problem creating event feed JSON with events param", e);
                e.printStackTrace(System.err);

                // return HTTP error response code
                return Response.serverError().entity(e.getMessage()).build();
            }

        } else if (eventId != null){
            
            EventFeedJSON eventFeedJSON = new EventFeedJSON();
            eventFeedJSON.setStartEventId(eventId);

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
        return Response.ok(jsonOutput,MediaType.APPLICATION_JSON).build();
    }
}
