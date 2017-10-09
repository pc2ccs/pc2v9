package edu.csus.ecs.pc2.services.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Team Member JSON.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class TeamMemberJSON extends JSONUtilities {
    
    private ObjectMapper mapper = new ObjectMapper();
    private ObjectNode element = mapper.createObjectNode();
    private ArrayNode childNode = mapper.createArrayNode();

    public String createJSON(IInternalContest contest, Account account, String teamMemberName) {
        
        element = mapper.createObjectNode();
        childNode = mapper.createArrayNode();

//      //      Id   ID  yes     no  provided by CDS     identifier of the team-member.
//      //      team_id  ID  yes     no  provided by CDS     team of this team member 
//      //      icpc_id     string  no  yes     provided by CDS     external identifier from ICPC CMS

        
        element.put("id", new Integer(account.getClientId().getClientNumber()).toString());
        
        if (notEmpty(account.getExternalId())) {
            element.put("team_id", new Integer(account.getClientId().getClientNumber()).toString());
        }
        
        if (notEmpty(account.getExternalId())) {
            element.put("icpc_id", account.getExternalId());
        }
        
        element.put("name", teamMemberName);
        
        childNode.add(element);

        return stripOuterJSON(childNode.toString());
        
        // SOMEDATE add code when TeamMember class is created/used
    }
}
