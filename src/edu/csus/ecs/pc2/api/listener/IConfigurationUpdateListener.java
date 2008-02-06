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
    void configurationElementAdded(ContestEvent contestEvent);

    /**
     * Contest element updated.
     * 
     * Added an Account, Language, Problem, etc.
     * 
     * @param contestEvent
     */
    void configurationElementUpdated(ContestEvent contestEvent);

    /**
     * Removed element.
     * 
     * @param contestEvent
     */
    void configurationElementRemoved(ContestEvent contestEvent);
}
