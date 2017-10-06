package edu.csus.ecs.pc2.services.core;

import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Group JSON
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class GroupJSON extends JSONUtilities {

    public String createJSON(IInternalContest contest, Group group) {

        StringBuilder stringBuilder = new StringBuilder();

        //    id 
        //    icpc_id 
        //    name 
        //    organization_id 

        appendPair(stringBuilder, "id", Integer.toString( group.getGroupId()));
        stringBuilder.append(", ");

        //        id  ID  yes     no  provided by CCS     identifier of the group
        //        icpc_id     string  no  yes     provided by CCS     external identifier from ICPC CMS
        //        name    string  yes     no  provided by CCS     name of the group 

        appendPair(stringBuilder, "icpc_id", Integer.toString(group.getGroupId()));
        stringBuilder.append(", ");

        appendPair(stringBuilder, "name", group.getDisplayName());

        return stringBuilder.toString();
    }
    
    
}
