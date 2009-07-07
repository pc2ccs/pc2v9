package edu.csus.ecs.pc2.core.model;

/**
 * A profile and a event state {@link edu.csus.ecs.pc2.core.model.ProfileEvent.Action}.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProfileEvent {

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
         * A new profile.
         */
        ADDED,
        /**
         * Modified Profile.
         */
        CHANGED,
    }

    private Action action;

    private Profile profile;

    public ProfileEvent(Action action, Profile profile) {
        super();
        this.action = action;
        this.profile = profile;
    }

    public Action getAction() {
        return action;
    }

    public Profile getProfile() {
        return profile;
    }
}
