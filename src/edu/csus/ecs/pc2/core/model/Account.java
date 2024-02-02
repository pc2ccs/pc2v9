// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.model;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.list.AccountList;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.PermissionList;

/**
 * User/Login Account.
 * 
 * @author pc2@ecs.csus.edu
 */
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
    
    /**
     * Label (workstation number)
     */
    private String label;

    /**
     * Team name.
     */
    private String displayName;
    
    /**
     * An alias that may be displayed to judges to keep
     * the identity of a team consistent yet anonymous.
     */
    private String aliasName = "";
    
    /**
     * An external identifier, ex: an ICPC id.
     * 
     */
    private String externalId = "";
    
    /**
     * Group id
     */
    private ElementId groupId;
    
    private PermissionList permissionList = new PermissionList();
    
    /**
     * The scoring adjustment (positive or negative)
     * aka penalty time
     */
    private int scoringAdjustment = 0;
    
//    public static final String DEFAULT_INSTITUTIONNAME = "undefined";
//
//    public static final String DEFAULT_INSTITUTIONSHORTNAME = "undefined";
//
//    public static final String DEFAULT_COUNTRY = "XXX";

    /**
     * Institution short name.
     */
    private String shortSchoolName = "";

    /**
     * Institution name.
     */
    private String longSchoolName = "";

    private String externalName = "";

    private String countryCode = Constants.DEFAULT_COUNTRY_CODE;
    
    private String institutionName = Constants.DEFAULT_INSTITUTIONNAME;
    private String institutionShortName= Constants.DEFAULT_INSTITUTIONSHORTNAME;
    private String institutionCode  = Constants.DEFAULT_INSTITUTIONCODE;
    
    private String teamName = "";
    
    private String [] memberNames = new String[0];

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
        displayName = getDefaultDisplayName(clientId);
        externalId = Long.toString(AccountList.generateExternalId(this));
        // TODO - use label from json config file
        label = "" + clientId.getClientNumber();
    }
    
    public String getDefaultDisplayName(ClientId inClientId) {
        return inClientId.getClientType().toString().toLowerCase() + inClientId.getClientNumber();
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

    /**
     * Get Team title for display on the scoreboard.
     */
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
        if (inPassword == null){
            return;
        }
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
 
    
    // The special case is that the previous instances could have a null value which
    // needs to be compareed as equal to an empty string.

    /**
     * Deep compare of Account.
     * 
     * @param account
     * @return true if same, else false.
     */
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
            // SOMEDAY consider implementing a Class.getDeclaredFields to cover the strings
            if (!aliasName.equals(account.getAliasName())) {
                return false;
            }
            if (!label.equals(account.getLabel())) {
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

            if (!StringUtilities.stringSame(institutionCode, account.getInstitutionCode())) {
                return false;
            }

            if (!StringUtilities.stringSame(institutionShortName, account.getInstitutionShortName())) {
                return false;
            }

            if (!StringUtilities.stringSame(institutionName, account.getInstitutionName())) {
                return false;
            }
            
            if (permissionList == null || account.getPermissionList() == null) {
                // if only 1 is null then return false
                if (!(permissionList == null && account.getPermissionList() == null)) {
                    return false;
                }
            } else {
                if (!permissionList.isSameAs(account.getPermissionList())) {
                    return false;
                }
            }
            if (scoringAdjustment != account.getScoringAdjustment()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            StaticLog.getLog().log(Log.WARNING, "Exception in isSameAs", e);
            e.printStackTrace(System.err);
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

    /**
     * Alias or alternative login name.
     */
    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    /**
     * label, aka workstation number
     * 
     * @return label (workstation number)
     */
    public String getLabel() {
        // check for deserialization of old contest
        if(label == null) {
            label = "" + clientId.getClientNumber();
        }
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


    /**
     * External id, aka ICPC Reservation Id.
     * 
     * @return external id/reservation id.
     */
    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    /**
     * Group element id.
     */
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

    public void setExternalName(String externalName) {
        this.externalName = externalName;
    }

    /**
     * University Name from ICPC CMS data. 
     */
    public String getExternalName() {
        return externalName;
    }

    /**
     * Get Institution name (long).
     * 
     * @return Institution name.
     */
    public String getLongSchoolName() {
        return longSchoolName;
    }

    /**
     * Get Institution short name.
     */
    public String getShortSchoolName() {
        return shortSchoolName;
    }

    /**
     * The Nationality as ISO 3166-1 alpha-3, per CCS spec.
     */
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * Team member names.
     */
    public String[] getMemberNames() {
        return memberNames;
    }

    public void setMemberNames(String[] names) {
        if (names == null) {
            names = new String[0];
        }
        this.memberNames = names;
    }

    /**
     * Update certain fields.
     * 
     * @param account
     */
    public void updateFrom(Account account) {
        aliasName = account.aliasName;
        countryCode = account.countryCode;
        displayName = account.displayName;
        label = account.label;
        externalId = account.externalId;
        externalName = account.externalName;
        groupId = account.getGroupId();
        longSchoolName = account.longSchoolName;
        password = account.password;
        shortSchoolName = account.shortSchoolName;
        teamName = account.getTeamName();
        
        institutionCode = account.getInstitutionCode();
        institutionShortName = account.getInstitutionShortName();
        institutionName = account.getInstitutionName();

        permissionList = account.permissionList;

        memberNames = StringUtilities.cloneStringArray(account.memberNames);

        scoringAdjustment = account.getScoringAdjustment();
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
    
    /**
     * Team name, ex. Hornets.
     */
    public String getTeamName() {
        if (teamName==null || teamName.trim().equals("")) {
            return getDisplayName();
        } else {
            return teamName;
        }
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getInstitutionShortName() {
        return institutionShortName;
    }

    public void setInstitutionShortName(String institutionShortName) {
        this.institutionShortName = institutionShortName;
    }

    public String getInstitutionCode() {
        return institutionCode;
    }

    public void setInstitutionCode(String institutionCode) {
        this.institutionCode = institutionCode;
    }

    /**
     * @return the scoringAdjustement
     */
    public int getScoringAdjustment() {
        return scoringAdjustment;
    }

    /**
     * @param newScoringAdjustment the scoringAdjustement to set
     */
    public void setScoringAdjustment(int newScoringAdjustment) {
        this.scoringAdjustment = newScoringAdjustment;
    }
    
    /**
     * Is this account a team client type account?.
     * 
     * @return true if team clienttype
     */
    public boolean isTeam() {
        return clientId == null || clientId.getClientType().equals(Type.TEAM);
    }
}
