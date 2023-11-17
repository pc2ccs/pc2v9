// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.ArrayList;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.util.JSONTool;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * WebService to handle languages
 * 
 * @author ICPC
 *
 */
@Path("/contests/{contestId}/judgement-types")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class JudgementTypeService implements Feature {

    private IInternalContest model;

    private IInternalController controller;

    public JudgementTypeService(IInternalContest inContest, IInternalController inController) {
        super();
        this.model = inContest;
        this.controller = inController;
    }

    /**
     * This method returns a representation of the current contest groups in JSON format. The returned value is a JSON array with one judgement-type description per array element, complying with 2023-06
     * 
     * @param contestId contest for which judgment types are requested
     * @return a {@link Response} object containing the contest judgement-types in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJudgementTypes(@PathParam("contestId") String contestId) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == false) {
            return Response.status(Response.Status.NOT_FOUND).build();        
        }
        
        ArrayList<CLICSJudgmentType> jlist = new ArrayList<CLICSJudgmentType>();
        
        for(Judgement judgment: model.getJudgements()) {
            if (judgment.isActive()) {
                jlist.add(new CLICSJudgmentType(model, judgment));
            }
        }
        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            String json = mapper.writeValueAsString(jlist);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for judgment-types " + e.getMessage()).build();
        }
    }

    /**
     * Return a response describing the judgement-type information for the specified contest and judgement-type
     * 
     * @param contestId The contest
     * @param judgmentType Acronym, such as RTE, TLE, AC
     * @return
     */
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("{judgmentId}/")
    public Response getJudgementType(@PathParam("contestId") String contestId, @PathParam("judgmentId") String judgmentType) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == true) {
        
            for(Judgement judgment: model.getJudgements()) {
                if (judgment.isActive() && JSONTool.getJudgementType(judgment).equals(judgmentType)) {
                    try {
                        ObjectMapper mapper = JSONUtilities.getObjectMapper();
                        String json = mapper.writeValueAsString(new CLICSJudgmentType(model, judgment));
                        return Response.ok(json, MediaType.APPLICATION_JSON).build();
                    } catch (Exception e) {
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for judgment-type " + judgmentType + " " + e.getMessage()).build();
                    }
                }
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
    
    /**
     * Retrieve access information about this endpoint for the supplied user's security context
     * 
     * @param sc User's security information
     * @return CLICSEndpoint object if the user can access this endpoint's properties, null otherwise
     */
    public static CLICSEndpoint getEndpointProperties(SecurityContext sc) {
        return(new CLICSEndpoint("judgement-types", JSONUtilities.getJsonProperties(CLICSJudgmentType.class)));
    }

    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }
}
