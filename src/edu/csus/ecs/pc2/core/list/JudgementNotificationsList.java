package edu.csus.ecs.pc2.core.list;

import edu.csus.ecs.pc2.core.model.NotificationSetting;
import edu.csus.ecs.pc2.core.model.Problem;

/**
 * Maintains a list of {@link NotificationSetting}s.
 * 
 * The key to these settings is a {@link Problem}
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class JudgementNotificationsList extends ElementList {

    /**
     * 
     */
    private static final long serialVersionUID = 6844308353156838127L;

    /**
     * Add notification setting into list.
     * 
     * @param notificationSetting
     */
    public void add(NotificationSetting notificationSetting) {
        super.add(notificationSetting);
    }
    
    public NotificationSetting get(Problem problem) {
        return (NotificationSetting) super.get(problem.getElementId());
    }

    public NotificationSetting[] getList() {
        return (NotificationSetting[]) values().toArray(new NotificationSetting[size()]);
    }

}
