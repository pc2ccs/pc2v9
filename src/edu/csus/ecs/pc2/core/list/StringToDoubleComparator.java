// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Compare the two strings as numbers
 * <P>
 * Simply convert to integers and compare
 *
 * @version $Id$
 * @author John Buck
 */

// $HeadURL$
// $Id$

public class StringToDoubleComparator implements Comparator<String>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(String NumOne, String NumTwo) {
        double dResult = 0;
        try {
            dResult = Double.parseDouble(NumOne) - Double.parseDouble(NumTwo);
        } catch(Exception exception) {
            return(NumOne.compareTo(NumTwo));
        }
        if(dResult < 0)
            return(-1);
        if(dResult > 0) {
            return(1);
        }
        return(0);
    }
}
