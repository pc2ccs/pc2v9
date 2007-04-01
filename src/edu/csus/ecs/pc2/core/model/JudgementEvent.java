package edu.csus.ecs.pc2.core.model;

/**
 * A judgement and a event state {@link edu.csus.ecs.pc2.core.model.JudgementEvent.Action}.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class JudgementEvent {

    public static final String SVN_ID = "$Id$";

    /**
     * Run Event States(s).
     * 
     * @author pc2@ecs.csus.edu
     */
    public enum Action {

        /**
         * Run set to be deleted.
         */
        DELETED,
        /**
         * A new judgement submission.
         */
        ADDED,
        /**
         * A new judgement submission.
         */
        UPDATED,

    }

    private Action action;

    private Judgement judgement;

    public JudgementEvent(Action judgementAction, Judgement judgement) {
        super();
        this.action = judgementAction;
        this.judgement = judgement;
    }

    public Action getAction() {
        return action;
    }

    public Judgement getJudgement() {
        return judgement;
    }

}
