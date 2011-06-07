package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.model.BalloonDeliveryInfo;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;

/**
 * Sort by client type, client number and problem.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class BalloonDeliveryComparator implements Comparator<BalloonDeliveryInfo>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 695175116577330925L;

    private IInternalContest contest = null;

    public BalloonDeliveryComparator(IInternalContest contest) {
        this.contest = contest;
    }

    public int compare(BalloonDeliveryInfo ballD1, BalloonDeliveryInfo ballD2) {

        ClientId clientId1 = ballD1.getClientId();
        ClientId clientId2 = ballD2.getClientId();

        int site1 = clientId1.getSiteNumber();
        int site2 = clientId2.getSiteNumber();

        if (site1 == site2) {

            if (clientId1.getClientType().equals(clientId2.getClientType())) {

                if (clientId1.getClientNumber() == (clientId2.getClientNumber())) {
                    // same type, same client number/id
                    // sort by problem #

                    ElementId problem1Id = ballD1.getProblemId();
                    ElementId problem2Id = ballD2.getProblemId();

                    return getProblemIndex(problem1Id) - getProblemIndex(problem2Id);
                }

                return clientId1.getClientNumber() - clientId2.getClientNumber();

            } else {

                return clientId1.getClientType().compareTo(clientId2.getClientType());
            }

        } else {
            return site1 - site2;
        }

    }

    /**
     * Get index for problem in list of problems.
     */

    public int getProblemIndex(ElementId problemId) {

        Problem[] problems = contest.getProblems();

        for (int i = 0; i < problems.length; i++) {
            if (problemId.equals(problems[i].getElementId())) {
                return i;
            }
        }
        return problems.length + 1;
    }
}
