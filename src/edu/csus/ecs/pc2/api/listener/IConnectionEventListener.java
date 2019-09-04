// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.api.listener;

/**
 * This interface describes the set of methods when a client is disconnected.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IConnectionEventListener {

    /**
     * Connection Dropped.
     */
    void connectionDropped();

}
