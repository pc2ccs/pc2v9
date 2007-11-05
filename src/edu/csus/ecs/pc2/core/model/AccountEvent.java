package edu.csus.ecs.pc2.core.model;

/**
 * An Account Change Event.
 * 
 * @author pc2@ecs.csus
 * 
 */

// $HeadURL$
public class AccountEvent {

    /**
     * Account Event actions.
     * 
     * @author pc2@ecs.csus
     * 
     */
    public enum Action {

        /**
         * Account updated.
         */
        CHANGED,
        /**
         * Account deleted
         */
        DELETED,
        /**
         * Account deactivated
         */
        DEACTIVATED,
        /**
         * Account added
         */
        ADDED,
        /**
         * More then 1 account was added
         */
        ADDED_ACCOUNTS,
        /**
         * Move then 1 account was updated
         */
        CHANGED_ACCOUNTS,

    };

    /**
     * Action for this event.
     */
    private Action action;

    /**
     * Account that was modified.
     */
    private Account account;

    private Account[] accounts;
    
    public AccountEvent(Action action, Account account) {
        super();
        // TODO Auto-generated constructor stub
        this.action = action;
        this.account = account;
    }
    
    public AccountEvent(Action action, Account[] accounts) {
        super();
        this.action = action;
        this.accounts = accounts;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    /**
     * @return Returns the accounts.
     */
    public Account[] getAccounts() {
        return accounts;
    }

    /**
     * @param accounts The accounts to set.
     */
    public void setAccounts(Account[] accounts) {
        this.accounts = accounts;
    }
    
    

}
