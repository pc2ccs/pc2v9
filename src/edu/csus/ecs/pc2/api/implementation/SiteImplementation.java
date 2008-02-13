package edu.csus.ecs.pc2.api.implementation;

import edu.csus.ecs.pc2.api.ISite;
import edu.csus.ecs.pc2.core.model.ElementId;
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

    private ElementId elementId;

    public SiteImplementation(Site site) {
        name = site.getDisplayName();
        number = site.getSiteNumber();
        elementId = site.getElementId();
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof SiteImplementation) {
            SiteImplementation siteImplementation = (SiteImplementation) obj;
            return (siteImplementation.elementId.equals(elementId));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return elementId.toString().hashCode();
    }

}
