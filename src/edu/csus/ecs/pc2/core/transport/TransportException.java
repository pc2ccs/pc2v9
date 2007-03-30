package edu.csus.ecs.pc2.core.transport;

/**
 * Transport Manager Exception Class. Used for all exceptions going up to the application
 * 
 * @author pc2@ecs.csus.edu
 * 
 */
// $HeadURL$
public class TransportException extends Exception {
    public static final String SVN_ID = "$Id$";

    /**
     * Transport related Exceptions
     * 
     */
    private static final long serialVersionUID = -6333266176304198528L;

    private String errMsg = "unknown exception";

    public static final String CONNECTION_RESET = "connection reset";

    public TransportException() {
        super();
    }

    /**
     * Constructor receives some kind of message that is saved in an instance variable.
     * 
     */
    public TransportException(String err) {
        super(err);
        errMsg = err;
    }

    /**
     * public method, callable by exception catcher. It returns the error message.
     * 
     * @return the error
     */
    public String getError() {
        return errMsg;
    }

}
