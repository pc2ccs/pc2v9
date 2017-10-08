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
        
        // SOMEDATE add code when TeamMember class is creaetd and populated
        
//        StringBuilder stringBuilder = new StringBuilder();
//
//        //      Id   ID  yes     no  provided by CDS     identifier of the team-member.
//        //      team_id  ID  yes     no  provided by CDS     team of this team member 
//        //      icpc_id     string  no  yes     provided by CDS     external identifier from ICPC CMS
//
//        appendPairNullValue(stringBuilder, "id"); // TODO CLICS DATA ADD  id needs to be added to new Account Member class and model
//        stringBuilder.append(", ");
//
//        appendPair(stringBuilder, "team_id", Integer.toString(account.getClientId().getClientNumber()));
//        stringBuilder.append(", ");
//
//        appendPairNullValue(stringBuilder, "icpc_id"); // TODO CLICS DATA ADD  icpc_id needs to be added to new Account Member class
//        stringBuilder.append(", ");
//
//        //        first_name  string  yes     no  provided by CDS     first name of team member
//        //        last_name   string  yes     no  provided by CDS     last name of team member
//
//        appendPairNullValue(stringBuilder, "first_name"); // TODO CLICS DATA ADD  first_name
//        stringBuilder.append(", ");
//
//        appendPairNullValue(stringBuilder, "last_name"); // TODO CLICS DATA ADD  last_name
//        stringBuilder.append(", ");
//
//        //      sex     string  no  no  provided by CDS     one of male, female or other
//        //      role    string  yes     no  provided by CDS     one of contestant or coach.
//
//        appendPairNullValue(stringBuilder, "sex");
//        stringBuilder.append(", ");
//
//        appendPairNullValue(stringBuilder, "role");
//
//        return stringBuilder.toString();

    }
}