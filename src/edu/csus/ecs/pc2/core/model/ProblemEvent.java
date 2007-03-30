package edu.csus.ecs.pc2.core.model;

/**
 * A problem and a event state {@link edu.csus.ecs.pc2.core.model.ProblemEvent.Action}.
 * 
 * @author pc2@ecs.csus.edu
 */

//$HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/tproblemk/src/edu/csus/ecs/pc2/core/model/RunEvent.java $
public class ProblemEvent {

    public static final String SVN_ID = "$Id: RunEvent.java 59 2007-03-29 08:28:17Z laned $";

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
         * A new problem submission.
         */
        ADDED,
        /**
         * A new problem submission.
         */
        UPDATED,

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
