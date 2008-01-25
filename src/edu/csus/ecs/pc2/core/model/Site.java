package edu.csus.ecs.pc2.core.model;

import java.util.Enumeration;
import java.util.Properties;

/**
 * Single Site Definition.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
// $HeadURL$
public class Site implements IElementObject {

    /**
     * 
     */
    private static final long serialVersionUID = 927149732372774783L;

    /**
     * Connection Info Property Key for port
     */
    public static final String PORT_KEY = "PORT_KEY";

    /**
     * Connection Info Property Key for IP (or hostname)
     */
    public static final String IP_KEY = "IP_KEY";

    /**
     * Title for the Site.
     */
    private String displayName = null;

    private Properties connectionInfo = new Properties();

    private String connectionDisplayInfo = "";

    /**
     * Unique identifier for this instance of Site.
     */
    private ElementId elementId = null;

    private boolean active = true;

    private String password;

    public Site(String displayName, int siteNumber) {
        super();
        this.displayName = displayName;
        elementId = new ElementId(displayName);
        setSiteNumber(siteNumber);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String toString() {
        return displayName;
    }

    /**
     * @return Returns the elementId.
     */
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

    public String getConnectionDisplayInfo() {
        return connectionDisplayInfo;
    }

    public void setConnectionDisplayInfo(String connectionDisplayInfo) {
        this.connectionDisplayInfo = connectionDisplayInfo;
    }

    public Properties getConnectionInfo() {
        return connectionInfo;
    }

    public void setConnectionInfo(Properties connectionInfo) {
        this.connectionInfo = connectionInfo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof Site) {
            Site otherSite = (Site) obj;
            return otherSite.getElementId().equals(elementId);
        } else {
            return false;
        }
    }

    /**
     * Clone this site.
     * 
     * Identical in every way, even elementId.
     */
    public Site clone() {
        Site newSite = new Site(this.getDisplayName(), this.getSiteNumber());
        newSite.elementId = elementId;
        newSite.active = active;
        newSite.password = new String(password);

        String[] keys = connectionInfo.keySet().toArray(new String[connectionInfo.size()]);
        for (String key : keys) {
            newSite.getConnectionInfo().put(key, connectionInfo.get(key));
        }

        return newSite;
    }

    public boolean isSameAs(Site site) {

        try {

            if (!displayName.equals(site.getDisplayName())) {
                return false;
            }
            if (isActive() != site.isActive()) {
                return false;
            }
            if (!connectionDisplayInfo.equals(site.getConnectionDisplayInfo())) {
                return false;
            }
            if (!password.equals(site.getPassword())) {
                return false;
            }

            Enumeration enumeration = connectionInfo.keys();

            while (enumeration.hasMoreElements()) {
                String element = (String) enumeration.nextElement();
                if (!connectionInfo.get(element).equals(site.getConnectionInfo().get(element))) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            // TODO add to Static exception Log
            return false;
        }
    }

    @Override
    public int hashCode() {
        // use elementId to be consistent with equals()
        return elementId.hashCode();
    }

}
