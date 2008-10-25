package edu.csus.ecs.pc2.api.apireports;

import edu.csus.ecs.pc2.api.APIAbstractTest;
import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.api.IStanding;

/**
 * Standings.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class PrintStandings extends APIAbstractTest {

    @Override
    public void printTest() {

        println("Standings - " + getContest().getStandings().length + " teams to rank");
        for (IStanding standing : getContest().getStandings()) {
            IClient client = standing.getClient();
            println("Rank " + standing.getRank() + " solved= " + standing.getNumProblemsSolved() + " pts= " + standing.getPenaltyPoints() + " " + "s" + client.getSiteNumber() + "t"
                    + client.getAccountNumber() + " " + client.getDisplayName());
        }

        println();
    }

    @Override
    public String getTitle() {
        return "getStandings";
    }
}
