package edu.csus.ecs.pc2.api;

//TODO java doc

/**
 * This interface describes the set of methods that any Clarification Listener must implement.
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IClarificationEventListener {

    // TODO java doc
    void clarificationAdded(IClarification clarification);

    /**
     * Invoked when an existing clarification has been removed from the contest (marked as deleted by the Contest Administrator).
     * 
     * @param clarification
     *            the deleted {@link IClarification}
     */

    // TODO java doc
    void clarificationRemoved(IClarification clarification);

    // TODO java doc

    void clarificationAnswered(IClarification clarification);

    // TODO java doc

    void clarificationUpdated(IClarification clarification);

}
