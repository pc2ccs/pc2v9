package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;

import edu.csus.ecs.pc2.core.model.ClientType.Type;

/**
 * Client Identification Information (site, type, and number).
 *
 * Contains site number, {@link ClientType.Type}, clientNumber, active.
 *
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// TODO doc complete javadoc for methods.

// $HeadURL$
public class ClientId implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -3481561733498755619L;

    public static final int UNSET = 0;

    private int siteNumber = UNSET;

    private int clientNumber = 0;

    private ClientType.Type clientType = ClientType.Type.UNKNOWN;

    public ClientId(int siteNumber, ClientType.Type type, int clientNumber) {
        this.siteNumber = siteNumber;
        this.clientNumber = clientNumber;
        clientType = type;
    }

    private boolean active = true;

    /**
     * Prints the type, number and site.
     * 
     * Example for team 4 site 3, will return "TEAM4 @ site 3".
     * 
     */
    public String toString() {
        return clientType.toString() + clientNumber + " @ site " + siteNumber;
    }

    /**
     * Gets the client number.
     * 
     * The client number is assigned to each Account/Client, for example
     * for team 5 the client number is 5.
     * @return Returns the clientNumber.
     */
    public int getClientNumber() {
        return clientNumber;
    }

    /**
     * Get the client type.
     * 
     * The client type is one of the predefined client types
     * found in {@link ClientType.Type}.
     * 
     * @return Returns the clientType.
     */
    public ClientType.Type getClientType() {
        return clientType;
    }

    /**
     * @return Returns the siteNumber.
     */
    public int getSiteNumber() {
        return siteNumber;
    }

    /**
     * Returns short name of client in lower case.
     *
     * Note that admin 0 returns root.
     *
     *
     * @return short version of name (team1, admin1)
     */
    public String getName() {
        if (clientNumber == 0 && clientType == Type.ADMINISTRATOR) {
            return "root";
        }

        return new String("" + clientType + clientNumber).toLowerCase();
    }

    /**
     * 
     * 
     * 
     * 
     * @return true if client is marked as active.
     */
    protected boolean isActive() {
        return active;
    }

    protected void setActive(boolean active) {
        this.active = active;
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof ClientId) {
            ClientId otherId = (ClientId) obj;
            return (getClientType() == otherId.getClientType()
                    && getClientNumber() == otherId.getClientNumber() && getSiteNumber() == otherId
                    .getSiteNumber());
        } else {
            return false;
        }
    }

    /**
     * Get Triplet Key (SiteNumber and ClientType and ClientNumber).
     * 
     * For team 5 site 12 will return "12TEAM5"
     * 
     * @return a string that is composed of site number and client type name and client number.
     */
    public String getTripletKey() {
        return getSiteNumber() + getClientType().toString() + getClientNumber();
    }

    public void setSiteNumber(int siteNumber) {
        this.siteNumber = siteNumber;
    }

}
