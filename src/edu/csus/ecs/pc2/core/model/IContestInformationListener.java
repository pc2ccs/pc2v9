package edu.csus.ecs.pc2.core.model;

/**
 * Listener for all ContestInformation Events.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IContestInformationListener {

    /**
     * New ContestInformation.
     * @param event
     */
    void contestInformationAdded(ContestInformationEvent event);

    /**
     * ContestInformation information has changed.
     * @param event
     */
    void contestInformationChanged(ContestInformationEvent event);

    /**
     * ContestInformation has been removed.
     * @param event
     */
    void contestInformationRemoved(ContestInformationEvent event);

    /**
     * Refresh all contest information.
     * @param contestInformationEvent
     */
    void contestInformationRefreshAll(ContestInformationEvent contestInformationEvent);
    
    /**
     * Update FinalizedData.
     * @param contestInformationEvent
     */
    void finalizeDataChanged (ContestInformationEvent contestInformationEvent);
}
