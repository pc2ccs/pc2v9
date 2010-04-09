package edu.csus.ecs.pc2.core.model;

/**
 * Listener for all Run Events.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
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
    /**
     * Accounts were added.
     * 
     * @param accountEvent
     */
    void accountsAdded(AccountEvent accountEvent);

    /**
     * Accounts were modified.
     * 
     * @param accountEvent
     */
    void accountsModified(AccountEvent accountEvent);

    /**
     * Reload all accounts.
     * 
     * @param accountEvent
     */
    void accountsRefreshAll(AccountEvent accountEvent);
}
