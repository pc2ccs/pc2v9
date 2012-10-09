package edu.csus.ecs.pc2.api.apireports;

import edu.csus.ecs.pc2.api.APIAbstractTest;
import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.api.IProblemDetails;
import edu.csus.ecs.pc2.api.IStanding;
import edu.csus.ecs.pc2.api.ITeam;

/**
 * Print standings for current user.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class PrintStandingForUser extends APIAbstractTest {

    @Override
    public void printTest() {

        ITeam team = lookupTeam(getContest().getMyClient());
        if (team == null) {

            println("No team found for user " + getContest().getMyClient().getLoginName());

        } else {

            IStanding standing = getContest().getStanding(team);
            IClient client = standing.getClient();
            println("Rank " + standing.getRank() + " solved= " + standing.getNumProblemsSolved() + " pts= " + standing.getPenaltyPoints() + " " + "s" + client.getSiteNumber() + "t"
                    + client.getAccountNumber() + " " + client.getDisplayName());

            IProblemDetails[] details = standing.getProblemDetails();
            println("There are " + details.length + " scoreboard details ");
            for (IProblemDetails det : details) {
                boolean solved = det.getSolutionTime() != 0;
                println("  solved=" + solved + " solutionTime=" + det.getSolutionTime() + //
                        " points=" + det.getPenaltyPoints() + " attempts=" + det.getAttempts());
            }
        }

        println();
    }

    private ITeam lookupTeam(IClient client) {
        ITeam[] teams = getContest().getTeams();

        for (ITeam team : teams) {
            if (sameAs(team, client)) {
                return team;
            }
        }
        return null;
    }

    private boolean sameAs(IClient client1, IClient client2) {
        return client1.equals(client2);
    }

    @Override
    public String getTitle() {
        return "getStandings(user)";
    }
}
