package edu.csus.ecs.pc2.services.web;

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
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.util.JSONTool;

/**
 * WebService to handle judgements
 * 
 * @author ICPC
 *
 */
@Path("/judgements")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class JudgementService implements Feature {

    private IInternalContest model;

    @SuppressWarnings("unused")
    private IInternalController controller;

    private JSONTool jsonTool;

    public JudgementService(IInternalContest inContest, IInternalController inController) {
        super();
        this.model = inContest;
        this.controller = inController;
        jsonTool = new JSONTool(inContest, inController);
    }

    /**
     * This method returns a representation of the current contest judgements in JSON format. The returned value is a JSON array with one language description per array element, matching the
     * description at {@link https://clics.ecs.baylor.edu/index.php/Draft_CCS_REST_interface#Judgements}.
     * 
     * @return a {@link Response} object containing the contest judgements in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJudgements(@Context SecurityContext sc) {
        // get the groups from the contest
        Run[] runs = model.getRuns();
        long freezeTime = getFreezeTime();

        // get an object to map the groups descriptions into JSON form
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode childNode = mapper.createArrayNode();
        for (int i = 0; i < runs.length; i++) {
            Run run = runs[i];
            if (sc.isUserInRole("public")) {
                // if run is after scoreboard freeze, and public access do not show judgement
                if (run.getElapsedMS() / 1000 > freezeTime) {
                    continue;
                }
            }
            childNode.add(jsonTool.convertJudgementToJSON(run));
        }

        // output the response to the requester (note that this actually returns it to Jersey,
        // which forwards it to the caller as the HTTP response).
        return Response.ok(childNode.toString(), MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("{judgementId}/")
    public Response getJudgement(@Context SecurityContext sc, @PathParam("judgementId") String judgementId) {
        // get the runs from the contest
        Run[] runs = model.getRuns();
        long freezeTime = getFreezeTime();

        // get an object to map the judgements descriptions into JSON form
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode childNode = mapper.createArrayNode();
        for (int i = 0; i < runs.length; i++) {
            Run run = runs[i];
            if (sc.isUserInRole("public")) {
                // if run is after scoreboard freeze, and public access do not show judgement
                if (run.getElapsedMS() / 1000 > freezeTime) {
                    continue;
                }
            }
            // judgementId's match runId's
            if (run.getElementId().toString().equals(judgementId)) {
                childNode.add(jsonTool.convertJudgementToJSON(run));
            }
        }
        return Response.ok(childNode.toString(), MediaType.APPLICATION_JSON).build();

    }

    private long getFreezeTime() {
        // this is from end of the contest
        long freezeTime = Utilities.convertStringToSeconds(model.getContestInformation().getFreezeTime());
        if (freezeTime == -1) {
            // invalid so set to end of contest
            freezeTime = model.getContestTime().getContestLengthSecs();
        } else {
            // convert to end of contest
            freezeTime = model.getContestTime().getContestLengthSecs() - freezeTime;
        }
        return freezeTime;
    }

    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }
}
