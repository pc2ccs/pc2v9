// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.scoring;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.list.AccountList;
import edu.csus.ecs.pc2.core.list.AccountNameCaseComparator;
import edu.csus.ecs.pc2.core.model.Account;

/**
 * Sorts StandingsRecord according to the ACM-ICPC World Finals Rules (as of 2025) for point scoring contests
 *
 * @author John Buck
 * @version $Id$
 */

// $HeadURL$
public class FinalsStandingsPointScoringRecordComparator implements Serializable, Comparator<StandingsRecord> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private AccountNameCaseComparator accountNameCaseComparator = new AccountNameCaseComparator();

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
        double aScore, bScore;
        long aTime, bTime;
        int aHash, bHash;
        String aName, bName;

        StandingsRecord teamA = o1;
        StandingsRecord teamB = o2;
        aScore = teamA.getScore();
        aTime = teamA.getLastSolved();
        Account accountA = cachedAccountList.getAccount(teamA.getClientId());
        aName = accountA.getDisplayName();
        aHash = teamA.getClientId().hashCode();
        bScore = teamB.getScore();
        bTime = teamB.getLastSolved();
        Account accountB = cachedAccountList.getAccount(teamB.getClientId());
        bName = accountB.getDisplayName();
        bHash = teamB.getClientId().hashCode();

        //
        // Primary Sort = score (high to low)
        // Secondary Sort = time (low to high)
        // Third Sort = teamName (low to high)
        // Fourth Sort = clientId (low to high)

        int nameComparison = accountNameCaseComparator.compare(aName, bName);
        if ((bScore == aScore) && (bTime == aTime) && (nameComparison == 0)
                && (bHash == aHash)) {
            status = 0; // elements equal, this shouldn't happen, Tammy...
        } else {
            if ((bScore > aScore)
                    || ((bScore == aScore) && (bTime < aTime))
                    || ((bScore == aScore) && (bTime == aTime) && (nameComparison > 0))
                    || ((bScore == aScore) && (bTime == aTime)
                            && (nameComparison == 0) && (bHash < aHash))) {
                status = 1; // a considered greater then b
            } else {
                status = -1; // a considered less then b
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
