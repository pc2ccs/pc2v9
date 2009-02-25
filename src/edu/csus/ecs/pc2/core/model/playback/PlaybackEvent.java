package edu.csus.ecs.pc2.core.model.playback;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IElementObject;
import edu.csus.ecs.pc2.core.model.ISubmission;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.SerializedFile;

/**
 * A single playback event.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PlaybackEvent implements IElementObject {

    /**
     * Actions for playback.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    public enum Action {
        /**
         * Undefined action.
         */
        UNDEFINED,
        /**
         * Team Submits a Run.
         */
        RUN_SUBMIT,
        /**
         * Submit a run judgement.
         */
        RUN_JUDGEMENT,
    }

    private ElementId elementId = null;

    private Action action = Action.UNDEFINED;

    private int sequenceId = 0;

    private Run run = null;

    private ISubmission submission = null;

    private ClientId submitterId;

    private ClientId clientId;

    private long eventTime;

    private EventStatus eventStatus = EventStatus.INVALID;

    private SerializedFile[] files = new SerializedFile[0];

    public SerializedFile[] getFiles() {
        return files;
    }

    public void setFiles(SerializedFile[] files) {
        this.files = files;
    }

    /**
     * 
     */
    private static final long serialVersionUID = -8414973988906358491L;

    public PlaybackEvent(Action action, ClientId clientId) {
        this.action = action;
        this.clientId = clientId;
    }

    public PlaybackEvent(Action action, ClientId clientId, Run run) {
        this.action = action;
        this.run = run;
        this.submission = run;
        this.clientId = clientId;
        submitterId = run.getSubmitter();
        eventStatus = EventStatus.PENDING;
        eventTime = run.getElapsedMins();
    }

    public long etElapsedMins() {
        return submission.getElapsedMins();
    }

    public Action getAction() {
        return action;
    }

    /**
     * The client who triggered this even.
     * 
     * A judge/admin client for clarification answers and judgements. A team for submitting clarification or run.
     * 
     * @return
     */
    public ClientId getClientId() {
        return clientId;
    }

    public ElementId getElementId() {
        return elementId;

    }

    public Run getRun() {
        return run;
    }

    /**
     * The sequence number for this event.
     * 
     * @return
     */
    public int getSequenceId() {
        return sequenceId;
    }

    public int getSiteNumber() {
        return elementId.getSiteNumber();
    }

    /**
     * The team or client that originated/submitted originally this submission.
     * 
     * The team who originally created the Run, Clarification or event.
     * 
     * @return
     */
    public ClientId getSubmitterId() {
        return submitterId;
    }

    public void setClientId(ClientId clientId) {
        this.clientId = clientId;
    }

    public void setSequenceId(int sequenceId) {
        this.sequenceId = sequenceId;
    }

    public void setSiteNumber(int siteNumber) {
        // TODO can not set site number, no visibility in ElementId
        // elementId.setSiteNumber(siteNumber);
    }

    public void setSubmitterId(ClientId submitterId) {
        this.submitterId = submitterId;
    }

    public int versionNumber() {
        return elementId.getVersionNumber();
    }

    /**
     * Elapsed time (minutes) when this event happened.
     * 
     * @return
     */
    public long getEventTime() {
        return eventTime;
    }

    public int getId() {
        if (run != null) {
            return run.getNumber();
        } else {
            return 0;
        }
    }

    public EventStatus getEventStatus() {
        return eventStatus;
    }
}
