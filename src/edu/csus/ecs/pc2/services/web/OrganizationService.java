package edu.csus.ecs.pc2.services.web;

import java.util.Hashtable;

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
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * WebService for handling teams
 * 
 * @author ICPC
 *
 */
@Path("/organizations")
@Produces(MediaType.APPLICATION_JSON)
public class OrganizationService {

    private IInternalContest model;
    @SuppressWarnings("unused")
    private IInternalController controller;

    public OrganizationService(IInternalContest inContest, IInternalController inController) {
        super();
        this.model = inContest;
        this.controller = inController;
    }

    /**
     * This method returns a representation of the current model teams in JSON format. 
     * The returned value is a JSON array with one team description per array element.
     * 
     * @return a {@link Response} object containing the model teams in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrganizations() {
        
        // get the team accounts from the model
        Account[] accounts = model.getAccounts();
        // keep track of which ones we have dumped
        Hashtable<String,Account> organizations = new Hashtable<String,Account>();

        // get an object to map the groups descriptions into JSON form
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode childNode = mapper.createArrayNode();
        for (int i = 0; i < accounts.length; i++) {
            Account account = accounts[i];
            if (account.getClientId().getClientType().equals(ClientType.Type.TEAM) && !account.getInstitutionCode().equals("undefined")) {
                if (!organizations.containsKey(account.getInstitutionCode())) {
                    organizations.put(account.getInstitutionCode(), account);
                    dumpAccount(mapper, childNode, account);
                }
            }
        }
        return Response.ok(childNode.toString(), MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
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
                dumpAccount(mapper, childNode, account);
                break; // only looking for 1 id, so only dump once
            }
        }
        return Response.ok(childNode.toString(), MediaType.APPLICATION_JSON).build();
    }

    private void dumpAccount(ObjectMapper mapper, ArrayNode childNode, Account account) {
        ObjectNode element = mapper.createObjectNode();
        element.put("id", account.getInstitutionCode());
        element.put("icpc_id", account.getInstitutionCode());
        element.put("name", account.getInstitutionShortName());
        if (notEmpty(account.getInstitutionName())) {
            element.put("formal_name", account.getInstitutionName());
        }
        if (notEmpty(account.getCountryCode()) && !account.getCountryCode().equals("XXX")) {
            element.put("country", account.getCountryCode());
        }
        // rest is provided by CDS, not CCS
        childNode.add(element);
    }

    /**
     * returns true if the value is not null and is not the empty string
     * @param value
     * @return
     */
    private boolean notEmpty(String value) {
        if (value != null && !value.equals("")) {
            return true;
        }
        return false;
    }

}
