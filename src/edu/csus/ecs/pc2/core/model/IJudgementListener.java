package edu.csus.ecs.pc2.core.model;


/**
 * Listener for all Judgement Events.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IJudgementListener {

    /**
     * New Judgement.
     * @param event
     */
    void judgementAdded(JudgementEvent event);

    /**
     * Judgement information has changed.
     * @param event
     */
    void judgementChanged(JudgementEvent event);

    /**
     * Judgement has been removed.
     * @param event
     */
    void judgementRemoved(JudgementEvent event);
}
