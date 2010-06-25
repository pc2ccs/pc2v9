package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.model.Clarification;

/**
 * Clarification Comparator, Order the clarifications by site # and clarification #.
 *
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
// $Id$ 
public class ClarificationComparator implements Comparator<Clarification>, Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = -7714495596349725729L;

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
