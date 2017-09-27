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
