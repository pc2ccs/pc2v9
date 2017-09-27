package edu.csus.ecs.pc2.services.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.services.core.EventFeedJSON;


// TODO remove these reference imports
//import javax.ws.rs.GET;
//import javax.ws.rs.Path;
//import javax.ws.rs.Produces;
//import javax.ws.rs.QueryParam;
//import javax.ws.rs.core.Context;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//import javax.ws.rs.core.Response.Status;
//import javax.ws.rs.core.SecurityContext;
//
//import edu.csus.ecs.pc2.core.IInternalController;
//import edu.csus.ecs.pc2.core.log.Log;
//import edu.csus.ecs.pc2.core.model.ClientId;
//import edu.csus.ecs.pc2.core.model.IInternalContest;
//import edu.csus.ecs.pc2.core.model.IRunListener;
//import edu.csus.ecs.pc2.core.model.Run;
//import edu.csus.ecs.pc2.core.model.RunEvent;
//import edu.csus.ecs.pc2.core.model.RunEvent.Action;
//import edu.csus.ecs.pc2.core.model.RunFiles;
//import edu.csus.ecs.pc2.exports.ccs.RunFilesJSON;

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
    public Response getEventFeed(@Context SecurityContext sc) {
        
        if (!sc.isUserInRole("admin")) {
            // do not return runs if the requestor is not an admin  (this might need to be relaxed later, e.g. by including wider credential...)
            return Response.status(Status.FORBIDDEN).build();
        }
        
        EventFeedJSON eventFeedJSON = new EventFeedJSON();

        String outputJson = "[]";
        try {
            outputJson = eventFeedJSON.createJSON(contest);
        } catch (IllegalContestState e) {
            //log exception
            controller.getLog().log(Log.WARNING, "Problem creating event feed JSON ", e);
            e.printStackTrace(System.err);

            // return HTTP error response code
            return Response.serverError().entity(e.getMessage()).build();
        }
        
        // output the response to the requester (note that this actually returns it to Jersey,
        // which forwards it to the caller as the HTTP response).
        return Response.ok(outputJson,MediaType.APPLICATION_JSON).build();
    }
}
