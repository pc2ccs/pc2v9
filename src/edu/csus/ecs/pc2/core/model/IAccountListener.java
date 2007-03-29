package edu.csus.ecs.pc2.core.model;

/**
 * Listener for all Run Events.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public interface IAccountListener {

    /**
     * An account was added.
     * 
     * @param accountEvent
     */
    void accountAdded(AccountEvent accountEvent);

    /**
     * An account was modified.
     * 
     * @param accountEvent
     */
    void accountModified(AccountEvent accountEvent);
}
