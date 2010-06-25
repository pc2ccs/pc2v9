package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.model.Run;

/**
 * Run Comparator, Order the runs by site # and run #.
 *
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$

public class RunComparator implements Comparator<Run>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2539446475650710249L;

    public int compare(Run run1, Run run2) {

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
