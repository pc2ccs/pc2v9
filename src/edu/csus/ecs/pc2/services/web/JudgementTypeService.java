package edu.csus.ecs.pc2.services.web;

import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;

/**
 * WebService to handle languages
 * @author ICPC
 *
 */
@Path("/judgement-types")
@Produces(MediaType.APPLICATION_JSON)
public class JudgementTypeService {

    private IInternalContest model;
    @SuppressWarnings("unused")
    private IInternalController controller;

    public JudgementTypeService(IInternalContest inContest, IInternalController inController) {
        super();
        this.model = inContest;
        this.controller = inController;
    }

    /**
     * This method converts a judgementType into a JSON object added to childNode.
     * 
     * @param mapper
     * @param childNode
     * @param judgement
     */
    private void dumpJudgementType(ObjectMapper mapper, ArrayNode childNode, Judgement judgement) {
        ObjectNode element = mapper.createObjectNode();
        String name = judgement.getDisplayName();
        Boolean solved = false; 
        Boolean penalty = true;
        if (name.equalsIgnoreCase("yes") || name.equalsIgnoreCase("accepted")) {
            name = "Accepted";
            solved = true;
            penalty = false;
        } else {
            name = name.substring(5, name.length());
            Properties scoringProperties = model.getContestInformation().getScoringProperties();
            if (judgement.getAcronym().equalsIgnoreCase("ce") || name.toLowerCase().contains("compilation error")) {
                Object result = scoringProperties.getProperty(DefaultScoringAlgorithm.POINTS_PER_NO_COMPILATION_ERROR, "0");
                if (result.equals("0")) {
                    penalty = false;
                }
            }
            if (judgement.getAcronym().equalsIgnoreCase("sv") || name.toLowerCase().contains("security violation")) {
                String result = scoringProperties.getProperty(DefaultScoringAlgorithm.POINTS_PER_NO_SECURITY_VIOLATION, "0");
                if (result.equals("0")) {
                    penalty = false;
                }
            }
                
        }
        element.put("id", judgement.getElementId().toString().replaceAll(" ", "_"));
        element.put("name", name);
        element.put("penalty", penalty);
        element.put("solved", solved);
        childNode.add(element);
    }
    
    /**
     * This method returns a representation of the current contest groups in JSON format. 
     * The returned value is a JSON array with one language description per array element, matching the
     * description at {@link https://clics.ecs.baylor.edu/index.php/Draft_CCS_REST_interface#GET_baseurl.2Flanguages}.
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
            dumpJudgementType(mapper, childNode, judgement);
        }

        // output the response to the requester (note that this actually returns it to Jersey,
        // which forwards it to the caller as the HTTP response).
        return Response.ok(childNode.toString(),MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("{judgementId}/")
    public Response getJudgement(@PathParam("judgementId") String judgementId) {
        // get the groups from the contest
        Judgement[] judgements = model.getJudgements();

        // so the ids match the element id.
        judgementId = judgementId.replaceAll("_"," ");
        // get an object to map the groups descriptions into JSON form
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode childNode = mapper.createArrayNode();
        for (int i = 0; i < judgements.length; i++) {
            Judgement judgement = judgements[i];
            if (judgement.getElementId().toString().equals(judgementId)) {
                dumpJudgementType(mapper, childNode, judgement);
            }
        }
        return Response.ok(childNode.toString(),MediaType.APPLICATION_JSON).build();
    
    }
}
