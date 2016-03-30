package edu.csus.ecs.pc2.exports.ccs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.ClientType.Type;

/**
 * Team information in CLI 2016 JSON format.
 * 
 * @author pc2@ecs.csus.edu
 */

public class TeamsJSON {

    /**
     * Returns a JSON string describing the current contest standings in the format defined by the 2016 CLI JSON Scoreboard.
     * 
     * @param contest
     *            - the current contest
     * @return a JSON string giving contest standings in 2016 format
     * @throws IllegalContestState
     */
    public String createJSON(IInternalContest contest) throws IllegalContestState {

        if (contest == null) {
            return "[]";
        }

        Vector<Account> accountlist = contest.getAccounts(Type.TEAM);
        if (accountlist.size() == 0) {
            return "[]";
        }
        Account[] accounts = (Account[]) accountlist.toArray(new Account[accountlist.size()]);

        Group[] groups = contest.getGroups();
        final Map<ElementId, String> groupMap = new HashMap<ElementId, String>();
        for (Group group : groups) {
            groupMap.put(group.getElementId(), group.getDisplayName());
        }
        StringBuffer buffer = new StringBuffer();

        int rowCount = 1;
        for (Account account : accounts) {
            /* add comma between rows */
            if (rowCount != 1) {
                buffer.append(',');
            }
            buffer.append('{');
            /*
             * [{"id":42,"name":"Shanghai Tigers","nationality":"CHN","affiliation":"Shanghai Jiao Tong University","group":"Asia"},
             * {"id":11,"name":"CMU1","nationality":"USA","affiliation":"Carnegie Mellon University","group":"North America"}, ... ]
             */
            int teamNum = account.getClientId().getClientNumber();
            String groupName = "null";
            if (account.getGroupId() != null && groupMap.containsKey(account.getGroupId())) {
                groupName = groupMap.get(account.getGroupId());
            }
            buffer.append(pair("id", teamNum) + "," + pair("name", account.getTeamName()) + "," + pair("nationality", account.getCountryCode()) + ","
                    + pair("affliliation", account.getLongSchoolName()) + "," + pair("group", groupName));
            // close the entry for the current team entry
            buffer.append('}');
            rowCount++;
        }

        // return the collected standings as elements of a JSON array
        return "[" + buffer.toString() + "]";
    }

    public static String join(String delimit, List<String> list) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            buffer.append(list.get(i));
            if (i < list.size() - 1) {
                buffer.append(delimit);
            }
        }
        return buffer.toString();
    }

    /*
     * these should be a utility class
     */
    private String pair(String name, long value) {
        return "\"" + name + "\":" + value;
    }

    private String pair(String name, String value) {
        return "\"" + name + "\":\"" + value + "\"";
    }
}
