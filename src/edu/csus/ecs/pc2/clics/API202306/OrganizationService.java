// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.ArrayList;
import java.util.HashSet;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.imports.LoadAccounts;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.util.JSONTool;
import edu.csus.ecs.pc2.imports.ccs.ICPCTSVLoader;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * WebService for handling teams
 * 
 * @author John Buck
 *
 */
@Path("/contests/{contestId}/organizations")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class OrganizationService implements Feature {

    private IInternalContest model;

    private IInternalController controller;

    public OrganizationService(IInternalContest inContest, IInternalController inController) {
        super();
        this.model = inContest;
        this.controller = inController;
    }

    /**
     * Returns a representation of the current model organizations in JSON format. The returned value is a JSON array with one organization description per array element.
     * This is compliant with 2023-06
     * 
     * @param contestId The contest
     * @return a {@link Response} object containing the model teams in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrganizations(@PathParam("contestId") String contestId) {

        // keep track of which ones are being used for this contest
        HashSet<String> orgSet = new HashSet<String>();

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == false) {
            return Response.status(Response.Status.NOT_FOUND).build();        
        }
        
        ArrayList<CLICSOrganization> olist = new ArrayList<CLICSOrganization>();
        
        LoadAccounts.loadInstitutions(model);
        
        // make up a list of CLICSOrganizations in use.
        for(Account account: model.getAccounts()) {
            if (account.getClientId().getClientType().equals(ClientType.Type.TEAM) && !account.getInstitutionCode().equals("undefined")) {
                String orgId = JSONTool.getOrganizationId(account);
                if(orgSet.add(orgId)) {
                    String [] orgFields = ICPCTSVLoader.getInstitutionNames(orgId);
                    if(orgFields != null) {
                        olist.add(new CLICSOrganization(orgId, account, orgFields));
                    }
                }
            }
        }
        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            String json = mapper.writeValueAsString(olist);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for organizations " + e.getMessage()).build();
        }
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("{organizationId}/")
    public Response getOrganization(@PathParam("contestId") String contestId, @PathParam("organizationId") String organizationId) {
        
        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == true) {
            LoadAccounts.loadInstitutions(model);
            
            String [] orgFields = ICPCTSVLoader.getInstitutionNames(organizationId);
            if(orgFields != null) {
                // find a team who belongs to this organization
                for(Account account: model.getAccounts()) {
                    if (account.getClientId().getClientType().equals(ClientType.Type.TEAM) && JSONTool.getOrganizationId(account).equals(organizationId)) {
                        return Response.ok(new CLICSOrganization(organizationId, account, orgFields).toJSON(), MediaType.APPLICATION_JSON).build();
                    }
                }
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }

}
