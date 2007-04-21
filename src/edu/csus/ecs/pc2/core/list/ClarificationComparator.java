package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.model.Clarification;

/**
 * Clarification Comparator, Order the clarifications by site # and clarification #.
 *
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class ClarificationComparator implements Comparator<Clarification>, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1402498954732267609L;

    public static final String SVN_ID = "$Id$";

    public int compare(Clarification clarification1, Clarification clarification2) {

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
