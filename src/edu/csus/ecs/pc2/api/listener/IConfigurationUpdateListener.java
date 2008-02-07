package edu.csus.ecs.pc2.api.listener;

/**
 * This interface defines the set of methods that any Contest Configuration Update Listener must implement.
 * 
 * <p>
 * These methods are invoked whenever any new contest configuration item (such as a problem 
 * or language) is added to the contest, or whenever an existing configuration item is modified or removed from the contest.
 * A client utilizing the PC<sup>2</sup> API can implement this
 * interface and add itself to the contest as a Listener, and therefore arrange to be notified when 
 * any change is made to the contest configuration.
 * <p>
 * Each method receives a parameter of type {@link edu.csus.ecs.pc2.api.listener.ContestEvent} which provides
 * detailed information about the type of change which occurred.   Listeners implementing this interface can examine the 
 * contents of the specified {@link edu.csus.ecs.pc2.api.listener.ContestEvent} to determine what kind of configuration
 * item was added or changed, and what kind(s) of change(s) occurred.
 * 
 * <p>
 * This documentation describes the current <I>draft</i> of the PC<sup>2</sup> API, which is subject to change.
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
// $HeadURL$
public interface IConfigurationUpdateListener {

    /**
     * Invoked when a new contest configuration item has been added to the contest.
     * 
     * For example, the Contest Administrator may have added a Client Account, a new Language, a new Problem, etc.
     * 
     * @see ContestEvent
     * @param contestEvent A {@link ContestEvent} object giving details about the configuration item that has been added to the contest.
     */
    void configurationItemAdded(ContestEvent contestEvent);

    /**
     * Invoked when an existing contest configuration item has been updated (modified in some way).
     * 
     * For example, the Contest Administrator may have modified the parameters of a Client Account, the name of a contest Language 
     * or Problem, etc.
     * 
     * @see ContestEvent
     * @param contestEvent A {@link ContestEvent} object giving details about the configuration item that has been modified.
     */
    void configurationItemUpdated(ContestEvent contestEvent);

    /**
     * Invoked when an existing contest configuration item has been removed from the contest configuration.
     * 
     * For example, the Contest Administrator may have deleted a Client Account, a contest Language 
     * or Problem, etc.
     * 
     * @see ContestEvent
     * @param contestEvent A {@link ContestEvent} object giving details about the configuration item that has been removed from the contest.
     */
    void configurationItemRemoved(ContestEvent contestEvent);
}
