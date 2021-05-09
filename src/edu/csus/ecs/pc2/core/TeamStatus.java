package edu.csus.ecs.pc2.core;

/**
 * Team contact status.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public enum TeamStatus {
    /**
     * Team has not logged in.
     */
    NO_TEAM_CONTACT,
    /**
     * Team has logged in only.
     */
    TEAM_HAS_LOGGED_IN,
    
    /**
     * Team has submitted runs only.
     */
    TEAM_HAS_SUBMITTED_RUNS_ONLY,
    
    /**
     * Team has submitted clars only.
     */
    TEAM_HAS_SUBMITTED_CLARS_ONLY,
    /**
     * Team has submitted runs and clars
     */
    TEAM_HAS_SUBMITTED_RUNS_AND_CLARS,
}
