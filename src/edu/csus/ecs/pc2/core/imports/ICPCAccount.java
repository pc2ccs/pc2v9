/**
 * 
 */
package edu.csus.ecs.pc2.core.imports;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;

/**
 * @author PC2
 *
 */
public class ICPCAccount {

    private int accountNumber = 0;
    /**
     * An external identifier, ex: an ICPC id
     */
    private String externalId = "";
    
    /**
     * Group id
     */
    private ElementId groupId;
    
    private ClientId clientId;
    
    private String groupExternalId = "";
    
    private String shortSchoolName = "";

    private String longSchoolName = "";

    private String externalName = "";
    /**
     * 
     */
    public ICPCAccount() {
        super();
    }
    public void setExternalId(String id) {
        externalId = id;
    }
    public void setGroupExternalId(String groupExternalId) {
        this.groupExternalId = groupExternalId;
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
     * @return Returns the groupId.
     */
    public String getGroupExternalId() {
        return groupExternalId;
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
     * @return Returns the groupId.
     */
    public ElementId getGroupId() {
        return groupId;
    }
    /**
     * @param groupId The groupId to set.
     */
    public void setGroupId(ElementId groupId) {
        this.groupId = groupId;
    }

}
