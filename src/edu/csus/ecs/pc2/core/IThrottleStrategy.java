// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core;

import edu.csus.ecs.pc2.core.model.Run;

/**
 * This interface defines the methods which a "throttler" (governor) of submission rates must implement.
 * 
 * @author John Clevenger
 *
 */
public interface IThrottleStrategy {
    /**
     * This method returns true if the implementing Strategy indicates the specified Submission ("Run" in PC2 terms)
     * should be accepted (that is, forwarded to the PC2 server); false if the Submission exceeds the 
     * implementation-defined strategy for submission limits.
     */
    boolean accept(Run run);
}
