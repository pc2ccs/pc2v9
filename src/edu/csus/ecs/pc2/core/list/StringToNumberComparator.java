// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;

/**
 * Compare the two strings as numbers
 * <P>
 * Simply convert to integers and compare
 *
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
// $Id$

public class StringToNumberComparator implements Comparator<String>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5842577552526394242L;

    public int compare(String NumOne, String NumTwo) {
        int iResult = 0;
        try {
            iResult = Integer.parseInt(NumOne) - Integer.parseInt(NumTwo);
        } catch(Exception exception) {
            StaticLog.getLog().log(Log.INFO, "Exception in compare() - StringToNumberComparator", exception);
        }
       return(iResult); 
    }
}
