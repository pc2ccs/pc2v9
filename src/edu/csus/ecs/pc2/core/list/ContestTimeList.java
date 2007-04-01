package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Hashtable;

import edu.csus.ecs.pc2.core.model.ContestTime;

/**
 * Maintain a list of ContestTime.
 *
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class ContestTimeList implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1573135566498698327L;

    public static final String SVN_ID = "$Id$";

    private Hashtable<Long, ContestTime> hash = new Hashtable<Long, ContestTime>(22);

    /**
     * Add site number for contestTime.
     *
     * @param siteNumber
     * @param contestTime
     */
    public void add(int siteNumber, ContestTime contestTime) {
        hash.put(new Long(siteNumber), contestTime);
    }

    /**
     * Get contest time for site.
     *
     * @param siteNumber
     * @return null or ContestTime.
     */
    public ContestTime get(int siteNumber) {
        return hash.get(new Long(siteNumber));
    }

    public ContestTime[] getList() {
        ContestTime[] theList = new ContestTime[hash.size()];

        if (theList.length == 0) {
            return theList;
        }

        theList = (ContestTime[]) hash.values().toArray(
                new ContestTime[hash.size()]);
        return theList;
    }

}
