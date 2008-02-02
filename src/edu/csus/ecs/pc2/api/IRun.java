package edu.csus.ecs.pc2.api;

/**
 * Run information.
 * <P>
 * Contains information about a run.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IRun {

    /**
     * Has run been judged?
     * 
     * @return true if judged, false if not judged.
     */
    boolean isJudged();

    /**
     * Has the run been given a Yes/Correct judgement?.
     * 
     * @return true if run solves problem, false if does not solve problem.
     */
    boolean isSolved();

    /**
     * Has run been marked as deleted?.
     * 
     * @return true if run deleted, else false.
     */
    boolean isDeleted();

    /**
     * Which team (client) submitted this run.
     * 
     * @return the ClientId (site, team number) for the team who submitted this run.
     */
    ITeam getSubmitterTeam();

    /**
     * Get the judgement title/text for the run.
     * 
     * If the run has been judged using an automated judger (also know as a validator) this method may return text from the validator which may not match any defined judgement title/text.
     * 
     * @return null if not judged, else the title for the judgement.
     */
    String getJudgementTitle();

    /**
     * Get problem for this run.
     * 
     * @return problem information
     */
    IProblem getProblem();

    /**
     * Get language for this run.
     * 
     * @return language information.
     */
    ILanguage getLanguage();

    /**
     * Get run number.
     * 
     * Run number is assigned on the server. Each run number is unique to its site. A run number with site number is unique in the contest.
     * 
     * @return the number for this run.
     */
    int getNumber();

    /**
     * Get site number.
     * 
     * @return the site number for the run.
     */
    int getSiteNumber();

    /**
     * Get elapsed minutes when this run was submitted.
     * 
     * @return the number of minutes elapsed when this run was submitted.
     */
    long getSubmissionTime();

}
