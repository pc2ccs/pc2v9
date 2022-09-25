// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

/**
 * Comparisons states.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public enum ComparisonState {
    
    /**
     * 
     */
    UNDEFINED, 
    
    /**
     * No source data exists
     */
    MISSING_SOURCE,
    
    /**
     * No target data exists
     */
    MISSING_TARGET,
    
    /**
     * Both source and target missing (null)
     */
    BOTH_MISSING,
    
    /**
     * same/identical
     */
    SAME,
    
    /**
     * not identical
     */
    NOT_SAME,

}
