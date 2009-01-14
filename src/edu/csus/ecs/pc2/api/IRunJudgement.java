package edu.csus.ecs.pc2.api;

/**
 * A run judgement and fields associated with that judgement.
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IRunJudgement {
    
    /**
     * Get the judgement assigned to the run by the Judges.
     * 
     * @return the judgement for this run.
     */
    IJudgement getJudgement();

    /**
     * Is this the active/current judgement.
     * 
     * Only one judgement is active at a time, this is the
     * judgement that currently is displayed.
     * 
     * @see #isSendToTeam()
     * 
     * @return true if this judgement is the judgement to be displayed.
     */
    boolean isActive();
    
    /**
     * Return true if this judgement was a computer (automatic) judgement.
     * 
     * @return true if judged by computer, false if judged by human.
     */
    boolean isComputerJudgement();
    
    /**
     * Is this the judgement shown to team?
     * 
     * Returns true if judgement to be shown to the team, returns
     * false if the judgment to not be sent/shown to team.
     * 
     * @return true if judgement to be sent/viewed by team.
     */
    boolean isSendToTeam();
    
    /**
     * Is this a preliminary (non-final) judgement?.
     * <P>
     * Each contest problem a final judgement, some problems are defined to
     * have preliminary judgements, for example computer (automated) judgement
     * with a manual review.  In this case the computer judgement would
     * return <code>true</code> and the manual judgement would return <code>false</code>.
     * <P>
     * In the case where a problem is defined a judging type of Computer Judging only
     * (no Manual Review), this method would always return <code>false</code>.  In this
     * example there would not be a case where this method would return <code>true</code. 
     * 
     * @return true if a preliminary judgement, false if this is the final judgement.
     */
    boolean isPreliminaryJudgement();
    
    /**
     * Return a boolean indicating whether the run been given a Yes (Correct) judgement.
     * 
     * Note that the value of this method is only meaningful if the Run has been judged.
     * 
     * @return true if the run was judged by the Judges as having correctly solved a problem, false otherwise.
     */
    boolean isSolved();
    
}
