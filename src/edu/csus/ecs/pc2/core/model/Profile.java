package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Profile information.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class Profile implements IElementObject, Serializable {

    private String name;

    private String description = "";

    private Date createDate = new Date();
    
    private ElementId elementId = null;

    /**
     * 
     */
    private static final long serialVersionUID = -8261748390647739218L;

    /**
     * Unique identifier for this instance of Site.
     */
    private boolean active = true;

    public Profile(String name) {
        elementId = new ElementId(new String(name));
        this.name = name;
    }

    public ElementId getElementId() {
        return elementId;
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

    public String getContestId() {
        return toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean matchesIdentifier(String identifier) {
        return toString().equals(identifier);
    }

    public int getSiteNumber() {
        return elementId.getSiteNumber();
    }

    public void setSiteNumber(int siteNumber) {
        setSiteNumber(elementId.getSiteNumber());
    }

    public int versionNumber() {
        return elementId.getVersionNumber();
    }
}
