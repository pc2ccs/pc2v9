package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.model.Site;

/**
 * Order the sites by site name.
 *
 * @author pc2@ecs.csus.edu
 *
 */

// $HeadURL$
public class SiteComparator implements Comparator<Site>, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1702348832322017798L;

    public static final String SVN_ID = "$Id$";

    public int compare(Site site1, Site site2) {
        return site1.toString().compareTo(site2.toString());
    }

}
