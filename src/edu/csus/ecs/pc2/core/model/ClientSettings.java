package edu.csus.ecs.pc2.core.model;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

/**
 * Settings specific for each client.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ClientSettings implements IElementObject {

    /**
     * 
     */
    private static final long serialVersionUID = 2225749134503510088L;

    public static final String LOGIN_DATE = "LoginDate";

    private ClientId clientId;

    private ElementId elementId;

    private Properties properties = new Properties();
    
    private NotificationSetting notificationSetting = null;
    
    /**
     * Is this client auto judging, auto judging enabled ?.
     */
    private boolean autoJudging = false;
    
    /**
     * List of problems which judge can autojudge.
     */
    private Filter autoJudgeFilter = new Filter();

    /**
     * List of when clientId.getTripletKey() + problem.getElementId() got a balloon.
     */
    private Hashtable<String,BalloonDeliveryInfo> balloonList = new Hashtable<String,BalloonDeliveryInfo>();
    
    public Filter getAutoJudgeFilter() {
        return autoJudgeFilter;
    }

    public void setAutoJudgeFilter(Filter autoJudgeFilter) {
        this.autoJudgeFilter = autoJudgeFilter;
    }

    public boolean isAutoJudging() {
        return autoJudging;
    }

    public void setAutoJudging(boolean autoJudging) {
        this.autoJudging = autoJudging;
    }

    public ClientSettings() {
        elementId = new ElementId("ClientS");
    }

    public ClientSettings(ClientId clientId) {
        this.clientId = clientId;
        elementId = new ElementId("ClientS" + clientId);
        setSiteNumber(clientId.getSiteNumber());
    }

    public ElementId getElementId() {
        return elementId;
    }

    public int versionNumber() {
        return elementId.getVersionNumber();
    }

    public int getSiteNumber() {
        return elementId.getSiteNumber();
    }

    public void setSiteNumber(int siteNumber) {
        elementId.setSiteNumber(siteNumber);
    }

    public ClientId getClientId() {
        return clientId;
    }

    void setClientId(ClientId clientId) {
        this.clientId = clientId;
    }

    public void put(String name, String value) {
        properties.put(name, value);
    }

    public String getProperty(String name) {
        return properties.getProperty(name);
    }
    
    public String [] getKeys () {
        
        Vector<String> v = new Vector<String>();
        for (Enumeration<?> e = properties.propertyNames() ; e.hasMoreElements() ;) {
            String key = (String)e.nextElement();
            v.addElement(key);
        }
        
        return (String[]) v.toArray(new String[v.size()]);
    }

    /**
     * @return Returns the balloonList.
     */
    public Hashtable<String, BalloonDeliveryInfo> getBalloonList() {
        return balloonList;
    }

    /**
     * @param balloonList The balloonList to set.
     */
    public void setBalloonList(Hashtable<String, BalloonDeliveryInfo> balloonList) {
        this.balloonList = balloonList;
    }

    public NotificationSetting getNotificationSetting() {
        return notificationSetting;
    }

    public void setNotificationSetting(NotificationSetting notificationSetting) {
        this.notificationSetting = notificationSetting;
    }
}
