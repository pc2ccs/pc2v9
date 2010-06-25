package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * Run Comparator, Order the runs by Site #, Team # and then Problem Id.
 * 
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class RunComparatorByTeamProblem implements Comparator<Run>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -419557289338217178L;

    public static final String SVN_ID = "$Id$";

    public int compare(Run run1, Run run2) {
        // sort by site id then number

        int site1 = run1.getSiteNumber();
        int site2 = run2.getSiteNumber();

        if (site1 == site2) {

            ClientId team1 = run1.getSubmitter();
            ClientId team2 = run2.getSubmitter();

            if (team1.getClientNumber() == team2.getClientNumber()) {
                if (run1.getProblemId().equals(run2.getProblemId())){
                    if (run1.getElapsedMins() < run2.getElapsedMins()){
                        return -1;
                    } else {
                        return 1;
                    }
                } else {
                    return run1.getProblemId().toString().compareTo(run2.getProblemId().toString());
                }
            } else {
                return team1.getClientNumber() - team2.getClientNumber();
            }
        } else {

            return site1 - site2;
        }

    }

}
