package edu.csus.ecs.pc2.core.model;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Single Site Definition.
 * 
 * @author pc2@ecs.csus.edu
 */
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
    
    public static final String NUMBER_PATTERN = "%#";
    public static final String LONG_NAME_PATTERN = "%L";
    public static final String SHORT_NAME_PATTERN = "%S";

    private static final int NO_PROXY_SITE_ASSINGED = -1;

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

    private int [] proxySites = null;
    
    /**
     * Proxy host for this site.
     * 
     * All incoming and outgoing packets must go through this site.
     */
    private int myProxy = NO_PROXY_SITE_ASSINGED;

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
        newSite.myProxy = myProxy;

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
            if (myProxy != site.myProxy){
                return false;
            }

            Enumeration<?> enumeration = connectionInfo.keys();

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
    
    /**
     * 
     * @param pattern
     * @return
     */
    public StringBuffer format(String pattern) {

        StringBuffer buffer = new StringBuffer();

        if (pattern.equals(NUMBER_PATTERN)) {
            buffer.append(getSiteNumber());
        } else if (pattern.equals(NUMBER_PATTERN + " - " + LONG_NAME_PATTERN)) {
            buffer.append(getSiteNumber());
            buffer.append(" - ");
            buffer.append(getDisplayName());
        } else if (pattern.equals(SHORT_NAME_PATTERN)) {
            buffer.append("Site ");
            buffer.append(getSiteNumber());
        } else if (pattern.equals(LONG_NAME_PATTERN)) {
            buffer.append(toString());
        }

        return buffer;
    }
        
    @Override
    public int hashCode() {
        // use elementId to be consistent with equals()
        return elementId.hashCode();
    }
    
    /**
     * Is this site a proxy for another site?
     * @param siteNumber
     * @return true if proxy, otherwise false
     */
    public boolean isProxyFor (Site site){
        
        if (site == null){
            return false;
        }
        
        return isProxyFor(site.getSiteNumber());
    }
    
    /**
     * Is this site a proxy for another site?
     * @param siteNumber
     * @return true if proxy, otherwise false
     */
    public boolean isProxyFor (int siteNumber){
        
        if (proxySites == null){
            return false;
        }
        
        if (proxySites.length == 1 && proxySites[0] == siteNumber) {
            return true;
        }

        for (int siteNum : proxySites) {
            if (siteNum == siteNumber) {
                return true;
            }
        }

        return false;
    }

    public int[] getProxySites() {
        return proxySites;
    }

    /**
     * Add another site as a proxy site for this site.
     * 
     * @param siteNumber
     */
    public void addProxySite(int siteNumber) {
        if (!isProxyFor(siteNumber)) {
            // Can't proxy oneself, that would be bad
            if (siteNumber != getSiteNumber()) {

                if (proxySites == null) {
                    proxySites = new int[1];
                    proxySites[0] = siteNumber;
                } else {
                    int[] newlist = Arrays.copyOf(proxySites, proxySites.length + 1);
                    newlist[proxySites.length] = siteNumber;
                    proxySites = newlist;

                }
            }
        }
    }

    public void setProxySites(int[] proxySites) {
        this.proxySites = proxySites;
    }

    /**
     * Remove all proxies.
     * 
     * Remove all proxy sites from this site.
     * 
     */
    public void removeAllProxies() {
        proxySites = null;
    }
    
    /**
     * Does this site have a proxy?
     * @see #getProxySites()
     * @return true if has proxy assigned
     */
    public boolean hasProxy() {
        return myProxy != NO_PROXY_SITE_ASSINGED;
    }
    
    /**
     * Un-proxy site.
     * 
     * The opposite of {@link #setMyProxy(int)}, this removes
     * the proxy site for this site.
     */
    public void unsetProxy () {
        myProxy = NO_PROXY_SITE_ASSINGED;
    }
    
    /**
     * Set a proxy to another site.
     * Use {@link #unsetProxy()} to remove proxy site assignment. 
     * @see #unsetProxy()
     * @param myProxy
     */
    public void setMyProxy(int myProxy) {
        this.myProxy = myProxy;
    }
    
    /**
     * Get my current proxy site.
     * 
     * @return {@value #NO_PROXY_SITE_ASSINGED} if no proxy assigned, else return proxy site number.
     */
    public int getMyProxy() {
        return myProxy;
    }

}
