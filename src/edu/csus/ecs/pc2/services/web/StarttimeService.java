package edu.csus.ecs.pc2.services.web;

import java.io.IOException;
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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestInformation;
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
    
    private enum StartTimeRequestType {ILLEGAL, SET_START_TO_UNDEFINED, SET_START_TO_SPECIFIED_DATE};
   
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
        
        //we got some potentially legal input; try parsing it for valid form
        Map<String,String> requestMap = parseJSONIntoMap(jsonInputString);
        
        //if the map is null then the parsing failed
        if (requestMap == null) {
            controller.getLog().log(Log.WARNING, "Starttime PUT Service: unable to parse JSON starttime string");
            //return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("Bad JSON starttime request").build();
        }
        
        //if we get here then the JSON parsed correctly; see if it contained "starttime" as a key
        if (!requestMap.containsKey("starttime")) {
            controller.getLog().log(Log.WARNING, "Starttime PUT Service: JSON input missing 'starttime' key: '" + jsonInputString + "'");
            //return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("Missing 'starttime' key in starttime request").build();
        }
        
        //verify the JSON didn't contain any OTHER key/value information
        if (requestMap.size() != 1) {
            controller.getLog().log(Log.WARNING, "Starttime PUT Service: JSON input contains illegal extra data: '" + jsonInputString + "'");
            //return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("Extra data in starttime request").build();
        }
        
        //if we get here the JSON is valid and contains exactly one element: starttime
        //get the Object corresponding to "starttime"
        String startTimeValueString = requestMap.get("starttime");
        
        //DEBUG:
        System.out.println("StarttimePut.setStartTime(): received start time value '" + startTimeValueString + "'");

        
        StartTimeRequestType requestType = StartTimeRequestType.ILLEGAL;
        GregorianCalendar requestedStartTime = null;
        
        //check if we have a viable starttime string
        if (startTimeValueString != null  &&  startTimeValueString.length()>0) {
            
            if (startTimeValueString.trim().equalsIgnoreCase("undefined")) {
            
                requestType = StartTimeRequestType.SET_START_TO_UNDEFINED;
            
            } else {
            
                //parse the starttime value for a valid date
                requestedStartTime = getDate(startTimeValueString);
                if (requestedStartTime != null) {
                    requestType = StartTimeRequestType.SET_START_TO_SPECIFIED_DATE;
                } else {
                    //null requestedStartTime means startTimeValueString failed to parse (wasn't a legal Unix epoch date);
                    // do nothing -- leaving requestType set to "ILLEGAL"
                }
            }
                
        } else {
            
            //the starttime value was null or empty
            controller.getLog().log(Log.WARNING, "Starttime PUT Service: JSON input contains empty starttime value" );
            //return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("Empty value in starttime request").build();
        }
        
        if (requestType==StartTimeRequestType.ILLEGAL) {
            
            //we can get here if the value was not "undefined" but also didn't parse to a valid date
            controller.getLog().log(Log.WARNING, "Starttime PUT Service: JSON input contains invalid starttime value: '" + startTimeValueString + "'");
            //return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("Bad value in starttime request").build();
        
        }
        
        //we have a legal request; check to insure contest has not already been started
        if (model.getContestTime().isContestStarted()) {
            //contest has started, cannot set scheduled start time
            controller.getLog().log(Log.WARNING, "Starttime PUT Service: request to set start time when contest has already started; ignored");
            //return HTTP 403 (Forbidden) response code per CLICS spec
            return Response.status(Status.FORBIDDEN).entity("Contest already started").build();
        }
                
        
        //get the scheduled start time and the current time
        GregorianCalendar scheduledStartTime = model.getContestInformation().getScheduledStartTime() ;
        GregorianCalendar now = new GregorianCalendar();
        boolean success = false ;

        switch (requestType) {

            case SET_START_TO_UNDEFINED:

                //check for less than 10 secs to scheduled start
                if (scheduledStartTime != null && scheduledStartTime.getTimeInMillis() < (now.getTimeInMillis() + 10000)) {
                    
                    // we have request to set start to "undefined", but we have a scheduled start and we're
                    // within 10 secs of it; cannot set scheduled start time to undefined (per CLICS spec);
                    controller.getLog().log(Log.WARNING,
                            "Starttime PUT Service: received request to set start time to 'undefined' with less than 10 seconds to go before start; ignored");
                    // return HTTP 403 (Forbidden) response code per CLICS spec
                    return Response.status(Status.FORBIDDEN).entity("Cannot change start time to 'undefined' within 10 seconds of already-scheduled start").build();
                    
                } else {
                    
                    //ok to set scheduled start to "undefined"
                    controller.getLog().log(Log.INFO, "StarttimeService.setStarttime(): setting contest start time to \"undefined\".");
                    success = setScheduledStart(null);
                    if (success) {
                        return Response.ok().entity("Contest start time updated to \"undefined\"").build();
                    } else {
                        controller.getLog().log(Log.SEVERE, "StarttimeService.setStarttime(): error setting contest start time to \"undefined\".");
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Server failed to set start time correctly").build();
                    }
                }

                // break; //can't get here, but Eclipse won't allow the explict break

            case SET_START_TO_SPECIFIED_DATE:

                // check for less than 30 sec before scheduled start
                if (scheduledStartTime != null && scheduledStartTime.getTimeInMillis() < (now.getTimeInMillis() + 30000)) {
                    
                    // we're within 30 secs of scheduled start; cannot set scheduled start time to new value (per CLICS spec);
                    controller.getLog().log(Log.WARNING,
                            "Starttime Service: received request to set start time with less than 30 seconds to go before start; ignored");
                    // return HTTP 403 (Forbidden) response code per CLICS spec
                    return Response.status(Status.FORBIDDEN).entity("Cannot change to new start time within 30 seconds of already-scheduled start").build();
                }

                // check for less than 30 sec in the future
                if (requestedStartTime.getTimeInMillis() < (now.getTimeInMillis() + 30000)) {
                    
                    // requested start time is less than 30sec from now; cannot set (per CLICS spec);
                    controller.getLog().log(Log.WARNING,
                            "Starttime Service: received request to set start time less than 30 seconds in the future; ignored");
                    // return HTTP 403 (Forbidden) response code per CLICS spec
                    return Response.status(Status.FORBIDDEN).entity("Cannot set start time less than 30 seconds in the future").build();
                }

                // ok to set scheduled start to a specific time
                controller.getLog().log(Log.INFO, "StarttimeService.setStarttime(): setting contest start time to " + requestedStartTime);
                success = setScheduledStart(requestedStartTime);
                if (success) {
                    String newStartTime = new Long(requestedStartTime.getTimeInMillis()/1000).toString();
                    newStartTime += ".";
                    newStartTime += new Long(requestedStartTime.getTimeInMillis()%1000).toString();
                    return Response.ok().entity("Contest start time updated to " + newStartTime).build();
                } else {
                    controller.getLog().log(Log.SEVERE, "StarttimeService.setStarttime(): error setting contest start time to requested date.");
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Server failed to set start time correctly").build();
                }

                // break;

            default:
                //shouldn't be able to get here!
                controller.getLog().log(Log.SEVERE, "StarttimeService.setStarttime(): unknown default condition: request type = " + requestType);
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Unknown condition in server: request type = " + requestType).build();
        }
            
    }
    
    /**
     * Parses the given String and returns a {@link GregorianCalendar} object if the String represents a valid Unix Epoch date; otherwise returns null.
     * @param startTimeValueString a String containing a date in Unix epoch form
     * @return the GregorianCalendar date/time represented by the String, or null if the String does not represent a valid date/time
     */
    private GregorianCalendar getDate(String startTimeValueString) {
        
        long seconds ;
        try {
            //get the float value and truncate it to whole seconds
            seconds = (long) Float.parseFloat(startTimeValueString);
        } catch (NumberFormatException e) {
            return null;
        }
        
        GregorianCalendar theDate = new GregorianCalendar();
        theDate.setTimeInMillis(seconds*1000);
        return theDate;
    }

    /**
     * This method updates the Scheduled Start Date for the contest, including causing the scheduling of a "start contest"
     * task for the specified date (which is assumed to be a valid date in the future).
     * 
     * This is accomplished by telling the controller to update the {@link ContestInformation} with the scheduled start date. 
     * The controller then sends a packet to the server to do that; the server in turn creates a task to start the contest at the
     * specified date/time. 
     * 
     * @param theDate the date/time to which the automatic start of the contest should be set, or
     *          null if the start date/time should be set to "undefined"
     * @return true if the method was successful in setting the scheduled start time; false otherwise
     */
    private boolean setScheduledStart(GregorianCalendar theDate) {
        
        //get the local model's ContestInformation 
        ContestInformation ci = model.getContestInformation();
        if (ci!=null) {
            //set the new start date/time into the ContestInformation
            ci.setScheduledStartTime(theDate);
            if (theDate!=null) {
                //if we have a valid start date, set the contest to auto-start
                ci.setAutoStartContest(true);
            }
            //tell the Controller to update the ContestInformation
            controller.updateContestInformation(ci);
            return true;
        } else {
            //for some reason we failed to get ContestInformation
            return false;
        }
    }

    /**
     * Converts the input string, assumed to be a JSON string, into a {@link Map<String,String>} of JSON key-value pairs.
     * 
     * @param jsonRequestString a JSON string specifying a starttime request in CLICS format
     * @return a Map of the JSON string key-to-value pairs as Strings, or null if the input JSON does not parse as a Map(String->String).  
     */
    private Map<String,String> parseJSONIntoMap(String jsonRequestString) {
        
        controller.getLog().log(Log.INFO, "StarttimePUT.parseJSONIntoMap(): attempting to convert JSON input '"
                + jsonRequestString + "' into Map");
        
        System.out.println ("StarttimeService.parseJSONIntoMap(): creating Map from input string '" + jsonRequestString + "'");
        
        //use Jackson's ObjectMapper to construct a Map of Strings-to-Strings from the JSON input
        final ObjectMapper mapper = new ObjectMapper();
        final MapType mapType = mapper.getTypeFactory().constructMapType(Map.class, String.class, String.class);
        final Map<String, String> jsonDataMap;

        try {
            jsonDataMap = mapper.readValue(jsonRequestString, mapType);
        } catch (JsonMappingException e) {
            //error parsing JSON input
            controller.getLog().log(Log.WARNING, "StarttimePUT.parseJSONIntoMap(): JsonMappingException parsing JSON input '"
                    + jsonRequestString + "'");
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            controller.getLog().log(Log.WARNING, "StarttimePUT.parseJSONIntoMap(): IOException parsing JSON input '"
                    + jsonRequestString + "'");
            e.printStackTrace();
            return null;
        }

        return jsonDataMap;
    }
    

    /**
     * This method returns a representation of the current contest scheduled start time in JSON format
     * as described on the CLICS wiki.
     * 
     * @return a {@link Response} object containing a JSON String giving the scheduled contest start time as a Unix Epoch value, 
     * or as the string "undefined" if no start time is currently scheduled.
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
        GregorianCalendar startTime = model.getContestInformation().getScheduledStartTime();
        
        //determine the number of seconds specified by the starttime
        long startTimeSecs;
        if (startTime == null) {
            //there is no start time currently scheduled
            startTimeSecs = 0;
        } else {
            //there IS a scheduled start time set; get its value in seconds
            startTimeSecs = startTime.getTimeInMillis()/1000;
        }
        
        //build a string describing the scheduled start time
        String timeString = "";
        if(startTimeSecs == 0) {
            timeString = "\"undefined\"";
        } else {
            timeString = new Long(startTimeSecs).toString();
        }

        //format the start time as JSON
        String jsonStartTime = "{" + "\"starttime\"" + ":" + timeString + "}";

        // output the starttime response to the requester (note that this actually returns it to Jersey,
        // which forwards it to the caller as the HTTP response).
        return Response.ok(jsonStartTime,MediaType.APPLICATION_JSON).build();
    }
}
