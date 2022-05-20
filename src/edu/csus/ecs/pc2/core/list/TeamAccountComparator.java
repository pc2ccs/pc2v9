// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.imports.clics.TeamAccount;

/*
 * TeamAccount Comparator, Order the accounts by client number then display name
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class TeamAccountComparator implements Comparator<TeamAccount>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6930011395916416715L;

    public int compare(TeamAccount account1, TeamAccount account2) {

        int teamId1 = Integer.parseInt(account1.getId().trim());
        int teamId2 = Integer.parseInt(account2.getId().trim());

        if (teamId1 == teamId2) {
            return StringUtilities.nullSafecompareTo(account1.getDisplay_name(), account2.getDisplay_name());
        } else {
            return teamId1 - teamId2;
        }
    }
}
