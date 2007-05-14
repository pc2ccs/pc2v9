package edu.csus.ecs.pc2.core.model;


/**
 * Listener for all ContestInformation Events.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
// $Id$
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
     * Run has been removed.
     * @param event
     */
    void contestInformationRemoved(ContestInformationEvent event);
}
