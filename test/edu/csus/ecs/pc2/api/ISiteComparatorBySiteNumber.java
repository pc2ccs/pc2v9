package edu.csus.ecs.pc2.api;

import java.io.Serializable;
import java.util.Comparator;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ISiteComparatorBySiteNumber implements Comparator<ISite>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6786242350133573386L;

    public int compare(ISite site1, ISite site2) {
        return site1.getNumber() - site2.getNumber();
    }

}
