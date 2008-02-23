package edu.csus.ecs.pc2.core.security;

/**
 * Exception for crypto routines.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class CryptoException extends Exception {

    public static final String SVN_ID = "$Id$";

    /**
     * Crypto related Exceptions
     * 
     */
    private static final long serialVersionUID = 835017454294483834L;

    private String errMsg = "unknown exception";

    /**
     * Default constructor
     * 
     */
    public CryptoException() {
        super();
    }

    /**
     * Constructor receives some kind of message that is saved in an instance variable.
     * 
     */
    public CryptoException(String err) {
        super(err);
        errMsg = err;
    }

    /**
     * public method, callable by exception catcher. It returns the error message.
     * 
     * @return returns the error message
     */
    public String getError() {
        return errMsg;
    }
}
