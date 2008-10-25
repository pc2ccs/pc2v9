package edu.csus.ecs.pc2.api.apireports;

import edu.csus.ecs.pc2.api.APIAbstractTest;
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
            println("Rank " + standing.getRank() + " solved= " + standing.getNumProblemsSolved() + " pts= " + standing.getPenaltyPoints() + " " + standing.getClient().getLoginName());
        }

        println();
    }

    @Override
    public String getTitle() {
        return "getStandings";
    }
}
