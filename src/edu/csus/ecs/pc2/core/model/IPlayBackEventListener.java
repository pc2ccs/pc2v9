package edu.csus.ecs.pc2.core.model;


/**
 * Listener for all Language Events.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IPlayBackEventListener {

    /**
     * Playback Changed, added, started, stopped.
     * @param playBackEvent
     */
    void playbackChanged(PlayBackEvent playBackEvent);

    /**
     * Refresh/reload all playback info and records.
     * 
     * @param playBackEvent
     */
    void playbackRefreshAll(PlayBackEvent playBackEvent);

    /**
     * Added playback record(s) or new playback info.
     * 
     * @param playBackEvent
     */
    void playbackAdded(PlayBackEvent playBackEvent);
    
    /**
     * Rewind or reset playback.
     * 
     * @param playBackEvent
     */
    void playbackReset (PlayBackEvent playBackEvent);
    
    
}
