package edu.csus.ecs.pc2.api;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Run Comparator, Order the runs by site # and run #.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class IRunComparator implements Comparator<IRun>, Serializable {
    
    // TODO Move from test.* into src.* API directory

    /**
     * 
     */
    private static final long serialVersionUID = -6670994983123949689L;

    public int compare(IRun run1, IRun run2) {

        // sort by site id then number

        int site1 = run1.getSiteNumber();
        int site2 = run2.getSiteNumber();

        if (site1 == site2) {
            return run1.getNumber() - run2.getNumber();
        } else {
            return site1 - site2;
        }

    }

}
