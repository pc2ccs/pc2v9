/**
 * 
 */
package edu.csus.ecs.pc2.core.imports;

import edu.csus.ecs.pc2.core.model.Group;

/**
 * @author PC2
 *
 */
public class ICPCImportData {

    private String contestTitle;
    private Group[] groups;
    private ICPCAccount[] accounts;
    /**
     * 
     */
    public ICPCImportData() {
        super();
    }
    /**
     * @return Returns the accounts.
     */
    public ICPCAccount[] getAccounts() {
        return accounts;
    }
    /**
     * @param accounts The accounts to set.
     */
    public void setAccounts(ICPCAccount[] accounts) {
        this.accounts = accounts;
    }
    /**
     * @return Returns the contestTitle.
     */
    public String getContestTitle() {
        return contestTitle;
    }
    /**
     * @param contestTitle The contestTitle to set.
     */
    public void setContestTitle(String contestTitle) {
        this.contestTitle = contestTitle;
    }
    /**
     * @return Returns the groups.
     */
    public Group[] getGroups() {
        return groups;
    }
    /**
     * @param groups The groups to set.
     */
    public void setGroups(Group[] groups) {
        this.groups = groups;
    }

}
