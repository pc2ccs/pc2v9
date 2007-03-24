package edu.csus.ecs.pc2.core;

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
        UPDATED,
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

    };

    /**
     * Action for this event.
     */
    private Action action;

    /**
     * Account that was modified.
     */
    private Account account;

    public AccountEvent(Action action, Account account) {
        super();
        // TODO Auto-generated constructor stub
        this.action = action;
        this.account = account;
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
    
    

}
