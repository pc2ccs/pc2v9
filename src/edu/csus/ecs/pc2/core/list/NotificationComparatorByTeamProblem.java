package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Notification;
import edu.csus.ecs.pc2.core.model.Problem;

/**
 * Sort Notifications by site, team number, and problem number.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class NotificationComparatorByTeamProblem implements Comparator<Notification>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5227962755821873627L;

    private IInternalContest contest = null;

    public NotificationComparatorByTeamProblem(IInternalContest contest) {
        this.contest = contest;
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

    public int compare(Notification n1, Notification n2) {

        int site1 = n1.getSiteNumber();
        int site2 = n2.getSiteNumber();

        if (site1 == site2) {

            ClientId clientId1 = n1.getSubmitter();
            ClientId clientId2 = n2.getSubmitter();

            if (clientId1.getClientType().equals(clientId2.getClientType())) {
                return clientId1.getClientNumber() - clientId2.getClientNumber();
            } else {
                int p1 = getProblemIndex(n1.getProblemId());
                int p2 = getProblemIndex(n2.getProblemId());
                return p1 - p2;
            }

        } else {
            return site1 - site2;
        }
    }

}
