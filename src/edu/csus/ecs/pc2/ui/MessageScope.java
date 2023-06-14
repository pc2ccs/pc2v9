// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

/**
 * Scope for messages.
 * 
 * These are areas or scopes for the message, can be used
 * so messages are only sent to listeners in scope.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public enum MessageScope {
    /**
     * Not assigned a scope.
     */
    NONE,
    
    /**
     * Message used/displayed on Shadow UI. 
     */
    SHADOW_UI,
}
