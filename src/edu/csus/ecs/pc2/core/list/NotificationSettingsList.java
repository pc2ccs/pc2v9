package edu.csus.ecs.pc2.core.list;

import edu.csus.ecs.pc2.core.model.IElementObject;
import edu.csus.ecs.pc2.core.model.NotificationSetting;

/**
 * Maintains a list of {@link NotificationSetting}s.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class NotificationSettingsList extends BaseElementList {

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

//    private ClientId getGenericClientId(Type type) {
//        return new ClientId(0, type, 0);
//    }

//    /**
//     * Add a notification setting for all users of a client type.
//     * 
//     * @param type
//     * @param notificationSetting
//     */
//    public void add(Type type, NotificationSetting notificationSetting) {
//        ClientId clientId = getGenericClientId(type);
//        NotificationSetting setting = new NotificationSetting(clientId);
//
//        setting.setFinalNotificationNo(notificationSetting.getFinalNotificationNo());
//        setting.setFinalNotificationYes(notificationSetting.getFinalNotificationYes());
//
//        setting.setPreliminaryNotificationNo(notificationSetting.getPreliminaryNotificationNo());
//        setting.setPreliminaryNotificationYes(notificationSetting.getPreliminaryNotificationYes());
//
//        super.add(setting);
//    }

    public NotificationSetting[] getList() {
        return (NotificationSetting[]) values().toArray(new NotificationSetting[size()]);
    }

//    public NotificationSetting get(Type type) {
//        return get(getGenericClientId(type));
//    }

    @Override
    public String getKey(IElementObject elementObject) {
        return elementObject.toString();
//        NotificationSetting notificationSetting = (NotificationSetting) elementObject;
//        return notificationSetting.getClientId().toString();
    }

//    public NotificationSetting get(ClientId clientId) {
//        return (NotificationSetting) super.get(new NotificationSetting(clientId));
//    }

}
