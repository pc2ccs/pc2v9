package edu.csus.ecs.pc2.api;


/**
 * This interface describes the PC<sup>2</sup> API view of the a standing (ranking) information for a particular {@link edu.csus.ecs.pc2.api.IClient} as determined by the current PC<sup>2</sup>
 * Scoring Algorithm.
 * <P>
 * An {@link IStanding} object contains information about the standing (ranking) of one particular problem.
 * 
 * <p>
 * This documentation describes the current <I>draft</i> of the PC<sup>2</sup> API, which is subject to change.
 * @author pc2@ecs.csus.edu
 * @version $Id$
 * 
 */

// $HeadURL$
public interface IProblemDetails {

    /**
     * The client for this problem data.
     * 
     * @return the client info for this problem details.
     */
    IClient getClient();

    /**
     * A Contest Problem.
     * @return
     */
    IProblem getProblem();

    /**
     * The number of attempts for this problem.
     * 
     * @return number of attempts for this problem.
     */
    long getAttempts();

    /**
     * The Problem Number for this problem
     * 
     * @return the number of the problem in the contest (numbers start at 1)
     */
    int getProblemId();

    /**
     * The solution time for this problem
     * 
     * @return number of elapsed minutes when problem was solved.
     */
    long getSolutionTime();

    /**
     * 
     * In Version 8, this was ProblemScoreData.getScore()
     * 
     * @return number of penalty points
     */
    long getPenaltyPoints();

    /**
     * Has this Problem Been Solved?
     * @return true if solved, else false.
     */
    boolean isSolved();

}
