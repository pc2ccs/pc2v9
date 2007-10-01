package edu.csus.ecs.pc2.core.model;

import edu.csus.ecs.pc2.core.list.ElementList;

/**
 * Maintain list of Sites.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class SiteList extends ElementList {

    private static final long serialVersionUID = 7601290893650237279L;

    /**
     * @param site
     *            {@link Site} to be added.
     */
    public void add(Site site) {
        super.add(site);
    }

    public void update(Site site) {
        super.update(site);
    }

    /**
     * Return list of Sites.
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
