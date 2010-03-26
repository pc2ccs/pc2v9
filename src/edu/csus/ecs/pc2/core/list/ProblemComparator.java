package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;

/**
 * Sort problems in order as defined by the contest admin.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProblemComparator implements Comparator<Problem>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5557488984991030825L;
    private IInternalContest contest = null;

    public ProblemComparator(IInternalContest contest) {
        this.contest = contest;
    }

    /**
     * Get index for problem in list of problems.
     */

    public int getProblemIndex(Problem problem) {

        Problem[] problems = contest.getProblems();

        for (int i = 0; i < problems.length; i++) {
            if (problem.equals(problems[i])) {
                return i;
            }
        }
        return problems.length + 1;
    }

    public int compare(Problem problem1, Problem problem2) {
        return getProblemIndex(problem1) - getProblemIndex(problem2);
    }

}
