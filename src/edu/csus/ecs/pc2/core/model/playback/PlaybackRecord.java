package edu.csus.ecs.pc2.core.model.playback;

import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IElementObject;
import edu.csus.ecs.pc2.core.model.ISubmission;
import edu.csus.ecs.pc2.core.model.playback.ReplayEvent.EventType;

/**
 * Information about re-running of {@link ReplayEvent}.
 * 
 * When a {@link ReplayEvent} is run by the {@link PlaybackManager} a
 * {@link PlaybackRecord} is created.
 * 
 * @see ReplayEvent
 * @see PlaybackManager
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PlaybackRecord implements IElementObject {

    /**
     * 
     */
    private static final long serialVersionUID = -63147005376962233L;
    
    private ElementId elementId = null;
    
    private int sequenceNumber = 0;
    
    private ReplayEvent replayEvent;
    
    private EventStatus eventStatus = EventStatus.INVALID;

    public PlaybackRecord(ReplayEvent replayEvent, int sequenceNumber) {
        super();
        this.replayEvent = replayEvent;
        this.sequenceNumber = sequenceNumber;
    }
    
    public void setEventStatus(EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }

    public ElementId getElementId() {
        return elementId;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public ReplayEvent getReplayEvent() {
        return replayEvent;
    }

    public EventStatus getEventStatus() {
        return eventStatus;
    }

    public void setSiteNumber(int siteNumber) {
        // TODO Arch can not set site number, no visibility in ElementId
        // elementId.setSiteNumber(siteNumber);
    }

    public int versionNumber() {
        return elementId.getVersionNumber();
    }
    
    public int getSiteNumber() {
        return elementId.getSiteNumber();
    }

    public EventType getEventType() {
        return replayEvent.getEventType();
    }

    public int getId() {
        ISubmission submission = replayEvent.getEventDetails().getSubmission();
        if (submission != null) {
            return submission.getNumber();
        } else {
            return 0;
        }
    }


    
}
