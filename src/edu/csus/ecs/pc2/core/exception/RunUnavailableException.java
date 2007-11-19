package edu.csus.ecs.pc2.core.exception;

/**
 * A run is not available to be retrieved.
 * 
 * Conditions like: run already checked out.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class RunUnavailableException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -2769674404775043282L;

    public RunUnavailableException(String message){
        super(message);
    }
}
