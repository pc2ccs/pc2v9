package edu.csus.ecs.pc2.core.model;

import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.PermissionList;


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

    private String displayName;
    
    /**
     * An alias that may be displayed to judges to keep
     * the identity of a team consistent yet anonymous.
     */
    private String aliasName;
    
    /**
     * An external identifier, ex: an ICPC id
     */
    private String externalId;
    
    /**
     * Group id
     * TODO consider replacing with ElementId
     */
    private String groupId;
    
    private PermissionList permissionList = new PermissionList();

    private String shortSchoolName;

    private String longSchoolName;

    private String externalName;

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
    
    public void clearListAndLoadPermissions(PermissionList newPermissionList){
        permissionList.clearAndLoadPermissions(newPermissionList);
    }
    
    public void addPermission(Permission.Type type){
        permissionList.addPermission(type);
    }

    public void removePermission(Permission.Type type){
        permissionList.removePermission(type);
    }

    public boolean isAllowed (Permission.Type type){
        return permissionList.isAllowed(type);
    }

    public PermissionList getPermissionList() {
        return permissionList;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setShortSchoolName(String shortSchoolName) {
        this.shortSchoolName = shortSchoolName;
    }

    public void setLongSchoolName(String longSchoolName) {
        this.longSchoolName = longSchoolName;
    }

    /**
     * E.g. the team name from the icpc data
     * 
     * @param externalName
     */
    public void setExternalName(String externalName) {
        this.externalName = externalName;
    }

    public String getExternalName() {
        return externalName;
    }

    public String getLongSchoolName() {
        return longSchoolName;
    }

    public String getShortSchoolName() {
        return shortSchoolName;
    }

    
}
