// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.services.eventFeed;

import java.util.Properties;

public class WebServerPropertyUtils {
    
    // keys for web service properties

    public static final String PORT_NUMBER_KEY = "port";

    public static final String CLICS_CONTEST_API_SERVICES_ENABLED_KEY = "enableCLICSContestAPI";

    public static final String STARTTIME_SERVICE_ENABLED_KEY = "enableStartTime";

    public static final String FETCH_RUN_SERVICE_ENABLED_KEY = "enableFetchRun";
    
    public static final String CLICS_API_VERSION = "apiVersion";
    
    public static final String CLICS_API_PACKAGE = "clicsAPIPackage";

    private Properties wsProperties = null;
    
    /**
     * 
     * @param props Web Server properties from pc2ws.properties
     */
    public WebServerPropertyUtils(Properties props)
    {
        wsProperties = props;
    }

    /**
     * Returns the value of the specified property in the global wsProperties table, or the specified boolean value if the specified property is not found in the wsProperties table. Property values
     * "true", "yes", "on", and "enabled" are treated as true; any other string is considered false.
     * 
     * @param key
     *            - a wsProperties table property key
     * @param b
     *            - the value to be returned if key is not found in wsProperties
     * 
     * @return true if key is found in wsProperties and has a value which is any of "true", "yes", "on", or "enabled"; false if key is found in wsProperties but has any other value; b if key is not
     *         found in wsProperties
     */
    public boolean getBooleanProperty(String key, boolean b) {

        String value = wsProperties.getProperty(key);

        if (value == null) {
            return b;
        } else {
            return "true".equalsIgnoreCase(value.trim()) || //
                    "yes".equalsIgnoreCase(value.trim()) || //
                    "on".equalsIgnoreCase(value.trim()) || //
                    "enabled".equalsIgnoreCase(value.trim());
        }

    }

    /**
     * Gets the integer value of a property
     * 
     * @param key Property to look up
     * @param defaultValue Value to return if not found
     * @return Integer Property value
     */
    public int getIntegerProperty(String key, int defaultValue) {

        String value = wsProperties.getProperty(key);

        if (value == null) {
            return defaultValue;
        } else {
            try {
                return Integer.parseInt(value);
            } catch (Exception e) {
                return defaultValue;
            }
        }
    }

    /**
     * Gets the string value of a property
     * 
     * @param key Property to look up
     * @param defaultValue Value to return if not found
     * @return Property value
     */
    public String getStringProperty(String key, String defaultValue) {

        String value = wsProperties.getProperty(key);

        if (value == null) {
            value = defaultValue;
        }
        return(value);
    }

}
