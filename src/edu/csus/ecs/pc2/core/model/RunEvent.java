package edu.csus.ecs.pc2.core.model;

/**
 * A run and a event state {@link edu.csus.ecs.pc2.core.model.RunEvent.Action}.
 * 
 * See {@link Contest#getRunIds()} for an example of use.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
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
        CHANGED,
        /**
         * The run from the server to be judged.
         */
        CHECKEDOUT_RUN,
        /**
         * A checked out rejudged run
         */
        CHECKEDOUT_REJUDGE_RUN,
        /**
         * A run is newly available.
         * 
         * Usually triggered by a cancel run.
         */
        RUN_AVAILABLE,
        /**
         * Held Run
         */
        RUN_HELD,
        /**
         * Run not available.
         * 
         * Send to a judge in response to a {@link #CHECKEDOUT_REJUDGE_RUN} or {@link #CHECKEDOUT_RUN}
         */
        RUN_NOT_AVIALABLE,
        /**
         * Run revoked.
         */
        RUN_REVOKED,
    }

    private Action action;

    private Run run;

    private RunFiles runFiles;
    
    private RunResultFiles[] runResultFiles;

    /**
     * Who this run is sent to.
     */
    private ClientId sentToClientId;

    /**
     * 
     */
    private ClientId whoModifiedRun;

    private String message;

    public RunEvent(Action action, Run run, RunFiles runFiles, RunResultFiles[] runResultFiles) {
        super();
        this.action = action;
        this.run = run;
        this.runFiles = runFiles;
        this.runResultFiles = runResultFiles;
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

    public ClientId getWhoModifiedRun() {
        return whoModifiedRun;
    }

    public void setWhoModifiedRun(ClientId whoModifiedRun) {
        this.whoModifiedRun = whoModifiedRun;
    }

    public RunResultFiles[] getRunResultFiles() {
        return runResultFiles;
    }

}
