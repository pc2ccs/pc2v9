package edu.csus.ecs.pc2.api.implementation;

import edu.csus.ecs.pc2.api.ISite;
import edu.csus.ecs.pc2.core.model.Site;

/**
 * API Site Implementation.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class SiteImplementation implements ISite {

    private String name;

    private int number;

    public SiteImplementation(Site site) {
        name = site.getDisplayName();
        number = site.getSiteNumber();
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

}
