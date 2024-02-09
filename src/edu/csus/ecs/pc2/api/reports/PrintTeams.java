// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.api.reports;

import java.util.HashMap;

import edu.csus.ecs.pc2.api.IGroup;
import edu.csus.ecs.pc2.api.ITeam;
import edu.csus.ecs.pc2.core.model.ElementId;

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
            HashMap<ElementId, IGroup> groups = team.getGroups();
            String name = "";
            boolean first = true;
            for(ElementId groupElementId : groups.keySet()) {
                IGroup group = groups.get(groupElementId);
                if(group != null) {
                    if(first) {
                        first = false;
                    } else {
                        name = name + ",";
                    }
                    name = name + group.getName();
                }
            }
            if(name.isEmpty()) {
                name = "(no groups assigned)";
            }
            println(team.getLoginName() + " title: " + team.getLoginName() + " groups: " + name);
        }
        println("");
        println();
    }

    @Override
    public String getTitle() {
        return "getTeams";
    }
}
