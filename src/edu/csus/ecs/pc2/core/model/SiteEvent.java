package edu.csus.ecs.pc2.core.model;

import edu.csus.ecs.pc2.core.model.ProfileChangeStatus.Status;


/**
 * A site event.
 * 
 * @see edu.csus.ecs.pc2.core.model.ISiteListener
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
// $HeadURL$

public class SiteEvent {

    /**
     * Run Event States(s).
     * 
     * @author pc2@ecs.csus.edu
     */
    public enum Action {

        /**
         * A site logoff.
         */
        LOGOFF,
        /**
         * A new site defined.
         */
        ADDED,
        /**
         * A site logoff.
         */
        LOGIN,
        /**
         * A site removed.
         */
        DELETED,
        /**
         * A site modified.
         */
        CHANGED, 
        /**
         * 
         */
        STATUS_CHANGE,
        /**
         * Reload/Refresh sites.
         */
        REFRESH_ALL,
    }

    private Action action = Action.ADDED;

    private Site site;
    
    private Profile profile;
    
    private Status profileStatus = Status.UNDEFINED;

    public SiteEvent(Action action, Site site) {
        super();
        this.action = action;
        this.site = site;
    }
    
    public SiteEvent(Action action, Site site, Profile profile, Status profileStatus) {
        super();
        this.action = action;
        this.site = site;
        this.profile = profile;
        this.profileStatus = profileStatus;
    }

    public SiteEvent(Site site) {
        super();
        this.site = site;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }
    
    public Profile getProfile() {
        return profile;
    }

    public Status getProfileStatus() {
        return profileStatus;
    }
}
