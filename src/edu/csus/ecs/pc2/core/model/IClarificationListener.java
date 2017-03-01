package edu.csus.ecs.pc2.core.model;

/**
 * Listeners for Clarifications.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IClarificationListener {
    
    /**
     * New Clarification.
     * @param event the ClarificationEvent which triggered this clarificationAdded action
     */
    void clarificationAdded(ClarificationEvent event);

    /**
     * Clarification state has changed.
     * 
     * The chage info is in the {@link edu.csus.ecs.pc2.core.model.ClarificationEvent.Action} of
     * {@link ClarificationEvent}.
     * 
     * @see ClarificationEvent
     * @see edu.csus.ecs.pc2.core.model.ClarificationEvent.Action
     * 
     * @param event the ClarificationEvent which triggered this clarificationChanged action
     */
    void clarificationChanged(ClarificationEvent event);

    /**
     * Clarification has been removed.
     * @param event the ClarificationEvent which triggered this clarificationRemoved action
     */
    void clarificationRemoved(ClarificationEvent event);
    
    /**
     * all clarifications removed, need to refresh.
     * 
     * @param event the ClarificationEvent which triggered this refreshClarfications action
     */
    void refreshClarfications (ClarificationEvent event);


}
