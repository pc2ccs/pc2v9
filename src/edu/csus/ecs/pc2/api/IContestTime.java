package edu.csus.ecs.pc2.api;

/**
 * Contest Time information.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IContestTime {

    /**
     * Get the remaining seconds in the contest.
     * <P>
     * Because of network lag and other factors there will likely
     * be an error in accuracy.   For this reason typically the
     * granularity of minutes is used, though seconds is given.
     * 
     * 
     * @return number of seconds left in contest.
     */
    long getRemainingSecs();

    /**
     * Get the elapsed seconds in contest.
     * <P>
     * Because of network lag and other factors there will likely
     * be an error in accuracy.   For this reason typically the
     * granularity of minutes is used, though seconds is given.
     * 
     * @return elapsed seconds in contest.
     */
    long getElapsedSecs();

    /**
     * Get the contest length.
     * <P>
     * Because of network lag and other factors there will likely
     * be an error in accuracy.   For this reason typically the
     * granularity of minutes is used, though seconds is given.
     * 
     * @return the number of seconds in the contest.
     */
    long getContestLengthSecs();
    
    /**
     * Is Contest Clock running?.
     * 
     * @return true if clock running, else false.
     */
    boolean isContestClockRunning ();

}
