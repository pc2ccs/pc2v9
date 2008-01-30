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
     * Get client short (login) name.
     * 
     * Example for team 4 site 5, would return "team4"
     * 
     * @return login name
     */
    String getShortName();

    /**
     * Get the display name or title for this client.
     * 
     * 
     * @return display name.
     */
    String getTitle();
}
