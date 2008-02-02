package edu.csus.ecs.pc2.api.exceptions;

/**
 * Exception when login failure occurs.
 * 
 * Use getMesssage() to find the why the login failed.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class LoginFailureException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -6675979618354284924L;

    public LoginFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoginFailureException(Throwable cause) {
        super(cause);
    }

    public LoginFailureException(String message) {
        super(message);
    }
    

}
