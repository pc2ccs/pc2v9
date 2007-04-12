package edu.csus.ecs.pc2.core.model;


/**
 * Listener for all Site Events.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public interface ISiteListener {

    /**
     * New Site.
     * @param event
     */
    void siteAdded(SiteEvent event);

    /**
     * Site Deleted/Removed.
     * @param event
     */
    void siteRemoved(SiteEvent event);
    
    /**
     * 
     */
    void siteChanged (SiteEvent event);

    /**
     * Site logged on.
     * @param event
     */
    void siteLoggedOn(SiteEvent event);

    /**
     * Site logged off.
     * @param event
     */
    void siteLoggedOff(SiteEvent event);
}
