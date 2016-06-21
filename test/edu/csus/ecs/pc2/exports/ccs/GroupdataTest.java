package edu.csus.ecs.pc2.exports.ccs;

import java.io.PrintStream;
import java.util.Arrays;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.GroupComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;

/**
 * Groupsdata tests.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class GroupdataTest extends TestCase {

    private SampleContest sample = new SampleContest();

    public void testThreeGroups() throws Exception {

        IInternalContest contest = sample.createContest(1, 1, 15, 5, true);

        setAccountNamesAndGroups(contest);

        Groupdata groupdata = new Groupdata();

        String[] lines = groupdata.getGroupData(contest);

//         Utilities.dumpStringArray(System.out, "testThreeGroups ", lines, true);

        // dumpGroups(System.out, contest);

        assertEquals("Expected number of lines ", 4, lines.length);
    }

    public void testNoGroups() throws Exception {

        IInternalContest contest = sample.createContest(1, 1, 15, 5, true);

        Groupdata groupdata = new Groupdata();

        String[] lines = groupdata.getGroupData(contest);

        // Utilities.dumpStringArray(System.out, "testNoGroups ", lines, true);

        // dumpGroups(System.out, contest);

        assertEquals("Expected number of lines ", 1, lines.length);
    }

    @SuppressWarnings("unused")
    private void dumpGroups(PrintStream out, IInternalContest contest) {

        Group[] groups = contest.getGroups();
        out.println("There are " + groups.length + " groups");
        int counter = 1;
        for (Group group : groups) {
            out.println(counter + ":" + " name=" + group.getDisplayName() + " id=" + group.getGroupId());
            counter++;
        }
    }

    void setAccountNamesAndGroups(IInternalContest contest) {

        if (contest.getGroups().length == 0) {
            Group group1 = new Group("Mississippi");
            group1.setGroupId(1024);
            contest.addGroup(group1);

            Group group2 = new Group("Arkansas");
            group2.setGroupId(2048);
            contest.addGroup(group2);

            Group group3 = new Group("Virginia");
            group2.setGroupId(2048);
            contest.addGroup(group3);
        }

        Account[] teams = SampleContest.getTeamAccounts(contest);

        Group[] groups = contest.getGroups();
        Arrays.sort(groups, new GroupComparator());

        sample.assignTeamGroup(contest, groups[0], 0, teams.length / 2);
        sample.assignTeamGroup(contest, groups[1], teams.length / 2, teams.length);
        sample.assignTeamGroup(contest, groups[2], teams.length - 4, teams.length);

        int bi = 0;
        int gi = 0;

        Account[] accounts = contest.getAccounts();
        Arrays.sort(accounts, new AccountComparator());

        for (Account account : accounts) {
            switch (account.getClientId().getClientType()) {
                case TEAM:
                    account.setDisplayName("Team " + SampleContest.BOYS_NAMES[bi++]);
                    break;
                case JUDGE:
                    account.setDisplayName("Judge " + SampleContest.GIRL_NAMES[gi++]);
                    break;
                default:
                    break;
            }
        }
    }
}
