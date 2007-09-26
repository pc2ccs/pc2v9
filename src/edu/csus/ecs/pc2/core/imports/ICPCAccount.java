/**
 * 
 */
package edu.csus.ecs.pc2.core.imports;

/**
 * @author PC2
 *
 */
public class ICPCAccount {

    private int accountNumber;
    /**
     * An external identifier, ex: an ICPC id
     */
    private String externalId;
    
    /**
     * Group id
     * TODO consider replacing with ElementId
     */
    private String groupId;
    
    private String shortSchoolName;

    private String longSchoolName;

    private String externalName;
    /**
     * 
     */
    public ICPCAccount() {
        super();
    }
    public void setExternalId(String id) {
        externalId = id;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
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
    public String getGroupId() {
        return groupId;
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

}
