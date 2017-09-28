package edu.csus.ecs.pc2.services.web;

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
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * WebService to handle languages
 * @author ICPC
 *
 */
@Path("/groups")
@Produces(MediaType.APPLICATION_JSON)
public class GroupService {

    private IInternalContest model;
    @SuppressWarnings("unused")
    private IInternalController controller;

    public GroupService(IInternalContest inContest, IInternalController inController) {
        super();
        this.model = inContest;
        this.controller = inController;
    }

    /**
     * This method converts a group into a JSON object added to childNode.
     * 
     * @param mapper
     * @param childNode
     * @param group
     */
    private void dumpGroup(ObjectMapper mapper, ArrayNode childNode, Group group) {
        ObjectNode element = mapper.createObjectNode();
        element.put("id", group.getElementId().toString().replaceAll(" ", "_"));
        if (group.getGroupId() != -1) {
            element.put("icpc_id", new Integer(group.getGroupId()).toString());
        }
        element.put("name", group.getDisplayName());
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
    public Response getGroups() {

        // get the groups from the contest
        Group[] groups = model.getGroups();

        // get an object to map the groups descriptions into JSON form
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode childNode = mapper.createArrayNode();
        for (int i = 0; i < groups.length; i++) {
            Group group = groups[i];
            dumpGroup(mapper, childNode, group);
        }

        // output the response to the requester (note that this actually returns it to Jersey,
        // which forwards it to the caller as the HTTP response).
        return Response.ok(childNode.toString(),MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("{groupId}/")
    public Response getGroup(@PathParam("groupId") String groupId) {
        // get the groups from the contest
        Group[] groups = model.getGroups();

        // get an object to map the groups descriptions into JSON form
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode childNode = mapper.createArrayNode();
        for (int i = 0; i < groups.length; i++) {
            Group group = groups[i];
            if (group.getElementId().toString().equals(groupId)) {
                dumpGroup(mapper, childNode, group);
            }
        }
        return Response.ok(childNode.toString(),MediaType.APPLICATION_JSON).build();
    
    }
}
