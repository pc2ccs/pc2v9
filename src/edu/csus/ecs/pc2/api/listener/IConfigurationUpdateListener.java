package edu.csus.ecs.pc2.api.listener;

/**
 * Set of methods that any Configuration Update Listener must implement.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
// $HeadURL$
public interface IConfigurationUpdateListener {

    /**
     * Contest element added.
     * 
     * Added an Account, Language, Problem, etc.
     * 
     * @param contestEvent
     */
    void configurationItemAdded(ContestEvent contestEvent);

    /**
     * Contest element updated.
     * 
     * Added an Account, Language, Problem, etc.
     * 
     * @param contestEvent
     */
    void configurationItemUpdated(ContestEvent contestEvent);

    /**
     * Removed element.
     * 
     * @param contestEvent
     */
    void configurationItemRemoved(ContestEvent contestEvent);
}
