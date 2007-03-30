package edu.csus.ecs.pc2.core.model;

/**
 * A run and a event state {@link edu.csus.ecs.pc2.core.model.RunEvent.Action}.
 * 
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
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

    private Run run;

    private RunFiles runFiles;

    public RunEvent(Action action, Run run, RunFiles runFiles) {
        super();
        this.action = action;
        this.run = run;
        this.runFiles = runFiles;
    }

    public Action getAction() {
        return action;
    }

    public Run getRun() {
        return run;
    }

    // TODO this should not be returning a reference to a submitted run object, it needs to return an interface or read only version
    // of the data (clone?)

    public void setRun(Run run) {
        this.run = run;
    }

    // TODO this should not be returning a reference to a submitted run object, it needs to return an interface or read only version
    // of the data (clone?)

    public RunFiles getRunFiles() {
        return runFiles;
    }

    public void setRunFiles(RunFiles runFiles) {
        this.runFiles = runFiles;
    }

    public void setAction(Action action) {
        this.action = action;
    }

}
