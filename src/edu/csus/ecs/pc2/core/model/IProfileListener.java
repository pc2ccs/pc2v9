// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.model;


/**
 * Listener for all Proifle Events.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IProfileListener {

    /**
     * New Profile.
     * @param event
     */
    void profileAdded(ProfileEvent event);

    /**
     * Profile information has changed.
     * @param event
     */
    void profileChanged(ProfileEvent event);

    /**
     * Profile has been removed.
     * @param event
     */
    void profileRemoved(ProfileEvent event);

    /**
     * Reload/refresh Profiles.
     * @param profileEvent
     */
    void profileRefreshAll(ProfileEvent profileEvent);
}
