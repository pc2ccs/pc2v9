package edu.csus.ecs.pc2.core.exception;

/**
 * A run is not allowed to be checked out
 * 
 * Conditions like: run already checked out.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class UnableToUncheckoutRunException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 3558168227045856508L;

    public UnableToUncheckoutRunException(String message){
        super(message);
    }
}
