package edu.csus.ecs.pc2.core;

import java.io.Serializable;

import edu.csus.ecs.pc2.core.ClientType.Type;

/**
 * Basic Client Information.
 *
 * Contains site number, {@link ClientType.Type}, clientNumber, active.
 *
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class ClientId implements Serializable {

    public static final String SVN_ID = "$Id$";

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

    public String toString() {
        return clientType.toString() + clientNumber + " @ site " + siteNumber;
    }

    /**
     * @return Returns the clientNumber.
     */
    public int getClientNumber() {
        return clientNumber;
    }

    /**
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

    public String getTripletKey() {
        return getSiteNumber() + getClientType().toString() + getClientNumber();
    }

}
