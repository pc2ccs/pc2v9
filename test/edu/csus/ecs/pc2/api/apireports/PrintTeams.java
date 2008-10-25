package edu.csus.ecs.pc2.api.apireports;

import edu.csus.ecs.pc2.api.APIAbstractTest;
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
            println(team.getLoginName() + " title: " + team.getLoginName() + " group: " + team.getGroup().getName());
        }
        println("");
        println();
    }

    @Override
    public String getTitle() {
        return "getTeams";
    }
}
