/**
 * 
 */
package edu.csus.ecs.pc2.api;

/**
 * This interface describes the PC<sup>2</sup> API view of the standing (ranking) information
 * for a particular {@link edu.csus.ecs.pc2.api.IClient} as determined by the current PC<sup>2</sup>
 * Scoring Algorithm.
 * <P>
 * An {@link IStanding} object contains information about the standing (ranking) of one particular
 * team in the contest.
 * 
 * <p>
 * This documentation describes the current <I>draft</i> of the PC<sup>2</sup> API, which is subject to change.
 * @author pc2@ecs.csus.edu
 * @version $Id$
 *
 */

//$HeadURL$
public interface IStanding {
    
    /**
     * Get the {@link IClient} associated with this IStanding object.
     * 
     * @return An object of type {@link IClient} representing the client to which this 
     * IStanding object applies.
     */
    IClient getClient();
    
    /**
     * Get the number of contest problems which the team represented by this {@link IStanding} object
     * has solved.
     * @return the number of problems solved by the team to which this {@link IStanding} applies.
     */
    int getNumProblemsSolved();
    
    /**
     * Get the total number of <I>penalty points</i> assigned by the currently active PC<sup>2</sup> scoring
     * algorithm to the team represented by this {@link IClient}.
     * 
     * @return the number of penalty points assigned to this team by the scoring algorithm.
     */
    int getPenaltyPoints();
    
    /**
     * Get the current rank position (where 1 represents first place, 2 represents second place, etc.)
     * assigned to the team represented by this {@link IClient} by the PC<sup>2</sup> scoring algorithm.
     * <P>
     * Note that assignment of rank numbers is a function of the plugin Scoring Algorithm.  Since there is no
     * predefined standard for how a scoring algorithm must handle assignment of rankings to teams which are tied, it is
     * possible that this method would return different ranks for teams with the same computed score, or would
     * return the same rank for teams with the same computed score.
     *   
     * @return an integer representing a team's rank in the contest standings as determined by the currently
     * active PC<sup>2</sup> scoring algorithm
     */
    int getRank();
    
    /**
     * Get the submission and scoring details for each problem.
     * 
     * The array is in the order: site, client, problem 
     * 
     * @return an array of information about the problem submission and scoring.
     */
    IProblemDetails[] getProblemDetails();

}
