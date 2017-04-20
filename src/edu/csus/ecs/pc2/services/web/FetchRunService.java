package edu.csus.ecs.pc2.services.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;


/**
 * WebService for handling fetch run requests.
 * 
 * @author john
 *
 */
@Path("/submission/{id}")
@Produces(MediaType.APPLICATION_JSON)
public class FetchRunService {

//    private IInternalContest contest;
//    private IInternalController controller;

    public FetchRunService(IInternalContest inContest, IInternalController inController) {
        super();
//        this.contest = inContest;
//        this.controller = inController;
    }

    /**
     * This method returns a JSON representation of the submitted run identified by {id}. 
     * 
     * @return a {@link Response} object containing the submitted run in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchRun(@PathParam("id") String runID) {
        
        
        //create a dummy hardcoded run for testing
        String run = "[{\"runID\":" + runID + ",\"filename\":\"a.java\",\"content\":\"<base64_string>\"},{\"filename\":\"helper.java\",\"content\":\"<base64_string>\"}]" ;

//        try {
//            //code here to construct a "request-run" packet, send it to the server, and wait (?) for the response
//        } catch (IllegalContestState e) {
//            //log exception
//            controller.getLog().log(Log.WARNING, "FetchRunService: problem fetching run from server ", e);
//            e.printStackTrace();
//
//            // return HTTP error response code
//            return Response.serverError().entity(e.getMessage()).build();
//        }

        // output the response to the requester (note that this actually returns it to Jersey,
        // which forwards it to the caller as the HTTP response).
        return Response.ok(run,MediaType.APPLICATION_JSON).build();
    }

}
