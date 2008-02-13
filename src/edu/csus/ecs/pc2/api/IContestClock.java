package edu.csus.ecs.pc2.api;

/**
 * This interface describes the PC<sup>2</sup> API view of Contest Time information.
 * It provides methods for accessing various time-related aspects of the contest, including
 * how much time has elapsed, how much time remains, and how long the contest is scheduled to last.
 * <P>
 * Note that under the current implementation, an {@link IContestClock} object is static once it
 * is obtained; to get a current copy of the contest time information a new {@link IContestClock} object
 * should be obtained each time.
 * 
 * <p>
 * This documentation describes the current <I>draft</i> of the PC<sup>2</sup> API, which is subject to change.
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IContestClock {

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
     * The returned value represents the length of time which the Contest Administrator has specified that
     * the contest should run.
     * 
     * @return the number of seconds the contest is intended to run.
     */
    long getContestLengthSecs();
    
    /**
     * Returns a boolean value indicating whether the contest clock is currently running.
     * If the method returns false, either the contest has not been started, or it has
     * been started and then paused by the Contest Administrator.  Method {@link #getElapsedSecs()} 
     * can be used to determine which case exists (not yet started vs. paused.)
     * 
     * @return true if the contest clock is currently running; false otherwise.
     */
    boolean isContestClockRunning ();

    // TODO document
    boolean equals(Object obj);

    int hashCode();
}
