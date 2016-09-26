package edu.csus.ecs.pc2.services.web;

import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
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

    private IInternalContest model;
    private IInternalController controller;
    private boolean requestToSetStartTimeToUndefined;
    private boolean requestToSetStartTimeToExplicitValue;
    private Date requestedStartDate;
   
    public StarttimeService(IInternalContest inModel, IInternalController inController) {
        super();
        this.model = inModel;
        this.controller = inController;
    }

    /**
     * This method resets the current contest scheduled start time according to the
     * received (input) string, which it expects to be in JSON format as described
     * in the CLICS Wiki "StartTime" interface specification.
     * 
     * @return a {@link Response} object indicating the status of the setStarttime request
     * as follows (from the CLI Wiki Contest_Start_Interface spec):
     * <pre>
     *  // PUT HTTP body is application/json:
     *  // { "starttime":1265335138.26 }
     *  // or:
     *  // { "starttime":"undefined" }
     *  // HTTP response is:
     *  // 200: if successful.
     *  // 400: if the payload is invalid json, start time is invalid, etc.
     *  // 401: if authentication failed.
     *  // 403: if contest is already started
     *  // 403: if setting to 'undefined' with less than 10s left to previous start time.
     *  // 403: if setting to new (defined) start time with less than 30s left to previous start time.
     *  // 403: if the new start time is less than 30s from now.
     * </pre>
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setStartime(@Context SecurityContext sc, String jsonInputString) {

        //DEBUG:
        System.out.println ("Starttime PUT: received the following request body: '" + jsonInputString + "'");
 
        //check authorization (verify requester is allowed to make this request)
        if (!sc.isUserInRole("admin")) {
            controller.getLog().log(Log.WARNING, "Starttime Service: unauthorized Starttime PUT request (user not in admin role)");
            //return HTTP 401 response code per CLICS spec
            return Response.status(Status.UNAUTHORIZED).entity("You are not authorized to access this page").build();
        }

        //check for empty request
        if (jsonInputString==null || jsonInputString.length()==0) {
            controller.getLog().log(Log.WARNING, "Starttime Service: received invalid (empty) JSON starttime string");
            //return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("Empty starttime request").build();
        }
        
        //check which kind of request we've received
        requestToSetStartTimeToUndefined = false ;
        requestToSetStartTimeToExplicitValue = false ;
        
        if (jsonInputString.equalsIgnoreCase("{\"starttime\":\"undefined\"}")) {
            requestToSetStartTimeToUndefined = true;
        } else {
            requestToSetStartTimeToExplicitValue = requestHasValidStarttimeDate(jsonInputString);
        }
        
        //check to insure received payload (request) is valid
        if (!requestToSetStartTimeToUndefined && !requestToSetStartTimeToExplicitValue) {
            //invalid payload -- log, return error status
            controller.getLog().log(Log.WARNING, "Starttime Service: received invalid data in request: '" + jsonInputString + "'");
            //return HTTP 400 (Bad Request) response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("Invalid data in Starttime request").build();
        }
        
        //if we get here, the JSON input either indicates "undefined" start or a valid start Date,
        // and if a valid start Date then the Date is contained in field "startDate" (otherwise the field is null)
        
        //check the following error conditions (per the CLICS spec) and return 403 Forbidden:
        // 403: if contest is already started
        // 403: if setting to 'undefined' with less than 10s left to previous start time.
        // 403: if setting to new (defined) start time with less than 30s left to previous start time.
        // 403: if the new start time is less than 30s from now.

        //check to insure contest has not already been started
        if (model.getContestTime().isContestStarted()) {
            //contest has started, cannot set scheduled start time -- log, and return error status
            controller.getLog().log(Log.WARNING, 
                "Starttime Service: received request to set start time when contest has already started; ignored");
            //return HTTP 403 (Forbidden) response code per CLICS spec
            return Response.status(Status.FORBIDDEN).entity("Contest already started").build();
        }
        
        //get the scheduled start date (if any) and the current date (time)
        Date scheduledStartDate = model.getContestInformation().getScheduledStartDate() ;
        GregorianCalendar now = new GregorianCalendar();

        //check if setting to "undefined" with less than 10s left to previous start time.
        if (requestToSetStartTimeToUndefined) {
            if (scheduledStartDate!=null && scheduledStartDate.getTime()<(now.getTimeInMillis()+10000)) {
                //we have request to set start to "undefined", but we have a scheduled start date and we're 
                // within 10 secs of it; cannot set scheduled start time to undefined (per CLICS spec);
                // log, return error status, and ignore (meaning, the contest is going to start!)
                controller.getLog().log(Log.WARNING, 
                    "Starttime Service: received request to set start time to 'undefined' with less than 10 seconds to go before start; ignored");
                //return HTTP 403 (Forbidden) response code per CLICS spec
                return Response.status(Status.FORBIDDEN).entity("Cannot change start time to 'undefined' within 10 seconds of already-scheduled start").build();
            }
        }
        
        //check if setting to new (defined) start time with less than 30s left to previously-defined start time.
        if (requestToSetStartTimeToExplicitValue) {
            if (requestedStartDate!=null) {
                if (scheduledStartDate!=null && scheduledStartDate.getTime()<(now.getTimeInMillis()+30000)) {
                    //we're within 30 secs of scheduled start; cannot set scheduled start time to new value (per CLICS spec);
                    // log, return error status, and ignore (meaning, the contest is going to start!)
                    controller.getLog().log(Log.WARNING, 
                            "Starttime Service: received request to set start time with less than 30 seconds to go before start; ignored");
                    //return HTTP 403 (Forbidden) response code per CLICS spec
                    return Response.status(Status.FORBIDDEN).entity("Cannot change to new start time within 30 seconds of already-scheduled start").build();
                }
            } else {
                //huh? we got a valid request to set a specific start date but the requestedStartDate is null -- coding error!
                controller.getLog().log(Log.SEVERE, 
                        "Starttime Service: Error: code says we got a valid start date but start date is null !?");
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Server failed to handle request correctly").build();
            }
        }
        
        //check if the new start time is less than 30s from now.
        if (requestToSetStartTimeToExplicitValue) {
            if (requestedStartDate!=null) {
                if (requestedStartDate.getTime()<(now.getTimeInMillis()+30000)) {
                    //requested start time is less than 30sec from now; cannot set (per CLICS spec);
                    // log, return error status, and ignore 
                    controller.getLog().log(Log.WARNING, 
                            "Starttime Service: received request to set start time less than 30 seconds in the future; ignored");
                    //return HTTP 403 (Forbidden) response code per CLICS spec
                    return Response.status(Status.FORBIDDEN).entity("Cannot set start time less than 30 seconds in the future").build();
                }
            } else {
                //huh? we got a valid request to set a specific start date but the requestedStartDate is null -- coding error!
                controller.getLog().log(Log.SEVERE, 
                        "Starttime Service: Error: code says we got a valid start date but start date is null !?");
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Server failed to handle request correctly").build();
            }
        }

        //if we get here then we have a valid starttime request; set the contest scheduled start time
        boolean success = false;
        if (requestToSetStartTimeToUndefined) {
            
            controller.getLog().log(Log.INFO, "StarttimeService.setStarttime(): setting contest start time to \"undefined\".");
            success = setScheduledStartDate(null);
            if (success) {
                return Response.ok().entity("Contest start time updated to \"undefined\"").build();
            } else {
                controller.getLog().log(Log.SEVERE, "StarttimeService.setStarttime(): error setting contest start time to \"undefined\".");
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Server failed to set start time correctly").build();
            }
            
        } else if (requestToSetStartTimeToExplicitValue) {
            
            controller.getLog().log(Log.INFO, "StarttimeService.setStarttime(): setting contest start time to " + requestedStartDate);
            success = setScheduledStartDate(requestedStartDate);
            if (success) {
                return Response.ok().entity("Contest start time updated to " + requestedStartDate).build();
            } else {
                controller.getLog().log(Log.SEVERE, "StarttimeService.setStarttime(): error setting contest start time to requested date.");
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Server failed to set start time correctly").build();
            }
            
        } else {
            
            //huh?  we should never get here; we are supposed to have a valid request either for "undefined" or for an explicit start date
            controller.getLog().log(Log.SEVERE, 
                    "Starttime Service: Error: code says we got a valid start date but start date is null !?");
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Server failed to handle request correctly").build();
        }
    }
    
    /**
     * 
     * @param theDate the Date to which the automatic start of the contest should be set, or
     *          null if the start date should be set to "undefined"
     * @return true if the method was successful in setting the scheduled start time (including all the implied 
     *          related tasks such as scheduling a "start contest" task); false otherwise
     */
    private boolean setScheduledStartDate(Date theDate) {
        // TODO change this method to use the CONTROLLER to set the date (not the model)
        //old code:
        model.getContestInformation().setScheduledStartDate(null);
        //note that the above doesn't work anyway; all it does is update the scheduledStartDate field in the model;
        // what needs to happen is 
        //  1) get the contest information from the model
        //  2) put the new start date in the contest information
        //  3) update the model contestInformation
        //  4) schedule a new StartContest task if the Date isn't null.
        // All this needs to be done in the CONTROLLER; in fact, it probably should be done by
        // having the Controller send a packet to the SERVER and have all the above done on the Server.
        
        return false;
    }

    /**
     * Examines the specified JSON string to see whether it contains a valid starttime request date
     * per the CLICS Starttime specification.
     * Tests include verifying the string is valid JSON, contains a "starttime" key,
     *  and that the specified starttime value in 
     * the string represents a valid start time of the form
     * { "starttime":1265335138.26 } .
     * 
     * If 'true' is returned then the field "startDate" is set to the Java Date object extracted from the JSON.
     * 
     * @param jsonRequestString a JSON string specifying a starttime request
     * @return true if the input JSON is a valid starttime request with a valid date; false otherwise.  
     */
    private boolean requestHasValidStarttimeDate(String jsonRequestString) {
        
        controller.getLog().log(Log.INFO, "StarttimePUT.requestHasValidStarttimeDate(): checking JSON input '"
                + jsonRequestString + "' for valid start date");
        
        System.out.println ("StarttimeService.requestHasValidStarttimeDate(): checking input string '" + jsonRequestString + "'");
        
        //use Jackson's ObjectMapper to construct a Map of Strings-to-Objects from the JSON input
        final ObjectMapper mapper = new ObjectMapper();
        final MapType mapType = mapper.getTypeFactory().constructMapType(Map.class, String.class, Date.class);
        final Map<String, Object> jsonDataMap;

        try {
            jsonDataMap = mapper.readValue(jsonRequestString, mapType);
        } catch (JsonMappingException e) {
            //log error
            controller.getLog().log(Log.WARNING, "StarttimePUT.requestHasValidStarttimeDate(): JsonMappingException parsing JSON input '"
                    + jsonRequestString + "'");
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            //log error
            controller.getLog().log(Log.WARNING, "StarttimePUT.requestHasValidStarttimeDate(): IOException parsing JSON input '"
                    + jsonRequestString + "'");
            e.printStackTrace();
            return false;
        }

        //if we get here then the JSON parsed correctly; see if it contained "starttime" as a key
        if (!jsonDataMap.containsKey("starttime")) {
            controller.getLog().log(Log.WARNING, "StarttimePUT.requestHasValidStarttimeDate(): JSON input missing 'starttime' key: '"
                    + jsonRequestString + "'");
            return false;
        }
        
        //verify the JSON didn't contain any OTHER key/value information
        if (jsonDataMap.size() != 1) {
            controller.getLog().log(Log.WARNING, "StarttimePUT.requestHasValidStarttimeDate(): JSON input contains illegal extra data: '"
                    + jsonRequestString + "'");
            return false;
        }
        
        //if we get here the JSON is valid and contains exactly one element: starttime
        //get the Object corresponding to "starttime"
        Object obj = jsonDataMap.get("starttime");
        
        //DEBUG:
        System.out.println("StarttimePut.requestHasValidStarttimeDate(): received start time value '" + obj + "'");

        //verify we got a valid date 
        if (obj != null && obj instanceof Date) {
            requestedStartDate = (Date)obj;
            return true;
        } else {
            //the starttime value was not a valid Date object
            controller.getLog().log(Log.WARNING, "StarttimePUT.requestHasValidStarttimeDate(): JSON input contains illegal date: '"
                    + jsonRequestString + "'");
            requestedStartDate = null;
            return false ;
        }
        
        //shouldn't ever get here; every one of the above branches has a return...
        // (And don't try to claim this is a "Tammy" and the code should have a return statement here -- Eclipse won't allow it)
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
        Date startDate = model.getContestInformation().getScheduledStartDate();
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
