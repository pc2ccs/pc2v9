package edu.csus.ecs.pc2.api;

/**
 * This interface describes the PC<sup>2</sup> API view of a contest <I>Run</i>.
 * A <I>Run</i> is a submission by a Team of a program for judging.
 * 
 * Information about run states is in {@link edu.csus.ecs.pc2.api.listener.IRunEventListener}
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
     * If the run has been assigned preliminary judgement or final judgement 
     * this method will return true.
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
     * Return a boolean indicating whether the run is being compiled.
     * 
     * <B>Note that the events {@link #isCompiling()}, {@link #isExecuting()} and {@link #isVaildating()} 
     * can appear in any order</B>
     * 
     * @return true if a judge is compiling this run.
     */
    boolean isCompiling();
    
    /**
     * Return a boolean indicating whether the run is being executed.
     * 
     * <B>Note that the events {@link #isCompiling()}, {@link #isExecuting()} and {@link #isVaildating()} 
     * can appear in any order</B>
     * @return true if a judge is executing this run.
     */
    boolean isExecuting();
    
    /**
     * Return a boolean indicating whether the run is being validated.
     *
     * <B>Note that the events {@link #isCompiling()}, {@link #isExecuting()} and {@link #isVaildating()} 
     * can appear in any order</B>
     * @return true if a judge is validating this run.
     */
    boolean isVaildating();

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
     * Return List of judgements assigned by judges.
     * 
     * @return list of IRunJudgements or zero length array of IRunJudgement.
     */
    IRunJudgement [] getRunJudgements();

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
     * Every submitted run is assigned a site-unique 
     * Run number by the PC<sup>2</sup> server which first receives
     * the submission (that is, by the server to which the submitting team client is connected). 
     * Run numbers are always positive and always increasing at any given site (that is, every new
     * run at a given site will acquire a run number higher than any previous run at that same site).
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
    
    /**
     * Get the names of each of the source code files submitted
     * as part of this {@link IRun} by the submitting team.  The number of elements in the returned array corresponds to 
     * the number of different source code files submitted by the Team; the first element of the array (element [0])
     * gives the name of the file which the Team submitted as the &quot;main&quot; program file.
     * 
     * @return An array of Strings where each array element contains the name of one source code file submitted by
     * the Team which created this {@link IRun}.  
     */
    String [] getSourceCodeFileNames();
    
    /**
     * Get the source code contained in the files submitted by the Team which created this {@link IRun}.
     * Returns an array of arrays of bytes containing the contents of the submitted source code files.  
     * The first element of the array contains the
     * bytes from the first source code file submitted, which is always the &quot;main program&quot; file; subsequent
     * elements of the array contain the bytes from each additional source code file submitted by the Team.  
     * 
     * @return The contents of each submitted source code file for this {@link IRun}.
     */
    byte [][] getSourceCodeFileContents();
    
    /**
     * Check whether this Run is the same as some other Run.
     * <P>
     * Determination of whether two Runs are equal is based on whether they refer to the
     * same run as submitted by a Team.  
     * Note in particular that subsequent changes to a Run
     * made by the Contest Administrator (for example,
     * changes to the time the run was received, the language or problem specified in the run,
     * or whether the run solved the problem or not) do <I>not</i> affect the result of the
     * <code>equals()</code> method; if this Run refers to the same Run as the one indicated by the 
     * specified parameter, this method will return true regardless of whether the internal contents of the two
     * Run objects is identical or not.
     * 
     * @param obj the Run which is to be compared with this Run for equality.
     * @return True if the specified object refers to the same Run as this Run
     *          (regardless of the actual content of the two Runs).
     */
    boolean equals(Object obj);

    /**
     * Get the hashcode associated with this client.
     * @return An integer hashcode for this object.
     */
    int hashCode();

}
