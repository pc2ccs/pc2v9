package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Profile Change Status.
 * 
 * Contains the status
 * 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProfileChangeStatus implements IElementObject, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -6595501993566416560L;

    /**
     * Status for site.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    public enum Status {
        /**
         * status not defined/assigned.
         */
        UNDEFINED,
        /**
         * 
         */
        NOT_CONNECTED,
        /**
         * Site is ready to sync with other sites.
         */
        READY,
        /**
         * Site is not ready.
         */
        NOTREADY,
    }

    private Site site;

    private Status status = Status.NOT_CONNECTED;

    private boolean active = true;
    
    private Profile profile = null;

    private Date date = null;

    public ProfileChangeStatus(Site site) {
        super();
        this.site = site;
    }

    /**
     * @return Returns the elementId.
     */
    public ElementId getElementId() {
        return site.getElementId();
    }

    public String toString() {
        return "Site " + site.getSiteNumber() + " " + status.toString();
    }

    /**
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active
     *            The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    public int versionNumber() {
        return getElementId().getVersionNumber();
    }

    public int getSiteNumber() {
        return getElementId().getSiteNumber();
    }

    public void setSiteNumber(int siteNumber) {
        // nothing to do.
    }
    
    public Site getSite() {
        return site;
    }
    
    public Status getStatus() {
        return status;
    }

    public Date getModifiedDate() {
        return date;
    }
    public void setStatus(Status status) {
        this.status = status;
        date = new Date();
    }
    
    public void setProfile(Profile profile) {
        this.profile = profile;
    }
    
    public Profile getProfile() {
        return profile;
    }
}
