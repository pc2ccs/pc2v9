package edu.csus.ecs.pc2.api;

/**
 * Run states.
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public enum RunStates {

    /**
     * Undefined/unknown
     */
    UNKNOWN, 
    
    /**
     * Newly submitted run
     */
    NEW,

    /**
     * Being judged.
     */
    BEING_JUDGED,

    /**
     * Being re-judged
     */
    BEING_RE_JUDGED,
    
    /**
     * Judgment complete. 
     */
    JUDGED, 

}
