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
     * @param event the ContestInformationEvent which triggered this contestInformationAdded action
     */
    void contestInformationAdded(ContestInformationEvent event);

    /**
     * ContestInformation information has changed.
     * @param event the ContestInformationEvent which triggered this contestInformationChanged action
     */
    void contestInformationChanged(ContestInformationEvent event);

    /**
     * ContestInformation has been removed.
     * @param event the ContestInformationEvent which triggered this contestInformationRemoved action
     */
    void contestInformationRemoved(ContestInformationEvent event);

    /**
     * Refresh all contest information.
     * @param event the ContestInformationEvent which triggered this contestInformationRefreshAll action
     */
    void contestInformationRefreshAll(ContestInformationEvent contestInformationEvent);
    
    /**
     * Update FinalizedData.
     * @param event the ContestInformationEvent which triggered this finalizeDataChanged action
     */
    void finalizeDataChanged (ContestInformationEvent contestInformationEvent);
}
