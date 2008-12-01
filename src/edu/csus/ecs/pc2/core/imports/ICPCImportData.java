/**
 * 
 */
package edu.csus.ecs.pc2.core.imports;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ElementId;
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
    public ICPCImportData(Vector<Account> inAccounts, Group[] inGroups, String inContestTitle) {
        HashMap<ElementId, String> groupHash = new HashMap<ElementId, String>();
        groups = inGroups;
        for (int i = 0; i < inGroups.length; i++) {
            groupHash.put(inGroups[i].getElementId(), Integer.toString(inGroups[i].getGroupId()));
        }
        contestTitle = inContestTitle;
        Enumeration<Account> accountsEnum = inAccounts.elements();
        accounts = new ICPCAccount[inAccounts.size()];
        int accountCount = 0;
        while (accountsEnum.hasMoreElements()) {
            Account account = (Account) accountsEnum.nextElement();
            ICPCAccount icpcAccount = new ICPCAccount(account, groupHash.get(account.getGroupId()));
            accounts[accountCount] = icpcAccount;
            accountCount++;
        }
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
