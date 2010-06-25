package edu.csus.ecs.pc2.core.model;

/**
 * A problem and a event state {@link edu.csus.ecs.pc2.core.model.ProblemEvent.Action}.
 * 
 * @version $Id$
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
        /**
         * Reload/Refresh all problems.
         */
        REFRESH_ALL,

    }

    private Action action;

    private Problem problem;
    
    @SuppressWarnings("unused")
    private ProblemDataFiles problemDataFiles;

    public ProblemEvent(Action problemAction, Problem problem) {
        super();
        this.action = problemAction;
        this.problem = problem;
    }

    public ProblemEvent(Action problemAction, Problem problem, ProblemDataFiles problemDataFiles) {
        this.action = problemAction;
        this.problem = problem;
        this.problemDataFiles = problemDataFiles;
    }

    public Action getAction() {
        return action;
    }

    public Problem getProblem() {
        return problem;
    }

}
