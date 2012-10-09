package edu.csus.ecs.pc2.api.reports;

import edu.csus.ecs.pc2.api.IGroup;
import edu.csus.ecs.pc2.api.ITeam;

/**
 * Teams.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class PrintTeams extends APIAbstractTest {

    @Override
    public void printTest() {
        println("There are " + getContest().getTeams().length + " team ");
        for (ITeam team : getContest().getTeams()) {
            IGroup group = team.getGroup();
            String name = "(no group assigned)";
            if (group != null) {
                name = group.getName();
            }
            println(team.getLoginName() + " title: " + team.getLoginName() + " group: " + name);
        }
        println("");
        println();
    }

    @Override
    public String getTitle() {
        return "getTeams";
    }
}
