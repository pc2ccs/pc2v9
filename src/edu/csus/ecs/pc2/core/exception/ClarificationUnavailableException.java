package edu.csus.ecs.pc2.core.exception;

/**
 * A clarification is not available to be retrieved.
 * 
 * Conditions like: clarification already checked out.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ClarificationUnavailableException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -2769674404775043282L;

    public ClarificationUnavailableException(String message){
        super(message);
    }
}
