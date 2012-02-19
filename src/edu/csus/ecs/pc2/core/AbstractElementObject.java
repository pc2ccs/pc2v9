package edu.csus.ecs.pc2.core;

import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IElementObject;

/**
 * A base class for {@link ElementId}.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class AbstractElementObject implements IElementObject {

    /**
     * 
     */
    private static final long serialVersionUID = -2906465010276063833L;

    private ElementId elementId;

    public AbstractElementObject(String displayName) {
        elementId = new ElementId(displayName);
    }

    public ElementId getElementId() {
        return elementId;
    }

    public int versionNumber() {
        return elementId.getVersionNumber();
    }

    public int getSiteNumber() {
        return elementId.getSiteNumber();
    }

    public void setSiteNumber(int siteNumber) {

    }

}
