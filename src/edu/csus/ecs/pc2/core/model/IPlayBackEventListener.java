package edu.csus.ecs.pc2.core.model;


/**
 * Listener for all Language Events.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IPlayBackEventListener {

    void playbackChanged(PlayBackEvent playBackEvent);

    void playbackRefreshAll(PlayBackEvent playBackEvent);

    void playbackEvent(PlayBackEvent playBackEvent);

    void playbackAdded(PlayBackEvent playBackEvent);
    
}
