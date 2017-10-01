package edu.csus.ecs.pc2.services.web;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClarificationAnswer;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * WebService to handle languages
 * 
 * @author ICPC
 *
 */
@Path("/clarifications")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class ClarificationService implements Feature {

    private IInternalContest model;

    @SuppressWarnings("unused")
    private IInternalController controller;

    public ClarificationService(IInternalContest inContest, IInternalController inController) {
        super();
        this.model = inContest;
        this.controller = inController;
    }

    /**
     * This method converts a group into a JSON object added to childNode.
     * 
     * @param mapper
     * @param childNode
     * @param clarification
     */
    private void dumpClarification(ObjectMapper mapper, ArrayNode childNode, Clarification clarification, ClarificationAnswer clarAnswer) {
        ObjectNode element = mapper.createObjectNode();
        String id = clarification.getElementId().toString();
        if (clarAnswer != null) {
            id = clarAnswer.getElementId().toString();
        }
        element.put("id", id);
        if (clarification.getSubmitter().getClientType().equals(ClientType.Type.TEAM) && clarAnswer == null) {
            element.put("from_team_id", new Integer(clarification.getSubmitter().getClientNumber()).toString());
        } else {
            element.set("from_team_id", null);
        }
        if (clarAnswer == null) {
            // the request goes to a judge not a team
            element.set("to_team_id", null);
            element.set("reply_to_id", null);
        } else {
            if (clarification.isSendToAll() || !clarification.getSubmitter().getClientType().equals(ClientType.Type.TEAM)) {
                element.set("to_team_id", null);
            } else {
                element.put("to_team_id", new Integer(clarification.getSubmitter().getClientNumber()).toString());
            }
            element.put("reply_to_id", clarification.getElementId().toString());
        }
        if (clarification.getProblemId().equals(model.getGeneralProblem()) || model.getCategory(clarification.getProblemId()) != null) {
            element.set("problem_id", null);
        } else {
            element.put("problem_id", clarification.getProblemId().toString());
        }
        if (clarAnswer != null) {
            ClarificationAnswer[] clarificationAnswers = clarification.getClarificationAnswers();
            int lastAnswer = clarificationAnswers.length - 1;
            element.put("text", clarificationAnswers[lastAnswer].getAnswer());
            String time = Utilities.getIso8601formatterWithMS().format(clarificationAnswers[lastAnswer].getDate());
            element.put("time", time);
            element.put("contest_time", ContestTime.formatTimeMS(clarificationAnswers[lastAnswer].getElapsedMS()));
        } else {
            element.put("text", clarification.getQuestion());
            String time = Utilities.getIso8601formatterWithMS().format(clarification.getCreateDate());
            element.put("time", time);
            element.put("contest_time", ContestTime.formatTimeMS(clarification.getElapsedMS()));
        }
        childNode.add(element);
    }

    /**
     * This method returns a representation of the current contest groups in JSON format. The returned value is a JSON array with one language description per array element, matching the description
     * at {@link https://clics.ecs.baylor.edu/index.php/Draft_CCS_REST_interface#GET_baseurl.2Flanguages}.
     * 
     * @return a {@link Response} object containing the contest languages in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClarifications() {

        // get the groups from the contest
        Clarification[] clarifications = model.getClarifications();

        // get an object to map the groups descriptions into JSON form
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode childNode = mapper.createArrayNode();
        for (int i = 0; i < clarifications.length; i++) {
            Clarification clarification = clarifications[i];
            // dump the request
            dumpClarification(mapper, childNode, clarification, null);
            if (clarification.isAnswered()) {
                // dump the answers
                ClarificationAnswer[] clarAnswers = clarification.getClarificationAnswers();
                for (int j = 0; j < clarAnswers.length; j++) {
                    dumpClarification(mapper, childNode, clarification, clarAnswers[j]);
                }
            }
        }

        // output the response to the requester (note that this actually returns it to Jersey,
        // which forwards it to the caller as the HTTP response).
        return Response.ok(childNode.toString(), MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("{clarificationId}/")
    public Response getClarification(@PathParam("clarificationId") String clarificationId) {
        // get the groups from the contest
        Clarification[] clarifications = model.getClarifications();

        // get an object to map the groups descriptions into JSON form
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode childNode = mapper.createArrayNode();
        ClarificationAnswer[] clarAnswers = null;
        for (int i = 0; i < clarifications.length; i++) {
            Clarification clarification = clarifications[i];
            if (clarification.getElementId().toString().equals(clarificationId)) {
                dumpClarification(mapper, childNode, clarification, null);
            }
            clarAnswers = clarification.getClarificationAnswers();
            if (clarAnswers != null) {
                for (int j = 0; j < clarAnswers.length; j++) {
                    ClarificationAnswer clarificationAnswer = clarAnswers[j];
                    if (clarificationAnswer.getElementId().toString().equals(clarificationId)) {
                        dumpClarification(mapper, childNode, clarification, clarificationAnswer);
                        break;
                    }                    
                }
            }
        }
        return Response.ok(childNode.toString(), MediaType.APPLICATION_JSON).build();

    }

    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }
}
