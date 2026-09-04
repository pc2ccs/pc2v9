// Copyright (C) 1989-2026 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.standings;

import java.util.ArrayList;
import java.util.List;

import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestInformation.ShowAllTeamsOnScoreboard;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * This is a utility class supporting division-wide scoreboard group selection.
 *
 * <p>Scoreboard group ids match {@code DefaultScoringAlgorithm.dumpGroupList}:
 * only groups with {@link Group#isDisplayOnScoreboard()} true, in
 * {@link IInternalContest#getGroups()} order, numbered from 1.</p>
 */
public final class ScoreboardGroupUtilities {

    private ScoreboardGroupUtilities() {
        // utility class
    }

    /**
     * Returns an indication of whether the WTI scoreboard should offer an "All teams" option.
     *
     * @param contest contest model
     * @return true if "All teams" should be shown
     */
    public static boolean shouldShowAllTeams(IInternalContest contest) {
        if (contest == null) {
            return true;    //not clear whether this should instead throw an exception?
        }

        ContestInformation info = contest.getContestInformation();
        ShowAllTeamsOnScoreboard policy = ShowAllTeamsOnScoreboard.AUTO;
        if (info != null) {
            policy = info.getShowAllTeamsOnScoreboard();
        }

        if (policy == ShowAllTeamsOnScoreboard.TRUE) {
            return true;
        }
        if (policy == ShowAllTeamsOnScoreboard.FALSE) {
            return false;
        }

        // AUTO: hide All teams when any scoreboard-visible division-wide cohort exists
        return !hasDivisionWideScoreboardGroup(contest);
    }

    /**
     * Returns an indication of whether there is at least one scoreboard-visible group which 
     * is marked as "division-wide" -- that is, at least one group that represents a scoreboard
     * for ALL teams in a division.
     * 
     * @return true if any display-on-scoreboard group is marked division-wide
     */
    public static boolean hasDivisionWideScoreboardGroup(IInternalContest contest) {
        for (Group group : getScoreboardVisibleGroups(contest)) {
            if (group.isDivisionWideScoreboard()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a list of groups that are marked as being visible on the scoreboad
     * (that is, groups which represent a selectable option on the WTI scoreboard
     * group-selection dropdown).
     *
     * @param contest contest model
     * @return list of groups with {@link Group#isDisplayOnScoreboard()} true
     */
    public static List<Group> getScoreboardVisibleGroups(IInternalContest contest) {
        List<Group> visible = new ArrayList<Group>();
        if (contest == null) {
            return visible;
        }
        Group[] groups = contest.getGroups();
        if (groups == null) {
            return visible;
        }
        for (Group group : groups) {
            if (group.isDisplayOnScoreboard()) {
                visible.add(group);
            }
        }
        return visible;
    }

    /**
     * Returns a List of scoreboard-visible groups which are marked as "Division-wide scoreboard
     * groups" -- that is, groups that appear in the scoreboard group-selection dropdown and 
     * represent the standings for a whole division.
     *
     * @param contest contest model
     * @return display-on-scoreboard groups with {@link Group#isDivisionWideScoreboard()} true
     */
    public static List<Group> getDivisionWideScoreboardGroups(IInternalContest contest) {
        List<Group> divisionWide = new ArrayList<Group>();
        for (Group group : getScoreboardVisibleGroups(contest)) {
            if (group.isDivisionWideScoreboard()) {
                divisionWide.add(group);
            }
        }
        return divisionWide;
    }

    /**
     * Returns the Group which is the default scoreboard group-selection dropdown group
     * for a team when "All teams" is hidden (in other words, returns an indication of 
     * which group a team should see as the default dropdown selection when "All teams"
     * is not visible because there are multiple divisions in the contest).
     * Prefers a division-wide scoreboard group that the account belongs to (contest order).
     * If the account's primary group is among those matches, that primary is preferred.
     *
     * @param contest contest model
     * @param account team (or other) account
     * @return matching division-wide group, or null if none
     */
    public static Group getDefaultDivisionWideGroup(IInternalContest contest, Account account) {
        if (contest == null || account == null) {
            return null;
        }

        List<Group> matches = new ArrayList<Group>();
        for (Group group : getDivisionWideScoreboardGroups(contest)) {
            if (account.isGroupMember(group.getElementId())) {
                matches.add(group);
            }
        }

        if (matches.isEmpty()) {
            return null;
        }

        if (account.getPrimaryGroupId() != null) {
            for (Group group : matches) {
                if (account.getPrimaryGroupId().equals(group.getElementId())) {
                    return group;
                }
            }
        }

        return matches.get(0);
    }

    /**
     * Returns the sequential scoreboard group id for the specified {@link Group}, 
     * matching dumpGroupList numbering.  Note that the returned id is the sequential number
     * of the group as determined when groups are loaded -- NOT the "External Id" of the group.
     *
     * @param contest contest model
     * @param group group whose Id is to be returned
     * @return id >= 1, or -1 if the group is null, not in the contest, or not display-on-scoreboard
     */
    public static int getScoreboardGroupId(IInternalContest contest, Group group) {
        if (contest == null || group == null || !group.isDisplayOnScoreboard()) {
            return -1;
        }

        int id = 0;
        for (Group candidate : getScoreboardVisibleGroups(contest)) {
            id++;
            if (candidate.getElementId().equals(group.getElementId())) {
                return id;
            }
        }
        return -1;
    }

    /**
     * Returns the {@link Group} corresponding to the specified integer scoreboardGroupId.
     * Note that scoreboardGroupId is expected to be the sequential Group Id assigned
     * when groups are first loaded, NOT a Group "External Id".
     *
     * @param contest contest model
     * @param scoreboardGroupId sequential id from dumpGroupList / WTI {@code ?groupId=}
     * @return matching group, or null if id is invalid or unknown
     */
    public static Group getGroupForScoreboardId(IInternalContest contest, int scoreboardGroupId) {
        if (contest == null || scoreboardGroupId < 1) {
            return null;
        }

        int id = 0;
        for (Group group : getScoreboardVisibleGroups(contest)) {
            id++;
            if (id == scoreboardGroupId) {
                return group;
            }
        }
        return null;
    }

    /**
     * Returns the {@link Group} corresponding to the specified string scoreboardGroupId.
     * Note that scoreboardGroupId is expected to be a string representation of the 
     * sequential Group Id assigned when groups are first loaded, NOT a Group "External Id".
     *
     *
     * @param contest contest model
     * @param scoreboardGroupId sequential id string
     * @return matching group, or null if id string is blank, non-numeric, or unknown
     */
    public static Group getGroupForScoreboardId(IInternalContest contest, String scoreboardGroupId) {
        if (scoreboardGroupId == null) {
            return null;
        }
        String trimmed = scoreboardGroupId.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        try {
            return getGroupForScoreboardId(contest, Integer.parseInt(trimmed));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
