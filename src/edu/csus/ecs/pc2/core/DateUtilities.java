package edu.csus.ecs.pc2.core;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public final class DateUtilities implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 2761218400178499655L;

    private DateUtilities() {
        super();
    }

    public static boolean dateSame(Date d1, Date d2) {
        if (d1 == null && d2 == null) {
            return true;
        }
        
        if (d1 == null && d2 != null){
            return false;
        }
        
        return d1.equals(d2);
    }

}
