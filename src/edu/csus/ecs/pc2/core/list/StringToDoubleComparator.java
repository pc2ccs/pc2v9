// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;

/**
 * Compare the two strings as doubles
 * <P>
 * Simply convert to doubles and compare
 *
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
// $Id$

public class StringToDoubleComparator implements Comparator<String>, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public int compare(String NumOne, String NumTwo) {
        int iResult = 0;
        try {
            double dResult = Double.parseDouble(NumOne) - Double.parseDouble(NumTwo);
            if(dResult < 0) {
                iResult = -1;
            } else if(dResult > 0) {
                iResult = 1;
            }
        } catch(Exception exception) {
            StaticLog.getLog().log(Log.DEBUG, "Exception in compare() - StringToDoubleComparator", exception);
        }
       return(iResult);
    }
}
