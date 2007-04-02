package edu.csus.ecs.pc2.core.model;

import edu.csus.ecs.pc2.core.list.ElementList;

/**
 * List of Sites.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class SiteList extends ElementList {

    private static final long serialVersionUID = 7601290893650237279L;

    public static final String SVN_ID = "$Id$";

    // private SiteComparator siteComparator = new SiteComparator();

    /**
     * @param site
     *            {@link Site} to be added.
     */
    public void add(Site site) {
        super.add(site);

    }

    /**
     * Return list of Languages.
     * 
     * @return list of {@link Site}.
     */
    @SuppressWarnings("unchecked")
    public Site[] getList() {
        if (size() == 0) {
            return new Site[0];
        }

        return (Site[]) values().toArray(new Site[size()]);
    }
}
