package edu.csus.ecs.pc2.api;

/**
 * Contest Client (Account).
 * 
 * Contains information about a contest account.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

public interface IClient {

    /**
     * Client types defined in the system.
     */
    public enum ClientType {
        /**
         * Team client.
         */
        TEAM_CLIENT,
        /**
         * Judge client.
         */
        JDUGE_CLIENT,
        /**
         * Scoreboard client.
         */
        SCOREBOARD_CLIENT,
        /**
         * Admin client.
         */
        ADMIN_CLIENT,
        /**
         * Custom client.
         */
        CUSTOM_CLIENT,
        /**
         * Other client.
         */
        OTHER_CLIENT,
        /**
         * Unknown client.
         */
        UNKNOWN_CLIENT 
    };

    /**
     * Get client login name.
     * 
     * Example for team 4 site 5, would return "team4"
     * 
     * @return login name
     */
    String getLoginName();

    /**
     * Get the client Type.
     * 
     * @return enum type
     */
    ClientType getType();

    /**
     * Get the &quot;display name&quot; for this client.
     * 
     * 
     * @return Client's display name.
     */
    String getDisplayName();
    
    /**
     * Get the site number for this client.
     * 
     * @return site number for this client.
     */
    int getSiteNumber();
    
    /**
     * Get the client number for the client.
     * 
     * For example, for a client whose login name is &quot;team4&quot; the method will return the integer 4.
     * 
     * @return the client number, ex 4 for team4
     */
    int getNumber();
}
