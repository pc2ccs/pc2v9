package edu.csus.ecs.pc2.core.model;

/**
 * Event change for ReplaySettingEvent.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ReplaySettingEvent {



    /**
     * Action for ReplaySetting.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    public enum Action {

        /**
         * Run set to be deleted.
         */
        DELETED,
        /**
         * Problem added.
         */
        ADDED,
        /**
         * Problem modified.
         */
        CHANGED,
        /**
         * Reload/Refresh all problems.
         */
        REFRESH_ALL,

    }

    private Action action = Action.CHANGED;

    private ReplaySetting replaySetting = null;

    public ReplaySettingEvent(Action action, ReplaySetting replaySetting) {
        super();
        this.action = action;
        this.replaySetting = replaySetting;
    }
    
    public Action getAction() {
        return action;
    }
    
    public ReplaySetting getReplaySetting() {
        return replaySetting;
    }

}
