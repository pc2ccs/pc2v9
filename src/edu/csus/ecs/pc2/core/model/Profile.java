package edu.csus.ecs.pc2.core.model;

import java.util.Date;

/**
 * Profile identification information.
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class Profile implements IElementObject {

    private String name;
    
    private Date createDate = new Date();

    /**
     * 
     */
    private static final long serialVersionUID = -8261748390647739218L;

    /**
     * Unique identifier for this instance of Site.
     */
    private ElementId elementId = null;

    private boolean active = true;

    public Profile(String name) {
        this.name = name;
        elementId = new ElementId("Profile" + name);
        setSiteNumber(0);
    }

    public ElementId getElementId() {
        // TODO Auto-generated method stub
        return null;
    }

    public int versionNumber() {
        return elementId.getVersionNumber();
    }

    public int getSiteNumber() {
        return elementId.getSiteNumber();
    }

    public void setSiteNumber(int siteNumber) {
        elementId.setSiteNumber(siteNumber);
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getCreateDate() {
        return createDate;
    }

}
