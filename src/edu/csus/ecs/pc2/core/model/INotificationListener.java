package edu.csus.ecs.pc2.core.model;


/**
 * Notification Listener. 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: IInternalContest.java 2350 2011-09-20 17:10:32Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/core/model/IInternalContest.java $
public interface INotificationListener {
    


    /**
     * New Notification.
     * @param event
     */
    void notificationAdded(NotificationEvent event);

    /**
     * Notification information has changed.
     * @param event
     */
    void notificationChanged(NotificationEvent event);

    /**
     * Notification has been removed.
     * @param event
     */
    void notificationRemoved(NotificationEvent event);

    /**
     * Refresh all Notifications.
     * @param notificationEvent
     */
    void notificationRefreshAll(NotificationEvent event);



}
