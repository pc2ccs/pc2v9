package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;

/**
 * Insure access to {@link ElementId}.
 *
 * This allows classes like {@link edu.csus.ecs.pc2.core.list.ElementList} to maintain lists. <br>
 *
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IElementObject extends Serializable {

    /**
     * Get the {@link ElementId}.
     *
     * @return {@link ElementId}.
     */
    ElementId getElementId();

    int versionNumber();

    int getSiteNumber();

    void setSiteNumber(int siteNumber);

}
