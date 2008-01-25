package edu.csus.ecs.pc2.api;

/**
 * Run Event.
 * 
 * An event that indicates that some action has happened to/on a Run.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
// $HeadURL$

public class RunUpdateEvent {

    /**
     * Actions for RunUpdateEvent.
     * 
     * @see RunUpdateEvent
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    public enum Action {
        /**
         * Run added to system.
         */
        ADDED,
        /**
         * Run removed from system.
         */
        REMOVED,
        /**
         * Run has been judged.
         */
        JUDGED,
        /**
         * Run has been updated or rejudged.
         */
        UPDATED,
    }

    private Action action;

    private Run run;

    // TODO doc params
    /**
     * Create a RunUpdateEvent.
     * 
     * @param action
     * @param run
     */
    public RunUpdateEvent(Action action, Run run) {
        super();
        // TODO Auto-generated constructor stub
        this.action = action;
        this.run = run;
    }

    /**
     * Get the action triggered.
     * 
     * @return the action triggered.
     */
    public Action getAction() {
        return action;
    }

    /**
     * The run.
     * 
     * @return the run affected by the action.
     */
    public Run getRun() {
        return run;
    }

}
