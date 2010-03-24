package edu.csus.ecs.pc2.core.exception;

/**
 * Exceptions for Profiles activities.
 * 
 * Adding, removing, creating directories, establishing encrypting of contest password, etc.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProfileException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -4955153900325717474L;

    public ProfileException() {
        super();
        // TODO Auto-generated constructor stub
    }

    public ProfileException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public ProfileException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public ProfileException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
