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
     * 
     * @return number of seconds left in contest.
     */
    long getRemainingSecs();

    /**
     * Get the elapsed minutes in contest.
     * 
     * @return elapsed time in contest.
     */
    long getElapsedMins();

    /**
     * Get the cotnest length.
     * 
     * @return the number of seconds in the contest.
     */
    long getContestLengthSecs();

}
