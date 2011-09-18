package edu.csus.ecs.pc2.exports.ccs;

import java.util.Arrays;

import edu.csus.ecs.pc2.core.list.GroupComparator;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Create file for groups.tsv.
 * 
 * Create lines that can be printed to groups.tsv file.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class Groupdata {

    private static final char TAB = '\t';

    public String[] getGroupData(IInternalContest contest) {
        return getGroupData(contest, contest.getGroups());
    }

    public String[] getGroupData(IInternalContest contest, Group[] groups) {

        String[] outLines = new String[groups.length + 1];

        // Field Description Example Type
        // 1 Label groups fixed string (always same value)
        // 2 Version number 1 integer

        outLines[0] = "groups" + TAB + "1";

        String[] userLines = getGroupDataLines(contest, groups);

        System.arraycopy(userLines, 0, outLines, 1, userLines.length);

        return outLines;
    }

    protected String[] getGroupDataLines(IInternalContest contest, Group[] groups) {

        String[] outLines = new String[groups.length];
        Arrays.sort(groups, new GroupComparator());

        int idx = 0;
        for (Group group : groups) {
            outLines[idx++] = groupDataLine(contest, group);
        }

        return outLines;
    }

    private String groupDataLine(IInternalContest contest, Group group) {

        // Field Description Example Type
        // 1 Group ID 902 integer
        // 2 Group name North America string

        return  Integer.toString(group.getGroupId()) + TAB + //
                group.getDisplayName();
    }
}
