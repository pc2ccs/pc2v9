// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
/**
 *
 */
package edu.csus.ecs.pc2.core.imports;

import java.util.HashMap;

import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;

/**
 * ICPC Account information.
 *
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ICPCAccount {

    private int accountNumber = 0;
    /**
     * An external identifier, ex: an ICPC id
     */
    private String externalId = "";

    /**
     * Group id
     */
    private HashMap<ElementId, String> groups;

    /**
     * Used for importing from an old teams.tab type file, there is one group
     * specified in the tab file, and it is a CMS id.  We store it here and it will
     * get converted after the account is created and placed in groups above.
     */
    private String externalGroupId;

    private ClientId clientId;

    private String shortSchoolName = "";

    private String longSchoolName = "";

    private String externalName = "";
    /**
     *
     */
    public ICPCAccount() {
        super();
    }

    /**
     * Fill in an ICPCAccount.
     *
     * @param account The PC2 account
     * @param groupHash Maps group element ids to CMS group id
     */
    public ICPCAccount(Account account, HashMap<ElementId, String> groupHash) {
        accountNumber = account.getClientId().getClientNumber();
        clientId = account.getClientId();
        externalId = account.getExternalId();
        externalName = account.getExternalName();
        if(account.getGroupIds() != null && groupHash != null) {
            for(ElementId elementId: account.getGroupIds()) {
                groups.put(elementId,  groupHash.get(elementId));
            }
        }
        longSchoolName = account.getLongSchoolName();
        shortSchoolName = account.getShortSchoolName();
    }
    public void setExternalId(String id) {
        externalId = id;
    }
    public void setExternalName(String name) {
        externalName = name;
    }
    public void setLongSchoolName(String schoolName) {
        longSchoolName = schoolName;
    }
    public void setShortSchoolName(String schoolName) {
        shortSchoolName = schoolName;
    }
    /**
     * @return Returns the accountNumber.
     */
    public int getAccountNumber() {
        return accountNumber;
    }
    /**
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }
    /**
     * @return Returns the externalId.
     */
    public String getExternalId() {
        return externalId;
    }
    /**
     * @return Returns the externalName.
     */
    public String getExternalName() {
        return externalName;
    }
    /**
     * @return Returns the longSchoolName.
     */
    public String getLongSchoolName() {
        return longSchoolName;
    }
    /**
     * @return Returns the shortSchoolName.
     */
    public String getShortSchoolName() {
        return shortSchoolName;
    }
    /**
     * @return Returns the clientId.
     */
    public ClientId getClientId() {
        return clientId;
    }
    /**
     * @param clientId The clientId to set.
     */
    public void setClientId(ClientId clientId) {
        this.clientId = clientId;
    }
    /**
     * @return Returns the groups map.
     */
    public HashMap<ElementId, String> getGroups() {
        return groups;
    }

    /**
     * Add external group ID (CMS) to the group map.  Currently, only one group is supported for the ICPCAccount
     * since the CMS only provides one group.  It is a HashMap now for future use.  We only use one entry for now.
     *
     * @param elementId
     * @param externalGroupId
     */
    public void addGroupId(ElementId elementId, String externalGroupId) {
        if(groups == null) {
            groups = new HashMap<ElementId, String>();
        }
        groups.put(elementId, externalGroupId);
    }

    /**
     * @param externalGroupId The CMS groupId to set after the account is created
     */
    public void setExternalGroupId(String groupId) {
        this.externalGroupId = groupId;
    }
    /**
     * Returns externalGroupid specified in tab file.  This is set to null AFTER an ICPCAccount is created.
     * It is only a place holder used during account creation from a TAB file.
     */
    public String getExternalGroupId() {
        return(this.externalGroupId);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return accountNumber+" "+groups+" "+externalName+ " " +shortSchoolName;
    }

}
