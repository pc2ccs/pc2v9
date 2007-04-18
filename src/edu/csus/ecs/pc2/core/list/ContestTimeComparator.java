package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.model.ContestTime;

/**
 * ContestTime Comparator, Order the contestTimes by site #.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class ContestTimeComparator implements Comparator<ContestTime>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 7881103239654378082L;

    public static final String SVN_ID = "$Id$";

    /**
     * Sort by site number
     * 
     * @param contestTime1
     * @param contestTime2
     * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
     */
    public int compare(ContestTime contestTime1, ContestTime contestTime2) {

        int site1 = contestTime1.getSiteNumber();
        int site2 = contestTime2.getSiteNumber();
        return site1 - site2;

    }

}
