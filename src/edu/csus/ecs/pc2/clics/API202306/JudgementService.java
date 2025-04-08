// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

import javax.inject.Singleton;
import javax.ws.rs.GET;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.services.core.JSONUtilities;
import edu.csus.ecs.pc2.services.eventFeed.WebServer;

/**
 * WebService to handle judgements endpoint
 *
 * @author John Buck
 *
 */
@Path("/contests/{contestId}/judgements")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class JudgementService implements Feature {
    private static final int MIN_NUM_SOLVED_FREEZE = 3;

    private IInternalContest model;

    @SuppressWarnings("unused")
    private IInternalController controller;

    public JudgementService(IInternalContest inContest, IInternalController inController) {
        super();
        this.model = inContest;
        this.controller = inController;
    }

    /**
     * This method returns a representation of judgments for the specified contest in JSON format. The returned value is a JSON array with one judgment description per array element, complying with 2023-06
     *
     * @param sc User's information
     * @param contestId The contest
     * @return a {@link Response} object containing the contest judgments in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJudgements(@Context SecurityContext sc, @PathParam("contestId") String contestId) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == false) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        long freezeTime = Utilities.getFreezeTime(model);
        Set<String> exceptProps = new HashSet<String>();
        StringJoiner allJudgments = new StringJoiner(",");
        ObjectMapper mapper = JSONUtilities.getObjectMapper();
        CLICSJudgement cJudgment;
        Run.RunStates status;
        HashMap<String,HashMap<ElementId,Boolean>> allTeamsSolvedMap = null;
        HashMap<ElementId,Boolean> teamSolvedMap;
        String user = sc.getUserPrincipal().getName();
        String runUser;
        boolean isAdminUser = sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) || sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE);
        boolean isAnalyst = sc.isUserInRole(WebServer.WEBAPI_ROLE_ANALYST);
        boolean includeMaxRunTime;

        for (Run run: model.getRuns()) {

            status = run.getStatus();
            // Check if judged or being judged - can't generate a judgment entry if not one of these states
            if(run.isJudged() || (status == RunStates.BEING_JUDGED || status == RunStates.BEING_COMPUTER_JUDGED)) {
                includeMaxRunTime = isAdminUser;
                runUser = run.getSubmitter().getName();
                // If not admin or judge or the team itself, can not see runs after freeze time
                if (!isAdminUser && !user.equals(runUser)) {
                    // if run is after scoreboard freeze, we have to check if it's for a team that solved < MIN_NUM_SOLVED_FREEZE problems
                    if (run.getElapsedMS() / 1000 > freezeTime && !model.getContestInformation().isUnfrozen()) {
                        if(allTeamsSolvedMap == null) {
                            // compute solved map first time we need it, since it's expensive to do.
                            allTeamsSolvedMap = computeNumberSolvedMap();
                        }
                        teamSolvedMap = allTeamsSolvedMap.get(runUser);
                        // if the submitter of this run solved at least MIN_NUM_SOLVED_FREEZE,
                        // then we don't return it's info unless its the actual team that's asking
                        if(teamSolvedMap != null && teamSolvedMap.size() >= MIN_NUM_SOLVED_FREEZE) {
                            continue;
                        }
                        includeMaxRunTime = false;
                    } else if(isAnalyst) {
                        includeMaxRunTime = true;
                    }
                }

                exceptProps.clear();
                cJudgment = new CLICSJudgement(model, controller, run, exceptProps);
                // Remove max_run_time property if indicated
                if(!includeMaxRunTime) {
                    exceptProps.add("max_run_time");
                }
                try {
                    // for this judgment, create filter to omit unused/bad properties (max_run_time in this case)
                    SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(exceptProps);
                    FilterProvider fp = new SimpleFilterProvider().addFilter("rtFilter", filter).setFailOnUnknownId(false);
                    // generate json with only properties we want and add to CSV list.
                    allJudgments.add(mapper.writer(fp).writeValueAsString(cJudgment));
                } catch (Exception e) {
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for judgment " + run.getElementId().toString() + " " + e.getMessage()).build();
                }
            }
        }
        return Response.ok("[" + allJudgments.toString() + "]", MediaType.APPLICATION_JSON).build();
    }

    /**
     * Returns a representation of a specified judgment for the specified contest in JSON format. The returned value compliant with 2023-06
     *
     * @param sc User's infor
     * @param contestId The contest
     * @param judgementId The judgement we're looking for
     * @return response
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{judgementId}/")
    public Response getJudgement(@Context SecurityContext sc, @PathParam("contestId") String contestId, @PathParam("judgementId") String judgementId) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == true) {
           long freezeTime = Utilities.getFreezeTime(model);
           Run.RunStates status;
           HashMap<String,HashMap<ElementId,Boolean>> allTeamsSolvedMap = null;
           HashMap<ElementId,Boolean> teamSolvedMap;
           String user = sc.getUserPrincipal().getName();
           String runUser;
           boolean isAdminUser = sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) || sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE);
           // admins can always see max_run_time
           boolean includeMaxRunTime = isAdminUser;

           for(Run run: model.getRuns()) {
               // judgementId's match runId's - we found the one we are looking for
               if (run.getElementId().toString().equals(judgementId)) {
                   status = run.getStatus();

                   // Check if judged or being judged - can't generate a judgment if not one of these states
                   if(run.isJudged() || (status == RunStates.BEING_JUDGED || status == RunStates.BEING_COMPUTER_JUDGED)) {
                       runUser = run.getSubmitter().getName();
                       // If not admin or judge or the team itself, can not see runs after freeze time
                       if (!isAdminUser && !user.equals(runUser)) {
                            // if run is after scoreboard freeze, and not unfrozen, do not return info for it
                            if (run.getElapsedMS() / 1000 > freezeTime && !model.getContestInformation().isUnfrozen()) {
                                // compute solved map first time we need it, since it's expensive to do.
                                allTeamsSolvedMap = computeNumberSolvedMap();

                                teamSolvedMap = allTeamsSolvedMap.get(runUser);
                                // if the submitter of this run solved at least MIN_NUM_SOLVED_FREEZE,
                                // then we don't return it's info unless its the actual team that's asking
                                if(teamSolvedMap != null && teamSolvedMap.size() >= MIN_NUM_SOLVED_FREEZE) {
                                    break;
                                }
                                includeMaxRunTime = false;
                            } else if(sc.isUserInRole(WebServer.WEBAPI_ROLE_ANALYST)) {
                                includeMaxRunTime = true;
                            }
                        }
                        Set<String> exceptProps = new HashSet<String>();
                        CLICSJudgement cJudgment = new CLICSJudgement(model, controller, run, exceptProps);
                        // Remove max_run_time property if indicated
                        if(!includeMaxRunTime) {
                            exceptProps.add("max_run_time");
                        }
                        try {
                            ObjectMapper mapper = JSONUtilities.getObjectMapper();
                            // create filter to omit unused/bad properties (location, for example)
                            SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(exceptProps);
                            FilterProvider fp = new SimpleFilterProvider().addFilter("rtFilter", filter);
                            String json = mapper.writer(fp).writeValueAsString(cJudgment);
                            return Response.ok(json, MediaType.APPLICATION_JSON).build();
                        } catch (Exception e) {
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for judgementId " + judgementId + " " + e.getMessage()).build();
                        }
                    }
                }
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Determine number of problems each team has solved
     *
     * @return a map of usernames to a map of problems solved
     */
    private HashMap<String,HashMap<ElementId,Boolean>> computeNumberSolvedMap() {
        HashMap<String,HashMap<ElementId,Boolean>> solvedMap = new HashMap<String,HashMap<ElementId,Boolean>>();

        String teamName;
        ElementId probId;
        HashMap<ElementId, Boolean> probMap;

        // we have to go through each run and look for solved ones
        for(Run run: model.getRuns()) {
            if(run.isSolved()) {
                teamName = run.getSubmitter().getName();
                // see if we know about this team's solved problems
                probMap = solvedMap.get(teamName);
                if(probMap == null) {
                    probMap = new HashMap<ElementId, Boolean>();
                    solvedMap.put(teamName, probMap);
                }
                probId = run.getProblemId();
                // if this team did not solve the problem yet, remember it did now
                if(probMap.get(probId) == null) {
                    probMap.put(probId, Boolean.valueOf(true));
                }
            }
        }
        return(solvedMap);
    }

    /**
     * Retrieve access information about this endpoint for the supplied user's security context
     *
     * @param contest The contest is included in case the inclusion of a property depends on the permissions
     *        set for the connected client.  It is included for uniformity since this method is called as a result
     *        of introspection, and the caller does not know what the callee may need.  Therefore, the contest is
     *        always included, as is the SecurityContext below (for the same reason).
     * @param sc User's security information
     * @return CLICSEndpoint object if the user can access this endpoint's properties, null otherwise
     */
    public static CLICSEndpoint getEndpointProperties(IInternalContest contest, SecurityContext sc) {
        String [] props = JSONUtilities.getJsonProperties(CLICSJudgement.class);

        // Non-admin users have restrictions:
        // max_run_time: Public & teams can never see it, analysts only if not in freeze
        // other properties: we always present them in the access endpoint since some
        // judgments may be visible, even if in freeze, so we have to say these properties
        // are available.
        if(!sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) && !sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE)) {
            ArrayList<String> aprops = new ArrayList<String>();
            boolean isAnalyst = sc.isUserInRole(WebServer.WEBAPI_ROLE_ANALYST);
            boolean isFrozen = false;

            // Check if in freeze period if contest is valid and contest times are valid
            if(contest != null) {
                ContestTime ct = contest.getContestTime();
                if(ct != null) {
                    isFrozen = (Utilities.getFreezeTime(contest) <= ct.getElapsedSecs() && !contest.getContestInformation().isUnfrozen());
                }
            }
            // Decide if we should remove max_run_time from the list
            for(String prop: props) {
                // If frozen, non-admins don't see max_run_time ever
                if(!prop.equals("max_run_time") || (isAnalyst && !isFrozen)) {
                    aprops.add(prop);
                }
            }
            // regenerate array
            props = aprops.toArray(new String [0]);
        }
        return(new CLICSEndpoint("judgements", props));
    }

    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }
}
