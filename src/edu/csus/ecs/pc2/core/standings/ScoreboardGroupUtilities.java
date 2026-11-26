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
 * This is a utility class supporting scoreboard group selections, including methods
 * for handling groups in a contest with "divisions".
 *
 * <p>Note that there are multiple notions associated with the term "group Id":
 * <ul>
 *   <li><b>Admin Groups tab "Id"</b> -- UI row number only; not used by these methods.</li>
 *   <li><b>External Id</b> ({@link Group#getGroupId()}) -- CMS / {@code groups.tsv} id;
 *       used by {@code system.pc2.yaml} {@code division-groups} {@code id:} matching, by WTI scoreboard
 *       filtering ({@code ?groupExternalId=}), and emitted as standings {@code group/@externalId}.</li>
 *   <li><b>Scoreboard group-list ordinal</b> -- 1-based position among groups with
 *       {@link Group#isDisplayOnScoreboard()} {@code true}, in
 *       {@link IInternalContest#getGroups()} order, as assigned by
 *       {@code DefaultScoringAlgorithm.dumpGroupList}. Appears as standings
 *       {@code group/@id} for classic XSL/HTML scoreboards. <em>Not</em> used by WTI
 *       for filtering (WTI uses External Id).</li>
 * </ul>
 * The scoreboard group-list ordinal is recomputed when standings are built; groups with
 * {@code displayOnScoreboard == false} are skipped and do not receive an ordinal.
 * External Id is stable across such renumbering.
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
     * @return matching division-wide {@link Group}, or null if none
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
     * Returns the scoreboard-visible {@link Group} whose {@link Group#getGroupId() External Id}
     * equals {@code externalId}.
     *
     * <p>This is the lookup used by WTI ({@code ?groupExternalId=}). Groups that are not
     * {@link Group#isDisplayOnScoreboard() display-on-scoreboard} are not matched.</p>
     *
     * @param contest contest model
     * @param externalId CMS / groups.tsv External Id
     * @return matching scoreboard-visible group, or null if none
     */
    public static Group getGroupForExternalId(IInternalContest contest, int externalId) {
        if (contest == null) {
            return null;
        }
        for (Group group : getScoreboardVisibleGroups(contest)) {
            if (group.getGroupId() == externalId) {
                return group;
            }
        }
        return null;
    }

    /**
     * Resolve a string form of External Id to a scoreboard-visible {@link Group}.
     *
     * @param contest contest model
     * @param externalId string form of {@link Group#getGroupId()} (e.g. WTI {@code groupExternalId} query value)
     * @return matching group, or null if blank, non-numeric, or unknown / not scoreboard-visible
     */
    public static Group getGroupForExternalId(IInternalContest contest, String externalId) {
        if (externalId == null) {
            return null;
        }
        String trimmed = externalId.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        try {
            return getGroupForExternalId(contest, Integer.parseInt(trimmed));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Returns the scoreboard group-list ordinal for a {@link Group}; that is, the 1-based 
     * ordinal within the group list defined by {@code DefaultScoringAlgorithm.dumpGroupList}.
     *
     * <p>Used by classic XSL/HTML scoreboards via standings {@code group/@id}.
     * This is <em>not</em> {@link Group#getGroupId()} (External Id), <em>not</em> the Admin
     * Groups tab "Id" column, and <em>not</em> the WTI scoreboard filter key.</p>
     *
     * @param contest contest model
     * @param group group whose scoreboard group-list ordinal is requested
     * @return ordinal >= 1, or -1 if the group is null, not scoreboard-visible, or not in the contest
     */
    public static int getScoreboardGroupListOrdinal(IInternalContest contest, Group group) {
        if (contest == null || group == null || !group.isDisplayOnScoreboard()) {
            return -1;
        }

        int ordinal = 0;
        for (Group candidate : getScoreboardVisibleGroups(contest)) {
            ordinal++;
            if (candidate.getElementId().equals(group.getElementId())) {
                return ordinal;
            }
        }
        return -1;
    }

    /**
     * Returns a {@link Group} corresponding to a scoreboard group-list ordinal.
     *
     * <p>{@code scoreboardGroupListOrdinal} is the dumpGroupList / standings
     * {@code group/@id} value used by classic XSL/HTML -- <em>not</em> External Id,
     * <em>not</em> the Admin Groups "Id", and <em>not</em> the WTI {@code groupExternalId} filter.</p>
     *
     * @param contest contest model
     * @param scoreboardGroupListOrdinal 1-based ordinal among scoreboard-visible groups
     * @return matching group, or null if ordinal is invalid or unknown
     */
    public static Group getGroupForScoreboardGroupListOrdinal(IInternalContest contest, int scoreboardGroupListOrdinal) {
        if (contest == null || scoreboardGroupListOrdinal < 1) {
            return null;
        }

        int ordinal = 0;
        for (Group group : getScoreboardVisibleGroups(contest)) {
            ordinal++;
            if (ordinal == scoreboardGroupListOrdinal) {
                return group;
            }
        }
        return null;
    }

    /**
     * Resolve a scoreboard group-list ordinal string to a model {@link Group}.
     *
     * <p>Same meaning as {@link #getGroupForScoreboardGroupListOrdinal(IInternalContest, int)}:
     * dumpGroupList ordinal for XSL/HTML, not External Id / WTI filter.</p>
     *
     * @param contest contest model
     * @param scoreboardGroupListOrdinal string form of the 1-based ordinal
     * @return matching group, or null if blank, non-numeric, or unknown
     */
    public static Group getGroupForScoreboardGroupListOrdinal(IInternalContest contest, String scoreboardGroupListOrdinal) {
        if (scoreboardGroupListOrdinal == null) {
            return null;
        }
        String trimmed = scoreboardGroupListOrdinal.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        try {
            return getGroupForScoreboardGroupListOrdinal(contest, Integer.parseInt(trimmed));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
