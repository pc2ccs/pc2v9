package edu.csus.ecs.pc2.core.model.playback;

/**
 * List of event states.
 * 
 * States like: {@link #PENDING}, {@link #IN_PROGRESS}, {@link #COMPLETED}.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public enum EventStatus {

    /**
     * invalid or undefined
     */
    INVALID,
    

    /**
     * Pending or read to execute
     */
    PENDING,

    /**
     * Event currently be replayed.
     */
    IN_PROGRESS,
    
    /**
     * Completed
     */
    COMPLETED

}
