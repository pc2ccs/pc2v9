package edu.csus.ecs.pc2.api.apireports;

import edu.csus.ecs.pc2.api.APIAbstractTest;
import edu.csus.ecs.pc2.api.IGroup;

/**
 * Groups.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class PrintGroups extends APIAbstractTest {

    @Override
    public void printTest() {
        IGroup[] groups = getContest().getGroups();
        println("There are " + groups.length + " groups");
        for (IGroup group : getContest().getGroups()) {
            println("Group = " + group.getName());
        }
    }

    @Override
    public String getTitle() {
        return "getGroups";
    }
}
