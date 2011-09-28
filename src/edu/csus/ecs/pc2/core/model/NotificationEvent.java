package edu.csus.ecs.pc2.core.model;

/**
 * A notification event and {{@link #action}
 * 
 * @version $Id: NotificationEvent.java 2092 2010-06-25 17:17:56Z laned $
 * @author pc2@ecs.csus.edu
 */

//$HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/core/model/NotificationEvent.java $
public class NotificationEvent {


    /**
     * Notification Event States(s).
     * 
     * @author pc2@ecs.csus.edu
      * @version $Id: NotificationEvent.java 2092 2010-06-25 17:17:56Z laned $
     */
    public enum Action {

        /**
         * Run set to be deleted.
         */
        DELETED,
        /**
         * Notification added.
         */
        ADDED,
        /**
         * Notification modified.
         */
        CHANGED,
        /**
         * Reload/Refresh all notifications.
         */
        REFRESH_ALL,

    }

    private Action action;

    private Notification notification;
    
    public NotificationEvent(Action notificationAction, Notification notification) {
        super();
        this.action = notificationAction;
        this.notification = notification;
    }

    public Action getAction() {
        return action;
    }

    public Notification getNotification() {
        return notification;
    }

}

