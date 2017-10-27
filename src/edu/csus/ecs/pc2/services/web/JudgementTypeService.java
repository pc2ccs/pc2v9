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

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.util.JSONTool;

/**
 * WebService to handle languages
 * 
 * @author ICPC
 *
 */
@Path("/contest/judgement-types")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class JudgementTypeService implements Feature {

    private IInternalContest model;

    private IInternalController controller;

    private JSONTool jsonTool;

    public JudgementTypeService(IInternalContest inContest, IInternalController inController) {
        super();
        this.model = inContest;
        this.controller = inController;
        jsonTool = new JSONTool(model, controller);
    }

    /**
     * This method returns a representation of the current contest groups in JSON format. The returned value is a JSON array with one language description per array element, matching the description
     * at {@link https://clics.ecs.baylor.edu/index.php/Draft_CCS_REST_interface#GET_baseurl.2Flanguages}.
     * 
     * @return a {@link Response} object containing the contest languages in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJudgementTypes() {

        // get the groups from the contest
        Judgement[] judgements = model.getJudgements();

        // get an object to map the groups descriptions into JSON form
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode childNode = mapper.createArrayNode();
        for (int i = 0; i < judgements.length; i++) {
            Judgement judgement = judgements[i];
            if (judgement.isActive()) {
                childNode.add(jsonTool.convertToJSON(judgement));
            }
        }

        // output the response to the requester (note that this actually returns it to Jersey,
        // which forwards it to the caller as the HTTP response).
        return Response.ok(childNode.toString(), MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("{judgementId}/")
    public Response getJudgementType(@PathParam("judgementId") String judgementType) {
        // get the judgements from the contest
        Judgement[] judgements = model.getJudgements();

        // get an object to map the groups descriptions into JSON form
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode childNode = mapper.createArrayNode();
        for (int i = 0; i < judgements.length; i++) {
            Judgement judgement = judgements[i];
            if (jsonTool.getJudgementType(judgement).equals(judgementType) && judgement.isActive()) {
                childNode.add(jsonTool.convertToJSON(judgement));
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
