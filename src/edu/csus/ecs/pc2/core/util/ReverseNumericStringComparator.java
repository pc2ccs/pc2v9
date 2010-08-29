package edu.csus.ecs.pc2.core.util;

import com.ibm.webrunner.j2mclb.util.Comparator;

/**
 * Reverse (Descending) numeric comparator.
 * 
 * @see NumericStringComparator
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ReverseNumericStringComparator implements Comparator, java.io.Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -2671499269539869587L;

    public ReverseNumericStringComparator() {
        super();
    }

    /**
     * Returns -1 if a < b; 0 if a == b; 1 if a > b.
     */
    public int compare(Object arg1, Object arg2) {
        if (arg1 == null && arg2 == null) {
            return 0;
        } else {
            if (arg1 == null) {
                return -1;
            } else {
                if (arg2 == null) {
                    return 1;
                }
            }
        }
        long l1 = Long.parseLong((String) arg1);
        long l2 = Long.parseLong((String) arg2);
        if (l1 == l2) {
            return 0;
        } else {
            if (l1 > l2) {
                return -1;
            } else {
                return 1;
            }
        }
    }

}
