package edu.csus.ecs.pc2.services.core;

import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Team JSON
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class TeamJSON extends JSONUtilities  {

    public String createJSON(IInternalContest contest, Account account) {

        StringBuilder stringBuilder = new StringBuilder();

        ClientId clientId = account.getClientId();

        //    id 
        //    icpc_id 
        //    name 
        //    organization_id 

        appendPair(stringBuilder, "id", Integer.toString(clientId.getClientNumber()));
        stringBuilder.append(", ");

        appendPair(stringBuilder, "icpc_id", account.getExternalId());
        stringBuilder.append(", ");

        appendPair(stringBuilder, "name", account.getDisplayName());
        stringBuilder.append(", ");

        appendPairNullValue(stringBuilder, "organization_id"); // TODO CLICS DATA ADD  technical deficit - add organizational id into account/model

        //    group_id 

        ElementId elementId = account.getGroupId();
        if (elementId != null) {
            Group group = contest.getGroup(elementId);
            if (group != null) {
                stringBuilder.append(", ");
                appendPair(stringBuilder, "group_id", Integer.toString(group.getGroupId()));
            }
        }

        //    location   //     JSON object as specified below. 
        //    location.x 
        //    location.y 
        //    location.rotation 

        return stringBuilder.toString();
    }
}
