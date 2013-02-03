package edu.csus.ecs.pc2.core.model;

import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;

/**
 * A definition for an Event Feed Server instance.
 * 
 * For a pc2 server, this is a definition for a Event Feed Server.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EventFeedDefinition implements IElementObject {

    /**
     * 
     */
    private static final long serialVersionUID = 5529479138590675584L;

    private int port;

    private String name;

    private boolean active = false;

    private ElementId elementId = null;

    private int siteNumber;
    
    private boolean frozen = false;

    public EventFeedDefinition(String name) {
        super();
        this.name = name;
        elementId = new ElementId(name);
    }

    public int versionNumber() {
        return elementId.getVersionNumber();
    }

    public int getSiteNumber() {
        return siteNumber;
    }

    public void setSiteNumber(int siteNumber) {
        this.siteNumber = siteNumber;
    }

    /**
     * @return Returns the elementId.
     */
    public ElementId getElementId() {
        return elementId;
    }

    /**
     * @return Returns the displayName.
     */
    public String getDisplayName() {
        return name;
    }

    /**
     * @param displayName
     *            The displayName to set.
     */
    public void setDisplayName(String displayName) {
        this.name = displayName;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }
    
    public boolean isFrozen() {
        return frozen;
    }

    public boolean isSameAs(EventFeedDefinition definition) {

        try {
            if (definition == null) {
                return false;
            }
            if (port != definition.getPort()) {
                return false;
            }
            if (siteNumber != definition.getSiteNumber()) {
                return false;
            }
            if (!StringUtilities.stringSame(name, definition.getDisplayName())) {
                return false;
            }
            if (frozen != definition.isFrozen()) {
                return false;
            }
            if (active != definition.isActive()) {
                return false;
            }

            return true;
            
        } catch (Exception e) {
            StaticLog.getLog().log(Log.WARNING, "Exception comparing Problem " + e.getMessage(), e);
            e.printStackTrace(System.err);
            return false;
        }
    }
}
