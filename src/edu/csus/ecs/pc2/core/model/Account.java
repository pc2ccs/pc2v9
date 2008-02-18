package edu.csus.ecs.pc2.core.model;

import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.PermissionList;


/**
 * Login Account.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class Account implements IElementObject {

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
    private String aliasName = "";
    
    /**
     * An external identifier, ex: an ICPC id
     */
    private String externalId = "";
    
    /**
     * Group id
     */
    private ElementId groupId;
    
    private PermissionList permissionList = new PermissionList();

    private String shortSchoolName = "";

    private String longSchoolName = "";

    private String externalName = "";

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
        setPassword(password);
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
        StringBuffer sb = new StringBuffer(password);

        String p = new String("");
        
        for (int i = 0; i < sb.length(); i++) {
            p = p + (char)(sb.charAt(i) ^ 0xfafa);
        }

        return p;
    }

    public void setPassword(String inPassword) {
        StringBuffer sb = new StringBuffer(inPassword);
        StringBuffer newStringBuffer = new StringBuffer();

        password = "";
        
        for (int i = 0; i < inPassword.length(); i++) {
            newStringBuffer.append((char)(sb.charAt(i) ^ 0xfafa));
        }

        password = new String(newStringBuffer);
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
            if (!getPassword().equals(account.getPassword())) {
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
            if (groupId == null || account.getGroupId() == null) {
                // if only 1 is null then return false
                if (!(groupId == null && account.getGroupId() == null)) {
                    return false;
                }
            } else {
                if (!groupId.equals(account.getGroupId())) {
                    return false;
                }
            }
            // TODO consider implementing a Class.getDeclaredFields to cover the strings
            if (!aliasName.equals(account.getAliasName())) {
                return false;
            }
            if (!externalId.equals(account.getExternalId())) {
                return false;
            }
            if (!externalName.equals(account.getExternalName())) {
                return false;
            }
            if (!longSchoolName.equals(account.getLongSchoolName())) {
                return false;
            }
            if (!shortSchoolName.equals(account.getShortSchoolName())) {
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

    public ElementId getGroupId() {
        return groupId;
    }

    public void setGroupId(ElementId groupId) {
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
