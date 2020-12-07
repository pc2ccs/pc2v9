// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.model;

/**
 * Event Feed Runnable.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 *
 */
public interface IEventFeedRunnable {

    /**
     * Send XML signature.
     * 
     * A runnable which is invoked when Event Feed XML is output/sent.
     * 
     * @param xmlString
     */
    void send(String xmlString);

}
