package edu.csus.ecs.pc2.core.model;

/**
 * Listener for all Message Events.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IMessageListener {

    /**
     * New Message.
     * 
     * @param event
     */
    void messageAdded(MessageEvent event);

    /**
     * Message has been removed.
     * 
     * @param event
     */
    void messageRemoved(MessageEvent event);

}
