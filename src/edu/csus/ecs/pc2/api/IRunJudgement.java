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
     * judgement that currenently is displayed.
     * 
     * @see #isSendToTeam()
     * 
     * @return
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
    
}
