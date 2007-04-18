package edu.csus.ecs.pc2.core.model;

/**
 * A problem and a event state {@link edu.csus.ecs.pc2.core.model.ProblemEvent.Action}.
 * 
 * @author pc2@ecs.csus.edu
 */

//$HeadURL$
public class ProblemEvent {

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
         * Problem added.
         */
        ADDED,
        /**
         * Problem modified.
         */
        CHANGED,

    }

    private Action action;

    private Problem problem;

    public ProblemEvent(Action problemAction, Problem problem) {
        super();
        this.action = problemAction;
        this.problem = problem;
    }

    public Action getAction() {
        return action;
    }

    public Problem getProblem() {
        return problem;
    }

}
