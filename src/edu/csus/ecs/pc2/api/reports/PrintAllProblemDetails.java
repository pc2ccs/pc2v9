package edu.csus.ecs.pc2.api.reports;

import java.util.Arrays;

import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.IProblemDetails;
import edu.csus.ecs.pc2.api.IStanding;
import edu.csus.ecs.pc2.api.implementation.ProblemDetailsComparator;

/**
 * Print all standings details
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class PrintAllProblemDetails extends APIAbstractTest {

    @Override
    public void printTest() {
        
        IProblem[] problems = getContest().getProblems();

        println("Standings - " + getContest().getStandings().length + " teams to rank");
        for (IStanding standing : getContest().getStandings()) {
            IClient client = standing.getClient();
            println("Rank " + standing.getRank() + " solved= " + standing.getNumProblemsSolved() + //
                    " pts= " + standing.getPenaltyPoints() + " " + "s" + client.getSiteNumber() + "t" + client.getAccountNumber() + //
                    " " + client.getDisplayName());

            IProblemDetails[] details = standing.getProblemDetails();

            Arrays.sort(details, new ProblemDetailsComparator());

            // println("There are "+details.length+" scoreboard details ");
            if (details.length == 0){
                println("    No details found for team "+client.getLoginName());
            }
            int detailCounter = 0;

            for (IProblemDetails det : details) {
                detailCounter ++;
                boolean solved = det.getSolutionTime() != 0;
                println("  " + detailCounter + " " + problems[det.getProblemId()-1].getName() + // 
                        " solved=" + solved + " solutionTime=" + det.getSolutionTime() + //
                        " points=" + det.getPenaltyPoints() + " attempts=" + det.getAttempts());
            }
        }
        println();

    }

    @Override
    public String getTitle() {
        return "getProblemDetails";
    }
}
