package edu.csus.ecs.pc2.api;

/**
 * This interface describes the set of methods that any Clarification Listener must implement.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IClarificationEventListener {

    /**
     * New Clarification submitted.
     * 
     * @param clarification - the added {@link IClarification}
     */
    void clarificationAdded(IClarification clarification);

    /**
     * Invoked when an existing clarification has been removed from the contest (marked as deleted by the Contest Administrator).
     * 
     * @param clarification
     *            the deleted {@link IClarification}
     */
    void clarificationRemoved(IClarification clarification);

    /**
     * Clarification answered by judge.
     * 
     * @param clarification - the answered {@link IClarification}
     */
    void clarificationAnswered(IClarification clarification);

    /**
     * Invoked when clarification edited/changed.
     * 
     * @param clarification the changed {@link IClarification}
     */
    void clarificationUpdated(IClarification clarification);

}
