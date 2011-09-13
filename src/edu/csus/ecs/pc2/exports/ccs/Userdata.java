package edu.csus.ecs.pc2.exports.ccs;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: Userdata.java 231 2011-09-03 20:20:59Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/src/edu/csus/ecs/pc2/exports/ccs/Userdata.java $
public class Userdata {

    private static final char TAB = '\t';

    /**
     * Create UserData file for all accounts
     * 
     * @param contest
     * @return
     */
    public String[] getAllUserData(IInternalContest contest) {
        Account[] accounts = contest.getAccounts();
        return getUserData(accounts);
    }

    /**
     * Get all teams, judges and non-admins/board accounts.
     * 
     * @param contest
     * @return
     */
    public String[] getUserData(IInternalContest contest) {

        Vector<Account> list = new Vector<Account>();
        Vector<Account> temp;

        for (ClientType.Type type : ClientType.Type.values()) {
            switch (type) {
                case ADMINISTRATOR:
                case SCOREBOARD:
                case UNKNOWN:
                case ALL:
                    break;

                default:
                    temp = contest.getAccounts(type);
                    for (Iterator<Account> iterator = temp.iterator(); iterator.hasNext();) {
                        Account account = (Account) iterator.next();
                        list.add(account);
                    }
                    break;
            }
        }

        return getUserData((Account[]) list.toArray(new Account[list.size()]));
    }

    public String[] getUserData(Account[] accounts) {

        String[] outLines = new String[accounts.length + 1];

        // Field Description Example Type
        // 1 Label userdata fixed string (always same value)
        // 2 Version number 1 integer

        outLines[0] = "userdata" + TAB + "1";

        String[] userLines = getUserDataLines(accounts);

        // Arrays.copy(original, newLength)

        System.arraycopy(userLines, 0, outLines, 1, userLines.length);

        return outLines;
    }

    public String[] getUserDataLines(Account[] accounts) {

        String[] outLines = new String[accounts.length];
        Arrays.sort(accounts, new AccountComparator());

        int idx = 0;
        for (Account account : accounts) {
            outLines[idx++] = userDataLine(account);
        }

        return outLines;
    }

    private String userDataLine(Account account) {

        ClientId clientId = account.getClientId();

        // Field Description Example Type
        // 1 Account Type team string
        // 2 Account Number 42 integer
        // 3 Full Name University of Virginia string
        // 4 Username/login team-001 string
        // 5 Password B!5MWJiy string
        // Account Types include: team, judge, admin, analyst, etc.

        return clientId.getClientType().toString().toLowerCase() + TAB + //
                clientId.getClientNumber() + TAB + //
                account.getDisplayName() + TAB + //
                clientId.getName() + TAB + //
                account.getPassword();
    }
}
