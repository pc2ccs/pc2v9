package edu.csus.ecs.pc2.services.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Group JSON
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class GroupJSON extends JSONUtilities {
    
    private ObjectMapper mapper = new ObjectMapper();
    private ArrayNode childNode = mapper.createArrayNode();

    public String createJSON(IInternalContest contest, Group group) {
        
        childNode = mapper.createArrayNode();
        
        ObjectNode element = mapper.createObjectNode();
        
      //    id 
      //    icpc_id 
      //    name 
      //    organization_id 
        
        element.put("id", new Integer(group.getGroupId()).toString());
        if (group.getGroupId() != -1) {
            element.put("icpc_id", new Integer(group.getGroupId()).toString());
        }
        element.put("name", group.getDisplayName());
        
        childNode.add(element);
        
        return stripOuterJSON(childNode.toString());
    }


}
