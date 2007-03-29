package edu.csus.ecs.pc2.core.model;


/**
 * Login Account.
 *
 * @author pc2@ecs.csus.edu
 *
 *
 */

// $HeadURL$
public class Account implements IElementObject {
    public static final String SVN_ID = "$Id$";

    /**
     *
     */
    private static final long serialVersionUID = -1098364914694875689L;

    private ClientId clientId;

    /**
     * Unique id, version and site number.
     *
     */
    private ElementId elementId;

    private String password;

    private boolean active = true;

    private String displayName;

    /**
     * Create an account
     *
     * @param clientId
     *            ClientId
     * @param password
     *            String
     * @param siteNumber
     *            int
     */
    public Account(ClientId clientId, String password, int siteNumber) {
        super();
        elementId = new ElementId(clientId.toString());
        this.clientId = clientId;
        this.password = password;
        elementId.setSiteNumber(siteNumber);
        displayName = clientId.getClientType().toString().toLowerCase()
                + clientId.getClientNumber();
    }

    public String toString() {
        return displayName;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ClientId getClientId() {
        return clientId;
    }

    public void setClientId(ClientId clientId) {
        this.clientId = clientId;
    }

    public boolean isSameAs(Account account) {
        try {
            if (!displayName.equals(account.getDisplayName())) {
                return false;
            }
            if (!password.equals(account.getPassword())) {
                return false;
            }
            if (isActive() != account.isActive()) {
                return false;
            }
            if (getClientId().getClientNumber() != account.getClientId()
                    .getClientNumber()) {
                return false;
            }
            if (getClientId().getClientType() != account.getClientId()
                    .getClientType()) {
                return false;
            }
            if (!getClientId().equals(account.getClientId())) {
                return false;
            }
            return true;
        } catch (Exception e) {
            // TODO log to static exception log
            return false;
        }
    }

}
