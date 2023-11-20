// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.ArrayList;

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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.services.core.JSONUtilities;
import edu.csus.ecs.pc2.services.eventFeed.WebServer;

/**
 * WebService for handling accounts endpoint
 * 
 * @author John Buck
 *
 */
@Path("/contests/{contestId}/{svc: accounts|account}")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class AccountService implements Feature {

    private IInternalContest model;

    private IInternalController controller;

    public AccountService(IInternalContest inContest, IInternalController inController) {
        super();
        this.model = inContest;
        this.controller = inController;
    }

    /**
     * This method returns a representation of the current model teams in JSON format. The returned value is a JSON array with one team description per array element.
     * 
     * @param sc user information
     * @param contestId the contest for which the teams are requested
     * @return a {@link Response} object containing the model teams in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccounts(@Context SecurityContext sc, @PathParam("contestId") String contestId, @PathParam("svc") String svc) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == false) {
            return Response.status(Response.Status.NOT_FOUND).build();        
        }
        
        String myUser = sc.getUserPrincipal().getName();
        
        if(svc.equals("accounts")) {
            ArrayList<CLICSAccount> alist = new ArrayList<CLICSAccount>();
            
            for(Account account: model.getAccounts()) {   
                
                // Admin can see everyone, everyone else only sees themselves.
                if (sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) || myUser.equals(account.getClientId().getName())) {
                    alist.add(new CLICSAccount(model, sc, account));
                }
            }
            try {
                ObjectMapper mapper = JSONUtilities.getObjectMapper();
                String json = mapper.writeValueAsString(alist);
                return Response.ok(json, MediaType.APPLICATION_JSON).build();
            } catch (Exception e) {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for accounts " + e.getMessage()).build();
            }
        } else {
            // account endpoint - who am i, basically.
            for(Account account: model.getAccounts()) {
                // Looking for currently authenticated user
                if(myUser.equals("" + account.getClientId().getName())){
                    return Response.ok(new CLICSAccount(model, sc, account).toJSON(), MediaType.APPLICATION_JSON).build();
                }
            }
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    /**
     * Return response to the request for information about a specific teamid in a specific contestid
     * 
     * @param sc user information
     * @param contestId the contest
     * @param accountId the account id
     * @return response
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("{accountId}/")
    public Response getTeam(@Context SecurityContext sc, @PathParam("contestId") String contestId, @PathParam("accountId") String accountId) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == true) {
            
            for(Account account: model.getAccounts()) {
                // can only see the account if this is an admin or the actual user
                if(accountId.equals("" + account.getClientId().getName()) && (sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) || sc.getUserPrincipal().getName().equals(account.getClientId().getName()))) {
                    return Response.ok(new CLICSAccount(model, sc, account).toJSON(), MediaType.APPLICATION_JSON).build();
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
        String [] props = JSONUtilities.getJsonProperties(CLICSAccount.class);
        // Non-admin users can not see password, so don't include in the access endpoint result
        if(!sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN)) {
            ArrayList<String> aprops = new ArrayList<String>();
            for(String prop: props) {
                if(!prop.equals("password")) {
                    aprops.add(prop);
                }
            }
            props = aprops.toArray(new String [0]);
        }
        return(new CLICSEndpoint("accounts", props));
    }

    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }

}
