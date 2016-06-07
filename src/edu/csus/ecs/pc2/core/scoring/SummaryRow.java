package edu.csus.ecs.pc2.core.scoring;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A single team row of problem info.
 *
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class SummaryRow implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1270412961987362937L;

    /**
     * storage of problemsummaryinfo for a given problem.
     */
    @JsonProperty
    private Hashtable<Integer, ProblemSummaryInfo> listOfSummaryInfo = new Hashtable<Integer, ProblemSummaryInfo>();

    /**
     * Retrieves a ProblemSummaryInfo for a given problem.
     *
     * @param problemNumber
     *            which problem
     * @return the ProblemSummaryInfo
     */
    @JsonProperty
    public final ProblemSummaryInfo get(int problemNumber) {
        return (ProblemSummaryInfo) listOfSummaryInfo.get(new Integer(problemNumber));
    }

    /**
     * Places a new problemSummaryInfo for a problem.
     *
     * @param problemNumber
     *            which problem
     * @param problemSummaryInfo
     *            info for given problem
     */
    public final void put(int problemNumber, ProblemSummaryInfo problemSummaryInfo) {
        listOfSummaryInfo.put(new Integer(problemNumber), problemSummaryInfo);
    }

    /**
     * List of sorted keys to fetch ProblemSummaryInfo.
     * 
     * @return
     */
    public Integer[] getSortedKeys() {
        Set<Integer> keySet = listOfSummaryInfo.keySet();
        Integer[] keys = (Integer[]) keySet.toArray(new Integer[keySet.size()]);
        Arrays.sort(keys);
        return keys;
    }
}
