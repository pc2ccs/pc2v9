package edu.csus.ecs.pc2.services.web;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * WebService to handle "starttime" REST endpoint as described by the CLICS wiki.
 * Invoking REST endpoint "getStarttime()" returns the currently configured start time (or "undefined"
 * if the contest has already been started or if there is no start time currently set).
 * Invoking REST endpoint "setStarttime()" when the contest has not already been started
 * sets the contest scheduled start time to either a specified future time or to "undefined".
 * If setStarttime() is invoked after the contest has started the call is ignored (but an error is returned). 
 * 
 * @author pc2@ecs.csus.edu
 *
 */
@Path("/starttime")
@Produces(MediaType.APPLICATION_JSON)
public class StarttimeService {

    private IInternalContest contest;
    private IInternalController controller;

    public StarttimeService(IInternalContest inContest, IInternalController inController) {
        super();
        this.contest = inContest;
        this.controller = inController;
    }

    /**
     * This method resets the current contest scheduled start time according to the
     * received (input) string, which it expects to be in JSON format as described
     * in the CLICS Wiki "StartTime" interface specification.
     * 
     * @return a {@link Response} object indicating the status of the setStarttime request
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setStartime() {

        //check to insure contest has not already been started; if so, ignore, log, and return some kind of error status
        
        // from the CLI Wiki Contest_Start_Interface spec:
        // PUT HTTP body is application/json:
        // { "starttime":1265335138.26 }
        // or:
        // { "starttime":"undefined" }
        // HTTP response is:
        // 200: if successful.
        // 400: if the payload is invalid json, start time is invalid, etc.
        // 401: if authentication failed.
        // 403: if contest is already started
        // 403: if setting to 'undefined' with less than 10s left to previous start time.
        // 403: if setting to new (defined) start time with less than 30s left to previous start time.
        // 403: if the new start time is less than 30s from now.

        // output the response to the requester (note that this actually returns it to Jersey,
        // which converts it to JSON and forwards that to the caller as the HTTP.response).
        // return Response.status(Response.Status.OK).build();

        return Response.ok("Start Time <here>").build();

    }

    /**
     * This method returns a representation of the current contest scheduled start time in JSON format
     * as described on the CLICS wiki.
     * 
     * @return a {@link Response} object containing a JSON String giving the scheduled contest start time, 
     * or the string "undefined" if no start time is currently scheduled (which includes if the contest has already started).
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStartTime() {

        // from the CLI Wiki Contest_Start_Interface spec:
        // GET HTTP body returns application/json:
        // { "starttime":1265335138.26 }
        // or:
        // { "starttime":"undefined" }

        // get the start time from the contest
        Date startDate = contest.getContestInformation().getScheduledStartDate();
        long startTime;
        if (startDate == null) {
            //there is no start time currently scheduled
            startTime = 0;
        } else {
            //there IS a scheduled start time set; get it
            startTime = startDate.getTime();
        }
        
        //build a string describing the scheduled start time
        String stringTime = "";
        if(startTime == 0) {
            stringTime = "\"undefined\"";
        } else {
            stringTime = new Long(startTime).toString();
        }

        //format the start time as JSON
        String jsonStartTime = "{" + "\"starttime\"" + ":" + stringTime + "}";

        // output the starttime response to the requester (note that this actually returns it to Jersey,
        // which forwards it to the caller as the HTTP response).
        return Response.ok(jsonStartTime,MediaType.APPLICATION_JSON).build();

    }

}
