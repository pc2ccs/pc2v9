package edu.csus.ecs.pc2.api;

/**
 * This interface defines the view which is visible through the PC<sup>2</sup> API of a PC<sup>2</sup> &quot;Client&quot;.
 * &quot;Client&quot; in this context refers to a piece of code logged in to a PC<sup>2</sup> server via an account
 * which has been created by the Contest Administrator; for example, an instance of a PC<sup>2</sup> Team, Judge, Administrator, or
 * Scoreboard, or a separate user-written client which logs in using a contest account.
 * 
 * <p>
 * This documentation describes the current <I>draft</i> of the PC<sup>2</sup> API, which is subject to change.
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

public interface IClient {

    /**
     * This enumerates the Client types defined in the system (that is, the types of Clients visible through the PC<sup>2</sup> API).
     */
    public enum ClientType {
        /**
         * Unknown client type.
         */
        UNKNOWN_CLIENT, 
        /**
         * Team client.
         */
        TEAM_CLIENT,
        /**
         * Judge client.
         */
        JUDGE_CLIENT,
        /**
         * Scoreboard client.
         */
        SCOREBOARD_CLIENT,
        /**
         * Admin client.
         */
        ADMIN_CLIENT
    };

    /**
     * Get the client's login name.
     * 
     * Returns a String giving the login name (account name) currently used by this client to connect to the PC<sup>2</sup> server.
     * For example, for a team at site 5 logging in using the account &quot;team4&quot; the method would return &quot;team4&quot;".
     * 
     * @return client's login name
     */
    String getLoginName();

    /**
     * Get the client Type.
     * Returns an enum element identifying the type of this client.
     * 
     * @return {@link ClientType} enum element
     */
    ClientType getType();

    /**
     * Get the client's &quot;display name&quot;.
     * &quot;Display name&quot; refers to the printable description of the client as
     * configured by the Contest Administrator.  For example, a Team's &quot;display name&quot;
     * might be configured to consist of a combination of the <I>team name</i> and the Team's
     * <I>school name</i>.
     * 
     * @return Client's display name.
     */
    String getDisplayName();
    
    /**
     * Get the site number for this client.
     * Returns the number of the site which this client is logged into.
     * 
     * @return site number for this client.
     */
    int getSiteNumber();
    
    /**
     * Get the client account number for this client.
     * Returns a numerical identifier for this client's login name (account).
     * The combination of account number, client type, and site number uniquely identifies
     * any client in the contest.
     * @return a numerical identifier for this client's login name (account)
     */
    int getAccountNumber();
    
    /**
     * Check whether this client is the same as some other client.
     * <P>
     * Determination of whether two clients are equal is based on whether they refer to the
     * same client account as originally created in PC<sup>2</sup> by the Contest Administrator.  
     * Note in particular that subsequent changes to a client account
     * made by the Contest Administrator (for example,
     * changes to the account display name, password, etc.) do <I>not</i> affect the result of the
     * <code>equals()</code> method; if this client refers to the same client as the one indicated by the 
     * specified parameter, this method will return true regardless of whether the internal contents of the two
     * client objects is identical or not.
     * 
     * @param obj the Client which is to be compared with this client for equality.
     * @return True if the specified object refers to the same client as this client
     *          (regardless of the actual content of the two clients).
     */
    boolean equals(Object obj);

    /**
     * Get the hashcode associated with this client.
     * @return An integer hashcode for this object. 
     */
    int hashCode();
}
