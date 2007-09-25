package edu.csus.ecs.pc2.core.model;


/**
 * Listener for all Balloon Settings Events.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IBalloonSettingsListener {

    /**
     * New BalloonSettings.
     * @param event
     */
    void balloonSettingsAdded(BalloonSettingsEvent event);

    /**
     * BalloonSettings information has changed.
     * @param event
     */
    void balloonSettingsChanged(BalloonSettingsEvent event);

    /**
     * Run has been removed.
     * @param event
     */
    void balloonSettingsRemoved(BalloonSettingsEvent event);
}
