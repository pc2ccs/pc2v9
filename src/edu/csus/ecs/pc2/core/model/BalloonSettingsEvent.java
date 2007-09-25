package edu.csus.ecs.pc2.core.model;

/**
 * A BalloonSettings and a event state {@link edu.csus.ecs.pc2.core.model.BalloonSettingsEvent.Action}.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

//$HeadURL$
public class BalloonSettingsEvent {

    public static final String SVN_ID = "$Id$";

    /**
     * Run Event States(s).
     * 
     * @author pc2@ecs.csus.edu
     */
    public enum Action {

        /**
         * BalloonSettings to delete.
         */
        DELETED,
        /**
         * BalloonSettings added.
         */
        ADDED,
        /**
         * BalloonSettings modified.
         */
        CHANGED,

    }

    private Action action;

    private BalloonSettings balloonSettings;
    
    public BalloonSettingsEvent(Action action, BalloonSettings balloonSettings) {
        super();
        this.action = action;
        this.balloonSettings = balloonSettings;
    }

    public Action getAction() {
        return action;
    }

    public BalloonSettings getBalloonSettings() {
        return balloonSettings;
    }

}
