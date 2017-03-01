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
     * @param accountEvent the AccountEvent which triggered this accountAdded action
     */
    void accountAdded(AccountEvent accountEvent);

    /**
     * An account was modified.
     * 
     * @param accountEvent the AccountEvent which triggered this accountModified action
     */
    void accountModified(AccountEvent accountEvent);
    
    /**
     * Accounts were added.
     * 
     * @param accountEvent the AccountEvent which triggered this accountsAdded action
     */
    void accountsAdded(AccountEvent accountEvent);

    /**
     * Accounts were modified.
     * 
     * @param accountEvent the AccountEvent which triggered this accountsModified action
     */
    void accountsModified(AccountEvent accountEvent);

    /**
     * Reload all accounts.
     * 
     * @param accountEvent the AccountEvent which triggered this accountsRefreshAll action
     */
    void accountsRefreshAll(AccountEvent accountEvent);
}
