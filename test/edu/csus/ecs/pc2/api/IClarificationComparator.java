package edu.csus.ecs.pc2.api;

import java.io.Serializable;
import java.util.Comparator;

/**
 * IClarification order by site and run.
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class IClarificationComparator implements Comparator<IClarification>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -6349228800687290849L;

    public int compare(IClarification clarification1, IClarification clarification2) {

        // sort by site id then number

        int site1 = clarification1.getSiteNumber();
        int site2 = clarification2.getSiteNumber();

        if (site1 == site2) {
            return clarification1.getNumber() - clarification2.getNumber();
        } else {
            return site1 - site2;
        }

    }

}
