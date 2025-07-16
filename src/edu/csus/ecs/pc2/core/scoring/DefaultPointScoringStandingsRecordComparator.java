// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.scoring;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.list.AccountList;
import edu.csus.ecs.pc2.core.list.AccountNameComparator;
import edu.csus.ecs.pc2.core.model.Account;

/**
 * Sorts StandingsRecord according to the Legacy Problem Package Format point scoring specification (such as it is)
 *
 * @author John Buck
 */

// $HeadURL$
public class DefaultPointScoringStandingsRecordComparator implements Serializable, Comparator<StandingsRecord> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private AccountNameComparator accountNameComparator = new AccountNameComparator();

    private AccountList cachedAccountList;

    /**
     * Compares its two arguments for order. Returns a negative integer, zero, or a positive integer as the first argument is less
     * than, equal to, or greater than the second.
     * <p>
     *
     * The implementor must ensure that <tt>sgn(compare(x, y)) ==
     * -sgn(compare(y, x))</tt> for all <tt>x</tt> and <tt>y</tt>.
     * (This implies that <tt>compare(x, y)</tt> must throw an exception if and only if <tt>compare(y, x)</tt> throws an
     * exception.)
     * <p>
     *
     * The implementor must also ensure that the relation is transitive:
     * <tt>((compare(x, y)&gt;0) &amp;&amp; (compare(y, z)&gt;0))</tt> implies <tt>compare(x, z)&gt;0</tt>.
     * <p>
     *
     * Finally, the implementer must ensure that <tt>compare(x, y)==0</tt> implies that
     * <tt>sgn(compare(x, z))==sgn(compare(y, z))</tt> for all <tt>z</tt>.
     * <p>
     *
     * It is generally the case, but <i>not</i> strictly required that <tt>(compare(x, y)==0) == (x.equals(y))</tt>. Generally
     * speaking, any comparator that violates this condition should clearly indicate this fact. The recommended language is "Note:
     * this comparator imposes orderings that are inconsistent with equals."
     *
     * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the
     *         second.
     * @throws ClassCastException
     *             if the arguments' types prevent them from being compared by this Comparator.
     */
    @Override
    public int compare(StandingsRecord o1, StandingsRecord o2) {
        int status = 0;
        long aLastSolvedTime, bLastSolvedTime;
        double aScore, bScore;
        int aClientHash, bClientHash;
        String nameA, nameB;
        int nameComparison;

        StandingsRecord teamA = o1;
        StandingsRecord teamB = o2;
        aScore = teamA.getScore();
        aLastSolvedTime = teamA.getLastSolved();
        Account accountA = cachedAccountList.getAccount(teamA.getClientId());
        nameA = accountA.getDisplayName();
        aClientHash = teamA.getClientId().hashCode();
        bScore = teamB.getScore();
        bLastSolvedTime = teamB.getLastSolved();
        Account accountB = cachedAccountList.getAccount(teamB.getClientId());
        nameB = accountB.getDisplayName();
        bClientHash = teamB.getClientId().hashCode();
        nameComparison = accountNameComparator.compare(nameA.toLowerCase(), nameB.toLowerCase());

        //
        // Primary Sort = score (high to low)
        // Secondary Sort = earliest submittal of last submission (low to high)
        // Third Sort = teamName (low to high)
        // Fourth Sort = clientId (low to high)

        if ((bScore == aScore) && (bLastSolvedTime == aLastSolvedTime) && (nameComparison == 0)
                && (bClientHash == aClientHash)) {
            status = 0; // elements equal, this shouldn't happen, Tammy...
        } else {
            // The sorting algorithm sorts things from "low to high" by default (ascending).
            // The comparators should return a value as to whether the first thing (A) should be
            // considered(!) less-than, equal-to or greater-than the 2nd thing (B). That is,
            // does A appear AFTER B in the sorted result.
            // For Point Scoring, the item with the biggest score should be the first thing
            // in the result. When comparing 2 things (ascore and bscore), we have to determine
            // if we want ascore to come after bscore. (IE if ascore is smaller than bscore,
            // then A comes after B, and should be considered that A is bigger than B
            // in terms of how the sorting is done, so we return 1, indicating that A should
            // come after B in the sorted list.
            if ((bScore > aScore)
                    || ((bScore == aScore) && (bLastSolvedTime < aLastSolvedTime))
                    || ((bScore == aScore) && (bLastSolvedTime == aLastSolvedTime) && (nameComparison > 0))
                    || ((bScore == aScore) && (bLastSolvedTime == aLastSolvedTime)
                            && (nameComparison == 0) && (bClientHash < aClientHash))) {
                status = 1; // a to be considered greater than b
            } else {
                status = -1; // a to be considered less than b
            }
        }
        return status;
    }

    /**
     * @param accountList
     *            The cachedAccountList to set.
     */
    public void setCachedAccountList(AccountList accountList) {
        this.cachedAccountList = accountList;
    }
}
