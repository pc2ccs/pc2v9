package edu.csus.ecs.pc2.core.model;

import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import edu.csus.ecs.pc2.core.model.playback.PlaybackRecord;
import edu.csus.ecs.pc2.core.model.playback.ReplayEvent;

/**
 * Playback Information.
 * 
 * Has values and methods for list of events ({@link #getReplayList()}), sequenceNumber, filename, state ({@link #isStarted()}), etc.
 * 
 * <br>
 * {@link ReplayEvent}s are the data for a replay, a {@link PlaybackRecord} is the actual record/status for
 * each playback event.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
// $HeadURL$
public class PlaybackInfo implements IElementObject {

    /**
     * 
     */
    private static final long serialVersionUID = -2365609088747285735L;

    /**
     * Title for the PlaybackInfo.
     */
    private String displayName = null;

    /**
     * Unique identifier for this instance of PlaybackInfo.
     */
    private ElementId elementId = null;

    private boolean active = true;

    private boolean started = false;

    private Date dateStarted = null;

    private Vector<ReplayEvent> playbackList = new Vector<ReplayEvent>();

    private int sequenceNumber = 0;

    private String filename = "";

    public PlaybackInfo(String displayName, ReplayEvent [] events) {
        super();
        this.displayName = displayName;
        elementId = new ElementId(displayName);
        setSiteNumber(0);
        if (events != null) {
            setPlaybackList (events);
        }
    }

    public PlaybackInfo(ReplayEvent [] events) {
        this("Playback", events);
    }
    
    public PlaybackInfo() {
        this("Playback", null);
    }

    /**
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @return Returns the title for this PlaybackInfo.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param active
     *            The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @param displayName
     *            The displayName to set.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @see Object#equals(java.lang.Object).
     */
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof PlaybackInfo) {
            PlaybackInfo otherLanguage = (PlaybackInfo) obj;
            return elementId.equals(otherLanguage.elementId);
        } else {
            throw new ClassCastException("expected an PlaybackInfo found: " + obj.getClass().getName());
        }
    }

    public String toString() {
        return displayName + " started " + isActive() + " seq " + sequenceNumber + " file " + filename;
    }

    /**
     * @return Returns the elementId.
     */
    public ElementId getElementId() {
        return elementId;
    }

    public int versionNumber() {
        return elementId.getVersionNumber();
    }

    public int getSiteNumber() {
        return elementId.getSiteNumber();
    }

    public void setSiteNumber(int siteNumber) {
        elementId.setSiteNumber(siteNumber);
    }

    public boolean isSameAs(PlaybackInfo language) {
        try {
            if (!displayName.equals(language.getDisplayName())) {
                return false;
            }
            if (isActive() != language.isActive()) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return elementId.hashCode();
    }

    int getTotalPlayback() {
        return playbackList.size();
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public int incrementSequenceNumber() {
        sequenceNumber++;
        return sequenceNumber;
    }

    public Date getDateStarted() {
        return dateStarted;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        if (! isStarted() && (started)) {
            dateStarted = new Date();
        }
        this.started = started;
    }

    public ReplayEvent[] getReplayList() {
        return (ReplayEvent[]) playbackList.toArray(new ReplayEvent[playbackList.size()]);
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setPlaybackList(Vector<ReplayEvent> playbackList) {
        this.playbackList = playbackList;
    }

    public void setPlaybackList(ReplayEvent[] list) {
        this.playbackList = new Vector<ReplayEvent>();
        playbackList.addAll(Arrays.asList(list));
    }

    public void rewind() {
        sequenceNumber = 0;
    }

}
