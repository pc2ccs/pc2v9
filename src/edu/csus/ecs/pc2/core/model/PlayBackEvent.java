package edu.csus.ecs.pc2.core.model;

/**
 * Playback event.
 * 
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

//$HeadURL$
public class PlayBackEvent {

    /**
     * Run Event States(s).
     * 
     * @author pc2@ecs.csus.edu
     */
    public enum Action {

        /**
         * A new language.
         */
        ADDED,
        /**
         * Run set to be deleted.
         */
        CHANGED,
        /**
         * Refresh all replay infos.
         */
        REFRESH_ALL, 
        /**
         *  Rewind/Reset replay.
         */
        RESET_REPLAY,
        /**
         * Start replay
         */
        START_REPLAY,
        /**
         * Halt/Stop replay
         */
        STOP_REPLAY,
        /**
         * Delete from list.
         */
        DELETE,

    }

    private Action action;

    private PlaybackInfo playbackInfo;

    public PlayBackEvent(Action problemAction, PlaybackInfo playbackInfo) {
        super();
        this.action = problemAction;
        this.playbackInfo = playbackInfo;
    }

    public Action getAction() {
        return action;
    }

    public PlaybackInfo getPlaybackInfo() {
        return playbackInfo;
    }

}
