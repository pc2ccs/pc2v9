package edu.csus.ecs.pc2.core.model;

import java.util.Date;

/**
 * Profile information.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class Profile extends ElementId {

    private String name;

    private String description = "";

    private Date createDate = new Date();

    /**
     * 
     */
    private static final long serialVersionUID = -8261748390647739218L;

    /**
     * Unique identifier for this instance of Site.
     */
    private boolean active = true;

    public Profile(String name) {
        super(name);
        this.name = name;
        setSiteNumber(0);
    }

    public ElementId getElementId() {
        return this;
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
}
