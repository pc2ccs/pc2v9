// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.log;

/**
 * 
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public enum LogType {
    
    /**
     * Create a log per client.
     */
    ONE_LOG_PER_CLIENT,
    
    /**
     * Create a single log all clients.
     */
    ONE_LOG_FOR_ALL_CLIENTS,

}
