package edu.csus.ecs.pc2.exports.ccs;

import java.util.Arrays;
import java.util.Vector;

import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Create file for team.tsv.
 * 
 * Create lines that can be printed to team.tsv file.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: Userdata.java 231 2011-09-03 20:20:59Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/src/edu/csus/ecs/pc2/exports/ccs/Userdata.java $
public class Teamdata {

    private static final String TAB = "\t";

//    /**
//     * Create TeamData file for all accounts
//     * 
//     * @param contest
//     * @return
//     */
//    public String[] getAllTeamData(IInternalContest contest) {
//        Account[] accounts = contest.getAccounts();
//        return getTeamData(contest, accounts);
//    }
//
//    /**
//     * Get all teams, judges and non-admins/board accounts.
//     * 
//     * @param contest
//     * @return
//     */
//    public String[] getTeamData(IInternalContest contest) {
//
//        Vector<Account> list = new Vector<Account>();
//        Vector<Account> temp;
//
//        for (ClientType.Type type : ClientType.Type.values()) {
//            switch (type) {
//                case ADMINISTRATOR:
//                case SCOREBOARD:
//                case UNKNOWN:
//                case ALL:
//                    break;
//
//                default:
//                    temp = contest.getAccounts(type);
//                    for (Iterator<Account> iterator = temp.iterator(); iterator.hasNext();) {
//                        Account account = (Account) iterator.next();
//                        list.add(account);
//                    }
//                    break;
//            }
//        }
//
//        return getTeamData(contest, (Account[]) list.toArray(new Account[list.size()]));
//    }
    
    public String[] getTeamData(IInternalContest contest) {
        Vector<Account> accountlist = contest.getAccounts(Type.TEAM);
        Account [] accounts = (Account[]) accountlist.toArray(new Account[accountlist.size()]); 
        return getTeamData(contest, accounts);
    }

    public String[] getTeamData(IInternalContest contest, Account[] accounts) {

        String[] outLines = new String[accounts.length + 1];

        // Field Description Example Type
        // 1 Label teams fixed string (always same value)
        // 2 Version number 1 integer

        outLines[0] = "teams" + TAB + "1";

        String[] userLines = getTeamDataLines(contest, accounts);

        System.arraycopy(userLines, 0, outLines, 1, userLines.length);

        return outLines;
    }

    protected String[] getTeamDataLines(IInternalContest contest, Account[] accounts) {

        String[] outLines = new String[accounts.length];
        Arrays.sort(accounts, new AccountComparator());

        int idx = 0;
        for (Account account : accounts) {
            outLines[idx++] = teamDataLine(contest, account);
        }

        return outLines;
    }

    private String teamDataLine(IInternalContest contest, Account account) {

        ClientId clientId = account.getClientId();

        // Field Description Example Type
        // 1 Team number 22 integer
        // 2 Reservation ID 24314 integer
        // 3 Group ID 4 integer
        // 4 Team name Hoos string
        // 5 Institution name University of Virginia string
        // 6 Institution short name U Virginia string
        // 7 Country USA string ISO 3166-1 alpha-3

        int groupId = 0;
        if (account.getGroupId() != null) {
            groupId = contest.getGroup(account.getGroupId()).getGroupId();
        }

        return clientId.getClientNumber() + TAB + //
                account.getExternalId() + TAB + //
                groupId + TAB + //
                account.getDisplayName() + TAB + //
                account.getLongSchoolName() + TAB + //
                account.getShortSchoolName() + TAB + //
                account.getCountryCode();
    }
}
