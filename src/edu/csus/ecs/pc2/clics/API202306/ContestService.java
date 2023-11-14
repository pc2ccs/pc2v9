// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.io.IOException;
import java.text.ParseException;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.services.core.JSONUtilities;
import edu.csus.ecs.pc2.services.eventFeed.WebServer;

/**
 * WebService to handle "contests/" and "contests/<id>" REST endpoints as described by the CLICS wiki.
 * 
 * example get output:
 * {
 *  "id": "wf2014",
 *  "name": "2014 ICPC World Finals",
 *  "formal_name": "38th Annual World Finals of the ACM International Collegiate Programming Contest",
 *  "start_time": "2014-06-25T10:00:00+01",
 *  "duration": "5:00:00",
 *  "scoreboard_freeze_duration": "1:00:00",
 *  "scoreboard_type": "pass-fail",
 *  "penalty_time": 20
 * }
 * 
 * example patch request (to set contest start time):
 * {
 *  "id":"7b0dd4ea-19a1-4434-9034-529ebe55ab45",
 *  "start_time":"2014-06-25T10:00:00+01"
 * }
 * 
 * or, to pause the countdown:
 * {
 *  "id":"wf2016",
 *  "start_time":null
 * }
 * 
 * or, to change thaw time:
 * {
 *  "id": "wf2014",
 *  "scoreboard_thaw_time": "2014-06-25T19:30:00+01"
 * }
 * 
 * @author pc2@ecs.csus.edu
 *
 */
@Path("/contests")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class ContestService implements Feature {

    private static final String LOG_PREFIX = "Contest Service " + ResourceConfig202306.CLICS_API_VERSION + ": PATCH for ";
    
    private static final String CONTEST_ID_KEY = "id";
    private static final String CONTEST_START_TIME_KEY = "start_time";
    private static final String CONTEST_COUNTDOWN_PAUSE_TIME_KEY = "countdown_pause_time";
    private static final String CONTEST_THAW_TIME = "scoreboard_thaw_time";
            
    private static final long MIN_MS_TO_START_OF_CONTEST = 30 * 1000;
    
    private IInternalContest model;

    private IInternalController controller;

    /**
     * List of the possible types of requests which might be received from clients.
     * 
     * @author john
     */
    private enum StartTimeRequestType {
        ILLEGAL, SET_START_TO_UNDEFINED, SET_START_TO_SPECIFIED_DATE, SET_COUNTDOWN_PAUSE_TIME, SET_CONTEST_THAW_TIME
    };

    public ContestService(IInternalContest inModel, IInternalController inController) {
        super();
        this.model = inModel;
        this.controller = inController;
    }

    /**
     * This method resets the current contest scheduled start time according to the received (input) string, which it expects to be in JSON format as described in the CLICS Wiki "StartTime" interface
     * specification.
     * 
     * @return a {@link Response} object indicating the status of the setStarttime request as follows (from the CLI Wiki Contest_Start_Interface spec):
     * 
     *         <pre>
     *         // PUT HTTP body is application/json:
     *         // { &quot;starttime&quot;:1265335138.26 }
     * // or:
     * // { &quot;starttime&quot;:&quot;undefined&quot; }
     * // HTTP response is:
     * // 200: if successful.
     * // 400: if the payload is invalid json, start time is invalid, etc.
     * // 401: if authentication failed.
     * // 403: if contest is already started
     * // 403: if setting to 'null' with less than 30s left to previous start time.
     * // 403: if setting to new (defined) start time with less than 30s left to previous start time.
     * // 403: if the new start time is less than 30s from now.
     *         </pre>
     */
    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{contestId}/")
    public Response setContestTimes(@Context HttpServletRequest servletRequest, @Context SecurityContext sc, @PathParam("contestId") String contestId, String jsonInputString) {

        // shorthand since we use this a bit
        String contestsEndpoint = "/contests/" + contestId + " PATCH request";
        
        controller.getLog().log(Log.DEBUG, LOG_PREFIX + contestId + " received the following request body: " + jsonInputString);

        // check for empty request
        if (jsonInputString == null || jsonInputString.length() == 0) {
            controller.getLog().log(Log.WARNING, LOG_PREFIX + contestId + ": received invalid (empty) JSON string");
            // return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("Empty contest request").build();
        }

        // we got some potentially legal input; try parsing it for valid form
        Map<String, String> requestMap = parseJSONIntoMap(contestId, jsonInputString);

        // if the map is null then the parsing failed
        if (requestMap == null) {
            controller.getLog().log(Log.WARNING, LOG_PREFIX + contestId + ": unable to parse JSON starttime string");
            // return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("Bad JSON starttime request").build();
        }

        // if we get here then the JSON parsed correctly; see if it contained "starttime" as a key
        if (!requestMap.containsKey(CONTEST_ID_KEY)) {
            controller.getLog().log(Log.WARNING, LOG_PREFIX + contestId + ": JSON input missing 'id' key: '" + jsonInputString + "'");
            // return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("Missing '" + CONTEST_ID_KEY + "' key in " + contestsEndpoint).build();
        }
        
        // validate id
        // TODO can the contestIdentifier be null?  Yes, but it may be something else too.  The CDS gives 'null',
        // and it is unclear what other CCS's that we are shadowing for may provide.  It is almost
        // certainly NOT what PC2 set up as the identifier (Default-###############).  As such, until the
        // API endpoints are fixed to include a (configurable) contest identifier, a reasonable thing to
        // do at this point is not validate the id at all.  Just make sure one was specified (above).  That's
        // enough for now.
        String jsonIdShorthand = LOG_PREFIX + contestId + ": JSON '" + CONTEST_ID_KEY + "' key ";
        String idAsk = requestMap.get(CONTEST_ID_KEY);
        
        if(idAsk == null) {
            controller.getLog().log(Log.WARNING, jsonIdShorthand + "is <null> - we are accepting this non-compliant client's request");               
        } else if(idAsk == "null") {
            // We have seen a CDS supply the actual string "null", so we will make believe it is null and accept it.
            controller.getLog().log(Log.WARNING, jsonIdShorthand + "is the word 'null' - we are accepting this non-compliant client's request");                               
        } else if(idAsk.equals(contestId) == false) {
            controller.getLog().log(Log.WARNING, jsonIdShorthand + "'" + idAsk + "' does not match the URL contestId '" + contestId + "'");                                               
            // return HTTP 409 response - client is confused and sending conflicting contest id's
            return Response.status(Status.CONFLICT).entity("Invalid '" + CONTEST_ID_KEY + "' key in " + contestsEndpoint + " (non-complaint client)").build();
        } else if (!model.getContestIdentifier().equals(idAsk)) {
            controller.getLog().log(Log.WARNING, jsonIdShorthand + "'" + idAsk + "' does not match the PC2 contest ID '" + model.getContestIdentifier() + "'");                
            // return HTTP 409 - client is confused and/or non-compliant
            return Response.status(Status.CONFLICT).entity("Invalid '" + CONTEST_ID_KEY + "' key in " + contestsEndpoint + " (non-complaint client)").build();
        }

        // get the Object corresponding to "start_time"
        String startTimeValueString = null;      
        // get the countdown_pause_time key, if there
        String countdownPauseTime = null;
        // Flag to indicate that countdown pause time was specified
        boolean sawCountdownPauseTime = false;
        
        // if we get here then the JSON parsed correctly; see if it contained "start_time" as a key (that is required by spec)
        if (!requestMap.containsKey(CONTEST_START_TIME_KEY)) {
            
            // check if thaw time is present
            if(!requestMap.containsKey(CONTEST_THAW_TIME)) {
                // no, neither one is included.  This is an error.
                controller.getLog().log(Log.WARNING, LOG_PREFIX + contestId + ": JSON input missing '" + CONTEST_START_TIME_KEY + "' key or '" + CONTEST_THAW_TIME + "' key: '" + jsonInputString + "'");
                // return HTTP 400 response code
                return Response.status(Status.BAD_REQUEST).entity("Missing '" + CONTEST_START_TIME_KEY + "' key or '" + CONTEST_THAW_TIME + "' key in " + contestsEndpoint).build();
            }
            return(HandleContestThawTime(sc, contestId, requestMap.get(CONTEST_THAW_TIME)));
        }
        
        // its either a contest start time adjustment or a countdown pause adjustment
        startTimeValueString = requestMap.get(CONTEST_START_TIME_KEY);
            
        if(requestMap.containsKey(CONTEST_COUNTDOWN_PAUSE_TIME_KEY)) {
            sawCountdownPauseTime = true;
            countdownPauseTime = requestMap.get(CONTEST_COUNTDOWN_PAUSE_TIME_KEY);
        }

        // check for count down pause time
        if(countdownPauseTime != null) {
            // can't have both a start time and count down pause time
            if(startTimeValueString != null) {
                controller.getLog().log(Log.WARNING, LOG_PREFIX + contestId + ": only one of '" + CONTEST_START_TIME_KEY + "' key or '" + CONTEST_COUNTDOWN_PAUSE_TIME_KEY + "' may be specified");
                // return HTTP 400 response
                return Response.status(Status.BAD_REQUEST).entity("Only one of '" + CONTEST_START_TIME_KEY + "' key or '" + CONTEST_COUNTDOWN_PAUSE_TIME_KEY + "' may be specified for " + contestsEndpoint).build();
            }
            return(HandleContestCountdownPauseTime(sc, contestId, countdownPauseTime));
        }
        return(HandleContestStartTime(sc, contestId, startTimeValueString, sawCountdownPauseTime));
    }
    
    /**
     * Process contest start time
     * 
     * @param contestId which contest
     * @param startTimeValueString new contest start time (ISO format) or null to make it undefined
     * @return web response
     */
    private Response HandleContestStartTime(SecurityContext sc, String contestId, String startTimeValueString, boolean sawCountdownPauseTime) {
        
        StartTimeRequestType requestType = StartTimeRequestType.ILLEGAL;
        GregorianCalendar requestedStartTime = null;
        String logString = LOG_PREFIX + contestId + ": received '" + CONTEST_START_TIME_KEY + "': ";
        
        // check authorization (verify requester is allowed to make this request)
        if (!isContestStartAllowed(sc)) {
            controller.getLog().log(Log.WARNING, LOG_PREFIX + contestId + ": unauthorized request");
            // return HTTP 401 response code per CLICS spec
            return Response.status(Status.UNAUTHORIZED).entity("You are not authorized to access this page").build();
        }
        // check if we have a start_time string (really? check for "null"?)
        if (startTimeValueString == null || startTimeValueString.trim().equalsIgnoreCase("null")) {
            requestType = StartTimeRequestType.SET_START_TO_UNDEFINED;
            logString += "<null>";
        } else {
            logString += startTimeValueString;

            // parse the starttime value for a valid date
            requestedStartTime = getDate(contestId, startTimeValueString);
            if (requestedStartTime != null) {
                requestType = StartTimeRequestType.SET_START_TO_SPECIFIED_DATE;
            // } else {
            // //null requestedStartTime means startTimeValueString failed to parse (wasn't a legal Unix epoch date);
            // // do nothing -- leaving requestType set to "ILLEGAL"
            }
        }
        controller.getLog().log(Log.DEBUG, logString + " Request Type: " + requestType.toString());

        if (requestType == StartTimeRequestType.ILLEGAL) {

            // we can get here if the value was not "null" but also didn't parse to a valid date
            controller.getLog().log(Log.WARNING, LOG_PREFIX + contestId + ": JSON input contains invalid starttime value: '" + startTimeValueString + "'");
            // return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("Bad value in starttime request").build();

        }

        // we have a legal request; check to insure contest has not already been started
        if (model.getContestTime().isContestStarted()) {
            // contest has started, cannot set scheduled start time
            controller.getLog().log(Log.WARNING, LOG_PREFIX + contestId + ": request to set start time when contest has already started; ignored");
            // return HTTP 403 (Forbidden) response code per CLICS spec
            return Response.status(Status.FORBIDDEN).entity("Contest already started").build();
        }

        // get the scheduled start time and the current time
        GregorianCalendar scheduledStartTime = model.getContestInformation().getScheduledStartTime();
        GregorianCalendar now = new GregorianCalendar();
        // validate scheduleStartTime
        // the contest has not yet started, but see if the scheduledStartTime was in the past
        if (scheduledStartTime != null && scheduledStartTime.before(now)) {
            // then clear it
            scheduledStartTime = null;
        }
        boolean success = false;

        String minSecsToStart = "" + MIN_MS_TO_START_OF_CONTEST/1000 + " seconds";
        
        switch (requestType) {

            case SET_START_TO_UNDEFINED:

                // check for less than 30 secs to scheduled start
                if (scheduledStartTime != null && scheduledStartTime.getTimeInMillis() < (now.getTimeInMillis() + MIN_MS_TO_START_OF_CONTEST)) {

                    // we have request to set start to "null", but we have a scheduled start and we're
                    // within 10 secs of it; cannot set scheduled start time to undefined (per CLICS spec);
                    controller.getLog().log(Log.WARNING, LOG_PREFIX + contestId + ": received request to set start time to 'null' with less than " + minSecsToStart + " to go before start; ignored");
                    // return HTTP 403 (Forbidden) response code per CLICS spec
                    return Response.status(Status.FORBIDDEN).entity("Cannot change start time to 'null' within " + minSecsToStart + " of already-scheduled start").build();

                } else {

                    // ok to set scheduled start to "undefined"
                    controller.getLog().log(Log.INFO, LOG_PREFIX + contestId + ": setStarttime(): setting contest start time to \"null\".");
                    success = setScheduledStart(null, sawCountdownPauseTime);
                    if (success) {
                        return Response.ok().entity("Contest start time updated to \"null\" (no scheduled start)").build();
                    } else {
                        controller.getLog().log(Log.SEVERE, LOG_PREFIX + contestId + ": setStarttime(): error setting contest start time to \"undefined\".");
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Server failed to set start time correctly").build();
                    }
                }

                // break; //can't get here, so Eclipse won't allow the explicit break

            case SET_START_TO_SPECIFIED_DATE:

                // check for less than 30 sec before scheduled start
                if (scheduledStartTime != null && scheduledStartTime.getTimeInMillis() < (now.getTimeInMillis() + MIN_MS_TO_START_OF_CONTEST)) {
                    // we're within 30 secs of scheduled start; cannot set scheduled start time to new value (per CLICS spec);
                    controller.getLog().log(Log.WARNING, LOG_PREFIX + contestId + ": received request to set start time with less than " + minSecsToStart + " to go before start; ignored");
                    // return HTTP 403 (Forbidden) response code per CLICS spec
                    return Response.status(Status.FORBIDDEN).entity("Cannot change to new start time within " + minSecsToStart + " of already-scheduled start").build();
                }

                // check for less than 30 sec in the future
                if (requestedStartTime.getTimeInMillis() < (now.getTimeInMillis() + MIN_MS_TO_START_OF_CONTEST)) {

                    // requested start time is less than 30sec from now; cannot set (per CLICS spec);
                    controller.getLog().log(Log.WARNING, LOG_PREFIX + contestId + ": received request to set start time less than " + minSecsToStart + " in the future; ignored");
                    // return HTTP 403 (Forbidden) response code per CLICS spec
                    return Response.status(Status.FORBIDDEN).entity("Cannot set start time less than " + minSecsToStart + " in the future").build();
                }

                // ok to set scheduled start to a specific time
                controller.getLog().log(Log.INFO, LOG_PREFIX + contestId + ": setStarttime(): setting contest start time to " + requestedStartTime);
                success = setScheduledStart(requestedStartTime, sawCountdownPauseTime);
                if (success) {
                    return Response.ok().entity("/contests/" + contestId).build();
                } else {
                    controller.getLog().log(Log.SEVERE, LOG_PREFIX + contestId + ": setStarttime(): error setting contest start time to requested date.");
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Server failed to set start time correctly").build();
                }

                // break;
                
            default:
                // shouldn't be able to get here!
                controller.getLog().log(Log.SEVERE, LOG_PREFIX + contestId + ": setStarttime(): unknown default condition: request type = " + requestType);
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Unknown condition in server: request type = " + requestType).build();
        }        
    }
    
    /**
     * Process Contest count down pause
     * 
     * @param contestId which contest
     * @param countdownPauseTime how long before contest start should the count down pause (CLICS RELTIME value) 
     * @return web resposne
     */
    private Response HandleContestCountdownPauseTime(SecurityContext sc, String contestId, String countdownPauseTime) {
        
        controller.getLog().log(Log.DEBUG, LOG_PREFIX + contestId + ": received '" + CONTEST_COUNTDOWN_PAUSE_TIME_KEY + "': " + countdownPauseTime);
        
        
        // check authorization (verify requester is allowed to make this request)
        if (!isContestStartAllowed(sc)) {
            controller.getLog().log(Log.WARNING, LOG_PREFIX + contestId + ": unauthorized request");
            // return HTTP 401 response code per CLICS spec
            return Response.status(Status.UNAUTHORIZED).entity("You are not authorized to access this page").build();
        }

        long pauseTime = Utilities.convertCLICSContestTimeToMS(countdownPauseTime);

        // MIN_VALUE is returned on format error
        if(pauseTime != Long.MIN_VALUE) {
            // want to stop the countdown with this many milliseconds left
            // TODO: tell PC2 to stop countdown when clock is 'pauseTime' ms away from start
            controller.getLog().log(Log.WARNING, LOG_PREFIX + contestId + ": countdown_pause_time not implemented");
            return Response.status(Status.NOT_MODIFIED).entity("Unable to set countdown_pause_time to " + pauseTime + " ms").build();
        }
        return Response.status(Status.BAD_REQUEST).entity("Bad value for count down pause time request").build();
    }
    
    /**
     * Process contest thaw time and generate response
     * 
     * @param contestId which contest
     * @param thawTimeValue ISO date of when the contest should unfreeze
     * @return web response
     */
    private Response HandleContestThawTime(SecurityContext sc, String contestId, String thawTimeValue) {
        
        // check authorization (verify requester is allowed to make this request)
        if (!isContestThawAllowed(sc)) {
            controller.getLog().log(Log.WARNING, LOG_PREFIX + contestId + ": unauthorized request");
            // return HTTP 401 response code per CLICS spec
            return Response.status(Status.UNAUTHORIZED).entity("You are not authorized to access this page").build();
        }
        
        // thaw time present, validate now
        GregorianCalendar thawTime = getDate(contestId, thawTimeValue);
        if (thawTime != null) {
            // Set thaw time to this time.
            // TODO: tell PC2 to thaw the contest at the given time.
            controller.getLog().log(Log.WARNING, LOG_PREFIX + contestId + ": setting of contest thaw time is not implemented");
            return Response.status(Status.NOT_MODIFIED).entity("Unable to set contest thaw time to " + thawTime.toString()).build();
        }
        return Response.status(Status.BAD_REQUEST).entity("Bad value for contest thaw time request").build();
        
    }
    
    /**
     * Parses the given String and returns a {@link GregorianCalendar} object if the String represents a valid Unix Epoch date; otherwise returns null.
     * 
     * @param contestId contest identifier
     * @param startTimeValueString
     *            a String containing a date in ISO 8601 format.
     * @return the GregorianCalendar date/time represented by the String, or null if the String does not represent a valid date/time
     */
    private GregorianCalendar getDate(String contestId, String startTimeValueString) {
        GregorianCalendar theDate = new GregorianCalendar();
        try {
            theDate.setTime(Utilities.getIso8601formatterWithMS().parse(startTimeValueString));
        } catch (ParseException e) {
            try {
                controller.getLog().log(Log.DEBUG, LOG_PREFIX + contestId + ": Re-parsing date without MS " + startTimeValueString);
                theDate.setTime(Utilities.getIso8601formatter().parse(startTimeValueString));
            } catch (ParseException e2) {
                controller.getLog().throwing("ContestService", "getDate", e2);
                return null;
            }
        }

        // debug
        // System.out.println ("ContestService.getDate(): returning a GregorianCalendar with a date of " + theDate.getTimeInMillis());
        controller.getLog().log(Log.DEBUG, LOG_PREFIX + contestId + ": getDate(): returning a GregorianCalendar with a start date of " + theDate.getTimeInMillis());

        return theDate;
    }

    /**
     * This method updates the Scheduled Start Date for the contest, including causing the scheduling of a "start contest" task for the specified date (which is assumed to be a valid date in the
     * future).
     * 
     * This is accomplished by telling the controller to update the {@link ContestInformation} with the scheduled start date. The controller then sends a packet to the server to do that; the server in
     * turn creates a task to start the contest at the specified date/time.
     * 
     * @param theDate
     *            the date/time to which the automatic start of the contest should be set, or null if the start date/time should be set to "undefined"
     * @param unPauseCountdown tell pc2 that the countdown pause (if in effect) should be cancelled, and countdown should resume, if there's a valid start time
     * @return true if the method was successful in setting the scheduled start time; false otherwise
     */
    private boolean setScheduledStart(GregorianCalendar theDate, boolean unPauseCountdown) {

        // get the local model's ContestInformation
        ContestInformation ci = model.getContestInformation();
        if (ci != null) {
            // set the new start date/time into the ContestInformation
            ci.setScheduledStartTime(theDate);
            if (theDate != null) {
                // if we have a valid start date, set the contest to auto-start
                ci.setAutoStartContest(true);
            }
            // TODO unpause countdown - this will be a flag in "ci"
            // tell the Controller to update the ContestInformation
            controller.updateContestInformation(ci);
            return true;
        } else {
            // for some reason we failed to get ContestInformation
            return false;
        }
    }

    /**
     * Converts the input string, assumed to be a JSON string, into a {@link Map<String,String>} of JSON key-value pairs.
     * 
     * @param contestId contest identifier
     * @param jsonRequestString
     *            a JSON string specifying a starttime request in CLICS format
     * @return a Map of the JSON string key-to-value pairs as Strings, or null if the input JSON does not parse as a Map(String->String).
     */
    private Map<String, String> parseJSONIntoMap(String contestId, String jsonRequestString) {

        controller.getLog().log(Log.INFO, LOG_PREFIX + contestId + ": parseJSONIntoMap(): attempting to convert JSON input '" + jsonRequestString + "' into Map");

        // use Jackson's ObjectMapper to construct a Map of Strings-to-Strings from the JSON input
        final ObjectMapper mapper = new ObjectMapper();
        final MapType mapType = mapper.getTypeFactory().constructMapType(Map.class, String.class, String.class);
        final Map<String, String> jsonDataMap;

        try {
            jsonDataMap = mapper.readValue(jsonRequestString, mapType);
        } catch (JsonMappingException e) {
            // error parsing JSON input
            controller.getLog().log(Log.WARNING, LOG_PREFIX + contestId + ": parseJSONIntoMap(): JsonMappingException parsing JSON input '" + jsonRequestString + "'");
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            controller.getLog().log(Log.WARNING, LOG_PREFIX + contestId + ": parseJSONIntoMap(): IOException parsing JSON input '" + jsonRequestString + "'");
            e.printStackTrace();
            return null;
        }

        return jsonDataMap;
    }

    /**
     * This method returns a representation of the current contest scheduled start time in JSON format as described on the CLICS wiki.
     * 
     * @return a {@link Response} object containing a JSON String giving the scheduled contest start time as a Unix Epoch value, or as the string "undefined" if no start time is currently scheduled.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getContests() {
        CLICSContestInfo [] allContests = new CLICSContestInfo[1];
        allContests[0] = new CLICSContestInfo(model);
        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            String json = mapper.writeValueAsString(allContests);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for contest info " + e.getMessage()).build();
        }
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("{contestId}/")
    public Response getTeam(@PathParam("contestId") String contestId) {
        
        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == true) {
            try {
                ObjectMapper mapper = JSONUtilities.getObjectMapper();
                String json = mapper.writeValueAsString(new CLICSContestInfo(model));
                return Response.ok(json, MediaType.APPLICATION_JSON).build();
            } catch (Exception e) {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for contest info " + e.getMessage()).build();
            }
        }
        
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Check the user has a role than change contest start time
     * 
     * @param sc Security context for the user
     * @return true if the user can perform the operation
     */
    public static boolean isContestStartAllowed(SecurityContext sc) {
        return(sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN));
    }

    /**
     * Check the user has a role than change contest thaw time
     * 
     * @param sc Security context for the user
     * @return true if the user can perform the operation
     */
    public static boolean isContestThawAllowed(SecurityContext sc) {
        return(sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN));
    }
    
    /**
     * Retrieve access information about this endpoint for the supplied user's security context
     * 
     * @param sc User's security information
     * @return CLICSEndpoint object if the user can access this endpoint's properties, null otherwise
     */
    public static CLICSEndpoint getEndpointProperties(SecurityContext sc) {
        // All properties are available to everyone
        String [] props = { "id", "name", "formal_name", "start_time", "countdown_pause_time", "duration",
                "scoreboard_freeze_duration", "scoreboard_thaw_time", "scoreboard_type", "penalty_time"
        };
        return(new CLICSEndpoint("contest", props));
    }
    
    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }
}
