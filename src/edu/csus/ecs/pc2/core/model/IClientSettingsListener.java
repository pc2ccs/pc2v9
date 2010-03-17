package edu.csus.ecs.pc2.core.model;


/**
 * Listener for all ClientSettings Events.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IClientSettingsListener {

    /**
     * New ClientSettings.
     * @param event
     */
    void clientSettingsAdded(ClientSettingsEvent event);

    /**
     * ClientSettings information has changed.
     * @param event
     */
    void clientSettingsChanged(ClientSettingsEvent event);

    void clientSettingsRemoved(ClientSettingsEvent event);
}
