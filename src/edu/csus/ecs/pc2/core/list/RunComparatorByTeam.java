package edu.csus.ecs.pc2.core.list;

import edu.csus.ecs.pc2.core.model.Run;

/**
 * Run Comparator, Order the runs by team, problem, elapsed, runNumber, isSolved. This comparator groups like records together, it
 * does not sort by team number or problem number, so it should not be used to display runs in order. <br>
 * Use {@link edu.csus.ecs.pc2.core.list.RunComparator RunCompartor} to sort runs in site # and run # order.
 * 
 * @see edu.csus.ecs.pc2.core.list.RunComparator
 * 
 * @author pc2@ecs.csus.edu
 */
public class RunComparatorByTeam implements java.io.Serializable, java.util.Comparator<Run> {
    public static final String SVN_ID = "$Id$";

    /**
     * 
     */
    private static final long serialVersionUID = 6552641417655816361L;

    /**
     * ScoreDataCompator constructor comment.
     */
    public RunComparatorByTeam() {
        super();
    }

    /**
     * Compares two arguments for order.
     * 
     * Returns a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the
     * second.
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
     *         second. if the arguments' types prevent them from being compared by this Comparator.
     * 
     * @param o1
     *            1st Run
     * @param o2
     *            2nd Run
     */
    public int compare(Run o1, Run o2) {
        int status = 0;
        int a1, b1, a2, b2, a4, b4;
        long a3, b3;
        boolean a5, b5;

        Run runA = (Run) o1;
        Run runB = (Run) o2;
        a1 = runA.getSubmitter().hashCode();
        a2 = runA.getProblemId().hashCode();
        a3 = runA.getElapsedMins();
        a4 = runA.getNumber();
        a5 = runA.isSolved();
        b1 = runB.getSubmitter().hashCode();
        b2 = runB.getProblemId().hashCode();
        b3 = runB.getElapsedMins();
        b5 = runB.isSolved();
        b4 = runB.getNumber();

        // Primary Sort = Team (low to high)
        // Secondary Sort = Problem (low to high)
        // Tertiary Sort = elapsedMin (low to high)
        // Five Sort = Run (low to high)
        // Forth Sort = isSolved (yes to no) shouldn't come to this
        if ((b1 == a1) && (b2 == a2) && (b3 == a3) && (b4 == a4) && (a5 == b5)) {
            // TODO log ... that this shouldn't happen
            status = 0; // elements equal, this shouldn't happen...
        } else {
            if ((b1 > a1) || (b1 == a1 && b2 > a2) || (b1 == a1 && b2 == a2 && b3 > a3)
                    || (b1 == a1 && b2 == a2 && b3 == a3 && b4 > a4)) {
                status = -1; // a less then b
            } else {
                status = 1; // a greater then b
            }
        }
        return status;
    }

}
