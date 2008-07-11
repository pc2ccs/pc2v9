package edu.csus.ecs.pc2.api;

/**
 * This interface describes the PC<sup>2</sup> API view of a contest <I>Clarification</i>. A <I>Clarification</i> is a submitted question by a Team to the Judges.
 * 
 * <p>
 * This documentation describes the current <I>draft</i> of the PC<sup>2</sup> API, which is subject to change.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IClarification {

    /**
     * Return a boolean indicating whether or not the clarification been answered.
     * 
     * @return true if the clarification has been answered, false if not answered.
     */
    boolean isAnswered();
    
    /**
     * Return a string of the team's question for the judges.
     * @return question from team.
     */
    String getQuestion();
    
    /**
     * Return a string of the judge's answer for the team's question.
     * 
     * @return answer from judge or null if not answered.
     */
    String getAnswer();

    /**
     * Return a boolean indicating whether the clarification been marked as deleted by the Contest Administrator.
     * 
     * @return true if the clarification is marked deleted, else false.
     */
    boolean isDeleted();

    /**
     * Get the team (client) that submitted this clarification.
     * 
     * @return the {@link ITeam} which submitted this clarification.
     */
    ITeam getTeam();

    /**
     * Get the problem for which this clarification was submitted.
     * 
     * @return the {@link IProblem} associated with this clarification.
     */
    IProblem getProblem();

    /**
     * Get the clarification number.
     * 
     * Every submitted clarification is assigned a site-unique Clarification number by the PC<sup>2</sup> server which first receives the submission (that is, by the server to which the submitting
     * team client is connected). Clarification numbers are always positive and always increasing at any given site (that is, every new clarification at a given site will acquire a clarification
     * number higher than any previous clarification at that same site). Every submitted clarification is also populated with the unique site number of the site where the clarification is first
     * received. The combination of the site number and the clarification number therefore provides a contest-wide unique identifier for every clarification in the contest.
     * 
     * @return the number for this clarification.
     */
    int getNumber();

    /**
     * Get the site number associated with the clarification. This will always be the unique number of the site where the clarification was received by a PC<sup>2</sup> server.
     * 
     * @return the site number for this clarification.
     */
    int getSiteNumber();

    /**
     * Get the number of minutes which had elapsed on the contest clock at the site where the clarification submission was received when the clarification was submitted.
     * 
     * @return the number of minutes elapsed when this clarification was submitted to a PC<sup>2</sup> server.
     */
    long getSubmissionTime();

    /**
     * Check whether this Clarification is the same as some other Clarification.
     * <P>
     * Determination of whether two Runs are equal is based on whether they refer to the same clarification as submitted by a Team. Note in particular that subsequent changes to a Clarification made
     * by the Contest Administrator (for example, changes to the time the clarification was received, the problem specified in the clarification, or whether the clarification solved the problem or
     * not) do <I>not</i> affect the result of the <code>equals()</code> method; if this Clarification refers to the same Clarification as the one indicated by the specified parameter, this method
     * will return true regardless of whether the internal contents of the two Clarification objects is identical or not.
     * 
     * @param obj
     *            the Clarification which is to be compared with this Clarification for equality.
     * @return True if the specified object refers to the same Clarification as this Clarification (regardless of the actual content of the two Runs).
     */
    boolean equals(Object obj);

    /**
     * Get the hashcode associated with this client.
     * 
     * @return An integer hashcode for this object.
     */
    int hashCode();

}
