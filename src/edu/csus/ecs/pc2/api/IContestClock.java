// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.api;

import java.util.Calendar;

/**
 * This interface describes the PC<sup>2</sup> API view of Contest Time information.
 * It provides methods for accessing various time-related aspects of the contest, including
 * how much time has elapsed, how much time remains, how long the contest is scheduled to last,
 * and (once the contest has been started) the date/time at which it was started.
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
    
    /**
     * Returns a {@link Calendar} containing the date/time that the contest actually started, 
     * or null if the contest has not ever been started.
     * Note that the returned value is independent of (has nothing to do with) the "Scheduled Start Time";
     * the latter is only relevant before the contest actually starts.  
     * Note also that once the contest is started, the value returned by this method never changes;
     * specifically, it is not affected by any subsequent "pause" (a.k.a. "stop contest") operations,
     * nor by any "start contest" operations following a pause; the returned value always indicates
     * when the contest was FIRST started.
     * @return a {@link Calendar} object indicating the date/time that the contest actually started.
     */
    public Calendar getContestStartTime();

    /**
     * Check whether this ContestClock object is the same as some other ContestClock.
     * <P>
     * Determination of whether two ContestClocks are equal is based on whether they refer to the
     * same ContestClock as obtained from {@link IContest#getContestClock()}. 
     * 
     * @param obj the ContestClock which is to be compared with this ContestClock for equality.
     * @return True if the specified object refers to the same ContestClock as this ContestClock
     *          (regardless of the actual content of the two ContestClocks).
     */
    boolean equals(Object obj);

    /**
     * Get the hashcode associated with this ContestClock.
     * @return An integer hashcode for this object.
     */
    int hashCode();
}
