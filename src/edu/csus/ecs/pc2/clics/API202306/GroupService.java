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
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.util.JSONTool;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * WebService to handle groups endpoint
 * @author John Buck
 *
 */
@Path("/contests/{contestId}/groups")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class GroupService implements Feature {

    private IInternalContest model;
    @SuppressWarnings("unused")
    private IInternalController controller;

    public GroupService(IInternalContest inContest, IInternalController inController) {
        super();
        this.model = inContest;
        this.controller = inController;
    }

    /**
     * This method returns a representation of the current contest groups in JSON format. 
     * The response is a JSON array with one group description per array element, complying with 2023-06
     * 
     * @param contestId The contest id
     * @return a {@link Response} object containing the groups in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroups(@PathParam("contestId") String contestId) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == false) {
            return Response.status(Response.Status.NOT_FOUND).build();        
        }

        ArrayList<CLICSGroup> glist = new ArrayList<CLICSGroup>();
               
        for(Group group: model.getGroups()) {
            if (group.isDisplayOnScoreboard()) {
                glist.add(new CLICSGroup(group));
            }
        }
        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            String json = mapper.writeValueAsString(glist);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for groups " + e.getMessage()).build();
        }
    }
    

    /**
     * Returns a representation of the specified groupid in the specified contestid. 
     * The response is a JSON object describing the group, complying with 2023-06
     * 
     * @param contestId The contest
     * @param groupId The desired group in the contest
     * @return a {@link Response} object containing the groups in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{groupId}/")
    public Response getGroup(@PathParam("contestId") String contestId, @PathParam("groupId") String groupId) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == true) {
            for(Group group: model.getGroups()) {
                if (group.isDisplayOnScoreboard() && JSONTool.getGroupId(group).equals(groupId)) {
                    return Response.ok(new CLICSGroup(group).toJSON(), MediaType.APPLICATION_JSON).build();
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
        return(new CLICSEndpoint("groups", JSONUtilities.getJsonProperties(CLICSGroup.class)));
    }

    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }
}
