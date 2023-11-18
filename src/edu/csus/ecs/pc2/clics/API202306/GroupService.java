// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

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
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.util.JSONTool;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * WebService to handle languages endpoint
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
     * The response is a JSON array with one language description per array element, complying with 2023-06
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

        Set<String> exceptProps = new HashSet<String>();
        StringJoiner grps = new StringJoiner(",");
       
        ObjectMapper mapper = JSONUtilities.getObjectMapper();
        CLICSGroup cgroup;
        
        for(Group group: model.getGroups()) {
            if (group.isDisplayOnScoreboard()) {
                exceptProps.clear();
                cgroup = new CLICSGroup(group, exceptProps);

                try {                       
                    // for this group, create filter to omit unused/bad properties (location, for example) 
                    SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(exceptProps);
                    FilterProvider fp = new SimpleFilterProvider().addFilter("locFilter", filter).setFailOnUnknownId(false);
                    // generate json with only properties we want and add to CSV list.
                    grps.add(mapper.writer(fp).writeValueAsString(cgroup));
                } catch (Exception e) {
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for group " + group.getDisplayName() + " " + e.getMessage()).build();
                }
            }
        }
        return Response.ok("[" + grps.toString() + "]", MediaType.APPLICATION_JSON).build();
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
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("{groupId}/")
    public Response getGroup(@PathParam("contestId") String contestId, @PathParam("groupId") String groupId) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == true) {
            for(Group group: model.getGroups()) {
                if (group.isDisplayOnScoreboard() && JSONTool.getGroupId(group).equals(groupId)) {
                    Set<String> exceptProps = new HashSet<String>();
                    CLICSGroup cgroup = new CLICSGroup(group, exceptProps);
                    try {                       
                        ObjectMapper mapper = JSONUtilities.getObjectMapper();
                        // create filter to omit unused/bad properties (location, for example)
                        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(exceptProps);
                        FilterProvider fp = new SimpleFilterProvider().addFilter("locFilter", filter);
                        String json = mapper.writer(fp).writeValueAsString(cgroup);
                        return Response.ok(json, MediaType.APPLICATION_JSON).build();
                    } catch (Exception e) {
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for group " + groupId + " " + e.getMessage()).build();
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
