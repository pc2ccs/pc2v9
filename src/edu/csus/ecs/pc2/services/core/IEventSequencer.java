// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.services.core;

/**
 * Methods to create a event feed sequence ("id") number 
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public interface IEventSequencer {

    /**
     * Increment and return next event Id.
     * 
     * @return
     */
    String getNextEventId();

}
