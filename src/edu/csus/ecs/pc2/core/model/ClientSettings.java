package edu.csus.ecs.pc2.core.model;

import java.util.Properties;

/**
 * Settings specific for each client.
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class ClientSettings implements IElementObject {

    /**
     * 
     */
    private static final long serialVersionUID = 2225749134503510088L;

    public static final String SVN_ID = "$Id$";

    private ClientId clientId;

    private ElementId elementId;

    private Properties properties = new Properties();

    public ClientSettings() {
        elementId = new ElementId("ClientS");
    }

    public ClientSettings(ClientId clientId) {
        elementId = new ElementId("ClientS" + clientId);
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

    public String getProperty(String name, String value) {
        return properties.getProperty(name);
    }
}
