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
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * WebService for handling teams
 * 
 * @author ICPC
 *
 */
@Path("/contests/{contestId}/teams")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class TeamService implements Feature {

    private IInternalContest model;

    private IInternalController controller;

    public TeamService(IInternalContest inContest, IInternalController inController) {
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
    public Response getTeams(@Context SecurityContext sc, @PathParam("contestId") String contestId) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == false) {
            return Response.status(Response.Status.NOT_FOUND).build();        
        }
        // get the team accounts from the model
        Account[] accounts = model.getAccounts();

        // Array of CLICSTeams for json conversion
        ArrayList<CLICSTeam> teams = new ArrayList<CLICSTeam>();
        for (int i = 0; i < accounts.length; i++) {
            Account account = accounts[i];
            
            if (account.getClientId().getClientType().equals(ClientType.Type.TEAM)) {
                teams.add(new CLICSTeam(model, account));
            }
        }
        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            String json = mapper.writeValueAsString(teams);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for teams " + e.getMessage()).build();
        }
    }

    /**
     * Return response to the request for information about a specific teamid in a specific contestid
     * 
     * @param sc user information
     * @param contestId the contest
     * @param teamId the team id
     * @return response
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("{teamId}/")
    public Response getTeam(@Context SecurityContext sc, @PathParam("contestId") String contestId, @PathParam("teamId") String teamId) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == true) {
            // get the team accounts from the model
            Account[] accounts = model.getAccounts();
    
            for (int i = 0; i < accounts.length; i++) {
                Account account = accounts[i];
                // TODO multi-site with overlapping teamNumbers?
                if(teamId.equals("" + account.getClientId().getClientNumber())) {
                    return Response.ok(new CLICSTeam(model, account).toJSON(), MediaType.APPLICATION_JSON).build();
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
        return(new CLICSEndpoint("teams", JSONUtilities.getJsonProperties(CLICSTeam.class)));
    }

    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }

}