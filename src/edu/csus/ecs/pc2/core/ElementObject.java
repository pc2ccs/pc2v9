package edu.csus.ecs.pc2.core;

import java.io.Serializable;

/**
 * Insure access to {@link ElementId}.
 *
 * This allows classes like {@link edu.csus.ecs.pc2.core.ElementList} to maintain lists. <br>
 *
 * @author pc2@ecs.csus.edu
 *
 */

// $HeadURL$
public interface ElementObject extends Serializable {

    String SVN_ID = "$Id$";

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
