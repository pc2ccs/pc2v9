package edu.csus.ecs.pc2.core.model;


/**
 * Clarification Event
 *
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class ClarificationEvent {


    /**
     * Clarification Event States(s).
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    public enum Action {

        /**
         * Clarification set to be deleted.
         */
        DELETED,
        /**
         * A new clarification submission.
         */
        ADDED,
        /**
         * An updated clarification submission.
         */
        CHANGED,
        /**
         * The clarification from the server to be judged.
         */
        CHECKEDOUT_CLARIFICATION,
        /**
         * A clarification answer.
         */
        ANSWERED_CLARIFICATION,
        /**
         * A checked out rejudged clarification
         */
        CHECKEDOUT_REANSWER_CLARIFICATION,
        /**
         * A clarification is newly available.
         * 
         * Usually triggered by a cancel clarification.
         */
        CLARIFICATION_AVAILABLE,
        /**
         * Held Clarification
         */
        CLARIFICATION_HELD,
        /**
         * Clarification not available.
         * 
         * Send to a judge in response to a {@link #CHECKEDOUT_REANSWER_CLARIFICATION} or {@link #CHECKEDOUT_CLARIFICATION}
         */
        CLARIFICATION_NOT_AVAILABLE,
        /**
         * Clarification revoked
         */
        CLARIFICATION_REVOKED,
        /**
         * (Force) refresh of all clarification lists.
         */
        REFRESH_ALL,
    }

    private Action action;

    private Clarification clarification;

    /**
     * Who this clarification is sent to.
     */
    private ClientId sentToClientId;

    /**
     * 
     */
    private ClientId whoModifiedClarification;

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Clarification getClarification() {
        return clarification;
    }

    public void setClarification(Clarification clarification) {
        this.clarification = clarification;
    }

    public ClientId getSentToClientId() {
        return sentToClientId;
    }

    public void setSentToClientId(ClientId sentToClientId) {
        this.sentToClientId = sentToClientId;
    }

    public ClientId getWhoModifiedClarification() {
        return whoModifiedClarification;
    }

    public void setWhoModifiedClarification(ClientId whoModifiedClarification) {
        this.whoModifiedClarification = whoModifiedClarification;
    }

    public ClarificationEvent(Action action, Clarification clarification) {
        super();
        this.action = action;
        this.clarification = clarification;
    }

}
