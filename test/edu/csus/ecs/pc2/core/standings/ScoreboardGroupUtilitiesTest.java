// Copyright (C) 1989-2026 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.standings;

import java.util.List;
import java.util.Vector;

import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestInformation.ShowAllTeamsOnScoreboard;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit tests for {@link ScoreboardGroupUtilities}.
 */
public class ScoreboardGroupUtilitiesTest extends AbstractTestCase {

    public void testShouldShowAllTeamsAutoWithNoDivisionWide() {
        IInternalContest contest = createPacNWStyleContest(false);

        assertEquals(ShowAllTeamsOnScoreboard.AUTO, contest.getContestInformation().getShowAllTeamsOnScoreboard());
        assertTrue(ScoreboardGroupUtilities.shouldShowAllTeams(contest));
    }

    public void testShouldShowAllTeamsAutoWithDivisionWide() {
        IInternalContest contest = createPacNWStyleContest(true);

        assertFalse(ScoreboardGroupUtilities.shouldShowAllTeams(contest));
    }

    public void testShouldShowAllTeamsTrueOverride() {
        IInternalContest contest = createPacNWStyleContest(true);
        ContestInformation info = contest.getContestInformation();
        info.setShowAllTeamsOnScoreboard(ShowAllTeamsOnScoreboard.TRUE);
        contest.updateContestInformation(info);

        assertTrue(ScoreboardGroupUtilities.shouldShowAllTeams(contest));
    }

    public void testShouldShowAllTeamsFalseOverride() {
        IInternalContest contest = createPacNWStyleContest(false);
        ContestInformation info = contest.getContestInformation();
        info.setShowAllTeamsOnScoreboard(ShowAllTeamsOnScoreboard.FALSE);
        contest.updateContestInformation(info);

        assertFalse(ScoreboardGroupUtilities.shouldShowAllTeams(contest));
    }

    public void testScoreboardVisibleGroupsSkipsHidden() {
        IInternalContest contest = createPacNWStyleContest(true);

        List<Group> visible = ScoreboardGroupUtilities.getScoreboardVisibleGroups(contest);
        assertEquals(3, visible.size());
        assertEquals("D1", visible.get(0).getDisplayName());
        assertEquals("D2", visible.get(1).getDisplayName());
        assertEquals("California - D1", visible.get(2).getDisplayName());
    }

    public void testGetScoreboardGroupListOrdinalNumbering() {
        IInternalContest contest = createPacNWStyleContest(true);

        Group d1 = findGroup(contest, "D1");
        Group d2 = findGroup(contest, "D2");
        Group california = findGroup(contest, "California");
        Group californiaD1 = findGroup(contest, "California - D1");

        // Ordinals are among display-on-scoreboard groups only (California is skipped)
        assertEquals(1, ScoreboardGroupUtilities.getScoreboardGroupListOrdinal(contest, d1));
        assertEquals(2, ScoreboardGroupUtilities.getScoreboardGroupListOrdinal(contest, d2));
        assertEquals(-1, ScoreboardGroupUtilities.getScoreboardGroupListOrdinal(contest, california));
        assertEquals(3, ScoreboardGroupUtilities.getScoreboardGroupListOrdinal(contest, californiaD1));
    }

    public void testGetGroupForScoreboardGroupListOrdinal() {
        IInternalContest contest = createPacNWStyleContest(true);

        assertEquals("D1", ScoreboardGroupUtilities.getGroupForScoreboardGroupListOrdinal(contest, 1).getDisplayName());
        assertEquals("D2", ScoreboardGroupUtilities.getGroupForScoreboardGroupListOrdinal(contest, "2").getDisplayName());
        assertEquals("California - D1",
                ScoreboardGroupUtilities.getGroupForScoreboardGroupListOrdinal(contest, 3).getDisplayName());
        assertNull(ScoreboardGroupUtilities.getGroupForScoreboardGroupListOrdinal(contest, 0));
        assertNull(ScoreboardGroupUtilities.getGroupForScoreboardGroupListOrdinal(contest, 99));
        assertNull(ScoreboardGroupUtilities.getGroupForScoreboardGroupListOrdinal(contest, "abc"));
        assertNull(ScoreboardGroupUtilities.getGroupForScoreboardGroupListOrdinal(contest, ""));
    }

    public void testDefaultDivisionWideGroupPrefersMembership() {
        IInternalContest contest = createPacNWStyleContest(true);
        Account team = getFirstTeam(contest);

        Group d1 = findGroup(contest, "D1");
        Group californiaD1 = findGroup(contest, "California - D1");
        team.addGroupId(californiaD1.getElementId(), true);
        team.addGroupId(d1.getElementId(), false);

        Group defaultGroup = ScoreboardGroupUtilities.getDefaultDivisionWideGroup(contest, team);
        assertNotNull(defaultGroup);
        assertEquals("D1", defaultGroup.getDisplayName());
        assertEquals(1, ScoreboardGroupUtilities.getScoreboardGroupListOrdinal(contest, defaultGroup));
    }

    public void testDefaultDivisionWideGroupPrefersPrimaryWhenDivisionWide() {
        IInternalContest contest = createPacNWStyleContest(true);
        Account team = getFirstTeam(contest);

        Group d1 = findGroup(contest, "D1");
        Group d2 = findGroup(contest, "D2");
        team.addGroupId(d1.getElementId(), false);
        team.addGroupId(d2.getElementId(), true);

        Group defaultGroup = ScoreboardGroupUtilities.getDefaultDivisionWideGroup(contest, team);
        assertEquals("D2", defaultGroup.getDisplayName());
    }

    public void testDefaultDivisionWideGroupNullWhenNone() {
        IInternalContest contest = createPacNWStyleContest(false);
        Account team = getFirstTeam(contest);
        team.addGroupId(findGroup(contest, "California - D1").getElementId(), true);

        assertNull(ScoreboardGroupUtilities.getDefaultDivisionWideGroup(contest, team));
    }

    /**
     * PacNW-like group set: D1/D2 (optional division-wide), hidden California site group,
     * and California - D1 site-by-division group on the scoreboard.
     */
    private IInternalContest createPacNWStyleContest(boolean markDivisionWide) {
        IInternalContest contest = new SampleContest().createContest(1, 1, 2, 0, true);

        Group d1 = new Group("D1");
        d1.setGroupId(1200);
        d1.setDisplayOnScoreboard(true);
        d1.setDivisionWideScoreboard(markDivisionWide);
        contest.addGroup(d1);

        Group d2 = new Group("D2");
        d2.setGroupId(1201);
        d2.setDisplayOnScoreboard(true);
        d2.setDivisionWideScoreboard(markDivisionWide);
        contest.addGroup(d2);

        Group california = new Group("California");
        california.setGroupId(1202);
        california.setDisplayOnScoreboard(false);
        contest.addGroup(california);

        Group californiaD1 = new Group("California - D1");
        californiaD1.setGroupId(400);
        californiaD1.setDisplayOnScoreboard(true);
        contest.addGroup(californiaD1);

        return contest;
    }

    private Group findGroup(IInternalContest contest, String name) {
        for (Group group : contest.getGroups()) {
            if (name.equals(group.getDisplayName())) {
                return group;
            }
        }
        fail("Group not found: " + name);
        return null;
    }

    private Account getFirstTeam(IInternalContest contest) {
        Vector<Account> teams = contest.getAccounts(Type.TEAM);
        assertTrue(teams.size() > 0);
        return teams.firstElement();
    }
}
