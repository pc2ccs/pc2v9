package edu.csus.ecs.pc2.core;

/**
 * A run and a event state {@link edu.csus.ecs.pc2.core.RunEvent.Action}.
 * 
 * 
 * @author Douglas A. Lane
 */

//$HeadURL$
public class RunEvent {

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
         * A new run submission.
         */
        ADDED,
        /**
         * A new run submission.
         */
        UPDATED,

    }

    private Action action;

    private SubmittedRun submittedRun;

    public RunEvent(Action runAction, SubmittedRun submittedRun) {
        super();
        this.action = runAction;
        this.submittedRun = submittedRun;
    }

    public Action getAction() {
        return action;
    }

    // TODO this should not be returning a reference to a submitted run object, it needs to return an interface or read only version
    // of the data (clone?)

    public SubmittedRun getSubmittedRun() {
        return submittedRun;
    }

}
