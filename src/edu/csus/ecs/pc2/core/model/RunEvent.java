package edu.csus.ecs.pc2.core.model;

/**
 * A run and a event state {@link edu.csus.ecs.pc2.core.model.RunEvent.Action}.
 * 
 * 
 * @author pc2@ecs.csus.edu
 */

// TODO should the get methods return clones or the references to the fields ??

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
         * An updated run submission.
         */
        UPDATED,
        /**
         * A checked out run
         */
        CHECKEDOUT_RUN,
        /**
         * A checked out rejudged run
         */
        CHECKEDOUT_REJUDGE_RUN,
        /**
         * A run has not been 
         */
        RUN_AVIALABLE,
        /**
         * Held Run
         */
        RUN_HELD,
    }

    private Action action;

    private Run run;

    private RunFiles runFiles;
    
    /**
     * Who this run is sent to.
     */
    private ClientId sentToClientId;
    
    private String message;

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

    public void setRun(Run run) {
        this.run = run;
    }

    public RunFiles getRunFiles() {
        return runFiles;
    }

    public void setRunFiles(RunFiles runFiles) {
        this.runFiles = runFiles;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public ClientId getSentToClientId() {
        return sentToClientId;
    }

    public void setSentToClientId(ClientId sentToClientId) {
        this.sentToClientId = sentToClientId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
