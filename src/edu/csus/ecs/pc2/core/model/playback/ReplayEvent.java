package edu.csus.ecs.pc2.core.model.playback;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IElementObject;

/**
 * Defines a playback event.
 * 
 * A playback event {@link ReplayEvent} is tracked during playback
 * using a {@link PlaybackRecord}.  Details (files, run info, clar info) for
 * this event is present in the {@link ReplayEventDetails}.
 * 
 * @see ReplayEventDetails
 * @see PlaybackRecord
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ReplayEvent implements IElementObject {

    /**
     * 
     */
    private static final long serialVersionUID = -2096823165590915352L;

    /**
     * Type of event.
     * 
     * {@link #RUN_SUBMIT}, {@link #RUN_JUDGEMENT}, etc.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    public enum EventType {
        /**
         * Undefined eventType.
         */
        UNDEFINED,
        /**
         * Team Submits a Run.
         */
        RUN_SUBMIT,
        /**
         * a run judgement.
         */
        RUN_JUDGEMENT,
        /**
         * Team submits a Clarification.
         */
        CLAR_SUBMIT,
        /**
         * A clarification answer.
         */
        CLAR_ANSWER
    }

    private ElementId elementId = null;

    private int sequenceId = 0;

    private ClientId submitterId;

    private ClientId clientId;

    private long eventTime;

    private EventType eventType = EventType.UNDEFINED;
    
    private ReplayEventDetails eventDetails = null;

    @SuppressWarnings("unused")
    private ReplayEvent() {
        super();
    }
    
    /**
     * Shallow clone.
     * 
     * @param replayEvent
     */
    protected ReplayEvent (ReplayEvent replayEvent) {
        this(replayEvent.getEventType(), replayEvent.getClientId());
        setElementId(replayEvent.getElementId());
        sequenceId = replayEvent.getSequenceId();
        submitterId= replayEvent. getSubmitterId();
        eventTime= replayEvent. getEventTime();
        /**
         * Do not clone details, thus the shallow clone.
         */
         eventDetails = null;
    }
    
    private void setElementId(ElementId elementId) {
        this.elementId = elementId;
    }

    public ReplayEvent(EventType eventType, ClientId clientId) {
        this.eventType = eventType;
        this.clientId = clientId;
        elementId = new ElementId("ReplayEvent");
    }
    
    public ReplayEvent(EventType eventType, ClientId clientId, int sequence) {
        this.eventType = eventType;
        this.clientId = clientId;
        this.sequenceId = sequence;
        elementId = new ElementId("ReplayEvent");
    }
    
    public EventType getEventType() {
        return eventType;
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

    /**
     * The sequence number for this event.
     * 
     * @return
     */
    public int getSequenceId() {
        return sequenceId;
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

    /**
     * @deprecated use constructor {@link #ReplayEvent(EventType, ClientId, int)}
     * @param sequenceId
     */
    public void setSequenceId(int sequenceId) {
        this.sequenceId = sequenceId;
    }

    public void setSiteNumber(int siteNumber) {
        // TODO Arch can not set site number, no visibility in ElementId
        // elementId.setSiteNumber(siteNumber);
    }

    public void setSubmitterId(ClientId submitterId) {
        this.submitterId = submitterId;
    }

    public int versionNumber() {
        return elementId.getVersionNumber();
    }

    public int getSiteNumber() {
        return elementId.getSiteNumber();
    }

    /**
     * Elapsed time (minutes) when this event happened.
     * 
     * @return
     */
    public long getEventTime() {
        return eventTime;
    }
    
    public void setEventDetails(ReplayEventDetails eventDetails) {
        this.eventDetails = eventDetails;
    }
    
    public ReplayEventDetails getEventDetails() {
        return eventDetails;
    }
    
    public ReplayEvent shallowClone (ReplayEvent inEvent) {
        return new ReplayEvent(inEvent);
    }
    
    @Override
    public String toString() {
        return "Replay " + eventType+" at "+eventTime+" by "+clientId+" details: "+eventDetails;
    }

}
