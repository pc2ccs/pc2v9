// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Category;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClarificationAnswer;
import edu.csus.ecs.pc2.core.model.ClarificationEvent;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IClarificationListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.services.core.JSONUtilities;
import edu.csus.ecs.pc2.services.eventFeed.WebServer;

/**
 * WebService to handle clarifications
 *
 * @author John Buck
 *
 */
@Path("/contests/{contestId}/clarifications")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class ClarificationService implements Feature {

    private static final int MAX_CLAR_RESPONSE_WAIT_MS = 10000;
    private static final int CLAR_RESPONSE_CHECK_FREQ_MS = 100;
    private static final String DEFAULT_CATEGORY = "General";

    private IInternalContest model;

    private IInternalController controller;

    private Clarification waitClarification = null;

    public ClarificationService(IInternalContest inContest, IInternalController inController) {
        super();
        this.model = inContest;
        this.controller = inController;
    }

    /**
     * This method returns a representation of the current contest clarifications in JSON format. The returned value is a JSON array with one clarification description per array element, complying with 2023-06
     *
     * @param sc security info for the user making the request
     * @param contestId Contest for which info is requested
     * @return a {@link Response} object containing the contest languages in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClarifications(@Context SecurityContext sc, @PathParam("contestId") String contestId) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == false) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // get the groups from the contest
        Clarification[] clarifications = model.getClarifications();

        ArrayList<CLICSClarification> clarList = new ArrayList<CLICSClarification>();

        // these are the only 2 that have special rules.
        boolean isStaff = sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) || sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE);
        boolean isTeam = sc.isUserInRole(WebServer.WEBAPI_ROLE_TEAM);

        String user = sc.getUserPrincipal().getName();

        // create list of clarifications to send back
        for (Clarification clarification: clarifications) {
            if (clarification.isSendToAll() || isStaff || (isTeam && isClarificationForUser(clarification, user))) {
                if(!clarification.isAnnounced()) {
                    clarList.add(new CLICSClarification(model, clarification));
                }
                if(clarification.isAnsweredorAnnounced()) {
                    ClarificationAnswer[] clarAnswers = clarification.getClarificationAnswers();
                    clarList.add(new CLICSClarification(model, clarification, clarAnswers[clarAnswers.length-1]));
                }
            }
        }
        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            String json = mapper.writeValueAsString(clarList);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for clarifications " + e.getMessage()).build();
        }
    }


    /**
     * This method returns a representation of the current contest clarification requested in JSON format. The returned value is a single clarification in json, Complying with 2023-06
     *
     * @param sc security info for the user making the request
     * @param contestId Contest for which info is requested
     * @param clarificationId the id of the desired clarification
     * @return a {@link Response} object containing the contest languages in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{clarificationId}/")
    public Response getClarification(@Context SecurityContext sc, @PathParam("contestId") String contestId, @PathParam("clarificationId") String clarificationId) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == false) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        // get the groups from the contest
        Clarification[] clarifications = model.getClarifications();

        // these are the only 2 that have special rules.
        boolean isStaff = sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) || sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE);
        boolean isTeam = sc.isUserInRole(WebServer.WEBAPI_ROLE_TEAM);

        String user = sc.getUserPrincipal().getName();

        ClarificationAnswer[] clarAnswers = null;

        // keep track of whether the clarificationId specified was for the question, in which case this will
        // be set to non-null
        Clarification clarNoAnswer = null;

        // create list of clarifications to send back
        for (Clarification clarification: clarifications) {
            if (clarification.isSendToAll() || isStaff || (isTeam && isClarificationForUser(clarification, user))) {
                if(clarification.getElementId().toString().equals(clarificationId)) {
                    clarNoAnswer = clarification;
                }
                clarAnswers = clarification.getClarificationAnswers();
                if (clarAnswers != null) {
                    for (ClarificationAnswer clarAns: clarAnswers) {
                        if (clarAns.getElementId().toString().equals(clarificationId)) {
                            try {
                                ObjectMapper mapper = JSONUtilities.getObjectMapper();
                                String json = mapper.writeValueAsString(new CLICSClarification(model, clarification, clarAns));
                                return Response.ok(json, MediaType.APPLICATION_JSON).build();
                            } catch (Exception e) {
                                return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for clarification " + clarificationId + " " + e.getMessage()).build();
                            }
                        }
                    }
                }
            }
        }
        // if set, this means the id of the clarification was specified, not the answer, so return that
        if(clarNoAnswer != null) {
            try {
                ObjectMapper mapper = JSONUtilities.getObjectMapper();
                String json = mapper.writeValueAsString(new CLICSClarification(model, clarNoAnswer, null));
                return Response.ok(json, MediaType.APPLICATION_JSON).build();
            } catch (Exception e) {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for clarification " + clarificationId + " " + e.getMessage()).build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Post a new clarification
     *
     * @param servletRequest details of request
     * @param sc requesting user's authorization info
     * @param contestId The contest
     * @param jsonInputString For non-admin, must not include id, to_team_id, time or contest_time.  For admin, must not include id.
     * @return json for the clarification, including the (new) id
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public synchronized Response addNewClarification(@Context HttpServletRequest servletRequest, @Context SecurityContext sc, @PathParam("contestId") String contestId, String jsonInputString) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == false) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // only admin, judge, or team can create a clarification.  And team can do it only if contest is started.
        if(!sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) && !sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE) && (!sc.isUserInRole(WebServer.WEBAPI_ROLE_TEAM) || !model.getContestTime().isContestStarted())) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        // check for empty request
        if (jsonInputString == null || jsonInputString.length() == 0) {
            // return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("empty json").build();
        }

        CLICSClarification clar = CLICSClarification.fromJSON(jsonInputString);
        if(clar == null) {
            // return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("invalid json supplied").build();
        }
        if(clar.getId() != null) {
            // return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("may not include id property").build();
        }
        if(StringUtilities.isEmpty(clar.getText())) {
            return Response.status(Status.BAD_REQUEST).entity("text must not be empty").build();
        }

        if(!sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) && !sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE)
            && (clar.getTo_team_id() != null || clar.getTime() != null || clar.getContest_time() != null || clar.getReply_to_id() != null)) {
            return Response.status(Status.BAD_REQUEST).entity("may not include one or more properties").build();
        }

        String user = sc.getUserPrincipal().getName();

        // TODO: For admin, spec says they MUST specify time, but we do not support that, so log a message for now
        if(clar.getTime() != null) {
            controller.getLog().log(Level.WARNING, "User " + user + " attempted to submit (POST) a clarification with the time property " + clar.getTime() + "; not currently supported");
        }

        // if team specifies "from id", it must be that of the current user.
        if(!StringUtilities.isEmpty(clar.getFrom_team_id())) {
            String fullUser = "team" + clar.getFrom_team_id();
            if(sc.isUserInRole(WebServer.WEBAPI_ROLE_TEAM) && !user.equals(fullUser)) {
                return Response.status(Status.FORBIDDEN).build();
            } else {
                user = fullUser;
            }
        }
        ClientId clientId = getClientIdFromUser(user);
        if(clientId != null && model.getAccount(clientId) != null) {
            String problemId = clar.getProblem_id();
            Problem problem = null;
            // If no problem_id specified, then it's a "general" clar request
            if(problemId == null || problemId.isEmpty()) {
                for(Problem findProb : model.getCategories()) {
                    if(findProb.getDisplayName().equals(DEFAULT_CATEGORY)) {
                        problem = findProb;
                        break;
                    }
                }
                if(problem == null) {
                    // if not problem found matching default, then make one up
                    problem = new Category(DEFAULT_CATEGORY);
                }
            } else {
                problem = getProblemFromId(clar.getProblem_id());
            }
            if(problem != null) {
                ClarificationAnswer clarificationAnswer = null;
                ClarificationListener clarListener = new ClarificationListener();

                // Start listening for clarification additions
                model.addClarificationListener(clarListener);
                // clear out response (created Clarification)
                waitClarification = null;

                // Is this a reply?  If so, we have to make it look like a new clarification was added
                String replyId = clar.getReply_to_id();
                if(replyId != null && !replyId.isEmpty()) {
                    // This means we have to update an existing clar.  eg.  this is a ClarificationAnswer
                    Clarification replyToClar = findClarificationFromId(replyId);
                    if(replyToClar == null) {
                        // No longer care about clar additions
                        model.removeClarificationListener(clarListener);
                        return Response.status(Response.Status.NOT_FOUND).build();
                    }
                    clarificationAnswer = new ClarificationAnswer(clar.getText(), clientId,
                        clar.getTo_team_id() == null, model.getContestTime());
                    replyToClar.addAnswer(clarificationAnswer);
                    clarListener.setWaitAnswerId(replyToClar.getElementId());
                    controller.submitClarificationAnswer(replyToClar);
                } else if(clar.getFrom_team_id() == null){
                    // this is an announcement since its from a judge/admin.  Really, it COULD be a question (clarification) but it does
                    // not fit in the model of CLICS
                    // tell listener what to wait for
                    // TODO: allow submitAnnouncement to take 'null' for the 2 arrays.
                    clarListener.setWaitId(controller.submitAnnouncement(clientId, problem, clar.getText(), new ElementId[0], new ClientId[0]));
                } else {
                    // tell listener what to wait for
                    clarListener.setWaitId(controller.submitClarification(clientId, problem, clar.getText()));
                }

                // wait for response from submit clar so we can get the ID and times for the response
                Clarification clarResponse = waitForClarificationResponse(user);
                // No longer care about clar additions
                model.removeClarificationListener(clarListener);

                if(clarResponse == null) {
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating clarification").build();
                }
                // fill in fields we were waiting for
                if(clarificationAnswer != null) {
                    clar.setId(clarificationAnswer.getElementId().toString());
                    clar.setTime(Utilities.getIso8601formatterWithMS().format(clarificationAnswer.getDate()));
                    clar.setContest_time(ContestTime.formatTimeMS(clarificationAnswer.getElapsedMS()));
                } else {
                    clar.setId(clarResponse.getElementId().toString());
                    clar.setTime(Utilities.getIso8601formatterWithMS().format(clarResponse.getCreateDate()));
                    clar.setContest_time(ContestTime.formatTimeMS(clarResponse.getElapsedMS()));
                }
                try {
                    ObjectMapper mapper = JSONUtilities.getObjectMapper();
                    String json = mapper.writeValueAsString(clar);
                    return Response.ok(json, MediaType.APPLICATION_JSON).build();
                } catch (Exception e) {
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for clarification " + e.getMessage()).build();
                }

            } else {
                controller.getLog().log(Log.WARNING, "Can not find problem for id " + clar.getProblem_id());
            }
        } else {
            controller.getLog().log(Log.WARNING, "Can not find client id for " + sc.getUserPrincipal().getName());
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Put add a clarification with a specific id
     *
     * @param servletRequest details of request
     * @param sc requesting user's authorization info
     * @param contestId The contest
     * @param jsonInputString citation and team_ids json for the new award
     * @return json for the award, including the id
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public synchronized Response putAdminClarification(@Context HttpServletRequest servletRequest, @Context SecurityContext sc, @PathParam("contestId") String contestId, String jsonInputString) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == false) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // only admin or judge can update a clarification.
        if(!sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) && !sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        // check for empty request
        if (jsonInputString == null || jsonInputString.length() == 0) {
            // return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("empty json").build();
        }

        CLICSClarification clar = CLICSClarification.fromJSON(jsonInputString);
        if(clar == null) {
            // return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("invalid json supplied").build();
        }

        if(StringUtilities.isEmpty(clar.getText())) {
            return Response.status(Status.BAD_REQUEST).entity("text must not be empty").build();
        }
        // id required for put
        if(clar.getId() == null) {
            // return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("must include id property").build();
        }
        // time is also required
        if(clar.getTime() == null || clar.getTime().isEmpty()) {
            // return HTTP 400 response code per CLICS spec
            return Response.status(Status.BAD_REQUEST).entity("must include time property").build();
        }

        controller.getLog().log(Level.WARNING, "Admin PUT clarification is not implemented due to forcing the ID");
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    /**
     * After submitting a clarification, this waits for the response from the server since the operation
     * is asynchronous (the server actually creates the clar)
     *
     * @param user The user adding the clar
     * @return created pc2 clarification or null if it couldnt be created or it timed out
     */
    Clarification waitForClarificationResponse(String user) {

        int waitedMS = 0;

        // wait for callback to clarification listener -- but only for up to a limit
        while (waitClarification == null && waitedMS < MAX_CLAR_RESPONSE_WAIT_MS) {
            try {
                Thread.sleep(CLAR_RESPONSE_CHECK_FREQ_MS);
            } catch (InterruptedException e) {
                controller.getLog().throwing("ClarificationService", "waitForClarificationResponse", e);
            }
            waitedMS += CLAR_RESPONSE_CHECK_FREQ_MS;
        }

        if(waitClarification == null) {
            controller.getLog().log(Level.WARNING, "ClarificationService timeout waiting for new clar to be created for " + user);
        }
        return(waitClarification);
    }

    /**
     * Check if the supplied clarification is from/to the supplied user
     *
     * @param clarification the clarification to check
     * @param user the user to check
     * @return true if the user is allowed to see this clarification
     */
    private boolean isClarificationForUser(Clarification clarification, String user) {
        boolean ret = false;

        // Quick return for easy case
        if(clarification.getSubmitter().getName().equals(user)) {
            return(true);
        }

        // harder cases now check if it was targeted to the team specifically
        ClientId [] destTeams = clarification.getAllDestinationsTeam();
        if(destTeams != null && destTeams.length > 0) {
            ret = isUserInDestination(user,  destTeams);
        }
        if(!ret) {
            // harder still, check groups
            ElementId [] destGroups = clarification.getAllDestinationsGroup();
            if(destGroups != null && destGroups.length > 0) {
                ret = this.isUserInDestinationGroup(getAccountFromUser(user), destGroups);
            }
        }
        return(ret);
    }

    /**
     * Tests if the supplied user context has a role to submit clarifications as a team
     *
     * @param sc User's security context
     * @return true of the user can submit clarifications
     */
    public static boolean isTeamSubmitClarificationAllowed(SecurityContext sc) {
        return(sc.isUserInRole(WebServer.WEBAPI_ROLE_TEAM));
    }


    /**
     * Tests if the supplied user context has a role to submit clarifications
     *
     * @param sc User's security context
     * @return true of the user can submit clarifications
     */
    public static boolean isAdminSubmitClarificationAllowed(SecurityContext sc) {
        return(sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN));
    }

    /**
     * Tests if the supplied user context has a role to submit clarifications on behalf of a team
     *
     * @param sc User's security context
     * @return true of the user can submit clarifications on behalf of a team
     */
    public static boolean isProxySubmitClarificationAllowed(SecurityContext sc) {
        return(sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN));
    }

    /**
     * Retrieve access information about this endpoint for the supplied user's security context
     *
     * @param sc User's security information
     * @return CLICSEndpoint object if the user can access this endpoint's properties, null otherwise
     */
    public static CLICSEndpoint getEndpointProperties(SecurityContext sc) {
        return(new CLICSEndpoint("clarifications", JSONUtilities.getJsonProperties(CLICSClarification.class)));
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

        // use Jackson's ObjectMapper to construct a Map of Strings-to-Strings from the JSON input
        final ObjectMapper mapper = new ObjectMapper();
        final MapType mapType = mapper.getTypeFactory().constructMapType(Map.class, String.class, String.class);
        final Map<String, String> jsonDataMap;

        try {
            jsonDataMap = mapper.readValue(jsonRequestString, mapType);
        } catch (JsonMappingException e) {
            // error parsing JSON input
            controller.getLog().log(Log.WARNING, contestId + ": parseJSONIntoMap(): JsonMappingException parsing JSON input '" + jsonRequestString + "'", e);
            return null;
        } catch (IOException e) {
            controller.getLog().log(Log.WARNING, contestId + ": parseJSONIntoMap(): IOException parsing JSON input '" + jsonRequestString + "'", e);
            return null;
        }

        return jsonDataMap;
    }

    /**
     * Returns a ClientId based on the user supplied.  eg. "team99", "administrator1", etc.
     * @param user eg. team99
     * @return The ClientId created, or null if the user is bad
     */
    private ClientId getClientIdFromUser(String user) {
        ClientId clientId = null;

        // basically, need to match lower case letters followed by digits
        Matcher matcher = Pattern.compile("^([a-z]+)([0-9]+)$").matcher(user);
        if(matcher.matches()) {
            try {
                clientId = new ClientId(model.getSiteNumber(), ClientType.Type.valueOf(matcher.group(1).toUpperCase()), Integer.parseInt(matcher.group(2)));
            } catch (Exception e) {
                controller.getLog().log(Log.WARNING, "Can not convert the supplied user " + user + " to a ClientId", e);
            }
        }
        return clientId;
    }

    /**
     * Returns the Account based on the user supplied.
     *
     * @param user
     * @return Account for user, or null if no account found
     */
    private Account getAccountFromUser(String user) {
        if(model.getAccounts() != null) {
            for(Account acct : model.getAccounts()) {
                if(acct.getDefaultDisplayName(acct.getClientId()).equals(user)) {
                    return(acct);
                }
            }
        }
        return(null);
    }

    /**
     * Returns the the Problem object for supplied id (short name) or null if none found
     *
     * @param id shortname of problem
     * @return Problem object or null
     */
    private Problem getProblemFromId(String id) {
        for(Problem problem : model.getProblems()) {
            if(problem.getShortName().equals(id)) {
                return(problem);
            }
        }
        return(null);
    }

    /**
     * Check if the user is in the array of client id's supplied
     *
     * @param user name, eg team101
     * @param destinations array of ClientId's to check
     * @return true if found, false otherwise
     */
    boolean isUserInDestination(String user, ClientId [] destinations) {
        boolean ret = false;

        if(destinations != null) {
            Account clientAccount;

            for(ClientId destClient : destinations) {
                clientAccount = model.getAccount(destClient);
                if(clientAccount != null && clientAccount.getDefaultDisplayName(destClient).equals(user)) {
                    ret = true;
                    break;
                }
            }
        }
        return(ret);
    }

    /**
     * Check if the user account is in the array of group ElementId's supplied
     *
     * @param userAccount Account for the user to check
     * @param destinations array of Group ElementId's to check
     * @return true if found, false otherwise
     */
    boolean isUserInDestinationGroup(Account userAccount, ElementId [] destinations) {
        boolean ret = false;

        if(userAccount != null && destinations != null) {
            for(ElementId groupElement : destinations) {
                if(userAccount.isGroupMember(groupElement)) {
                    ret = true;
                    break;
                }
            }
        }
        return(false);
    }

    /**
     * Try to find the Clarification from its id
     *
     * @param szClarId This ID of the clar
     * @return the Clarification if found or null if not found
     */
    private Clarification findClarificationFromId(String clarId) {

        Clarification clar = null;

        // create list of clarifications to send back
        for (Clarification clarification: model.getClarifications()) {
            if(clarification.getElementId().toString().equals(clarId)) {
                clar = clarification;
                break;
            }
        }
        return(clar);
    }

    /**
     * Clarification Listener.
     *
     * @author pc2@ecs.csus.edu
     */
    protected class ClarificationListener implements IClarificationListener {
        private ElementId waitId = null;
        private ElementId waitAnsId = null;

        @Override
        public void clarificationAdded(ClarificationEvent event) {
            Clarification newClar = event.getClarification();

            // Only match the clar we're waiting for
            if(waitId != null && newClar != null && waitId.equals(newClar.getElementId())) {
                waitClarification = event.getClarification();
            }
        }

        @Override
        public void clarificationChanged(ClarificationEvent event) {
            Clarification newClar = event.getClarification();

            // Only match the clar we're waiting for
            if(waitAnsId != null && newClar != null && waitAnsId.equals(newClar.getElementId())) {
                waitClarification = event.getClarification();
            }
        }

        @Override
        public void clarificationRemoved(ClarificationEvent event) {
            // ignore
        }

        @Override
        public void refreshClarfications(ClarificationEvent event) {
            // ignore
        }

        void setWaitId(ElementId id) {
            waitId = id;
        }

        void setWaitAnswerId(ElementId id) {
            waitAnsId = id;
        }
    }

    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }
}
