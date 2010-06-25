package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.model.Site;

/**
 * Order the sites by site name.
 *
 * @version $Id$
 * @author pc2@ecs.csus.edu
 *
 */

// $HeadURL$
public class SiteComparatorBySiteNumber implements Comparator<Site>,
        Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 8289050651523973553L;

    public static final String SVN_ID = "$Id$";

    public int compare(Site site1, Site site2) {
        return site1.getSiteNumber() - site2.getSiteNumber();
    }

}
