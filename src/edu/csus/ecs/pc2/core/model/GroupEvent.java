package edu.csus.ecs.pc2.core.model;

/**
 * A group and a event state {@link edu.csus.ecs.pc2.core.model.GroupEvent.Action}.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class GroupEvent {

    public static final String SVN_ID = "$Id$";

    /**
     * Run Event States(s).
     * 
     * @author pc2@ecs.csus.edu
     */
    public enum Action {

        /**
         * Group deleted.
         */
        DELETED,
        /**
         * A new group.
         */
        ADDED,
        /**
         * Modify a group.
         */
        CHANGED,

    }

    private Action action;

    private Group group;

    public GroupEvent(Action groupAction, Group group) {
        super();
        this.action = groupAction;
        this.group = group;
    }

    public Action getAction() {
        return action;
    }

    public Group getGroup() {
        return group;
    }

}
