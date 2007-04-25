package edu.csus.ecs.pc2.core.scoring;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * A single team row of problem info.
 *
 * @author pc2@ecs.csus.edu
 *
 *
 */

// $HeadURL$
public class SummaryRow implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1270412961987362937L;

    /**
     * The svn id.
     */
    public static final String SVN_ID = "$Id$";

    /**
     * storage of problemsummaryinfo for a given problem.
     */
    private Hashtable<Integer, ProblemSummaryInfo> listOfSummaryInfo = new Hashtable<Integer, ProblemSummaryInfo>();

    /**
     * Retrieves a ProblemSummaryInfo for a given problem.
     *
     * @param problemNumber
     *            which problem
     * @return the ProblemSummaryInfo
     */
    public final ProblemSummaryInfo get(int problemNumber) {
        return (ProblemSummaryInfo) listOfSummaryInfo.get(new Integer(
                problemNumber));
    }

    /**
     * Places a new problemSummaryInfo for a problem.
     *
     * @param problemNumber
     *            which problem
     * @param problemSummaryInfo
     *            info for given problem
     */
    public final void put(int problemNumber,
            ProblemSummaryInfo problemSummaryInfo) {
        listOfSummaryInfo.put(new Integer(problemNumber), problemSummaryInfo);
    }

}
