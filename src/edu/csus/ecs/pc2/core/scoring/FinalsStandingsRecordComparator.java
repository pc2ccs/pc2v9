package edu.csus.ecs.pc2.core.scoring;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.list.AccountList;
import edu.csus.ecs.pc2.core.list.AccountNameComparator;
import edu.csus.ecs.pc2.core.model.Account;

/**
 * Sorts StandingsRecord according to the ACM-ICPC World Finals Rules (as of 2006).
 *
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class FinalsStandingsRecordComparator implements Serializable, Comparator<StandingsRecord> {

    /**
     *
     */
    private static final long serialVersionUID = 2417425534254224622L;
    
    private AccountNameComparator accountNameComparator = new AccountNameComparator();

    private AccountList cachedAccountList;
    
    private int median = -1;
    
    private int lastRank = -1;

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
    public int compare(StandingsRecord o1, StandingsRecord o2) {
        int status = 0;
        long a1, b1;
        long a3, b3;
        long a2, b2;
        int a5, b5;
        String nameA, nameB;
        int nameComparison;
        int a0, b0;

        StandingsRecord teamA = (StandingsRecord) o1;
        StandingsRecord teamB = (StandingsRecord) o2;
        a0 = teamA.getRankNumber();
        a1 = teamA.getNumberSolved();
        a2 = teamA.getPenaltyPoints();
        a3 = teamA.getLastSolved();
        Account accountA = cachedAccountList.getAccount(teamA.getClientId());
        nameA = accountA.getDisplayName();
        a5 = teamA.getClientId().hashCode();
        b0 = teamB.getRankNumber();
        b1 = teamB.getNumberSolved();
        b2 = teamB.getPenaltyPoints();
        b3 = teamB.getLastSolved();
        Account accountB = cachedAccountList.getAccount(teamB.getClientId());
        nameB = accountB.getDisplayName();
        b5 = teamB.getClientId().hashCode();
//        nameComparison = nameA.toLowerCase().compareTo(nameB.toLowerCase());
        nameComparison = accountNameComparator.compare(nameA, nameB);

        // 
        // Primary Sort = number of solved problems (high to low)
        // Secondary Sort = score (low to high)
        // Tertiary Sort = earliest submittal of last submission (low to high)
        // Forth Sort = teamName (low to high)
        // Fifth Sort = clientId (low to high)
        if (a1 < getMedian() && b1 < getMedian()) {
            // only sort by name, this is honorable mention
            return (nameComparison);
        } else if (a1 < getMedian()) {
            return 1;
        } else if (b1 < getMedian()) {
            return -1;
        } else if( a0 > getLastRank() && b0 > getLastRank() ) {
            // compare only number of solved and name
            if (b1 == a1) {
                return(nameComparison);
            } else {
                if (b1 < a1) {
                    return (-1);
                } else {
                    return (1);
                }
            }
        } else if (a0 > getLastRank()) {
            return 1;
        } else if (b1 > getLastRank()) {
            return -1;
        }
        
        if ((b1 == a1) && (b2 == a2) && (b3 == a3) && (nameComparison == 0)
                && (b5 == a5)) {
            status = 0; // elements equal, this shouldn't happen...
        } else {
            if ((b1 > a1)
                    || ((b1 == a1) && (b2 < a2))
                    || ((b1 == a1) && (b2 == a2) && (b3 < a3))
                    || ((b1 == a1) && (b2 == a2) && (b3 == a3) && (nameComparison > 0))
                    || ((b1 == a1) && (b2 == a2) && (b3 == a3)
                            && (nameComparison == 0) && (b5 < a5))) {
                status = 1; // a greater then b
            } else {
                status = -1; // a less then b
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

    public int getMedian() {
        return median;
    }

    public void setMedian(int median) {
        this.median = median;
    }

    public int getLastRank() {
        return lastRank;
    }

    public void setLastRank(int lastRank) {
        this.lastRank = lastRank;
    }
}
