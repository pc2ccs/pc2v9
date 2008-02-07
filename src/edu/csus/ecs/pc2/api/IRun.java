package edu.csus.ecs.pc2.api;

/**
 * This interface describes the PC<sup>2</sup> API view of a contest <I>Run</i>.
 * A <I>Run</i> is a submission by a Team of a program for judging.
 * 
 * <p>
 * This documentation describes the current <I>draft</i> of the PC<sup>2</sup> API, which is subject to change.
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IRun {

    /**
     * Return a boolean indicating whether or not the run been judged.
     * 
     * @return true if the run has been judged, false if not judged.
     */
    boolean isJudged();

    /**
     * Return a boolean indicating whether the run been given a Yes (Correct) judgement.
     * Note that the value of this method is only meaningful if the Run has been judged.
     * 
     * @return true if the run was judged by the Judges as having correctly solved a problem, false otherwise.
     */
    boolean isSolved();

    /**
     * Return a boolean indicating whether the run been marked as deleted by the Contest Administrator.
     * 
     * @return true if the run is marked deleted, else false.
     */
    boolean isDeleted();

    /**
     * Get the team (client) that submitted this run.
     * 
     * @return the {@link ITeam} which submitted this run.
     */
    ITeam getTeam();

    /**
     * Get the judgement name assigned to the run by the Judges.
     * 
     * Note that if the run has been judged using an automated judger (also know as a validator) 
     * this method may return text from the validator which may not match any judgement name defined by 
     * the Contest Administrator (that is, the return value might be different from any defined {@link IJudgement}).
     * 
     * @return null if the run has not been judged; otherwise, a String representing judgement
     * assigned to the run.
     */
    String getJudgementName();

    /**
     * Get the problem for which this run was submitted.
     * 
     * @return the {@link IProblem} associated with this run.
     */
    IProblem getProblem();

    /**
     * Get the language which was specified for this run when it was submitted by the submitting team.
     * 
     * @return the {@link ILanguage} associated with this run.
     */
    ILanguage getLanguage();

    /**
     * Get the run number.
     * 
     * Every submitted run is assigned a site-unique Run number by the PC<sup>2</sup> server which first receives
     * the submission (that is, by the server to which the submitting team client is connected). 
     * Every submitted run is also populated with the unique site number of the site where the run is first received.
     * The combination of the site number and the run number therefore provides a contest-wide unique identifier for
     * every run in the contest.
     * 
     * @return the number for this run.
     */
    int getNumber();

    /**
     * Get the site number associated with the run.
     * This will always be the unique number of the site where the run was received by a PC<sup>2</sup> server.
     * 
     * @return the site number for this run.
     */
    int getSiteNumber();

    /**
     * Get the number of minutes which had elapsed on the contest clock at the site where the run submission was 
     * received when the run was submitted.
     * 
     * @return the number of minutes elapsed when this run was submitted to a PC<sup>2</sup> server.
     */
    long getSubmissionTime();

}
