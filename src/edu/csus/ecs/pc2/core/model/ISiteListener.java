package edu.csus.ecs.pc2.core.model;

/**
 * Listener for all Site Events.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface ISiteListener {

    /**
     * New Site.
     * @param event the SiteEvent triggered by adding a site to the contest
     */
    void siteAdded(SiteEvent event);

    /**
     * Site Deleted/Removed.
     * @param event the SiteEvent triggered by removing a site from the contest
     */
    void siteRemoved(SiteEvent event);

    /**
     * Site changed.
     * @param event the SiteEvent triggered by changing a site in the contest
     */
    void siteChanged (SiteEvent event);

    /**
     * Site logged on.
     * @param event the SiteEvent triggered by a site logging into the contest
     */
    void siteLoggedOn(SiteEvent event);

    /**
     * Site logged off.
     * @param event the SiteEvent triggered by a site logging out of the contest
     */
    void siteLoggedOff(SiteEvent event);
    
    /**
     * Profile status for this site. 
     * 
     * @param event the SiteEvent triggered by a change in the profile of a contest site
     */
    void siteProfileStatusChanged (SiteEvent event);

    /**
     * Reload/refresh sites.
     * 
     * @param event the SiteEvent triggered by a request to refresh all contest sites
     */
    void sitesRefreshAll(SiteEvent siteEvent);
}
