package edu.csus.ecs.pc2.services.web;

import java.util.Hashtable;

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
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.util.JSONTool;

/**
 * WebService for handling teams
 * 
 * @author ICPC
 *
 */
@Path("/contest/organizations")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class OrganizationService implements Feature {

    private IInternalContest model;

    private IInternalController controller;

    private JSONTool jsonTool;

    public OrganizationService(IInternalContest inContest, IInternalController inController) {
        super();
        this.model = inContest;
        this.controller = inController;
        jsonTool = new JSONTool(model, controller);
    }

    /**
     * This method returns a representation of the current model teams in JSON format. The returned value is a JSON array with one team description per array element.
     * 
     * @return a {@link Response} object containing the model teams in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrganizations() {

        // get the team accounts from the model
        Account[] accounts = model.getAccounts();
        // keep track of which ones we have dumped
        Hashtable<String, Account> organizations = new Hashtable<String, Account>();

        // get an object to map the groups descriptions into JSON form
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode childNode = mapper.createArrayNode();
        for (int i = 0; i < accounts.length; i++) {
            Account account = accounts[i];
            if (account.getClientId().getClientType().equals(ClientType.Type.TEAM) && !account.getInstitutionCode().equals("undefined")) {
                if (!organizations.containsKey(account.getInstitutionCode())) {
                    organizations.put(account.getInstitutionCode(), account);
                    childNode.add(jsonTool.convertOrganizationsToJSON(account));
                }
            }
        }
        return Response.ok(childNode.toString(), MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("{organizationId}/")
    public Response getOrganization(@PathParam("organizationId") String organizationId) {
        // get the team accounts from the model
        Account[] accounts = model.getAccounts();

        // get an object to map the groups descriptions into JSON form
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode childNode = mapper.createArrayNode();
        for (int i = 0; i < accounts.length; i++) {
            Account account = accounts[i];
            if (account.getClientId().getClientType().equals(ClientType.Type.TEAM) && account.getInstitutionCode().equals(organizationId)) {
                childNode.add(jsonTool.convertOrganizationsToJSON(account));
                break; // only looking for 1 id, so only dump once
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
