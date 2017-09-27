package edu.csus.ecs.pc2.services.web;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.MapType;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;

/**
 * WebService to handle "contest" REST endpoint as described by the CLICS wiki.
 * 
 * example get output: {"id":"7b0dd4ea-19a1-4434-9034-529ebe55ab45", "name":"2014 ICPC World Finals", "formal_name":"38th Annual World Finals of the ACM International Collegiate Programming Contest",
 * "start_time":"2014-06-25T10:00:00+01","duration":"5:00:00","scoreboard_freeze_duration":"1:00:00","penalty_time":20, "state":{"running":true,"frozen":false,"final":false} } example patch request
 * data: {"id":"7b0dd4ea-19a1-4434-9034-529ebe55ab45","start_time":"2014-06-25T10:00:00+01"}
 * 
 *  or
 *  {"id":"wf2016","start_time":null}
 * 
 * @author pc2@ecs.csus.edu
 *
 */
@Path("/contest")
@Produces(MediaType.APPLICATION_JSON)
public class ContestService {

    private IInternalContest model;

    private IInternalController controller;

    /**
     * List of the possible types of requests which might be received from clients.
     * 
     * @author john
     */
    private enum StartTimeRequestType {
        ILLEGAL, SET_START_TO_UNDEFINED, SET_START_TO_SPECIFIED_DATE
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
     * // { &quot;starttime&quot;:1265335138.26 }
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
    public Response setStartime(@Context SecurityContext sc, String jsonInputString) {

        // DEBUG:
        // System.out.println ("Starttime PATCH: received the following request body: '" + jsonInputString + "'");
        controller.getLog().log(Log.DEBUG, "Contest Service PATCH: received the following request body: " + jsonInputString);

        // check authorization (verify requester is allowed to make this request)
        if (!sc.isUserInRole("admin")) {
            controller.getLog().log(Log.WARNING, "Contest Service: unauthorized Contest PATCH request (user not in admin role)");
            // return HTTP 401 response code per CLICS spec
            return Response.status(Status.UNAUTHORIZED).entity("You are not authorized to access this page").build();
        }

        // check for empty request
        if (jsonInputString == null || jsonInputString.length() == 0) {
            controller.getLog().log(Log.WARNING, "Contest Service: received invalid (empty) JSON string");
            // return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("Empty contest request").build();
        }

        // we got some potentially legal input; try parsing it for valid form
        Map<String, String> requestMap = parseJSONIntoMap(jsonInputString);

        // if the map is null then the parsing failed
        if (requestMap == null) {
            controller.getLog().log(Log.WARNING, "Contest Service PATCH: unable to parse JSON starttime string");
            // return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("Bad JSON starttime request").build();
        }

        // if we get here then the JSON parsed correctly; see if it contained "starttime" as a key
        if (!requestMap.containsKey("id")) {
            controller.getLog().log(Log.WARNING, "Contest Service PATCH: JSON input missing 'id' key: '" + jsonInputString + "'");
            // return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("Missing 'id' key in /contest request").build();

        } else {
            // validate id
            // TODO can the contestIdentifier be null?
            if (!model.getContestIdentifier().equals(requestMap.get("id"))) {
                controller.getLog().log(Log.WARNING, "Starttime PATCH Service: JSON mismatched 'id' key: '" + requestMap.get("id") + "'");
                // return HTTP 400 response code per CLICS spec
                return Response.status(Status.CONFLICT).entity("Invalid 'id' key in /contest request").build();
            }
        }
        // if we get here then the JSON parsed correctly; see if it contained "start_time" as a key
        if (!requestMap.containsKey("start_time")) {
            controller.getLog().log(Log.WARNING, "Contest Service PATCH: JSON input missing 'start_time' key: '" + jsonInputString + "'");
            // return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("Missing 'starttime' key in /contest request").build();
        }

        // verify the JSON didn't contain any OTHER key/value information
        if (requestMap.size() != 2) {
            controller.getLog().log(Log.WARNING, "Contest Service PATCH: JSON input contains illegal extra data: '" + jsonInputString + "'");
            // return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("Extra data in contest patch request").build();
        }

        // if we get here the JSON is valid and contains exactly one element: starttime
        // get the Object corresponding to "start_time"
        String startTimeValueString = requestMap.get("start_time");

        // DEBUG:
        // System.out.println("StarttimePut.setStartTime(): received start time value '" + startTimeValueString + "'");
        controller.getLog().log(Log.DEBUG, "Contest Service PATCH: received start time value '" + startTimeValueString + "'");

        StartTimeRequestType requestType = StartTimeRequestType.ILLEGAL;
        GregorianCalendar requestedStartTime = null;

        // check if we have a viable start_time string
        if (startTimeValueString != null && startTimeValueString.length() > 0) {

            if (startTimeValueString.trim().equalsIgnoreCase("null")) {

                requestType = StartTimeRequestType.SET_START_TO_UNDEFINED;

            } else {

                // parse the starttime value for a valid date
                requestedStartTime = getDate(startTimeValueString);
                if (requestedStartTime != null) {
                    requestType = StartTimeRequestType.SET_START_TO_SPECIFIED_DATE;
                    // } else {
                    // //null requestedStartTime means startTimeValueString failed to parse (wasn't a legal Unix epoch date);
                    // // do nothing -- leaving requestType set to "ILLEGAL"
                }
            }

        } else {

            // the starttime value was null or empty
            controller.getLog().log(Log.WARNING, "Contest Service PATCH: JSON input contains empty starttime value");
            // return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("Empty value in starttime request").build();
        }

        if (requestType == StartTimeRequestType.ILLEGAL) {

            // we can get here if the value was not "null" but also didn't parse to a valid date
            controller.getLog().log(Log.WARNING, "Contest Service PATCH: JSON input contains invalid starttime value: '" + startTimeValueString + "'");
            System.err.println("Contest Service PATCH: JSON input contains invalid starttime value: '" + startTimeValueString + "'");
            // return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("Bad value in starttime request").build();

        }

        // we have a legal request; check to insure contest has not already been started
        if (model.getContestTime().isContestStarted()) {
            // contest has started, cannot set scheduled start time
            controller.getLog().log(Log.WARNING, "Contest Service PATCH: request to set start time when contest has already started; ignored");
            // return HTTP 403 (Forbidden) response code per CLICS spec
            return Response.status(Status.FORBIDDEN).entity("Contest already started").build();
        }

        // get the scheduled start time and the current time
        GregorianCalendar scheduledStartTime = model.getContestInformation().getScheduledStartTime();
        GregorianCalendar now = new GregorianCalendar();
        // validate scheduleStartTime
        // if the contestTime has not started yet, but the scheduledStartTime was in the past
        if (!model.getContestTime().isContestStarted() && scheduledStartTime.before(now)) {
            // then clear it
            scheduledStartTime = null;
        }
        boolean success = false;

        switch (requestType) {

            case SET_START_TO_UNDEFINED:

                // check for less than 30 secs to scheduled start
                if (scheduledStartTime != null && scheduledStartTime.getTimeInMillis() < (now.getTimeInMillis() + 30000)) {

                    // we have request to set start to "null", but we have a scheduled start and we're
                    // within 10 secs of it; cannot set scheduled start time to undefined (per CLICS spec);
                    controller.getLog().log(Log.WARNING, "Contest Service PATCH: received request to set start time to 'null' with less than 10 seconds to go before start; ignored");
                    // return HTTP 403 (Forbidden) response code per CLICS spec
                    return Response.status(Status.FORBIDDEN).entity("Cannot change start time to 'null' within 30 seconds of already-scheduled start").build();

                } else {

                    // ok to set scheduled start to "undefined"
                    controller.getLog().log(Log.INFO, "ContestService.setStarttime(): setting contest start time to \"null\".");
                    success = setScheduledStart(null);
                    if (success) {
                        return Response.ok().entity("Contest start time updated to \"null\" (no scheduled start)").build();
                    } else {
                        controller.getLog().log(Log.SEVERE, "ContestService.setStarttime(): error setting contest start time to \"undefined\".");
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Server failed to set start time correctly").build();
                    }
                }

                // break; //can't get here, so Eclipse won't allow the explicit break

            case SET_START_TO_SPECIFIED_DATE:

                // check for less than 30 sec before scheduled start
                if (scheduledStartTime != null && scheduledStartTime.getTimeInMillis() < (now.getTimeInMillis() + 30000)) {
                    // we're within 30 secs of scheduled start; cannot set scheduled start time to new value (per CLICS spec);
                    controller.getLog().log(Log.WARNING, "Contest Service: received request to set start time with less than 30 seconds to go before start; ignored");
                    // return HTTP 403 (Forbidden) response code per CLICS spec
                    return Response.status(Status.FORBIDDEN).entity("Cannot change to new start time within 30 seconds of already-scheduled start").build();
                }

                // check for less than 30 sec in the future
                if (requestedStartTime.getTimeInMillis() < (now.getTimeInMillis() + 30000)) {

                    // requested start time is less than 30sec from now; cannot set (per CLICS spec);
                    controller.getLog().log(Log.WARNING, "Contest Service: received request to set start time less than 30 seconds in the future; ignored");
                    // return HTTP 403 (Forbidden) response code per CLICS spec
                    return Response.status(Status.FORBIDDEN).entity("Cannot set start time less than 30 seconds in the future").build();
                }

                // ok to set scheduled start to a specific time
                controller.getLog().log(Log.INFO, "ContestService.setStarttime(): setting contest start time to " + requestedStartTime);
                success = setScheduledStart(requestedStartTime);
                if (success) {
                    return Response.ok().entity("/contest").build();
                } else {
                    controller.getLog().log(Log.SEVERE, "ContestService.setStarttime(): error setting contest start time to requested date.");
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Server failed to set start time correctly").build();
                }

                // break;

            default:
                // shouldn't be able to get here!
                controller.getLog().log(Log.SEVERE, "ContestService.setStarttime(): unknown default condition: request type = " + requestType);
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Unknown condition in server: request type = " + requestType).build();
        }

    }

    /**
     * Parses the given String and returns a {@link GregorianCalendar} object if the String represents a valid Unix Epoch date; otherwise returns null.
     * 
     * @param startTimeValueString
     *            a String containing a date in ISO 8601 format.
     * @return the GregorianCalendar date/time represented by the String, or null if the String does not represent a valid date/time
     */
    private GregorianCalendar getDate(String startTimeValueString) {
        GregorianCalendar theDate = new GregorianCalendar();
        try {
            theDate.setTime(Utilities.getIso8601formatter().parse(startTimeValueString));
        } catch (ParseException e) {
            try {
                e.printStackTrace();
                theDate.setTime(Utilities.getIso8601formatterWithMS().parse(startTimeValueString));
            } catch (ParseException e2) {
                controller.getLog().throwing("ContestService", "getDate", e2);
                e2.printStackTrace();
                return null;
            }
        }

        // debug
        // System.out.println ("ContestService.getDate(): returning a GregorianCalendar with a date of " + theDate.getTimeInMillis());
        controller.getLog().log(Log.DEBUG, "ContestService.getDate(): returning a GregorianCalendar with a start date of " + theDate.getTimeInMillis());

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
     * @return true if the method was successful in setting the scheduled start time; false otherwise
     */
    private boolean setScheduledStart(GregorianCalendar theDate) {

        // get the local model's ContestInformation
        ContestInformation ci = model.getContestInformation();
        if (ci != null) {
            // set the new start date/time into the ContestInformation
            ci.setScheduledStartTime(theDate);
            if (theDate != null) {
                // if we have a valid start date, set the contest to auto-start
                ci.setAutoStartContest(true);
            }
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
     * @param jsonRequestString
     *            a JSON string specifying a starttime request in CLICS format
     * @return a Map of the JSON string key-to-value pairs as Strings, or null if the input JSON does not parse as a Map(String->String).
     */
    private Map<String, String> parseJSONIntoMap(String jsonRequestString) {

        controller.getLog().log(Log.INFO, "StarttimePUT.parseJSONIntoMap(): attempting to convert JSON input '" + jsonRequestString + "' into Map");

        // debug
        // System.out.println ("ContestService.parseJSONIntoMap(): creating Map from input string '" + jsonRequestString + "'");

        // use Jackson's ObjectMapper to construct a Map of Strings-to-Strings from the JSON input
        final ObjectMapper mapper = new ObjectMapper();
        final MapType mapType = mapper.getTypeFactory().constructMapType(Map.class, String.class, String.class);
        final Map<String, String> jsonDataMap;

        try {
            jsonDataMap = mapper.readValue(jsonRequestString, mapType);
        } catch (JsonMappingException e) {
            // error parsing JSON input
            controller.getLog().log(Log.WARNING, "ContestService.parseJSONIntoMap(): JsonMappingException parsing JSON input '" + jsonRequestString + "'");
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            controller.getLog().log(Log.WARNING, "ContestService.parseJSONIntoMap(): IOException parsing JSON input '" + jsonRequestString + "'");
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
    public Response getContest() {

        // from the CLI Wiki Contest_Start_Interface spec:
        /*
         * {"id":"7b0dd4ea-19a1-4434-9034-529ebe55ab45","name":"2014 ICPC World Finals",
         * "formal_name":"38th Annual World Finals of the ACM International Collegiate Programming Contest",
         * "start_time":"2014-06-25T10:00:00+01","duration":"5:00:00","scoreboard_freeze_duration":"1:00:00","penalty_time":20,
         * "state":{"running":true,"frozen":false,"final":false}}
         */
        String id = model.getContestIdentifier();
        ContestInformation ci = model.getContestInformation();
        String name = ci.getContestShortName();
        String formalName = ci.getContestTitle();
        if (name == null || name.equals("")) {
            name = formalName;
        }
        String duration = model.getContestTime().getContestLengthStr();
        String scoreboardFreezeDuration = ci.getFreezeTime();
        String startTime = "null";
        if (model.getContestTime().isContestStarted()) {
            if (model.getContestTime().isContestRunning()) {
                startTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(model.getContestTime().getContestStartTime().getTime());
            }
            // else startTime is null during a pause
        } else {
            // contest has not started, check for a scheduledStartTime
            Calendar calendar = ci.getScheduledStartTime();
            if (calendar != null) {
                Date date = calendar.getTime();
                startTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(date); 
            }
        }
        String penaltyTime = ci.getScoringProperties().getProperty(DefaultScoringAlgorithm.POINTS_PER_NO, "20");
        boolean stateRunning = model.getContestTime().isContestRunning();
        boolean skipStateFrozen = false;
        boolean stateFrozen = false;
        if (ci.getFreezeTime() != null) {
            ci.getFreezeTime();
            long elapsed = model.getContestTime().getElapsedSecs();
            long length = model.getContestTime().getContestLengthSecs();
            long freezeTime = Utilities.convertStringToSeconds(ci.getFreezeTime());
            if ((length - elapsed) > freezeTime ) {
                stateFrozen = false;
            } else {
                stateFrozen = true;
            }
        } else {
            skipStateFrozen = true;
        }
        boolean stateFinal = false;
        if (model.getFinalizeData() != null) {
            stateFinal = model.getFinalizeData().isCertified();
        }
        
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode element = mapper.createObjectNode();
        element.put("id", id);
        element.put("name", name);
        if (formalName != null && !formalName.equals("")) {
            element.put("formal_name",formalName);
        }
        if (startTime.equals("null")) {
            startTime = null;
        }
        element.put("start_time", startTime);
        element.put("duration",duration);
        element.put("scoreboard_freeze_duration",scoreboardFreezeDuration);
        element.put("penalty_time",penaltyTime);
        ObjectNode state = mapper.createObjectNode();
        state.put("running",stateRunning);
        if (!skipStateFrozen) {
            state.put("frozen",stateFrozen);
        }
        state.put("final",stateFinal);
        element.set("state", state);

        return Response.ok(element.toString(),MediaType.APPLICATION_JSON).build();
    }
}
